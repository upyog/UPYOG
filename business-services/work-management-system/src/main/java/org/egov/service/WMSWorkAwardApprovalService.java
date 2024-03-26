package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkAwardApprovalConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.enrichment.WMSWorkAwardApprovalApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkAwardApprovalRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkAwardApprovalValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkAwardApprovalApplication;
import org.egov.web.models.WMSWorkAwardApprovalApplicationSearchCriteria;
import org.egov.web.models.WMSWorkAwardApprovalRequest;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSWorkAwardApprovalService {

	 @Autowired
	    private WMSWorkAwardApprovalValidator wmsWorkAwardApprovalApplicationValidator;
	    @Autowired
	    private WMSWorkAwardApprovalApplicationEnrichment wmsWorkAwardApprovalApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSWorkAwardApprovalConfiguration configuration;
	    @Autowired
	    private WMSWorkAwardApprovalRepository wmsWorkAwardApprovalRepository;
	   
	
	public List<WMSWorkAwardApprovalApplication> registerWMSWorkAwardApprovalRequest(WMSWorkAwardApprovalRequest wmsWorkAwardApprovalRequest) {
		wmsWorkAwardApprovalApplicationValidator.validateWorkAwardApprovalApplication(wmsWorkAwardApprovalRequest);
		wmsWorkAwardApprovalApplicationEnrichment.enrichWorkAwardApprovalApplication(wmsWorkAwardApprovalRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsWorkAwardApprovalRequest);

        // Return the response back to user
        return wmsWorkAwardApprovalRequest.getWmsWorkAwardApprovalApplications();
    }


	public List<WMSWorkAwardApprovalApplication> fetchWorkAwardApprovalApplications(RequestInfo requestInfo,
			 WMSWorkAwardApprovalApplicationSearchCriteria workAwardApprovalApplicationSearchCriteria) {
		List<WMSWorkAwardApprovalApplication> applications = wmsWorkAwardApprovalRepository.getApplications(workAwardApprovalApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSWorkAwardApprovalApplication> updateWorkAwardApprovalMaster(
			 WMSWorkAwardApprovalRequest workAwardApprovalRequest) {
		List<WMSWorkAwardApprovalApplication> existingApplication = wmsWorkAwardApprovalApplicationValidator.validateApplicationUpdateRequest(workAwardApprovalRequest);
        // Enrich application upon update
        
		wmsWorkAwardApprovalApplicationEnrichment.enrichWorkAwardApprovalApplicationUpdate(workAwardApprovalRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), workAwardApprovalRequest);

        return workAwardApprovalRequest.getWmsWorkAwardApprovalApplications();
	}


	public List<WMSWorkAwardApprovalApplication> searchWMSWorkAwardApprovalApplications(RequestInfo requestInfo,
			 WMSWorkAwardApprovalApplicationSearchCriteria wmsWorkAwardApprovalApplicationSearchCriteria) {
		List<WMSWorkAwardApprovalApplication> applications = wmsWorkAwardApprovalRepository.getApplications(wmsWorkAwardApprovalApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
