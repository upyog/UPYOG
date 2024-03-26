package org.egov.service;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.config.WMSConfiguration;
import org.egov.config.WMSContractorConfiguration;
import org.egov.config.WMSRunningAccountFinalBillConfiguration;
import org.egov.config.WMSWorkConfiguration;
import org.egov.enrichment.SORApplicationEnrichment;
import org.egov.enrichment.WMSContractorApplicationEnrichment;
import org.egov.enrichment.WMSRunningAccountFinalBillApplicationEnrichment;
import org.egov.enrichment.WMSWorkApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.WMSContractorRepository;
import org.egov.repository.WMSRunningAccountFinalBillRepository;
import org.egov.repository.WMSSORRepository;
import org.egov.repository.WMSWorkRepository;
import org.egov.validator.WMSContractorValidator;
import org.egov.validator.WMSRunningAccountFinalBillValidator;
import org.egov.validator.WMSSORValidator;
import org.egov.validator.WMSWorkValidator;
import org.egov.web.models.SORApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSContractorRequest;
import org.egov.web.models.WMSRunningAccountFinalBillApplication;
import org.egov.web.models.WMSRunningAccountFinalBillApplicationSearchCriteria;
import org.egov.web.models.WMSRunningAccountFinalBillRequest;
import org.egov.web.models.WMSSORRequest;
import org.egov.web.models.WMSWorkApplication;
import org.egov.web.models.WMSWorkApplicationSearchCriteria;
import org.egov.web.models.WMSWorkRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


@Service
public class WMSRunningAccountFinalBillService {

	 @Autowired
	    private WMSRunningAccountFinalBillValidator wmsRunningAccountFinalBillApplicationValidator;
	    @Autowired
	    private WMSRunningAccountFinalBillApplicationEnrichment wmsRunningAccountFinalBillApplicationEnrichment;
	    @Autowired
	    private Producer producer;
	    @Autowired
	    private WMSRunningAccountFinalBillConfiguration configuration;
	    @Autowired
	    private WMSRunningAccountFinalBillRepository wmsRunningAccountFinalBillRepository;
	   
	
	public List<WMSRunningAccountFinalBillApplication> registerWMSRunningAccountFinalBillRequest(WMSRunningAccountFinalBillRequest wmsRunningAccountFinalBillRequest) {
		wmsRunningAccountFinalBillApplicationValidator.validateRunningAccountFinalBillApplication(wmsRunningAccountFinalBillRequest);
		wmsRunningAccountFinalBillApplicationEnrichment.enrichRunningAccountFinalBillApplication(wmsRunningAccountFinalBillRequest);

        // Enrich/Upsert user in upon birth registration
       // userService.callUserService(birthRegistrationRequest);

        // Initiate workflow for the new application
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);

        //Call calculator to calculate and create demand
        //calculationService.getCalculation(birthRegistrationRequest);

        // Push the application to the topic for persister to listen and persist
       producer.push(configuration.getCreateTopic(), wmsRunningAccountFinalBillRequest);

        // Return the response back to user
        return wmsRunningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications();
    }


	public List<WMSRunningAccountFinalBillApplication> fetchRunningAccountFinalBillApplications(RequestInfo requestInfo,
			WMSRunningAccountFinalBillApplicationSearchCriteria runningAccountFinalBillApplicationSearchCriteria) {
		List<WMSRunningAccountFinalBillApplication> applications = wmsRunningAccountFinalBillRepository.getApplications(runningAccountFinalBillApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


	public List<WMSRunningAccountFinalBillApplication> updateRunningAccountFinalBillMaster(
			 WMSRunningAccountFinalBillRequest runningAccountFinalBillRequest) {
		List<WMSRunningAccountFinalBillApplication> existingApplication = wmsRunningAccountFinalBillApplicationValidator.validateApplicationUpdateRequest(runningAccountFinalBillRequest);
        // Enrich application upon update
        
		wmsRunningAccountFinalBillApplicationEnrichment.enrichRunningAccountFinalBillApplicationUpdate(runningAccountFinalBillRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), runningAccountFinalBillRequest);

        return runningAccountFinalBillRequest.getWmsRunningAccountFinalBillApplications();
	}


	public List<WMSRunningAccountFinalBillApplication> searchWMSRunningAccountFinalBillApplications(
			RequestInfo requestInfo,
			 WMSRunningAccountFinalBillApplicationSearchCriteria wmsRunningAccountFinalBillApplicationSearchCriteria) {
		List<WMSRunningAccountFinalBillApplication> applications = wmsRunningAccountFinalBillRepository.getApplications(wmsRunningAccountFinalBillApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}
	
	
	
	
}
