package org.upyog.chb.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.repository.IdGenRepository;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.CommunityHallBookingRequest;
import org.upyog.chb.web.models.idgen.IdResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	public void enrichCreateBookingRequest(CommunityHallBookingRequest bookingRequest, Object mdmsData,
			Map<String, String> values) {
		String bookingId = CommunityHallBookingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + bookingId);
		CommunityHallBookingDetail bookingDetail = bookingRequest.getHallsBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = CommunityHallBookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		bookingDetail.setAuditDetails(auditDetails);
		bookingDetail.setBookingId(bookingId);

		// Set booking it to dependent tables with foreign key relation

		bookingDetail.getBookingSlotDetails().stream().forEach(slot -> {
			slot.setBookingId(bookingDetail.getBookingId());
			slot.setSlotId(CommunityHallBookingUtil.getRandonUUID());
			slot.setAuditDetails(auditDetails);
		});

		bookingDetail.getUploadedDocumentDetails().stream().forEach(document -> {
			document.setBookingId(bookingDetail.getBookingId());
			document.setDocumentId(CommunityHallBookingUtil.getRandonUUID());
			document.setAuditDetails(auditDetails);
		});

		bookingDetail.getBankDetails().setBookingId(bookingDetail.getBookingId());
		bookingDetail.getBankDetails().setBankDetailId(CommunityHallBookingUtil.getRandonUUID());
		bookingDetail.getBankDetails().setAuditDetails(auditDetails);

		List<String> customIds = getIdList(requestInfo, bookingDetail.getTenantId(),
				config.getCommunityHallBookingIdKey(), config.getCommunityHallBookingIdFromat(), 1);

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

}
