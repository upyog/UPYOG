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
import org.upyog.dashboard.config.DashboardProperties;
import org.upyog.dashboard.model.IngestionResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Unified client component for uploading Excel datasets to downstream systems
 * (either directly via AWS S3 or over HTTP multipart REST endpoints).
 * <p>
 * Implements Single Responsibility and Dependency Inversion principles by centralizing
 * all file upload dispatching, error handling, and response standardisation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardIngestionClient {

    private final RestTemplate restTemplate;
    private final S3UploadClient s3UploadClient;
    private final DashboardProperties dashboardProperties;

    @Value("${national.dashboard.ingest.url}")
    private String engineIngestUrl;

    /**
     * Ingests the given dataset file by dynamically routing to AWS S3 or HTTP REST
     * based on the supplied upload/ingestion mode.
     *
     * @param file       the local file to ingest
     * @param moduleName target module name
     * @param tenantId   tenant identifier
     * @param uploadMode upload/ingest mode (e.g. "S3", "FILESTORE", "API")
     * @return normalized {@link IngestionResult}
     */
    public IngestionResult ingest(File file, String moduleName, String tenantId, String uploadMode) {
        if ("S3".equalsIgnoreCase(uploadMode) || "FILESTORE".equalsIgnoreCase(uploadMode)) {
            return uploadToS3(file, moduleName, tenantId);
        }
        return uploadViaHttp(file, moduleName, tenantId);
    }

    /**
     * Uploads the file to AWS S3 using {@link S3UploadClient} and builds a standardized {@link IngestionResult}.
     *
     * @param file       temporary local file
     * @param moduleName target module name
     * @param tenantId   tenant identifier
     * @return normalized {@link IngestionResult}
     */
    public IngestionResult uploadToS3(File file, String moduleName, String tenantId) {
        try {
            String fileStoreId = s3UploadClient.uploadFile(file, tenantId, moduleName);
            if (fileStoreId != null) {
                return IngestionResult.builder()
                        .ingestionStatus("SUCCESS")
                        .responseData("{\"fileStoreId\": \"" + fileStoreId + "\"}")
                        .build();
            }
            return IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason("Failed to upload file to S3")
                    .build();
        } catch (Exception exception) {
            log.error("DashboardIngestionClient | Failed to upload file to S3 for module {}", moduleName, exception);
            return IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason("Exception during S3 upload: " + exception.getMessage())
                    .build();
        }
    }

    /**
     * Uploads the generated binary file to the dashboard ingestion engine over HTTP multipart POST.
     *
     * @param file       temporary file on local disk
     * @param moduleName target module name
     * @param tenantId   state tenant ID
     * @return IngestionResult response from downstream ingestion engine
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
