package org.upyog.chb.web.controllers;

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
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.SchedulerService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingResponse;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallDemandEstimationCriteria;
import org.upyog.chb.web.models.CommunityHallDemandEstimationResponse;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityResponse;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.upyog.chb.web.models.ResponseInfo;
import org.upyog.chb.web.models.ResponseInfo.StatusEnum;
import org.upyog.chb.web.models.billing.Demand;

import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

/**
 * This controller class handles API endpoints for the Community Hall Booking module.
 * 
 * Purpose:
 * - To expose RESTful APIs for creating, updating, and searching community hall bookings.
 * - To act as the entry point for client requests related to community hall bookings.
 * 
 * Dependencies:
 * - CommunityHallBookingService: Handles business logic for booking operations.
 * - CommunityHallBookingUtil: Provides utility methods for creating standardized responses.
 * 
 * Features:
 * - Provides endpoints for creating initial bookings, updating bookings, and searching bookings.
 * - Validates incoming requests and delegates processing to the appropriate service layer.
 * - Constructs and returns standardized API responses with ResponseInfo metadata.
 * - Logs API requests and responses for debugging and monitoring purposes.
 * 
 * Endpoints:
 * 1. /v1/_createInit:
 *    - HTTP Method: POST
 *    - Description: Creates an initial community hall booking.
 *    - Request Body: CommunityHallBookingRequest containing initial booking details.
 *    - Response: CommunityHallBookingResponse with booking details and status.
 * 
 * 2. /v1/_update:
 *    - HTTP Method: POST
 *    - Description: Updates an existing community hall booking.
 *    - Request Body: CommunityHallBookingRequest containing updated booking details.
 *    - Response: CommunityHallBookingResponse with updated booking details and status.
 *    - Use Cases:
 *      - Update filestore ID for payment link and permission letter link.
 *      - Update booking status when cancelled.
 *      - Update workflow when the application reaches employee login.
 * 
 * 3. /v1/_search:
 *    - HTTP Method: POST
 *    - Description: Searches for community hall bookings based on criteria.
 *    - Request Body: RequestInfoWrapper containing request metadata.
 *    - Query Parameters: CommunityHallBookingSearchCriteria for filtering results.
 *    - Response: CommunityHallBookingResponse with a list of matching bookings.
 * 
 * Usage:
 * - This class is automatically managed by Spring and mapped to the "/booking" base path.
 * - It ensures consistent and reusable logic for handling booking-related API requests.
 */
@Controller
@RequestMapping("/booking")
public class CommunityHallBookingController {

	@Autowired
	private CommunityHallBookingService bookingService;
	
	@Autowired
	private DemandService demandService;
	
	@Autowired
	private SchedulerService schedulerService;
	
	@RequestMapping(value = "/v1/_create", method = RequestMethod.POST) 
	public ResponseEntity<CommunityHallBookingResponse> createBooking(
			@ApiParam(value = "Details for the community halls booking time payment and documents", required = true) @Valid @RequestBody CommunityHallBookingRequest communityHallsBookingRequest) {
		
		CommunityHallBookingDetail bookingDetail = bookingService.createBooking(communityHallsBookingRequest);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_CREATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder()
				.responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v1/_init", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> initBooking(
			@ApiParam(value = "Details for the community halls booking time payment and documents", required = true) @Valid @RequestBody CommunityHallBookingRequest communityHallsBookingRequest) {
		
		CommunityHallBookingDetail bookingDetail = bookingService.createInitBooking(communityHallsBookingRequest);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_INIT_CREATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder().responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/_update", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> v1UpdateBooking(
			@ApiParam(value = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody CommunityHallBookingRequest communityHallsBookingRequest) {
		
		/**
		 * This update booking method will be called for below two tasks : 
		 * 1.Update filestoreid for payment link and permission letter link
		 * 2. Update status when cancelled
		 * 3. Update workflow when the application has reached employee login
		 */
		
		CommunityHallBookingDetail bookingDetail = bookingService.updateBooking(communityHallsBookingRequest, null, 
				 BookingStatusEnum.valueOf(communityHallsBookingRequest.getHallsBookingApplication().getBookingStatus()));
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_UPDATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder().responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}

	@RequestMapping(value = "/v1/_search", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> v1SearchCommunityHallBooking(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute CommunityHallBookingSearchCriteria criteria) {
		List<CommunityHallBookingDetail> applications = bookingService.getBookingDetails(criteria, requestInfoWrapper.getRequestInfo());
		
		/**
		 * Count : it is used to show load more booking attribute on front end 
		 */
		Integer count = bookingService.getBookingCount(criteria, requestInfoWrapper.getRequestInfo());
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_LIST,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse response = CommunityHallBookingResponse.builder().hallsBookingApplication(applications).count(count)
				.responseInfo(info).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v1/_slot-search", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallSlotAvailabilityResponse> v1GetCommmunityHallSlotAvailablity(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute CommunityHallSlotSearchCriteria criteria) {
		CommunityHallSlotAvailabilityResponse communityHallSlotAvailabilityResponse  = bookingService.getCommunityHallSlotAvailability(criteria, requestInfoWrapper.getRequestInfo());
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_AVIALABILITY_SEARCH,
				StatusEnum.SUCCESSFUL);
		
		communityHallSlotAvailabilityResponse.setResponseInfo(info);
		
		return new ResponseEntity<>(communityHallSlotAvailabilityResponse, HttpStatus.OK);
	}
	
	@RequestMapping(value = "/v1/_estimate", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallDemandEstimationResponse> v1GetEstimateDemand(
			@ApiParam(value = "Details for the community halls booking for demand estimation", required = true) @Valid @RequestBody CommunityHallDemandEstimationCriteria estimationCriteria) {
		List<Demand> demands = demandService.getDemand(estimationCriteria);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(estimationCriteria.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_DEMAND_ESTIMATION,
				StatusEnum.SUCCESSFUL);
		CommunityHallDemandEstimationResponse response = CommunityHallDemandEstimationResponse.builder()
				.demands(demands)
				.responseInfo(info).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@RequestMapping("/trigger-workflow-update")
    public ResponseEntity<String> triggerWorkflowUpdate() {
        try {
            schedulerService.updateWorkflowForBookedApplications(); 
            return ResponseEntity.ok("Scheduler triggered successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to trigger scheduler: " + e.getMessage());
        }
    }
	
}
