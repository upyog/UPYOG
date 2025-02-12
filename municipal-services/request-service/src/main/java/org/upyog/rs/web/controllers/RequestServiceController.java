package org.upyog.rs.web.controllers;


import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.service.WaterTankerService;
import org.upyog.rs.util.RequestServiceUtil;
import org.upyog.rs.web.models.ResponseInfo;
import org.upyog.rs.web.models.WaterTankerBookingDetail;
import org.upyog.rs.web.models.WaterTankerBookingRequest;
import org.upyog.rs.web.models.WaterTankerBookingResponse;
import org.upyog.rs.web.models.WaterTankerBookingSearchCriteria;
import org.upyog.rs.web.models.WaterTankerBookingSearchResponse;
import org.upyog.rs.web.models.ResponseInfo.StatusEnum;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2025-01-16T15:46:56.897+05:30")

@Controller
@Slf4j
public class RequestServiceController {
	
	@Autowired
	private WaterTankerService waterTankerService;

	@PostMapping("/water-tanker/v1/_create")
	public ResponseEntity<WaterTankerBookingResponse> createWaterTankerBooking(
			@ApiParam(value = "Details for the water tanker booking time, payment and documents", required = true)
			@Valid @RequestBody WaterTankerBookingRequest waterTankerbookingRequest) {
		log.info("waterTankerbookingRequest : {}" , waterTankerbookingRequest);

		WaterTankerBookingDetail waterTankerDetail = waterTankerService.createNewWaterTankerBookingRequest(waterTankerbookingRequest);
		ResponseInfo info = RequestServiceUtil.createReponseInfo(waterTankerbookingRequest.getRequestInfo(),
				RequestServiceConstants.BOOKING_CREATED, StatusEnum.SUCCESSFUL);
		WaterTankerBookingResponse response = WaterTankerBookingResponse.builder()
				.waterTankerBookingApplication(waterTankerDetail)
				.responseInfo(info).build();
		return new ResponseEntity<WaterTankerBookingResponse>(response, HttpStatus.OK);
	}

	@PostMapping("/water-tanker/v1/_search")
	public ResponseEntity<WaterTankerBookingSearchResponse> searchWaterTankerBookingDetails(
			@ApiParam(value = "Details for the water tanker booking time, payment and documents", required = true)
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria) {

		List<WaterTankerBookingDetail> applications = null;
		Integer count = 0;


		applications = waterTankerService.getWaterTankerBookingDetails(requestInfoWrapper.getRequestInfo(),
				waterTankerBookingSearchCriteria);

		count = waterTankerService.getApplicationsCount(waterTankerBookingSearchCriteria,
		requestInfoWrapper.getRequestInfo());


		/*
		* Create Response Info with success status and used utilize method
		* to generate standardized response
		* */
		ResponseInfo responseInfo = RequestServiceUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(),
				RequestServiceConstants.BOOKING_DETAIL_FOUND, StatusEnum.SUCCESSFUL);
		/*
		*Build search response using builder and retrieve booking details and response metadata
		* */
		WaterTankerBookingSearchResponse response = WaterTankerBookingSearchResponse.builder().waterTankerBookingDetails(applications)
				.responseInfo(responseInfo)
				.count(count)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/water-tanker/v1/_update")
	public ResponseEntity<WaterTankerBookingResponse> waterTankerUpdate(
			@ApiParam(value = "Updated water tanker details and RequestInfo meta data.", required = true)
			@RequestBody WaterTankerBookingRequest waterTankerRequest) {
		
		WaterTankerBookingDetail waterTankerDetail = waterTankerService.updateWaterTankerBooking(waterTankerRequest, null, null);

		WaterTankerBookingResponse response = WaterTankerBookingResponse.builder().waterTankerBookingApplication(waterTankerDetail)
				.responseInfo(RequestServiceUtil.createReponseInfo(waterTankerRequest.getRequestInfo(),
						RequestServiceConstants.APPLICATION_UPDATED, StatusEnum.SUCCESSFUL))
				.build();
		return new ResponseEntity<WaterTankerBookingResponse>(response, HttpStatus.OK);
	}
}
