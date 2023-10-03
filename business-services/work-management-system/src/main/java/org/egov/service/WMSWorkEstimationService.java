package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.config.WMSWorkEstimationConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.enrichment.WMSWorkEstimationApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkEstimationRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkEstimationValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkEstimationApplication;
import org.egov.web.models.WMSWorkEstimationApplicationSearchCriteria;
import org.egov.web.models.WMSWorkEstimationRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSWorkEstimationService {

	 @Autowired
	    private WMSWorkEstimationValidator wmsWorkEstimationApplicationValidator;
	    @Autowired
	    private WMSWorkEstimationApplicationEnrichment wmsWorkEstimationApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSWorkEstimationConfiguration configuration;
	    @Autowired
	    private WMSWorkEstimationRepository wmsWorkEstimationRepository;
	    @Autowired
	    private WorkflowService workflowService;
	
	public List<WMSWorkEstimationApplication> registerWMSWorkEstimationRequest(WMSWorkEstimationRequest wmsWorkEstimationRequest) {
		wmsWorkEstimationApplicationValidator.validateWorkEstimationApplication(wmsWorkEstimationRequest);
		wmsWorkEstimationApplicationEnrichment.enrichWorkEstimationApplication(wmsWorkEstimationRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        workflowService.updateWorkflowStatus(wmsWorkEstimationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsWorkEstimationRequest);

        // Return the response back to user
        return wmsWorkEstimationRequest.getWmsWorkEstimationApplications();
    }


	public List<WMSWorkEstimationApplication> fetchWorkEstimationApplications(RequestInfo requestInfo,
			 WMSWorkEstimationApplicationSearchCriteria workEstimationApplicationSearchCriteria) {
		List<WMSWorkEstimationApplication> applications = wmsWorkEstimationRepository.getApplications(workEstimationApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSWorkEstimationApplication> updateWorkEstimationMaster(
			 WMSWorkEstimationRequest workEstimationRequest) {
		List<WMSWorkEstimationApplication> existingApplication = wmsWorkEstimationApplicationValidator.validateApplicationUpdateRequest(workEstimationRequest);
        // Enrich application upon update
        
		wmsWorkEstimationApplicationEnrichment.enrichWorkEstimationApplicationUpdate(workEstimationRequest,existingApplication);
        workflowService.updateWorkflowStatus(workEstimationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), workEstimationRequest);

        return workEstimationRequest.getWmsWorkEstimationApplications();
	}


	public List<WMSWorkEstimationApplication> searchWMSWorkEstimationApplications(RequestInfo requestInfo,
			 WMSWorkEstimationApplicationSearchCriteria wmsWorkEstimationApplicationSearchCriteria) {
		List<WMSWorkEstimationApplication> applications = wmsWorkEstimationRepository.getApplications(wmsWorkEstimationApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
