package org.egov.tl.repository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class DraftServiceClient {

    private final RestTemplate restTemplate;
    private final TLConfiguration config;

    public DraftServiceClient(RestTemplate restTemplate, TLConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    /**
     * Best-effort call to mark a draft as submitted after a successful TL create.
     * Failures are logged and never propagated to the caller.
     */
    public void markSubmitted(RequestInfo requestInfo, String draftId, String tenantId) {
        if (!config.isDraftServiceEnabled() || StringUtils.isBlank(draftId)) {
            return;
        }

        try {
            String url = config.getDraftServiceHost() + config.getDraftServiceMarkSubmittedPath();
            Map<String, Object> body = buildMarkSubmittedBody(requestInfo, draftId, tenantId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            restTemplate.postForObject(url, entity, Map.class);
            log.info("Marked draft {} as submitted for tenant {}", draftId, tenantId);
        } catch (Exception ex) {
            log.warn("Failed to mark draft {} as submitted (best-effort): {}", draftId, ex.getMessage());
        }
    }

    private Map<String, Object> buildMarkSubmittedBody(RequestInfo requestInfo, String draftId, String tenantId) {
        Map<String, Object> userInfo = new HashMap<>();
        if (requestInfo != null && requestInfo.getUserInfo() != null) {
            userInfo.put("uuid", requestInfo.getUserInfo().getUuid());
        }

        Map<String, Object> requestInfoMap = new HashMap<>();
        requestInfoMap.put("userInfo", userInfo);

        Map<String, Object> draft = new HashMap<>();
        draft.put("draftId", draftId);
        draft.put("tenantId", tenantId);

        Map<String, Object> body = new HashMap<>();
        body.put("RequestInfo", requestInfoMap);
        body.put("Draft", draft);
        return body;
    }
}
