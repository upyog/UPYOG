package org.egov.pt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PetConfiguration;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.PetApplicationSearchCriteria;
import org.egov.pt.models.PetRegistrationApplication;
import org.egov.pt.models.PetRegistrationRequest;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.user.UserDetailResponse;
import org.egov.pt.models.user.UserSearchRequest;
import org.egov.pt.models.workflow.State;
import org.egov.pt.producer.Producer;
import org.egov.pt.repository.PetRegistrationRepository;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.PTConstants;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

@Service
public class PetRegistrationService {

	@Autowired
	private Producer producer;
	
	@Autowired
	private NotificationService notifService;

	@Autowired
	private PetConfiguration config;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private PropertyValidator validator;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService wfService;



	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private PetRegistrationRepository petRegistrationRepository;
	

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request PropertyRequest containing list of properties to be created
	 * @return List of properties successfully created
	 */
	public List<PetRegistrationApplication> registerPtrRequest(PetRegistrationRequest petRegistrationRequest) {

        validator.validatePetApplication(petRegistrationRequest);
        enrichmentService.enrichPetApplication(petRegistrationRequest);

       // Enrich/Upsert user in upon pet registration
      //userService.callUserService(petRegistrationRequest);  need to build the method when required

        // Initiate workflow for the new application
        wfService.updateWorkflowStatus(petRegistrationRequest);
        producer.push(config.getCreatePtrTopic(), petRegistrationRequest);

        // Return the response back to user
        return petRegistrationRequest.getPetRegistrationApplications();
    }
	
    public List<PetRegistrationApplication> searchPtrApplications(RequestInfo requestInfo, PetApplicationSearchCriteria petApplicationSearchCriteria) {
        // Fetch applications from database according to the given search criteria
        List<PetRegistrationApplication> applications = petRegistrationRepository.getApplications(petApplicationSearchCriteria);

        // If no applications are found matching the given criteria, return an empty list
        if(CollectionUtils.isEmpty(applications))
            return new ArrayList<>();

        // Enrich mother and father of applicant objects
//        applications.forEach(application -> {
////            enrichmentUtil.enrichFatherApplicantOnSearch(application);
////            enrichmentUtil.enrichMotherApplicantOnSearch(application);
//        });

        // Otherwise return the found applications
        return applications;
    }

    public PetRegistrationApplication updatePtrApplication(PetRegistrationRequest petRegistrationRequest) {
        // Validate whether the application that is being requested for update indeed exists
        PetRegistrationApplication existingApplication = validator.validateApplicationExistence(petRegistrationRequest.getPetRegistrationApplications().get(0));
        existingApplication.setWorkflow(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow()); /// uncomment when working on workflow integration
       // log.info(existingApplication.toString());
        petRegistrationRequest.setPetRegistrationApplications(Collections.singletonList(existingApplication));

        // Enrich application upon update
        enrichmentService.enrichPetApplicationUponUpdate(petRegistrationRequest);

        wfService.updateWorkflowStatus(petRegistrationRequest);

        // Just like create request, update request will be handled asynchronously by the persister
        producer.push("update-ptr-application", petRegistrationRequest);

        return petRegistrationRequest.getPetRegistrationApplications().get(0);
    }
	
	
	


	


	
}
