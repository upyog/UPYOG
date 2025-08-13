package org.upyog.chb.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.upyog.chb.util.CommunityHallBookingUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * This class is a custom validator for validating date strings in the
 * Community Hall Booking module.
 * 
 * Purpose:
 * - To ensure that date strings conform to the expected format and are valid.
 * - To provide reusable validation logic for date fields in booking requests and other inputs.
 * 
 * Features:
 * - Implements the ConstraintValidator interface to integrate with the Java Bean Validation API.
 * - Validates date strings against a predefined date format.
 * - Handles null or empty date strings gracefully by treating them as valid.
 * - Logs validation errors for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. initialize:
 *    - Initializes the validator with any required configuration.
 *    - Currently, no initialization logic is needed.
 * 
 * 2. isValid:
 *    - Validates the given date string against the expected date format.
 *    - Returns true if the date is valid or null/empty; otherwise, returns false.
 * 
 * Usage:
 * - This class is used as a custom validator for date fields annotated with @ValidDate.
 * - It ensures consistent and reusable date validation logic across the module.
 */
public class DateValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        // Initialization code can go here if needed
    }

    @Override
    public boolean isValid(String dateStr, ConstraintValidatorContext context) {
        if (dateStr == null || dateStr.isEmpty()) {
            return true; 
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommunityHallBookingUtil.DATE_FORMAT);

        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            return true; // If parsing is successful, the date is valid
        } catch (DateTimeParseException e) {
            return false; // If parsing fails, the date is invalid
        }
    }
}

