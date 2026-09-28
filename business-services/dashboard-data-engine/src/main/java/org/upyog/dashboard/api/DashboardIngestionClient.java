package org.upyog.dashboard.api;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.upyog.dashboard.common.constants.DashboardConstants;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.service.BulkIngestionInitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Unified client component for uploading dataset files to downstream ingestion systems.
 * <p>
 * Supports multiple ingestion strategies:
 * <ul>
 *   <li>Direct upload to AWS S3 storage followed by triggering the bulk ingestion initialization REST API.</li>
 *   <li>Direct HTTP multipart POST transmission to the downstream ingestion engine.</li>
 * </ul>
 * </p>
 * <p>
 * Follows SOLID design principles (SRP, OCP, DIP) by centralizing file transfer logic and
 * delegating storage operations to {@link S3UploadClient} and post-upload initialization
 * to {@link BulkIngestionInitService}.
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardIngestionClient {

    private final RestTemplate restTemplate;
    private final S3UploadClient s3UploadClient;
    private final BulkIngestionInitService bulkIngestionInitService;

    @Value("${national.dashboard.ingest.url}")
    private String engineIngestUrl;

    /**
     * Ingests the given dataset file by dynamically routing to AWS S3 or HTTP multipart REST
     * based on the supplied upload/ingestion mode.
     *
     * @param file       the local file on disk containing the generated dataset
     * @param moduleName target business module name (e.g. PGR, PT, TL, WS)
     * @param tenantId   state or ULB tenant identifier (e.g. "pg", "pb.amritsar")
     * @param uploadMode upload/ingest mode strategy string (e.g. "S3", "FILESTORE", "API")
     * @return normalized {@link IngestionResult} containing execution status, response data, or error details
     */
    public IngestionResult ingest(File file, String moduleName, String tenantId, String uploadMode) {
        return ingest(file, moduleName, tenantId, uploadMode, null);
    }

    /**
     * Ingests the given dataset file with explicit delimiter metadata for flat files.
     *
     * @param file       the local file on disk containing the generated dataset
     * @param moduleName target business module name (e.g. PGR, PT, TL, WS)
     * @param tenantId   state or ULB tenant identifier (e.g. "pg", "pb.amritsar")
     * @param uploadMode upload/ingest mode strategy string (e.g. "S3", "FILESTORE", "API")
     * @param delimiter  flat file delimiter string (e.g. "|", ",") or null for Excel files
     * @return normalized {@link IngestionResult} containing execution status, response data, or error details
     */
    public IngestionResult ingest(File file, String moduleName, String tenantId, String uploadMode, String delimiter) {
        if (DashboardConstants.UPLOAD_MODE_S3.equalsIgnoreCase(uploadMode) || DashboardConstants.UPLOAD_MODE_FILESTORE.equalsIgnoreCase(uploadMode)) {
            return uploadToS3(file, moduleName, tenantId, delimiter);
        }
        return uploadViaHttp(file, moduleName, tenantId);
    }

    /**
     * Uploads the generated dataset file to AWS S3 storage and triggers downstream bulk ingestion initialization.
     *
     * @param file       the temporary dataset file on local disk
     * @param moduleName target module name
     * @param tenantId   state tenant identifier
     * @return normalized {@link IngestionResult} indicating SUCCESS or FAILURE
     */
    public IngestionResult uploadToS3(File file, String moduleName, String tenantId) {
        return uploadToS3(file, moduleName, tenantId, null);
    }

    /**
     * Uploads the generated dataset file to AWS S3 storage with optional delimiter metadata for bulk initialization.
     *
     * @param file       the temporary dataset file on local disk
     * @param moduleName target module name
     * @param tenantId   state tenant identifier
     * @param delimiter  flat file delimiter string (e.g. "|", ",") or null for Excel files
     * @return normalized {@link IngestionResult} indicating SUCCESS or FAILURE
     */
    public IngestionResult uploadToS3(File file, String moduleName, String tenantId, String delimiter) {
        try {
            String fileStoreId = s3UploadClient.uploadFile(file, tenantId, moduleName);
            if (fileStoreId != null) {
                log.info("File uploaded to S3 successfully with key: {}. Triggering bulk ingest init API with delimiter: {}...", fileStoreId, delimiter);
                String initResponse = bulkIngestionInitService.initializeBulkIngestion(fileStoreId, delimiter);
                return IngestionResult.builder()
                        .ingestionStatus(DashboardConstants.STATUS_SUCCESS)
                        .responseData("{\"fileStoreId\": \"" + fileStoreId + "\", \"bulkInitResponse\": " + (initResponse != null ? initResponse : "null") + "}")
                        .build();
            }
            return IngestionResult.builder()
                    .ingestionStatus(DashboardConstants.STATUS_FAILURE)
                    .failureReason("Failed to upload file to S3")
                    .build();
        } catch (Exception exception) {
            log.error("DashboardIngestionClient | Failed to upload file to S3 for module {}", moduleName, exception);
            return IngestionResult.builder()
                    .ingestionStatus(DashboardConstants.STATUS_FAILURE)
                    .failureReason("Exception during S3 upload: " + exception.getMessage())
                    .build();
        }
    }

    /**
     * Uploads the generated dataset binary file directly to the downstream dashboard ingestion engine over HTTP multipart POST.
     *
     * @param file       temporary dataset file on local disk
     * @param moduleName target module name
     * @param tenantId   state or ULB tenant ID
     * @return {@link IngestionResult} response body received from the downstream engine, or FAILURE result if transmission errors
     */
    public IngestionResult uploadViaHttp(File file, String moduleName, String tenantId) {
        log.info("Posting payload to downstream engine: {} (file size: {} bytes)", engineIngestUrl, file != null ? file.length() : 0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        body.add("moduleName", moduleName);
        body.add("tenantId", tenantId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<IngestionResult> response = restTemplate.exchange(engineIngestUrl, HttpMethod.POST, requestEntity, IngestionResult.class);
            log.info("Received ingestion response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception exception) {
            log.error("Failed to upload file to engine API: {}", exception.getMessage(), exception);
            return IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason("Failed downstream upload: " + exception.getMessage())
                    .build();
        }
    }
}
