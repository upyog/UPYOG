package org.upyog.chb.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.enums.SlotStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.service.WorkflowService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabiltityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

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
	
	@Autowired
	private MdmsUtil mdmsUtil;
	
	
	@Override
	public CommunityHallBookingDetail createBooking(
			@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall booking for user : " + communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		String tenantId = communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.")[0];
		if (communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT, "Please provide valid tenant id for booking creation");
		}
		
		Object mdmsData = null;//mdmsUtil.mDMSCall(communityHallsBookingRequest.getRequestInfo(), tenantId);
		
		//1. Validate request master data to confirm it has only valid data in records
		hallBookingValidator.validateCreate(communityHallsBookingRequest, mdmsData);
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
		//Add validation for search criteria
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if(bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE", "Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		CommunityHallBookingDetail bookingDetail = bookingDetails.get(0);
		bookingDetail.setBookingStatus(BookingStatusEnum.BOOKED.toString());
		CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder()
				.hallsBookingApplication(bookingDetail).build();
		updateBooking(bookingRequest);
	}

	@Override
	public List<CommunityHallSlotAvailabiltityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria) {
		//TODO add validation for the search fields
		List<CommunityHallSlotAvailabiltityDetail> availabiltityDetails = bookingRepository.getCommunityHallSlotAvailability(criteria);
		log.info("Availabiltity details fetched from DB :" + availabiltityDetails);
		
		List<CommunityHallSlotAvailabiltityDetail> availabiltityDetailsResponse = convertToCommunityHallAvailabilityResponse(criteria ,availabiltityDetails);
		log.info("Availabiltity details response after updating status :" + availabiltityDetailsResponse);
		return availabiltityDetailsResponse;
	}

	/**
	 * @param criteria
	 * @param availabiltityDetails
	 * @return
	 */
	private List<CommunityHallSlotAvailabiltityDetail> convertToCommunityHallAvailabilityResponse(
			CommunityHallSlotSearchCriteria criteria, List<CommunityHallSlotAvailabiltityDetail> availabiltityDetails) {
		
		List<CommunityHallSlotAvailabiltityDetail> availabiltityDetailsResponse = new ArrayList<CommunityHallSlotAvailabiltityDetail>();
		LocalDate startDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());
		
		/*
		 * if(criteria.getBookingStartDate().equals(criteria.getBookingEndDate())) {
		 * availabiltityDetailsResponse.add(createCommunityHallSlotAvailabiltityDetail(
		 * criteria, startDate, availabiltityDetails)); } else { }
		 */
		
		LocalDate endDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());
		
		List<LocalDate> totalDates = new ArrayList<>();
		while (!startDate.isAfter(endDate)) {
		    totalDates.add(startDate);
		  //  availabiltityDetailsResponse.add(createCommunityHallSlotAvailabiltityDetail(criteria, startDate, availabiltityDetails));
		    startDate = startDate.plusDays(1);
		}
		
		totalDates.stream().forEach(date -> {
			availabiltityDetailsResponse.add(createCommunityHallSlotAvailabiltityDetail(
					  criteria, date, availabiltityDetails));
		});
		
		availabiltityDetailsResponse.stream().forEach(detail -> {
			if(availabiltityDetails.contains(detail)) {
				detail.setSlotStaus(SlotStatusEnum.BOOKED.toString());
			}
		});
		
		return availabiltityDetailsResponse;
	}
	
	private CommunityHallSlotAvailabiltityDetail createCommunityHallSlotAvailabiltityDetail(CommunityHallSlotSearchCriteria criteria, LocalDate date,
			List<CommunityHallSlotAvailabiltityDetail> availabiltityDetails) {
		CommunityHallSlotAvailabiltityDetail availabiltityDetail = CommunityHallSlotAvailabiltityDetail.builder()
				.communityHallName(criteria.getCommunityHallName())
				.hallCode(criteria.getHallCode())
				.slotStaus(SlotStatusEnum.AVAILABLE.toString())
				.tenantId(criteria.getTenantId())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(date))
				.build();
		return availabiltityDetail;
	}
	
	
	public static void main(String[] args) {
        // Define the two LocalDates
        LocalDate startDate = LocalDate.of(2024, 6, 1);  // Start date
        LocalDate endDate = LocalDate.of(2024, 6, 1);   // End date

        // List to store the dates
        List<LocalDate> datesInRange = new ArrayList<>();

        // Add the start date to the list
        LocalDate currentDate = startDate;

        // Loop through all dates from start to end, inclusive
        while (!currentDate.isAfter(endDate)) {
            datesInRange.add(currentDate);
            currentDate = currentDate.plusDays(1);  // Move to the next day
        }

        // Print all dates in the range
        System.out.println("Dates between " + startDate + " and " + endDate + ":");
        for (LocalDate date : datesInRange) {
            System.out.println(date);
        }
    }

}
