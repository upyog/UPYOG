package org.upyog.cdwm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.cdwm.constants.CNDConstants;
import org.upyog.cdwm.repository.impl.CNDServiceRepositoryImpl;
import org.upyog.cdwm.service.CNDService;
import org.upyog.cdwm.service.EnrichmentService;
import org.upyog.cdwm.service.WorkflowService;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;
import org.upyog.cdwm.web.models.CNDServiceSearchCriteria;

@Service
public class CNDServiceImpl implements CNDService {

    private static final Logger log = LoggerFactory.getLogger(CNDServiceImpl.class);

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private CNDServiceRepositoryImpl cndApplicationRepository;

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
     * Retrieves a list of CND application details based on search criteria.
     *
     * @param requestInfo              The request information.
     * @param cndServiceSearchCriteria The search criteria for fetching applications.
     * @return List of CND application details.
     */
    @Override
    public List<CNDApplicationDetail> getCNDApplicationDetails(RequestInfo requestInfo,
                                                                CNDServiceSearchCriteria cndServiceSearchCriteria) {
        List<CNDApplicationDetail> applications = cndApplicationRepository.getCNDApplicationDetail(cndServiceSearchCriteria);
        return CollectionUtils.isEmpty(applications) ? new ArrayList<>() : applications;
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
}
