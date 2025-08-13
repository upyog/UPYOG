
package org.egov.ewst.web.controllers;

import java.util.Collections;
import java.util.List;
import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.egov.ewst.models.EwasteRegistrationRequest;
import org.egov.ewst.models.EwasteRegistrationResponse;
import org.egov.ewst.service.EwasteService;
import org.egov.ewst.util.ResponseInfoFactory;
import org.egov.ewst.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.annotations.ApiParam;

/**
 * Controller class for handling e-waste application requests.
 * This class provides endpoints for creating, searching, and updating e-waste applications.
 */
@Controller
@RequestMapping("/ewaste-request")
public class EwasteController {

	@Autowired
	private EwasteService ewasteService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	/**
	 * Endpoint for creating a new e-waste application request.
	 *
	 * @param ewasteRegistrationRequest The request payload containing e-waste application details.
	 * @return ResponseEntity containing the created e-waste application and response information.
	 */
	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<EwasteRegistrationResponse> ewasteRequestCreate(
			@ApiParam(value = "Details for the new e-waste request + RequestInfo meta data.", required = true) @Valid @RequestBody EwasteRegistrationRequest ewasteRegistrationRequest) {

		List<EwasteApplication> application = ewasteService.createEwasteRequest(ewasteRegistrationRequest);

		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(ewasteRegistrationRequest.getRequestInfo(), true);

		EwasteRegistrationResponse response = EwasteRegistrationResponse.builder().ewasteApplication(application)
				.responseInfo(responseInfo).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Endpoint for searching e-waste applications based on search criteria.
	 *
	 * @param requestInfoWrapper The wrapper containing request information.
	 * @param ewasteApplicationSearchCriteria The search criteria for e-waste applications.
	 * @return ResponseEntity containing the list of matching e-waste applications and response information.
	 */
	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<EwasteRegistrationResponse> ewasteRegistrationSearch(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute EwasteApplicationSearchCriteria ewasteApplicationSearchCriteria) {

		List<EwasteApplication> applications = ewasteService
				.searchEwasteApplications(requestInfoWrapper.getRequestInfo(), ewasteApplicationSearchCriteria);

		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);

		EwasteRegistrationResponse response = EwasteRegistrationResponse.builder().ewasteApplication(applications)
				.responseInfo(responseInfo).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Endpoint for updating an existing e-waste application request.
	 *
	 * @param ewasteRegistrationRequest The request payload containing updated e-waste application details.
	 * @return ResponseEntity containing the updated e-waste application and response information.
	 */
	@RequestMapping(value = "/_update", method = RequestMethod.POST)
	public ResponseEntity<EwasteRegistrationResponse> ewasteRequestUpdate(
			@ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody EwasteRegistrationRequest ewasteRegistrationRequest) {
		EwasteApplication application = ewasteService.updateEwasteRequest(ewasteRegistrationRequest);

		ResponseInfo responseInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(ewasteRegistrationRequest.getRequestInfo(), true);
		EwasteRegistrationResponse response = EwasteRegistrationResponse.builder()
				.ewasteApplication(Collections.singletonList(application)).responseInfo(responseInfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
