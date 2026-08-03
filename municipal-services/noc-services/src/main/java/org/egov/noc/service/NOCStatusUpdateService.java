package org.egov.noc.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.web.model.*;
import org.egov.noc.web.model.aai.AAIApplicationStatus;
import org.egov.noc.web.model.aai.AAIStatusResponse;
import org.egov.noc.workflow.WorkflowIntegrator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

/**
 * Service for updating NOC statuses based on AAI responses
 */
@Slf4j
@Service
public class NOCStatusUpdateService {

    @Autowired
    private AAINOCASIntegrationService aaiIntegrationService;

    @Autowired
    private org.egov.noc.repository.NOCRepository nocRepository;

    @Autowired
    private WorkflowIntegrator wfIntegrator;

    @Autowired
    private FileStoreService fileStoreService;

    /**
     * Updates NOC statuses based on AAI response
     * Iterates through each AAI application status data object and updates the corresponding NOC
     *
     * 
     * @param aaiResponse AAI response
     * @param requestInfo Request info
     * @return Updated NOC list
     */
    public List<Noc> updateNOCStatusFromAAI(AAIStatusResponse aaiResponse, RequestInfo requestInfo) {
        if (aaiResponse == null || !aaiResponse.getSuccess() || CollectionUtils.isEmpty(aaiResponse.getApplicationStatuses())) {
            return new ArrayList<>();
        }

        List<Noc> updatedNocs = new ArrayList<>();
        for (AAIApplicationStatus aaiStatus : aaiResponse.getApplicationStatuses()) {
            try {
                Noc updatedNoc = updateSingleNOCStatus(aaiStatus, requestInfo);
                if (updatedNoc != null) {
                    updatedNocs.add(updatedNoc);
                }
            } catch (Exception e) {
                log.error("AAI sync: failed to update {}", aaiStatus.getUniqueId(), e);
            }
        }
        log.info("AAI sync: updated {} applications", updatedNocs.size());
        return updatedNocs;
    }

    /**
     * Updates single NOC status
     * Get the NOC by uniqueId, map AAI status to NOC status, and update if different
     * For INPROCESS status, only update additionalDetails and nocNo without changing applicationStatus
     * For other statuses, determine workflow action and execute status update
     * 
     * @param aaiStatus AAI status
     * @param requestInfo Request info
     * @return Updated NOC
     */
    private Noc updateSingleNOCStatus(AAIApplicationStatus aaiStatus, RequestInfo requestInfo) {
        String uniqueId = aaiStatus.getUniqueId();
        
        NocSearchCriteria searchCriteria = NocSearchCriteria.builder()
                .sourceRefId(uniqueId.trim())
                .nocType(NOCConstants.CIVIL_AVIATION_NOC_TYPE)
                .build();

        List<Noc> nocs = nocRepository.getNocData(searchCriteria);
        if (CollectionUtils.isEmpty(nocs)) {
            log.warn("AAI sync: CIVIL_AVIATION NOC not found for {}", uniqueId);
            return null;
        }
        Noc existingNoc = nocs.get(0);

        String aaiStatusValue = aaiStatus.getStatus();
        String newNocStatus = aaiIntegrationService.mapAAIStatusToNOCStatus(aaiStatusValue);
        
        if (aaiStatus.getNocasId() != null && !aaiStatus.getNocasId().trim().isEmpty()) {
            existingNoc.setNocNo(aaiStatus.getNocasId());
        }
        
        if (NOCConstants.AAI_STATUS_INPROCESS.equalsIgnoreCase(aaiStatusValue)) {
            log.info("NOC {} is INPROCESS - updating applicationStatus to INPROCESS, additionalDetails and nocNo", uniqueId);
            updateAdditionalDetailsFromAAI(existingNoc, aaiStatus);
            // Update applicationStatus to INPROCESS
            existingNoc.setApplicationStatus(NOCConstants.AAI_STATUS_INPROCESS);  
            NocRequest nocRequest = NocRequest.builder()
                    .noc(existingNoc)
                    .requestInfo(requestInfo)
                    .build();
            nocRepository.update(nocRequest, false);
            return existingNoc;
        }
        
        if (newNocStatus.equals(existingNoc.getApplicationStatus())) {
            log.debug("NOC {} already has status {}, updating nocNo and additionalDetails", uniqueId, newNocStatus);
            updateAdditionalDetailsFromAAI(existingNoc, aaiStatus);
            NocRequest nocRequest = NocRequest.builder()
                    .noc(existingNoc)
                    .requestInfo(requestInfo)
                    .build();
            nocRepository.update(nocRequest, false);
            return existingNoc;
        }
        String workflowAction = determineWorkflowAction(newNocStatus);
        return executeNOCStatusUpdate(existingNoc, aaiStatus, workflowAction, newNocStatus, requestInfo);
    }

