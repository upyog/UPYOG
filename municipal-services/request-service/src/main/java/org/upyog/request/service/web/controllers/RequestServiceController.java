package org.upyog.request.service.web.controllers;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.upyog.request.service.WaterTankerService;
import org.upyog.request.service.constant.RequestServiceConstants;
import org.upyog.request.service.util.RequestServiceUtil;
import org.upyog.request.service.web.models.ResponseInfo;
import org.upyog.request.service.web.models.ResponseInfo.StatusEnum;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;
import org.upyog.request.service.web.models.WaterTankerBookingResponse;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-01-16T15:46:56.897+05:30")

@Controller
@Slf4j
public class RequestServiceController {
	
	@Autowired
	WaterTankerService waterTankerService;

	@PostMapping("/water-tanker/v1/_create")
	public ResponseEntity<WaterTankerBookingResponse> createWaterTankerBooking(
			@ApiParam(value = "Details for the water tanker booking time, payment and documents", required = true) @Valid @RequestBody WaterTankerBookingRequest waterTankerbookingRequest) {
		log.info("waterTankerbookingRequest : {}" , waterTankerbookingRequest);

		WaterTankerBookingDetail waterTankerDetail = waterTankerService.createNewWaterTankerBookingRequest(waterTankerbookingRequest);
	
		ResponseInfo info = RequestServiceUtil.createReponseInfo(waterTankerbookingRequest.getRequestInfo(),
				RequestServiceConstants.BOOKING_CREATED, StatusEnum.SUCCESSFUL);
		WaterTankerBookingResponse response = WaterTankerBookingResponse.builder().responseInfo(info).build();
		response.addNewWaterTankerBookingApplication(waterTankerDetail);
		return new ResponseEntity<WaterTankerBookingResponse>(response, HttpStatus.OK);
	}

}
