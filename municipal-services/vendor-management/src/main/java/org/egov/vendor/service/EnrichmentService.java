package org.egov.vendor.service;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.vendor.repository.IdGenRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.vendor.utils.VendorErrorConstants;
import org.egov.vendor.utils.VendorUtil;
import org.egov.vendor.web.models.AuditDetails;
import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.egov.vendor.web.models.VendorAdditionalDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnrichmentService {

    @Autowired
    private VendorConfiguration config;

    @Autowired
    private VendorUtil assetUtil;

    @Autowired
    private IdGenRepository idGenRepository;

    /**
     * Enriches the Asset create request by adding audit details and unique identifiers (UUIDs).
     *
     * @param request The request object containing asset details to be enriched.
     * @param mdmsData     Master data required for enrichment.
     */
    public void enrichAssetCreateRequest(VendorAdditionalDetailsRequest request, Object mdmsData) {
        log.info("Enriching Asset Create Request");
        RequestInfo requestInfo = request.getRequestInfo();

        // Set audit details for the asset
        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        request.getVendorAdditionalDetails().setAuditDetails(auditDetails);
        request.getVendorAdditionalDetails().setVendorAdditionalDetailsId(UUID.randomUUID().toString());


        // Enrich documents with unique identifiers if present
        if (!CollectionUtils.isEmpty(request.getVendorAdditionalDetails().getDocuments())) {
            request.getVendorAdditionalDetails().getDocuments().forEach(document -> {
                if (document.getDocumentId() == null) {
                    document.setDocumentId(UUID.randomUUID().toString());
                    document.setDocumentUid(UUID.randomUUID().toString());
                    document.setVendorAdditionalDetailsId(request.getVendorAdditionalDetails().getVendorAdditionalDetailsId());
                }
            });
        }

        // Generate and set ID generation fields for the asset
        setIdgenIds(request);
    }

    /**
     * Enriches Asset assignment update requests by adding audit details.
     *
     * @param request The request object containing asset assignment update details.
     */
    public void enrichAssetOtherOperationsUpdateRequest(VendorAdditionalDetailsRequest request) {
        log.info("Enriching Asset Assignment Update Request");
        RequestInfo requestInfo = request.getRequestInfo();

        // Set audit details for the asset assignment
        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
        request.getVendorAdditionalDetails().setAuditDetails(auditDetails);
    }

    /**
     * Generates and sets application numbers for the given Asset request using the ID generation service.
     *
     * @param request The Asset request for which application numbers need to be generated.
     */
    private void setIdgenIds(VendorAdditionalDetailsRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getVendorAdditionalDetails().getTenantId();
        VendorAdditionalDetails vendorAdditionalDetails = request.getVendorAdditionalDetails();

        // Generate application numbers using ID generation service
        List<String> applicationNumbers = getIdList(requestInfo, tenantId, config.getApplicationNoIdgenName(),
                config.getApplicationNoIdgenFormat(), 1);

        if (applicationNumbers.isEmpty()) {
            throw new CustomException(VendorErrorConstants.IDGEN_ERROR, "No IDs returned from ID generation service");
        }

        // Set the application number for the asset
        request.getVendorAdditionalDetails().setRegistrationNo(applicationNumbers.get(0));
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
            throw new CustomException(VendorErrorConstants.IDGEN_ERROR, "No IDs returned from ID generation service");
        }

        // Extract and return IDs from the response
        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    /**
     * Enriches the Asset update request by adding audit details.
     *
     * @param request The request object containing asset details to be updated.
     * @param mdmsData     Master data required for enrichment.
     */
    public void enrichAssetUpdateRequest(VendorAdditionalDetailsRequest request, Object mdmsData) {
        RequestInfo requestInfo = request.getRequestInfo();

        // Set audit details for the asset
        AuditDetails auditDetails = assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);
        request.getVendorAdditionalDetails().setAuditDetails(auditDetails);
    }


    /**
     * Enriches other Asset operations (e.g., assignment, disposal) by adding audit details and unique identifiers.
     *
     * @param requestInfo The request object containing asset operation details to be enriched.
     */
    public AuditDetails enrichOtherOperations(RequestInfo requestInfo) {
        log.info("Enriching Other Operations Request");
        // Set audit details for the asset assignment
        return assetUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);
    }


}
