package org.upyog.adv.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.upyog.adv.util.BookingUtil;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateValidator implements ConstraintValidator<ValidDate, String> {

	@Override
	public void initialize(ValidDate constraintAnnotation) {
		// No initialization required
	}

	@Override
	public boolean isValid(String dateStr, ConstraintValidatorContext context) {
		if (dateStr == null || dateStr.isEmpty()) {
			return true;
		}
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(BookingUtil.DATE_FORMAT);

		try {
			LocalDate.parse(dateStr, formatter);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}
}
