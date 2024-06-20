package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSTenderEntryConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSTenderEntryApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSTenderEntryRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSTenderEntryValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSTenderEntryApplication;
import org.egov.web.models.WMSTenderEntryApplicationSearchCriteria;
import org.egov.web.models.WMSTenderEntryRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSTenderEntryService {

	 @Autowired
	    private WMSTenderEntryValidator wmsTenderEntryApplicationValidator;
	    @Autowired
	    private WMSTenderEntryApplicationEnrichment wmsTenderEntryApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSTenderEntryConfiguration configuration;
	    @Autowired
	    private WMSTenderEntryRepository wmsTenderEntryRepository;
	   
	
	public List<WMSTenderEntryApplication> registerWMSTenderEntryRequest(WMSTenderEntryRequest wmsTenderEntryRequest) {
		wmsTenderEntryApplicationValidator.validateTenderEntryApplication(wmsTenderEntryRequest);
		wmsTenderEntryApplicationEnrichment.enrichTenderEntryApplication(wmsTenderEntryRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsTenderEntryRequest);

        // Return the response back to user
        return wmsTenderEntryRequest.getWmsTenderEntryApplications();
    }


	public List<WMSTenderEntryApplication> fetchTenderEntryApplications(RequestInfo requestInfo,
			 WMSTenderEntryApplicationSearchCriteria tenderEntryApplicationSearchCriteria) {
		List<WMSTenderEntryApplication> applications = wmsTenderEntryRepository.getApplications(tenderEntryApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSTenderEntryApplication> updateTenderEntryMaster( WMSTenderEntryRequest tenderEntryRequest) {
		List<WMSTenderEntryApplication> existingApplication = wmsTenderEntryApplicationValidator.validateApplicationUpdateRequest(tenderEntryRequest);
        // Enrich application upon update
        
		wmsTenderEntryApplicationEnrichment.enrichTenderEntryApplicationUpdate(tenderEntryRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), tenderEntryRequest);

        return tenderEntryRequest.getWmsTenderEntryApplications();
	}


	public List<WMSTenderEntryApplication> searchWMSTenderEntryApplications(RequestInfo requestInfo,
			 WMSTenderEntryApplicationSearchCriteria wmsTenderEntryApplicationSearchCriteria) {
		List<WMSTenderEntryApplication> applications = wmsTenderEntryRepository.getApplications(wmsTenderEntryApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
