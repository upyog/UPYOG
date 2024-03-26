package org.egov.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.egov.config.ProjectConfiguration;
import org.egov.config.SchemeConfiguration;
import org.egov.enrichment.ProjectApplicationEnrichment;
import org.egov.enrichment.SchemeApplicationEnrichment;
import org.egov.producer.Producer;
import org.egov.repository.ProjectMasterRepository;
import org.egov.repository.SchemeMasterRepository;
import org.egov.validator.WMSProjectValidator;
import org.egov.validator.WMSSchemeValidator;
import org.egov.web.models.Project;
import org.egov.web.models.ProjectApplicationSearchCriteria;
import org.egov.web.models.ScheduleOfRateApplication;
import org.egov.web.models.Scheme;
import org.egov.web.models.SchemeApplicationSearchCriteria;
import org.egov.web.models.SchemeCreationApplication;
import org.egov.web.models.WMSContractorApplication;
import org.egov.web.models.WMSProjectRequest;
//import org.wms.web.models.SchemeCreationRequest;
import org.egov.web.models.WMSSchemeRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProjectMasterService {
	
	@Autowired
    private WMSProjectValidator validator;

    @Autowired
    private ProjectApplicationEnrichment enrichmentUtil;

    //@Autowired
    //private BirthApplicationValidator birthApplicationValidator;
    //@Autowired
    //private BirthApplicationEnrichment birthApplicationEnrichment;
    @Autowired
    private Producer producer;
    @Autowired
    private ProjectConfiguration configuration;
    @Autowired
    private ProjectMasterRepository projectMasterRepository;
	/*
	 * @Autowired private UserService userService;
	 * 
	 * @Autowired private WorkflowService workflowService;
	 * 
	 * @Autowired private CalculationService calculationService;
	 */

    public List<Project> createProjectRequest( WMSProjectRequest projectRequest) {
        
    	validator.validateProjectApplication(projectRequest);
    	enrichmentUtil.enrichProjectApplication(projectRequest);
        // Push the application to the topic for persister to listen and persist
        producer.push(configuration.getCreateTopic(), projectRequest);
        //List<SchemeCreationApplication> request=new ArrayList<>();
        // Return the response back to user
        //return schemeRequest.getBirthRegistrationApplications();
        
        //schemeMasterRepository.save(schemeRequest);
        return projectRequest.getProjectApplications();
        
        //return savedScheme;
    }

    
    public List<Project> updateProjectMaster(WMSProjectRequest projectRequest) {
    	List<Project> existingApplication = validator.validateApplicationUpdateRequest(projectRequest);
        // Enrich application upon update
        
        enrichmentUtil.enrichProjectApplicationUponUpdate(projectRequest,existingApplication);
        //workflowService.updateWorkflowStatus(birthRegistrationRequest);
        // Just like create request, update request will be handled asynchronously by the persister
        producer.push(configuration.getUpdateTopic(), projectRequest);

        return projectRequest.getProjectApplications();
    }

	/*
	 * public List<Project> viewProject() { List<Project> optionalProject =
	 * projectMasterRepository.getAllProject();
	 * 
	 * // If the scheme exists, return it; otherwise, return null. return
	 * optionalProject; }
	 * 
	 * public Project getProjectById(Long id) { return
	 * projectMasterRepository.getProjectById(id);
	 * 
	 * 
	 * }
	 */

	public List<Project> searchProjectApplications(RequestInfo requestInfo,
			 ProjectApplicationSearchCriteria projectApplicationSearchCriteria) {
		// TODO Auto-generated method stub
		List<Project> applications = projectMasterRepository.getApplications(projectApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();
		
		
		return applications;
	}


	public List<Project> fetchProjectApplications(RequestInfo requestInfo,
			 ProjectApplicationSearchCriteria projectApplicationSearchCriteria) {
		List<Project> applications = projectMasterRepository.getApplications(projectApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        return applications;
	}


}
