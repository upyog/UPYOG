package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSPhysicalMileStoneActivityConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSPhysicalMileStoneActivityApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSPhysicalMileStoneActivityRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSPhysicalMileStoneActivityValidator;
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
import org.egov.web.models.WMSPhysicalMileStoneActivityApplication;
import org.egov.web.models.WMSPhysicalMileStoneActivityApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalMileStoneActivityRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSPhysicalMileStoneActivityService {

	 @Autowired
	    private WMSPhysicalMileStoneActivityValidator wmsPhysicalMileStoneActivityApplicationValidator;
	    @Autowired
	    private WMSPhysicalMileStoneActivityApplicationEnrichment wmsPhysicalMileStoneActivityApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSPhysicalMileStoneActivityConfiguration configuration;
	    @Autowired
	    private WMSPhysicalMileStoneActivityRepository wmsPhysicalMileStoneActivityRepository;
	   
	
	public List<WMSPhysicalMileStoneActivityApplication> registerWMSPhysicalMileStoneActivityRequest(WMSPhysicalMileStoneActivityRequest wmsPhysicalMileStoneActivityRequest) {
		wmsPhysicalMileStoneActivityApplicationValidator.validatePhysicalMileStoneActivityApplication(wmsPhysicalMileStoneActivityRequest);
		wmsPhysicalMileStoneActivityApplicationEnrichment.enrichPhysicalMileStoneActivityApplication(wmsPhysicalMileStoneActivityRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsPhysicalMileStoneActivityRequest);

        // Return the response back to user
        return wmsPhysicalMileStoneActivityRequest.getWmsPhysicalMileStoneActivityApplications();
    }


	public List<WMSPhysicalMileStoneActivityApplication> fetchPhysicalMileStoneActivityApplications(RequestInfo requestInfo,
			WMSPhysicalMileStoneActivityApplicationSearchCriteria physicalMileStoneActivityApplicationSearchCriteria) {
	
			List<WMSPhysicalMileStoneActivityApplication> applications = wmsPhysicalMileStoneActivityRepository.getApplications(physicalMileStoneActivityApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSPhysicalMileStoneActivityApplication> updatePhysicalMileStoneActivityMaster(WMSPhysicalMileStoneActivityRequest physicalMileStoneActivityRequest) {
		List<WMSPhysicalMileStoneActivityApplication> existingApplication = wmsPhysicalMileStoneActivityApplicationValidator.validateApplicationUpdateRequest(physicalMileStoneActivityRequest);
        // Enrich application upon update
        
		wmsPhysicalMileStoneActivityApplicationEnrichment.enrichPhysicalMileStoneActivityApplicationUpdate(physicalMileStoneActivityRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), physicalMileStoneActivityRequest);

        return physicalMileStoneActivityRequest.getWmsPhysicalMileStoneActivityApplications();
	}


	public List<WMSPhysicalMileStoneActivityApplication> searchWMSPhysicalMileStoneActivityApplications(RequestInfo requestInfo,
			 WMSPhysicalMileStoneActivityApplicationSearchCriteria wmsPhysicalMileStoneActivityApplicationSearchCriteria) {
		List<WMSPhysicalMileStoneActivityApplication> applications = wmsPhysicalMileStoneActivityRepository.getApplications(wmsPhysicalMileStoneActivityApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	
	
	
	
	
}
