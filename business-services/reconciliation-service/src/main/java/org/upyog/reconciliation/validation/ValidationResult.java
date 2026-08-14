package org.upyog.reconciliation.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ValidationResult {
    private boolean valid = true;
    private List<String> invalidFields = new ArrayList<>();
    private List<String> errors = new ArrayList<>();

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public void addError(String field, String message) {
        this.valid = false;
        this.invalidFields.add(field);
        this.errors.add(message);
    }

    public String getInvalidFieldsConcatenated() {
        return String.join(", ", invalidFields);
    }

    public String getErrorSummary() {
        return String.join("; ", errors);
    }
    
    public List<String> getErrors() {
        return errors;
    }
}
