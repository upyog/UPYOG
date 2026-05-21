package org.upyog.adv.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.service.*;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.validator.BookingValidator;
import org.upyog.adv.web.models.*;
import org.upyog.adv.web.models.billing.PaymentDetail;
import org.upyog.adv.workflow.WorkflowIntegrator;
import org.upyog.adv.web.models.workflow.Workflow;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

	@Autowired
	private MdmsUtil mdmsUtil;

	@Autowired
	@Lazy
	private BookingRepository bookingRepository;
	@Autowired
	private BookingValidator bookingValidator;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private DemandService demandService;

	@Autowired
	UserService userService;

	@Autowired
	private org.upyog.adv.config.BookingConfiguration bookingConfiguration;

	@Autowired
	private PaymentTimerService paymentTimerService;

	@Autowired
	private ADVEncryptionService encryptionService;

	@Autowired(required = false)
	private WorkflowIntegrator workflowIntegrator;

	@Override
	public BookingDetail createBooking(@Valid BookingRequest bookingRequest) throws JsonProcessingException {
		log.info("Create advertisement booking for user : " + bookingRequest.getRequestInfo().getUserInfo().getId());
		String uuid = bookingRequest.getRequestInfo().getUserInfo().getUuid();
		String tenantId = bookingRequest.getBookingApplication().getTenantId();
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
		userService.createUser(bookingRequest.getRequestInfo(),bookingRequest.getBookingApplication());

		try {
			if (workflowIntegrator != null && bookingRequest.getBookingApplication().getWorkflow() != null
					&& StringUtils.isNotBlank(bookingRequest.getBookingApplication().getWorkflow().getAction())) {
				String nextStatus = workflowIntegrator.transition(bookingRequest.getRequestInfo(), bookingRequest.getBookingApplication(),
						bookingRequest.getBookingApplication().getWorkflow().getAction());
				if (StringUtils.isNotBlank(nextStatus)) {
					bookingRequest.getBookingApplication().setBookingStatus(nextStatus);
				}
			}
		} catch (Exception ex) {
			log.error("Workflow initiation failed for booking {}", bookingRequest.getBookingApplication().getBookingNo(), ex);
		}





		// 4.Persist the request using persister service
		bookingRepository.saveBooking(bookingRequest);

		String draftId = bookingRequest.getBookingApplication().getDraftId();
		// 5

		String bookingId = bookingRequest.getBookingApplication().getBookingId();

		BookingDetail bookingDetails = encryptionService.decryptObject(bookingRequest.getBookingApplication(),
				bookingRequest.getRequestInfo());

		List<AdvertisementDraftDetail> draftData = bookingRepository.getDraftData(uuid);

		String draftIdFromDraft = "";

		if (draftData != null && !draftData.isEmpty()) {
			draftIdFromDraft = draftData.get(0).getDraftId();
		}

		bookingRepository.updateTimerBookingId(bookingId, bookingDetails.getBookingNo(), draftIdFromDraft);

		if (StringUtils.isNotBlank(draftId)) {
			log.info("Deleting draft entry for draft id: " + draftId);
			bookingRepository.deleteDraftApplication(draftId);
		}



		// Initiate workflow if action provided


		return bookingDetails;
	}

	@Override
	public List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria advertisementSearchCriteria,
			RequestInfo info) {
		// BookingValidator.validateSearch(info, advertisementSearchCriteria);
		List<BookingDetail> bookingDetails = new ArrayList<BookingDetail>();
		// advertisementSearchCriteria =
		// addCreatedByMeToCriteria(advertisementSearchCriteria, info);

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

	bookingDetails = bookingRepository.getBookingDetails(advertisementSearchCriteria);

	for (BookingDetail bookingDetail : bookingDetails) {
		AdvertisementSearchCriteria criteria = new AdvertisementSearchCriteria();
		criteria.setTenantId(bookingDetail.getTenantId());

		String bookingId = bookingDetail.getBookingId();
		List<OwnerInfo> owners = bookingRepository.getOwnerByBookingId(bookingId);

		if (owners != null && !owners.isEmpty()) {
			List<String> ownerIds = owners.stream()
					.map(OwnerInfo::getUuid) // Make sure getUuid() returns the correct value
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			criteria.setOwnerIds(ownerIds);
		}
        System.out.println("BOOKINGNO!!!" + bookingDetail.getBookingNo());
        System.out.println("CRITERIA!!!!!!" + criteria.toString());
        if(criteria.getOwnerIds()!=null)
        {
            UserResponse userDetailResponse = userService.getUser(criteria, info);
            bookingDetail.setOwners(userDetailResponse.getUser());
        }

		
		// Enrich cart details with advertisement information from MDMS
		enrichCartDetailsWithAdvertisementInfo(bookingDetail, info);
	}

		// Fetch remaining timer values for the booking details
		// paymentTimerService.getRemainingTimerValue(bookingDetails);

		if (CollectionUtils.isEmpty(bookingDetails)) {
			return bookingDetails;
		}
		bookingDetails = encryptionService.decryptObject(bookingDetails, info);

		return bookingDetails;
	}

	@Override
	public Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria, @NonNull RequestInfo requestInfo) {
		criteria.setCountCall(true);
		Integer bookingCount = 0;

		// criteria = addCreatedByMeToCriteria(criteria, requestInfo);
		bookingCount = bookingRepository.getBookingCount(criteria);

		return bookingCount;
	}

	@Override
	public List<AdvertisementSlotAvailabilityDetail> checkAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo) {

		List<AdvertisementSlotAvailabilityDetail> availabilityDetails = bookingRepository
				.getAdvertisementSlotAvailability(criteria);
		log.info("Fetched availability details: " + availabilityDetails);

		List<AdvertisementSlotAvailabilityDetail> availabilityDetailsResponse = convertToAdvertisementAvailabilityResponse(
				criteria, availabilityDetails, requestInfo);

		updateSlotAvailaibilityStatusFromTimer(availabilityDetailsResponse, criteria, requestInfo);
		log.info("Updated availability details: " + availabilityDetailsResponse);

		return availabilityDetailsResponse;
	}

	@Override
	public List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			List<AdvertisementSlotSearchCriteria> criteriaList, RequestInfo requestInfo) {

		List<AdvertisementSlotAvailabilityDetail> allAvailabilityDetails = new ArrayList<>();

		for (AdvertisementSlotSearchCriteria criteria : criteriaList) {
			List<AdvertisementSlotAvailabilityDetail> availabilityDetails = checkAdvertisementSlotAvailability(criteria,
					requestInfo);
			allAvailabilityDetails.addAll(availabilityDetails);

		}

		boolean isTimerRequiredForAnyCriteria = criteriaList.stream()
				.anyMatch(criteria -> criteria.getIsTimerRequired());

		boolean slotBookedFlag = setSlotBookedFlag(allAvailabilityDetails);
		log.info("Slot booked flag for criteria : " + slotBookedFlag);
		if (isTimerRequiredForAnyCriteria) {
			bookingRepository.deleteDataFromTimerAndDraft(requestInfo.getUserInfo().getUuid(),
					criteriaList.get(0).getDraftId(), criteriaList.get(0).getBookingId());
		}
		if (isTimerRequiredForAnyCriteria && !slotBookedFlag) {
			// Insert the timer for all criteria at once
			paymentTimerService.insertBookingIdForTimer(criteriaList, requestInfo, allAvailabilityDetails);
			log.info("Inserted booking ID for timer for all criteria.");
		}

		return allAvailabilityDetails;
	}

	@Override
	public boolean setSlotBookedFlag(List<AdvertisementSlotAvailabilityDetail> details) {
		// Check if any slot is booked and return true if so
		return details.stream()
				.anyMatch(slot -> BookingStatusEnum.BOOKED.toString().equals(slot.getSlotStaus()));
	}

	private List<AdvertisementSlotAvailabilityDetail> convertToAdvertisementAvailabilityResponse(
			AdvertisementSlotSearchCriteria criteria, List<AdvertisementSlotAvailabilityDetail> availabiltityDetails,
			RequestInfo requestInfo) {

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

		// Fetch advertisement details from MDMS to enrich slots
		List<Advertisements> advertisementsList = mdmsUtil.fetchAdvertisementsData(requestInfo, criteria.getTenantId());
		Advertisements advertisementData = null;
		if (advertisementsList != null && criteria.getAdvertisementId() != null) {
			String advertisementIdStr = criteria.getAdvertisementId();
			advertisementData = advertisementsList.stream()
				.filter(ad -> ad.getId() != null && ad.getId().toString().equals(advertisementIdStr))
				.findFirst()
				.orElse(null);
		}

		// Create a slot availability detail for each date
		final Advertisements finalAdvertisementData = advertisementData;
		totalDates.forEach(date -> {
			AdvertisementSlotAvailabilityDetail slot = createAdvertisementSlotAvailabiltityDetail(criteria, date);
			// Enrich with advertisement details
			if (finalAdvertisementData != null) {
				enrichSlotWithAdvertisementDetails(slot, finalAdvertisementData);
			}
			availabiltityDetailsResponse.add(slot);
		});

	// Set advertisement status to 'BOOKED' if already booked
	// Match slots based on critical fields: advertisementId, bookingDate, and other attributes
	availabiltityDetailsResponse.forEach(detail -> {
		// Find matching booked slot from database
		availabiltityDetails.stream()
			.filter(dbDetail -> isSlotMatching(detail, dbDetail))
			.findFirst()
			.ifPresent(bookedSlot -> {
				// Mark as BOOKED and set the actual booking ID from database
				detail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
				if (bookedSlot.getBookingId() != null) {
					detail.setBookingId(bookedSlot.getBookingId());
				}
			});
	});

		log.info("Availability details response after updating status: " + availabiltityDetailsResponse);

		return availabiltityDetailsResponse;
	}

	public List<AdvertisementSlotAvailabilityDetail> updateSlotAvailaibilityStatusFromTimer(
			List<AdvertisementSlotAvailabilityDetail> availabilityDetailsResponse,
			AdvertisementSlotSearchCriteria criteria, RequestInfo requestInfo) {

		List<AdvertisementSlotAvailabilityDetail> bookedSlotsFromTimer = bookingRepository.getBookedSlots(criteria,
				requestInfo);
		if (bookedSlotsFromTimer == null || bookedSlotsFromTimer.isEmpty()) {
			log.info("Timer details are null or empty, returning availability details as is.");
			return availabilityDetailsResponse;
		}

		log.info("Timer Details from db : " + bookedSlotsFromTimer);

		// Check each timer entry against availability response using smart matching
		bookedSlotsFromTimer.forEach(timerDetail -> {
			// Find matching slot in availability response using smart matching
			availabilityDetailsResponse.stream()
				.filter(responseSlot -> isSlotMatching(responseSlot, timerDetail))
				.findFirst()
				.ifPresent(matchedSlot -> {
					boolean isCreatedByCurrentUser = timerDetail.getUuid().equals(requestInfo.getUserInfo().getUuid());
					boolean existingBookingId = timerDetail.getBookingId() != null &&
						timerDetail.getBookingId().equals(criteria.getBookingId());

					boolean existingDraftId = false;
					String draftId = getDraftId(availabilityDetailsResponse, requestInfo);
					if (!StringUtils.isBlank(criteria.getDraftId()) && !StringUtils.isBlank(draftId)) {
						existingDraftId = draftId.equals(criteria.getDraftId());
					}
					
					// If user is checking their own timer hold, show as AVAILABLE so they can modify
					// Otherwise, show as BOOKED to prevent others from selecting
					if (isCreatedByCurrentUser && (existingBookingId || existingDraftId)) {
						log.info("Slot held by current user with same booking/draft id - showing as AVAILABLE");
						matchedSlot.setSlotStaus(BookingStatusEnum.AVAILABLE.toString());
					} else {
						log.info("Slot held by another user or different booking - showing as BOOKED");
						matchedSlot.setSlotStaus(BookingStatusEnum.BOOKED.toString());
					}
				});
		});

		return availabilityDetailsResponse;
	}

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
		AdvertisementSlotAvailabilityDetail availabiltityDetail = AdvertisementSlotAvailabilityDetail.builder()
				.addType(criteria.getAddType()).faceArea(criteria.getFaceArea()).location(criteria.getLocation())
				.nightLight(criteria.getNightLight()).slotStaus(BookingStatusEnum.AVAILABLE.toString()).advertisementId(criteria.getAdvertisementId())
				.tenantId(criteria.getTenantId()).bookingDate(BookingUtil.parseLocalDateToString(date, "yyyy-MM-dd"))
				.bookingFromTime(criteria.getBookingFromTime())
				.bookingToTime(criteria.getBookingToTime())
				.build();
		return availabiltityDetail;
	}

	/**
	 * Enriches a slot with advertisement details from MDMS
	 */
	private void enrichSlotWithAdvertisementDetails(AdvertisementSlotAvailabilityDetail slot, Advertisements advertisement) {
		if (advertisement.getAmount() != null) {
			slot.setAmount(advertisement.getAmount().doubleValue());
		}
		slot.setAdvertisementName(advertisement.getName());
		if (advertisement.getPoleNo() != null) {
			try {
				slot.setPoleNo(Integer.parseInt(advertisement.getPoleNo()));
			} catch (NumberFormatException e) {
				log.warn("Unable to parse pole number: " + advertisement.getPoleNo());
			}
		}
		slot.setImageSrc(advertisement.getImageSrc());
		slot.setWidth(advertisement.getWidth());
		slot.setHeight(advertisement.getHeight());
		slot.setLightType(advertisement.getLight());
	}

	/**
	 * Enriches cart details with advertisement information from MDMS
	 */
	private void enrichCartDetailsWithAdvertisementInfo(BookingDetail bookingDetail, RequestInfo requestInfo) {
		if (bookingDetail == null || bookingDetail.getCartDetails() == null || bookingDetail.getCartDetails().isEmpty()) {
			return;
		}

		// Fetch advertisement data from MDMS
		String tenantId = bookingDetail.getTenantId();
		if (tenantId != null && tenantId.contains(".")) {
			tenantId = tenantId.split("\\.")[0];
		}
		
		List<Advertisements> advertisementsList = mdmsUtil.fetchAdvertisementsData(requestInfo, tenantId);
		if (advertisementsList == null || advertisementsList.isEmpty()) {
			log.warn("No advertisement data found in MDMS for tenant: " + tenantId);
			return;
		}

		// Enrich each cart detail with advertisement information
		for (CartDetail cartDetail : bookingDetail.getCartDetails()) {
			if (cartDetail.getAdvertisementId() != null) {
				String advertisementIdStr = cartDetail.getAdvertisementId();
				Advertisements advertisement = advertisementsList.stream()
					.filter(ad -> ad.getId() != null && ad.getId().toString().equals(advertisementIdStr))
					.findFirst()
					.orElse(null);

				if (advertisement != null) {
					enrichCartDetailWithAdvertisementData(cartDetail, advertisement);
				} else {
					log.warn("Advertisement not found in MDMS for advertisementId: " + advertisementIdStr);
				}
			}
		}
	}

	/**
	 * Enriches a single cart detail with advertisement data
	 */
	private void enrichCartDetailWithAdvertisementData(CartDetail cartDetail, Advertisements advertisement) {
		if (advertisement.getAmount() != null) {
			cartDetail.setAmount(advertisement.getAmount().doubleValue());
		}
		cartDetail.setAdvertisementName(advertisement.getName());
		if (advertisement.getPoleNo() != null) {
			try {
				cartDetail.setPoleNo(Integer.parseInt(advertisement.getPoleNo()));
			} catch (NumberFormatException e) {
				log.warn("Unable to parse pole number: " + advertisement.getPoleNo());
			}
		}
		cartDetail.setImageSrc(advertisement.getImageSrc());
		cartDetail.setWidth(advertisement.getWidth());
		cartDetail.setHeight(advertisement.getHeight());
		cartDetail.setLightType(advertisement.getLight());
	}

	/**
	 * Helper method to match slots based on critical fields
	 * Matches on: advertisementId, bookingDate, and optional fields (addType, location, faceArea, nightLight)
	 */
	private boolean isSlotMatching(AdvertisementSlotAvailabilityDetail searchSlot, 
			AdvertisementSlotAvailabilityDetail dbSlot) {
		
		// Primary match: advertisementId and bookingDate must match
		if (!Objects.equals(searchSlot.getAdvertisementId(), dbSlot.getAdvertisementId())) {
			return false;
		}
		
		if (!Objects.equals(searchSlot.getBookingDate(), dbSlot.getBookingDate())) {
			return false;
		}
		
		// Secondary match: if search criteria has these fields, they must match DB
		if (StringUtils.isNotBlank(searchSlot.getAddType()) && 
			!Objects.equals(searchSlot.getAddType(), dbSlot.getAddType())) {
			return false;
		}
		
		if (StringUtils.isNotBlank(searchSlot.getLocation()) && 
			!Objects.equals(searchSlot.getLocation(), dbSlot.getLocation())) {
			return false;
		}
		
		if (StringUtils.isNotBlank(searchSlot.getFaceArea()) && 
			!Objects.equals(searchSlot.getFaceArea(), dbSlot.getFaceArea())) {
			return false;
		}
		
		if (searchSlot.getNightLight() != null && 
			!Objects.equals(searchSlot.getNightLight(), dbSlot.getNightLight())) {
			return false;
		}
		
		return true;
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
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		List<OwnerInfo> owners = bookingDetails.get(0).getOwners();
		if (owners != null) {
			userService.createUser(advertisementBookingRequest.getRequestInfo(),advertisementBookingRequest.getBookingApplication());
		}
		// String tenantId = bookingDetails.get(0).getTenantId();
		// Object mdmsData =
		// mdmsUtil.mDMSCall(advertisementBookingRequest.getRequestInfo(), tenantId);
		// bookingValidator.validateUpdate(advertisementBookingRequest.getBookingApplication(),
		// mdmsData,
		// advertisementBookingRequest.getBookingApplication().getBookingStatus());

		// Preserve workflow/businessService from request (DB object won't have these)
		Workflow incomingWorkflow = advertisementBookingRequest.getBookingApplication().getWorkflow();
		String incomingBusinessService = advertisementBookingRequest.getBookingApplication().getBusinessService();

		convertBookingRequest(advertisementBookingRequest, bookingDetails.get(0));

		// Restore workflow/businessService onto the DB-loaded booking object
		advertisementBookingRequest.getBookingApplication().setWorkflow(incomingWorkflow);
//		if (incomingBusinessService != null)
//			advertisementBookingRequest.getBookingApplication().setBusinessService(incomingBusinessService);

		// If workflow action present, transition and set status from WF response
		boolean usedWorkflow = false;
		String workflowAction = null;
		try {
			if (workflowIntegrator != null
					&& advertisementBookingRequest.getBookingApplication().getWorkflow() != null
					&& StringUtils.isNotBlank(
							advertisementBookingRequest.getBookingApplication().getWorkflow().getAction())) {

				workflowAction = advertisementBookingRequest.getBookingApplication().getWorkflow().getAction();
				
				// Validate cancellation if CANCEL action is triggered
				if ("CANCEL".equalsIgnoreCase(workflowAction) || "REQUEST_CANCELLATION".equalsIgnoreCase(workflowAction)) {
					bookingValidator.validateCancellation(advertisementBookingRequest);
				}

				String nextStatus = workflowIntegrator.transition(advertisementBookingRequest.getRequestInfo(),
						advertisementBookingRequest.getBookingApplication(),
						workflowAction);
				if("SUBMIT".equalsIgnoreCase(workflowAction)){

					Object mdmsData = mdmsUtil.mDMSCall(advertisementBookingRequest.getRequestInfo(), advertisementBookingRequest.getBookingApplication().getTenantId());
					demandService.createDemand(advertisementBookingRequest, mdmsData, true);

				}
				
				// Release slots and clean timers on cancellation
				if ("CANCEL".equalsIgnoreCase(workflowAction) && StringUtils.isNotBlank(nextStatus) 
						&& nextStatus.equals(BookingStatusEnum.CANCELLED.toString())) {
					log.info("Releasing slots and cleaning timers for cancelled booking: " + bookingNo);
					String bookingId = advertisementBookingRequest.getBookingApplication().getBookingId();
					if (StringUtils.isNotBlank(bookingId) && advertisementBookingRequest.getBookingApplication().getCartDetails() != null) {
						bookingRepository.deleteTimerEntriesForSlots(bookingId, 
							advertisementBookingRequest.getBookingApplication().getCartDetails());
					}
				}

				if (StringUtils.isNotBlank(nextStatus)) {
					advertisementBookingRequest.getBookingApplication().setBookingStatus(nextStatus);
					if (advertisementBookingRequest.getBookingApplication().getCartDetails() != null) {
						advertisementBookingRequest.getBookingApplication().getCartDetails()
								.forEach(c -> c.setStatus(nextStatus));
					}
					usedWorkflow = true;
				}
			}
		} catch (Exception ex) {
			log.error("Workflow transition on update failed for booking {}", bookingNo, ex);
		}



		// Enrich (audit/payment date), and conditionally avoid overriding WF status
		enrichmentService.enrichUpdateBookingRequest(advertisementBookingRequest, usedWorkflow ? null : status);

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
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		// String tenantId = bookingDetails.get(0).getTenantId();
		// Object mdmsData =
		// mdmsUtil.mDMSCall(advertisementBookingRequest.getRequestInfo(), tenantId);
		// bookingValidator.validateUpdate(advertisementBookingRequest.getBookingApplication(),
		// mdmsData,
		// advertisementBookingRequest.getBookingApplication().getBookingStatus());

		// Preserve workflow/businessService from request (DB object won't have these)
		Workflow incomingWorkflowSync = advertisementBookingRequest.getBookingApplication().getWorkflow();
		if(incomingWorkflowSync==null){
			incomingWorkflowSync = new Workflow();
			incomingWorkflowSync.setAction("PAY");
		}
		String incomingBusinessServiceSync = advertisementBookingRequest.getBookingApplication().getBusinessService();

		convertBookingRequest(advertisementBookingRequest, bookingDetails.get(0));

		// Restore workflow/businessService onto the DB-loaded booking object
		advertisementBookingRequest.getBookingApplication().setWorkflow(incomingWorkflowSync);
		if (incomingBusinessServiceSync != null)
			advertisementBookingRequest.getBookingApplication().setBusinessService(incomingBusinessServiceSync);

		boolean usedWorkflow = false;
		try {
			if (workflowIntegrator != null
					&& advertisementBookingRequest.getBookingApplication().getWorkflow() != null
					&& StringUtils.isNotBlank(
							advertisementBookingRequest.getBookingApplication().getWorkflow().getAction())) {
				advertisementBookingRequest.getBookingApplication().getWorkflow();
				String nextStatus = workflowIntegrator.transition(advertisementBookingRequest.getRequestInfo(),
						advertisementBookingRequest.getBookingApplication(),
						advertisementBookingRequest.getBookingApplication().getWorkflow().getAction());
				if (StringUtils.isNotBlank(nextStatus)) {
					advertisementBookingRequest.getBookingApplication().setBookingStatus(nextStatus);
					if (advertisementBookingRequest.getBookingApplication().getCartDetails() != null) {
						advertisementBookingRequest.getBookingApplication().getCartDetails()
								.forEach(c -> c.setStatus(nextStatus));
					}
					usedWorkflow = true;
				}
			}
		} catch (Exception ex) {
			log.error("Workflow transition on update (sync) failed for booking {}", bookingNo, ex);
		}

		enrichmentService.enrichUpdateBookingRequest(advertisementBookingRequest, usedWorkflow ? null : status);

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
		List<DocumentDetail> documents = bookingDetailRequest.getUploadedDocumentDetails();
		if (bookingDetailDB.getPermissionLetterFilestoreId() == null
				&& bookingDetailRequest.getPermissionLetterFilestoreId() != null) {
			bookingDetailDB.setPermissionLetterFilestoreId(bookingDetailRequest.getPermissionLetterFilestoreId());
		}

		if (bookingDetailDB.getPaymentReceiptFilestoreId() == null
				&& bookingDetailRequest.getPaymentReceiptFilestoreId() != null) {
			bookingDetailDB.setPaymentReceiptFilestoreId(bookingDetailRequest.getPaymentReceiptFilestoreId());
		}
		advertisementbookingRequest.setBookingApplication(bookingDetailDB);
		advertisementbookingRequest.getBookingApplication().setUploadedDocumentDetails(documents);
	}

	@Override
	public BookingDetail createAdvertisementDraftApplication(BookingRequest bookingRequest) {

		String draftId = bookingRequest.getBookingApplication().getDraftId();
		userService.createUser(bookingRequest.getRequestInfo(),bookingRequest.getBookingApplication());

		if (StringUtils.isNotBlank(draftId)) {

			// Update existing draft
			enrichmentService.enrichUpdateAdvertisementDraftApplicationRequest(bookingRequest);
			bookingRepository.updateDraftApplication(bookingRequest);
		} else {
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
    @Transactional
    public BookingDetail modifyCartSlots(BookingRequest bookingRequest) {
        BookingDetail incoming = bookingRequest.getBookingApplication();
        String bookingNo = incoming.getBookingNo();
        String bookingId = incoming.getBookingId();

        if (StringUtils.isBlank(bookingNo)) {
            throw new CustomException("INVALID_REQUEST", "bookingId is required to modify cart");
        }

        // Fetch existing booking
        AdvertisementSearchCriteria criteria = AdvertisementSearchCriteria.builder()
                .bookingNo(bookingNo)
                .build();
        //if not found throw exception
        List<BookingDetail> existingList = bookingRepository.getBookingDetails(criteria);
        if (existingList == null || existingList.isEmpty()) {
            throw new CustomException("INVALID_BOOKING_No", "Booking not found: " + bookingNo);
        }

        BookingDetail existing = existingList.get(0);
        List<CartDetail> incomingCart = incoming.getCartDetails() != null ? incoming.getCartDetails() : new ArrayList<>();
        //set the modified by to the user in request info otherwise set it to SYSTEM
        String modifiedBy = bookingRequest.getRequestInfo() != null && bookingRequest.getRequestInfo().getUserInfo() != null
                ? bookingRequest.getRequestInfo().getUserInfo().getUuid()
                : "SYSTEM";
        long modifiedTime = BookingUtil.getCurrentTimestamp();

        // Remove all existing cart slots and timers
        bookingRepository.markCartSlotsRemoved(bookingId, existing.getCartDetails(), modifiedBy, modifiedTime);
        bookingRepository.deleteTimerEntriesForSlots(bookingId, existing.getCartDetails());

        // Step 2: Prepare incoming slots
		for (CartDetail c : incomingCart) {
            //set a cart-id if its blank usually for a newly added one it will be upserted to booking_created
            if (StringUtils.isBlank(c.getCartId())) {
                c.setCartId(BookingUtil.getRandonUUID());
            }
            c.setBookingId(bookingId);
			// Ensure audit details are set for new/modified cart entries so DB non-null constraints are satisfied
			if (c.getAuditDetails() == null || c.getAuditDetails().getCreatedBy() == null) {
				AuditDetails ad = new AuditDetails();
				ad.setCreatedBy(modifiedBy);
				ad.setLastModifiedBy(modifiedBy);
				long now = BookingUtil.getCurrentTimestamp();
				ad.setCreatedTime(now);
				ad.setLastModifiedTime(now);
				c.setAuditDetails(ad);
			} else {
				// If audit present, at least update lastModified fields
				c.getAuditDetails().setLastModifiedBy(modifiedBy);
				c.getAuditDetails().setLastModifiedTime(BookingUtil.getCurrentTimestamp());
			}
        }

        // Step 3: Group by key attributes to handle continuous ranges per advertisement slot
        Map<String, List<CartDetail>> groupedByAttributes = incomingCart.stream()
                .collect(Collectors.groupingBy(c ->
                        c.getAdvertisementId() + "|" +
                                c.getAddType() + "|" +
                                c.getLocation() + "|" +
                                c.getFaceArea() + "|" +
                                c.getNightLight()
                ));

        List<AdvertisementSlotSearchCriteria> criteriaList = new ArrayList<>();

        for (Map.Entry<String, List<CartDetail>> entry : groupedByAttributes.entrySet()) {
            List<CartDetail> group = entry.getValue();
            List<LocalDate> dates = group.stream()
                    .map(CartDetail::getBookingDate)
                    .filter(Objects::nonNull)
                    .sorted()
                    .collect(Collectors.toList());

            // Split into continuous ranges this is done cause a person can remove a day in between the continuity so splitting them makes the timers consistent
            List<DateRange> ranges = splitIntoContinuousRanges(dates);
            CartDetail sample = group.get(0);

            for (DateRange range : ranges) {
                AdvertisementSlotSearchCriteria criteriaItem = AdvertisementSlotSearchCriteria.builder()
                        .advertisementId(sample.getAdvertisementId())
                        .addType(sample.getAddType())
                        .location(sample.getLocation())
                        .faceArea(sample.getFaceArea())
                        .nightLight(sample.getNightLight())
                        .tenantId(incoming.getTenantId())
                        .bookingStartDate(BookingUtil.parseLocalDateToString(range.getStart(), "yyyy-MM-dd"))
                        .bookingEndDate(BookingUtil.parseLocalDateToString(range.getEnd(), "yyyy-MM-dd"))
                        .isTimerRequired(true)
                        .build();

                criteriaList.add(criteriaItem);
            }
        }

        // 🔥 Step 4: Check slot availability for all criteria ranges
        List<AdvertisementSlotAvailabilityDetail> allAvailabilityDetails = new ArrayList<>();
        for (AdvertisementSlotSearchCriteria criteria1 : criteriaList) {
            List<AdvertisementSlotAvailabilityDetail> availabilityDetails =
                    checkAdvertisementSlotAvailability(criteria1, bookingRequest.getRequestInfo());
            allAvailabilityDetails.addAll(availabilityDetails);
        }

        // 🔥 Step 5: Insert new timers for updated slots
        if (!allAvailabilityDetails.isEmpty()) {
            paymentTimerService.insertBookingIdForTimerWithOwner(
                    criteriaList,
                    bookingNo,
                    bookingId,
                    bookingRequest.getRequestInfo(),
                    allAvailabilityDetails.get(0)
            );
        }

        bookingRepository.updateTimerBookingId(bookingId, bookingNo, bookingId);

        // 🔥 Step 6: Persist new cart data
        bookingRepository.updateBooking(bookingRequest);

        existing.setCartDetails(incomingCart);
        return existing;
    }
    /**
	 * Helper to compare two cart slots for equivalence based on advertisementId, bookingDate and optional attributes
	 */
	private boolean isCartSlotMatching(CartDetail a, CartDetail b) {
		if (a == null || b == null) return false;
		if (!Objects.equals(a.getAdvertisementId(), b.getAdvertisementId())) return false;
		if (!Objects.equals(a.getBookingDate(), b.getBookingDate())) return false;
		if (StringUtils.isNotBlank(a.getAddType()) && !Objects.equals(a.getAddType(), b.getAddType())) return false;
		if (StringUtils.isNotBlank(a.getFaceArea()) && !Objects.equals(a.getFaceArea(), b.getFaceArea())) return false;
		if (StringUtils.isNotBlank(a.getLocation()) && !Objects.equals(a.getLocation(), b.getLocation())) return false;
		if (a.getNightLight() != null && !Objects.equals(a.getNightLight(), b.getNightLight())) return false;
		return true;
	}

    /**
     * Splits a list of dates into continuous ranges (e.g. 1,2,4,5 → [1–2], [4–5]).
     */
    private List<DateRange> splitIntoContinuousRanges(List<LocalDate> dates) {
        List<DateRange> ranges = new ArrayList<>();
        if (dates == null || dates.isEmpty()) return ranges;

        LocalDate start = dates.get(0);
        LocalDate prev = start;

        for (int i = 1; i < dates.size(); i++) {
            LocalDate current = dates.get(i);

            if (!current.minusDays(1).equals(prev)) {
                ranges.add(new DateRange(start, prev));
                start = current;
            }

            prev = current;
        }

        // Add final range
        ranges.add(new DateRange(start, prev));
        return ranges;
    }

	@Override
	public List<BookingDetail> getAdvertisementDraftApplicationDetails(@NonNull RequestInfo requestInfo,
			@Valid AdvertisementSearchCriteria criteria) {
		return bookingRepository.getAdvertisementDraftApplications(requestInfo, criteria);
	}

	public String deleteAdvertisementDraft(String draftId) {

		if (StringUtils.isNotBlank(draftId)) {
			log.info("Deleting draft entry for draft id: " + draftId);
			bookingRepository.deleteDraftApplication(draftId);
		}
		return BookingConstants.DRAFT_DISCARDED;
	}

}
