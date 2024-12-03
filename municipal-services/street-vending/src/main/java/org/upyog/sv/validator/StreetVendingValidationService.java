package org.upyog.sv.validator;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.springframework.stereotype.Service;
import org.upyog.sv.web.models.StreetVendingRequest;

@Service
public class StreetVendingValidationService {

	private final Validator validator;

	public StreetVendingValidationService(Validator validator) {
		this.validator = validator;
	}

	public void validateRequest(StreetVendingRequest request) {
		// Choose validation group dynamically based on isDraftApplication
		Class<?> validationGroup = request.isDraftApplication() ? CreateDraftGroup.class : CreateApplicationGroup.class;

		// Validate the request based on the selected group
		Set<ConstraintViolation<StreetVendingRequest>> violations = validator.validate(request, validationGroup);

		if (!violations.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (ConstraintViolation<StreetVendingRequest> violation : violations) {
				sb.append(violation.getPropertyPath()).append(": ").append(violation.getMessage()).append("\n");
			}
			throw new ValidationException("Validation failed: \n" + sb.toString());
		}
	}
}
