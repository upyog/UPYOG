package org.egov.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.config.SchemeConfiguration;
import org.egov.enrichment.SchemeApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.SchemeMasterRepository;
import org.egov.validator.WMSSchemeValidator;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.egov.web.models.SchemeCreationApplication;
import org.egov.web.models.WMSContractorApplication;
//import org.wms.web.models.SchemeCreationRequest;
import org.egov.web.models.WMSSchemeRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SchemeMasterService {
	
	@Autowired
    private WMSSchemeValidator validator;

    @Autowired
    private SchemeApplicationEnrichment enrichmentUtil;

    //@Autowired
    //private BirthApplicationValidator birthApplicationValidator;
    //@Autowired
    //private BirthApplicationEnrichment birthApplicationEnrichment;
    @Autowired
    private Producer producer;
    @Autowired
    private SchemeConfiguration configuration;
    @Autowired
    private SchemeMasterRepository schemeMasterRepository;
	/*
	 * @Autowired private UserService userService;
	 * 
	 * @Autowired private WorkflowService workflowService;
	 * 
	 * @Autowired private CalculationService calculationService;
	 */

    public List<Scheme> createSchemeRequest( WMSSchemeRequest schemeRequest) {
        
    	validator.validateSchemeApplication(schemeRequest);
    	enrichmentUtil.enrichSchemeApplication(schemeRequest);
        // Push the application to the topic for persister to listen and persist
        producer.push(configuration.getCreateTopic(), schemeRequest);
        //List<SchemeCreationApplication> request=new ArrayList<>();
        // Return the response back to user
        //return schemeRequest.getBirthRegistrationApplications();
        
        //schemeMasterRepository.save(schemeRequest);
        return schemeRequest.getSchemeApplications();
        
        //return savedScheme;
    }

    
    public List<Scheme> updateSchemeMaster(WMSSchemeRequest schemeRequest) {
    	List<Scheme> existingApplication = validator.validateApplicationUpdateRequest(schemeRequest);
        // Enrich application upon update
        
        enrichmentUtil.enrichSchemeApplicationUponUpdate(schemeRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), schemeRequest);

        return schemeRequest.getSchemeApplications();
    }

	/*
	 * public List<Scheme> viewScheme() { List<Scheme> optionalScheme =
	 * schemeMasterRepository.getAllSchemes();
	 * 
	 * // If the scheme exists, return it; otherwise, return null. return
	 * optionalScheme; }
	 * 
	 * public Scheme getSchemeById(Long id) { return
	 * schemeMasterRepository.getSchemeById(id);
	 * 
	 * 
	 * }
	 */

	public List<Scheme> searchSchemeApplications(RequestInfo requestInfo,
			 SchemeApplicationSearchCriteria schemerApplicationSearchCriteria) {
		// TODO Auto-generated method stub
		List<Scheme> applications = schemeMasterRepository.getApplications(schemerApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();
		
		
		return applications;
	}


	public List<Scheme> fetchSchemeApplications(RequestInfo requestInfo,
			 SchemeApplicationSearchCriteria schemerApplicationSearchCriteria) {
		List<Scheme> applications = schemeMasterRepository.getApplications(schemerApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


}
