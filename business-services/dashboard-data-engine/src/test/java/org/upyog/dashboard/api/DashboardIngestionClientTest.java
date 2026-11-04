package org.upyog.dashboard.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.service.BulkIngestionInitService;

/**
 * Unit test suite for {@link DashboardIngestionClient}.
 * <p>
 * Verifies upload routing, S3 client delegation, response wrapping, and conditional invocation
 * of downstream {@link BulkIngestionInitService} upon upload success/failure.
 * </p>
 */
@ExtendWith(MockitoExtension.class)
class DashboardIngestionClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private S3UploadClient s3UploadClient;

    @Mock
    private BulkIngestionInitService bulkIngestionInitService;

    private DashboardIngestionClient client;

    /**
     * Sets up mock collaborators and initializes the {@link DashboardIngestionClient} instance before each test.
     */
    @BeforeEach
    void setUp() {
        client = new DashboardIngestionClient(
                restTemplate,
                s3UploadClient,
                bulkIngestionInitService
        );
    }

    /**
     * Verifies that when S3 file upload succeeds, the client triggers the downstream
     * bulk ingestion initialization service and packages the response into a SUCCESS {@link IngestionResult}.
     *
     * @throws Exception if temporary test file creation fails
     */
    @Test
    @DisplayName("uploadToS3 triggers bulk ingest init service on successful S3 upload")
    void uploadToS3_success_triggersBulkInitService() throws Exception {
        File file = File.createTempFile("test_upload", ".xlsx");
        file.deleteOnExit();

        when(s3UploadClient.uploadFile(file, "pg", "DASHBOARD"))
                .thenReturn("UPYOG/pg/DASHBOARD/test_upload.xlsx");
        when(bulkIngestionInitService.initializeBulkIngestion(anyString(), any()))
                .thenReturn("{\"status\":\"INITIATED\", \"jobId\":\"12345\"}");

        IngestionResult result = client.uploadToS3(file, "DASHBOARD", "pg");

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
        assertThat(result.getResponseData()).contains("UPYOG/pg/DASHBOARD/test_upload.xlsx");
        assertThat(result.getResponseData()).contains("INITIATED");

        verify(bulkIngestionInitService).initializeBulkIngestion(anyString(), any());
    }

    /**
     * Verifies that when S3 file upload fails (e.g. returns null), the client returns a FAILURE
     * {@link IngestionResult} and does NOT invoke the downstream bulk initialization service.
     */
    @Test
    @DisplayName("uploadToS3 does not trigger bulk ingest init service when S3 upload fails")
    void uploadToS3_failure_doesNotTriggerBulkInitService() {
        File file = new File("non_existent_file.xlsx");

        when(s3UploadClient.uploadFile(file, "pg", "DASHBOARD"))
                .thenReturn(null);

        IngestionResult result = client.uploadToS3(file, "DASHBOARD", "pg");

        assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
        verify(bulkIngestionInitService, never()).initializeBulkIngestion(anyString(), any());
    }

    @Test
    @DisplayName("uploadToS3 passes explicit delimiter to bulkIngestionInitService for flat dataset files")
    void uploadToS3_withDelimiter_passesDelimiterToBulkInitService() throws Exception {
        File file = File.createTempFile("test_pt_bulk", ".psv");
        file.deleteOnExit();

        when(s3UploadClient.uploadFile(file, "pg", "PT"))
                .thenReturn("niuatt-internaltech/pg/PT/test_pt_bulk.psv");
        when(bulkIngestionInitService.initializeBulkIngestion("niuatt-internaltech/pg/PT/test_pt_bulk.psv", "|"))
                .thenReturn("{\"status\":\"INITIATED\"}");

        IngestionResult result = client.uploadToS3(file, "PT", "pg", "|");

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
        verify(bulkIngestionInitService).initializeBulkIngestion("niuatt-internaltech/pg/PT/test_pt_bulk.psv", "|");
    }
}
