package org.upyog.wms.validator;

import java.util.LinkedHashMap;
import java.util.Map;
import org.egov.tracer.model.CustomException;

/**
 * Collects every validation error of a request so the user gets the full list in one response.
 * Error key = RULE_ID:fieldPath (unique per field/row), value = actionable reason.
 */
public final class ValidationErrors {

    private final Map<String, String> errors = new LinkedHashMap<>();

    public void add(String ruleId, String field, String message) {
        errors.putIfAbsent(ruleId + ":" + field, message);
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public Map<String, String> asMap() {
        return Map.copyOf(errors);
    }

    public boolean containsRule(String ruleId) {
        return errors.keySet().stream().anyMatch(k -> k.startsWith(ruleId + ":"));
    }

    public void throwIfAny() {
        if (!errors.isEmpty()) {
            throw new CustomException(new LinkedHashMap<>(errors));
        }
    }
}
