package org.egov.nationaldashboardingest.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.nationaldashboardingest.repository.BulkIngestJobRepository;
import org.egov.nationaldashboardingest.service.BatchIngestionProcessor;
import org.egov.nationaldashboardingest.service.BulkFileProcessorService;
import org.egov.nationaldashboardingest.utils.BulkIngestConstants;
import org.egov.nationaldashboardingest.utils.DelimitedStreamingBatchReader;
import org.egov.nationaldashboardingest.utils.ExcelStreamingBatchReader;
import org.egov.nationaldashboardingest.utils.S3FileDownloader;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitDetail;
import org.egov.nationaldashboardingest.web.models.BulkIngestJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Production implementation of {@link BulkFileProcessorService}.
 * <p>
 * Orchestrates the end-to-end processing pipeline for bulk ingestion files:
 * <ol>
 * <li>Checks {@link BulkIngestJobRepository} to enforce file-level idempotency,
 * skipping files that are already completed or processing.</li>
 * <li>Downloads remote AWS S3 object identified by
 * {@link BulkIngestInitDetail#getFileName()} to local disk via
 * {@link S3FileDownloader}.</li>
 * <li>Parses Excel spreadsheet sheets or Delimited text files (.csv, .psv,
 * .txt) in streaming batches of 100 rows.</li>
 * <li>Passes each batch to
 * {@link BatchIngestionProcessor#processBatchWithFallback} for per-row auditing
 * and ingestion.</li>
 * <li>Updates the {@code ug_bulk_ingest_job} table with completion status and
 * row counts.</li>
 * <li>Guarantees deletion of the temporary disk file in a {@code finally}
 * block.</li>
 * </ol>
 * </p>
 */
@Slf4j
@Service
public class BulkFileProcessorServiceImpl implements BulkFileProcessorService {

    @Autowired
    private S3FileDownloader s3FileDownloader;

    @Autowired
    private ExcelStreamingBatchReader excelStreamingBatchReader;

    @Autowired
    private DelimitedStreamingBatchReader delimitedStreamingBatchReader;

    @Autowired
    private BatchIngestionProcessor batchIngestionProcessor;

    @Autowired
    private BulkIngestJobRepository bulkIngestJobRepository;

    /**
     * Downloads an S3 file object, streams its rows, ingests metrics per row,
     * updates job status, and cleans up local disk resources.
     *
     * @param bulkIngestInitDetail request DTO specifying the S3 file path and
     * target state code
     */
    @Override
    public void processBulkFile(BulkIngestInitDetail bulkIngestInitDetail) {
        if (bulkIngestInitDetail == null || bulkIngestInitDetail.getFileName() == null) {
            log.error("Invalid bulk ingest init detail provided for processing");
            return;
        }

        String fileName = bulkIngestInitDetail.getFileName();
        String stateCode = bulkIngestInitDetail.getStateCode();

        // STEP 0: Idempotency check via PostgreSQL repository
        Optional<BulkIngestJob> existingJobOpt = bulkIngestJobRepository.findByFileName(fileName);
        if (existingJobOpt.isPresent()) {
            BulkIngestJob existingJob = existingJobOpt.get();
            if (BulkIngestConstants.JOB_STATUS_COMPLETED.equalsIgnoreCase(existingJob.getStatus())) {
                log.warn("Bulk file [{}] has already been successfully processed (processedRows={}, failedRows={}). Skipping execution for idempotency.",
                        fileName, existingJob.getTotalRowsProcessed(), existingJob.getFailedRowsCount());
                return;
            }
            if (BulkIngestConstants.JOB_STATUS_IN_PROGRESS.equalsIgnoreCase(existingJob.getStatus())) {
                log.warn("Bulk file [{}] is currently being processed by another worker task. Skipping duplicate execution.", fileName);
                return;
            }
        }

        // STEP 1: Register or update job record as IN_PROGRESS
        String jobId = existingJobOpt.map(BulkIngestJob::getId).orElse(UUID.randomUUID().toString());
        long now = System.currentTimeMillis();
        BulkIngestJob job = BulkIngestJob.builder()
                .id(jobId)
                .fileName(fileName)
                .stateCode(stateCode != null ? stateCode : "UNKNOWN")
                .status(BulkIngestConstants.JOB_STATUS_IN_PROGRESS)
                .createdTime(existingJobOpt.map(BulkIngestJob::getCreatedTime).orElse(now))
                .lastModifiedTime(now)
                .build();
        bulkIngestJobRepository.save(job);

        File tempFile = null;
        List<Map<String, Object>> failedDatesList = new ArrayList<>();
        int totalRowsProcessed = 0;

        try {
            // STEP 2: Download File from S3 to Disk
            tempFile = s3FileDownloader.downloadFileFromS3(fileName);

            if (tempFile != null) {
                // STEP 3: Stream Read Rows & Ingest each row using row-level RequestInfo
                String lowerName = fileName.toLowerCase();
                boolean isDelimitedText = lowerName.endsWith(".csv") || lowerName.endsWith(".psv")
                        || lowerName.endsWith(".txt") || lowerName.endsWith(".tsv")
                        || "CSV".equalsIgnoreCase(bulkIngestInitDetail.getFileType())
                        || "PSV".equalsIgnoreCase(bulkIngestInitDetail.getFileType())
                        || "TEXT".equalsIgnoreCase(bulkIngestInitDetail.getFileType());

                if (isDelimitedText) {
                    log.info("Processing bulk dataset [{}] using DelimitedStreamingBatchReader", fileName);
                    totalRowsProcessed = delimitedStreamingBatchReader.processDelimitedFileInBatches(tempFile,
                            bulkIngestInitDetail.getDelimiter(),
                            batchDataList -> batchIngestionProcessor.processBatchWithFallback(batchDataList, null, failedDatesList));
                } else {
                    log.info("Processing bulk dataset [{}] using ExcelStreamingBatchReader", fileName);
                    totalRowsProcessed = excelStreamingBatchReader.processExcelFileInBatches(tempFile,
                            batchDataList -> batchIngestionProcessor.processBatchWithFallback(batchDataList, null, failedDatesList));
                }

                log.info("Finished processing bulk file: {}. Total rows processed: {}, Failures: {}",
                        fileName, totalRowsProcessed, failedDatesList.size());

                // STEP 4: Mark Job Status as COMPLETED
                bulkIngestJobRepository.updateJobStatus(jobId, BulkIngestConstants.JOB_STATUS_COMPLETED,
                        totalRowsProcessed, failedDatesList.size(), System.currentTimeMillis());
            } else {
                log.error("Failed to download S3 file: {}. File object is null.", fileName);
                bulkIngestJobRepository.updateJobStatus(jobId, BulkIngestConstants.JOB_STATUS_FAILED, 0, 0, System.currentTimeMillis());
            }

        } catch (Exception e) {
            log.error("Error processing bulk ingest init file: {}", fileName, e);
            bulkIngestJobRepository.updateJobStatus(jobId, BulkIngestConstants.JOB_STATUS_FAILED, 0, 0, System.currentTimeMillis());
        } finally {
            // STEP 5: Cleanup Temporary File
            if (tempFile != null && tempFile.exists()) {
                try {
                    boolean deleted = Files.deleteIfExists(tempFile.toPath());
                    log.info("Temporary file deleted from /tmp: {}, status: {}", tempFile.getAbsolutePath(), deleted);
                } catch (Exception e) {
                    log.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath(), e);
                }
            }
        }
    }
}
