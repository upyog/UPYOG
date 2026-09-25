package org.upyog.tp.service;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
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

    private final TreePruningConfiguration config;
    private final IdGenRepository idGenRepository;
    private final UserService userService;

    public EnrichmentService(TreePruningConfiguration config, IdGenRepository idGenRepository, UserService userService) {
        this.config = config;
        this.idGenRepository = idGenRepository;
        this.userService = userService;
    }

    public void enrichCreateTreePruningRequest(TreePruningBookingRequest treePruningRequest) {
        String bookingId = TreePruningUtil.getRandonUUID();
        log.info("Enriching Tree Pruning booking id :" + bookingId);

        TreePruningBookingDetail treePruningDetail = treePruningRequest.getTreePruningBookingDetail();
        RequestInfo requestInfo = treePruningRequest.getRequestInfo();
        String userUuid = requestInfo.getUserInfo().getUuid();
        AuditDetails auditDetails = TreePruningUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

        if (Boolean.TRUE.equals(config.getIsUserProfileEnabled())){
            if (UserUtil.isCurrentUserApplicant(treePruningRequest)) {
                treePruningDetail.setApplicantUuid(userUuid);
            } else {
                treePruningDetail.setApplicantUuid(null);
                treePruningDetail.setAddressDetailId(null);
            }

            String applicantDetailId = treePruningDetail.getApplicantUuid();
            String addressDetailId = treePruningDetail.getAddressDetailId();

            if (StringUtils.isBlank(applicantDetailId)) {
                enrichUserDetails(treePruningRequest);
            }
            if (StringUtils.isBlank(addressDetailId)) {
                enrichAddressDetails(treePruningRequest, treePruningDetail);
            }
        }else{
            if (!UserUtil.isCurrentUserApplicant(treePruningRequest)) {
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
                .collect(java.util.stream.Collectors.joining(", "));
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
     *
     * @param treePruningRequest The full application request containing applicant and address info.
     */
    private void enrichUserDetails(TreePruningBookingRequest treePruningRequest) {
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

        User newUser = userService.createUserHandler(treePruningRequest.getRequestInfo(), treePruningRequest.getTreePruningBookingDetail().getApplicantDetail(),
                treePruningRequest.getTreePruningBookingDetail().getAddress(), treePruningRequest.getTreePruningBookingDetail().getTenantId());
        log.info("New user created with ID: {}", newUser.getUuid());
        treePruningDetail.setApplicantUuid(newUser.getUuid());

        newUser.getAddresses().stream()
                .filter(addr -> addr.getId() != null)
                .findFirst()
                .ifPresent(addr -> treePruningDetail.setAddressDetailId(String.valueOf(addr.getId())));
    }


    /**
     * Enriches the address details in the given TreePruningBookingDetail object by creating a new address
     * based on the user UUID provided in the TreePruningBookingRequest object.
     *
     * @param treePruningRequest The request object containing necessary data for address creation.
     * @param treePruningDetail The application details object to be enriched with the new address ID.
     */
    private void enrichAddressDetails(TreePruningBookingRequest treePruningRequest, TreePruningBookingDetail treePruningDetail) {

        if (StringUtils.isBlank(treePruningRequest.getTreePruningBookingDetail().getApplicantUuid())) {
            throw new CustomException("APPLICANT_UUID_NULL", "Applicant UUID is blank");
        }

        AddressV2 addressDetails = UserService.convertApplicantAddressToUserAddress(treePruningRequest.getTreePruningBookingDetail().getAddress(), TreePruningUtil.extractTenantId(treePruningRequest.getTreePruningBookingDetail().getTenantId()));
        AddressV2 address = userService.createNewAddressV2ByUserUuid(addressDetails,treePruningRequest.getRequestInfo(),treePruningRequest.getTreePruningBookingDetail().getApplicantUuid());

        if (address != null) {
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

        return idResponses.stream().map(IdResponse::getId).toList();
    }

    public void enrichTreePruningBookingUponUpdate(String bookingStatus, TreePruningBookingRequest treePruningRequest) {
        TreePruningBookingDetail treePruningDetail = treePruningRequest.getTreePruningBookingDetail();
        treePruningDetail.getAuditDetails().setLastModifiedBy(treePruningRequest.getRequestInfo().getUserInfo().getUuid());
        treePruningDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
        treePruningDetail.setBookingStatus(bookingStatus);

    }



}
