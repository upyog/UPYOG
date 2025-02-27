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
//
//    @Autowired
//    private UserService userService;

    @Autowired
    private CNDServiceRepositoryImpl cndApplicationRepository;

    @Override
    public CNDApplicationDetail createConstructionAndDemolitionRequest(CNDApplicationRequest cndApplicationRequest) {
        log.info("Create CND application for user: " + cndApplicationRequest.getRequestInfo().getUserInfo().getUserName()
                + " for the request: " + cndApplicationRequest.getCndApplication());

        enrichmentService.enrichCreateCNDRequest(cndApplicationRequest);

        workflowService.updateWorkflowStatus(null, cndApplicationRequest);

        // Get the uuid of User from user registry
//        try {
//            String uuid = userService.getUuidExistingOrNewUser(cndApplicationRequest);
//            cndApplicationRequest.getCndApplication().setApplicantUuid(uuid);
//            log.info("Applicant or User Uuid: " + uuid);
//        } catch (Exception e) {
//            log.error("Error while creating user: " + e.getMessage(), e);
//        }

        cndApplicationRepository.saveCNDApplicationDetail(cndApplicationRequest);

        CNDApplicationDetail cndApplicationDetails = cndApplicationRequest.getCndApplication();

        return cndApplicationDetails;
    }

    @Override
	public List<CNDApplicationDetail> getCNDApplicationDetails(RequestInfo requestInfo,
			CNDServiceSearchCriteria cndServiceSearchCriteria) {
		
    	List<CNDApplicationDetail> applications = cndApplicationRepository
				.getCNDApplicationDetail(cndServiceSearchCriteria);

		if (CollectionUtils.isEmpty(applications)) {
			return new ArrayList<>();
		}

		return applications;
	}

    @Override
	public Integer getApplicationsCount(CNDServiceSearchCriteria cndServiceSearchCriteria, RequestInfo requestInfo) {
		
    	cndServiceSearchCriteria.setCountCall(true);
		Integer applicationCount = 0;

		cndServiceSearchCriteria = addCreatedByMeToCriteria(cndServiceSearchCriteria, requestInfo);
		applicationCount = cndApplicationRepository.getCNDApplicationsCount(cndServiceSearchCriteria);

		return applicationCount;
	}
    
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