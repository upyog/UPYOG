package org.upyog.sv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.enums.VendorRelationshipType;
import org.upyog.sv.repository.IdGenRepository;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.common.AuditDetails;
import org.upyog.sv.web.models.idgen.IdResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private StreetVendingConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;

	public void enrichCreateStreetVendingRequest(StreetVendingRequest vendingRequest) {
		String applicationId = StreetVendingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + applicationId);

		StreetVendingDetail streetVendingDetail = vendingRequest.getStreetVendingDetail();
		RequestInfo requestInfo = vendingRequest.getRequestInfo();
		AuditDetails auditDetails = StreetVendingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

		streetVendingDetail.setApplicationId(applicationId);
		streetVendingDetail.setAuditDetails(auditDetails);
		streetVendingDetail.setApplicationDate(auditDetails.getCreatedTime());
		// streetVendingDetail.setApplicationStatus(streetVendingDetail.getApplicationStatus());

		List<String> customIds = getIdList(requestInfo, streetVendingDetail.getTenantId(),
				config.getStreetVendingApplicationKey(), config.getStreetVendingApplicationFormat(), 1);

		log.info("Enriched application request application no :" + customIds.get(0));

		streetVendingDetail.setApplicationNo(customIds.get(0));

		// Updating id in documents
		streetVendingDetail.getDocumentDetails().stream().forEach(document -> {
			document.setApplicationId(applicationId);
			document.setDocumentDetailId(StreetVendingUtil.getRandonUUID());
			document.setAuditDetails(auditDetails);
		});

		String vendorId = StreetVendingUtil.getRandonUUID();

		streetVendingDetail.getVendorDetail().stream().forEach(vendorDetail -> {
			if (!vendorDetail.getRelationshipType().equals(VendorRelationshipType.VENDOR.toString())) {
				vendorDetail.setVendorId(vendorId);
				vendorDetail.setId(StreetVendingUtil.getRandonUUID());
			} else {
				vendorDetail.setId(vendorId);
			}
			vendorDetail.setApplicationId(applicationId);
			vendorDetail.setAuditDetails(auditDetails);
		});

		streetVendingDetail.getAddressDetails().stream().forEach(address -> {
			address.setAddressId(StreetVendingUtil.getRandonUUID());
			address.setVendorId(vendorId);
		});

		streetVendingDetail.getBankDetail().setId(StreetVendingUtil.getRandonUUID());
		streetVendingDetail.getBankDetail().setApplicationId(applicationId);
		streetVendingDetail.getBankDetail().setAuditDetails(auditDetails);

		// Updating id and status for vending operation details
		streetVendingDetail.getVendingOperationTimeDetails().stream().forEach(timedetails -> {
			timedetails.setApplicationId(applicationId);
			timedetails.setId(StreetVendingUtil.getRandonUUID());
		});
		
		log.info("Enriched application request data :" + streetVendingDetail);

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
