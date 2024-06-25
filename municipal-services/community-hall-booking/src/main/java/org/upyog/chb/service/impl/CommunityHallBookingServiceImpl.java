package org.upyog.chb.service.impl;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.constants.WorkflowStatus;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
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
	
	@Autowired
	private DemandService demandService;
	
	
	@Override
	public CommunityHallBookingDetail createBooking(
			@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall booking for user : " + communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		
		if (communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}
		
		//1. Validate request master data to confirm it has only valid data in records
		hallBookingValidator.validateCreate(communityHallsBookingRequest, communityHallsBookingRequest);
		//2. Add fields that has custom logic like booking no, ids using UUID 
		enrichmentService.enrichCreateBookingRequest(communityHallsBookingRequest, communityHallsBookingRequest);
		
		/**
		 * Workflow will come into picture once hall location is changes or
		 * booking is cancelled
		 * otherwise after payment booking will be auto approved
		 * 
		 */
		
		//3.Update workflow of the application 
		//workflowService.updateWorkflow(communityHallsBookingRequest, WorkflowStatus.CREATE);
		
		demandService.createDemand(communityHallsBookingRequest);
		
		//4.Persist the request using persister service
		bookingRepository.saveCommunityHallBooking(communityHallsBookingRequest);
		
		
		
		return communityHallsBookingRequest.getHallsBookingApplication();
	}

	@Override
	public CommunityHallBookingDetail createInitBooking(
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

	@Override
	public CommunityHallBookingDetail updateBooking(CommunityHallBookingRequest communityHallsBookingRequest) {
		String bookingNo = communityHallsBookingRequest.getHallsBookingApplication().getBookingNo();
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if(bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE", "Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		Object mdmsdata = null;
		hallBookingValidator.validateUpdate(communityHallsBookingRequest, mdmsdata);
		enrichmentService.enrichUpdateBookingRequest(communityHallsBookingRequest, mdmsdata);
	//	workflowService.updateWorkflow(communityHallsBookingRequest, WorkflowStatus.UPDATE);
		bookingRepository.updateBooking(communityHallsBookingRequest);
		return communityHallsBookingRequest.getHallsBookingApplication();
	}
	
	@Override
	public void updateBookingStatus(String bookingNo) {
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if(bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE", "Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		CommunityHallBookingDetail bookingDetail = bookingDetails.get(0);
		bookingDetail.setBookingStatus(BookingStatusEnum.PAYMENT_DONE.toString());
		CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder()
				.hallsBookingApplication(bookingDetail).build();
		updateBooking(bookingRequest);
	}

}
