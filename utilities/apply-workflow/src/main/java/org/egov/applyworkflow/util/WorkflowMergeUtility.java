package org.egov.applyworkflow.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Component;

@Component
public class WorkflowMergeUtility {

    public JsonNode updateWorkflowObject(Object source, Object target) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode sourceJson = objectMapper.valueToTree(source);
        JsonNode targetJson = objectMapper.valueToTree(target);

        if (sourceJson == null || targetJson == null) {
            throw new IllegalArgumentException("Source or Target JSON cannot be null.");
        }

        // Update only matching fields from source to target
        updateMatchingFields(sourceJson, targetJson);

        return targetJson;
    }

    private void updateMatchingFields(JsonNode source, JsonNode target) {
        source.fields().forEachRemaining(entry -> {
            String fieldName = entry.getKey();
            JsonNode sourceValue = entry.getValue();
            JsonNode targetValue = target.get(fieldName);

            if (sourceValue.isObject() && targetValue != null && targetValue.isObject()) {
                // Recursive update for nested objects
                updateMatchingFields(sourceValue, targetValue);
            } else if (sourceValue.isArray() && targetValue != null && targetValue.isArray()) {
                // Handle array fields
                updateMatchingArrays((ArrayNode) sourceValue, (ArrayNode) targetValue);
            } else if (target.has(fieldName)) {
                // Update target field value if it exists in the source
                if (target instanceof ObjectNode) {
                    ((ObjectNode) target).set(fieldName, sourceValue);
                }
            }
        });
    }

    private void updateMatchingArrays(ArrayNode sourceArray, ArrayNode targetArray) {
        for (JsonNode sourceElement : sourceArray) {
            boolean updated = false;

            for (JsonNode targetElement : targetArray) {
                if (isMatchingElement(sourceElement, targetElement)) {
                    // Recursive update for matching array elements
                    updateMatchingFields(sourceElement, targetElement);
                    updated = true;
                    break;
                }
            }

            // If no matching element is found, skip the source element (do not add new elements)
            if (!updated) {
                // Log or handle unprocessed source elements if needed
            }
        }
    }

    private boolean isMatchingElement(JsonNode sourceElement, JsonNode targetElement) {
        // Define matching logic (e.g., based on "uuid" or another unique identifier)
        String matchingField = "uuid"; // Change this to your identifier key
        return sourceElement.has(matchingField)
                && targetElement.has(matchingField)
                && sourceElement.get(matchingField).equals(targetElement.get(matchingField));
    }
}
