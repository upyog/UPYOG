package org.upyog.rs.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.RequestServiceRepository;
import org.upyog.rs.service.DemandService;
import org.upyog.rs.service.EnrichmentService;
import org.upyog.rs.service.UserService;
import org.upyog.rs.service.WaterTankerService;
import org.upyog.rs.service.WorkflowService;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingDetail;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingRequest;
import org.upyog.rs.web.models.waterTanker.WaterTankerBookingSearchCriteria;
import org.upyog.rs.web.models.Workflow;
import org.upyog.rs.web.models.workflow.State;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WaterTankerServiceImpl implements WaterTankerService {

	@Autowired
	EnrichmentService enrichmentService;

	@Autowired
	RequestServiceRepository requestServiceRepository;

	@Autowired
	WorkflowService workflowService;

	@Autowired
	DemandService demandService;

	@Autowired
	RequestServiceConfiguration config;

	@Autowired
	private UserService userService;

	@Override
	public WaterTankerBookingDetail createNewWaterTankerBookingRequest(WaterTankerBookingRequest waterTankerRequest) {

		log.info("Create water tanker booking for user : " + waterTankerRequest.getRequestInfo().getUserInfo().getUuid()
				+ " for the request : " + waterTankerRequest.getWaterTankerBookingDetail());

		enrichmentService.enrichCreateWaterTankerRequest(waterTankerRequest);

		workflowService.updateWorkflowStatus(null, waterTankerRequest);

		// Get the uuid of User from user registry
		try {
			RequestInfo requestInfo = waterTankerRequest.getRequestInfo();
			ApplicantDetail applicantDetail = waterTankerRequest.getWaterTankerBookingDetail().getApplicantDetail();
			String tenantId = waterTankerRequest.getWaterTankerBookingDetail().getTenantId();
			org.upyog.rs.web.models.user.User user = userService.fetchExistingUser(tenantId, applicantDetail, requestInfo);
			if (user == null) {
				throw new RuntimeException("User not found for this mobile number: " +
						applicantDetail.getMobileNumber());
			}
			if(config.getIsUserProfileEnabled()) {
				waterTankerRequest.getWaterTankerBookingDetail().setApplicantUuid(user.getUuid());
			} else{
				// If user profile is not enabled, set the applicantUuid null
				waterTankerRequest.getWaterTankerBookingDetail().setApplicantUuid(null);
			}
			log.info("Applicant or User Uuid: " + user.getUuid());
		} catch (Exception e) {
			log.error("Error fetching or creating user: " + e.getMessage(), e);
			throw new RuntimeException("Failed to fetch/create user: " + e.getMessage(), e);
		}

		requestServiceRepository.saveWaterTankerBooking(waterTankerRequest);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();

		return waterTankerDetail;
	}

	@Override
	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(RequestInfo requestInfo,
			WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria) {
		/*
		 * Retrieve WT booking details from the repository based on search criteria and
		 * and give the data already retrieved to the repository layer
		 */
		List<WaterTankerBookingDetail> applications = requestServiceRepository
				.getWaterTankerBookingDetails(waterTankerBookingSearchCriteria);

		/**
		 * Check if the retrieved list is empty using Spring's CollectionUtils Prevents
		 * potential null pointer exceptions by returning an empty list Ensures
		 * consistent return type and prevents calling methods from handling null
		 */
		if (CollectionUtils.isEmpty(applications)) {
			return new ArrayList<>();
		}
		if (config.getIsUserProfileEnabled()) {
			// Enrich each booking with user details
			for (WaterTankerBookingDetail booking : applications) {
				userService.enrichBookingWithUserDetails(booking, waterTankerBookingSearchCriteria);
			}
		}
		// Return retrieved application
		return applications;
	}

	@Override
	public Integer getApplicationsCount(WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria,
			RequestInfo requestInfo) {
		waterTankerBookingSearchCriteria.setCountCall(true);
		Integer bookingCount = 0;

		waterTankerBookingSearchCriteria = addCreatedByMeToCriteria(waterTankerBookingSearchCriteria, requestInfo);
		bookingCount = requestServiceRepository.getApplicationsCount(waterTankerBookingSearchCriteria);

		return bookingCount;
	}

	private WaterTankerBookingSearchCriteria addCreatedByMeToCriteria(WaterTankerBookingSearchCriteria criteria,
			RequestInfo requestInfo) {
		if (requestInfo.getUserInfo() == null) {
			log.info("Request info is null returning criteira");
			return criteria;
		}
		List<String> roles = new ArrayList<>();
		for (Role role : requestInfo.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		log.info("user roles for searching : " + roles);
		/**
		 * Citizen can see booking details only booked by him
		 */
		List<String> uuids = new ArrayList<>();
		if (roles.contains(RequestServiceConstants.CITIZEN)
				&& !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
			log.debug("loading data of created and by me" + uuids.toString());
		}
		return criteria;
	}

	@Override
	public WaterTankerBookingDetail updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest,
			String applicationStatus) {
		String bookingNo = waterTankerRequest.getWaterTankerBookingDetail().getBookingNo();
		log.info("Updating booking for booking no: {}", bookingNo);

		if (bookingNo == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		// If no payment request, update workflow status and process booking request
		if (waterTankerRequest.getWaterTankerBookingDetail().getWorkflow() != null) {
			State state = workflowService.updateWorkflowStatus(null, waterTankerRequest);
			enrichmentService.enrichWaterTankerBookingUponUpdate(state.getApplicationStatus(), waterTankerRequest);

			// If action is APPROVE, create demand
			if (RequestServiceConstants.ACTION_APPROVE
					.equals(waterTankerRequest.getWaterTankerBookingDetail().getWorkflow().getAction())) {
				demandService.createDemand(waterTankerRequest);
			}
		}

		log.info("Payment request is null, updating water tanker booking without payment");
		// If no payment request, just update the water tanker booking request
		requestServiceRepository.updateWaterTankerBooking(waterTankerRequest);

		Workflow workflow = waterTankerRequest.getWaterTankerBookingDetail().getWorkflow();

		if (workflow != null && waterTankerRequest.getWaterTankerBookingDetail().getWorkflow().getAction()
				.equalsIgnoreCase(RequestServiceConstants.WF_ACTION_SUBMIT_FEEDBACK)) {
			log.info("Processing feedback submission for booking no: {}", bookingNo);
			handleRSSubmitFeeback(waterTankerRequest);
		}

		if (workflow != null && waterTankerRequest.getWaterTankerBookingDetail().getWorkflow().getAction()
				.equalsIgnoreCase(RequestServiceConstants.WF_ACTION_REJECTED_BY_VENDOR)) {
			log.info("Processing rejection by vendor for booking no: {}", bookingNo);
			handleRejectedByVendor(waterTankerRequest);
		}

		return waterTankerRequest.getWaterTankerBookingDetail();
	}

	@Override
	public void updateWaterTankerBooking(PaymentRequest paymentRequest, String applicationStatus) {
		log.info("Payment request: {}", paymentRequest);
		// Handle the payment request and update the water tanker booking if applicable
		WaterTankerBookingDetail waterTankerDetail=null;
		if (paymentRequest != null) {
			try {
				String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
				waterTankerDetail = requestServiceRepository
						.getWaterTankerBookingDetails(
								WaterTankerBookingSearchCriteria.builder().bookingNo(consumerCode).build())
						.stream().findFirst().orElse(null);
				log.info("Water tanker booking detail: {}", waterTankerDetail);
				log.info("Consumer code: {}", consumerCode);
				if (waterTankerDetail == null) {
					log.info("Application not found in consumer class while updating status");
				} else{
					// Update the booking details
					waterTankerDetail.getAuditDetails()
							.setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
					waterTankerDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
					waterTankerDetail.setBookingStatus(applicationStatus);
					waterTankerDetail.setPaymentDate(System.currentTimeMillis());

					log.info("Water tanker detail after updating booking status: {}", waterTankerDetail);

					// Update water tanker booking request
					WaterTankerBookingRequest updatedWaterTankerRequest = WaterTankerBookingRequest.builder()
							.requestInfo(paymentRequest.getRequestInfo()).waterTankerBookingDetail(waterTankerDetail).build();

					log.info("Water Tanker Request to update application status in consumer: {}", updatedWaterTankerRequest);
					requestServiceRepository.updateWaterTankerBooking(updatedWaterTankerRequest);
				}
			}
			catch (Exception e) {
				log.error("Error while updating water tanker booking: {}", e.getMessage(), e);
				throw new CustomException("UPDATE_FAILED", "Failed to update water tanker booking");
			}

		}
		log.info("final object {}", waterTankerDetail);
	}

	private void handleRSSubmitFeeback(WaterTankerBookingRequest waterTankerRequest) {
		log.info("Handling water tanker Submit Feedback for request: {}", waterTankerRequest);
		User citizen = waterTankerRequest.getRequestInfo().getUserInfo();
		if (!citizen.getUuid().equalsIgnoreCase(waterTankerRequest.getRequestInfo().getUserInfo().getUuid())) {
			throw new CustomException("Rating Error", " Only owner of the application can submit the feedback !.");
		}
		if (waterTankerRequest.getWaterTankerBookingDetail().getWorkflow().getRating() == null) {
			throw new CustomException("Rating Error", " Rating has to be provided!");
		} else if (config.getAverageRatingCommentMandatory() != null
				&& Integer.parseInt(config.getAverageRatingCommentMandatory()) > waterTankerRequest
						.getWaterTankerBookingDetail().getWorkflow().getRating()) {

			throw new CustomException("Rating Error", " Comment mandatory for rating "
					+ waterTankerRequest.getWaterTankerBookingDetail().getWorkflow().getRating());
		}

	}

	private void handleRejectedByVendor(WaterTankerBookingRequest waterTankerRequest) {
		log.info("Handling rejected by vendor for request: {}", waterTankerRequest);
		WaterTankerBookingDetail tankerRequest = waterTankerRequest.getWaterTankerBookingDetail();
		tankerRequest.setVendorId(null);

		org.upyog.rs.web.models.Workflow workflow = tankerRequest.getWorkflow();
		if ((StringUtils.isBlank(workflow.getComments()))) {
			throw new CustomException("", " Comment is mandatory to reject the request for vendor.");
		}
	}

}
