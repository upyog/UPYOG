package org.upyog.dashboard.util;

import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

/**
 * Utility class for JSON formatting and parsing.
 */
public final class JsonUtil {

    private JsonUtil() {
        // Prevent instantiation
    }

    /**
     * Converts a raw string input into a valid JSON string. If it's not already valid JSON,
     * wraps it in an {"error": "..."} object.
     *
     * @param input        the input string
     * @param objectMapper the ObjectMapper to use
     * @return a valid JSON string
     */
    public static String toJsonString(String input, ObjectMapper objectMapper) {
        if (StringUtils.isBlank(input)) {
            return "{}";
        }
        try {
            com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(input);
            if (node != null && (node.isObject() || node.isArray())) {
                return input;
            }
        } catch (Exception ignored) {
        }
        try {
            return objectMapper.writeValueAsString(Map.of("error", input));
        } catch (Exception exception) {
            throw new RuntimeException("Failed to format input as JSON: " + exception.getMessage(), exception);
        }
    }
}
