package org.upyog.cdwm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.config.CNDConfiguration;
import org.upyog.cdwm.constants.CNDConstants;
import org.upyog.cdwm.repository.impl.CNDServiceRepositoryImpl;
import org.upyog.cdwm.service.CNDService;
import org.upyog.cdwm.service.CalculationService;
import org.upyog.cdwm.service.EnrichmentService;
import org.upyog.cdwm.service.UserService;
import org.upyog.cdwm.service.WorkflowService;
import org.upyog.cdwm.util.CNDServiceUtil;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;
import org.upyog.cdwm.web.models.DocumentDetail;
import org.upyog.cdwm.web.models.FacilityCenterDetail;
import org.upyog.cdwm.web.models.WasteTypeDetail;
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
    private CalculationService calculationService;

    @Autowired
    private CNDServiceRepositoryImpl cndApplicationRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CNDConfiguration config;
	
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

        // Enrich only if isUserDetailRequired is true
        if (!CollectionUtils.isEmpty(applications) && Boolean.TRUE.equals(cndServiceSearchCriteria.getIsUserDetailRequired())) {
            log.info("Enriching CND applications with user, address, waste, and document details for applications: {}", applications);

            // Fetch waste, document and deposit center details
            List<WasteTypeDetail> wasteDetails = cndApplicationRepository.getCNDWasteTypeDetail(cndServiceSearchCriteria);
            List<DocumentDetail> documentDetails = cndApplicationRepository.getCNDDocumentDetail(cndServiceSearchCriteria);
            List<FacilityCenterDetail> facilityCenterDetails = cndApplicationRepository.getCNDFacilityCenterDetail(cndServiceSearchCriteria);

            // Group waste, facility, and document details by applicationId
            Map<String, List<WasteTypeDetail>> wasteByAppId = wasteDetails.stream()
                .collect(Collectors.groupingBy(WasteTypeDetail::getApplicationId));

            Map<String, List<DocumentDetail>> docsByAppId = documentDetails.stream()
                .collect(Collectors.groupingBy(DocumentDetail::getApplicationId));
            
            Map<String, List<FacilityCenterDetail>> facilityByAppId = facilityCenterDetails.stream()
                    .collect(Collectors.groupingBy(FacilityCenterDetail::getApplicationId));

            // Enrich each application with waste, document, user, and address details
            for (CNDApplicationDetail application : applications) {
                application.setWasteTypeDetails(wasteByAppId.getOrDefault(application.getApplicationId(), Collections.emptyList()));
                application.setDocumentDetails(docsByAppId.getOrDefault(application.getApplicationId(), Collections.emptyList()));
                
                List<FacilityCenterDetail> facilityDetails = facilityByAppId.get(application.getApplicationId());
                if (facilityDetails != null && !facilityDetails.isEmpty()) {
                    application.setFacilityCenterDetail(facilityDetails.get(0));
                }
                
				application.getApplicantDetail().setAuditDetails(application.getAuditDetails());
				application.getApplicantDetail().setApplicationId(application.getApplicationId());
				application.getAddressDetail().setApplicationId(application.getApplicationId());
				if(config.getIsUserProfileEnabled()){
					User user = userService.getUser(application.getApplicantDetailId(), application.getAddressDetailId() ,application.getTenantId(), requestInfo);
					application.setApplicantDetail(userService.convertUserToApplicantDetail(user));
					application.setAddressDetail(userService.convertUserAddressToAddressDetail(user.getAddresses()));
				}
            }
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

	/**
	 * Updates the details of a CND application based on the provided request data.
	 *
	 * <p>
	 * This method processes a CND application update request. It validates the
	 * application number, updates workflow status, and handles payment requests if
	 * applicable.
	 * </p>
	 *
	 * <p>
	 * If no payment request is present, it updates the workflow status and enriches
	 * application data. If the action is "APPROVE," it triggers the demand creation
	 * process.
	 * </p>
	 *
	 * <p>
	 * If a payment request is provided, it updates the application status, modifies
	 * audit details, and persists the changes to the repository.
	 * </p>
	 *
	 * @param cndApplicationRequest The request containing application details.
	 * @param paymentRequest        The payment details, if applicable.
	 * @param applicationStatus     The new status to be set for the application.
	 * @return The updated CNDApplicationDetail, or null if the update fails.
	 * @throws CustomException If the application number is invalid.
	 */

	@Override
	public CNDApplicationDetail updateCNDApplicationDetails(CNDApplicationRequest cndApplicationRequest,
			PaymentRequest paymentRequest, String applicationStatus) {

		String applicationNumber = cndApplicationRequest.getCndApplication().getApplicationNumber();
		log.info("Updating Application for Application no: {}", applicationNumber);

		if (applicationNumber == null) {
			throw new CustomException("INVALID_BOOKING_CODE",
					"Application no not valid. Failed to update application status for : " + applicationNumber);
		}

		// Fetch existing application details
		CNDApplicationDetail cndApplicationDetail = cndApplicationRepository
				.getCNDApplicationDetail(
						CNDServiceSearchCriteria.builder().applicationNumber(applicationNumber).build())
				.stream().findFirst().orElse(null);

		if (cndApplicationDetail == null) {
			log.warn("Application not found for applicationNumber: {}", applicationNumber);
			return null;
		}

		// If no payment request, update workflow status and process booking request
		if (paymentRequest == null) {
			State state = workflowService.updateWorkflowStatus(null, cndApplicationRequest);
			enrichmentService.enrichCNDApplicationUponUpdate(state.getApplicationStatus(), cndApplicationRequest);
			updateWasteAndDocumentDetails(cndApplicationRequest);

		} else {
			// Handle payment request updates
			log.info("Inside Payment application status update method For Application no: {}", applicationNumber );
			cndApplicationDetail.getAuditDetails()
					.setLastModifiedBy(paymentRequest.getRequestInfo().getUserInfo().getUuid());
			cndApplicationDetail.getAuditDetails().setLastModifiedTime(CNDServiceUtil.getCurrentTimestamp());
			cndApplicationDetail.setApplicationStatus(applicationStatus);
			
	        CNDApplicationRequest updatedRequestWithStatus = CNDApplicationRequest.builder()
	                .requestInfo(paymentRequest.getRequestInfo())
	                .cndApplication(cndApplicationDetail)
	                .build();

	        log.info("Updating CND Application payment scenario in the database: {}", updatedRequestWithStatus);
	        cndApplicationRepository.updateCNDApplicationDetail(updatedRequestWithStatus);
	        return updatedRequestWithStatus.getCndApplication();
		}
		CNDApplicationRequest updatedCNDApplicationRequest = CNDApplicationRequest.builder()
				.requestInfo(cndApplicationRequest.getRequestInfo())
				.cndApplication(cndApplicationRequest.getCndApplication()).build();

		log.info("Updating CND Application in the database: {}", updatedCNDApplicationRequest);
		cndApplicationRepository.updateCNDApplicationDetail(updatedCNDApplicationRequest);
		
		// If action is APPROVE, create demand
		if (CNDConstants.ACTION_APPROVE
			.equals(updatedCNDApplicationRequest.getCndApplication().getWorkflow().getAction())) {
	        calculationService.addCalculation(updatedCNDApplicationRequest);
			}

		return updatedCNDApplicationRequest.getCndApplication();
	}

	
	/**
	 * Processes waste type details and document details within a CND application request.
	 *
	 * <p>This method separates new and existing waste type details and document details.
	 * New records are assigned a unique identifier before being stored, while existing records
	 * are categorized for further processing. After processing, new records are saved in the database,
	 * and all details (new and existing) are merged and updated in the request object.</p>
	 *
	 * @param cndApplicationRequest The CND application request containing waste and document details.
	 */

	private void updateWasteAndDocumentDetails(CNDApplicationRequest cndApplicationRequest) {
	    List<WasteTypeDetail> newWasteDetails = new ArrayList<>();
	    List<WasteTypeDetail> existingWasteDetails = new ArrayList<>();
	    processWasteDetails(cndApplicationRequest, newWasteDetails, existingWasteDetails);
	    
	    List<DocumentDetail> newDocumentDetails = new ArrayList<>();
	    List<DocumentDetail> existingDocumentDetails = new ArrayList<>();
	    processDocumentDetails(cndApplicationRequest, newDocumentDetails, existingDocumentDetails);
	    
	    if (!newWasteDetails.isEmpty() || !newDocumentDetails.isEmpty()) {
	        cndApplicationRepository.saveCNDWasteAndDocumentDetail(newWasteDetails, newDocumentDetails, cndApplicationRequest.getCndApplication().getApplicationId());
	    }
	    
	    // Merge new and existing details before sending to Kafka
	    cndApplicationRequest.getCndApplication().setWasteTypeDetails(
	        Stream.concat(existingWasteDetails.stream(), newWasteDetails.stream()).collect(Collectors.toList())
	    );
	    cndApplicationRequest.getCndApplication().setDocumentDetails(
	        Stream.concat(existingDocumentDetails.stream(), newDocumentDetails.stream()).collect(Collectors.toList())
	    );
	}

	private void processWasteDetails(CNDApplicationRequest cndApplicationRequest, List<WasteTypeDetail> newWasteDetails, List<WasteTypeDetail> existingWasteDetails) {
	    List<WasteTypeDetail> wasteTypeDetails = cndApplicationRequest.getCndApplication().getWasteTypeDetails();
	    if (wasteTypeDetails != null) {
	        for (WasteTypeDetail wasteTypeDetail : wasteTypeDetails) {
	            if (wasteTypeDetail.getWasteTypeId() == null || wasteTypeDetail.getWasteTypeId().isEmpty()) {
	                wasteTypeDetail.setWasteTypeId(CNDServiceUtil.getRandomUUID());
	                wasteTypeDetail.setApplicationId(cndApplicationRequest.getCndApplication().getApplicationId());
	                newWasteDetails.add(wasteTypeDetail);
	            } else {
	                existingWasteDetails.add(wasteTypeDetail);
	            }
	        }
	    }
	}

	private void processDocumentDetails(CNDApplicationRequest cndApplicationRequest, List<DocumentDetail> newDocumentDetails, List<DocumentDetail> existingDocumentDetails) {
	    List<DocumentDetail> documentDetails = cndApplicationRequest.getCndApplication().getDocumentDetails();
	    if (documentDetails != null) {
	        for (DocumentDetail documentDetail : documentDetails) {
	            if (documentDetail.getDocumentDetailId() == null || documentDetail.getDocumentDetailId().isEmpty()) {
	                documentDetail.setDocumentDetailId(CNDServiceUtil.getRandomUUID());
	                documentDetail.setApplicationId(cndApplicationRequest.getCndApplication().getApplicationId());
	                newDocumentDetails.add(documentDetail);
	            } else {
	                existingDocumentDetails.add(documentDetail);
	            }
	        }
	    }
	}



	}
