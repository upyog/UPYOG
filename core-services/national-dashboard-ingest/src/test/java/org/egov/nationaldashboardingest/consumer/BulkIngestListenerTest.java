package org.egov.nationaldashboardingest.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.service.ExternalApiAuditLogger;
import org.egov.nationaldashboardingest.service.IngestService;
import org.egov.nationaldashboardingest.service.impl.BatchIngestionProcessorImpl;
import org.egov.nationaldashboardingest.service.impl.BulkFileProcessorServiceImpl;
import org.egov.nationaldashboardingest.utils.ExcelStreamingBatchReader;
import org.egov.nationaldashboardingest.utils.ResponseInfoFactory;
import org.egov.nationaldashboardingest.utils.S3FileDownloader;
import org.egov.nationaldashboardingest.web.models.BulkIngestInitDetail;
import org.egov.nationaldashboardingest.web.models.Data;
import org.egov.nationaldashboardingest.web.models.IngestRowData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Integration and unit test suite for {@link BulkIngestListener} and the asynchronous processing pipeline.
 * <p>
 * Tests message consumption from Kafka, S3 file downloading, streaming Excel row batching,
 * and individual per-row inbound API audit logging via {@link ExternalApiAuditLogger}.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class BulkIngestListenerTest {

    @Mock
    private S3FileDownloader s3FileDownloader;

    @Mock
    private ExcelStreamingBatchReader excelStreamingBatchReader;

    @Mock
    private IngestService ingestService;

    @Mock
    private ExternalApiAuditLogger integrationAuditLogger;

    @Mock
    private org.egov.nationaldashboardingest.repository.BulkIngestJobRepository bulkIngestJobRepository;

    @Spy
    private ResponseInfoFactory responseInfoFactory = new ResponseInfoFactory();

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private Producer producer;

    @Spy
    @InjectMocks
    private BatchIngestionProcessorImpl batchIngestionProcessor;

    @InjectMocks
    private BulkFileProcessorServiceImpl bulkFileProcessorService;

    @InjectMocks
    private BulkIngestListener bulkIngestListener;

    private BulkIngestInitDetail initDetail;
    private HashMap<String, Object> record;
    private File mockTempFile;

    /**
     * Initializes test fixtures, mocks, and injects dependency chains using {@link ReflectionTestUtils}.
     *
     * @throws IOException if temporary file generation fails
     */
    @BeforeEach
    void setUp() throws IOException {
        initDetail = new BulkIngestInitDetail("niuatt-internaltech/pg/PGR/test.xlsx", "pg");
        record = new HashMap<>();
        record.put("fileName", initDetail.getFileName());
        record.put("stateCode", initDetail.getStateCode());

        mockTempFile = File.createTempFile("test_legacy_", ".xlsx");
        try (FileWriter writer = new FileWriter(mockTempFile)) {
            writer.write("dummy-excel-content");
        }

        ReflectionTestUtils.setField(bulkIngestListener, "bulkFileProcessorService", bulkFileProcessorService);
        ReflectionTestUtils.setField(bulkIngestListener, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(bulkFileProcessorService, "batchIngestionProcessor", batchIngestionProcessor);
        ReflectionTestUtils.setField(bulkFileProcessorService, "bulkIngestJobRepository", bulkIngestJobRepository);
        ReflectionTestUtils.setField(batchIngestionProcessor, "integrationAuditLogger", integrationAuditLogger);
    }

    /**
     * Verifies that consuming a single bulk ingest Kafka message coordinates S3 download
     * and executes per-row audit logging for a single row.
     */
    @Test
    void testListen_SuccessFlow() {
        when(bulkIngestJobRepository.findByFileName(initDetail.getFileName())).thenReturn(java.util.Optional.empty());
        when(s3FileDownloader.downloadFileFromS3(initDetail.getFileName())).thenReturn(mockTempFile);

        doAnswer(invocation -> {
            Consumer<List<IngestRowData>> consumer = invocation.getArgument(1);
            Data rowData = Data.builder().date("01-01-2026").ulb("pg.citya").module("PGR").build();
            consumer.accept(List.of(IngestRowData.builder().data(rowData).build()));
            return 1;
        }).when(excelStreamingBatchReader).processExcelFileInBatches(any(File.class), any());

        bulkIngestListener.listen(record, "key", "bulk-ingest-init");

        verify(s3FileDownloader).downloadFileFromS3(initDetail.getFileName());
        verify(integrationAuditLogger, times(1)).logInboundApi(any(), any(), any(), any(), any());
        verify(bulkIngestJobRepository).updateJobStatus(any(), eq("COMPLETED"), eq(1), eq(0), anyLong());
    }

    /**
     * Verifies that when a batch contains multiple rows, {@link ExternalApiAuditLogger#logInboundApi}
     * is invoked separately for each individual row to guarantee isolated per-row audit tracking.
     */
    @Test
    void testListen_MultipleRowsPerRowAudited() {
        when(bulkIngestJobRepository.findByFileName(initDetail.getFileName())).thenReturn(java.util.Optional.empty());
        when(s3FileDownloader.downloadFileFromS3(initDetail.getFileName())).thenReturn(mockTempFile);

        Data row1 = Data.builder().date("01-01-2026").ulb("pg.citya").module("PGR").build();
        Data row2 = Data.builder().date("02-01-2026").ulb("pg.cityb").module("PGR").build();

        doAnswer(invocation -> {
            Consumer<List<IngestRowData>> consumer = invocation.getArgument(1);
            consumer.accept(List.of(
                    IngestRowData.builder().data(row1).build(),
                    IngestRowData.builder().data(row2).build()
            ));
            return 2;
        }).when(excelStreamingBatchReader).processExcelFileInBatches(any(File.class), any());

        bulkIngestListener.listen(record, "key", "bulk-ingest-init");

        // Should call logInboundApi for each of the 2 rows individually
        verify(integrationAuditLogger, times(2)).logInboundApi(any(), any(), any(), any(), any());
    }

    /**
     * Verifies that if a file job is already COMPLETED, S3 download and row ingestion are skipped for idempotency.
     */
    @Test
    void testListen_AlreadyCompletedFileSkipped() {
        org.egov.nationaldashboardingest.web.models.BulkIngestJob existingCompletedJob =
                org.egov.nationaldashboardingest.web.models.BulkIngestJob.builder()
                        .id("job-123")
                        .fileName(initDetail.getFileName())
                        .status("COMPLETED")
                        .totalRowsProcessed(10)
                        .failedRowsCount(0)
                        .build();

        when(bulkIngestJobRepository.findByFileName(initDetail.getFileName())).thenReturn(java.util.Optional.of(existingCompletedJob));

        bulkIngestListener.listen(record, "key", "bulk-ingest-init");

        // Should skip downloading and processing
        verifyNoInteractions(s3FileDownloader);
        verifyNoInteractions(excelStreamingBatchReader);
    }
}
