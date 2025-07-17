package org.egov.finance.voucher.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class ValidationException extends RuntimeException {

    private final List<ValidationError> errors;

    public ValidationException(final List<ValidationError> errors) {
        this.errors = errors;
    }

    public ValidationException(final ValidationError... errors) {
        this.errors = Arrays.asList(errors);
    }

    public ValidationException(final String key, final String defaultMsg, final String... args) {
        errors = new ArrayList<>();
        errors.add(new ValidationError(key, defaultMsg, args));
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}