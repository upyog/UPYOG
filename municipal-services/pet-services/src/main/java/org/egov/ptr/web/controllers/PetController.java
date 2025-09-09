
package org.egov.ptr.web.controllers;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.ApiParam;


/**
 * Controller for handling pet registration-related operations.
 */
@Controller
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
	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationCreate(
			@ApiParam(value = "Details for the new Pet Registration Application(s) + RequestInfo meta data.", required = true) @Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
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
	@RequestMapping(value = "/_search", method = RequestMethod.POST)
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
	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationUpdate(
			@ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
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
	@RequestMapping(value = "/trigger-expire-petapplications", method = RequestMethod.POST)
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
	@RequestMapping(value = "/trigger-advance-notification", method = RequestMethod.POST)
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
