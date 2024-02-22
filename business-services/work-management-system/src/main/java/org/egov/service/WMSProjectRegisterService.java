package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSProjectRegisterConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSProjectRegisterApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSProjectRegisterRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSProjectRegisterValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSProjectRegisterApplication;
import org.egov.web.models.WMSProjectRegisterApplicationSearchCriteria;
import org.egov.web.models.WMSProjectRegisterRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSProjectRegisterService {

	 @Autowired
	    private WMSProjectRegisterValidator wmsProjectRegisterApplicationValidator;
	    @Autowired
	    private WMSProjectRegisterApplicationEnrichment wmsProjectRegisterApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSProjectRegisterConfiguration configuration;
	    @Autowired
	    private WMSProjectRegisterRepository wmsProjectRegisterRepository;
	   
	
	public List<WMSProjectRegisterApplication> registerWMSProjectRegisterRequest(WMSProjectRegisterRequest wmsProjectRegisterRequest) {
		wmsProjectRegisterApplicationValidator.validateProjectRegisterApplication(wmsProjectRegisterRequest);
		wmsProjectRegisterApplicationEnrichment.enrichProjectRegisterApplication(wmsProjectRegisterRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsProjectRegisterRequest);

        // Return the response back to user
        return wmsProjectRegisterRequest.getWmsProjectRegisterApplications();
    }


	public List<WMSProjectRegisterApplication> fetchProjectRegisterApplications(RequestInfo requestInfo,
			WMSProjectRegisterApplicationSearchCriteria projectRegisterApplicationSearchCriteria) {
	
			List<WMSProjectRegisterApplication> applications = wmsProjectRegisterRepository.getApplications(projectRegisterApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSProjectRegisterApplication> updateProjectRegisterMaster(WMSProjectRegisterRequest projectRegisterRequest) {
		List<WMSProjectRegisterApplication> existingApplication = wmsProjectRegisterApplicationValidator.validateApplicationUpdateRequest(projectRegisterRequest);
        // Enrich application upon update
        
		wmsProjectRegisterApplicationEnrichment.enrichProjectRegisterApplicationUpdate(projectRegisterRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), projectRegisterRequest);

        return projectRegisterRequest.getWmsProjectRegisterApplications();
	}


	public List<WMSProjectRegisterApplication> searchWMSProjectRegisterApplications(RequestInfo requestInfo,
			 WMSProjectRegisterApplicationSearchCriteria wmsProjectRegisterApplicationSearchCriteria) {
		List<WMSProjectRegisterApplication> applications = wmsProjectRegisterRepository.getApplications(wmsProjectRegisterApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
