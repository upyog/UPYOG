package upyog.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import upyog.notification.model.NotificationChannel;
import upyog.notification.model.NotificationConfig;
import upyog.notification.model.NotificationContext;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Builds {@link NotificationContext} from Kafka payload using MDMS JSONPath mappings.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationContextBuilder {

    private static final String DEFAULT_TENANT_PATH = "$.Allotments[0].tenantId";

    private final ObjectMapper objectMapper;

    public NotificationContext build(Map<String, Object> payload,
                                     NotificationConfig config,
                                     RequestInfo requestInfo) {
        try {
            DocumentContext document = JsonPath.parse(objectMapper.writeValueAsString(payload));
            String tenantId = readString(document, config.getTenantIdPath(), DEFAULT_TENANT_PATH);

            Set<String> mobileNumbers = readStringSet(document, config.getRecipientMobilePath());
            Set<String> userUuids = readStringSet(document, config.getRecipientUuidPath());
            String email = readString(document, config.getRecipientEmailPath(), null);

            Map<String, String> emailByMobile = new HashMap<>();
            if (StringUtils.hasText(email) && !mobileNumbers.isEmpty()) {
                emailByMobile.put(mobileNumbers.iterator().next(), email);
            }

            Map<String, String> variables = new HashMap<>();
            if (config.getVariables() != null) {
                config.getVariables().forEach((placeholder, path) ->
                        variables.put(placeholder, readString(document, path, "")));
            }

            return NotificationContext.builder()
                    .requestInfo(requestInfo)
                    .tenantId(tenantId)
                    .module(config.getModule())
                    .action(config.getAction())
                    .localizationModule(config.getLocalizationModule())
                    .mobileNumbers(mobileNumbers)
                    .userUuids(userUuids)
                    .emailByMobile(emailByMobile)
                    .templateVariables(variables)
                    .messageTemplates(config.getMessages() == null ? Map.of() : config.getMessages())
                    .channels(toChannels(config))
                    .build();
        } catch (Exception e) {
            log.error("Failed to build notification context for action={}: {}", config.getAction(), e.getMessage(), e);
            return null;
        }
    }

    public boolean matchesTrigger(Map<String, Object> payload, NotificationConfig config, String topic) {
        if (!StringUtils.hasText(config.getTriggerTopic()) || !config.getTriggerTopic().equals(topic)) {
            return false;
        }
        if (!StringUtils.hasText(config.getTriggerField())) {
            return true;
        }
        try {
            DocumentContext document = JsonPath.parse(objectMapper.writeValueAsString(payload));
            String actual = readString(document, config.getTriggerField(), null);
            return StringUtils.hasText(config.getTriggerValue())
                    && config.getTriggerValue().equalsIgnoreCase(actual);
        } catch (Exception e) {
            log.warn("Trigger evaluation failed for action={}: {}", config.getAction(), e.getMessage());
            return false;
        }
    }

    private Set<NotificationChannel> toChannels(NotificationConfig config) {
        Set<NotificationChannel> channels = EnumSet.noneOf(NotificationChannel.class);
        if (config.getChannelNames() == null) {
            return channels;
        }
        config.getChannelNames().stream()
                .map(NotificationChannel::from)
                .filter(c -> c != null)
                .forEach(channels::add);
        return channels;
    }

    private Set<String> readStringSet(DocumentContext document, String path) {
        Set<String> values = new HashSet<>();
        String value = readString(document, path, null);
        if (StringUtils.hasText(value)) {
            values.add(value);
        }
        return values;
    }

    private String readString(DocumentContext document, String path, String defaultValue) {
        if (!StringUtils.hasText(path)) {
            return defaultValue;
        }
        try {
            Object value = document.read(path);
            return value == null ? defaultValue : String.valueOf(value);
        } catch (Exception e) {
            log.debug("JSONPath read failed for path {}: {}", path, e.getMessage());
            return defaultValue;
        }
    }
}
