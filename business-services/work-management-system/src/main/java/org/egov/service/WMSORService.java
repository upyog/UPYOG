package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSSORRepository;
import org.egov.validator.WMSSORValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSORService {

	 @Autowired
	    private WMSSORValidator sorApplicationValidator;
	    @Autowired
	    private SORApplicationEnrichment sorApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSConfiguration configuration;
	    @Autowired
	    private WMSSORRepository sorRepository;
	   
	
	public List<ScheduleOfRateApplication> registerWSMRequest(WMSSORRequest sorRequest) {
		sorApplicationValidator.validateSORApplication(sorRequest);
		sorApplicationEnrichment.enrichSORApplication(sorRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), sorRequest);

        // Return the response back to user
        return sorRequest.getScheduleOfRateApplications();
    }
	
	public List<ScheduleOfRateApplication> searchSORApplications(RequestInfo requestInfo, SORApplicationSearchCriteria sorApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<ScheduleOfRateApplication> applications = sorRepository.getApplications(sorApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich mother and father of applicant objects
//        applications.forEach(application -> {
//            birthApplicationEnrichment.enrichFatherApplicantOnSearch(application);
//            birthApplicationEnrichment.enrichMotherApplicantOnSearch(application);
//        });

//        applications.forEach(application -> {
//            application.setWorkflow(Workflow.builder().status(workflowService.getCurrentWorkflow(requestInfo, application.getTenantId(), application.getApplicationNumber()).getState().getState()).build());
//        });

        // Otherwise, return the found applications
        return applications;
    }
	
	public List<ScheduleOfRateApplication> updateBtApplication(WMSSORRequest sorRequest) {
        // Validate whether the application that is being requested for update indeed exists
        List<ScheduleOfRateApplication> existingApplication = sorApplicationValidator.validateApplicationUpdateRequest(sorRequest);
        // Enrich application upon update
        
        sorApplicationEnrichment.enrichSORApplicationUponUpdate(sorRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), sorRequest);

        return sorRequest.getScheduleOfRateApplications();
    }
}
