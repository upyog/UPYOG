package org.upyog.cdwm.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.cdwm.repository.impl.CNDServiceRepositoryImpl;
import org.upyog.cdwm.service.CNDService;
import org.upyog.cdwm.service.EnrichmentService;
import org.upyog.cdwm.web.models.CNDApplicationDetail;
import org.upyog.cdwm.web.models.CNDApplicationRequest;

@Service
public class CNDServiceImpl implements CNDService {

    private static final Logger log = LoggerFactory.getLogger(CNDServiceImpl.class);

    @Autowired
    private EnrichmentService enrichmentService;

//    @Autowired
//    private WorkflowService workflowService;
//
//    @Autowired
//    private UserService userService;

    @Autowired
    private CNDServiceRepositoryImpl cndApplicationRepository;

    @Override
    public CNDApplicationDetail createConstructionAndDemolitionRequest(CNDApplicationRequest cndApplicationRequest) {
        log.info("Create CND application for user: " + cndApplicationRequest.getRequestInfo().getUserInfo().getUuid()
                + " for the request: " + cndApplicationRequest.getCndApplication());

        enrichmentService.enrichCreateCNDRequest(cndApplicationRequest);

      //  workflowService.updateWorkflowStatus(null, cndApplicationRequest);

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
}