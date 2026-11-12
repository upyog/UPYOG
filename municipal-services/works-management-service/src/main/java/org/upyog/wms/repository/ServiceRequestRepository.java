package org.upyog.wms.repository;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.upyog.wms.constants.ErrorCodes;

/** Single place for outbound HTTP calls (RestTemplate bean comes from tracer, with correlation-id propagation). */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ServiceRequestRepository {

    private final RestTemplate restTemplate;

    public <T> T post(String url, Object request, Class<T> responseType) {
        try {
            // URI (not String) so that already-encoded query parameters are not encoded a second time
            return restTemplate.postForObject(URI.create(url), request, responseType);
        } catch (RestClientResponseException e) {
            log.error("External call failed: {} -> {} {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCodes.EXTERNAL_SERVICE, "Call to " + url + " failed: "
                    + e.getResponseBodyAsString());
        } catch (ResourceAccessException e) {
            log.error("External service not reachable: {}", url, e);
            throw new CustomException(ErrorCodes.EXTERNAL_SERVICE, "Service not reachable: " + url);
        }
    }
}
