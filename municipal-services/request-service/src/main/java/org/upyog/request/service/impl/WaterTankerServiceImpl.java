package org.upyog.request.service.impl;



import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.request.service.EnrichmentService;
import org.upyog.request.service.WaterTankerService;
import org.upyog.request.service.WorkflowService;
import org.upyog.request.service.constant.RequestServiceConstants;
import org.upyog.request.service.repository.RequestServiceRepository;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;
import org.upyog.request.service.web.models.WaterTankerBookingSearchCriteria;
import org.upyog.request.service.web.models.workflow.State;

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

	@Override
	public WaterTankerBookingDetail createNewWaterTankerBookingRequest(WaterTankerBookingRequest waterTankerRequest) {

		log.info("Create water tanker booking for user : " + waterTankerRequest.getRequestInfo().getUserInfo().getUuid()
				+ " for the request : " + waterTankerRequest.getWaterTankerBookingDetail());

		enrichmentService.enrichCreateWaterTankerRequest(waterTankerRequest);
		
		workflowService.createWorkflowStatus(waterTankerRequest);

		requestServiceRepository.saveWaterTankerBooking(waterTankerRequest);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();

		return waterTankerDetail;
	}

	@Override
	public List<WaterTankerBookingDetail> getWaterTankerBookingDetails(RequestInfo requestInfo,
															 WaterTankerBookingSearchCriteria waterTankerBookingSearchCriteria) {
		/*
		* Retrieve WT booking details from the repository based on search criteria
		* and and give the data already retrieved to the repository layer
		* */
		List<WaterTankerBookingDetail> applications = requestServiceRepository
				.getWaterTankerBookingDetails(waterTankerBookingSearchCriteria);

		/**
		 *   Check if the retrieved list is empty using Spring's CollectionUtils
		 *    Prevents potential null pointer exceptions by returning an empty list
		 *    Ensures consistent return type and prevents calling methods from handling null
		 *    */
		if (CollectionUtils.isEmpty(applications)) {
			return new ArrayList<>();
		}

		//Return retrieved application
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
	public WaterTankerBookingDetail updateWaterTankerBooking(WaterTankerBookingRequest waterTankerRequest) {
		String bookingNo = waterTankerRequest.getWaterTankerBookingDetail().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		State state = workflowService.createWorkflowStatus(waterTankerRequest);
		enrichmentService.enrichWaterTankerBookingUponUpdate(state.getApplicationStatus(), waterTankerRequest);

		if (RequestServiceConstants.ACTION_APPROVE
				.equals(waterTankerRequest.getWaterTankerBookingDetail().getWorkflow().getAction())) {
			// TODO create demand

		}
		requestServiceRepository.updateWaterTankerBooking(waterTankerRequest);

		return waterTankerRequest.getWaterTankerBookingDetail();
	}
	
	

}
