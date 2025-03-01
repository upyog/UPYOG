package org.upyog.rs.service.impl;

import digit.models.coremodels.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.rs.config.RequestServiceConfiguration;
import org.upyog.rs.constant.RequestServiceConstants;
import org.upyog.rs.repository.RequestServiceRepository;
import org.upyog.rs.service.*;
import org.upyog.rs.web.models.ApplicantDetail;
import org.upyog.rs.web.models.Workflow;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingDetail;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingRequest;
import org.upyog.rs.web.models.mobileToilet.MobileToiletBookingSearchCriteria;
import org.upyog.rs.web.models.user.UserDetailResponse;
import org.upyog.rs.web.models.user.UserSearchRequest;
import org.upyog.rs.web.models.workflow.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class MobileToiletServiceImpl implements MobileToiletService{

    @Autowired
    EnrichmentService enrichmentService;

    @Autowired
    RequestServiceRepository requestServiceRepository;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    DemandService demandService;

    @Autowired
    RequestServiceConfiguration config;

    @Autowired
    private UserService userService;

    @Override
    public MobileToiletBookingDetail createNewMobileToiletBookingRequest(MobileToiletBookingRequest mobileToiletRequest) {

        log.info("Create water tanker booking for user : " + mobileToiletRequest.getRequestInfo().getUserInfo().getUuid()
                + " for the request : " + mobileToiletRequest.getMobileToiletBookingDetail());

        enrichmentService.enrichCreateMobileToiletRequest(mobileToiletRequest);

//        workflowService.updateWorkflowStatus(null, mobileToiletRequest);

        // Get the uuid of User from user registry
        try {
            String uuid = userService.getUuidExistingOrNewUser(mobileToiletRequest);
            mobileToiletRequest.getMobileToiletBookingDetail().setApplicantUuid(uuid);
            log.info("Applicant or User Uuid: " + uuid);
        } catch (Exception e) {
            log.error("Error while creating user: " + e.getMessage(), e);
        }

        requestServiceRepository.saveMobileToiletBooking(mobileToiletRequest);

        MobileToiletBookingDetail mobileToiletDetail = mobileToiletRequest.getMobileToiletBookingDetail();

        return mobileToiletDetail;
    }

    @Override
    public List<MobileToiletBookingDetail> getMobileToiletBookingDetails(RequestInfo requestInfo,
                                                                       MobileToiletBookingSearchCriteria mobileToiletBookingSearchCriteria) {
        /*
         * Retrieve mobile Toilet booking details from the repository based on search criteria and
         * and give the data already retrieved to the repository layer
         */

        List<MobileToiletBookingDetail> applications = requestServiceRepository
                .getMobileToiletBookingDetails(mobileToiletBookingSearchCriteria);



        /**
         * Check if the retrieved list is empty using Spring's CollectionUtils Prevents
         * potential null pointer exceptions by returning an empty list Ensures
         * consistent return type and prevents calling methods from handling null
         */
        if (CollectionUtils.isEmpty(applications)) {
            return new ArrayList<>();
        }
        // Loop through each booking and fetch user details
        for (MobileToiletBookingDetail booking : applications) {
            String applicantUuid = booking.getApplicantUuid();  // Extract UUID

            if (applicantUuid != null) {
                // Using builder pattern to create UserSearchRequest
                UserSearchRequest userSearchRequest = UserSearchRequest.builder()
                        .uuid(Collections.singleton(applicantUuid))
                        .build();

                try {
                    // Fetch user details
                    UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);

                    if (userDetailResponse != null && !CollectionUtils.isEmpty(userDetailResponse.getUser())) {
                        org.upyog.rs.web.models.user.User user = userDetailResponse.getUser().get(0);

                        // Using builder pattern to create ApplicantDetail
                        ApplicantDetail applicant = ApplicantDetail.builder()
                                .name(user.getName())
                                .emailId(user.getEmailId())
                                .mobileNumber(user.getMobileNumber())
                                .alternateNumber(user.getAltContactNumber())
                                .bookingId(booking.getBookingId())
                                .applicantId(booking.getApplicantUuid())
                                .build();

                        booking.setApplicantDetail(applicant);
                    }
                } catch (Exception e) {
                    log.error("Error while fetching user details: " + e.getMessage(), e);
                }
            }
        }

        return applications;
    }

    @Override
    public Integer getApplicationsCount(MobileToiletBookingSearchCriteria mobileToiletBookingSearchCriteria,
                                        RequestInfo requestInfo) {
        mobileToiletBookingSearchCriteria.setCountCall(true);
        Integer bookingCount = 0;

        mobileToiletBookingSearchCriteria = addCreatedByMeToCriteria(mobileToiletBookingSearchCriteria, requestInfo);
        bookingCount = requestServiceRepository.getApplicationsCount(mobileToiletBookingSearchCriteria);

        return bookingCount;
    }

    private MobileToiletBookingSearchCriteria addCreatedByMeToCriteria(MobileToiletBookingSearchCriteria criteria,
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
        if (roles.contains(RequestServiceConstants.CITIZEN)
                && !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
            uuids.add(requestInfo.getUserInfo().getUuid());
            criteria.setCreatedBy(uuids);
            log.debug("loading data of created and by me" + uuids.toString());
        }
        return criteria;
    }
