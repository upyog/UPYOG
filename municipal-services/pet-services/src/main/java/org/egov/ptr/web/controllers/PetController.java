
package org.egov.ptr.web.controllers;

import java.util.Collections;
import java.util.List;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationActionRequest;
import org.egov.ptr.models.PetRegistrationActionResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

	@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
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

	@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<PetRegistrationResponse> petRegistrationSearch(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PetApplicationSearchCriteria petApplicationSearchCriteria) {
		List<PetRegistrationApplication> applications = petRegistrationService
				.searchPtrApplications(requestInfoWrapper.getRequestInfo(), petApplicationSearchCriteria);
		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		PetRegistrationResponse response = PetRegistrationResponse.builder().petRegistrationApplications(applications)
				.responseDetail(petRegistrationService.enrichResponseDetail(applications)).responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
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

    @CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
    @PostMapping({"/fetch","/fetch/{value}"})
    public ResponseEntity<?> calculateTLFee(@RequestBody PetRegistrationActionRequest ptradeLicenseActionRequest
    										, @PathVariable String value){
    	
    	PetRegistrationActionResponse response = null;
    	
    	if(StringUtils.equalsIgnoreCase(value, "CALCULATEFEE")) {
    		response = petRegistrationService.getApplicationDetails(ptradeLicenseActionRequest);
    	}else if(StringUtils.equalsIgnoreCase(value, "ACTIONS")){
    		response = petRegistrationService.getActionsOnApplication(ptradeLicenseActionRequest);
    	}else {
    		return new ResponseEntity("Provide parameter to be fetched in URL.", HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity(response, HttpStatus.OK);
    }
    
}
