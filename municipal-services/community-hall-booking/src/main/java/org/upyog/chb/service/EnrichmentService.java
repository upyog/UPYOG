package org.upyog.chb.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.IdGenRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.idgen.IdResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * This service class handles the enrichment of booking requests in the
 * Community Hall Booking module.
 * 
 * Purpose:
 * - To enrich booking requests with additional data required for processing.
 * - To generate and assign unique identifiers for bookings and related entities.
 * - To populate audit details for tracking creation and modification events.
 * 
 * Dependencies:
 * - CommunityHallBookingConfiguration: Provides configuration properties for enrichment operations.
 * - IdGenRepository: Interacts with the ID generation service to generate unique IDs.
 * - CommunityHallBookingUtil: Utility class for common operations such as generating UUIDs.
 * 
 * Features:
 * - Enriches booking requests with unique booking IDs and slot IDs.
 * - Populates audit details, including createdBy, createdTime, lastModifiedBy, and lastModifiedTime.
 * - Validates and processes data to ensure consistency and completeness.
 * - Logs enrichment operations for debugging and monitoring purposes.
 * 
 * Methods:
 * 1. enrichCreateBookingRequest:
 *    - Enriches a booking request during the creation process.
 *    - Generates a unique booking ID and populates audit details.
 * 
 * 2. enrichUpdateBookingRequest:
 *    - Enriches a booking request during the update process.
 *    - Updates audit details to reflect the modification event.
 * 
 * Usage:
 * - This class is automatically managed by Spring and injected wherever enrichment
 *   operations are required.
 * - It ensures consistent and reusable logic for enriching booking requests in the module.
 */
@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	public void enrichCreateBookingRequest(CommunityHallBookingRequest bookingRequest) {
		String bookingId = CommunityHallBookingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + bookingId);
		
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = CommunityHallBookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		
		bookingDetail.setAuditDetails(auditDetails);
		bookingDetail.setBookingId(bookingId);
		bookingDetail.setApplicationDate(auditDetails.getCreatedTime());
		bookingDetail.setBookingStatus(BookingStatusEnum.valueOf(bookingDetail.getBookingStatus()).toString());
		
		//Updating id and status for slot details
		bookingDetail.getBookingSlotDetails().stream().forEach(slot -> {
			slot.setBookingId(bookingId);
			slot.setSlotId(CommunityHallBookingUtil.getRandonUUID());
			//Check Slot staus before setting TODO: booking_created
			slot.setStatus(BookingStatusEnum.valueOf(slot.getStatus()).toString());
			slot.setAuditDetails(auditDetails);
		});
		
		//Updating id booking in documents
		bookingDetail.getUploadedDocumentDetails().stream().forEach(document -> {
			document.setBookingId(bookingId);
			document.setDocumentDetailId(CommunityHallBookingUtil.getRandonUUID());
			document.setAuditDetails(auditDetails);
		});


		bookingDetail.getApplicantDetail().setBookingId(bookingId);
		bookingDetail.getApplicantDetail().setApplicantDetailId(CommunityHallBookingUtil.getRandonUUID());
		bookingDetail.getApplicantDetail().setAuditDetails(auditDetails);
	
		
		bookingDetail.getAddress().setAddressId(CommunityHallBookingUtil.getRandonUUID());
		bookingDetail.getAddress().setApplicantDetailId(bookingDetail.getApplicantDetail().getApplicantDetailId());

		List<String> customIds = getIdList(requestInfo, bookingDetail.getTenantId(),
				config.getCommunityHallBookingIdKey(), config.getCommunityHallBookingIdFromat(), 1);
		
		log.info("Enriched booking request for booking no :" + customIds.get(0));

		bookingDetail.setBookingNo(customIds.get(0));

	}

	/**
	 * Returns a list of numbers generated from idgen
	 *
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idKey       code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated
	 * @return List of ids generated using idGen service
	 */
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	public void enrichUpdateBookingRequest(CommunityHallBookingRequest communityHallsBookingRequest, BookingStatusEnum statusEnum) {
		AuditDetails auditDetails = CommunityHallBookingUtil.getAuditDetails(communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid(), false);
		CommunityHallBookingDetail bookingDetail = communityHallsBookingRequest.getHallsBookingApplication();
		if(statusEnum != null) {
			bookingDetail.setBookingStatus(statusEnum.toString());
			//bookingDetail.setReceiptNo(paymentRequest.getPayment().getTransactionNumber());;
			bookingDetail.getBookingSlotDetails().stream().forEach(slot -> {
				slot.setStatus(statusEnum.toString());
			});
		}
		communityHallsBookingRequest.getHallsBookingApplication().setPaymentDate(auditDetails.getLastModifiedTime());
		communityHallsBookingRequest.getHallsBookingApplication().setAuditDetails(auditDetails);
		
	}

}
