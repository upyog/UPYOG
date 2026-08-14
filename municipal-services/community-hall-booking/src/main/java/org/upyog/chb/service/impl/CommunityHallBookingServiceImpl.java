package org.upyog.chb.service.impl;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
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
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.VenueBookingSearchCriteria;
import org.upyog.chb.web.models.VenueSlotAvailabilityDetail;
import org.upyog.chb.web.models.VenueSlotAvailabilityResponse;
import org.upyog.chb.web.models.VenueSlotSearchCriteria;
import org.upyog.chb.web.models.workflow.State;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * This class implements the CommunityHallBookingService interface and provides
 * the business logic for the Community Hall Booking module.
 */
@Service
@Slf4j
public class CommunityHallBookingServiceImpl implements CommunityHallBookingService {

	private final CommunityHallBookingRepository bookingRepository;
	private final CommunityHallBookingValidator hallBookingValidator;
	private final WorkflowService workflowService;
	private final EnrichmentService enrichmentService;
	private final DemandService demandService;
	private final MdmsUtil mdmsUtil;
	private final CHBEncryptionService encryptionService;
	private final BookingTimerService bookingTimerService;

	public CommunityHallBookingServiceImpl(CommunityHallBookingRepository bookingRepository,
			CommunityHallBookingValidator hallBookingValidator, WorkflowService workflowService,
			EnrichmentService enrichmentService, DemandService demandService, MdmsUtil mdmsUtil,
			CHBEncryptionService encryptionService, BookingTimerService bookingTimerService) {
		this.bookingRepository = bookingRepository;
		this.hallBookingValidator = hallBookingValidator;
		this.workflowService = workflowService;
		this.enrichmentService = enrichmentService;
		this.demandService = demandService;
		this.mdmsUtil = mdmsUtil;
		this.encryptionService = encryptionService;
		this.bookingTimerService = bookingTimerService;
	}
	
	@Override
	public VenueBookingDetail createBooking(@Valid VenueBookingRequest venueBookingRequest) {
		log.info("Create community hall booking for user : "
				+ venueBookingRequest.getRequestInfo().getUserInfo().getUuid());
		if (venueBookingRequest.getVenueBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_TENANT,
					"Please provide valid tenant id for booking creation");
		}

		Object mdmsData = mdmsUtil.mDMSCall(venueBookingRequest.getRequestInfo(), venueBookingRequest.getVenueBookingApplication().getTenantId());

		Object venueTypeMasterData = mdmsUtil.mDMSCall(venueBookingRequest.getRequestInfo(), venueBookingRequest.getVenueBookingApplication().getTenantId());
		hallBookingValidator.validateCreate(venueBookingRequest, mdmsData,venueTypeMasterData);
		enrichmentService.enrichCreateBookingRequest(venueBookingRequest);
		
		encryptionService.encryptObject(venueBookingRequest);

		demandService.createDemand(venueBookingRequest, true);

		bookingRepository.saveCommunityHallBooking(venueBookingRequest);
		bookingRepository.updateTimerBookingId(
				venueBookingRequest.getVenueBookingApplication().getBookingId(),
				venueBookingRequest.getVenueBookingApplication().getBookingNo(),
				venueBookingRequest.getVenueBookingApplication().getDraftId());
		venueBookingRequest.getVenueBookingApplication().setTimerValue(bookingTimerService
				.getRemainingTimerValue(venueBookingRequest.getVenueBookingApplication().getBookingId()));

