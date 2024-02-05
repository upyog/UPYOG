package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSBankDetailsConfiguration;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.config.WMSWorkStatusReportConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSBankDetailsApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.enrichment.WMSWorkStatusReportApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSBankDetailsRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.repository.WMSWorkStatusReportRepository;
import org.egov.validator.WMSBankDetailsValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkStatusReportValidator;
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
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.egov.web.models.WMSWorkStatusReportApplication;
import org.egov.web.models.WMSWorkStatusReportApplicationSearchCriteria;
import org.egov.web.models.WMSWorkStatusReportRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSWorkStatusReportService {

	 @Autowired
	    private WMSWorkStatusReportValidator wmsWorkStatusReportApplicationValidator;
	    @Autowired
	    private WMSWorkStatusReportApplicationEnrichment wmsWorkStatusReportApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSWorkStatusReportConfiguration configuration;
	    @Autowired
	    private WMSWorkStatusReportRepository wmsWorkStatusReportRepository;
	   
	
	public List<WMSWorkStatusReportApplication> registerWMSWorkStatusReportRequest(WMSWorkStatusReportRequest wmsWorkStatusReportRequest) {
		wmsWorkStatusReportApplicationValidator.validateWorkStatusReportApplication(wmsWorkStatusReportRequest);
		wmsWorkStatusReportApplicationEnrichment.enrichWorkStatusReportApplication(wmsWorkStatusReportRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsWorkStatusReportRequest);

        // Return the response back to user
        return wmsWorkStatusReportRequest.getWmsWorkStatusReportApplications();
    }


	public List<WMSWorkStatusReportApplication> fetchWorkStatusReportApplications(RequestInfo requestInfo,
			WMSWorkStatusReportApplicationSearchCriteria workStatusReportApplicationSearchCriteria) {
	
			List<WMSWorkStatusReportApplication> applications = wmsWorkStatusReportRepository.getApplications(workStatusReportApplicationSearchCriteria);

	        // If no applications are found matching the given criteria, return an empty list
	        if(CollectionUtils.isEmpty(applications))
	            return new ArrayList<>();

	        return applications;
	}


	public List<WMSWorkStatusReportApplication> updateWorkStatusReportMaster(WMSWorkStatusReportRequest workStatusReport) {
		List<WMSWorkStatusReportApplication> existingApplication = wmsWorkStatusReportApplicationValidator.validateApplicationUpdateRequest(workStatusReport);
        // Enrich application upon update
        
		wmsWorkStatusReportApplicationEnrichment.enrichWorkStatusReportApplicationUpdate(workStatusReport,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), workStatusReport);

        return workStatusReport.getWmsWorkStatusReportApplications();
	}


	public List<WMSWorkStatusReportApplication> searchWMSWorkStatusReportApplications(RequestInfo requestInfo,
			 WMSWorkStatusReportApplicationSearchCriteria wmsWorkStatusReportApplicationSearchCriteria) {
		List<WMSWorkStatusReportApplication> applications = wmsWorkStatusReportRepository.getApplications(wmsWorkStatusReportApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	
	
	
	
	
}
