package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
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
public class WMSContractorService {

	 @Autowired
	    private WMSContractorValidator wmsContractorApplicationValidator;
	    @Autowired
	    private WMSContractorApplicationEnrichment wmsContractorApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSContractorConfiguration configuration;
	    @Autowired
	    private WMSContractorRepository wmsContractorRepository;
	   
	
	public List<WMSContractorApplication> registerWMSContractorRequest(WMSContractorRequest wmsContractorRequest) {
		wmsContractorApplicationValidator.validateContractorApplication(wmsContractorRequest);
		wmsContractorApplicationEnrichment.enrichContractorApplication(wmsContractorRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsContractorRequest);

        // Return the response back to user
        return wmsContractorRequest.getWmsContractorApplications();
    }
	
	
	
	
}
