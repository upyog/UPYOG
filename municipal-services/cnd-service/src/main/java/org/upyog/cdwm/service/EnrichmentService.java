package org.upyog.cdwm.service;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
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
import org.upyog.cdwm.web.models.DocumentDetail;
import org.upyog.cdwm.web.models.FacilityCenterDetail;
import org.upyog.cdwm.web.models.WasteTypeDetail;

import digit.models.coremodels.AuditDetails;
import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;
import org.upyog.cdwm.web.models.user.Address;
import org.upyog.cdwm.web.models.user.User;
import org.upyog.cdwm.web.models.user.enums.AddressType;

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

    @Autowired
    private UserService userService;

    /**
     * Enriches the CND application request with necessary details such as application ID,
     * user information, audit details, and generated application number.
     *
     * @param cndApplicationRequest The request object containing CND application details.
     */
    public void enrichCreateCNDRequest(CNDApplicationRequest cndApplicationRequest) {
        String applicationId = CNDServiceUtil.getRandomUUID();
        log.info("Enriching CND application with ID: {}", applicationId);

        CNDApplicationDetail cndApplicationDetails = cndApplicationRequest.getCndApplication();
        RequestInfo requestInfo = cndApplicationRequest.getRequestInfo();
        AuditDetails auditDetails = CNDServiceUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

//        Checks the conditions for Applicant ID and Address ID in the application details object
//          and invokes appropriate methods to enrich the user details or address details
        if (StringUtils.isBlank(cndApplicationDetails.getAddressDetailId())) {
            if (StringUtils.isBlank(cndApplicationDetails.getApplicantDetailId())) {
                // Both Applicant ID and Address ID are null or blank; enrich user details with address.
                enrichUserDetails(cndApplicationRequest, cndApplicationDetails);
            } else {
                // Applicant ID is present but Address ID is null or blank; enrich address details only.
                enrichAddressDetails(cndApplicationRequest, cndApplicationDetails);
            }
        }

        // Set application details
        cndApplicationDetails.setApplicationId(applicationId);
        cndApplicationDetails.setApplicationStatus(CNDStatus.valueOf(cndApplicationDetails.getApplicationStatus()).toString());
        cndApplicationDetails.setAuditDetails(auditDetails);
        cndApplicationDetails.setTenantId(cndApplicationDetails.getTenantId());

        // Copy mobile number from applicant details to a separate field to save in application table
        cndApplicationDetails.setApplicantMobileNumber(cndApplicationDetails.getApplicantDetail().getMobileNumber());
       
        
        List<WasteTypeDetail> wasteTypeDetails = cndApplicationDetails.getWasteTypeDetails();
        if (wasteTypeDetails != null) {
            for (WasteTypeDetail wasteTypeDetail : wasteTypeDetails) {
                String wasteTypeId = CNDServiceUtil.getRandomUUID(); 
                wasteTypeDetail.setWasteTypeId(wasteTypeId);
                wasteTypeDetail.setApplicationId(applicationId);
            }
        }
        
        FacilityCenterDetail facilityCentreDetail = cndApplicationDetails.getFacilityCenterDetail();
        if (facilityCentreDetail != null) { 	
        	String disposalId = CNDServiceUtil.getRandomUUID();
        	facilityCentreDetail.setDisposalId(disposalId);
        	facilityCentreDetail.setApplicationId(applicationId);
        	
        	}
        
        
        List<DocumentDetail> documentDetails = cndApplicationDetails.getDocumentDetails();
        if (documentDetails != null) {
            for (DocumentDetail documentDetail : documentDetails) {
                String documentId = CNDServiceUtil.getRandomUUID(); 
                documentDetail.setDocumentDetailId(documentId);
                documentDetail.setApplicationId(applicationId);
            }
        }
        List<String> customIds = getIdList(requestInfo, cndApplicationDetails.getTenantId(),
                config.getCNDApplicationKey(), config.getCNDApplicationFormat(), 1);


        String applicationNumber = customIds.get(0);
        log.info("Generated Application Number: {}", applicationNumber);
        cndApplicationDetails.setApplicationNumber(applicationNumber);

    }

    /**
     * Enriches the address details in the given CNDApplicationDetail object by creating a new address
     * based on the user UUID provided in the CNDApplicationRequest object. If the new address is created
     * successfully, the addressDetailId in the CNDApplicationDetail object is updated.
     *
     * @param cndApplicationRequest The request object containing necessary data for address creation.
     * @param cndApplicationDetails The application details object to be enriched with the new address ID.
     */
    private void enrichAddressDetails(CNDApplicationRequest cndApplicationRequest, CNDApplicationDetail cndApplicationDetails) {
        try {
            // Fetch the new address associated with the user's UUID
            Address address = userService.getNewAddressByUserUuid(cndApplicationRequest);

            if (address != null) {
                // Set the address detail ID in the application details object
                cndApplicationDetails.setAddressDetailId(String.valueOf(address.getId()));
                log.info("Address details successfully enriched with ID: {}", address.getId());
            } else {
                log.warn("Failed to fetch the new address for user UUID: {}", cndApplicationRequest.getCndApplication().getApplicantDetailId());
            }
        } catch (Exception e) {
            log.error("Error while enriching address details: {}", e.getMessage(), e);
        }
    }


    /**
     * Fetches and assigns user details to the CND application.
     * If the user exists, their UUID is assigned as the applicantDetailId.
     * The permanent address ID is also set in the application details
     * if not found correspondence address id is set if not found first address id is set.
     *
     * @param cndApplicationRequest   The request containing applicant details.
     * @param cndApplicationDetails   The application object to be enriched.
     */
    private void enrichUserDetails(CNDApplicationRequest cndApplicationRequest, CNDApplicationDetail cndApplicationDetails) {
        try {
            if (cndApplicationDetails.getApplicantDetailId() != null) {
                User user = userService.getExistingOrNewUser(cndApplicationRequest);
                cndApplicationDetails.setApplicantDetailId(user.getUuid());

                Address selectedAddress = user.getAddresses().stream()
                        .filter(address -> AddressType.PERMANENT.equals(address.getType()))
                        .findFirst()
                        .orElseGet(() -> user.getAddresses().stream()
                                .filter(address -> AddressType.CORRESPONDENCE.equals(address.getType()))
                                .findFirst()
                                .orElse(user.getAddresses().stream().findFirst().orElse(null))
                        );

                if (selectedAddress != null) {
                    cndApplicationDetails.setAddressDetailId(String.valueOf(selectedAddress.getId()));
                }
                log.info("Applicant/User UUID: {}", user.getUuid());
            }
        } catch (Exception e) {
            log.error("Error while fetching user details: {}", e.getMessage(), e);
        }
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
        cndApplicationDetails.setVendorId(cndApplicationRequest.getCndApplication().getVendorId());
		cndApplicationDetails.setVehicleId(cndApplicationRequest.getCndApplication().getVehicleId());
		cndApplicationDetails.getAuditDetails().setLastModifiedBy(cndApplicationRequest.getRequestInfo().getUserInfo().getUuid());
		cndApplicationDetails.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        cndApplicationDetails.setApplicationStatus(applicationStatus);
    }
}
