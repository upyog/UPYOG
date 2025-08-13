package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.ACTION_APPROVE;
import static org.egov.ptr.util.PTRConstants.NEW_PET_APPLICATION;
import static org.egov.ptr.util.PTRConstants.RENEW_PET_APPLICATION;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.workflow.State;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.validator.PetApplicationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetRegistrationService {

	@Autowired
	private Producer producer;

	@Autowired
	private PetConfiguration config;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private PetApplicationValidator validator;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private PetRegistrationRepository petRegistrationRepository;

	/**
	 * Registers a new pet application request.
	 * Validates, enriches, updates workflow status, and pushes to the queue based on application type.
	 *
	 * @param petRegistrationRequest The request containing pet registration details.
	 * @return List of registered pet applications.
	 */
	public List<PetRegistrationApplication> registerPtrRequest(PetRegistrationRequest petRegistrationRequest) {

		validator.validatePetApplication(petRegistrationRequest);

		enrichmentService.enrichPetApplication(petRegistrationRequest);

		State state = wfService.updateWorkflowStatus(petRegistrationRequest);
		petRegistrationRequest.getPetRegistrationApplications().get(0).setStatus(state.getApplicationStatus());

		petRegistrationRequest.getPetRegistrationApplications().forEach(application -> {
			if (application.getApplicationType().equals(RENEW_PET_APPLICATION)) {
				producer.push(config.getRenewPtrTopic(), petRegistrationRequest);
			} else if (application.getApplicationType().equals(NEW_PET_APPLICATION)) {
				producer.push(config.getCreatePtrTopic(), petRegistrationRequest);
			}
		});

		return petRegistrationRequest.getPetRegistrationApplications();
	}

	/**
	 * Searches for pet registration applications based on search criteria.
	 *
	 * @param requestInfo The request information.
	 * @param petApplicationSearchCriteria The search criteria.
	 * @return List of matching pet registration applications.
	 */
	public List<PetRegistrationApplication> searchPtrApplications(RequestInfo requestInfo,
			PetApplicationSearchCriteria petApplicationSearchCriteria) {

		List<PetRegistrationApplication> applications = petRegistrationRepository
				.getApplications(petApplicationSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();

		return applications;
	}

	/**
	 * Updates an existing pet registration application.
	 * Validates, updates workflow status, enriches, and triggers payment demand if applicable.
	 *
	 * @param petRegistrationRequest The request containing updated pet registration details.
	 * @return The updated pet registration application.
	 */
	public PetRegistrationApplication updatePtrApplication(PetRegistrationRequest petRegistrationRequest) {
		
		PetRegistrationApplication existingApplication = validator
				.validateApplicationExistence(petRegistrationRequest.getPetRegistrationApplications().get(0));
		existingApplication.setWorkflow(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow());
		petRegistrationRequest.setPetRegistrationApplications(Collections.singletonList(existingApplication));

		if (petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction()
				.equals(ACTION_APPROVE)) {
			demandService.createDemand(petRegistrationRequest);
		}
		State state = wfService.updateWorkflowStatus(petRegistrationRequest);
		enrichmentService.enrichPetApplicationUponUpdate(state.getApplicationStatus(), petRegistrationRequest);
		producer.push(config.getUpdatePtrTopic(), petRegistrationRequest);
		return petRegistrationRequest.getPetRegistrationApplications().get(0);
	}

}
