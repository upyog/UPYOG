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
			wfService.updateWorkflowForAppeal(request, request.getAppeal().getCreationReason());
		}
		else {
			request.getAppeal().setStatus(Status.ACTIVE);
		}
		//Save Topic For Appeal
		
		
		producer.push(config.getSaveAppealTopic(), request);
		return request.getAppeal();
	}

	/**
	 * Updates the property
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
		criteria.setPropertyIds(Set.of(request.getAppeal().getPropertyId()));
		//Update for single object
		List<Appeal> appeal = searchAppeal(criteria);
		if(null==appeal || appeal.isEmpty()) {
			throw new CustomException("INVALID_PROPERTY","No Appeals found for this property");
		}
		propertyValidator.validateAppealUpdateRequest(request);
		enrichmentService.enrichAppealForUpdateRequest(request);
		propertyValidator.validateAppealWorkFlowRequestForAppeal(request,appeal.get(0));
		
		return request.getAppeal();
	}

	public List<Appeal> searchAppeal(AppealCriteria appealCriteria) {

		List<Appeal> appeals;
		if(CollectionUtils.isEmpty(appealCriteria.getPropertyIds()))
			throw new CustomException("EG_PT_APPEAL_ERROR", "No property id for search");
		
		appeals = repository.getAppeal(appealCriteria);
		return appeals;
	}
	
	
}
