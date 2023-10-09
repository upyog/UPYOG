package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSDepartmentConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSDepartmentApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSDepartmentRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSDepartmentValidator;
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
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSDepartmentService {

	 @Autowired
	    private WMSDepartmentValidator wmsDepartmentApplicationValidator;
	    @Autowired
	    private WMSDepartmentApplicationEnrichment wmsDepartmentApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSDepartmentConfiguration configuration;
	    @Autowired
	    private WMSDepartmentRepository wmsDepartmentRepository;
	   
	
	public List<WMSDepartmentApplication> registerWMSDepartmentRequest(WMSDepartmentRequest wmsDepartmentRequest) {
		wmsDepartmentApplicationValidator.validateDepartmentApplication(wmsDepartmentRequest);
		wmsDepartmentApplicationEnrichment.enrichDepartmentApplication(wmsDepartmentRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsDepartmentRequest);

        // Return the response back to user
        return wmsDepartmentRequest.getWmsDepartmentApplications();
    }


	public List<WMSDepartmentApplication> fetchDepartmentApplications(RequestInfo requestInfo,
			WMSDepartmentApplicationSearchCriteria departmentApplicationSearchCriteria) {
	
			List<WMSDepartmentApplication> applications = wmsDepartmentRepository.getApplications(departmentApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSDepartmentApplication> updateDepartmentMaster(WMSDepartmentRequest departmentRequest) {
		List<WMSDepartmentApplication> existingApplication = wmsDepartmentApplicationValidator.validateApplicationUpdateRequest(departmentRequest);
        // Enrich application upon update
        
		wmsDepartmentApplicationEnrichment.enrichDepartmentApplicationUpdate(departmentRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), departmentRequest);

        return departmentRequest.getWmsDepartmentApplications();
	}


	public List<WMSDepartmentApplication> searchWMSDepartmentApplications(RequestInfo requestInfo,
			 WMSDepartmentApplicationSearchCriteria wmsDepartmentApplicationSearchCriteria) {
		List<WMSDepartmentApplication> applications = wmsDepartmentRepository.getApplications(wmsDepartmentApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	
	
	
	
	
}