    /**
     * Executes NOC status update with or without workflow
     * For ISSUED and AutoSettled statuses, also downloads and saves the approval document
     * 
     * @param existingNoc NOC application
     * @param aaiStatus AAI status
     * @param workflowAction Workflow action (null if no workflow should be called)
     * @param newNocStatus New NOC status to set
     * @param requestInfo Request info
     * @return Updated NOC
     */
    private Noc executeNOCStatusUpdate(Noc existingNoc, AAIApplicationStatus aaiStatus, 
                                     String workflowAction, String newNocStatus, RequestInfo requestInfo) {
        updateAdditionalDetailsFromAAI(existingNoc, aaiStatus);
        
        String aaiStatusValue = aaiStatus.getStatus();
        if ((NOCConstants.AAI_STATUS_ISSUED.equalsIgnoreCase(aaiStatusValue) 
                || NOCConstants.AAI_STATUS_AUTOSETTLED.equalsIgnoreCase(aaiStatusValue))
                && StringUtils.hasText(aaiStatus.getFileName())) {
            
            try {
                String fileStoreId = fileStoreService.uploadFileFromUrlToFileStore(
                        aaiStatus.getFileName(), 
                        existingNoc.getTenantId(), 
                        NOCConstants.NOC_MODULE);
                
                if (StringUtils.hasText(fileStoreId)) {
                    Document aaiDocument = Document.builder()
                            .id(UUID.randomUUID().toString())
                            .documentType(NOCConstants.DOC_TYPE_AAI_NOC_APPROVAL)
                            .fileStoreId(fileStoreId)
                            .documentUid(fileStoreId)
                            .build();
                    
                    if (existingNoc.getDocuments() == null) {
                        existingNoc.setDocuments(new ArrayList<>());
                    }
                    existingNoc.getDocuments().add(aaiDocument);
                    log.info("Added AAI NOC approval document to NOC {} for saving through update flow", existingNoc.getId());
                } else {
                    log.warn("Failed to upload AAI document for NOC {}, fileName: {}", 
                            existingNoc.getId(), aaiStatus.getFileName());
                }
            } catch (Exception e) {
                log.error("Failed to save AAI document for NOC {}", existingNoc.getId(), e);
            }
        }
        
        NocRequest nocRequest = NocRequest.builder()
                .noc(existingNoc)
                .requestInfo(requestInfo)
                .build();
        if (workflowAction != null) {
            Workflow workflow = Workflow.builder().action(workflowAction).build();
            String comment = "Status updated from AAI: " + 
                    (aaiStatus.getRemark() != null ? aaiStatus.getRemark() : aaiStatus.getStatus());
            workflow.setComment(comment);
            existingNoc.setWorkflow(workflow);
            wfIntegrator.callWorkFlow(nocRequest, NOCConstants.CIVIL_NOC_WORKFLOW_CODE);
        }
            existingNoc.setApplicationStatus(newNocStatus);
            existingNoc.setWorkflow(null);
            nocRepository.update(nocRequest, true);
        return existingNoc;
    }

    /**
     * Updates NOC additionalDetails with AAI status data
     * @param noc NOC application
     * @param aaiStatus AAI status
    * */
    private void updateAdditionalDetailsFromAAI(Noc noc, AAIApplicationStatus aaiStatus) {
        @SuppressWarnings("unchecked")
        Map<String, Object> additionalDetails = noc.getAdditionalDetails() != null ? 
                (Map<String, Object>) noc.getAdditionalDetails() : new HashMap<>();

        Map<String, Object> aaiData = new HashMap<>();
        aaiData.put("NOCASID", aaiStatus.getNocasId());
        aaiData.put("UNIQUEID", aaiStatus.getUniqueId());
        aaiData.put("AuthorityName", aaiStatus.getAuthorityName());
        aaiData.put("STATUS", aaiStatus.getStatus());
        aaiData.put("PTE", aaiStatus.getPte());
        aaiData.put("ISSUEDATE", aaiStatus.getIssueDate());
        aaiData.put("AirportName", aaiStatus.getAirportName());
        aaiData.put("FILENAME", aaiStatus.getFileName());
        aaiData.put("ActionType", aaiStatus.getActionType());
        aaiData.put("QueryType", aaiStatus.getQueryType());
        aaiData.put("SearchType", aaiStatus.getSearchType());
        aaiData.put("ErrorCode", aaiStatus.getErrorCode());
        aaiData.put("Message", aaiStatus.getMessage());
        aaiData.put("Status", aaiStatus.getStatusFlag());
        aaiData.put("aaiLastUpdated", Instant.now().toEpochMilli());

        String aaiStatusValue = aaiStatus.getStatus();
        if (!NOCConstants.AAI_STATUS_INPROCESS.equalsIgnoreCase(aaiStatusValue) && aaiStatus.getRemark() != null) {
            aaiData.put("REMARK", aaiStatus.getRemark());
        }
        
        if (NOCConstants.AAI_STATUS_REJECTED.equalsIgnoreCase(aaiStatusValue) || 
            NOCConstants.AAI_STATUS_VERIFICATIONREJECTED.equalsIgnoreCase(aaiStatusValue)) {
            aaiData.put("aaiRejectionRemarks", aaiStatus.getRemark());
        }

        additionalDetails.put("aaiData", aaiData);
        noc.setAdditionalDetails(additionalDetails);
    }

    /**
     * Determines workflow action based on status
     *
     * @param newStatus New status
     * @return Workflow action
     */
    private String determineWorkflowAction(String newStatus) {
        if (NOCConstants.APPROVED_STATE.equals(newStatus)) {
            return NOCConstants.ACTION_APPROVE;
        }
        
        if (NOCConstants.APPLICATION_STATUS_REJECTED.equals(newStatus)) {
            return NOCConstants.ACTION_REJECT;
        }
        
        return null;
    }
}

