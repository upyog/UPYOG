package org.upyog.chb.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.enums.SlotStatusEnum;
import org.upyog.chb.repository.CommunityHallBookingRepository;
import org.upyog.chb.service.CommunityHallBookingService;
import org.upyog.chb.service.DemandService;
import org.upyog.chb.service.EnrichmentService;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.util.MdmsUtil;
import org.upyog.chb.validator.CommunityHallBookingValidator;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.CommunityHallBookingSearchCriteria;
import org.upyog.chb.web.models.CommunityHallSlotAvailabilityDetail;
import org.upyog.chb.web.models.CommunityHallSlotSearchCriteria;

import digit.models.coremodels.PaymentDetail;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommunityHallBookingServiceImpl implements CommunityHallBookingService {

	@Autowired
	private CommunityHallBookingRepository bookingRepository;
	@Autowired
	private CommunityHallBookingValidator hallBookingValidator;

	/*
	 * @Autowired private WorkflowService workflowService;
	 */

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private MdmsUtil mdmsUtil;
	
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
		enrichmentService.enrichCreateBookingRequest(communityHallsBookingRequest, communityHallsBookingRequest);

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
		List<String> roles = new ArrayList<>();
		for (Role role : info.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		log.info("user roles for searching : " + roles);
		if ((bookingSearchCriteria.tenantIdOnly() || bookingSearchCriteria.isEmpty())
				&& roles.contains(CommunityHallBookingConstants.CITIZEN)) {
			log.debug("loading data of created and by me");
			bookingDetails = this.getBookingCreatedByMe(bookingSearchCriteria, info);
		} else {
			bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		}

		return bookingDetails;
	}

	private List<CommunityHallBookingDetail> getBookingCreatedByMe(CommunityHallBookingSearchCriteria criteria,
			RequestInfo requestInfo) {
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();

		List<String> uuids = new ArrayList<>();
		if (requestInfo.getUserInfo() != null && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
		}
		log.debug("loading data of created and by me" + uuids.toString());
		bookingDetails = bookingRepository.getBookingDetails(criteria);
		return bookingDetails;
	}

	@Override
	public CommunityHallBookingDetail updateBooking(CommunityHallBookingRequest communityHallsBookingRequest,
			PaymentDetail paymentDetail, BookingStatusEnum status) {
		String bookingNo = communityHallsBookingRequest.getHallsBookingApplication().getBookingNo();
		log.info("Updating booking for booking no : " + bookingNo);
		if (bookingNo == null) {
			return null;
		}
		CommunityHallBookingSearchCriteria bookingSearchCriteria = CommunityHallBookingSearchCriteria.builder()
				.bookingNo(bookingNo).build();
		List<CommunityHallBookingDetail> bookingDetails = bookingRepository.getBookingDetails(bookingSearchCriteria);
		if (bookingDetails.size() == 0) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Booking no not valid. Failed to update booking status for : " + bookingNo);
		}

		convertBookingRequest(communityHallsBookingRequest, bookingDetails.get(0));

		enrichmentService.enrichUpdateBookingRequest(communityHallsBookingRequest, status);
		if (paymentDetail != null) {
			communityHallsBookingRequest.getHallsBookingApplication().setReceiptNo(paymentDetail.getReceiptNumber());
			communityHallsBookingRequest.getHallsBookingApplication().setPaymentDate(paymentDetail.getReceiptDate());
		}
		bookingRepository.updateBooking(communityHallsBookingRequest);
		log.info("fetched booking detail and updated status "
				+ communityHallsBookingRequest.getHallsBookingApplication().getBookingStatus());
		return communityHallsBookingRequest.getHallsBookingApplication();
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
		communityHallsBookingRequest.setHallsBookingApplication(bookingDetailDB);
	}

	@Override
	public List<CommunityHallSlotAvailabilityDetail> getCommunityHallSlotAvailability(
			CommunityHallSlotSearchCriteria criteria) {
		if (criteria.getCommunityHallCode() == null || CollectionUtils.isEmpty(criteria.getHallCodes())) {

		}

		List<CommunityHallSlotAvailabilityDetail> availabiltityDetails = bookingRepository
				.getCommunityHallSlotAvailability(criteria);
		log.info("Availabiltity details fetched from DB :" + availabiltityDetails);
		
		List<CommunityHallSlotAvailabilityDetail> availabiltityDetailsResponse = convertToCommunityHallAvailabilityResponse(
				criteria, availabiltityDetails);

		log.info("Availabiltity details response after updating status :" + availabiltityDetailsResponse);
		return availabiltityDetailsResponse;
	}

	/**
	 * 
	 * @param criteria
	 * @param availabiltityDetails
	 * @return
	 */
	private List<CommunityHallSlotAvailabilityDetail> convertToCommunityHallAvailabilityResponse(
			CommunityHallSlotSearchCriteria criteria, List<CommunityHallSlotAvailabilityDetail> availabiltityDetails) {

		List<CommunityHallSlotAvailabilityDetail> availabiltityDetailsResponse = new ArrayList<CommunityHallSlotAvailabilityDetail>();
		LocalDate startDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingStartDate());

		LocalDate endDate = CommunityHallBookingUtil.parseStringToLocalDate(criteria.getBookingEndDate());

		List<LocalDate> totalDates = new ArrayList<>();
		//Calculating list of dates for booking
		while (!startDate.isAfter(endDate)) {
			totalDates.add(startDate);
			startDate = startDate.plusDays(1);
		}

		totalDates.stream().forEach(date -> {
			List<String> hallCodes = new ArrayList<>();
			if (StringUtils.isNotBlank(criteria.getHallCode())) {
				hallCodes.add(criteria.getHallCode());
			} else {
				hallCodes.addAll(criteria.getHallCodes());
			}
			hallCodes.stream().forEach(data -> {
				availabiltityDetailsResponse.add(createCommunityHallSlotAvailabiltityDetail(criteria, date, data));
			});
		});

		//Setting hall status to booked if it is already booked by checking in the database entry
		availabiltityDetailsResponse.stream().forEach(detail -> {
			if (availabiltityDetails.contains(detail)) {
				detail.setSlotStaus(SlotStatusEnum.BOOKED.toString());
			}
		});

		return availabiltityDetailsResponse;
	}

	private CommunityHallSlotAvailabilityDetail createCommunityHallSlotAvailabiltityDetail(
			CommunityHallSlotSearchCriteria criteria, LocalDate date, String hallCode) {
		CommunityHallSlotAvailabilityDetail availabiltityDetail = CommunityHallSlotAvailabilityDetail.builder()
				.communityHallCode(criteria.getCommunityHallCode()).hallCode(hallCode)
			//Setting slot status available for every hall and hall code
				.slotStaus(SlotStatusEnum.AVAILABLE.toString()).tenantId(criteria.getTenantId())
				.bookingDate(CommunityHallBookingUtil.parseLocalDateToString(date)).build();
		return availabiltityDetail;
	}

}
