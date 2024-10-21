
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
import org.egov.ptr.util.ResponseInfoFactory;
import org.egov.ptr.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping("/pet-registration")
public class PetController {

	@Autowired
	private PetRegistrationService petRegistrationService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationCreate(
			@ApiParam(value = "Details for the new Pet Registration Application(s) + RequestInfo meta data.", required = true) @Valid @RequestBody PetRegistrationRequest petRegistrationRequest) {
		List<PetRegistrationApplication> applications = petRegistrationService
				.registerPtrRequest(petRegistrationRequest);
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(petRegistrationRequest.getRequestInfo(), true);
		PetRegistrationResponse response = PetRegistrationResponse.builder().petRegistrationApplications(applications)
				.responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

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

	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationUpdate(
			@ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody PetRegistrationRequest petRegistrationRequest) {
		PetRegistrationApplication application = petRegistrationService.updatePtrApplication(petRegistrationRequest);

		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(petRegistrationRequest.getRequestInfo(), true);
		PetRegistrationResponse response = PetRegistrationResponse.builder()
				.petRegistrationApplications(Collections.singletonList(application)).responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = { "/{servicename}/{jobname}/_batch", "/_batch" }, method = RequestMethod.POST)
	public ResponseEntity sendReminderAndExpire(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@PathVariable(required = false) String servicename, @PathVariable(required = true) String jobname) {

		petRegistrationService.runJob(servicename, jobname, requestInfoWrapper.getRequestInfo());

		return new ResponseEntity(HttpStatus.ACCEPTED);
	}
}
