package org.upyog.adv.service;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.upyog.adv.validator.CreateApplicationGroup;
import org.upyog.adv.validator.CreateDraftGroup;
import org.upyog.adv.web.models.BookingRequest;

@Service
public class AdvertisementValidationService {

	private final Validator validator;

	public AdvertisementValidationService(Validator validator) {
		this.validator = validator;
	}

	public void validateRequest(BookingRequest request) {
		// Choose validation group dynamically based on isDraftApplication
		Class<?> validationGroup = request.isDraftApplication() ? CreateDraftGroup.class : CreateApplicationGroup.class;

		// Validate the request based on the selected group
		Set<ConstraintViolation<BookingRequest>> violations = validator.validate(request, validationGroup);

		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<BookingRequest> violation : violations) {
				sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
			}
			throw new ValidationException("Validation failed: \n" + sb.toString());
		}
	}
}
