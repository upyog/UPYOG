package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.config.WMSWorkOrderConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.enrichment.WMSWorkOrderApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkOrderRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkOrderValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkOrderApplication;
import org.egov.web.models.WMSWorkOrderApplicationSearchCriteria;
import org.egov.web.models.WMSWorkOrderRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSWorkOrderService {

	 @Autowired
	    private WMSWorkOrderValidator wmsWorkOrderApplicationValidator;
	    @Autowired
	    private WMSWorkOrderApplicationEnrichment wmsWorkOrderApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSWorkOrderConfiguration configuration;
	    @Autowired
	    private WMSWorkOrderRepository wmsWorkOrderRepository;
	   
	
	public List<WMSWorkOrderApplication> registerWMSWorkOrderRequest(WMSWorkOrderRequest wmsWorkOrderRequest) {
		wmsWorkOrderApplicationValidator.validateWorkOrderApplication(wmsWorkOrderRequest);
		wmsWorkOrderApplicationEnrichment.enrichWorkOrderApplication(wmsWorkOrderRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsWorkOrderRequest);

        // Return the response back to user
        return wmsWorkOrderRequest.getWmsWorkOrderApplications();
    }


	public List<WMSWorkOrderApplication> fetchWorkOrderApplications(RequestInfo requestInfo,
			 WMSWorkOrderApplicationSearchCriteria workOrderApplicationSearchCriteria) {
		List<WMSWorkOrderApplication> applications = wmsWorkOrderRepository.getApplications(workOrderApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSWorkOrderApplication> updateWorkOrderMaster(@Valid WMSWorkOrderRequest workOrderRequest) {
		List<WMSWorkOrderApplication> existingApplication = wmsWorkOrderApplicationValidator.validateApplicationUpdateRequest(workOrderRequest);
        // Enrich application upon update
        
		wmsWorkOrderApplicationEnrichment.enrichWorkOrderApplicationUpdate(workOrderRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), workOrderRequest);

        return workOrderRequest.getWmsWorkOrderApplications();
	}


	public List<WMSWorkOrderApplication> searchWMSWorkOrderApplications(RequestInfo requestInfo,
			 WMSWorkOrderApplicationSearchCriteria wmsWorkOrderApplicationSearchCriteria) {
		List<WMSWorkOrderApplication> applications = wmsWorkOrderRepository.getApplications(wmsWorkOrderApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
