package org.upyog.chb.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.enums.BookingStatusEnum;
import org.upyog.chb.repository.IdGenRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.VenueBookingRequest;
import org.upyog.chb.web.models.idgen.IdResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	private final CommunityHallBookingConfiguration config;
	private final IdGenRepository idGenRepository;

	public EnrichmentService(CommunityHallBookingConfiguration config, IdGenRepository idGenRepository) {
		this.config = config;
		this.idGenRepository = idGenRepository;
	}

	public void enrichCreateBookingRequest(VenueBookingRequest bookingRequest) {
		String bookingId = CommunityHallBookingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + bookingId);
		
		VenueBookingDetail bookingDetail = bookingRequest.getVenueBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = CommunityHallBookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		
		bookingDetail.setAuditDetails(auditDetails);
		bookingDetail.setBookingId(bookingId);
		bookingDetail.setApplicationDate(auditDetails.getCreatedTime());
		bookingDetail.setBookingStatus(BookingStatusEnum.valueOf(bookingDetail.getBookingStatus()).toString());
		
		bookingDetail.getBookingSlotDetails().forEach(slot -> {
			slot.setBookingId(bookingId);
			slot.setSlotId(CommunityHallBookingUtil.getRandonUUID());
			slot.setStatus(BookingStatusEnum.valueOf(slot.getStatus()).toString());
			slot.setAuditDetails(auditDetails);
		});
		
		bookingDetail.getUploadedDocumentDetails().forEach(document -> {
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

	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).toList();
	}

	public void enrichUpdateBookingRequest(VenueBookingRequest communityHallsBookingRequest, BookingStatusEnum statusEnum) {
		AuditDetails auditDetails = CommunityHallBookingUtil.getAuditDetails(communityHallsBookingRequest.getRequestInfo().getUserInfo().getUuid(), false);
		VenueBookingDetail bookingDetail = communityHallsBookingRequest.getVenueBookingApplication();
		if(statusEnum != null) {
			bookingDetail.setBookingStatus(statusEnum.toString());
			bookingDetail.getBookingSlotDetails().forEach(slot -> slot.setStatus(statusEnum.toString()));
		}
		communityHallsBookingRequest.getVenueBookingApplication().setPaymentDate(auditDetails.getLastModifiedTime());
		communityHallsBookingRequest.getVenueBookingApplication().setAuditDetails(auditDetails);
		
	}

}
