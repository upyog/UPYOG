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
import org.upyog.cdwm.web.models.user.AddressV2;
import org.upyog.cdwm.web.models.user.User;

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
        String userUuid = requestInfo.getUserInfo().getUuid();
        AuditDetails auditDetails = CNDServiceUtil.getAuditDetails(userUuid, true);

        if(config.getIsUserProfileEnabled()) {
            // If the mobile number in the request matches the applicant's mobile number, then set the applicantDetailId as userUuid
            if (CNDServiceUtil.isCurrentUserApplicant(cndApplicationRequest)) {
                cndApplicationDetails.setApplicantDetailId(userUuid);
            } else { // If the mobile number does not match, set the applicantDetailId to null and addressDetailId to null
                cndApplicationDetails.setApplicantDetailId(null);
                cndApplicationDetails.setAddressDetailId(null);
            }

            String applicantDetailId = cndApplicationDetails.getApplicantDetailId();

            if (StringUtils.isBlank(applicantDetailId)) {
                // Enrich user details for existing user or user details with address for new user
                enrichUserDetails(cndApplicationRequest);
            }
            String addressDetailId = cndApplicationDetails.getAddressDetailId();
            if (StringUtils.isBlank(addressDetailId)) {
                // Enrich address details only
                enrichAddressDetails(cndApplicationRequest, cndApplicationDetails);
            }
        }
        else{
            /*
             * If the currently logged-in user is not the same as the applicant mobile number entered in the form is different from the login mobile number,
             * then we proceed to enrich user details, which will create a new user with the provided details.
             * If the currently logged-in user is the same as the applicant mobile number, we do not enrich user details
             * user profile is not enabled for this service, we explicitly set `applicantDetailId` and `addressDetailId` to null in the application details
             */
            if (!CNDServiceUtil.isCurrentUserApplicant(cndApplicationRequest)) {
                enrichUserDetails(cndApplicationRequest);
            }
            cndApplicationDetails.setApplicantDetailId(null);
            cndApplicationDetails.setAddressDetailId(null);
            log.info("User profile is not enabled, setting applicantDetailId and addressDetailId to null");
        }

        // Set application details
        cndApplicationDetails.setApplicationId(applicationId);
        cndApplicationDetails.setApplicationStatus(CNDStatus.valueOf(cndApplicationDetails.getApplicationStatus()).toString());
        cndApplicationDetails.setAuditDetails(auditDetails);
        cndApplicationDetails.setTenantId(cndApplicationDetails.getTenantId());
        cndApplicationDetails.getApplicantDetail().setApplicationId(applicationId);
        cndApplicationDetails.getApplicantDetail().setAuditDetails(auditDetails);
        cndApplicationDetails.getAddressDetail().setApplicationId(applicationId);
        cndApplicationDetails.setLocality(cndApplicationDetails.getAddressDetail().getLocality());
        // Copy mobile number from applicant details to a separate field to save in application table
        cndApplicationDetails.setApplicantMobileNumber(cndApplicationDetails.getApplicantDetail().getMobileNumber());
        cndApplicationDetails.setCreatedByUserType(requestInfo.getUserInfo().getType().toString());

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

        // If applicantDetailId is null or blank, throw custom exception
        if (StringUtils.isBlank(cndApplicationRequest.getCndApplication().getApplicantDetailId())) {
            throw new CustomException("APPLICANTID_IS_BLANK", "Applicant Detail ID is blank");
        }
            // Fetch the new address associated with the user's UUID
            AddressV2 address = userService.createNewAddressV2ByUserUuid(cndApplicationRequest);

            if (address != null) {
                // Set the address detail ID in the application details object
                cndApplicationDetails.setAddressDetailId(String.valueOf(address.getId()));
                log.info("Address details successfully enriched with ID: {}", address.getId());
            }

    }


    /**
     * Enriches the applicant and address detail IDs in the given application detail.
     * <p>
     * If the applicantDetailId is present, it attempts to fetch an existing user based on the request.
     * - If an existing user is found, sets the applicantDetailId accordingly.
     * - If not found, it creates a new user and sets both applicantDetailId and addressDetailId
     *   from the newly created user and their associated address.
     * </p>
     *
     * @param request The full application request containing applicant and address info.
     */
    private void enrichUserDetails(CNDApplicationRequest request) {
        // Try fetching an existing user for the given request
        CNDApplicationDetail detail = request.getCndApplication();
        List<User> existingUsers = userService.getUserDetails(request);

        if (!CollectionUtils.isEmpty(existingUsers)) {
            detail.setApplicantDetailId(existingUsers.get(0).getUuid());
            log.info("Existing user found with ID: {}", existingUsers.get(0).getUuid());
            return;
        }

        // Create a new user with address details if no existing user was found
        User newUser = userService.createUserHandler(request.getRequestInfo(), request.getCndApplication().getApplicantDetail(),
                request.getCndApplication().getAddressDetail(), request.getCndApplication().getTenantId());
        log.info("New user created with ID: {}", newUser.getUuid());
        detail.setApplicantDetailId(newUser.getUuid());

        // Set addressDetailId from the first address with a non-null ID, if present
        newUser.getAddresses().stream()
                .filter(addr -> addr.getId() != null)
                .findFirst()
                .ifPresent(addr -> detail.setAddressDetailId(String.valueOf(addr.getId())));
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
