package org.upyog.tp.service;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.enums.TreePruningStatus;
import org.upyog.tp.repository.IdGenRepository;
import org.upyog.tp.util.TreePruningUtil;
import org.upyog.tp.util.UserUtil;
import org.upyog.tp.web.models.ApplicantDetail;
import org.upyog.tp.web.models.AuditDetails;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.user.AddressV2;
import org.upyog.tp.web.models.user.User;
import org.apache.commons.lang3.StringUtils;

import digit.models.coremodels.IdResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EnrichmentService {

    @Autowired
    private TreePruningConfiguration config;

    @Autowired
    private IdGenRepository idGenRepository;

    @Autowired
    private UserService userService;

    public void enrichCreateTreePruningRequest(TreePruningBookingRequest treePruningRequest) {
        String bookingId = TreePruningUtil.getRandonUUID();
        log.info("Enriching Tree Pruning booking id :" + bookingId);

        TreePruningBookingDetail treePruningDetail = treePruningRequest.getTreePruningBookingDetail();
        RequestInfo requestInfo = treePruningRequest.getRequestInfo();
        String userUuid = requestInfo.getUserInfo().getUuid();
        AuditDetails auditDetails = TreePruningUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

        if (config.getIsUserProfileEnabled()){
            // If the mobile number in the request matches the applicant's mobile number, then set the applicantDetailId as userUuid
            if (UserUtil.isCurrentUserApplicant(treePruningRequest)) {
                treePruningDetail.setApplicantUuid(userUuid);
            } else {
                // If the mobile number does not match, set the applicantDetailId to null and addressDetailId to null
                treePruningDetail.setApplicantUuid(null);
                treePruningDetail.setAddressDetailId(null);
            }

            String applicantDetailId = treePruningDetail.getApplicantUuid();
            String addressDetailId = treePruningDetail.getAddressDetailId();

            if (StringUtils.isBlank(applicantDetailId)) {
                // Enrich user details for existing user or user details with address for new user
                enrichUserDetails(treePruningRequest);
            }
            if (StringUtils.isBlank(addressDetailId)) {
                // Enrich address details only
                enrichAddressDetails(treePruningRequest, treePruningDetail);
            }
        }else{
            /*
             * If the currently logged-in user is not the same as the applicant mobile number entered in the form is different from the login mobile number,
             * then we proceed to enrich user details, which will create a new user with the provided details.
             * If the currently logged-in user is the same as the applicant mobile number, we do not enrich user details
             * user profile is not enabled for this service, we explicitly set `applicantDetailId` and `addressDetailId` to null in the application details
             */
            if (!UserUtil.isCurrentUserApplicant(treePruningRequest)) {
                // Enrich user details for existing user or user details with address for new user
                enrichUserDetails(treePruningRequest);
            }
            treePruningDetail.setApplicantUuid(null);
            treePruningDetail.setAddressDetailId(null);
        }
        treePruningDetail.setBookingId(bookingId);
        treePruningDetail.setApplicationDate(auditDetails.getCreatedTime());
        treePruningDetail.setBookingStatus(TreePruningStatus.valueOf(treePruningDetail.getBookingStatus()).toString());
        treePruningDetail.setAuditDetails(auditDetails);
        treePruningDetail.setTenantId(treePruningRequest.getTreePruningBookingDetail().getTenantId());

        List<String> customIds = getIdList(requestInfo, treePruningDetail.getTenantId(),
                config.getTreePruningApplicationKey(), config.getTreePruningApplicationFormat(), 1);

        log.info("Enriched application request application no :" + customIds.get(0));

        treePruningDetail.setBookingNo(customIds.get(0));

        treePruningDetail.setReasonForPruning(treePruningRequest.getTreePruningBookingDetail().getReasonForPruning());
        treePruningDetail.setLatitude(treePruningRequest.getTreePruningBookingDetail().getLatitude());
        treePruningDetail.setLongitude(treePruningRequest.getTreePruningBookingDetail().getLongitude());

        // Updating id in documents
        treePruningDetail.getDocumentDetails().stream().forEach(document -> {
            document.setBookingId(bookingId);
            document.setDocumentDetailId(TreePruningUtil.getRandonUUID());
            document.setAuditDetails(auditDetails);
        });

        treePruningDetail.setDocumentDetails(treePruningRequest.getTreePruningBookingDetail().getDocumentDetails());
        treePruningDetail.setMobileNumber(treePruningRequest.getTreePruningBookingDetail().getApplicantDetail().getMobileNumber());
        treePruningDetail.setLocalityCode(treePruningRequest.getTreePruningBookingDetail().getAddress().getLocalityCode());
        String roles = treePruningRequest.getRequestInfo().getUserInfo().getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(", "));
        treePruningDetail.setBookingCreatedBy(roles);

        treePruningDetail.getApplicantDetail().setBookingId(bookingId);
        treePruningDetail.getApplicantDetail().setApplicantId(TreePruningUtil.getRandonUUID());
        treePruningDetail.getAddress().setAddressId(TreePruningUtil.getRandonUUID());
        treePruningDetail.getApplicantDetail().setAuditDetails(auditDetails);
        treePruningDetail.getAddress().setApplicantId(treePruningDetail.getApplicantDetail().getApplicantId());

        log.info("Enriched application request data :" + treePruningDetail);

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
     * @param treePruningRequest The full application request containing applicant and address info.
     */
    private void enrichUserDetails(TreePruningBookingRequest treePruningRequest) {
        // Try fetching an existing user for the given request
        TreePruningBookingDetail treePruningDetail = treePruningRequest.getTreePruningBookingDetail();
        RequestInfo requestInfo = treePruningRequest.getRequestInfo();
        ApplicantDetail applicantDetail = treePruningDetail.getApplicantDetail();
        String tenantId = treePruningDetail.getTenantId();
        User existingUsers = userService.fetchExistingUser(tenantId, applicantDetail, requestInfo);

        if (existingUsers != null) {
            treePruningDetail.setApplicantUuid(existingUsers.getUuid());
            log.info("Existing user found with ID: {}", existingUsers.getUuid());
            return;
        }

        // Create a new user with address details if no existing user was found
        User newUser = userService.createUserHandler(treePruningRequest.getRequestInfo(), treePruningRequest.getTreePruningBookingDetail().getApplicantDetail(),
                treePruningRequest.getTreePruningBookingDetail().getAddress(), treePruningRequest.getTreePruningBookingDetail().getTenantId());
        log.info("New user created with ID: {}", newUser.getUuid());
        treePruningDetail.setApplicantUuid(newUser.getUuid());

        // Set addressDetailId from the first address with a non-null ID, if present
        newUser.getAddresses().stream()
                .filter(addr -> addr.getId() != null)
                .findFirst()
                .ifPresent(addr -> treePruningDetail.setAddressDetailId(String.valueOf(addr.getId())));
    }


    /**
     * Enriches the address details in the given TreePruningBookingDetail object by creating a new address
     * based on the user UUID provided in the TreePruningBookingRequest object. If the new address is created
     * successfully, the addressDetailId in the TreePruningBookingDetail object is updated.
     *
     * @param treePruningRequest The request object containing necessary data for address creation.
     * @param treePruningDetail The application details object to be enriched with the new address ID.
     */
    private void enrichAddressDetails(TreePruningBookingRequest treePruningRequest, TreePruningBookingDetail treePruningDetail) {

        // If applicant UUID is null or blank, throw custom exception
        if (StringUtils.isBlank(treePruningRequest.getTreePruningBookingDetail().getApplicantUuid())) {
            throw new CustomException("APPLICANT_UUID_NULL", "Applicant UUID is blank");
        }

        // Fetch the new address associated with the user's UUID
        AddressV2 addressDetails = UserService.convertApplicantAddressToUserAddress(treePruningRequest.getTreePruningBookingDetail().getAddress(), TreePruningUtil.extractTenantId(treePruningRequest.getTreePruningBookingDetail().getTenantId()));
        AddressV2 address = userService.createNewAddressV2ByUserUuid(addressDetails,treePruningRequest.getRequestInfo(),treePruningRequest.getTreePruningBookingDetail().getApplicantUuid());

        if (address != null) {
            // Set the address detail ID in the booking detail
            treePruningDetail.setAddressDetailId(String.valueOf(address.getId()));
            log.info("Address successfully enriched with ID: {}", address.getId());
        } else {
            throw new CustomException("ADDRESS_CREATION_FAILED", "Failed to create address for the given applicant UUID");
        }
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

    public void enrichTreePruningBookingUponUpdate(String bookingStatus, TreePruningBookingRequest treePruningRequest) {
        TreePruningBookingDetail treePruningDetail = treePruningRequest.getTreePruningBookingDetail();
        treePruningDetail.getAuditDetails().setLastModifiedBy(treePruningRequest.getRequestInfo().getUserInfo().getUuid());
        treePruningDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        treePruningDetail.setBookingStatus(bookingStatus);

    }



}
