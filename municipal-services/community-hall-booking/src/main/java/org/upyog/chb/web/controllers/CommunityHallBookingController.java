package org.upyog.chb.web.controllers;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.SchedulerService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingResponse;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallDemandEstimationCriteria;
import org.upyog.chb.web.models.CommunityHallDemandEstimationResponse;
import org.upyog.chb.web.models.VenueSlotAvailabilityDetail;
import org.upyog.chb.web.models.VenueSlotAvailabilityResponse;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;
import org.upyog.chb.web.models.RequestInfoWrapper;
import org.upyog.chb.web.models.ResponseInfo;
import org.upyog.chb.web.models.ResponseInfo.StatusEnum;
import org.upyog.chb.web.models.billing.Demand;

import io.swagger.v3.oas.annotations.Parameter;

@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

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
@RestController
@RequestMapping("/booking")
public class CommunityHallBookingController {

	private final CommunityHallBookingService bookingService;
	private final DemandService demandService;
	private final SchedulerService schedulerService;

	public CommunityHallBookingController(CommunityHallBookingService bookingService, DemandService demandService,
			SchedulerService schedulerService) {
		this.bookingService = bookingService;
		this.demandService = demandService;
		this.schedulerService = schedulerService;
	}
	
	/**
	 * Creates a new community hall booking.
	 *
	 * <p>
	 * This API endpoint receives the booking request, delegates creation to the
	 * service layer, and returns the created booking details. When a payment timer
	 * is in use for the booking, the booking response may include the remaining
	 * timer value.
	 * </p>
	 *
	 * @param communityHallsBookingRequest request payload containing booking details and request metadata
	 * @return response containing the created booking detail and standard response info
	 */
	@RequestMapping(value = "/v1/_create", method = RequestMethod.POST) 
	public ResponseEntity<CommunityHallBookingResponse> createBooking(
			@Parameter(description = "Details for the community halls booking time payment and documents", required = true) @Valid @RequestBody VenueBookingRequest communityHallsBookingRequest) {
		
		VenueBookingDetail bookingDetail = bookingService.createBooking(communityHallsBookingRequest);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_CREATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder()
				.responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}
	
