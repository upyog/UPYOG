
package org.egov.ptr.web.controllers;

import java.util.Collections;
import java.util.List;

<<<<<<< HEAD
=======
import javax.validation.Valid;
>>>>>>> master-LTS

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
<<<<<<< HEAD
=======
import org.springframework.stereotype.Controller;
>>>>>>> master-LTS
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
<<<<<<< HEAD

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

=======

import io.swagger.annotations.ApiParam;

>>>>>>> master-LTS

/**
 * Controller for handling pet registration-related operations.
 */
<<<<<<< HEAD
@Tag(name = "Pet Registration", description = "APIs for pet registration operations")
@RestController
=======
@Controller
>>>>>>> master-LTS
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
<<<<<<< HEAD
	@Operation(summary = "Create pet registration application", description = "Creates a new pet registration application")
	@RequestMapping("/_create")
	public ResponseEntity<PetRegistrationResponse> petRegistrationCreate(
			@Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
=======
	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationCreate(
			@ApiParam(value = "Details for the new Pet Registration Application(s) + RequestInfo meta data.", required = true) @Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
>>>>>>> master-LTS
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
<<<<<<< HEAD
	@Operation(summary = "Search pet registration applications", description = "Searches for pet registration applications based on criteria")
	@RequestMapping("/_search")
=======
	@RequestMapping(value = "/_search", method = RequestMethod.POST)
>>>>>>> master-LTS
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
<<<<<<< HEAD
	@Operation(summary = "Update pet registration application", description = "Updates an existing pet registration application")
	@RequestMapping("/_update")
	public ResponseEntity<PetRegistrationResponse> petRegistrationUpdate(
			@Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
=======
	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationUpdate(
			@ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody PetRegistrationRequest petRegistrationRequest)
>>>>>>> master-LTS
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
<<<<<<< HEAD
	@Operation(summary = "Trigger pet application expiration", description = "Triggers the expiration process for pet applications")
	@RequestMapping("/trigger-expire-petapplications")
=======
	@RequestMapping(value = "/trigger-expire-petapplications", method = RequestMethod.POST)
>>>>>>> master-LTS
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
<<<<<<< HEAD
	@Operation(summary = "Trigger advance notification", description = "Triggers advance notification for upcoming pet application expirations")
	@RequestMapping("/trigger-advance-notification")
=======
	@RequestMapping(value = "/trigger-advance-notification", method = RequestMethod.POST)
>>>>>>> master-LTS
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
