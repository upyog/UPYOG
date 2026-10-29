package org.upyog.adv.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.upyog.adv.validator.CreateApplicationGroup;
import org.upyog.adv.validator.CreateDraftGroup;
import org.upyog.adv.web.models.BookingRequest;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
/**
 * Service class for validating advertisement booking requests.
 * 
 * Key Responsibilities:
 * - Validates booking requests dynamically based on whether they are draft applications or final applications.
 * - Ensures that all required fields meet the defined constraints.
 * - Throws a ValidationException if any validation errors are found.
 * 
 * Dependencies:
 * - Validator: Used to perform validation based on the specified validation group.
 * - CreateApplicationGroup: Validation group for final booking applications.
 * - CreateDraftGroup: Validation group for draft booking applications.
 * 
 * Methods:
 * - `validateRequest`: Validates the booking request and throws an exception if validation fails.
 * 
 * Annotations:
 * - @Service: Marks this class as a Spring-managed service component.
 */

@Service
public class AdvertisementValidationService {

	private final Validator validator;

	public AdvertisementValidationService(Validator validator) {
		this.validator = validator;
	}

	/**
	 * Validates the booking request using the draft or application validation group.
	 *
	 * @param request booking request to validate
	 * @throws ValidationException when one or more constraint violations are found
	 */
	public void validateRequest(BookingRequest request) {
		Class<?> validationGroup = request.isDraftApplication() ? CreateDraftGroup.class : CreateApplicationGroup.class;

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
