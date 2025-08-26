package org.upyog.rs.validator;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;


/**
 * Service for validating incoming requests using Java Bean Validation (JSR-303/JSR-380).
 * This service utilizes a `Validator` instance to check for constraint violations
 * in the provided request object. If any violations are found, a `ValidationException`
 * is thrown with a detailed message.
 * Key Features:
 * - Identifies fields that must not be null.
 * - Filters and formats the violation messages to exclude unnecessary prefixes.
 * - Constructs a meaningful error message to assist with debugging.
 */

@Service
public class ValidatorService {

    private final Validator validator;

    public ValidatorService(Validator validator) {
        this.validator = validator;
    }

    public <T> void validateRequest(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            // Extract field names, removing "mobileToiletBookingDetail."
            String nullFields = violations.stream()
                    .filter(violation -> violation.getMessage().contains("must not be null"))
                    .map(violation -> violation.getPropertyPath().toString().replace("mobileToiletBookingDetail.", ""))
                    .collect(Collectors.joining(", "));

            // Construct the error message
            String errorMessage = "Validation failed";
            if (!nullFields.isEmpty()) {
                errorMessage += "; following fields must not be null: " + nullFields;
            }

            throw new ValidationException(errorMessage);
        }
    }
}
