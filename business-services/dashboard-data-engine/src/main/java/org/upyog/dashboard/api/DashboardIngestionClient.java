package org.upyog.dashboard.api;

import java.io.File;

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

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardIngestionClient {

    private final RestTemplate restTemplate;
    private final DashboardProperties dashboardProperties;

    @org.springframework.beans.factory.annotation.Value("${national.dashboard.ingest.url}")
    private String engineIngestUrl;

    /**
     * Uploads the generated Excel binary file to the dashboard ingestion engine
     * endpoint.
     *
     * @param excelFile temporary Excel file on local disk
     * @param moduleName target module name
     * @param tenantId state tenant ID
     * @return IngestionResult response from downstream ingestion engine
     */
    public IngestionResult uploadLegacyExcelFile(File excelFile, String moduleName, String tenantId) {
        log.info("Posting legacy Excel payload to downstream engine: {} (file size: {} bytes)", engineIngestUrl, excelFile.length());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(excelFile));
        body.add("moduleName", moduleName);
        body.add("tenantId", tenantId);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<IngestionResult> response = restTemplate.exchange(engineIngestUrl, HttpMethod.POST, requestEntity, IngestionResult.class);
            log.info("Received ingestion response status: {}", response.getStatusCode());
            return response.getBody();
        } catch (Exception exception) {
            log.error("Failed to upload legacy Excel file to engine API: {}", exception.getMessage(), exception);
            return IngestionResult.builder()
                    .ingestionStatus("FAILURE")
                    .failureReason("Failed downstream upload: " + exception.getMessage())
                    .build();
        }
    }
}
