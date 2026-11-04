package org.upyog.externalaudit.masking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.upyog.externalaudit.config.ExternalApiAuditProperties;
import org.upyog.externalaudit.constants.ExternalApiAuditConstants;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Field-name based payload masking for audit persistence.
 * Matches known secret/PII keys case-insensitively (alphanumerics only),
 * following the same principle as enc-client attribute masking without introducing a second MDMS policy.
 */
@Slf4j
@Component
public class SensitivePayloadMasker {

    private final ObjectMapper objectMapper;
    private final Set<String> sensitiveKeys;

    public SensitivePayloadMasker(ObjectMapper objectMapper, ExternalApiAuditProperties properties) {
        this.objectMapper = objectMapper;
        this.sensitiveKeys = properties.getSensitiveFields().stream()
                .map(SensitivePayloadMasker::normalizeKey)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a copy of {@code payload} with sensitive field values replaced by {@code ********}.
     * On serialization failure the original payload is returned unmasked and a warning is logged.
     */
    public Object mask(Object payload) {
        if (payload == null) {
            return null;
        }
        try {
            JsonNode node = objectMapper.valueToTree(payload);
            JsonNode masked = maskNode(node);
            if (payload instanceof String && masked.isTextual()) {
                return masked.asText();
            }
            return objectMapper.convertValue(masked, Object.class);
        } catch (Exception exception) {
            log.warn("Unable to mask integration audit payload: {}", exception.getMessage());
            return payload;
        }
    }

    private JsonNode maskNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return node;
        }
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node.deepCopy();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (sensitiveKeys.contains(normalizeKey(field.getKey()))) {
                    objectNode.put(field.getKey(), ExternalApiAuditConstants.MASKED_VALUE);
                } else {
                    objectNode.set(field.getKey(), maskNode(field.getValue()));
                }
            }
            return objectNode;
        }
        if (node.isArray()) {
            ArrayNode arrayNode = objectMapper.createArrayNode();
            for (JsonNode child : node) {
                arrayNode.add(maskNode(child));
            }
            return arrayNode;
        }
        return node;
    }

    /**
     * {@code Password}, {@code access_token}, and {@code ULBBankAccountNumber} all normalize to the same key.
     */
    static String normalizeKey(String key) {
        if (key == null) {
            return "";
        }
        return key.replaceAll("[^a-zA-Z0-9]", "").toLowerCase(Locale.ROOT);
    }
}
