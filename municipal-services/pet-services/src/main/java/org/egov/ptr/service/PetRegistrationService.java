package org.egov.ptr.service;

import static org.egov.ptr.util.PTRConstants.*;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.validator.PetApplicationValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

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
	private UserService userService;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private DemandService demandService;

	@Autowired
	private PetRegistrationRepository petRegistrationRepository;

	@Autowired
	private PTRBatchService ptrBatchService;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 */
	public List<PetRegistrationApplication> registerPtrRequest(PetRegistrationRequest petRegistrationRequest) {

		validator.validatePetApplication(petRegistrationRequest);
		enrichmentService.enrichPetApplication(petRegistrationRequest);

		// Create user in user service
		userService.createUser(petRegistrationRequest);

		wfService.updateWorkflowStatus(petRegistrationRequest);
		petRegistrationRequest.getPetRegistrationApplications().forEach(application -> {
			if (RENEW_PET_APPLICATION.equals(application.getApplicationType())) {
				log.debug("Pushing renewal application to Kafka - ApplicationNumber: {}, petRegistrationNumber: {}, petToken: {}", 
					application.getApplicationNumber(), 
					application.getPetRegistrationNumber(), 
					application.getPetToken());
				producer.push(config.getRenewPtrTopic(), petRegistrationRequest);
			} else if (NEW_PET_APPLICATION.equals(application.getApplicationType())) {
				producer.push(config.getCreatePtrTopic(), petRegistrationRequest);
			}
		});

		return petRegistrationRequest.getPetRegistrationApplications();
	}

	public List<PetRegistrationApplication> searchPtrApplications(RequestInfo requestInfo,
																  PetApplicationSearchCriteria petApplicationSearchCriteria) {

		// Handle mobile number search by converting to owner UUIDs
		if (!ObjectUtils.isEmpty(petApplicationSearchCriteria.getMobileNumber())) {
			System.out.println("DEBUG: Searching by mobile number: " + petApplicationSearchCriteria.getMobileNumber());

			List<String> userUuids = userService.getUserUuidsByMobileNumber(
					petApplicationSearchCriteria.getMobileNumber(),
					petApplicationSearchCriteria.getTenantId(),
					requestInfo
			);

			System.out.println("DEBUG: Found user UUIDs: " + userUuids);

			if (CollectionUtils.isEmpty(userUuids)) {
				// No users found for this mobile number, return empty list
				System.out.println("DEBUG: No users found for mobile number, returning empty list");
				return new ArrayList<>();
			}

			// Set owner UUIDs for search and clear mobile number
			petApplicationSearchCriteria.setOwnerUuids(userUuids);
			petApplicationSearchCriteria.setMobileNumber(null);
			System.out.println("DEBUG: Set ownerUuids in criteria: " + petApplicationSearchCriteria.getOwnerUuids());
		}

		List<PetRegistrationApplication> applications = petRegistrationRepository
				.getApplications(petApplicationSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();

		// Enrich owner details from user service
		enrichmentService.enrichOwnerDetailsFromUserService(applications, requestInfo);

		return applications;
	}

	public PetRegistrationApplication updatePtrApplication(PetRegistrationRequest petRegistrationRequest) {
		PetRegistrationApplication existingApplication = validator
				.validateApplicationExistence(petRegistrationRequest.getPetRegistrationApplications().get(0));

		enrichmentService.enrichPetApplicationUponUpdate(petRegistrationRequest);

		PetRegistrationApplication application = petRegistrationRequest.getPetRegistrationApplications().get(0);
		
		boolean isApproveAction = application.getWorkflow().getAction().equals(ACTION_APPROVE);
		
		if (isApproveAction) {
			demandService.createDemand(petRegistrationRequest);
		}
		
		// Update workflow status - this will set the status from workflow response
		wfService.updateWorkflowStatus(petRegistrationRequest);

		producer.push(config.getUpdatePtrTopic(), petRegistrationRequest);
		return application;
	}


	public void runJob(String servicename, String jobname, RequestInfo requestInfo) {
		if (servicename == null)
			servicename = PET_BUSINESSSERVICE;

		ptrBatchService.getPetApplicationsAndPerformAction(servicename, jobname, requestInfo);

	}

}
