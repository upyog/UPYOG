package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSMeasurementBookConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSMeasurementBookApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSMeasurementBookRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSMeasurementBookValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorApplicationSearchCriteria;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSMeasurementBookApplication;
import org.egov.web.models.WMSMeasurementBookApplicationSearchCriteria;
import org.egov.web.models.WMSMeasurementBookRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSMeasurementBookService {

	 @Autowired
	    private WMSMeasurementBookValidator wmsMeasurementBookApplicationValidator;
	    @Autowired
	    private WMSMeasurementBookApplicationEnrichment wmsMeasurementBookApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSMeasurementBookConfiguration configuration;
	    @Autowired
	    private WMSMeasurementBookRepository wmsMeasurementBookRepository;
	   
	
	public List<WMSMeasurementBookApplication> registerWMSMeasurementBookRequest(WMSMeasurementBookRequest wmsMeasurementBookRequest) {
		wmsMeasurementBookApplicationValidator.validateMeasurementBookApplication(wmsMeasurementBookRequest);
		wmsMeasurementBookApplicationEnrichment.enrichMeasurementBookApplication(wmsMeasurementBookRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsMeasurementBookRequest);

        // Return the response back to user
        return wmsMeasurementBookRequest.getWmsMeasurementBookApplications();
    }


	public List<WMSMeasurementBookApplication> fetchMeasurementBookApplications(RequestInfo requestInfo,
			 WMSMeasurementBookApplicationSearchCriteria measurementBookApplicationSearchCriteria) {
		
			
			List<WMSMeasurementBookApplication> applications = wmsMeasurementBookRepository.getApplications(measurementBookApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSMeasurementBookApplication> updateMeasurementBookMaster(
			 WMSMeasurementBookRequest measurementBookRequest) {
		List<WMSMeasurementBookApplication> existingApplication = wmsMeasurementBookApplicationValidator.validateApplicationUpdateRequest(measurementBookRequest);
        // Enrich application upon update
        
		wmsMeasurementBookApplicationEnrichment.enrichMeasurementBookApplicationUpdate(measurementBookRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), measurementBookRequest);

        return measurementBookRequest.getWmsMeasurementBookApplications();
	}


	public List<WMSMeasurementBookApplication> searchWMSMeasurementBookApplications(RequestInfo requestInfo,
			 WMSMeasurementBookApplicationSearchCriteria wmsMeasurementBookApplicationSearchCriteria) {
		List<WMSMeasurementBookApplication> applications = wmsMeasurementBookRepository.getApplications(wmsMeasurementBookApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
