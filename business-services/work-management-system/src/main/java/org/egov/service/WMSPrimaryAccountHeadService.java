package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSContractorSubTypeConfiguration;
import org.egov.config.WMSPrimaryAccountHeadConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSContractorSubTypeApplicationEnrichment;
import org.egov.enrichment.WMSPrimaryAccountHeadApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSContractorSubTypeRepository;
import org.egov.repository.WMSPrimaryAccountHeadRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorSubTypeValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSPrimaryAccountHeadValidator;
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
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeRequest;
import org.egov.web.models.WMSPrimaryAccountHeadApplication;
import org.egov.web.models.WMSPrimaryAccountHeadApplicationSearchCriteria;
import org.egov.web.models.WMSPrimaryAccountHeadRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSPrimaryAccountHeadService {

	 @Autowired
	    private WMSPrimaryAccountHeadValidator wmsPrimaryAccountHeadApplicationValidator;
	    @Autowired
	    private WMSPrimaryAccountHeadApplicationEnrichment wmsPrimaryAccountHeadApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSPrimaryAccountHeadConfiguration configuration;
	    @Autowired
	    private WMSPrimaryAccountHeadRepository wmsPrimaryAccountHeadRepository;
	   
	
	public List<WMSPrimaryAccountHeadApplication> registerWMSPrimaryAccountHeadRequest(WMSPrimaryAccountHeadRequest wmsPrimaryAccountHeadRequest) {
		wmsPrimaryAccountHeadApplicationValidator.validatePrimaryAccountHeadApplication(wmsPrimaryAccountHeadRequest);
		wmsPrimaryAccountHeadApplicationEnrichment.enrichPrimaryAccountHeadApplication(wmsPrimaryAccountHeadRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsPrimaryAccountHeadRequest);

        // Return the response back to user
        return wmsPrimaryAccountHeadRequest.getWmsPrimaryAccountHeadApplications();
    }


	public List<WMSPrimaryAccountHeadApplication> fetchPrimaryAccountHeadApplications(RequestInfo requestInfo,
			WMSPrimaryAccountHeadApplicationSearchCriteria primaryAccountHeadApplicationSearchCriteria) {
	
			List<WMSPrimaryAccountHeadApplication> applications = wmsPrimaryAccountHeadRepository.getApplications(primaryAccountHeadApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSPrimaryAccountHeadApplication> updatePrimaryAccountHeadMaster(WMSPrimaryAccountHeadRequest primaryAccountHeadRequest) {
		List<WMSPrimaryAccountHeadApplication> existingApplication = wmsPrimaryAccountHeadApplicationValidator.validateApplicationUpdateRequest(primaryAccountHeadRequest);
        // Enrich application upon update
        
		wmsPrimaryAccountHeadApplicationEnrichment.enrichPrimaryAccountHeadApplicationUpdate(primaryAccountHeadRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), primaryAccountHeadRequest);

        return primaryAccountHeadRequest.getWmsPrimaryAccountHeadApplications();
	}


	public List<WMSPrimaryAccountHeadApplication> searchWMSPrimaryAccountHeadApplications(RequestInfo requestInfo,
			 WMSPrimaryAccountHeadApplicationSearchCriteria wmsPrimaryAccountHeadApplicationSearchCriteria) {
		List<WMSPrimaryAccountHeadApplication> applications = wmsPrimaryAccountHeadRepository.getApplications(wmsPrimaryAccountHeadApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
