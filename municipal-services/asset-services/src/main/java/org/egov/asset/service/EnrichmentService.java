package org.egov.asset.service;

import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.AssetRepository;
import org.egov.asset.repository.IdGenRepository;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import digit.models.coremodels.IdResponse;
//import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private AssetConfiguration config;

	@Autowired
	private AssetUtil assetUtil;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	AssetRepository assetRepository;

	// @Autowired
	// private WorkflowService workflowService;

	/**
	 * encrich create Asset Reqeust by adding auditdetails and uuids
	 * 
	 * @param assetRequest
	 * @param mdmsData
	 * @param values
	 */
	// public void enrichAssetCreateRequest(AssetRequest assetRequest, Object
	// mdmsData)
	public void enrichAssetCreateRequest(AssetRequest assetRequest) {
		log.info("Doing EnrichAssetCreateRequest");
		RequestInfo requestInfo = assetRequest.getRequestInfo();
		AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		assetRequest.getAsset().setAuditDetails(auditDetails);
		assetRequest.getAsset().setId(UUID.randomUUID().toString());
		String[] tenantSpilt = assetRequest.getAsset().getTenantId().split("\\.");
		String tenantId = tenantSpilt[1];
		assetRequest.getAsset().setAccountId(assetRequest.getAsset().getAuditDetails().getCreatedBy());
		// String applicationType = values.get(AssetConstants.ASSET_PARENT_CATEGORY);

		if (StringUtils.isEmpty(assetRequest.getAsset().getAssetClassification())) {
			throw new CustomException("ASSET_CLASSIFICATION_NULL", "Asset Classification cannot be empty!!!");
		}
		if (StringUtils.equalsIgnoreCase(assetRequest.getAsset().getAssetClassification(), "MOVABLE")) {
			String name = assetRequest.getAsset().getAssetClassification().substring(0, 3).toUpperCase();
			assetRequest.getAsset()
					.setApplicationNo(name + "-" + tenantId + "-" + assetRepository.getNextassetApplicationSequence());
		}
		if (StringUtils.equalsIgnoreCase(assetRequest.getAsset().getAssetClassification(), "IMMOVABLE")) {
			String name = assetRequest.getAsset().getAssetClassification().substring(0, 5).toUpperCase();
			assetRequest.getAsset()
					.setApplicationNo(name + "-" + tenantId + "-" + assetRepository.getNextassetApplicationSequence());
		}

		// Asset Documents
		if (!CollectionUtils.isEmpty(assetRequest.getAsset().getDocuments()))
			assetRequest.getAsset().getDocuments().forEach(document -> {
				if (document.getDocumentId() == null) {
					String uuid = UUID.randomUUID().toString();
					document.setDocumentId(uuid);
					String docUuid = UUID.randomUUID().toString();
					document.setDocumentUid(docUuid);
				}
			});

		// Asset AddressDetails
		if (assetRequest.getAsset().getAddressDetails() != null) {
			String uuid = UUID.randomUUID().toString();
			assetRequest.getAsset().getAddressDetails().setAddressId(uuid);
		}

		// setIdgenIds(assetRequest);
	}

	/**
	 * encrich Asset other operation assignment Request like assignment, desposal
	 * etc by adding auditdetails and uuids
	 * 
	 * @param assetRequest
	 * @param mdmsData
	 * @param values
	 */
	public void enrichAssetOtherOperationsCreateRequest(AssetRequest assetRequest) {
		log.info("Doing EnrichAssetOtherOperationsRequest");
		RequestInfo requestInfo = assetRequest.getRequestInfo();
		AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		assetRequest.getAsset().getAssetAssignment().setAuditDetails(auditDetails);
		assetRequest.getAsset().getAssetAssignment().setAssignmentId(UUID.randomUUID().toString());

	}

	public void enrichAssetOtherOperationsUpdateRequest(AssetRequest assetRequest) {
		log.info("Doing EnrichAssetOtherOperationsRequest");
		RequestInfo requestInfo = assetRequest.getRequestInfo();
		AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		assetRequest.getAsset().getAssetAssignment().setAuditDetails(auditDetails);

	}

	/**
	 * Sets the ApplicationNumber for given bpaRequest
	 *
	 * @param request bpaRequest which is to be created
	 */
	private void setIdgenIds(AssetRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		String tenantId = request.getAsset().getTenantId();
		Asset asset = request.getAsset();

		List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNoIdgenName(),
				config.getApplicationNoIdgenFormat(), 1);
		ListIterator<String> itr = applicationNumbers.listIterator();

		Map<String, String> errorMap = new HashMap<>();

		if (!errorMap.isEmpty())
			throw new CustomException(errorMap);

		asset.setApplicationNo(AssetUtil.improveAssetID(itr.next(), request));
	}

	/**
	 * Returns a list of numbers generated from idgen
	 *
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idKey       code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated // count not used at present
	 * @return List of ids generated using idGen service
	 */
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException(AssetErrorConstants.IDGEN_ERROR, "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	}

	/**
	 * encrich update Asset Reqeust by adding auditdetails and uuids
	 * 
	 * @param assetRequest
	 * @param mdmsData
	 * @param values
	 */
	public void enrichAssetUpdateRequest(AssetRequest assetRequest, Object mdmsData) {
		RequestInfo requestInfo = assetRequest.getRequestInfo();
		AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
		assetRequest.getAsset().setAuditDetails(auditDetails);
		// assetRequest.getAsset().getAuditDetails().setLastModifiedBy(auditDetails.getLastModifiedBy());
		// assetRequest.getAsset().getAuditDetails().setLastModifiedTime(auditDetails.getLastModifiedTime());

	}

}