package org.upyog.cdwm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.constants.CNDConstants;
import org.upyog.cdwm.repository.impl.CNDServiceRepositoryImpl;
import org.upyog.cdwm.service.CNDService;
import org.upyog.cdwm.service.EnrichmentService;
import org.upyog.cdwm.service.UserService;
import org.upyog.cdwm.service.WorkflowService;
import org.upyog.cdwm.web.models.*;
import org.upyog.cdwm.web.models.user.User;
import org.upyog.cdwm.web.models.workflow.State;
import digit.models.coremodels.PaymentRequest;

@Service
public class CNDServiceImpl implements CNDService {

    private static final Logger log = LoggerFactory.getLogger(CNDServiceImpl.class);

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private CNDServiceRepositoryImpl cndApplicationRepository;

	@Autowired
	private UserService userService;

    /**
     * Creates a new Construction and Demolition (CND) application request.
     *
     * @param cndApplicationRequest The request containing application details.
     * @return The created CND application details.
     */
    @Override
    public CNDApplicationDetail createConstructionAndDemolitionRequest(CNDApplicationRequest cndApplicationRequest) {
        log.info("Create CND application for user: {} for the request: {}", 
                 cndApplicationRequest.getRequestInfo().getUserInfo().getUserName(),
                 cndApplicationRequest.getCndApplication());

        // Enrich the CND application request with necessary details
        enrichmentService.enrichCreateCNDRequest(cndApplicationRequest);

        // Update the workflow status
        workflowService.updateWorkflowStatus(null, cndApplicationRequest);

        // Save the application details in the repository
        cndApplicationRepository.saveCNDApplicationDetail(cndApplicationRequest);

        return cndApplicationRequest.getCndApplication();
    }

	/**
	 * Fetches a list of CND application details based on the given search criteria.
	 * If user details are required, it enriches each application with user and address details.
	 *
	 * @param requestInfo              The request metadata containing user context.
	 * @param cndServiceSearchCriteria The criteria used for filtering CND applications.
	 * @return A list of CND application details, enriched with user and address data if requested.
	 */
	@Override
	public List<CNDApplicationDetail> getCNDApplicationDetails(RequestInfo requestInfo,
															   CNDServiceSearchCriteria cndServiceSearchCriteria) {
		List<CNDApplicationDetail> applications = cndApplicationRepository.getCNDApplicationDetail(cndServiceSearchCriteria);

		if (!CollectionUtils.isEmpty(applications) && Boolean.TRUE.equals(cndServiceSearchCriteria.getIsUserDetailRequired())) {
			log.info(
					"Enriching CND applications with user and address details for the following applications: {}",
					applications);

			applications.forEach(application -> {
				User user = userService.getUser(application.getApplicantDetailId(), application.getTenantId(), requestInfo);
				application.setApplicantDetail(userService.convertUserToApplicantDetail(user));
				application.setAddressDetail(userService.convertUserAddressToAddressDetail(user.getAddresses()));
			});
		}

		return applications;
	}


    /**
     * Retrieves the count of applications based on the given search criteria.
     *
     * @param cndServiceSearchCriteria The search criteria for counting applications.
     * @param requestInfo              The request information.
     * @return The count of applications matching the criteria.
     */
    @Override
    public Integer getApplicationsCount(CNDServiceSearchCriteria cndServiceSearchCriteria, RequestInfo requestInfo) {
        cndServiceSearchCriteria.setCountCall(true);
        cndServiceSearchCriteria = addCreatedByMeToCriteria(cndServiceSearchCriteria, requestInfo);
        return cndApplicationRepository.getCNDApplicationsCount(cndServiceSearchCriteria);
    }

    /**
     * Adds filtering criteria to restrict results based on the requesting user's role.
     *
     * @param criteria    The search criteria.
     * @param requestInfo The request information.
     * @return Updated search criteria with filtering applied.
     */
    private CNDServiceSearchCriteria addCreatedByMeToCriteria(CNDServiceSearchCriteria criteria,
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
		if (roles.contains(CNDConstants.CITIZEN)
				&& !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
			log.debug("loading data of created and by me" + uuids.toString());
		}
		return criteria;
	}

	@Override
	public CNDApplicationDetail updateCNDApplicationDetails(CNDApplicationRequest cndApplicationRequest,
			PaymentRequest paymentRequest, String applicationStatus) {
		String applicationNumber = cndApplicationRequest.getCndApplication().getApplicationNumber();
		log.info("Updating Application for Application no: {}", applicationNumber);

		if (applicationNumber == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Application no not valid. Failed to update application status for : " + applicationNumber);
		}

		// If no payment request, update workflow status and process booking request
		if (paymentRequest == null) {
			State state = workflowService.updateWorkflowStatus(null, cndApplicationRequest);
			enrichmentService.enrichCNDApplicationUponUpdate(state.getApplicationStatus(), cndApplicationRequest);

			// If action is APPROVE, create demand
			if (CNDConstants.ACTION_APPROVE
					.equals(cndApplicationRequest.getCndApplication().getWorkflow().getAction())) {
//				demandService.createDemand(cndApplicationRequest);
			}
		}

		// Handle the payment request and update the water tanker booking if applicable
		if (paymentRequest != null) {
			String consumerCode = paymentRequest.getPayment().getPaymentDetails().get(0).getBill().getConsumerCode();
			CNDApplicationDetail cndApplicationDetail = cndApplicationRepository
					.getCNDApplicationDetail(
							CNDServiceSearchCriteria.builder().applicationNumber(consumerCode).build())
					.stream().findFirst().orElse(null);

			if (cndApplicationDetail == null) {
				log.info("Application not found in consumer class while updating status");
				return null;
			}

			// Update the booking details
			cndApplicationDetail.getAuditDetails()
					.setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
			cndApplicationDetail.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
			cndApplicationDetail.setApplicationStatus(applicationStatus);
			//cndApplicationDetail.setPaymentDate(System.currentTimeMillis());

			// Update CND request
			CNDApplicationRequest updatedCNDApplicationRequest = CNDApplicationRequest.builder()
					.requestInfo(paymentRequest.getRequestInfo()).cndApplication(cndApplicationDetail).build();

			log.info("Cnd Application to update application status in consumer: {}", updatedCNDApplicationRequest);
			cndApplicationRepository.updateCNDApplicationDetail(cndApplicationRequest);

			return cndApplicationDetail;
		}

		// If no payment request, just update the CND request
		cndApplicationRepository.updateCNDApplicationDetail(cndApplicationRequest);
		
     	return cndApplicationRequest.getCndApplication();
	}
}