		return venueBookingRequest.getVenueBookingApplication();
	}
	
	@Override
	public VenueBookingDetail createInitBooking(
			@Valid VenueBookingRequest venueBookingRequest) {
		log.info("Create community hall temp booking for user : "
				+ venueBookingRequest.getRequestInfo().getUserInfo().getUuid());
		bookingRepository.saveCommunityHallBookingInit(venueBookingRequest);
		return null;
	}

	@Override
	public List<VenueBookingDetail> getBookingDetails(VenueBookingSearchCriteria bookingSearchCriteria,
			RequestInfo info) {
		hallBookingValidator.validateSearch(info, bookingSearchCriteria);
		bookingSearchCriteria  = addCreatedByMeToCriteria(bookingSearchCriteria, info);
		
		log.info("loading data based on criteria" + bookingSearchCriteria);
		
		if (bookingSearchCriteria.getMobileNumber() != null
				&& bookingSearchCriteria.getMobileNumber().trim().length() > 9) {

			ApplicantDetail applicantDetail = ApplicantDetail.builder()
					.applicantMobileNo(bookingSearchCriteria.getMobileNumber()).build();
			VenueBookingDetail communityHallBookingDetail = VenueBookingDetail.builder()
					.applicantDetail(applicantDetail).build();
			VenueBookingRequest bookingRequest = VenueBookingRequest.builder()
					.venueBookingApplication(communityHallBookingDetail).requestInfo(info).build();

			communityHallBookingDetail = encryptionService.encryptObject(bookingRequest);

			bookingSearchCriteria
					.setMobileNumber(communityHallBookingDetail.getApplicantDetail().getApplicantMobileNo());

			log.info("loading data based on criteria after encrypting mobile no : " + bookingSearchCriteria);

		}
		
		List<VenueBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if(CollectionUtils.isEmpty(bookingDetails)) {
			return bookingDetails;
		}
		return encryptionService.decryptObject(bookingDetails, info);
	}
	
	
	@Override
	public Integer getBookingCount(@Valid VenueBookingSearchCriteria criteria,
			@NonNull RequestInfo requestInfo) {
		criteria.setCountCall(true);
		
		criteria  = addCreatedByMeToCriteria(criteria, requestInfo);
		return bookingRepository.getBookingCount(criteria);
	}

	
	private VenueBookingSearchCriteria addCreatedByMeToCriteria(VenueBookingSearchCriteria criteria, RequestInfo requestInfo) {
		if(requestInfo.getUserInfo() == null) {
			log.info("Request info is null returning criteira");
			return criteria;
		}
		List<String> roles = new ArrayList<>();
		for (Role role : requestInfo.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		log.info("user roles for searching : " + roles);
		List<String> uuids = new ArrayList<>();
		if (roles.contains(CommunityHallBookingConstants.CITIZEN) && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
			log.debug("loading data of created and by me" + uuids.toString());
		}
		return criteria;
	}

	@Override
	public VenueBookingDetail updateBooking(VenueBookingRequest venueBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status) {
		String bookingNo = venueBookingRequest.getVenueBookingApplication().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		VenueBookingSearchCriteria bookingSearchCriteria = VenueBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<VenueBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if (bookingDetails.isEmpty()) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		
		hallBookingValidator.validateUpdate(venueBookingRequest.getVenueBookingApplication(), bookingDetails.get(0));

		convertBookingRequest(venueBookingRequest, bookingDetails.get(0));

		
		if (paymentDetail != null) {
			venueBookingRequest.getVenueBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			venueBookingRequest.getVenueBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}
		if (venueBookingRequest.getVenueBookingApplication().getWorkflow()!=null) {
			State state = workflowService.updateWorkflow(venueBookingRequest);
			status = BookingStatusEnum.valueOf(state.getApplicationStatus());
		}
		enrichmentService.enrichUpdateBookingRequest(venueBookingRequest, status);
		bookingRepository.updateBooking(venueBookingRequest);
		log.info("fetched booking detail and updated status "
				+ venueBookingRequest.getVenueBookingApplication().getBookingStatus());
		return venueBookingRequest.getVenueBookingApplication();
	}
	
	@Transactional
	@Override
	public void updateBookingSynchronously(VenueBookingRequest venueBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status, boolean deleteBookingTimer) {
		String bookingNo = venueBookingRequest.getVenueBookingApplication().getBookingNo();
		log.info("Updating booking synchronously for booking no : " + bookingNo);
		if (bookingNo == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		VenueBookingSearchCriteria bookingSearchCriteria = VenueBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<VenueBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if (bookingDetails.isEmpty()) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}
		VenueBookingDetail bookingDetail = bookingDetails.get(0);
		venueBookingRequest.setVenueBookingApplication(bookingDetail);
		try {
			bookingRepository.updateBookingSynchronously(bookingDetail.getBookingId(), venueBookingRequest.getRequestInfo().getUserInfo().getUuid(), paymentDetail, status.toString());
		} catch (Exception e) {
			log.error("Error while updating booking synchronously for booking no : " + bookingNo, e);
			throw new CustomException("BOOKING_UPDATE_FAILED",
					"Failed to update booking status for : " + bookingNo);
		}
		if(deleteBookingTimer) {
			log.info("Deleting booking timer with booking id  {}", venueBookingRequest.getVenueBookingApplication().getBookingId());
			bookingTimerService.deleteBookingTimer(venueBookingRequest.getVenueBookingApplication().getBookingId(), false);
		}
	}

	private void convertBookingRequest(VenueBookingRequest venueBookingRequest,
			VenueBookingDetail bookingDetailDB) {
		VenueBookingDetail bookingDetailRequest = venueBookingRequest.getVenueBookingApplication();
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
		venueBookingRequest.setVenueBookingApplication(bookingDetailDB);
		
	}

	@Override
	public VenueSlotAvailabilityResponse getCommunityHallSlotAvailability(
			VenueSlotSearchCriteria criteria, RequestInfo info) {
		if (criteria.getVenueCode() == null && CollectionUtils.isEmpty(criteria.getUnitCodes())) {
			throw new CustomException("INVALID_HALL_CODE", "Invalid hall code provided for slot search");
		}
		log.info("criteria : {}", criteria);
		List<VenueSlotAvailabilityDetail> availabiltityDetails = bookingRepository
				.getCommunityHallSlotAvailability(criteria);
		log.info("Availabiltity details fetched from DB :" + availabiltityDetails);

		List<VenueSlotAvailabilityDetail> availabiltityDetailsList = convertToCommunityHallAvailabilityResponse(
				criteria, availabiltityDetails);

		Long timerValue = -1l;
		availabiltityDetailsList = checkTimerTableForAvailaibility(info, criteria, availabiltityDetailsList);
		boolean bookingAllowed = availabiltityDetailsList.stream()
				.anyMatch(detail -> BookingStatusEnum.BOOKED.toString().equals(detail.getSlotStaus()));

		if (!bookingAllowed && Boolean.TRUE.equals(criteria.getIsTimerRequired())) {
			timerValue = bookingTimerService.managePaymentTimer(criteria, info, availabiltityDetailsList);
		}

		VenueSlotAvailabilityResponse hallSlotAvailabilityResponse = VenueSlotAvailabilityResponse
				.builder().hallSlotAvailabiltityDetails(availabiltityDetailsList).timerValue(timerValue)
				.draftId(criteria.getDraftId()).build();

		log.info("Availability details response after updating status :" + hallSlotAvailabilityResponse);
		return hallSlotAvailabilityResponse;
	}



private List<VenueSlotAvailabilityDetail> checkTimerTableForAvailaibility(
            RequestInfo info, VenueSlotSearchCriteria criteria,
		List<VenueSlotAvailabilityDetail> availabilityDetails) {

	List<BookingPaymentTimerDetails> timerDetails = bookingTimerService.getBookingFromTimerTable(info, criteria);

	if (timerDetails == null || timerDetails.isEmpty()) {
		log.info("Timer details are null or empty, returning availability details as is.");
		return availabilityDetails;
	}

	Map<VenueSlotAvailabilityDetail, VenueSlotAvailabilityDetail> slotDetailsMap = availabilityDetails
			.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
	log.info("Timer Details from db : " + timerDetails);

		timerDetails.forEach(detail -> applyTimerDetailToAvailability(info, criteria, availabilityDetails, slotDetailsMap,
				detail));

	return availabilityDetails;
}

	private void applyTimerDetailToAvailability(RequestInfo info, VenueSlotSearchCriteria criteria,
			List<VenueSlotAvailabilityDetail> availabilityDetails,
			Map<VenueSlotAvailabilityDetail, VenueSlotAvailabilityDetail> slotDetailsMap,
			BookingPaymentTimerDetails detail) {
		VenueSlotAvailabilityDetail availabilityDetail = VenueSlotAvailabilityDetail.builder()
				.venueCode(detail.getVenueCode()).code(detail.getUnitCode())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(detail.getBookingDate(),
						CommunityHallBookingConstants.DATE_FORMAT))
				.tenantId(detail.getTenantId()).build();

		if (availabilityDetails.contains(availabilityDetail)) {
			log.info("Booking created by user id {} and booking id {} ", criteria.getBookingId(),
					info.getUserInfo().getUuid());
			VenueSlotAvailabilityDetail slotAvailabilityDetail = slotDetailsMap.get(availabilityDetail);
			log.info("Slot Availability detail ::: " + slotAvailabilityDetail.toString());
			boolean isCreatedByCurrentUser = detail.getCreatedBy().equals(info.getUserInfo().getUuid());
			boolean existingBookingIdCheck = detail.getBookingId().equals(getTimerBookingReference(criteria));

			String fromTime = criteria.getFromTime() != null ? criteria.getFromTime() : null;
			String toTime = criteria.getToTime() != null ? criteria.getToTime() :  null;
			slotAvailabilityDetail.setFromTime(fromTime);
			slotAvailabilityDetail.setToTime(toTime);
			if (isCreatedByCurrentUser && existingBookingIdCheck) {
				log.info("inside booking created by me with same booking id ");
				slotAvailabilityDetail.setSlotStaus(BookingStatusEnum.AVAILABLE.toString());
			} else {
				slotAvailabilityDetail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
			}
		}
	}

	private String getTimerBookingReference(VenueSlotSearchCriteria criteria) {
		return StringUtils.isNotBlank(criteria.getBookingId()) ? criteria.getBookingId() : criteria.getDraftId();
	}



	private List<VenueSlotAvailabilityDetail> convertToCommunityHallAvailabilityResponse(
			VenueSlotSearchCriteria criteria, List<VenueSlotAvailabilityDetail> availabiltityDetails) {

		List<VenueSlotAvailabilityDetail> availabiltityDetailsList = new ArrayList<>();
		LocalDate startDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());

		LocalDate endDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());

		List<LocalDate> totalDates = new ArrayList<>();
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}
		
		if(totalDates.size() > 3) {
			throw new CustomException(CommunityHallBookingConstants.INVALID_BOOKING_DATE_RANGE,
					"Booking is not allowed for this no of days.");
		}

		Map<String, VenueSlotAvailabilityDetail> dbAvailabilityDetails = availabiltityDetails.stream()
				.collect(Collectors.toMap(detail -> buildSlotKey(detail.getTenantId(), detail.getVenueCode(),
						detail.getCode(), detail.getBookingDate()), Function.identity(), (first, second) -> first));
		for (LocalDate date : totalDates) {
			List<String> hallCodes = new ArrayList<>();
			if (StringUtils.isNotBlank(criteria.getUnitCode())) {
				hallCodes.add(criteria.getUnitCode());
			} else {
				hallCodes.addAll(criteria.getUnitCodes());
			}
			for (String data : hallCodes) {
				String bookingDate = CommunityHallBookingUtil.parseLocalDateToString(date,
					CommunityHallBookingConstants.DATE_FORMAT);
				String key = buildSlotKey(criteria.getTenantId(), criteria.getVenueCode(), data, bookingDate);
				VenueSlotAvailabilityDetail existingDetail = dbAvailabilityDetails.get(key);
				if (existingDetail != null) {
					availabiltityDetailsList.add(existingDetail);
				} else {
					availabiltityDetailsList.add(createCommunityHallSlotAvailabiltityDetail(criteria, date, data));
				}
			}
		}

		for (VenueSlotAvailabilityDetail detail : availabiltityDetailsList) {
			int index = availabiltityDetails.indexOf(detail);
			if (index >= 0) {
				VenueSlotAvailabilityDetail dbDetail = availabiltityDetails.get(index);
				detail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
				detail.setFromTime(dbDetail.getFromTime());
				detail.setToTime(dbDetail.getToTime());
			}
		}
		
		return availabiltityDetailsList;
	}

	private String buildSlotKey(String tenantId, String venueCode, String code, String bookingDate) {
		return tenantId + "|" + venueCode + "|" + code + "|" + bookingDate;
	}

	private VenueSlotAvailabilityDetail createCommunityHallSlotAvailabiltityDetail(
			VenueSlotSearchCriteria criteria, LocalDate date, String hallCode) {
		return VenueSlotAvailabilityDetail.builder()
				.venueCode(criteria.getVenueCode()).code(hallCode)
				.slotStaus(BookingStatusEnum.AVAILABLE.toString()).tenantId(criteria.getTenantId())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(date, "dd-MM-yyyy")).build();
	}


}
