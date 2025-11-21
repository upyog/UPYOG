package org.upyog.adv.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.repository.IdGenRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AdvertisementDraftDetail;
import org.upyog.adv.web.models.AuditDetails;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;
import org.upyog.adv.web.models.idgen.IdResponse;

import lombok.extern.slf4j.Slf4j;
/**
 * Service class for enriching advertisement booking requests in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Enriches booking requests with additional details such as booking IDs and audit details.
 * - Generates unique IDs for bookings using the ID generation service.
 * - Updates booking details with metadata like timestamps and user information.
 * - Handles draft applications and ensures proper enrichment before persistence.
 * 
 * Dependencies:
 * - BookingConfiguration: Provides configuration properties for enrichment.
 * - IdGenRepository: Interacts with the ID generation service to generate unique IDs.
 * - BookingRepository: Handles database interactions for booking-related operations.
 * - BookingUtil: Provides utility methods for generating UUIDs and other enrichment tasks.
 * 
 * Annotations:
 * - @Service: Marks this class as a Spring-managed service component.
 * - @Slf4j: Enables logging for debugging and monitoring enrichment processes.
 */
@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private BookingConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;
	
	@Autowired
	@Lazy
	private BookingRepository bookingRepository;

	public void enrichCreateBookingRequest(BookingRequest bookingRequest) {
		String bookingId = BookingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + bookingId);
		
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = BookingUtil.getAuditDetails(
			    String.valueOf(bookingRequest.getRequestInfo().getUserInfo().getUuid()), 
			    true
			);
		
		bookingDetail.setAuditDetails(auditDetails);
		bookingDetail.setBookingId(bookingId);
		bookingDetail.setApplicationDate(auditDetails.getCreatedTime());
		bookingDetail.setBookingStatus(BookingStatusEnum.valueOf(bookingDetail.getBookingStatus()).toString());
		
		
		//Updating id and status for cart details
		bookingDetail.getCartDetails().stream().forEach(cart -> {
			cart.setBookingId(bookingId);
			
			cart.setCartId(BookingUtil.getRandonUUID());
			//Check cart staus before setting TODO: booking_created
			cart.setStatus(BookingStatusEnum.valueOf(cart.getStatus()).toString());
			cart.setAuditDetails(auditDetails);
			
			
		});
		
		//Updating id booking in documents
		bookingDetail.getUploadedDocumentDetails().stream().forEach(document -> {
			document.setBookingId(bookingId);
			document.setDocumentDetailId(BookingUtil.getRandonUUID());
			document.setAuditDetails(auditDetails);
			
		});


		bookingDetail.getApplicantDetail().setBookingId(bookingId);
		bookingDetail.getApplicantDetail().setApplicantDetailId(BookingUtil.getRandonUUID());
		bookingDetail.getApplicantDetail().setAuditDetails(auditDetails);
	
		
		bookingDetail.getAddress().setAddressId(BookingUtil.getRandonUUID());
		bookingDetail.getAddress().setApplicantDetailId(bookingDetail.getApplicantDetail().getApplicantDetailId());

		List<String> customIds = getIdList(requestInfo, bookingDetail.getTenantId(),
				config.getAdvertisementBookingIdKey(), config.getAdvertisementBookingIdFromat(), 1);
		
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

	//This enriches the booking request, if status is not null then it updates the booking status in booking detail and cart detail, also updates the payment date and audit details
	public void enrichUpdateBookingRequest(BookingRequest bookingRequest, BookingStatusEnum statusEnum) {
		AuditDetails auditDetails = BookingUtil.getAuditDetails(
			    String.valueOf(bookingRequest.getRequestInfo().getUserInfo().getUuid()), 
			    Boolean.FALSE
			);

		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		if(statusEnum != null) {
			bookingDetail.setBookingStatus(statusEnum.toString());
			bookingDetail.getCartDetails().stream().forEach(cart -> {
				cart.setStatus(statusEnum.toString());
			});
		}
		bookingRequest.getBookingApplication().setPaymentDate(auditDetails.getLastModifiedTime());
		bookingRequest.getBookingApplication().setAuditDetails(auditDetails);
		
	}
	
	public void enrichCreateAdvertisementDraftApplicationRequest(BookingRequest bookingRequest) {
	 
	    List<AdvertisementDraftDetail> draftData = bookingRepository
	            .getDraftData(bookingRequest.getRequestInfo().getUserInfo().getUuid());

	    if (draftData != null && !draftData.isEmpty()) {	      
	        String draftId = draftData.get(0).getDraftId(); 
	        log.info("Enriching create draft street vending application with draft id: " + draftId);

	        BookingDetail bookingDetail = bookingRequest.getBookingApplication();
	        RequestInfo requestInfo = bookingRequest.getRequestInfo();
	        AuditDetails auditDetails = BookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

	        bookingDetail.setDraftId(draftId);
	        bookingDetail.setAuditDetails(auditDetails);
	    } else {
	        log.warn("No draft data found for UUID: " + bookingRequest.getRequestInfo().getUserInfo().getUuid());
	    }
	}

	
	public void enrichUpdateAdvertisementDraftApplicationRequest(BookingRequest bookingRequest) {
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		log.info("Enriching update draft street vending application with draft id :" + bookingDetail.getDraftId());
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = BookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);

		bookingDetail.setAuditDetails(auditDetails);
		
	}

}
