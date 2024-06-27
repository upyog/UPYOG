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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EwasteService {

	@Autowired
	private Producer producer;

	@Autowired
	private EwasteConfiguration config;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private EwasteRequestValidator validator;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private EwasteApplicationRepository ewasteApplicationRepository;

	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 */
	public List<EwasteApplication> createEwasteRequest(EwasteRegistrationRequest ewasteRegistrationRequest) {

		enrichmentService.enrichEwasteApplication(ewasteRegistrationRequest);
		wfService.updateWorkflowStatus(ewasteRegistrationRequest);
		producer.push(config.getCreateEwasteTopic(), ewasteRegistrationRequest);

		return ewasteRegistrationRequest.getEwasteApplication();
	}

	public List<EwasteApplication> searchEwasteApplications(RequestInfo requestInfo,
			EwasteApplicationSearchCriteria ewasteApplicationSearchCriteria) {

		List<EwasteApplication> applications = ewasteApplicationRepository
				.getApplication(ewasteApplicationSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();

		return applications;
	}

	public EwasteApplication updateEwasteRequest(EwasteRegistrationRequest ewasteRegistrationRequest) {

		// Validate the existence of the application
		EwasteApplication payloadApplication = ewasteRegistrationRequest.getEwasteApplication().get(0);
		EwasteApplication existingApplication = validator.validateApplicationExistence(payloadApplication);

		// Update the fields of the existing application with the payload data
		existingApplication.setTransactionId(payloadApplication.getTransactionId());
		existingApplication.setPickUpDate(payloadApplication.getPickUpDate());
		existingApplication.setFinalAmount(payloadApplication.getFinalAmount());
		String action = payloadApplication.getWorkflow().getAction();

		if ("VERIFYPRODUCT".equals(action)) {
			existingApplication.setRequestStatus(Status.PRODUCTVERIFIED.toString());
		} else if ("SENDPICKUPALERT".equals(action)) {
			existingApplication.setRequestStatus(Status.COMPLETIONPENDING.toString());
		} else if ("REJECT".equals(action)) {
			existingApplication.setRequestStatus(Status.REJECTED.toString());
		} else if ("COMPLETEREQUEST".equals(action)) {
			existingApplication.setRequestStatus(Status.REQUESTCOMPLETED.toString());
		}

		existingApplication.setWorkflow(payloadApplication.getWorkflow());
		//Update the audit details upon update
		existingApplication.getAuditDetails()
				.setLastModifiedBy(ewasteRegistrationRequest.getRequestInfo().getUserInfo().getUuid());
		existingApplication.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());
		
		// Enrich the application with audit details upon update
//		enrichmentService.enrichEwasteApplicationUponUpdate(ewasteRegistrationRequest);

		// Update the ewaste application request with the modified existing application
		ewasteRegistrationRequest.setEwasteApplication(Collections.singletonList(existingApplication));
		wfService.updateWorkflowStatus(ewasteRegistrationRequest);
		// Push the updated application to the update topic
		producer.push(config.getUpdateEwasteTopic(), ewasteRegistrationRequest);

		// Return the updated application
		return existingApplication;
	}

}