//
    @Override
    public MobileToiletBookingDetail updateMobileToiletBooking(MobileToiletBookingRequest mobileToiletRequest,
                                                             PaymentRequest paymentRequest, String applicationStatus) {
        String bookingNo = mobileToiletRequest.getMobileToiletBookingDetail().getBookingNo();
        log.info("Updating booking for booking no: {}", bookingNo);

        if (bookingNo == null) {
            throw new CustomException("INVALID_BOOKING_CODE",
                    "Booking no not valid. Failed to update booking status for : " + bookingNo);
        }

        // If no payment request, update workflow status and process booking request
        if (paymentRequest == null) {
            State state = workflowService.updateMTWorkflowStatus(null, mobileToiletRequest);
            enrichmentService.enrichMobileToiletBookingUponUpdate(state.getApplicationStatus(), mobileToiletRequest);

//            // If action is APPROVE, create demand
//            if (RequestServiceConstants.ACTION_APPROVE
//                    .equals(mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow().getAction())) {
//                demandService.createDemand(mobileToiletRequest);
//            }
        }

        // Handle the payment request and update the water tanker booking if applicable
        if (paymentRequest != null) {
            String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
            MobileToiletBookingDetail mobileToiletDetail = requestServiceRepository
                    .getMobileToiletBookingDetails(
                            MobileToiletBookingSearchCriteria.builder().bookingNo(consumerCode).build())
                    .stream().findFirst().orElse(null);

            if (mobileToiletDetail == null) {
                log.info("Application not found in consumer class while updating status");
                return null;
            }

            // Update the booking details
            mobileToiletDetail.getAuditDetails()
                    .setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
            mobileToiletDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
            mobileToiletDetail.setBookingStatus(applicationStatus);
            mobileToiletDetail.setPaymentDate(System.currentTimeMillis());

            // Update water tanker booking request
            MobileToiletBookingRequest updatedMobileToiletRequest = MobileToiletBookingRequest.builder()
                    .requestInfo(paymentRequest.getRequestInfo()).mobileToiletBookingDetail(mobileToiletDetail).build();

            log.info("Water Tanker Request to update application status in consumer: {}", updatedMobileToiletRequest);
//            requestServiceRepository.updateMobileToiletBooking(updatedMobileToiletRequest);

            return mobileToiletDetail;
        }

//        log.info("Mobile Toilet Request to update application status in consumer: {}", updatedMobileToletRequest);
//        requestServiceRepository.updateMobileToiletBooking(mobileToiletRequest);

        Workflow workflow = mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow();

        if (workflow != null && mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow().getAction()
                .equalsIgnoreCase(RequestServiceConstants.WF_ACTION_SUBMIT_FEEDBACK)) {
            log.info("Processing feedback submission for booking no: {}", bookingNo);
            handleRSSubmitFeeback(mobileToiletRequest);
        }

        if (workflow != null && mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow().getAction()
                .equalsIgnoreCase(RequestServiceConstants.WF_ACTION_REJECTED_BY_VENDOR)) {
            log.info("Processing rejection by vendor for booking no: {}", bookingNo);
            handleRejectedByVendor(mobileToiletRequest);
        }

        return mobileToiletRequest.getMobileToiletBookingDetail();
    }
    private void handleRSSubmitFeeback(MobileToiletBookingRequest mobileToiletRequest) {
        log.info("Handling mobile Toilet Submit Feedback for request: {}", mobileToiletRequest);
        User citizen = mobileToiletRequest.getRequestInfo().getUserInfo();
        if (!citizen.getUuid().equalsIgnoreCase(mobileToiletRequest.getRequestInfo().getUserInfo().getUuid())) {
            throw new CustomException("Rating Error", " Only owner of the application can submit the feedback !.");
        }
        if (mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow().getRating() == null) {
            throw new CustomException("Rating Error", " Rating has to be provided!");

        } else if (config.getAverageRatingCommentMandatory() != null
                && Integer.parseInt(config.getAverageRatingCommentMandatory()) > mobileToiletRequest
                .getMobileToiletBookingDetail().getWorkflow().getRating()) {

            throw new CustomException("Rating Error", " Comment mandatory for rating "
                    + mobileToiletRequest.getMobileToiletBookingDetail().getWorkflow().getRating());
        }

    }
    private void handleRejectedByVendor(MobileToiletBookingRequest mobileToiletRequest) {
        log.info("Handling rejected by vendor for request: {}", mobileToiletRequest);
        MobileToiletBookingDetail toiletRequest = mobileToiletRequest.getMobileToiletBookingDetail();
        toiletRequest.setVendorId(null);

        org.upyog.rs.web.models.Workflow workflow = toiletRequest.getWorkflow();
        if ((StringUtils.isBlank(workflow.getComments()))) {
            throw new CustomException("", " Comment is mandatory to reject the request for vendor.");
        }
    }

}
