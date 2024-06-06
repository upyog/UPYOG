package org.upyog.chb.service.impl;

import java.util.List;

import javax.validation.Valid;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.constants.WorkflowStatus;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.service.WorkflowService;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunityHallBookingServiceImpl implements CommunityHallBookingService {

	@Autowired
	private CommunityHallBookingRepository bookingRepository;
	@Autowired
	private CommunityHallBookingValidator hallBookingValidator;
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private EnrichmentService enrichmentService;
	
	
	@Override
	public CommunityHallBookingRequest createBooking(
			@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall booking for user : " + communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		
		if (communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}
		
		//1. Validate request master data to confirm it has only valid data in records
		hallBookingValidator.validateCreate(communityHallsBookingRequest, communityHallsBookingRequest);
		//2. Add fields that has custom logic like booking no, ids using UUID 
		enrichmentService.enrichCreateBookingRequest(communityHallsBookingRequest, communityHallsBookingRequest, null);
		//3.Update workflow of the application 
	//	workflowService.updateWorkflow(communityHallsBookingRequest, WorkflowStatus.CREATE);
		//4.Persist the request using persister service
		bookingRepository.saveCommunityHallBooking(communityHallsBookingRequest);
		
		return communityHallsBookingRequest;
	}

	@Override
	public CommunityHallBookingRequest createInitBooking(
			@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall temp booking for user : " + communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		bookingRepository.saveCommunityHallBookingInit(communityHallsBookingRequest);
		return null;
	}

	@Override
	public List<CommunityHallBookingDetail> getBookingDetails(
			CommunityHallBookingSearchCriteria bookingSearchCriteria) {
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		return bookingDetails;
	}

}
