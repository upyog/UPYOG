package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractAgreementConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractAgreementApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractAgreementRepository;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractAgreementValidator;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractAgreementApplication;
import org.egov.web.models.WMSContractAgreementApplicationSearchCriteria;
import org.egov.web.models.WMSContractAgreementRequest;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSContractAgreementService {

	 @Autowired
	    private WMSContractAgreementValidator wmsContractAgreementApplicationValidator;
	    @Autowired
	    private WMSContractAgreementApplicationEnrichment wmsContractAgreementApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSContractAgreementConfiguration configuration;
	    @Autowired
	    private WMSContractAgreementRepository wmsContractAgreementRepository;
	   
	
	public List<WMSContractAgreementApplication> registerWMSContractAgreementRequest(WMSContractAgreementRequest wmsContractAgreementRequest) {
		wmsContractAgreementApplicationValidator.validateContractAgreementApplication(wmsContractAgreementRequest);
		wmsContractAgreementApplicationEnrichment.enrichContractAgreementApplication(wmsContractAgreementRequest);
		//for(int i=0;i<wmsContractAgreementRequest.getWmsContractAgreementApplications().get(0).getAgreementInfo().size();i++) {
			
			
			producer.push(configuration.getCreateTopic(), wmsContractAgreementRequest);
			
		
		//}
        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       //producer.push(configuration.getCreateTopic(), wmsContractAgreementRequest);

        // Return the response back to user
        return wmsContractAgreementRequest.getWmsContractAgreementApplications();
    }


	public List<WMSContractAgreementApplication> fetchContractAgreementApplications(RequestInfo requestInfo,
			 WMSContractAgreementApplicationSearchCriteria contractAgreementApplicationSearchCriteria) {
		List<WMSContractAgreementApplication> applications = wmsContractAgreementRepository.getApplications(contractAgreementApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSContractAgreementApplication> updateContractAgreementMaster(
			 WMSContractAgreementRequest contractAgreementRequest) {
		List<WMSContractAgreementApplication> existingApplication = wmsContractAgreementApplicationValidator.validateApplicationUpdateRequest(contractAgreementRequest);
        // Enrich application upon update
        
		wmsContractAgreementApplicationEnrichment.enrichContractAgreementApplicationUpdate(contractAgreementRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), contractAgreementRequest);

        return contractAgreementRequest.getWmsContractAgreementApplications();
	}


	public List<WMSContractAgreementApplication> searchWMSContractAgreementApplications(RequestInfo requestInfo,
			 WMSContractAgreementApplicationSearchCriteria wmsContractAgreementApplicationSearchCriteria) {
		List<WMSContractAgreementApplication> applications = wmsContractAgreementRepository.getApplications(wmsContractAgreementApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
