package org.upyog.tp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.repository.TreePruningRepository;
import org.upyog.tp.service.*;
import org.upyog.tp.web.models.ApplicantDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;
import org.upyog.tp.web.models.treePruning.TreePruningBookingSearchCriteria;
import org.upyog.tp.web.models.workflow.State;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TreePruningServiceImpl implements TreePruningService {

    private final EnrichmentService enrichmentService;
    private final TreePruningRepository treePruningRepository;
    private final WorkflowService workflowService;
    private final DemandService demandService;
    private final UserService userService;
    private final TreePruningConfiguration config;

    public TreePruningServiceImpl(EnrichmentService enrichmentService, TreePruningRepository treePruningRepository,
                                  WorkflowService workflowService, DemandService demandService, UserService userService,
                                  TreePruningConfiguration config) {
        this.enrichmentService = enrichmentService;
        this.treePruningRepository = treePruningRepository;
        this.workflowService = workflowService;
        this.demandService = demandService;
        this.userService = userService;
        this.config = config;
    }

    @Override
    public TreePruningBookingDetail createNewTreePruningBookingRequest(TreePruningBookingRequest treePruningRequest) {

        log.info("Create Tree Pruning booking for user : " + treePruningRequest.getRequestInfo().getUserInfo().getUuid()
                + " for the request : " + treePruningRequest.getTreePruningBookingDetail());

        enrichmentService.enrichCreateTreePruningRequest(treePruningRequest);

        workflowService.updateWorkflowStatus(null, treePruningRequest);

        try {
            RequestInfo requestInfo = treePruningRequest.getRequestInfo();
            ApplicantDetail applicantDetail = treePruningRequest.getTreePruningBookingDetail().getApplicantDetail();
            String tenantId = treePruningRequest.getTreePruningBookingDetail().getTenantId();
            org.upyog.tp.web.models.user.User user = userService.fetchExistingUser(tenantId,applicantDetail, requestInfo);

            if (user == null) {
                throw new CustomException("USER_NOT_FOUND",
                        "User not found for this mobile number: " +
                        treePruningRequest.getTreePruningBookingDetail().getApplicantDetail().getMobileNumber());
            }
            if (Boolean.TRUE.equals(config.getIsUserProfileEnabled())) {
                treePruningRequest.getTreePruningBookingDetail().setApplicantUuid(user.getUuid());
                log.info("Applicant or User Uuid: " + user.getUuid());
            } else {
                treePruningRequest.getTreePruningBookingDetail().setApplicantUuid(null);
                log.info("User profile is not enabled, setting applicantUuid to null");
            }
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching user: " + e.getMessage(), e);
            throw new CustomException("USER_FETCH_FAILED", "Failed to fetching user: " + e.getMessage());
        }

        treePruningRepository.saveTreePruningBooking(treePruningRequest);

        return treePruningRequest.getTreePruningBookingDetail();
    }

    @Override
    public List<TreePruningBookingDetail> getTreePruningBookingDetails(RequestInfo requestInfo,
                                                                       TreePruningBookingSearchCriteria treePruningBookingSearchCriteria) {
        List<TreePruningBookingDetail> applications = treePruningRepository
                .getTreePruningBookingDetails(treePruningBookingSearchCriteria);

        if (CollectionUtils.isEmpty(applications)) {
            return new ArrayList<>();
        }
       if (Boolean.TRUE.equals(config.getIsUserProfileEnabled())) {
           for (TreePruningBookingDetail booking : applications) {
               userService.enrichBookingWithUserDetails(booking, treePruningBookingSearchCriteria);
           }
       }
        return applications;
    }

    @Override
    public Integer getApplicationsCount(TreePruningBookingSearchCriteria treePruningBookingSearchCriteria,
                                        RequestInfo requestInfo) {
        treePruningBookingSearchCriteria.setCountCall(true);

        treePruningBookingSearchCriteria = addCreatedByMeToCriteria(treePruningBookingSearchCriteria, requestInfo);
        return treePruningRepository.getApplicationsCount(treePruningBookingSearchCriteria);
    }

    private TreePruningBookingSearchCriteria addCreatedByMeToCriteria(TreePruningBookingSearchCriteria criteria,
                                                                      RequestInfo requestInfo) {
        if (requestInfo.getUserInfo() == null) {
            log.info("Request info is null returning criteira");
            return criteria;
        }
        List<String> roles = new ArrayList<>();
        for (Role role : requestInfo.getUserInfo().getRoles()) {
            roles.add(role.getCode());
        }
        log.info("user roles for searching : " + roles);
        List<String> uuids = new ArrayList<>();
        if (roles.contains(TreePruningConstants.CITIZEN)
                && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
            uuids.add(requestInfo.getUserInfo().getUuid());
            criteria.setCreatedBy(uuids);
            log.debug("loading data of created and by me" + uuids.toString());
        }
        return criteria;
    }

    @Override
    public TreePruningBookingDetail updateTreePruningBooking(TreePruningBookingRequest treePruningRequest,
                                                             String applicationStatus) {
        String bookingNo = treePruningRequest.getTreePruningBookingDetail().getBookingNo();
        log.info("Updating booking for booking no: {}", bookingNo);

        if (bookingNo == null) {
            throw new CustomException("INVALID_BOOKING_CODE",
                    "Booking no not valid. Failed to update booking status for : " + bookingNo);
        }

        if (treePruningRequest.getTreePruningBookingDetail().getWorkflow() != null) {
            State state = workflowService.updateWorkflowStatus(null, treePruningRequest);
            enrichmentService.enrichTreePruningBookingUponUpdate(state.getApplicationStatus(), treePruningRequest);

            if (TreePruningConstants.ACTION_APPROVE
                    .equals(treePruningRequest.getTreePruningBookingDetail().getWorkflow().getAction())) {
                demandService.createDemand(treePruningRequest);
            }
        }

        log.info("Payment request is null, updating Tree Pruning booking without payment");
        treePruningRepository.updateTreePruningBooking(treePruningRequest);

        return treePruningRequest.getTreePruningBookingDetail();
    }

    @Override
    public void updateTreePruningBooking(PaymentRequest paymentRequest, String applicationStatus) {
        log.info("Payment request: {}", paymentRequest);
        TreePruningBookingDetail treePruningDetail=null;
        if (paymentRequest != null) {
            try {
                String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
                treePruningDetail = treePruningRepository
                        .getTreePruningBookingDetails(
                                TreePruningBookingSearchCriteria.builder().bookingNo(consumerCode).build())
                        .stream().findFirst().orElse(null);
                log.info("Tree Pruning booking detail: {}", treePruningDetail);
                log.info("Consumer code: {}", consumerCode);
                if (treePruningDetail == null) {
                    log.info("Application not found in consumer class while updating status");
                } else{
                    treePruningDetail.getAuditDetails()
                            .setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
                    treePruningDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
                    treePruningDetail.setBookingStatus(applicationStatus);
                    treePruningDetail.setPaymentDate(System.currentTimeMillis());

                    log.info("Tree Pruning detail after updating booking status: {}", treePruningDetail);

                    TreePruningBookingRequest updatedTreePruningRequest = TreePruningBookingRequest.builder()
                            .requestInfo(paymentRequest.getRequestInfo()).treePruningBookingDetail(treePruningDetail).build();

                    log.info("Tree Pruning Request to update application status in consumer: {}", updatedTreePruningRequest);
                    treePruningRepository.updateTreePruningBooking(updatedTreePruningRequest);
                }
            }
            catch (Exception e) {
                log.error("Error while updating Tree Pruning booking: {}", e.getMessage(), e);
                throw new CustomException("UPDATE_FAILED", "Failed to update Tree Pruning booking");
            }

        }
        log.info("final object {}", treePruningDetail);
    }

}
