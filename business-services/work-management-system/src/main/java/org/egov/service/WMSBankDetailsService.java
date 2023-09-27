package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
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
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSBankDetailsService {

	 @Autowired
	    private WMSBankDetailsValidator wmsBankDetailsApplicationValidator;
	    @Autowired
	    private WMSBankDetailsApplicationEnrichment wmsBankDetailsApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSBankDetailsConfiguration configuration;
	    @Autowired
	    private WMSBankDetailsRepository wmsBankDetailsRepository;
	   
	
	public List<WMSBankDetailsApplication> registerWMSBankDetailsRequest(WMSBankDetailsRequest wmsBankDetailsRequest) {
		wmsBankDetailsApplicationValidator.validateBankDetailsApplication(wmsBankDetailsRequest);
		wmsBankDetailsApplicationEnrichment.enrichBankDetailsApplication(wmsBankDetailsRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsBankDetailsRequest);

        // Return the response back to user
        return wmsBankDetailsRequest.getWmsBankDetailsApplications();
    }


	public List<WMSBankDetailsApplication> fetchBankDetailsApplications(RequestInfo requestInfo,
			WMSBankDetailsApplicationSearchCriteria bankDetailsApplicationSearchCriteria) {
	
			List<WMSBankDetailsApplication> applications = wmsBankDetailsRepository.getApplications(bankDetailsApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSBankDetailsApplication> updateBankDetailsMaster(WMSBankDetailsRequest bankDetailsRequest) {
		List<WMSBankDetailsApplication> existingApplication = wmsBankDetailsApplicationValidator.validateApplicationUpdateRequest(bankDetailsRequest);
        // Enrich application upon update
        
		wmsBankDetailsApplicationEnrichment.enrichBankDetailsApplicationUpdate(bankDetailsRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), bankDetailsRequest);

        return bankDetailsRequest.getWmsBankDetailsApplications();
	}


	public List<WMSBankDetailsApplication> searchWMSBankDetailsApplications(RequestInfo requestInfo,
			 WMSBankDetailsApplicationSearchCriteria wmsBankDetailsApplicationSearchCriteria) {
		List<WMSBankDetailsApplication> applications = wmsBankDetailsRepository.getApplications(wmsBankDetailsApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public void saveUploadedFile(List<WMSBankDetailsApplication> entities) {
		wmsBankDetailsRepository.saveFile(entities);
		
	}
	
	
	
	
}
