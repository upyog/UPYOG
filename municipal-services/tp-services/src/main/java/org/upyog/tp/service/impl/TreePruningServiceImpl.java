package org.upyog.tp.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.upyog.tp.web.models.Workflow;
import org.upyog.tp.web.models.workflow.State;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TreePruningServiceImpl implements TreePruningService {
    @Autowired
    EnrichmentService enrichmentService;

    @Autowired
    TreePruningRepository treePruningRepository;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    DemandService demandService;

    @Autowired
    private UserService userService;

    @Autowired
    TreePruningConfiguration config;

    @Override
    public TreePruningBookingDetail createNewTreePruningBookingRequest(TreePruningBookingRequest treePruningRequest) {

        log.info("Create Tree Pruning booking for user : " + treePruningRequest.getRequestInfo().getUserInfo().getUuid()
                + " for the request : " + treePruningRequest.getTreePruningBookingDetail());

        enrichmentService.enrichCreateTreePruningRequest(treePruningRequest);

        workflowService.updateWorkflowStatus(null, treePruningRequest);

        // Get the uuid of User from user registry
        try {
            RequestInfo requestInfo = treePruningRequest.getRequestInfo();
            ApplicantDetail applicantDetail = treePruningRequest.getTreePruningBookingDetail().getApplicantDetail();
            String tenantId = treePruningRequest.getTreePruningBookingDetail().getTenantId();
            org.upyog.tp.web.models.user.User user = userService.fetchExistingUser(tenantId,applicantDetail, requestInfo);

            if (user == null) {
                throw new RuntimeException("User not found for this mobile number: " +
                        treePruningRequest.getTreePruningBookingDetail().getApplicantDetail().getMobileNumber());
            }
            if(config.getIsUserProfileEnabled()){
            treePruningRequest.getTreePruningBookingDetail().setApplicantUuid(user.getUuid());
            log.info("Applicant or User Uuid: " + user.getUuid());
            } else {
                // If user profile is not enabled, set the applicantUuid to null
                treePruningRequest.getTreePruningBookingDetail().setApplicantUuid(null);
                log.info("User profile is not enabled, setting applicantUuid to null");
            }
        } catch (Exception e) {
            log.error("Error fetching user: " + e.getMessage(), e);
            throw new RuntimeException("Failed to fetching user: " + e.getMessage(), e);
        }

        treePruningRepository.saveTreePruningBooking(treePruningRequest);

        TreePruningBookingDetail treePruningDetail = treePruningRequest.getTreePruningBookingDetail();

        return treePruningDetail;
    }

    @Override
    public List<TreePruningBookingDetail> getTreePruningBookingDetails(RequestInfo requestInfo,
                                                                       TreePruningBookingSearchCriteria treePruningBookingSearchCriteria) {
        /*
         * Retrieve TP booking details from the repository based on search criteria and
         * and give the data already retrieved to the repository layer
         */
        List<TreePruningBookingDetail> applications = treePruningRepository
                .getTreePruningBookingDetails(treePruningBookingSearchCriteria);

        /**
         * Check if the retrieved list is empty using Spring's CollectionUtils Prevents
         * potential null pointer exceptions by returning an empty list Ensures
         * consistent return type and prevents calling methods from handling null
         */
        if (CollectionUtils.isEmpty(applications)) {
            return new ArrayList<>();
        }
       if(config.getIsUserProfileEnabled()) {
           // Enrich each booking with user details
           for (TreePruningBookingDetail booking : applications) {
               userService.enrichBookingWithUserDetails(booking, treePruningBookingSearchCriteria);
           }
       }
        // Return retrieved application
        return applications;
    }

    @Override
    public Integer getApplicationsCount(TreePruningBookingSearchCriteria treePruningBookingSearchCriteria,
                                        RequestInfo requestInfo) {
        treePruningBookingSearchCriteria.setCountCall(true);
        Integer bookingCount = 0;

        treePruningBookingSearchCriteria = addCreatedByMeToCriteria(treePruningBookingSearchCriteria, requestInfo);
        bookingCount = treePruningRepository.getApplicationsCount(treePruningBookingSearchCriteria);

        return bookingCount;
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
        /**
         * Citizen can see booking details only booked by him
         */
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

        // If no payment request, update workflow status and process booking request
        if (treePruningRequest.getTreePruningBookingDetail().getWorkflow() != null) {
            State state = workflowService.updateWorkflowStatus(null, treePruningRequest);
            enrichmentService.enrichTreePruningBookingUponUpdate(state.getApplicationStatus(), treePruningRequest);

            // If action is APPROVE, create demand
            if (TreePruningConstants.ACTION_APPROVE
                    .equals(treePruningRequest.getTreePruningBookingDetail().getWorkflow().getAction())) {
                demandService.createDemand(treePruningRequest);
            }
        }

        log.info("Payment request is null, updating Tree Pruning booking without payment");
        // If no payment request, just update the tree pruning booking request
        treePruningRepository.updateTreePruningBooking(treePruningRequest);

        return treePruningRequest.getTreePruningBookingDetail();
    }

    @Override
    public void updateTreePruningBooking(PaymentRequest paymentRequest, String applicationStatus) {
        log.info("Payment request: {}", paymentRequest);
        // Handle the payment request and update the Tree Pruning booking if applicable
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
                    // Update the booking details
                    treePruningDetail.getAuditDetails()
                            .setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
                    treePruningDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
                    treePruningDetail.setBookingStatus(applicationStatus);
                    treePruningDetail.setPaymentDate(System.currentTimeMillis());

                    log.info("Tree Pruning detail after updating booking status: {}", treePruningDetail);

                    // Update Tree Pruning booking request
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
