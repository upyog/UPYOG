package upyog.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import upyog.notification.config.NotificationProperties;
import upyog.notification.model.NotificationChannel;
import upyog.repository.ServiceRequestRepository;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads localized templates and injects dynamic variables of the form {@code {variableName}}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalizationMessageService {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{([a-zA-Z0-9_]+)}");

    private final NotificationProperties properties;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ObjectMapper objectMapper;

    @SuppressWarnings("rawtypes")
    public String fetchMessages(String tenantId, String localizationModule, RequestInfo requestInfo) {
        StringBuilder uri = buildUri(tenantId, localizationModule, requestInfo);
        Map response = (Map) serviceRequestRepository.fetchResult(uri, requestInfo);
        if (response == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            log.error("Failed to serialize localization response: {}", e.getMessage(), e);
            return null;
        }
    }

    public String resolveMessage(String localizationMessages, String action, NotificationChannel channel,
                                 Map<String, String> variables) {
        String code = action + "_" + channel.name();
        String template = extractTemplate(localizationMessages, code);
        if (!StringUtils.hasText(template)) {
            log.warn("Localization template missing for code={}", code);
            return null;
        }
        return injectVariables(template, variables);
    }

    @SuppressWarnings("unchecked")
    private String extractTemplate(String localizationMessages, String code) {
        if (!StringUtils.hasText(localizationMessages)) {
            return null;
        }
        String path = "$..messages[?(@.code==\"" + code + "\")].message";
        try {
            Object messageObj = JsonPath.parse(localizationMessages).read(path);
            return ((ArrayList<String>) messageObj).get(0);
        } catch (Exception e) {
            log.warn("Failed to extract localization code {}: {}", code, e.getMessage());
            return null;
        }
    }

    public String injectVariables(String template, Map<String, String> variables) {
        if (!StringUtils.hasText(template) || variables == null || variables.isEmpty()) {
            return template;
        }
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = variables.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value == null ? "" : value));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private StringBuilder buildUri(String tenantId, String localizationModule, RequestInfo requestInfo) {
        String resolvedTenant = tenantId;
        if (properties.isLocalizationStateLevel() && tenantId != null && tenantId.contains(".")) {
            resolvedTenant = tenantId.split("\\.")[0];
        }

        String locale = properties.getDefaultLocale();
        if (requestInfo != null && StringUtils.hasText(requestInfo.getMsgId())) {
            String[] parts = requestInfo.getMsgId().split("\\|");
            if (parts.length >= 2 && StringUtils.hasText(parts[1])) {
                locale = parts[1];
            }
        }

        return new StringBuilder()
                .append(properties.getLocalizationHost())
                .append(properties.getLocalizationContextPath())
                .append(properties.getLocalizationSearchEndpoint())
                .append("?locale=").append(locale)
                .append("&tenantId=").append(resolvedTenant)
                .append("&module=").append(localizationModule);
    }
}
