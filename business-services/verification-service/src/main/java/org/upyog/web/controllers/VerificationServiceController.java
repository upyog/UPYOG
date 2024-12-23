package org.upyog.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
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

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")

@Controller
@RequestMapping("/validity")
public class VerificationServiceController {

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	private CommonService commonService;

	@Autowired
	public VerificationServiceController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<CommonModuleResponse> v1RegistrationSearchPost(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@ApiParam(value = "Unique application number to search") @Valid @RequestParam(value = "applicationNumber", required = true) String applicationNumber,
			@ApiParam(value = "Module name to search") @Valid @RequestParam(value = "moduleName", required = true) String moduleName) {
		
		CommonDetails commonDetail = commonService.getApplicationCommonDetails(requestInfoWrapper.getRequestInfo(),moduleName,applicationNumber);
		CommonModuleResponse response = CommonModuleResponse.builder().commonDetail(commonDetail)
				.responseInfo(new ResponseInfo())
				.build();

		return new ResponseEntity<CommonModuleResponse>(response, HttpStatus.OK);
	}

}
