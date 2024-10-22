package org.upyog.adv.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.IdGenRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.AuditDetails;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private BookingConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	public void enrichCreateBookingRequest(BookingRequest bookingRequest) {
		String bookingId = BookingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + bookingId);
		
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = BookingUtil.getAuditDetails(requestInfo.getUserInfo().getId(), true);
		
		
		bookingDetail.setAuditDetails(auditDetails);
		bookingDetail.setBookingId(bookingId);
		bookingDetail.setApplicationDate(auditDetails.getCreatedTime());
		bookingDetail.setBookingStatus(BookingStatusEnum.valueOf(bookingDetail.getBookingStatus()).toString());
		
		
		//Updating id and status for slot details
		bookingDetail.getBookingSlotDetails().stream().forEach(slot -> {
			slot.setBookingId(bookingId);
			
			slot.setSlotId(BookingUtil.getRandonUUID());
			//Check Slot staus before setting TODO: booking_created
			slot.setStatus(BookingStatusEnum.valueOf(slot.getStatus()).toString());
			slot.setAuditDetails(auditDetails);
			
			
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

//		List<String> customIds = getIdList(requestInfo, bookingDetail.getTenantId(),
//				config.getAdvertisementBookingIdKey(), config.getAdvertisementBookingIdFromat(), 1);
		
		List<String> customIds = Arrays.asList(
			    "ADV-" + UUID.randomUUID().toString(),
			    "CITY-" + UUID.randomUUID().toString()
			  
			);

		
		
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

	public void enrichUpdateBookingRequest(BookingRequest bookingRequest, BookingStatusEnum statusEnum) {
		AuditDetails auditDetails = BookingUtil.getAuditDetails(bookingRequest.getRequestInfo().getUserInfo().getId(), false);
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		if(statusEnum != null) {
			bookingDetail.setBookingStatus(statusEnum.toString());
			//bookingDetail.setReceiptNo(paymentRequest.getPayment().getTransactionNumber());;
			bookingDetail.getBookingSlotDetails().stream().forEach(slot -> {
				slot.setStatus(statusEnum.toString());
			});
		}
		bookingRequest.getBookingApplication().setPaymentDate(auditDetails.getLastModifiedTime());
		bookingRequest.getBookingApplication().setAuditDetails(auditDetails);
		
	}

}
