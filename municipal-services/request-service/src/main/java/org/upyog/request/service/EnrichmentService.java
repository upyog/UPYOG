package org.upyog.request.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.request.service.config.RequestServiceConfiguration;
import org.upyog.request.service.enums.RequestServiceStatus;
import org.upyog.request.service.repository.IdGenRepository;
import org.upyog.request.service.util.RequestServiceUtil;
import org.upyog.request.service.web.models.AuditDetails;
import org.upyog.request.service.web.models.WaterTankerBookingDetail;
import org.upyog.request.service.web.models.WaterTankerBookingRequest;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private RequestServiceConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	public void enrichCreateWaterTankerRequest(WaterTankerBookingRequest waterTankerRequest) {
		String bookingId = RequestServiceUtil.getRandonUUID();
		log.info("Enriching water tanker booking id :" + bookingId);

		WaterTankerBookingDetail waterTankerDetail = waterTankerRequest.getWaterTankerBookingDetail();
		RequestInfo requestInfo = waterTankerRequest.getRequestInfo();
		AuditDetails auditDetails = RequestServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		
		
		waterTankerDetail.setBookingId(bookingId);
		waterTankerDetail.setApplicationDate(auditDetails.getCreatedTime());
		waterTankerDetail.setBookingStatus(RequestServiceStatus.valueOf(waterTankerDetail.getBookingStatus()).toString());
		waterTankerDetail.setAuditDetails(auditDetails);
		waterTankerDetail.setTenantId(waterTankerRequest.getWaterTankerBookingDetail().getTenantId());	
		
	    List<String> customIds = getIdList(requestInfo, waterTankerDetail.getTenantId(),
				config.getWaterTankerApplicationKey(), config.getWaterTankerApplicationFormat(), 1);

		log.info("Enriched application request application no :" + customIds.get(0));

		waterTankerDetail.setBookingNo(customIds.get(0)); 

		
		waterTankerDetail.setTankerType(waterTankerRequest.getWaterTankerBookingDetail().getTankerType());			
		waterTankerDetail.setTankerQuantity(waterTankerRequest.getWaterTankerBookingDetail().getTankerQuantity());	
		waterTankerDetail.setWaterQuantity(waterTankerRequest.getWaterTankerBookingDetail().getWaterQuantity());
		waterTankerDetail.setDescription(waterTankerRequest.getWaterTankerBookingDetail().getDescription());
		waterTankerDetail.setDeliveryDate(waterTankerRequest.getWaterTankerBookingDetail().getDeliveryDate());
		waterTankerDetail.setDeliveryTime(waterTankerRequest.getWaterTankerBookingDetail().getDeliveryTime());
		String roles = waterTankerRequest.getRequestInfo().getUserInfo().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
		waterTankerDetail.setBookingCreatedBy(roles);
		
		waterTankerDetail.getApplicantDetail().setBookingId(bookingId);
		waterTankerDetail.getApplicantDetail().setApplicantId(RequestServiceUtil.getRandonUUID());
		waterTankerDetail.getApplicantDetail().setAuditDetails(auditDetails);
	

		waterTankerDetail.getAddress().
			setAddressId(RequestServiceUtil.getRandonUUID());
		waterTankerDetail.getAddress().setApplicantId(waterTankerDetail.getApplicantDetail().getApplicantId());
		
		log.info("Enriched application request data :" + waterTankerDetail);

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
			throw new CustomException("IDGEN_ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	


}
