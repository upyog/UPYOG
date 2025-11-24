
package org.egov.ptr.web.controllers;

import java.util.Collections;
import java.util.List;


import org.egov.common.contract.response.ResponseInfo;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.PetRegistrationResponse;
import org.egov.ptr.service.PetRegistrationService;
import org.egov.ptr.service.PetSchedulerService;
import org.egov.ptr.util.ResponseInfoFactory;
import org.egov.ptr.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


/**
 * Controller for handling pet registration-related operations.
 */
@Tag(name = "Pet Registration", description = "APIs for pet registration operations")
@RestController
@RequestMapping("/pet-registration")
public class PetController {

	@Autowired
	private PetRegistrationService petRegistrationService;

	@Autowired
	private PetSchedulerService petSchedulerService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	/**
	 * Handles the creation of new pet registration applications.
	 *
	 * @param petRegistrationRequest The request containing application details and metadata.
	 * @return Response containing the created applications and response info.
	 */
	@Operation(summary = "Create pet registration application", description = "Creates a new pet registration application")
	@RequestMapping("/_create")
	public ResponseEntity<PetRegistrationResponse> petRegistrationCreate(
			@Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
			throws JsonMappingException, JsonProcessingException {
		List<PetRegistrationApplication> applications = petRegistrationService
				.registerPtrRequest(petRegistrationRequest);
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(petRegistrationRequest.getRequestInfo(), true);
		PetRegistrationResponse response = PetRegistrationResponse.builder().petRegistrationApplications(applications)
				.responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	/**
	 * Handles searching for pet registration applications based on criteria.
	 *
	 * @param requestInfoWrapper       The request containing metadata.
	 * @param petApplicationSearchCriteria The search criteria for filtering applications.
	 * @return Response containing the matching applications.
	 */
	@Operation(summary = "Search pet registration applications", description = "Searches for pet registration applications based on criteria")
	@RequestMapping("/_search")
	public ResponseEntity<PetRegistrationResponse> petRegistrationSearch(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PetApplicationSearchCriteria petApplicationSearchCriteria) {
		List<PetRegistrationApplication> applications = petRegistrationService
				.searchPtrApplications(requestInfoWrapper.getRequestInfo(), petApplicationSearchCriteria);
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		PetRegistrationResponse response = PetRegistrationResponse.builder().petRegistrationApplications(applications)
				.responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	/**
	 * Handles updating an existing pet registration application.
	 *
	 * @param petRegistrationRequest The request containing updated application details.
	 * @return Response containing the updated application.
	 */
	@Operation(summary = "Update pet registration application", description = "Updates an existing pet registration application")
	@RequestMapping("/_update")
	public ResponseEntity<PetRegistrationResponse> petRegistrationUpdate(
			@Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
			throws JsonMappingException, JsonProcessingException {
		PetRegistrationApplication application = petRegistrationService.updatePtrApplication(petRegistrationRequest);

		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(petRegistrationRequest.getRequestInfo(), true);
		PetRegistrationResponse response = PetRegistrationResponse.builder()
				.petRegistrationApplications(Collections.singletonList(application)).responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	/**
	 * Triggers the expiration process for pet applications.
	 *
	 * @return Response indicating the status of the scheduler trigger.
	 */
	@Operation(summary = "Trigger pet application expiration", description = "Triggers the expiration process for pet applications")
	@RequestMapping("/trigger-expire-petapplications")
	public ResponseEntity<String> triggerWorkflowUpdate() {
		try {
			petSchedulerService.expirePetApplications();
			return ResponseEntity.ok("Expire Scheduler triggered successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to trigger scheduler: " + e.getMessage());
		}
	}


	/**
	 * Triggers advance notification for upcoming pet application expirations.
	 *
	 * @return Response indicating the status of the notification trigger.
	 */
	@Operation(summary = "Trigger advance notification", description = "Triggers advance notification for upcoming pet application expirations")
	@RequestMapping("/trigger-advance-notification")
	public ResponseEntity<String> triggerAdvanceNotification() {
		try {
			petSchedulerService.sendNotificationBeforeExpiration();
			return ResponseEntity.ok("Advance notification Scheduler triggered successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to trigger scheduler: " + e.getMessage());
		}
	}
}
