package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSWorkService {

	 @Autowired
	    private WMSWorkValidator wmsWorkApplicationValidator;
	    @Autowired
	    private WMSWorkApplicationEnrichment wmsWorkApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSWorkConfiguration configuration;
	    @Autowired
	    private WMSWorkRepository wmsWorkRepository;
	   
	
	public List<WMSWorkApplication> registerWMSWOrkRequest(WMSWorkRequest wmsWorkRequest) {
		wmsWorkApplicationValidator.validateWorkApplication(wmsWorkRequest);
		wmsWorkApplicationEnrichment.enrichWorkApplication(wmsWorkRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsWorkRequest);

        // Return the response back to user
        return wmsWorkRequest.getWmsWorkApplications();
    }
	
	public List<WMSWorkApplication> searchWMSWorkApplications(RequestInfo requestInfo, WMSWorkApplicationSearchCriteria wmsWorkApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<WMSWorkApplication> applications = wmsWorkRepository.getApplications(wmsWorkApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
    }
	
	public List<WMSWorkApplication> updateWMSWorkApplication(WMSWorkRequest wmsWorkRequest) {
        // Validate whether the application that is being requested for update indeed exists
        List<WMSWorkApplication> existingApplication = wmsWorkApplicationValidator.validateApplicationUpdateRequest(wmsWorkRequest);
        // Enrich application upon update
        
        wmsWorkApplicationEnrichment.enrichWorkApplicationUpdate(wmsWorkRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), wmsWorkRequest);

        return wmsWorkRequest.getWmsWorkApplications();
    }
}
