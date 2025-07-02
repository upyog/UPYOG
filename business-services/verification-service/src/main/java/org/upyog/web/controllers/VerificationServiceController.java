package org.upyog.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.upyog.service.CommonService;
import org.upyog.web.models.CommonDetails;
import org.upyog.web.models.CommonModuleResponse;
import org.upyog.web.models.ResponseInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")

@Controller
@RequestMapping("/validity")
@Tag(name = "Verification Service", description = "APIs for verification service operations")
public class VerificationServiceController {

	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	@Operation(summary = "Search application details", description = "Retrieve application details by application number and module")
	public ResponseEntity<CommonModuleResponse> v1RegistrationSearchPost(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			 @Valid @RequestParam(value = "tenantId", required = true) String tenantId,
			 @Valid @RequestParam(value = "applicationNumber", required = true)
			String applicationNumber,
			@Valid @RequestParam(value = "moduleName", required = true) String moduleName) {

		CommonDetails commonDetail = commonService.getApplicationCommonDetails(requestInfoWrapper.getRequestInfo(),moduleName,applicationNumber,tenantId);
		CommonModuleResponse response = CommonModuleResponse.builder().commonDetail(commonDetail)
				.responseInfo(new ResponseInfo())
				.build();

		return new ResponseEntity<CommonModuleResponse>(response, HttpStatus.OK);
	}

}
