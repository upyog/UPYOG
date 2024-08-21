package org.egov.ptr.service;

import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.Demand;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.models.collection.BillResponse;
import org.egov.ptr.models.collection.GenerateBillCriteria;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import org.egov.ptr.validator.PetApplicationValidator;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

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
	private ObjectMapper mapper;

	@Autowired
	private PetRegistrationRepository petRegistrationRepository;

	@Autowired
	private BillingService billingService;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 */
	public List<PetRegistrationApplication> registerPtrRequest(PetRegistrationRequest petRegistrationRequest) {

		validator.validatePetApplication(petRegistrationRequest);
		enrichmentService.enrichPetApplication(petRegistrationRequest);

		// Enrich/Upsert user in upon pet registration
		// userService.callUserService(petRegistrationRequest); need to build the method
		wfService.updateWorkflowStatus(petRegistrationRequest);
		producer.push(config.getCreatePtrTopic(), petRegistrationRequest);

		return petRegistrationRequest.getPetRegistrationApplications();
	}

	public List<PetRegistrationApplication> searchPtrApplications(RequestInfo requestInfo,
			PetApplicationSearchCriteria petApplicationSearchCriteria) {

		List<PetRegistrationApplication> applications = petRegistrationRepository
				.getApplications(petApplicationSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();

		return applications;
	}

	public PetRegistrationApplication updatePtrApplication(PetRegistrationRequest petRegistrationRequest) {
		PetRegistrationApplication existingApplication = validator
				.validateApplicationExistence(petRegistrationRequest.getPetRegistrationApplications().get(0));
		existingApplication.setWorkflow(petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow());
//		petRegistrationRequest.setPetRegistrationApplications(Collections.singletonList(existingApplication));

		enrichmentService.enrichPetApplicationUponUpdate(petRegistrationRequest);

		if (petRegistrationRequest.getPetRegistrationApplications().get(0).getWorkflow().getAction()
				.equals("APPROVE")) {
			
			// create demands
			List<Demand> savedDemands = demandService.createDemand(petRegistrationRequest);

	        if(CollectionUtils.isEmpty(savedDemands)) {
	            throw new CustomException("INVALID_CONSUMERCODE","Bill not generated due to no Demand found for the given consumerCode");
	        }

			// fetch/create bill
            GenerateBillCriteria billCriteria = GenerateBillCriteria.builder()
            									.tenantId(petRegistrationRequest.getPetRegistrationApplications().get(0).getTenantId())
            									.businessService("pet-services")
            									.consumerCode(petRegistrationRequest.getPetRegistrationApplications().get(0).getApplicationNumber()).build();
            BillResponse billResponse = billingService.generateBill(petRegistrationRequest.getRequestInfo(),billCriteria);
            
		}
		wfService.updateWorkflowStatus(petRegistrationRequest);
		producer.push(config.getUpdatePtrTopic(), petRegistrationRequest);

		return petRegistrationRequest.getPetRegistrationApplications().get(0);
	}

}
