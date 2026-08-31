package org.upyog.dashboard.api;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.upyog.dashboard.config.DashboardProperties;

import lombok.extern.slf4j.Slf4j;
/**
 * HTTP Client wrapper for interacting with the egov-filestore microservice.
 * <p>
 * Handles the complexities of {@code multipart/form-data} uploads, specifically tailored for
 * seamlessly streaming generated Excel chunks into the persistent cloud storage layer.
 * </p>
 */

@Slf4j
@Service
public class FileStoreClient {

    private final RestTemplate restTemplate;
    private final DashboardProperties properties;

    /**
     * <p>Constructor for FileStoreClient.</p>
     *
     * @param restTemplate a {@link org.springframework.web.client.RestTemplate} object
     * @param properties a {@link org.upyog.dashboard.config.DashboardProperties} object
     */
    public FileStoreClient(RestTemplate restTemplate, DashboardProperties properties) {
        this.restTemplate = restTemplate;
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public String uploadFile(File file, String tenantId, String moduleName) {
        String url = UriComponentsBuilder.fromHttpUrl(properties.getFilestoreHost() + properties.getFilestoreUploadEndpoint())
                .queryParam("tenantId", tenantId)
                .queryParam("module", moduleName)
                .toUriString();

        log.info("Uploading file to egov-filestore at URL: {} (size: {} bytes)", url, file.length());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> files = (List<Map<String, Object>>) response.getBody().get("files");
                if (files != null && !files.isEmpty()) {
                    String fileStoreId = (String) files.get(0).get("fileStoreId");
                    log.info("File successfully uploaded to egov-filestore. FileStoreId: {}", fileStoreId);
                    return fileStoreId;
                }
            }
            log.error("Failed to extract fileStoreId from egov-filestore response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Exception while uploading file to egov-filestore: {}", e.getMessage(), e);
        }
        return null;
    }
}
