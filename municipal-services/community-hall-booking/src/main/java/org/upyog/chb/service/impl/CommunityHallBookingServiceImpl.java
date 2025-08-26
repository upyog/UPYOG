package org.upyog.chb.service.impl;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.service.BookingTimerService;
import org.upyog.chb.service.CHBEncryptionService;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.service.WorkflowService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.ApplicantDetail;
import org.upyog.chb.web.models.BookingPaymentTimerDetails;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityResponse;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;
import org.upyog.chb.web.models.workflow.State;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This class implements the CommunityHallBookingService interface and provides
 * the business logic for the Community Hall Booking module.
 * 
 * Purpose:
 * - To handle all service-level operations related to community hall bookings, such as
 *   creating, updating, validating, and retrieving booking records.
 * - To coordinate between the repository, validation, workflow, and enrichment layers.
 * 
 * Dependencies:
 * - CommunityHallBookingRepository: Handles database operations for bookings.
 * - CommunityHallBookingValidator: Validates booking requests and search criteria.
 * - WorkflowService: Manages workflow-related operations for bookings.
 * - EnrichmentService: Enriches booking requests with additional data.
 * - DemandService: Handles demand generation and payment-related operations.
 * - MdmsUtil: Fetches and processes master data from MDMS.
 * - CHBEncryptionService: Handles encryption and decryption of sensitive booking data.
 * 
 * Features:
 * - Provides methods to create and update bookings while ensuring data validation and enrichment.
 * - Integrates with the workflow service to manage booking states.
 * - Handles slot availability checks and demand generation for bookings.
 * - Logs important operations and errors for debugging and monitoring purposes.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever the
 *   CommunityHallBookingService interface is required.
 * - It ensures consistent and reusable business logic for the Community Hall Booking module.
 */
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
	
	@Autowired
	private CHBEncryptionService encryptionService;
	
	@Autowired
	private BookingTimerService bookingTimerService;
	
	@Override
	public CommunityHallBookingDetail createBooking(@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall booking for user : "
				+ communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		// TODO move to util calss 
		String tenantId = communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.")[0];
		if (communityHallsBookingRequest.getHallsBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT,
					"Please provide valid tenant id for booking creation");
		}

		Object mdmsData = mdmsUtil.mDMSCall(communityHallsBookingRequest.getRequestInfo(), tenantId);

		// 1. Validate request master data to confirm it has only valid data in records
		hallBookingValidator.validateCreate(communityHallsBookingRequest, mdmsData);
		// 2. Add fields that has custom logic like booking no, ids using UUID
		enrichmentService.enrichCreateBookingRequest(communityHallsBookingRequest);
		
		//ENcrypt PII data of applicant
		encryptionService.encryptObject(communityHallsBookingRequest);

		/**
		 * Workflow will come into picture once hall location changes or booking is
		 * cancelled otherwise after payment booking will be auto approved
		 * 
		 */

		// 3.Update workflow of the application
		// workflowService.updateWorkflow(communityHallsBookingRequest,
		// WorkflowStatus.CREATE);

		demandService.createDemand(communityHallsBookingRequest, mdmsData, true);

		// 4.Persist the request using persister service
		bookingRepository.saveCommunityHallBooking(communityHallsBookingRequest);

		return communityHallsBookingRequest.getHallsBookingApplication();
	}
	
	@Override
	public CommunityHallBookingDetail createInitBooking(
			@Valid CommunityHallBookingRequest communityHallsBookingRequest) {
		log.info("Create community hall temp booking for user : "
				+ communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid());
		bookingRepository.saveCommunityHallBookingInit(communityHallsBookingRequest);
		return null;
	}

	@Override
	public List<CommunityHallBookingDetail> getBookingDetails(CommunityHallBookingSearchCriteria bookingSearchCriteria,
			RequestInfo info) {
		hallBookingValidator.validateSearch(info, bookingSearchCriteria);
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();
		bookingSearchCriteria  = addCreatedByMeToCriteria(bookingSearchCriteria, info);
		
		log.info("loading data based on criteria" + bookingSearchCriteria);
		
		if (bookingSearchCriteria.getMobileNumber() != null
				&& bookingSearchCriteria.getMobileNumber().trim().length() > 9) {

			ApplicantDetail applicantDetail = ApplicantDetail.builder()
					.applicantMobileNo(bookingSearchCriteria.getMobileNumber()).build();
			CommunityHallBookingDetail communityHallBookingDetail = CommunityHallBookingDetail.builder()
					.applicantDetail(applicantDetail).build();
			CommunityHallBookingRequest bookingRequest = CommunityHallBookingRequest.builder()
					.hallsBookingApplication(communityHallBookingDetail).requestInfo(info).build();

			communityHallBookingDetail = encryptionService.encryptObject(bookingRequest);

			bookingSearchCriteria
					.setMobileNumber(communityHallBookingDetail.getApplicantDetail().getApplicantMobileNo());

			log.info("loading data based on criteria after encrypting mobile no : " + bookingSearchCriteria);

		}
		
		bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if(CollectionUtils.isEmpty(bookingDetails)) {
			return bookingDetails;
		}
		bookingDetails = encryptionService.decryptObject(bookingDetails, info);
		
		return bookingDetails;
	}
	
	
	@Override
	public Integer getBookingCount(@Valid CommunityHallBookingSearchCriteria criteria,
			@NonNull RequestInfo requestInfo) {
		criteria.setCountCall(true);
		Integer bookingCount = 0;
		
		criteria  = addCreatedByMeToCriteria(criteria, requestInfo);
		bookingCount = bookingRepository.getBookingCount(criteria);
		
		return bookingCount;
	}

	
	private CommunityHallBookingSearchCriteria addCreatedByMeToCriteria(CommunityHallBookingSearchCriteria criteria, RequestInfo requestInfo) {
		if(requestInfo.getUserInfo() == null) {
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
		if (roles.contains(CommunityHallBookingConstants.CITIZEN) && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
			log.debug("loading data of created and by me" + uuids.toString());
		}
		return criteria;
	}

	@Override
	public CommunityHallBookingDetail updateBooking(CommunityHallBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status) {
		String bookingNo = communityHallsBookingRequest.getHallsBookingApplication().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		
		hallBookingValidator.validateUpdate(communityHallsBookingRequest.getHallsBookingApplication(), bookingDetails.get(0));

		convertBookingRequest(communityHallsBookingRequest, bookingDetails.get(0));

		
		//Update payment date and receipt no on successful payment when payment detail object is received
		if (paymentDetail != null) {
			communityHallsBookingRequest.getHallsBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			communityHallsBookingRequest.getHallsBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}
		//Update workflow of booking application for refund when the workflow object is not null in payload
		if (communityHallsBookingRequest.getHallsBookingApplication().getWorkflow()!=null) {
			State state = workflowService.updateWorkflow(communityHallsBookingRequest);
			status = BookingStatusEnum.valueOf(state.getApplicationStatus());
		}
		enrichmentService.enrichUpdateBookingRequest(communityHallsBookingRequest, status);
		bookingRepository.updateBooking(communityHallsBookingRequest);
		log.info("fetched booking detail and updated status "
				+ communityHallsBookingRequest.getHallsBookingApplication().getBookingStatus());
		return communityHallsBookingRequest.getHallsBookingApplication();
	}
	
	/**
	 * We are updating booking status synchronously for updating booking status on payment success 
	 * Deleting the timer entry here after successful update of booking
	 */
	@Transactional
	@Override
	public void updateBookingSynchronously(CommunityHallBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status, boolean deleteBookingTimer) {
		String bookingNo = communityHallsBookingRequest.getHallsBookingApplication().getBookingNo();
		log.info("Updating booking synchronously for booking no : " + bookingNo);
		if (bookingNo == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		CommunityHallBookingDetail bookingDetail = bookingDetails.get(0);
		communityHallsBookingRequest.setHallsBookingApplication(bookingDetail);
		bookingRepository.updateBookingSynchronously(bookingDetail.getBookingId(), communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid(), paymentDetail, status.toString());
		if(deleteBookingTimer) {
			log.info("Deleting booking timer with booking id  {}", communityHallsBookingRequest.getHallsBookingApplication().getBookingId());
			bookingTimerService.deleteBookingTimer(communityHallsBookingRequest.getHallsBookingApplication().getBookingId(), false);
		}
	}

	private void convertBookingRequest(CommunityHallBookingRequest communityHallsBookingRequest,
			CommunityHallBookingDetail bookingDetailDB) {
		CommunityHallBookingDetail bookingDetailRequest = communityHallsBookingRequest.getHallsBookingApplication();
		if (bookingDetailDB.getPermissionLetterFilestoreId() == null
				&& bookingDetailRequest.getPermissionLetterFilestoreId() != null) {
			bookingDetailDB.setPermissionLetterFilestoreId(bookingDetailRequest.getPermissionLetterFilestoreId());
		}
 
		if (bookingDetailDB.getPaymentReceiptFilestoreId() == null
				&& bookingDetailRequest.getPaymentReceiptFilestoreId() != null) {
			bookingDetailDB.setPaymentReceiptFilestoreId(bookingDetailRequest.getPaymentReceiptFilestoreId());
		}
		if(bookingDetailRequest.getWorkflow()!=null) {
			bookingDetailDB.setWorkflow(bookingDetailRequest.getWorkflow());
		}
		communityHallsBookingRequest.setHallsBookingApplication(bookingDetailDB);
		
	}

	@Override
	public CommunityHallSlotAvailabilityResponse getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria, RequestInfo info) {
		if (criteria.getCommunityHallCode() == null && CollectionUtils.isEmpty(criteria.getHallCodes())) {
			throw new CustomException("INVALID_HALL_CODE", "Invalid hall code provided for slot search");
		}
		log.info("criteria : {}", criteria);
		List<CommunityHallSlotAvailabilityDetail> availabiltityDetails = bookingRepository
				.getCommunityHallSlotAvailability(criteria);
		log.info("Availabiltity details fetched from DB :" + availabiltityDetails);

		List<CommunityHallSlotAvailabilityDetail> availabiltityDetailsList = convertToCommunityHallAvailabilityResponse(
				criteria, availabiltityDetails);

		Long timerValue = -1l;
		availabiltityDetailsList = checkTimerTableForAvailaibility(info, criteria, availabiltityDetailsList);
		boolean bookingAllowed = availabiltityDetailsList.stream()
				.anyMatch(detail -> BookingStatusEnum.BOOKED.toString().equals(detail.getSlotStaus()));

		if (!bookingAllowed && criteria.getIsTimerRequired()) {
			timerValue = bookingTimerService.getTimerValue(criteria, info, availabiltityDetailsList);
		}

		CommunityHallSlotAvailabilityResponse hallSlotAvailabilityResponse = CommunityHallSlotAvailabilityResponse
				.builder().hallSlotAvailabiltityDetails(availabiltityDetailsList).timerValue(timerValue).build();

		log.info("Availabiltity details response after updating status :" + hallSlotAvailabilityResponse);
		return hallSlotAvailabilityResponse;
	}



private List<CommunityHallSlotAvailabilityDetail> checkTimerTableForAvailaibility(
            RequestInfo info, CommunityHallSlotSearchCriteria criteria,
		List<CommunityHallSlotAvailabilityDetail> availabilityDetails) {

	List<BookingPaymentTimerDetails> timerDetails = bookingTimerService.getBookingFromTimerTable(info, criteria);

	// If timer details are null or empty, return availability details as is
	if (timerDetails == null || timerDetails.isEmpty()) {
		log.info("Timer details are null or empty, returning availability details as is.");
		return availabilityDetails;
	}

	Map<CommunityHallSlotAvailabilityDetail, CommunityHallSlotAvailabilityDetail> slotDetailsMap = availabilityDetails
			.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
	log.info("Timer Details from db : " + timerDetails);

	timerDetails.forEach(detail -> {
		// Create a Slot availability object for comparison
		CommunityHallSlotAvailabilityDetail availabilityDetail = CommunityHallSlotAvailabilityDetail.builder()
				.communityHallCode(detail.getCommunityHallcode()).hallCode(detail.getHallcode())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(detail.getBookingDate(), CommunityHallBookingConstants.DATE_FORMAT))
				.tenantId(detail.getTenantId()).build();

		// Check if the timerDetails set contains this booking and if it's created by
		// the current user
		// Update the slot status based on the comparison
		if (availabilityDetails.contains(availabilityDetail)) {
			log.info("Booking created by user id {} and booking id {} ", criteria.getBookingId(),
					info.getUserInfo().getUuid());
			CommunityHallSlotAvailabilityDetail slotAvailabilityDetail = slotDetailsMap.get(availabilityDetail);
			log.info("Slot Availability detail ::: " + slotAvailabilityDetail.toString());
			boolean isCreatedByCurrentUser = detail.getCreatedBy().equals(info.getUserInfo().getUuid());
			boolean existingBookingIdCheck = detail.getBookingId().equals(criteria.getBookingId());

			if (isCreatedByCurrentUser && existingBookingIdCheck) {
				log.info("inside booking created by me with same booking id ");
				slotAvailabilityDetail.setSlotStaus(BookingStatusEnum.AVAILABLE.toString());
			} else {
				slotAvailabilityDetail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
			}
		}

	});

	return availabilityDetails;
}



	/**
	 * 
	 * @param criteria
	 * @param availabiltityDetails
	 * @return
	 */
	private List<CommunityHallSlotAvailabilityDetail> convertToCommunityHallAvailabilityResponse(
			CommunityHallSlotSearchCriteria criteria, List<CommunityHallSlotAvailabilityDetail> availabiltityDetails) {

		List<CommunityHallSlotAvailabilityDetail> availabiltityDetailsList = new ArrayList<CommunityHallSlotAvailabilityDetail>();
		LocalDate startDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());

		LocalDate endDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());

		List<LocalDate> totalDates = new ArrayList<>();
		//Calculating list of dates for booking
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}
		
		//Move the no of days to application properties File
		if(totalDates.size() > 3) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_BOOKING_DATE_RANGE,
					"Booking is not allowed for this no of days.");
		}

		totalDates.stream().forEach(date -> {
			List<String> hallCodes = new ArrayList<>();
			if (StringUtils.isNotBlank(criteria.getHallCode())) {
				hallCodes.add(criteria.getHallCode());
			} else {
				hallCodes.addAll(criteria.getHallCodes());
			}
			hallCodes.stream().forEach(data -> {
				availabiltityDetailsList.add(createCommunityHallSlotAvailabiltityDetail(criteria, date, data));
			});
		});

		//Setting hall status to booked if it is already booked by checking in the database entry
		availabiltityDetailsList.stream().forEach(detail -> {
			if (availabiltityDetails.contains(detail)) {
				detail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
			}
		});
		
		return availabiltityDetailsList;
	}

	private CommunityHallSlotAvailabilityDetail createCommunityHallSlotAvailabiltityDetail(
			CommunityHallSlotSearchCriteria criteria, LocalDate date, String hallCode) {
		CommunityHallSlotAvailabilityDetail availabiltityDetail = CommunityHallSlotAvailabilityDetail.builder()
				.communityHallCode(criteria.getCommunityHallCode()).hallCode(hallCode)
			//Setting slot status available for every hall and hall code
				.slotStaus(BookingStatusEnum.AVAILABLE.toString()).tenantId(criteria.getTenantId())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(date, "dd-MM-yyyy")).build();
		return availabiltityDetail;
	}


}
