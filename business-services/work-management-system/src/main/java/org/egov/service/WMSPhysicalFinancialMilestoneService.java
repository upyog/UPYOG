package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSPhysicalFinancialMilestoneConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSPhysicalFinancialMilestoneApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSPhysicalFinancialMilestoneRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSPhysicalFinancialMilestoneValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplication;
import org.egov.web.models.WMSPhysicalFinancialMilestoneApplicationSearchCriteria;
import org.egov.web.models.WMSPhysicalFinancialMilestoneRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSPhysicalFinancialMilestoneService {

	 @Autowired
	    private WMSPhysicalFinancialMilestoneValidator wmsPhysicalFinancialMilestoneApplicationValidator;
	    @Autowired
	    private WMSPhysicalFinancialMilestoneApplicationEnrichment wmsPhysicalFinancialMilestoneApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSPhysicalFinancialMilestoneConfiguration configuration;
	    @Autowired
	    private WMSPhysicalFinancialMilestoneRepository wmsPhysicalFinancialMilestoneRepository;
	   
	
	public List<WMSPhysicalFinancialMilestoneApplication> registerWMSPhysicalFinancialMilestoneRequest(WMSPhysicalFinancialMilestoneRequest wmsPhysicalFinancialMilestoneRequest) {
		wmsPhysicalFinancialMilestoneApplicationValidator.validatePhysicalFinancialMilestoneApplication(wmsPhysicalFinancialMilestoneRequest);
		wmsPhysicalFinancialMilestoneApplicationEnrichment.enrichPhysicalFinancialMilestoneApplication(wmsPhysicalFinancialMilestoneRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsPhysicalFinancialMilestoneRequest);

        // Return the response back to user
        return wmsPhysicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications();
    }


	public List<WMSPhysicalFinancialMilestoneApplication> fetchPhysicalFinancialMilestoneApplications(
			RequestInfo requestInfo,
			 WMSPhysicalFinancialMilestoneApplicationSearchCriteria physicalFinancialMilestoneApplicationSearchCriteria) {
		List<WMSPhysicalFinancialMilestoneApplication> applications = wmsPhysicalFinancialMilestoneRepository.getApplications(physicalFinancialMilestoneApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSPhysicalFinancialMilestoneApplication> updatePhysicalFinancialMilestoneMaster(
			 WMSPhysicalFinancialMilestoneRequest physicalFinancialMilestoneRequest) {
		List<WMSPhysicalFinancialMilestoneApplication> existingApplication = wmsPhysicalFinancialMilestoneApplicationValidator.validateApplicationUpdateRequest(physicalFinancialMilestoneRequest);
        // Enrich application upon update
        
		wmsPhysicalFinancialMilestoneApplicationEnrichment.enrichPhysicalFinancialMilestoneApplicationUpdate(physicalFinancialMilestoneRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), physicalFinancialMilestoneRequest);

        return physicalFinancialMilestoneRequest.getWmsPhysicalFinancialMilestoneApplications();
	}


	public List<WMSPhysicalFinancialMilestoneApplication> searchWMSPhysicalFinancialMilestoneApplications(
			RequestInfo requestInfo,
			WMSPhysicalFinancialMilestoneApplicationSearchCriteria wmsPhysicalFinancialMilestoneApplicationSearchCriteria) {
		List<WMSPhysicalFinancialMilestoneApplication> applications = wmsPhysicalFinancialMilestoneRepository.getApplications(wmsPhysicalFinancialMilestoneApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
