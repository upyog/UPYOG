package org.upyog.cdwm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
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

@Service
@Slf4j
public class EnrichmentService {

    @Autowired
    private CNDConfiguration config;

    @Autowired
    private IdGenRepository idGenRepository;

    public void enrichCreateCNDRequest(CNDApplicationRequest cndApplicationRequest) {
        String applicationId = CNDServiceUtil.getRandonUUID();
        log.info("Enriching CND application id: " + applicationId);

        CNDApplicationDetail cndApplicationDetails = cndApplicationRequest.getCndApplication();
        RequestInfo requestInfo = cndApplicationRequest.getRequestInfo();
        AuditDetails auditDetails = CNDServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

        cndApplicationDetails.setApplicationId(applicationId);
        cndApplicationDetails.setApplicationStatus(CNDStatus.valueOf(cndApplicationDetails.getApplicationStatus()).toString());
        cndApplicationDetails.setAuditDetails(auditDetails);
        cndApplicationDetails.setTenantId(cndApplicationRequest.getCndApplication().getTenantId());

        List<String> customIds = getIdList(requestInfo, cndApplicationDetails.getTenantId(),
                config.getCNDApplicationKey(), config.getCNDApplicationFormat(), 1);

        log.info("Enriched application request application no: " + customIds.get(0));

        cndApplicationDetails.setApplicationNumber(customIds.get(0)); 

        cndApplicationDetails.setDescription(cndApplicationRequest.getCndApplication().getDescription());
        cndApplicationDetails.setLocation(cndApplicationRequest.getCndApplication().getLocation());
        cndApplicationDetails.setWasteType(cndApplicationRequest.getCndApplication().getWasteType());
        cndApplicationDetails.setQuantity(cndApplicationRequest.getCndApplication().getQuantity());
        
        log.info("Enriched application request data: " + cndApplicationDetails);
    }

    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
                .getIdResponses();

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN_ERROR", "No ids returned from idgen Service");

        return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
    }

    public void enrichCNDApplicationUponUpdate(String applicationStatus, CNDApplicationRequest cndApplicationRequest) {
        CNDApplicationDetail cndApplicationDetails = cndApplicationRequest.getCndApplication();
        cndApplicationDetails.setAuditDetails(CNDServiceUtil.getAuditDetails(cndApplicationRequest.getRequestInfo().getUserInfo().getUuid(), false));
        cndApplicationDetails.setApplicationStatus(applicationStatus);
    }
}