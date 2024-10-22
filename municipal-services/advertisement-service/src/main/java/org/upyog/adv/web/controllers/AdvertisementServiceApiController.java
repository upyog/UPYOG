package org.upyog.adv.web.controllers;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.service.BookingService;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AdvertisementResponse;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.ResponseInfo;
import org.upyog.adv.web.models.ResponseInfo.StatusEnum;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-15T13:40:01.245+05:30")

@Controller
@Api(value = "Advertisement Controller", description = "Operations related to Advertisement Booking")
@RequestMapping("/booking")
public class AdvertisementServiceApiController {

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	public AdvertisementServiceApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	@Autowired
	private BookingService bookingService;

	@RequestMapping(value = "/v1/_create", method = RequestMethod.POST)
	public ResponseEntity<AdvertisementResponse> createBooking(
			@ApiParam(value = "Details for theadvertisement booking time, payment and documents", required = true) @Valid @RequestBody BookingRequest bookingRequest) {

		BookingDetail bookingDetail = bookingService.createBooking(bookingRequest);
		ResponseInfo info = BookingUtil.createReponseInfo(bookingRequest.getRequestInfo(),
				BookingConstants.BOOKING_CREATED, StatusEnum.SUCCESSFUL);
		AdvertisementResponse response = AdvertisementResponse.builder().responseInfo(info).build();
		response.addNewBookingApplication(bookingDetail);
		return new ResponseEntity<AdvertisementResponse>(response, HttpStatus.OK);
	}

}
