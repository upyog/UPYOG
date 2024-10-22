package org.upyog.sv.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.service.StreetVendingService;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingResponse;
import org.upyog.sv.web.models.common.ResponseInfo.StatusEnum;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-16T13:19:19.125+05:30")

@Controller
@RequestMapping("/street-vending")
public class StreetVendingController {

	
	@Autowired
	private StreetVendingService streetVendingService;

	@RequestMapping(value = "/_create", method = RequestMethod.POST)
	public ResponseEntity<StreetVendingResponse> createStreetVendingApplication(@RequestBody StreetVendingRequest vendingRequest) {
		
		StreetVendingDetail streetVendingDetail = streetVendingService.createStreetVendingApplication(vendingRequest);
		
		StreetVendingResponse response = StreetVendingResponse.builder().streetVendingDetail(streetVendingDetail)
				.responseInfo(StreetVendingUtil.createReponseInfo(vendingRequest.getRequestInfo(), 
						StreetVendingConstants.APPLICATION_CREATED ,StatusEnum.SUCCESSFUL))
				.build();
		
		return new ResponseEntity<StreetVendingResponse>(response, HttpStatus.OK);
	}

}
