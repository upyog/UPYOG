package org.upyog.adv.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.upyog.adv.util.BookingUtil;

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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(BookingUtil.DATE_FORMAT);

        try {
            LocalDate date = LocalDate.parse(dateStr, formatter);
            return true; // If parsing is successful, the date is valid
        } catch (DateTimeParseException e) {
            return false; // If parsing fails, the date is invalid
        }
    }
}

