package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSDepartmentConfiguration;
import org.egov.config.WMSTenderCategoryConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSDepartmentApplicationEnrichment;
import org.egov.enrichment.WMSTenderCategoryApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSDepartmentRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSTenderCategoryRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSDepartmentValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSTenderCategoryValidator;
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
import org.egov.web.models.WMSDepartmentApplication;
import org.egov.web.models.WMSDepartmentApplicationSearchCriteria;
import org.egov.web.models.WMSDepartmentRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSTenderCategoryApplication;
import org.egov.web.models.WMSTenderCategoryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderCategoryRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSTenderCategoryService {

	 @Autowired
	    private WMSTenderCategoryValidator wmsTenderCategoryApplicationValidator;
	    @Autowired
	    private WMSTenderCategoryApplicationEnrichment wmsTenderCategoryApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSTenderCategoryConfiguration configuration;
	    @Autowired
	    private WMSTenderCategoryRepository wmsTenderCategoryRepository;
	   
	
	public List<WMSTenderCategoryApplication> registerWMSTenderCategoryRequest(WMSTenderCategoryRequest wmsTenderCategoryRequest) {
		wmsTenderCategoryApplicationValidator.validateTenderCategoryApplication(wmsTenderCategoryRequest);
		wmsTenderCategoryApplicationEnrichment.enrichTenderCategoryApplication(wmsTenderCategoryRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsTenderCategoryRequest);

        // Return the response back to user
        return wmsTenderCategoryRequest.getWmsTenderCategoryApplications();
    }


	public List<WMSTenderCategoryApplication> fetchTenderCategoryApplications(RequestInfo requestInfo,
			WMSTenderCategoryApplicationSearchCriteria tenderCategoryApplicationSearchCriteria) {
	
			List<WMSTenderCategoryApplication> applications = wmsTenderCategoryRepository.getApplications(tenderCategoryApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSTenderCategoryApplication> updateTenderCategoryMaster(WMSTenderCategoryRequest tenderCategoryRequest) {
		List<WMSTenderCategoryApplication> existingApplication = wmsTenderCategoryApplicationValidator.validateApplicationUpdateRequest(tenderCategoryRequest);
        // Enrich application upon update
        
		wmsTenderCategoryApplicationEnrichment.enrichTenderCategoryApplicationUpdate(tenderCategoryRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), tenderCategoryRequest);

        return tenderCategoryRequest.getWmsTenderCategoryApplications();
	}


	public List<WMSTenderCategoryApplication> searchWMSTenderCategoryApplications(RequestInfo requestInfo,
			 WMSTenderCategoryApplicationSearchCriteria wmsTenderCategoryApplicationSearchCriteria) {
		List<WMSTenderCategoryApplication> applications = wmsTenderCategoryRepository.getApplications(wmsTenderCategoryApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	
	
	
	
	
}
