package org.upyog.adv.web.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.service.AdvertisementValidationService;
import org.upyog.adv.service.BookingService;
import org.upyog.adv.service.DemandService;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AdvertisementDemandEstimationCriteria;
import org.upyog.adv.web.models.AdvertisementDemandEstimationResponse;
import org.upyog.adv.web.models.AdvertisementResponse;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityResponse;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.ResponseInfo;
import org.upyog.adv.web.models.ResponseInfo.StatusEnum;
import org.upyog.adv.web.models.SlotSearchRequest;
import org.upyog.adv.web.models.billing.Demand;

import digit.models.coremodels.RequestInfoWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-15T13:40:01.245+05:30")

@Controller
@Api(value = "Advertisement Controller", description = "Operations related to Advertisement Booking")
@RequestMapping("/booking")
@Slf4j
public class AdvertisementServiceApiController {


	
	@Autowired
	private AdvertisementValidationService validationService;

	@Autowired
	private BookingService bookingService;
	
	@Autowired
	private DemandService demandService;
	
	

	@RequestMapping(value = "/v1/_create", method = RequestMethod.POST)
	public ResponseEntity<AdvertisementResponse> createBooking(
			@ApiParam(value = "Details for theadvertisement booking time, payment and documents", required = true) @Valid @RequestBody BookingRequest bookingRequest) {
		log.info("bookingRequest : {}" , bookingRequest);
		log.info("bookingRequest.isDraftApplication() {} ", bookingRequest.isDraftApplication());
		validationService.validateRequest(bookingRequest);
		BookingDetail bookingDetail = null;
		if (bookingRequest.isDraftApplication()) {
			bookingDetail = bookingService.createAdvertisementDraftApplication(bookingRequest);
		} else {

			bookingDetail = bookingService.createBooking(bookingRequest);
		}
		ResponseInfo info = BookingUtil.createReponseInfo(bookingRequest.getRequestInfo(),
				BookingConstants.BOOKING_CREATED, StatusEnum.SUCCESSFUL);
		AdvertisementResponse response = AdvertisementResponse.builder().responseInfo(info).build();
		response.addNewBookingApplication(bookingDetail);
		return new ResponseEntity<AdvertisementResponse>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v1/_search", method = RequestMethod.POST)
	public ResponseEntity<AdvertisementResponse> v1SearchAdvertisement(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
	        @Valid @ModelAttribute AdvertisementSearchCriteria criteria) {
		
		List<BookingDetail> applications = null;
		Integer count = 0;
		if ("true".equals(criteria.getIsDraftApplication())) {
			applications = bookingService.getAdvertisementDraftApplicationDetails(
					requestInfoWrapper.getRequestInfo(), criteria);
			count = applications != null ? applications.size() : 0;
		} else {
			applications = bookingService.getBookingDetails(criteria, requestInfoWrapper.getRequestInfo());					
			count = bookingService.getBookingCount(criteria, requestInfoWrapper.getRequestInfo());
		}
		
		ResponseInfo info = BookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(), BookingConstants.ADVERTISEMENT_BOOKING_LIST,
				StatusEnum.SUCCESSFUL);
		AdvertisementResponse response = AdvertisementResponse.builder().bookingApplication(applications).count(count)
				.responseInfo(info).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/_slot-search", method = RequestMethod.POST)
	public ResponseEntity<AdvertisementSlotAvailabilityResponse> v1GetAdvertisementSlotAvailablity(
			@Valid @RequestBody SlotSearchRequest slotSearchRequest) {

		List<AdvertisementSlotAvailabilityDetail> applications = bookingService
				.getAdvertisementSlotAvailability(slotSearchRequest.getCriteria(), slotSearchRequest.getRequestInfo());

		boolean isSlotBooked = bookingService.setSlotBookedFlag(applications);

		String draftId = bookingService.getDraftId(applications,
	    slotSearchRequest.getRequestInfo());

		ResponseInfo info = BookingUtil.createReponseInfo(slotSearchRequest.getRequestInfo(),
				BookingConstants.ADVERTISEMENT_AVAILABILITY_SEARCH, StatusEnum.SUCCESSFUL);

		AdvertisementSlotAvailabilityResponse response = AdvertisementSlotAvailabilityResponse.builder()
				.advertisementSlotAvailabiltityDetails(applications).responseInfo(info)
			    .draftId(draftId)
				.slotBooked(isSlotBooked).build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}


	
	 @RequestMapping(value = "/v1/_update", method = RequestMethod.POST)
	    public ResponseEntity<AdvertisementResponse> v1UpdateAdvertisementBooking(
	            @ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) 
	            @Valid @RequestBody BookingRequest advertisementBookingRequest) {
	        
	        /**
	         * This update booking method will be called for below two tasks : 
	         * 1.Update filestoreid for payment link and permission letter link
	         * 2. Update status when cancelled
	         * 
	         */
		 	validationService.validateRequest(advertisementBookingRequest);
	        BookingDetail bookingDetail = bookingService.updateBooking(advertisementBookingRequest, null, 
	                 BookingStatusEnum.valueOf(advertisementBookingRequest.getBookingApplication().getBookingStatus()));
	        ResponseInfo info = BookingUtil.createReponseInfo(advertisementBookingRequest.getRequestInfo(), BookingConstants.ADVERTISEMENT_BOOKING_UPDATED,
	                StatusEnum.SUCCESSFUL);
	        AdvertisementResponse advResponse = AdvertisementResponse.builder().responseInfo(info)
	                .build();
	        advResponse.addNewBookingApplication(bookingDetail);
	        return new ResponseEntity<AdvertisementResponse>(advResponse, HttpStatus.OK);
	    }
	 
	 //This calculates the estimate amount to be paid for the advetisement booking :
	 // Gets the demand 
	 @RequestMapping(value = "/v1/_estimate", method = RequestMethod.POST)
		public ResponseEntity<AdvertisementDemandEstimationResponse> v1GetEstimateDemand(
				@ApiParam(value = "Details for the advertisement booking for demand estimation", required = true) @Valid @RequestBody AdvertisementDemandEstimationCriteria estimationCriteria) {
			List<Demand> demands = demandService.getDemand(estimationCriteria);
			ResponseInfo info = BookingUtil.createReponseInfo(estimationCriteria.getRequestInfo(), BookingConstants.ADVERTISEMENT_DEMAND_ESTIMATION,
					StatusEnum.SUCCESSFUL);
			AdvertisementDemandEstimationResponse response = AdvertisementDemandEstimationResponse.builder()
					.demands(demands)
					.responseInfo(info).build();
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
	 
	 @RequestMapping(value = "/_deletedraft", method = RequestMethod.POST)
		public ResponseEntity<AdvertisementResponse> advertisementDeleteDraft(
				@ApiParam(value = "Details for draft deletion + RequestInfo meta data.", required = true) 
				@RequestBody RequestInfoWrapper requestInfoWrapper,
				@RequestParam(value = "draftId", required = true) String draftId) {
			String draftDiscardResponse = bookingService.deleteAdvertisementDraft(draftId);
			ResponseInfo responseInfo = BookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(),
					draftDiscardResponse, StatusEnum.SUCCESSFUL);
			AdvertisementResponse response = AdvertisementResponse.builder().responseInfo(responseInfo).build();
			return new ResponseEntity<AdvertisementResponse>(response, HttpStatus.OK);
		}
	 
	
}