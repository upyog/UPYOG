package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSDepartmentConfiguration;
import org.egov.config.WMSDesignationConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSDepartmentApplicationEnrichment;
import org.egov.enrichment.WMSDesignationApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSDepartmentRepository;
import org.egov.repository.WMSDesignationRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSDepartmentValidator;
import org.egov.validator.WMSDesignationValidator;
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
import org.egov.web.models.WMSDepartmentApplication;
import org.egov.web.models.WMSDepartmentApplicationSearchCriteria;
import org.egov.web.models.WMSDepartmentRequest;
import org.egov.web.models.WMSDesignationApplication;
import org.egov.web.models.WMSDesignationApplicationSearchCriteria;
import org.egov.web.models.WMSDesignationRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSDesignationService {

	 @Autowired
	    private WMSDesignationValidator wmsDesignationApplicationValidator;
	    @Autowired
	    private WMSDesignationApplicationEnrichment wmsDesignationApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSDesignationConfiguration configuration;
	    @Autowired
	    private WMSDesignationRepository wmsDesignationRepository;
	   
	
	public List<WMSDesignationApplication> registerWMSDesignationRequest(WMSDesignationRequest wmsDesignationRequest) {
		wmsDesignationApplicationValidator.validateDesignationApplication(wmsDesignationRequest);
		wmsDesignationApplicationEnrichment.enrichDesignationApplication(wmsDesignationRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsDesignationRequest);

        // Return the response back to user
        return wmsDesignationRequest.getWmsDesignationApplications();
    }


	public List<WMSDesignationApplication> fetchDesignationApplications(RequestInfo requestInfo,
			WMSDesignationApplicationSearchCriteria designationApplicationSearchCriteria) {
	
			List<WMSDesignationApplication> applications = wmsDesignationRepository.getApplications(designationApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSDesignationApplication> updateDesignationMaster(WMSDesignationRequest designationRequest) {
		List<WMSDesignationApplication> existingApplication = wmsDesignationApplicationValidator.validateApplicationUpdateRequest(designationRequest);
        // Enrich application upon update
        
		wmsDesignationApplicationEnrichment.enrichDesignationApplicationUpdate(designationRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), designationRequest);

        return designationRequest.getWmsDesignationApplications();
	}


	public List<WMSDesignationApplication> searchWMSDesignationApplications(RequestInfo requestInfo,
			 WMSDesignationApplicationSearchCriteria wmsDesignationApplicationSearchCriteria) {
		List<WMSDesignationApplication> applications = wmsDesignationRepository.getApplications(wmsDesignationApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	
	
	
	
	
}
