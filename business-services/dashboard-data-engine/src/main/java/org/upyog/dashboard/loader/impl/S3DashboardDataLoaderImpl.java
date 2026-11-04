package org.upyog.dashboard.loader.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.api.DashboardIngestionClient;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.service.IngestionRecordPersistenceService;
import org.upyog.dashboard.service.SXSSFExcelGeneratorService;
import org.upyog.dashboard.util.CommonUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Strategy implementation of {@link DashboardDataLoader} that aggregates normalized
 * dashboard payload records into a dataset file (Excel or Delimited format), uploads the dataset
 * to AWS S3 storage, and persists the ingestion audit records via {@link IngestionRecordPersistenceService}.
 * <p>
 * Key Responsibilities:
 * <ul>
 *   <li>Streams batch records into temporary files (Excel {@code .xlsx} or Delimited {@code .psv}/{@code .csv}) via {@link SXSSFExcelGeneratorService}.</li>
 *   <li>Determines tenant and state context for S3 bucket key routing.</li>
 *   <li>Uploads generated dataset files to AWS S3 and invokes the downstream bulk initialization API via {@link DashboardIngestionClient}.</li>
 *   <li>Publishes audit persistence logs to Kafka topics or DB stores via {@link IngestionRecordPersistenceService}.</li>
 *   <li>Manages disk cleanup of temporary files based on property configuration settings.</li>
 * </ul>
 * </p>
 *
 * @see DashboardDataLoader
 * @see DashboardIngestionClient
 * @see SXSSFExcelGeneratorService
 * @see IngestionRecordPersistenceService
 */
@Slf4j
@Component("s3DataLoader")
@RequiredArgsConstructor
public class S3DashboardDataLoaderImpl implements DashboardDataLoader {

    /** Client interface handling AWS S3 dataset uploads and bulk ingestion initialization calls. */
    private final DashboardIngestionClient ingestionClient;

    /** Streaming service for creating memory-safe Excel workbooks or flat delimited dataset files. */
    private final SXSSFExcelGeneratorService excelGeneratorService;

    /** Centralized environment and application properties. */
    private final DashboardProperties properties;

    /** Persistence service for saving ingestion audit details and state updates to Kafka/DB. */
    private final IngestionRecordPersistenceService persistenceService;

    /** Jackson ObjectMapper for JSON serialization of payload data. */
    private final ObjectMapper objectMapper;

    /**
     * Aggregates normalized KPI metrics into a dataset file, uploads the file to AWS S3 storage,
     * triggers downstream bulk ingestion processing, and records the audit outcome.
     *
     * @param payload container holding the module name, target tenant, and normalized {@link DashboardData} records
     * @return {@link IngestionResult} summarizing status (SUCCESS / FAILURE), response payload, or failure diagnostic messages
     */
    @Override
    public IngestionResult load(DashboardPayload payload) {
        String moduleName = "DASHBOARD";
        if (payload.getData() != null && !payload.getData().isEmpty() && payload.getData().get(0).getModule() != null) {
            moduleName = payload.getData().get(0).getModule();
        }

        int payloadDataSize = (payload.getData() != null) ? payload.getData().size() : 0;
        DashboardData firstElement = (payloadDataSize > 0) ? payload.getData().get(0) : null;
        String ingestionDateString = firstElement != null ? firstElement.getDate() : null;

        log.info("S3DashboardDataLoaderImpl | Executing S3 upload routing for module: {}", moduleName);
        File generatedFile = null;
        String requestJson = null;
        try {
            List<Object> records = new ArrayList<>();
            if (payload.getData() != null) {
                for (DashboardData data : payload.getData()) {
                    records.add(data);
                }
            }

            try (SXSSFExcelGeneratorService.StreamingExcelSession session = excelGeneratorService.createStreamingSession(moduleName, DashboardConstants.DAILY)) {
                session.appendBatchRecords(records);
                generatedFile = session.finishWorkbook();
            }

            String tenantId = properties.getTenantId();
            if (payload.getData() != null && !payload.getData().isEmpty()) {
                String payloadUlb = payload.getData().get(0).getUlb();
                if (payloadUlb != null && payloadUlb.contains(".")) {
                    tenantId = payloadUlb.split("\\.")[0];
                } else if (payloadUlb != null) {
                    tenantId = payloadUlb;
                }
            }

            try {
                requestJson = objectMapper.writeValueAsString(payload);
            } catch (Exception e) {
                requestJson = "{\"moduleName\":\"" + moduleName + "\",\"tenantId\":\"" + tenantId + "\"}";
            }

            String delimiter = (properties != null && (properties.isDelimitedFileEnabled() || properties.isPipeFileEnabled()))
                    ? properties.getPipeFileDelimiter()
                    : null;

            IngestionResult clientResult = ingestionClient.uploadToS3(generatedFile, moduleName, tenantId, delimiter);

            IngestionResult finalResult = IngestionResult.builder()
                    .ingestionStatus(clientResult.getIngestionStatus())
                    .responseData(clientResult.getResponseData())
                    .failureReason(clientResult.getFailureReason())
                    .date(ingestionDateString)
                    .moduleName(moduleName)
                    .ingestedAt(CommonUtils.getCurrentEpochMillis())
                    .build();

            pushIngestionRecord(payload, requestJson,
                    clientResult.getResponseData() != null ? clientResult.getResponseData() : clientResult.getFailureReason(),
                    clientResult.getIngestionStatus());

            return finalResult;
        } catch (Exception exception) {
            log.error("S3DashboardDataLoaderImpl | Failed to generate and upload dataset file for module {}", moduleName, exception);
            String failureReason = "Exception during S3 routing: " + exception.getMessage();
            IngestionResult failureResult = IngestionResult.builder()
                    .ingestionStatus(DashboardConstants.STATUS_FAILURE)
                    .failureReason(failureReason)
                    .date(ingestionDateString)
                    .moduleName(moduleName)
                    .ingestedAt(CommonUtils.getCurrentEpochMillis())
                    .build();

            pushIngestionRecord(payload, requestJson, failureReason, DashboardConstants.STATUS_FAILURE);
            return failureResult;
        } finally {
            if (generatedFile != null && generatedFile.exists()) {
                boolean keepFile = (properties != null && (properties.isDelimitedFileEnabled() || properties.isPipeFileEnabled()))
                        ? properties.isDelimitedKeepFile()
                        : false;
                if (!keepFile) {
                    generatedFile.delete();
                }
            }
        }
    }

    /**
     * Helper method to asynchronously push ingestion audit logs and raw request/response payloads
     * to the underlying persistence service.
     *
     * @param payload         original input payload containing data records
     * @param requestJson     serialized JSON string of the ingestion request
     * @param responseOrError response JSON string from downstream API or error diagnostic message
     * @param status          final ingestion status indicator (e.g. SUCCESS, FAILURE, SUCCESS_ZERO_METRICS)
     */
    private void pushIngestionRecord(DashboardPayload payload, String requestJson, String responseOrError, String status) {
        try {
            persistenceService.pushIngestionRecord(payload, requestJson, responseOrError, status);
        } catch (Exception exception) {
            log.error("S3DashboardDataLoaderImpl | Failed to push ingestion record to persistence service", exception);
        }
    }
}
