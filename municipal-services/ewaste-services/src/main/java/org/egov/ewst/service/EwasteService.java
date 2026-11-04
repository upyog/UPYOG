package org.egov.ewst.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ewst.config.EwasteConfiguration;
import org.egov.ewst.models.EwasteApplication;
import org.egov.ewst.models.EwasteApplicationSearchCriteria;
import org.egov.ewst.models.EwasteRegistrationRequest;
import org.egov.ewst.models.enums.Status;
import org.egov.ewst.producer.Producer;
import org.egov.ewst.repository.EwasteApplicationRepository;
import org.egov.ewst.validator.EwasteRequestValidator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service class for managing Ewaste applications.
 * This class handles the creation, update, and search operations for Ewaste applications.
 */
@Service
public class EwasteService {

	private final Producer producer;
	private final EwasteConfiguration config;
	private final EnrichmentService enrichmentService;
	private final EwasteRequestValidator validator;
	private final WorkflowService wfService;
	private final EwasteApplicationRepository ewasteApplicationRepository;

	public EwasteService(Producer producer, EwasteConfiguration config, EnrichmentService enrichmentService,
			EwasteRequestValidator validator, WorkflowService wfService,
			EwasteApplicationRepository ewasteApplicationRepository) {
		this.producer = producer;
		this.config = config;
		this.enrichmentService = enrichmentService;
		this.validator = validator;
		this.wfService = wfService;
		this.ewasteApplicationRepository = ewasteApplicationRepository;
	}

	/**
	 * Creates a new Ewaste application request.
	 * Validates, enriches, and pushes the request to the Kafka topic.
	 *
	 * @param ewasteRegistrationRequest The Ewaste registration request.
	 * @return A list of created Ewaste applications.
	 */
	public List<EwasteApplication> createEwasteRequest(EwasteRegistrationRequest ewasteRegistrationRequest) {

		validator.validateCreateRequest(ewasteRegistrationRequest);
		enrichmentService.enrichEwasteApplication(ewasteRegistrationRequest);
		wfService.updateWorkflowStatus(ewasteRegistrationRequest);
		producer.push(config.getCreateEwasteTopic(), ewasteRegistrationRequest);

		return ewasteRegistrationRequest.getEwasteApplication();
	}

	/**
	 * Searches for Ewaste applications based on the provided criteria.
	 *
	 * @param requestInfo The request information.
	 * @param ewasteApplicationSearchCriteria The search criteria.
	 * @return A list of matching Ewaste applications.
	 */
	public List<EwasteApplication> searchEwasteApplications(RequestInfo requestInfo,
			EwasteApplicationSearchCriteria ewasteApplicationSearchCriteria) {

		List<EwasteApplication> applications = ewasteApplicationRepository
				.getApplication(ewasteApplicationSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();

		return applications;
	}

	/**
	 * Updates an existing Ewaste application request.
	 * Validates, updates, and pushes the updated request to the Kafka topic.
	 *
	 * @param ewasteRegistrationRequest The Ewaste registration request.
	 * @return The updated Ewaste application.
	 */
	public EwasteApplication updateEwasteRequest(EwasteRegistrationRequest ewasteRegistrationRequest) {

		EwasteApplication payloadApplication = ewasteRegistrationRequest.getEwasteApplication().get(0);
		EwasteApplication existingApplication = validator.validateApplicationExistence(payloadApplication);

		existingApplication.setTransactionId(payloadApplication.getTransactionId());
		existingApplication.setPickUpDate(payloadApplication.getPickUpDate());
		existingApplication.setFinalAmount(payloadApplication.getFinalAmount());
		applyWorkflowAction(existingApplication, payloadApplication.getWorkflow().getAction());

		existingApplication.setWorkflow(payloadApplication.getWorkflow());
		existingApplication.getAuditDetails()
				.setLastModifiedBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		existingApplication.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());

		ewasteRegistrationRequest.setEwasteApplication(Collections.singletonList(existingApplication));
		wfService.updateWorkflowStatus(ewasteRegistrationRequest);
		producer.push(config.getUpdateEwasteTopic(), ewasteRegistrationRequest);
		return existingApplication;
	}

	private void applyWorkflowAction(EwasteApplication existingApplication, String action) {
		if ("VERIFYPRODUCT".equals(action)) {
			existingApplication.setRequestStatus(Status.PRODUCTVERIFIED.toString());
			return;
		}
		if ("SENDPICKUPALERT".equals(action)) {
			existingApplication.setRequestStatus(Status.COMPLETIONPENDING.toString());
			return;
		}
		if ("REJECT".equals(action)) {
			existingApplication.setRequestStatus(Status.REJECTED.toString());
			return;
		}
		if ("COMPLETEREQUEST".equals(action)) {
			existingApplication.setRequestStatus(Status.REQUESTCOMPLETED.toString());
		}
	}

}
