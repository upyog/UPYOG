package org.egov.asset.service;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.IdGenRepository;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.web.models.Asset;
import org.egov.asset.web.models.AssetRequest;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.maintenance.AssetMaintenanceRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class EnrichmentService {

    private static final String ENRICHING_OTHER_OPERATIONS_REQUEST = "Enriching Other Operations Request";

    private final AssetConfiguration config;
    private final AssetUtil assetUtil;
    private final IdGenRepository idGenRepository;

    public EnrichmentService(AssetConfiguration config, AssetUtil assetUtil, IdGenRepository idGenRepository) {
        this.config = config;
        this.assetUtil = assetUtil;
        this.idGenRepository = idGenRepository;
    }

    /**
     * Enriches the Asset create request by adding audit details and unique identifiers (UUIDs).
     *
     * @param assetRequest The request object containing asset details to be enriched.
     * @param mdmsData     Master data required for enrichment.
     */
    public void enrichAssetCreateRequest(AssetRequest assetRequest, Object mdmsData) {
        log.info("Enriching Asset Create Request");
        RequestInfo requestInfo = assetRequest.getRequestInfo();

        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        assetRequest.getAsset().setAuditDetails(auditDetails);
        assetRequest.getAsset().setId(UUID.randomUUID().toString());
        assetRequest.getAsset().setAccountId(assetRequest.getAsset().getAuditDetails().getCreatedBy());

        if (!CollectionUtils.isEmpty(assetRequest.getAsset().getDocuments())) {
            assetRequest.getAsset().getDocuments().forEach(document -> {
                if (document.getDocumentId() == null) {
                    document.setDocumentId(UUID.randomUUID().toString());
                    document.setDocumentUid(UUID.randomUUID().toString());
                }
            });
        }

        if (assetRequest.getAsset().getAddressDetails() != null) {
            assetRequest.getAsset().getAddressDetails().setAddressId(UUID.randomUUID().toString());
        }

        setIdgenIds(assetRequest);
    }

    /**
     * Enriches other Asset operations (e.g., assignment, disposal) by adding audit details and unique identifiers.
     *
     * @param assetRequest The request object containing asset operation details to be enriched.
     */
    public void enrichAssetOtherOperationsCreateRequest(AssetRequest assetRequest) {
        log.info("Enriching Other Asset Operations Request");
        RequestInfo requestInfo = assetRequest.getRequestInfo();

        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        assetRequest.getAsset().getAssetAssignment().setAuditDetails(auditDetails);
        assetRequest.getAsset().getAssetAssignment().setAssignmentId(UUID.randomUUID().toString());
    }

    /**
     * Enriches Asset assignment update requests by adding audit details.
     *
     * @param assetRequest The request object containing asset assignment update details.
     */
    public void enrichAssetOtherOperationsUpdateRequest(AssetRequest assetRequest) {
        log.info("Enriching Asset Assignment Update Request");
        RequestInfo requestInfo = assetRequest.getRequestInfo();

        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        assetRequest.getAsset().getAssetAssignment().setAuditDetails(auditDetails);
    }

    /**
     * Generates and sets application numbers for the given Asset request using the ID generation service.
     *
     * @param request The Asset request for which application numbers need to be generated.
     */
    private void setIdgenIds(AssetRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getAsset().getTenantId();
        Asset asset = request.getAsset();

        List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNoIdgenName(),
                config.getApplicationNoIdgenFormat(), 1);

        if (applicationNumbers.isEmpty()) {
            throw new CustomException(AssetErrorConstants.IDGEN_ERROR, "No IDs returned from ID generation service");
        }

        asset.setApplicationNo(AssetUtil.improveAssetID(applicationNumbers.get(0), request));
    }

    /**
     * Fetches a list of IDs from the ID generation service.
     *
     * @param requestInfo Request information for the service call.
     * @param tenantId    Tenant ID for which IDs are generated.
     * @param idKey       Key used for ID generation.
     * @param idformat    Format of the IDs to be generated.
     * @param count       Number of IDs to generate.
     * @return A list of generated IDs.
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
                .getIdResponses();

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(AssetErrorConstants.IDGEN_ERROR, "No IDs returned from ID generation service");
        }

        return idResponses.stream().map(IdResponse::getId).toList();
    }

    /**
     * Enriches the Asset update request by adding audit details.
     *
     * @param assetRequest The request object containing asset details to be updated.
     * @param mdmsData     Master data required for enrichment.
     */
    public void enrichAssetUpdateRequest(AssetRequest assetRequest, Object mdmsData) {
        RequestInfo requestInfo = assetRequest.getRequestInfo();
        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        assetRequest.getAsset().setAuditDetails(auditDetails);
    }

    /**
     * Enriches other Asset operations (e.g., assignment, disposal) by adding audit details and unique identifiers.
     *
     * @param requestInfo The request object containing asset operation details to be enriched.
     */
    public AuditDetails enrichOtherOperations(RequestInfo requestInfo) {
        log.info(ENRICHING_OTHER_OPERATIONS_REQUEST);
        return assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
    }

    /**
     * Enriches other Asset operations (e.g., assignment, disposal) by adding audit details and unique identifiers.
     *
     * @param request The AssetDisposalRequest object containing asset operation details to be enriched.
     */
    public void enrichDisposalCreateOperations(AssetDisposalRequest request) {
        log.info(ENRICHING_OTHER_OPERATIONS_REQUEST);
        AuditDetails auditDetails = assetUtil.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), true);

        if (!CollectionUtils.isEmpty(request.getAssetDisposal().getDocuments())) {
            request.getAssetDisposal().getDocuments().forEach(document -> {
                if (document.getDocumentId() == null) {
                    document.setDocumentId(UUID.randomUUID().toString());
                    document.setDocumentUid(UUID.randomUUID().toString());
                }
            });
        }
        request.getAssetDisposal().setAuditDetails(auditDetails);
        request.getAssetDisposal().setDisposalId(UUID.randomUUID().toString());
    }

    /**
     * Enriches other Asset operations (e.g., assignment, disposal) by adding audit details and unique identifiers.
     *
     * @param request The AssetDisposalRequest object containing asset operation details to be enriched.
     */
    public void enrichDisposalUpdateOperations(AssetDisposalRequest request) {
        log.info(ENRICHING_OTHER_OPERATIONS_REQUEST);
        AuditDetails auditDetails = assetUtil.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), false);

        if (!CollectionUtils.isEmpty(request.getAssetDisposal().getDocuments())) {
            request.getAssetDisposal().getDocuments().forEach(document -> {
                if (document.getDocumentId() == null) {
                    document.setDocumentId(UUID.randomUUID().toString());
                    document.setDocumentUid(UUID.randomUUID().toString());
                }
            });
        }

        request.getAssetDisposal().setAuditDetails(auditDetails);
    }

    /**
     * Enriches Asset Maintenance creation request by adding audit details and unique identifiers.
     *
     * @param request The AssetMaintenanceRequest object containing maintenance details to be enriched.
     */
    public void enrichMaintenanceCreateOperations(AssetMaintenanceRequest request) {
        log.info("Enriching Asset Maintenance Create Request");

        AuditDetails auditDetails = assetUtil.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), true);

        if (!CollectionUtils.isEmpty(request.getAssetMaintenance().getDocuments())) {
            request.getAssetMaintenance().getDocuments().forEach(document -> {
                if (document.getDocumentId() == null) {
                    document.setDocumentId(UUID.randomUUID().toString());
                    document.setDocumentUid(UUID.randomUUID().toString());
                }
            });
        }

        request.getAssetMaintenance().setAuditDetails(auditDetails);
        request.getAssetMaintenance().setMaintenanceId(UUID.randomUUID().toString());
    }

    /**
     * Enriches Asset Maintenance update request by adding audit details.
     *
     * @param request The AssetMaintenanceRequest object containing maintenance details to be enriched.
     */
    public void enrichMaintenanceUpdateOperations(AssetMaintenanceRequest request) {
        log.info("Enriching Asset Maintenance Update Request");

        AuditDetails auditDetails = assetUtil.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), false);
        request.getAssetMaintenance().setAuditDetails(auditDetails);
    }
}
