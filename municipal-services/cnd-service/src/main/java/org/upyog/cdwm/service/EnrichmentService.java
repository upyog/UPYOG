package org.upyog.cdwm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.enums.CNDStatus;
import org.upyog.cdwm.repository.IdGenRepository;
import org.upyog.cdwm.util.CNDServiceUtil;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;

import digit.models.coremodels.AuditDetails;
import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Service class responsible for enriching CND application requests.
 */
@Service
@Slf4j
public class EnrichmentService {

    @Autowired
    private CNDConfiguration config;

    @Autowired
    private IdGenRepository idGenRepository;

    /**
     * Enriches the CND application request with necessary details such as ID, status, and audit information.
     * 
     * @param cndApplicationRequest the application request to enrich
     */
    public void enrichCreateCNDRequest(CNDApplicationRequest cndApplicationRequest) {
        String applicationId = CNDServiceUtil.getRandonUUID();
        log.info("Enriching CND application with ID: {}", applicationId);

        CNDApplicationDetail cndApplicationDetails = cndApplicationRequest.getCndApplication();
        RequestInfo requestInfo = cndApplicationRequest.getRequestInfo();
        AuditDetails auditDetails = CNDServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

        cndApplicationDetails.setApplicationId(applicationId);
        cndApplicationDetails.setApplicationStatus(CNDStatus.valueOf(cndApplicationDetails.getApplicationStatus()).toString());
        cndApplicationDetails.setAuditDetails(auditDetails);
        cndApplicationDetails.setTenantId(cndApplicationDetails.getTenantId());

        List<String> customIds = getIdList(requestInfo, cndApplicationDetails.getTenantId(),
                config.getCNDApplicationKey(), config.getCNDApplicationFormat(), 1);

        log.info("Generated Application Number: {}", customIds.get(0));
        cndApplicationDetails.setApplicationNumber(customIds.get(0));
    }

    /**
     * Fetches a list of generated IDs for the application.
     * 
     * @param requestInfo Request metadata
     * @param tenantId    Tenant ID
     * @param idKey       Key for ID generation
     * @param idFormat    ID format
     * @param count       Number of IDs required
     * @return List of generated IDs
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idFormat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idFormat, count).getIdResponses();

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException("IDGEN_ERROR", "No IDs returned from ID generation service");
        }

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    /**
     * Updates the CND application request with the new status and audit details.
     * 
     * @param applicationStatus     New application status
     * @param cndApplicationRequest CND application request to update
     */
    public void enrichCNDApplicationUponUpdate(String applicationStatus, CNDApplicationRequest cndApplicationRequest) {
        CNDApplicationDetail cndApplicationDetails = cndApplicationRequest.getCndApplication();
        cndApplicationDetails.setAuditDetails(CNDServiceUtil.getAuditDetails(
                cndApplicationRequest.getRequestInfo().getUserInfo().getUuid(), false));
        cndApplicationDetails.setApplicationStatus(applicationStatus);
    }
}
