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

        // Enrich user-related details
        enrichUserDetails(cndApplicationRequest, cndApplicationDetails);

        // Set application details
        cndApplicationDetails.setApplicationId(applicationId);
        cndApplicationDetails.setApplicationStatus(CNDStatus.valueOf(cndApplicationDetails.getApplicationStatus()).toString());
        cndApplicationDetails.setAuditDetails(auditDetails);
        cndApplicationDetails.setTenantId(cndApplicationDetails.getTenantId());

        // Copy mobile number from applicant details to a separate field to save in application table
        cndApplicationDetails.setApplicantMobileNumber(cndApplicationDetails.getApplicantDetail().getMobileNumber());


        // Generate and assign a unique application number
        List<String> customIds = getIdList(requestInfo, cndApplicationDetails.getTenantId(),
                config.getCNDApplicationKey(), config.getCNDApplicationFormat(), 1);


        String applicationNumber = customIds.get(0);
        log.info("Generated Application Number: {}", applicationNumber);
        cndApplicationDetails.setApplicationNumber(applicationNumber);

    }

    /**
     * Fetches and assigns user details to the CND application.
     * If the user exists, their UUID is assigned as the applicantDetailId.
     * The permanent address ID is also set in the application details.
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
