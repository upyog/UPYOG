package org.upyog.adv.service.impl;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.service.ADVEncryptionService;
import org.upyog.adv.service.BookingService;
import org.upyog.adv.service.DemandService;
import org.upyog.adv.service.EnrichmentService;
import org.upyog.adv.service.PaymentTimerService;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.validator.BookingValidator;
import org.upyog.adv.web.models.AdvertisementDraftDetail;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityResponse;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.ApplicantDetail;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

import digit.models.coremodels.PaymentDetail;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of booking operations for advertisement slot reservations.
 *
 * <p>This service contains timer-aware slot availability handling, draft cleanup,
 * and booking creation logic that reconciles draft timer holds with final
 * booking identifiers.</p>
 *
 * <p>Timer logic is used to keep slot holds active during payment and to
 * reflect held or booked slots when checking availability.</p>
 */
@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

	private final MdmsUtil mdmsUtil;
	private final BookingRepository bookingRepository;
	private final BookingValidator bookingValidator;
	private final EnrichmentService enrichmentService;
	private final DemandService demandService;
	private final PaymentTimerService paymentTimerService;
	private final ADVEncryptionService encryptionService;

	public BookingServiceImpl(MdmsUtil mdmsUtil, @Lazy BookingRepository bookingRepository,
			BookingValidator bookingValidator, EnrichmentService enrichmentService, DemandService demandService,
			PaymentTimerService paymentTimerService, ADVEncryptionService encryptionService) {
		this.mdmsUtil = mdmsUtil;
		this.bookingRepository = bookingRepository;
		this.bookingValidator = bookingValidator;
		this.enrichmentService = enrichmentService;
		this.demandService = demandService;
		this.paymentTimerService = paymentTimerService;
		this.encryptionService = encryptionService;
	}

	/**
	 * Creates a new advertisement booking and reconciles draft timer holds.
	 *
	 * <p>After the booking is persisted, this method updates any existing timer
	 * entries that were held against a draft id so they reference the final
	 * booking identifier instead.</p>
	 *
	 * @param bookingRequest booking request containing applicant and slot data
	 * @return created booking details with final identifiers
	 */
	@Override
	public BookingDetail createBooking(@Valid BookingRequest bookingRequest) {
		log.info("Create advertisement booking for user : " + bookingRequest.getRequestInfo().getUserInfo().getId());
		String uuid = bookingRequest.getRequestInfo().getUserInfo().getUuid();
		String tenantId = bookingRequest.getBookingApplication().getTenantId().split("\\.")[0];
		if (bookingRequest.getBookingApplication().getTenantId().split("\\.").length == 1) {
			throw new CustomException(BookingConstants.INVALID_TENANT,
					"Please provide valid tenant id for booking creation");
		}

		Object mdmsData = mdmsUtil.mDMSCall(bookingRequest.getRequestInfo(), tenantId);

		// 1. Validate request master data to confirm it has only valid data in records
		bookingValidator.validateCreate(bookingRequest, mdmsData);

		// 2. Add fields that has custom logic like booking no, ids using UUID
		enrichmentService.enrichCreateBookingRequest(bookingRequest);

		// ENcrypt PII data of applicant
		encryptionService.encryptObject(bookingRequest);

	    demandService.createDemand(bookingRequest, mdmsData, true);

		// 4.Persist the request using persister service
		bookingRepository.saveBooking(bookingRequest);

		String draftId = bookingRequest.getBookingApplication().getDraftId();

		String bookingId = bookingRequest.getBookingApplication().getBookingId();

		BookingDetail bookingDetails = encryptionService.decryptObject(bookingRequest.getBookingApplication(),
				bookingRequest.getRequestInfo());

		List<AdvertisementDraftDetail> draftData = bookingRepository.getDraftData(uuid);
		
		String draftIdFromDraft = "";

		if (draftData != null && !draftData.isEmpty()) {
		     draftIdFromDraft = draftData.get(0).getDraftId(); 
		}

		/*
		 * Slot-search stores the draft id in the timer table until the booking is
		 * created. After enrichment generates the final booking id and booking number,
		 * the timer rows are moved from draft id to the real booking reference.
		 */
		bookingRepository.updateTimerBookingId(bookingId, bookingDetails.getBookingNo(), draftIdFromDraft);

		if (StringUtils.isNotBlank(draftId)) {
			log.info("Deleting draft entry for draft id: " + draftId);
			bookingRepository.deleteDraftApplication(draftId);
		}

		return bookingDetails;
	}

	@Override
	public List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria advertisementSearchCriteria,
			RequestInfo info) {
		log.info("loading data based on criteria" + advertisementSearchCriteria);

		if (advertisementSearchCriteria.getMobileNumber() != null
				|| advertisementSearchCriteria.getApplicantName() != null) {

			ApplicantDetail applicantDetail = ApplicantDetail.builder()
					.applicantMobileNo(advertisementSearchCriteria.getMobileNumber())
					.applicantName(advertisementSearchCriteria.getApplicantName()).build();
			BookingDetail bookingDetail = BookingDetail.builder().applicantDetail(applicantDetail).build();
			BookingRequest bookingRequest = BookingRequest.builder().bookingApplication(bookingDetail).requestInfo(info)
					.build();

			bookingDetail = encryptionService.encryptObject(bookingRequest);

			advertisementSearchCriteria.setMobileNumber(bookingDetail.getApplicantDetail().getApplicantMobileNo());
			advertisementSearchCriteria.setApplicantName(bookingDetail.getApplicantDetail().getApplicantName());

			log.info("loading data based on criteria after encrypting mobile no : " + advertisementSearchCriteria);

		}

		List<BookingDetail> bookingDetails = bookingRepository.getBookingDetails(advertisementSearchCriteria);

		if (CollectionUtils.isEmpty(bookingDetails)) {
			return bookingDetails;
		}
		bookingDetails = encryptionService.decryptObject(bookingDetails, info);

		return bookingDetails;
	}

	@Override
	public Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria, @NonNull RequestInfo requestInfo) {
		criteria.setCountCall(true);
		return bookingRepository.getBookingCount(criteria);
	}

	/**
	 * Checks availability for a single advertisement slot criteria and adjusts
	 * the response based on currently active timer holds.
	 *
	 * <p>Timer-held slots may remain available for the current user but are
	 * marked booked for other users.</p>
	 *
	 * @param criteria slot search criteria
	 * @param requestInfo request metadata and authenticated user details
	 * @return availability details after applying timer status updates
	 */
	@Override
	public List<AdvertisementSlotAvailabilityDetail> checkAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo) {

		List<AdvertisementSlotAvailabilityDetail> availabilityDetails = bookingRepository
				.getAdvertisementSlotAvailability(criteria);
		log.info("Fetched availability details: " + availabilityDetails);

		List<AdvertisementSlotAvailabilityDetail> availabilityDetailsResponse = convertToAdvertisementAvailabilityResponse(
				criteria, availabilityDetails);

		updateSlotAvailaibilityStatusFromTimer(availabilityDetailsResponse, criteria, requestInfo);
		log.info("Updated availability details: " + availabilityDetailsResponse);

		return availabilityDetailsResponse;
	}
	
	/**
	 * Evaluates availability for multiple advertisement slot search criteria and
	 * applies timer holds when necessary.
	 *
	 * <p>If any of the requested slot criteria require a timer, existing draft
	 * timer holds are cleaned up and a new booking timer is inserted for the
	 * requested availability details.</p>
	 *
	 * @param criteriaList list of slot search criteria
	 * @param requestInfo request metadata and authenticated user details
	 * @return merged availability details with timer-based booking status applied
	 */
	@Override
	public AdvertisementSlotAvailabilityResponse getAdvertisementSlotAvailability(
	        List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo) {

	    List<AdvertisementSlotAvailabilityDetail> allAvailabilityDetails = new ArrayList<>();
	        
	    for (AdvertisementSlotSearchCriteria criteria : criteriaList) {
	        List<AdvertisementSlotAvailabilityDetail> availabilityDetails = checkAdvertisementSlotAvailability(criteria, requestInfo);
	        allAvailabilityDetails.addAll(availabilityDetails);
	       
	    }

	    boolean isTimerRequiredForAnyCriteria = criteriaList.stream()
	            .anyMatch(criteria -> criteria.getIsTimerRequired());
	    
	    boolean slotBookedFlag = setSlotBookedFlag(allAvailabilityDetails);
	    log.info("Slot booked flag for criteria : " + slotBookedFlag);
	    if (isTimerRequiredForAnyCriteria) {
	        paymentTimerService.deleteDataFromTimerAndDraft(requestInfo.getUserInfo().getUuid(), criteriaList.get(0).getDraftId(), criteriaList.get(0).getBookingId());
	    }
	    
	    long timerValue = 0;
	    if (isTimerRequiredForAnyCriteria && !slotBookedFlag) {
	        // Insert the timer for all criteria at once
	        timerValue = paymentTimerService.insertBookingIdForTimer(criteriaList, requestInfo);
	        log.info("Inserted booking ID for timer for all criteria. Timer value: " + timerValue);
	    }

	    String draftId = getDraftId(allAvailabilityDetails, requestInfo);
	   
	    return AdvertisementSlotAvailabilityResponse.builder()
	            .advertisementSlotAvailabiltityDetails(allAvailabilityDetails)
	            .slotBooked(slotBookedFlag)
	            .draftId(draftId)
	            .timerValue(timerValue)
	            .build();
	}

	/**
	 * Determines whether any slot in the availability response is already booked.
	 *
	 * @param details slot availability details to inspect
	 * @return {@code true} if any slot is booked; otherwise {@code false}
	 */
	@Override
	public boolean setSlotBookedFlag(List<AdvertisementSlotAvailabilityDetail> details) {
	    // Check if any slot is booked and return true if so
	    return details.stream()
	            .anyMatch(slot -> BookingStatusEnum.BOOKED.toString().equals(slot.getSlotStaus()));
	}


	private List<AdvertisementSlotAvailabilityDetail> convertToAdvertisementAvailabilityResponse(
			AdvertisementSlotSearchCriteria criteria, List<AdvertisementSlotAvailabilityDetail> availabiltityDetails) {

		List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse = new ArrayList<>();
		LocalDate startDate = BookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());
		LocalDate endDate = BookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());

		List<LocalDate> totalDates = new ArrayList<>();

		// Calculating list of dates for booking
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}

		// Enforcing the maximum booking days constraint
		if (totalDates.size() > 90) {
			throw new CustomException(BookingConstants.INVALID_BOOKING_DATE_RANGE,
					"Booking is not allowed for this number of days.");
		}

		// Create a slot availability detail for each date
		totalDates.forEach(date -> availabiltityDetailsResponse
				.add(createAdvertisementSlotAvailabiltityDetail(criteria, date)));

	
		// Set advertisement status to 'BOOKED' if already booked
		availabiltityDetailsResponse.forEach(detail -> {
			if (availabiltityDetails.contains(detail)) {
				detail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
				detail.setBookingId(criteria.getBookingId());
			}

		});
		
		log.info("Availability details response after updating status: " + availabiltityDetailsResponse);

		return availabiltityDetailsResponse;
	}



	/**
	 * Updates availability details based on timer-held slot entries from the database.
	 *
	 * <p>If a timer entry belongs to the current user and matches the active draft or
	 * booking id, the slot remains available. Otherwise, it is marked as booked.</p>
	 *
	 * @param availabilityDetailsResponse current availability response list
	 * @param criteria slot search criteria used to query timer entries
	 * @param requestInfo request metadata and authenticated user details
	 * @return availability details with timer status updates applied
	 */
	public List<AdvertisementSlotAvailabilityDetail> updateSlotAvailaibilityStatusFromTimer(
			List<AdvertisementSlotAvailabilityDetail> availabilityDetailsResponse,
			AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo) { 

		List<AdvertisementSlotAvailabilityDetail> bookedSlotsFromTimer = bookingRepository.getBookedSlots(criteria,
				requestInfo);
		if (bookedSlotsFromTimer == null || bookedSlotsFromTimer.isEmpty()) {
			log.info("Timer details are null or empty, returning availability details as is.");
		}

		Map<AdvertisementSlotAvailabilityDetail, AdvertisementSlotAvailabilityDetail> slotDetailsMap = availabilityDetailsResponse
				.stream().collect(Collectors.toMap(Function.identity(), Function.identity()));
		log.info("Timer Details from db : " + bookedSlotsFromTimer);

		bookedSlotsFromTimer.forEach(detail -> {
			AdvertisementSlotAvailabilityDetail availabilityDetail = AdvertisementSlotAvailabilityDetail.builder()
					.addType(detail.getAddType()).location(detail.getLocation()).faceArea(detail.getFaceArea())
					.nightLight(detail.getNightLight()).bookingDate(detail.getBookingDate()).build();

			// Check if the timerDetails set contains this booking and if it's created by
			// the current user
			// Update the slot status based on the comparison
			if (availabilityDetailsResponse.contains(availabilityDetail)) {
				
				AdvertisementSlotAvailabilityDetail slotAvailabilityDetail = slotDetailsMap.get(availabilityDetail);

				boolean isCreatedByCurrentUser = detail.getUuid().equals(requestInfo.getUserInfo().getUuid());
				boolean existingBookingId =
				detail.getBookingId().equals(criteria.getBookingId());
				
				boolean existingDraftId = false;
				 String draftId = getDraftId(availabilityDetailsResponse, requestInfo);
				 if(!StringUtils.isBlank(criteria.getDraftId())) {
					 existingDraftId = draftId.equals(criteria.getDraftId());
				 }
				if (isCreatedByCurrentUser && (existingBookingId || existingDraftId)) {
					log.info("inside booking created by me with same booking id ");
					slotAvailabilityDetail.setSlotStaus(BookingStatusEnum.AVAILABLE.toString());
				} else {
					slotAvailabilityDetail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
				}
			}

		});

		return availabilityDetailsResponse;

	}
	
	/**
	 * Resolves the current draft id for the authenticated user.
	 *
	 * @param availabiltityDetailsResponse list of availability details (unused for lookup)
	 * @param requestInfo request metadata and authenticated user details
	 * @return the current draft id if present, otherwise {@code null}
	 */
	@Override
	public String getDraftId(List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse,
            RequestInfo requestInfo) {
		List<AdvertisementDraftDetail> draftData = bookingRepository.getDraftData(requestInfo.getUserInfo().getUuid());

		if (draftData != null && !draftData.isEmpty()) {
		    String draftId = draftData.get(0).getDraftId(); 
		    return (draftId != null && !draftId.isEmpty()) ? draftId : null;
		}
		return null;
	}


	private AdvertisementSlotAvailabilityDetail createAdvertisementSlotAvailabiltityDetail(
			AdvertisementSlotSearchCriteria criteria, LocalDate date) {
		return AdvertisementSlotAvailabilityDetail.builder()
				.addType(criteria.getAddType()).faceArea(criteria.getFaceArea()).location(criteria.getLocation())
				.nightLight(criteria.getNightLight()).slotStaus(BookingStatusEnum.AVAILABLE.toString())
				.tenantId(criteria.getTenantId()).bookingDate(BookingUtil.parseLocalDateToString(date, "yyyy-MM-dd"))
				.build();
	}

	// This method updates booking from the booking number, searches the booking num
	// and get its details, if payment detail is not null the it sets the receipt
	// number and payment date
	@Override
	public BookingDetail updateBooking(BookingRequest advertisementBookingRequest, PaymentDetail paymentDetail,
			BookingStatusEnum status) {
		String bookingNo = advertisementBookingRequest.getBookingApplication().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			return null;
		}
		AdvertisementSearchCriteria advertisementSearchCriteria = AdvertisementSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<BookingDetail> bookingDetails = bookingRepository.getBookingDetails(advertisementSearchCriteria);
		if (bookingDetails.isEmpty()) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		convertBookingRequest(advertisementBookingRequest, bookingDetails.get(0));

		enrichmentService.enrichUpdateBookingRequest(advertisementBookingRequest, status);

		// Update payment date and receipt no on successful payment when payment detail
		// object is received
		if (paymentDetail != null) {
			advertisementBookingRequest.getBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			advertisementBookingRequest.getBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}
		bookingRepository.updateBooking(advertisementBookingRequest);
		log.info("fetched booking detail and updated status "
				+ advertisementBookingRequest.getBookingApplication().getBookingStatus());
		return advertisementBookingRequest.getBookingApplication();
	}

	@Transactional
	public BookingDetail updateBookingSynchronously(BookingRequest advertisementBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status) {
		String bookingNo = advertisementBookingRequest.getBookingApplication().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			return null;
		}
		AdvertisementSearchCriteria advertisementSearchCriteria = AdvertisementSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<BookingDetail> bookingDetails = bookingRepository.getBookingDetails(advertisementSearchCriteria);
		if (bookingDetails.isEmpty()) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		convertBookingRequest(advertisementBookingRequest, bookingDetails.get(0));

		enrichmentService.enrichUpdateBookingRequest(advertisementBookingRequest, status);

		// Update payment date and receipt no on successful payment when payment detail
		// object is received
		if (paymentDetail != null) {
			advertisementBookingRequest.getBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			advertisementBookingRequest.getBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}

		bookingRepository.updateBookingSynchronously(advertisementBookingRequest);
		log.info("fetched booking detail and updated status "
				+ advertisementBookingRequest.getBookingApplication().getBookingStatus());
		return advertisementBookingRequest.getBookingApplication();
	}

	// This sets the paymennt receipt file store id and permission letter file store
	// id
	private void convertBookingRequest(BookingRequest advertisementbookingRequest, BookingDetail bookingDetailDB) {
		BookingDetail bookingDetailRequest = advertisementbookingRequest.getBookingApplication();
		if (bookingDetailDB.getPermissionLetterFilestoreId() == null
				&& bookingDetailRequest.getPermissionLetterFilestoreId() != null) {
			bookingDetailDB.setPermissionLetterFilestoreId(bookingDetailRequest.getPermissionLetterFilestoreId());
		}

		if (bookingDetailDB.getPaymentReceiptFilestoreId() == null
				&& bookingDetailRequest.getPaymentReceiptFilestoreId() != null) {
			bookingDetailDB.setPaymentReceiptFilestoreId(bookingDetailRequest.getPaymentReceiptFilestoreId());
		}
		advertisementbookingRequest.setBookingApplication(bookingDetailDB);
	}

	@Override
	public BookingDetail createAdvertisementDraftApplication(BookingRequest bookingRequest) {

		String draftId = bookingRequest.getBookingApplication().getDraftId();

		if (StringUtils.isNotBlank(draftId)) {

			// Update existing draft
			enrichmentService.enrichUpdateAdvertisementDraftApplicationRequest(bookingRequest);
			bookingRepository.updateDraftApplication(bookingRequest);
		}else {
		    enrichmentService.enrichCreateAdvertisementDraftApplicationRequest(bookingRequest);
		    
		    List<AdvertisementDraftDetail> draftData = bookingRepository
		            .getDraftData(bookingRequest.getRequestInfo().getUserInfo().getUuid());
		    
		   
		    if (draftData != null && !draftData.isEmpty()) {
		        String draftIdInDraft = draftData.get(0).getDraftId(); 
		        
		        if (draftIdInDraft == null) {
		            bookingRepository.saveDraftApplication(bookingRequest);
		        }
		    }
		

		}

		// Return the enriched booking application object
		return bookingRequest.getBookingApplication();
	}

	@Override
	public List<BookingDetail> getAdvertisementDraftApplicationDetails(@NonNull RequestInfo requestInfo,
			@Valid AdvertisementSearchCriteria criteria) {
		return bookingRepository.getAdvertisementDraftApplications(requestInfo, criteria);
	}

	/**
	 * Deletes a draft advertisement booking and removes any associated timer
	 * mirror entries from Redis.
	 *
	 * @param draftId draft id to delete
	 * @return confirmation message after draft discard
	 */
	public String deleteAdvertisementDraft(String draftId) {

		if (StringUtils.isNotBlank(draftId)) {
			log.info("Deleting draft entry for draft id: " + draftId);
			paymentTimerService.removeRedisMirrorForDraft(draftId);
			bookingRepository.deleteDraftApplication(draftId);
		}
		return BookingConstants.DRAFT_DISCARDED;
	}

}
