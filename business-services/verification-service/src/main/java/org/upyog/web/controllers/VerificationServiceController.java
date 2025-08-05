package org.upyog.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.upyog.service.CommonService;
import org.upyog.web.models.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")

@Controller
@RequestMapping("/validity")
public class VerificationServiceController {

	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<CommonModuleResponse> v1RegistrationSearchPost(
			@RequestBody @Valid ModuleSearchRequest request)
	{
		CommonDetails commonDetail = commonService.getApplicationCommonDetails(request);
		CommonModuleResponse response = CommonModuleResponse.builder()
                                .commonDetail(commonDetail)
				.responseInfo(new ResponseInfo())
				.build();

		return new ResponseEntity<CommonModuleResponse>(response, HttpStatus.OK);
	}

}
