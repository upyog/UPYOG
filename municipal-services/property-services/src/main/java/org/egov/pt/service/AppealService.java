package org.egov.pt.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.AmalgamatedProperty;
import org.egov.pt.models.Appeal;
import org.egov.pt.models.AppealCriteria;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyBifurcation;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.user.UserDetailResponse;
import org.egov.pt.models.user.UserSearchRequest;
import org.egov.pt.models.workflow.ProcessInstanceRequest;
import org.egov.pt.models.workflow.State;
import org.egov.pt.producer.PropertyProducer;
import org.egov.pt.repository.PropertyRepository;
import org.egov.pt.util.EncryptionDecryptionUtil;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.util.UnmaskingUtil;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.web.contracts.AppealRequest;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.xml.BeanDefinitionDocumentReader;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AppealService {

	@Autowired
	private UnmaskingUtil unmaskingUtil;

	@Autowired
	private PropertyProducer producer;

	@Autowired
	private NotificationService notifService;

	@Autowired
	private PropertyConfiguration config;

	@Autowired
	private PropertyRepository repository;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private UserService userService;

	@Autowired
	private WorkflowService wfService;

	@Autowired
	private PropertyUtil util;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CalculationService calculatorService;

	@Autowired
	private FuzzySearchService fuzzySearchService;

	@Autowired
	EncryptionDecryptionUtil encryptionDecryptionUtil;

	@Autowired
	private PropertyUtil propertyUtil;

	@Autowired
	private WorkflowService workflowService;
	/**
	 * Enriches the Request and pushes to the Queue
	 *
	 * @param request PropertyRequest containing list of properties to be created
	 * @return List of properties successfully created
	 */
	public Appeal createAppeal(AppealRequest request) {

		propertyValidator.validateAppealCreateRequest(request);
		enrichmentService.enrichAppealCreateRequest(request);

		if(config.getIsWorkflowEnabled())
		{
			wfService.updateWorkflowForAppeal(request, CreationReason.APPEAL);
		}
		else {
			request.getAppeal().setStatus(Status.ACTIVE);
		}
		//Save Topic For Appeal


		producer.push(config.getSaveAppealTopic(), request);
		return request.getAppeal();
	}

	/**
	 * Updates the 
	 *
	 * handles multiple processes
	 *
	 * Update
	 *
	 * Mutation
	 *
	 * @param request PropertyRequest containing list of properties to be update
	 * @return List of updated properties
	 */
	public Appeal updateProperty(AppealRequest request) {

		AppealCriteria criteria = new AppealCriteria();
		criteria.setPropertyIds(Sets.newHashSet(request.getAppeal().getPropertyId()));
		//Update for single object
		List<Appeal> appeal = searchAppeal(criteria);
		if(null==appeal || appeal.isEmpty()) {
			throw new CustomException("INVALID_PROPERTY","No Appeals found for this property");
		}
		Appeal appFromDb = appeal.get(0);
		propertyValidator.validateAppealUpdateRequest(request);
		enrichmentService.enrichAppealForUpdateRequest(request);
		propertyValidator.validateAppealWorkFlowRequestForAppeal(request,appeal.get(0));
		//State state = wfService.updateWorkflowForAppeal(request, CreationReason.APPEAL);

		ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(request.getRequestInfo(), Collections.singletonList(request.getAppeal().getWorkflow()));
		State state = workflowService.callWorkFlow(workflowRequest);
		String status = state.getApplicationStatus();
		request.getAppeal().getWorkflow().setState(state);
		request.getAppeal().setStatus(Status.fromValue(status));
		/*
		 * if( request.getAppeal().getStatus().equals(Status.INWORKFLOW) &&
		 * request.getAppeal().getStatus().equals(state.getApplicationStatus().equals(
		 * Status.INWORKFLOW.toString()))) {
		 * 
		 * // System.out.println("In normal update"); } else if
		 * (state.getIsTerminateState() &&
		 * !state.getApplicationStatus().equalsIgnoreCase(Status.ACTIVE.toString())) {
		 * //Failed request.getAppeal().setStatus(Status.REJECTED);
		 * producer.push(config.getAppealUpdateTopic(), request); } else { //success
		 * request.getAppeal().setStatus(Status.ACTIVE);
		 * producer.push(config.getAppealUpdateTopic(), request);
		 * 
		 * }
		 */
		producer.push(config.getAppealUpdateTopic(), request);


		return request.getAppeal();
	}

	public List<Appeal> searchAppeal(AppealCriteria appealCriteria) {
		List<Appeal> appeals;
		appeals = repository.getAppeal(appealCriteria);
		return appeals;
	}


}
