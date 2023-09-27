package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSFunctionConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSFunctionApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSFunctionRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSFunctionValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.WMSBankDetailsApplication;
import org.egov.web.models.WMSBankDetailsApplicationSearchCriteria;
import org.egov.web.models.WMSBankDetailsRequest;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSFunctionApplication;
import org.egov.web.models.WMSFunctionApplicationSearchCriteria;
import org.egov.web.models.WMSFunctionRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSFunctionService {

	 @Autowired
	    private WMSFunctionValidator wmsFunctionApplicationValidator;
	    @Autowired
	    private WMSFunctionApplicationEnrichment wmsFunctionApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSFunctionConfiguration configuration;
	    @Autowired
	    private WMSFunctionRepository wmsFunctionRepository;
	   
	
	public List<WMSFunctionApplication> registerWMSFunctionRequest(WMSFunctionRequest wmsFunctionRequest) {
		wmsFunctionApplicationValidator.validateFunctionApplication(wmsFunctionRequest);
		wmsFunctionApplicationEnrichment.enrichFunctionApplication(wmsFunctionRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsFunctionRequest);

        // Return the response back to user
        return wmsFunctionRequest.getWmsFunctionApplications();
    }


	public List<WMSFunctionApplication> fetchFunctionApplications(RequestInfo requestInfo,
			WMSFunctionApplicationSearchCriteria functionApplicationSearchCriteria) {
	
			List<WMSFunctionApplication> applications = wmsFunctionRepository.getApplications(functionApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSFunctionApplication> updateFunctionMaster(WMSFunctionRequest functionRequest) {
		List<WMSFunctionApplication> existingApplication = wmsFunctionApplicationValidator.validateApplicationUpdateRequest(functionRequest);
        // Enrich application upon update
        
		wmsFunctionApplicationEnrichment.enrichFunctionApplicationUpdate(functionRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), functionRequest);

        return functionRequest.getWmsFunctionApplications();
	}


	public List<WMSFunctionApplication> searchWMSFunctionApplications(RequestInfo requestInfo,
			 WMSFunctionApplicationSearchCriteria wmsFunctionApplicationSearchCriteria) {
		List<WMSFunctionApplication> applications = wmsFunctionRepository.getApplications(wmsFunctionApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	
	
	
	
	
}