	/**
	 * Creates an initial community hall booking record.
	 *
	 * <p>
	 * This API stores a temporary booking entry that can be completed later.
	 * It is typically used for booking initialization workflows before final
	 * booking confirmation.
	 * </p>
	 *
	 * @param communityHallsBookingRequest initial booking request payload
	 * @return response containing the created initial booking detail
	 */
	@RequestMapping(value = "/v1/_init", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> initBooking(
			@Parameter(description = "Details for the community halls booking time payment and documents", required = true) @Valid @RequestBody VenueBookingRequest communityHallsBookingRequest) {
		
		VenueBookingDetail bookingDetail = bookingService.createInitBooking(communityHallsBookingRequest);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_INIT_CREATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder().responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}

	/**
	 * Updates an existing community hall booking.
	 *
	 * <p>
	 * This endpoint is used to update booking details, payment status, or workflow
	 * status for an existing booking application.
	 * </p>
	 *
	 * @param communityHallsBookingRequest booking request containing updated details and request metadata
	 * @return response containing the updated booking detail
	 */
	@RequestMapping(value = "/v1/_update", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> v1UpdateBooking(
			@Parameter(description = "Details for the new (s) + RequestInfo meta data.", required = true) @Valid @RequestBody VenueBookingRequest communityHallsBookingRequest) {
		
		/**
		 * This update booking method will be called for below two tasks : 
		 * 1.Update filestoreid for payment link and permission letter link
		 * 2. Update status when cancelled
		 * 3. Update workflow when the application has reached employee login
		 */
		
		VenueBookingDetail bookingDetail = bookingService.updateBooking(communityHallsBookingRequest, null, 
				 BookingStatusEnum.valueOf(communityHallsBookingRequest.getVenueBookingApplication().getBookingStatus()));
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(communityHallsBookingRequest.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_UPDATED,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse communityHallResponse = CommunityHallBookingResponse.builder().responseInfo(info)
				.build();
		communityHallResponse.addNewHallsBookingApplication(bookingDetail);
		return new ResponseEntity<CommunityHallBookingResponse>(communityHallResponse, HttpStatus.OK);
	}

	/**
	 * Searches for community hall bookings based on provided criteria.
	 *
	 * <p>
	 * This API returns a list of matching bookings and the total count for the
	 * current search criteria, supporting front-end pagination.
	 * </p>
	 *
	 * @param requestInfoWrapper request metadata wrapper
	 * @param criteria            booking search criteria
	 * @return response containing matching booking details and count
	 */
	@RequestMapping(value = "/v1/_search", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallBookingResponse> v1SearchCommunityHallBooking(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute VenueBookingSearchCriteria criteria) {
		List<VenueBookingDetail> applications = bookingService.getBookingDetails(criteria, requestInfoWrapper.getRequestInfo());
		
		/**
		 * Count : it is used to show load more booking attribute on front end 
		 */
		Integer count = bookingService.getBookingCount(criteria, requestInfoWrapper.getRequestInfo());
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_LIST,
				StatusEnum.SUCCESSFUL);
		CommunityHallBookingResponse response = CommunityHallBookingResponse.builder().venueBookingApplication(applications).count(count)
				.responseInfo(info).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Searches for available community hall slots and returns slot availability.
	 *
	 * <p>
	 * This API endpoint supports the slot search workflow, including timer-based
	 * slot holds when requested. It returns available slots and any active
	 * payment timer value associated with the requested criteria.
	 * </p>
	 *
	 * @param requestInfoWrapper request metadata wrapper
	 * @param criteria           slot search criteria containing hall codes and dates
	 * @return response containing slot availability details and timer information
	 */
	@RequestMapping(value = "/v1/_slot-search", method = RequestMethod.POST)
	public ResponseEntity<VenueSlotAvailabilityResponse> v1GetCommmunityHallSlotAvailablity(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
            @Valid @ModelAttribute VenueSlotSearchCriteria criteria) {
		VenueSlotAvailabilityResponse communityHallSlotAvailabilityResponse  = bookingService.getCommunityHallSlotAvailability(criteria, requestInfoWrapper.getRequestInfo());
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(requestInfoWrapper.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_AVIALABILITY_SEARCH,
				StatusEnum.SUCCESSFUL);
		
		communityHallSlotAvailabilityResponse.setResponseInfo(info);
//		communityHallSlotAvailabilityResponse.setDraftId(criteria.getDraftId());
		return new ResponseEntity<>(communityHallSlotAvailabilityResponse, HttpStatus.OK);
	}
	
	/**
	 * Estimates demand for a community hall booking request.
	 *
	 * <p>
	 * This endpoint returns expected demand details based on the provided booking
	 * estimation criteria.
	 * </p>
	 *
	 * @param estimationCriteria demand estimation request payload
	 * @return response containing demand estimation results
	 */
	@RequestMapping(value = "/v1/_estimate", method = RequestMethod.POST)
	public ResponseEntity<CommunityHallDemandEstimationResponse> v1GetEstimateDemand(
			@Parameter(description = "Details for the community halls booking for demand estimation", required = true) @Valid @RequestBody CommunityHallDemandEstimationCriteria estimationCriteria) {
		List<Demand> demands = demandService.getDemand(estimationCriteria);
		ResponseInfo info = CommunityHallBookingUtil.createReponseInfo(estimationCriteria.getRequestInfo(), CommunityHallBookingConstants.COMMUNITY_HALL_DEMAND_ESTIMATION,
				StatusEnum.SUCCESSFUL);
		CommunityHallDemandEstimationResponse response = CommunityHallDemandEstimationResponse.builder()
				.demands(demands)
				.responseInfo(info).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * Manually triggers workflow update for booked applications.
	 *
	 * <p>
	 * This endpoint invokes the scheduler service to update workflows for
	 * bookings whose dates have passed and may require refund processing.
	 * </p>
	 *
	 * @return success message when workflow update is triggered, or error status on failure
	 */
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
