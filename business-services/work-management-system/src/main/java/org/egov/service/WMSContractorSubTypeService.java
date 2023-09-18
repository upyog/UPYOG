package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSContractorSubTypeConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSContractorSubTypeApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSContractorSubTypeRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorSubTypeValidator;
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
import org.egov.web.models.WMSContractorSubTypeApplication;
import org.egov.web.models.WMSContractorSubTypeApplicationSearchCriteria;
import org.egov.web.models.WMSContractorSubTypeRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSContractorSubTypeService {

	 @Autowired
	    private WMSContractorSubTypeValidator wmsContractorSubTypeApplicationValidator;
	    @Autowired
	    private WMSContractorSubTypeApplicationEnrichment wmsContractorSubTypeApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSContractorSubTypeConfiguration configuration;
	    @Autowired
	    private WMSContractorSubTypeRepository wmsContractorSubTypeRepository;
	   
	
	public List<WMSContractorSubTypeApplication> registerWMSContractorSubTypeRequest(WMSContractorSubTypeRequest wmsContractorSubTypeRequest) {
		wmsContractorSubTypeApplicationValidator.validateContractorSubTypeApplication(wmsContractorSubTypeRequest);
		wmsContractorSubTypeApplicationEnrichment.enrichContractorSubTypeApplication(wmsContractorSubTypeRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsContractorSubTypeRequest);

        // Return the response back to user
        return wmsContractorSubTypeRequest.getWmsContractorSubTypeApplications();
    }


	public List<WMSContractorSubTypeApplication> fetchContractorSubTypeApplications(RequestInfo requestInfo,
			WMSContractorSubTypeApplicationSearchCriteria contractorSubTypeApplicationSearchCriteria) {
	
			List<WMSContractorSubTypeApplication> applications = wmsContractorSubTypeRepository.getApplications(contractorSubTypeApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSContractorSubTypeApplication> updateContractorSubTypeMaster(WMSContractorSubTypeRequest contractorSubTypeRequest) {
		List<WMSContractorSubTypeApplication> existingApplication = wmsContractorSubTypeApplicationValidator.validateApplicationUpdateRequest(contractorSubTypeRequest);
        // Enrich application upon update
        
		wmsContractorSubTypeApplicationEnrichment.enrichContractorSubTypeApplicationUpdate(contractorSubTypeRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), contractorSubTypeRequest);

        return contractorSubTypeRequest.getWmsContractorSubTypeApplications();
	}


	public List<WMSContractorSubTypeApplication> searchWMSContractorSubTypeApplications(RequestInfo requestInfo,
			 WMSContractorSubTypeApplicationSearchCriteria wmsContractorSubTypeApplicationSearchCriteria) {
		List<WMSContractorSubTypeApplication> applications = wmsContractorSubTypeRepository.getApplications(wmsContractorSubTypeApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
