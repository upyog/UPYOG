package org.upyog.adv.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.service.BookingService;
import org.upyog.adv.validator.BookingValidator;
//import org.upyog.adv.service.EncryptionService;
import org.upyog.adv.service.EnrichmentService;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.MdmsUtil;
import org.upyog.adv.web.models.AdvertisementResponse;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.AdvertisementSlotAvailabilityDetail;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.RequestInfoWrapper;
import org.upyog.adv.web.models.ResponseInfo;
import org.upyog.adv.web.models.ResponseInfo.StatusEnum;
import org.upyog.adv.web.models.ApplicantDetail;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {

	@Autowired
	private MdmsUtil mdmsUtil;

	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private BookingValidator bookingValidator;

	@Autowired
	private EnrichmentService enrichmentService;

//		@Autowired
//		EncryptionService encryptionService;

	@Override
	public BookingDetail createBooking(@Valid BookingRequest bookingRequest) {
		log.info("Create advertisement booking for user : " + bookingRequest.getRequestInfo().getUserInfo().getId());
		// TODO move to util calss 
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
		// encryptionService.encryptObject(bookingRequest);

		/**
		 * Workflow will come into picture once hall location changes or booking is
		 * cancelled otherwise after payment booking will be auto approved
		 * 
		 */

		// 3.Update workflow of the application
		// workflowService.updateWorkflow(bookingRequest,
		// WorkflowStatus.CREATE);

		// demandService.createDemand(bookingRequest, mdmsData, true);

		// 4.Persist the request using persister service
		bookingRepository.saveBooking(bookingRequest);

		return bookingRequest.getBookingApplication();
	}

@Override
public List<BookingDetail> getBookingDetails(AdvertisementSearchCriteria bookingSearchCriteria, RequestInfo info) {
//	BookingValidator.validateSearch(info, bookingSearchCriteria);
	List<BookingDetail> bookingDetails = new ArrayList<BookingDetail>();
//	bookingSearchCriteria  = addCreatedByMeToCriteria(bookingSearchCriteria, info);
	
	log.info("loading data based on criteria" + bookingSearchCriteria);
	
	if(bookingSearchCriteria.getMobileNumber() != null){
	
	ApplicantDetail applicantDetail = ApplicantDetail.builder().applicantMobileNo(bookingSearchCriteria.getMobileNumber())
			.build();
	BookingDetail bookingDetail = BookingDetail.builder().applicantDetail(applicantDetail)
			.build();
	BookingRequest bookingRequest = BookingRequest.builder()
			.bookingApplication(bookingDetail).requestInfo(info).build();
	
//	BookingDetail = encryptionService.encryptObject(bookingRequest);
	
	bookingSearchCriteria.setMobileNumber(bookingDetail.getApplicantDetail().getApplicantMobileNo());
	
	log.info("loading data based on criteria after encrypting mobile no : " + bookingSearchCriteria);
	
	}
	
	bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
	if(CollectionUtils.isEmpty(bookingDetails)) {
		return bookingDetails;
	}
//	bookingDetails = encryptionService.decryptObject(bookingDetails, info);

	return bookingDetails;
}

	@Override
	public Integer getBookingCount(@Valid AdvertisementSearchCriteria criteria,
			@NonNull RequestInfo requestInfo) {
		criteria.setCountCall(true);
		Integer bookingCount = 0;
		
	//	criteria  = addCreatedByMeToCriteria(criteria, requestInfo);
		bookingCount = bookingRepository.getBookingCount(criteria);
		
		return bookingCount;
	}
		

	@Override
	public List<AdvertisementSlotAvailabilityDetail> getAdvertisementSlotAvailability(
			AdvertisementSlotSearchCriteria criteria) {

		List<AdvertisementSlotAvailabilityDetail> availabiltityDetails = bookingRepository
				.getAdvertisementSlotAvailability(criteria);
		log.info("Availabiltity details fetched from DB :" + availabiltityDetails);

		List<AdvertisementSlotAvailabilityDetail> availabiltityDetailsResponse = convertToAdvertisementAvailabilityResponse(
				criteria, availabiltityDetails);

		log.info("Availabiltity details response after updating status :" + availabiltityDetailsResponse);
		return availabiltityDetailsResponse;
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
		if (totalDates.size() > 3) {
			throw new CustomException(BookingConstants.INVALID_BOOKING_DATE_RANGE,
					"Booking is not allowed for this number of days.");
		}

		// Create a slot availability detail for each date
		totalDates.forEach(date -> {
			availabiltityDetailsResponse.add(createAdvertisementSlotAvailabiltityDetail(criteria, date));
		});

		// Set advertisement status to 'BOOKED' if already booked
		availabiltityDetailsResponse.forEach(detail -> {
			if (availabiltityDetails.contains(detail)) {
				detail.setSlotStaus(BookingStatusEnum.BOOKED.toString());
			}
		});

		return availabiltityDetailsResponse;
	}

	private AdvertisementSlotAvailabilityDetail createAdvertisementSlotAvailabiltityDetail(
			AdvertisementSlotSearchCriteria criteria, LocalDate date) {
		AdvertisementSlotAvailabilityDetail availabiltityDetail = AdvertisementSlotAvailabilityDetail.builder()
				.addType(criteria.getAddType()).faceArea(criteria.getFaceArea()).location(criteria.getLocation())
				.nightLight(criteria.getNightLight()).slotStaus(BookingStatusEnum.AVAILABLE.toString())
				.tenantId(criteria.getTenantId()).bookingDate(BookingUtil.parseLocalDateToString(date, "yyyy-MM-dd"))
				.build();
		return availabiltityDetail;
	}

}

