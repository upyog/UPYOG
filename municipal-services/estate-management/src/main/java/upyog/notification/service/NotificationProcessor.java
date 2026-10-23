package upyog.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import upyog.notification.config.NotificationProperties;
import upyog.notification.model.NotificationConfig;
import upyog.notification.model.NotificationContext;

import java.util.List;
import java.util.Map;

/**
 * Config-driven notification processor invoked from Kafka consumers.
 * Business services only publish domain events; this class resolves MDMS rules and dispatches.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private final NotificationProperties properties;
    private final MdmsNotificationConfigService mdmsNotificationConfigService;
    private final NotificationContextBuilder notificationContextBuilder;
    private final NotificationOrchestrator notificationOrchestrator;
    private final ObjectMapper objectMapper;

    public void process(String topic, Map<String, Object> payload) {
        if (!properties.isEnabled() || payload == null || payload.isEmpty()) {
            return;
        }

        RequestInfo requestInfo = extractRequestInfo(payload);
        if (requestInfo == null) {
            log.warn("Skipping notification on topic={}: RequestInfo missing", topic);
            return;
        }

        String tenantId = extractTenantId(payload);
        List<NotificationConfig> configs = mdmsNotificationConfigService.getConfigs(
                requestInfo, tenantId, properties.getModuleName());

        if (CollectionUtils.isEmpty(configs)) {
            log.info("No notification configs for module={} on topic={}", properties.getModuleName(), topic);
            return;
        }

        configs.stream()
                .filter(config -> notificationContextBuilder.matchesTrigger(payload, config, topic))
                .forEach(config -> dispatch(payload, config, requestInfo));
    }

    private void dispatch(Map<String, Object> payload, NotificationConfig config, RequestInfo requestInfo) {
        NotificationContext context = notificationContextBuilder.build(payload, config, requestInfo);
        if (context == null) {
            return;
        }
        log.info("Dispatching notification module={}, action={}, topic trigger={}",
                config.getModule(), config.getAction(), config.getTriggerTopic());
        notificationOrchestrator.send(context);
    }

    @SuppressWarnings("unchecked")
    private RequestInfo extractRequestInfo(Map<String, Object> payload) {
        Object requestInfoObj = payload.get("RequestInfo");
        if (requestInfoObj == null) {
            return null;
        }
        return objectMapper.convertValue(requestInfoObj, RequestInfo.class);
    }

    @SuppressWarnings("unchecked")
    private String extractTenantId(Map<String, Object> payload) {
        try {
            List<Map<String, Object>> allotments = (List<Map<String, Object>>) payload.get("Allotments");
            if (allotments != null && !allotments.isEmpty()) {
                Object tenantId = allotments.get(0).get("tenantId");
                if (tenantId != null) {
                    return String.valueOf(tenantId);
                }
            }
        } catch (Exception ignored) {
            // fallback below
        }
        Object tenantId = payload.get("tenantId");
        return tenantId == null ? null : String.valueOf(tenantId);
    }
}
