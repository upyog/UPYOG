package org.egov.pt.service;

import static org.egov.pt.util.PTConstants.ASSESSMENT_BUSINESSSERVICE;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Assessment;
import org.egov.pt.models.AssessmentSearchCriteria;
import org.egov.pt.models.Demand;
import org.egov.pt.models.Demand.StatusEnum;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.user.UserDetailResponse;
import org.egov.pt.models.user.UserSearchRequest;
import org.egov.pt.models.workflow.BusinessService;
import org.egov.pt.models.workflow.ProcessInstance;
import org.egov.pt.models.workflow.ProcessInstanceRequest;
import org.egov.pt.models.workflow.State;
import org.egov.pt.producer.PropertyProducer;
import org.egov.pt.repository.AssessmentRepository;
import org.egov.pt.util.AssessmentUtils;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.PropertyUtil;
import org.egov.pt.validator.AssessmentValidator;
import org.egov.pt.web.contracts.AssessmentRequest;
import org.egov.pt.web.contracts.DemandRequest;
import org.egov.pt.web.contracts.DemandResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import ch.qos.logback.core.recovery.ResilientSyslogOutputStream;

import org.egov.tracer.model.CustomException;
import org.json.JSONObject;

@Service
public class AssessmentService {

	private static final String URL_PARAMS_SEPARATER = "&";

	private AssessmentValidator validator;

	private PropertyProducer producer;

	private PropertyConfiguration props;

	private AssessmentRepository repository;

	private AssessmentEnrichmentService assessmentEnrichmentService;

	private PropertyConfiguration config;

	private DiffService diffService;

	private AssessmentUtils utils;

	private WorkflowService workflowService;

	private CalculationService calculationService;

	@Autowired
	private PropertyUtil util;

	@Autowired
	private UserService userService;
	
	@Autowired
	BillingService billingService;


	@Autowired
	public AssessmentService(AssessmentValidator validator, PropertyProducer producer, PropertyConfiguration props, AssessmentRepository repository,
			AssessmentEnrichmentService assessmentEnrichmentService, PropertyConfiguration config, DiffService diffService,
			AssessmentUtils utils, WorkflowService workflowService, CalculationService calculationService) {
		this.validator = validator;
		this.producer = producer;
		this.props = props;
		this.repository = repository;
		this.assessmentEnrichmentService = assessmentEnrichmentService;
		this.config = config;
		this.diffService = diffService;
		this.utils = utils;
		this.workflowService = workflowService;
		this.calculationService = calculationService;
	}

	/**
	 * Method to create an assessment asynchronously.
	 *
	 * @param request
	 * @return
	 */
	public Assessment createAssessment(AssessmentRequest request) {
		AssessmentSearchCriteria crt = new AssessmentSearchCriteria();
		Set<String>propertyIds = new HashSet<>();
		
		Property property = utils.getPropertyForAssessment(request);
		validator.validateAssessmentCreate(request, property);
		List<Assessment> earlierAssesmentForTheFinancialYear =null;
		SimpleDateFormat year=new SimpleDateFormat("yyyy");
		Timestamp stamp = new Timestamp(property.getAuditDetails().getCreatedTime());
		Date date = new Date(stamp.getTime());
		String propertyCreationYear = year.format(date);
		String currentFinYearStart = props.getFinYearStart().toString();
		boolean found=true;
		StringBuilder sb = new StringBuilder("Please complete the assesment of previous years : ");
		/*if(props.getAssesmentStartyear()>=Integer.parseInt(propertyCreationYear))
		{*/
			propertyIds.add(property.getPropertyId());
			crt.setPropertyIds(propertyIds);
			String assemtmentyearFromRequest = request.getAssessment().getFinancialYear().split("-")[0].toString();
			for(int i=0;i<Integer.parseInt(assemtmentyearFromRequest)-props.getAssesmentStartyear();i++) {
				Integer checkForYearStart = props.getAssesmentStartyear()+i;
				Integer checkForYearEnd = props.getAssesmentStartyear()+1+i;
				
				//if(checkForYearStart.compareTo(props.getFinYearStart())!=0 && checkForYearEnd.compareTo(props.getFinYearEnd())!=0) {
					crt.setFinancialYear(checkForYearStart.toString()+"-"+checkForYearEnd.toString().substring(2));
					earlierAssesmentForTheFinancialYear=  searchAssessments(crt, request.getRequestInfo());
					if(null==earlierAssesmentForTheFinancialYear || earlierAssesmentForTheFinancialYear.isEmpty()) {
						found=false;
						sb.append(" "+checkForYearStart.toString()+"-"+checkForYearEnd.toString().substring(2)+",");
				}
				
					
				//}
			}
			
		//}
		if(!found) {
			throw new CustomException("ASSESMENT_EXCEPTION",sb.toString());
		}
		
		//property.getAuditDetails().getCreatedTime()
		
		assessmentEnrichmentService.enrichAssessmentCreate(request);

		//For Checking Assesmnt Done for the year
		earlierAssesmentForTheFinancialYear=null;
		propertyIds.add(property.getPropertyId());
		crt.setPropertyIds(propertyIds);
		crt.setFinancialYear(request.getAssessment().getFinancialYear());
		crt.setStatus(Status.ACTIVE);


		 earlierAssesmentForTheFinancialYear =  searchAssessments(crt, request.getRequestInfo());
		if(null!=earlierAssesmentForTheFinancialYear&&earlierAssesmentForTheFinancialYear.size()>0)
			throw new CustomException("ASSESMENT_EXCEPTION","Property assessment is already completed for this property for the financial year "+crt.getFinancialYear());

		//Call For Previous Year Demand Deactivation
		
		if(config.getIsAssessmentWorkflowEnabled()){
			assessmentEnrichmentService.enrichWorkflowForInitiation(request);
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(request.getRequestInfo(),
					Collections.singletonList(request.getAssessment().getWorkflow()));
			State state = workflowService.callWorkFlow(workflowRequest);
			request.getAssessment().getWorkflow().setState(state);
		}
		else {
			calculationService.calculateTax(request, property);
		}
		
		deactivateOldDemandsForPreiousYears(request);
		producer.push(props.getCreateAssessmentTopic(), request);

		return request.getAssessment();
	}


	/**
	 * Method to update an assessment asynchronously.
	 *
	 * @param request
	 * @return
	 */
	
	
	public void deactivateOldDemandsForPreiousYears(AssessmentRequest request) {
		
		String assemtmentyearFromRequest = request.getAssessment().getFinancialYear().split("-")[1].toString();
		assemtmentyearFromRequest = "31-12-20"+assemtmentyearFromRequest;
		DemandResponse dmr = billingService.fetchDemand(request);
		DemandRequest demRequest = new DemandRequest();
		List<Demand>demaListToBeUpdated = new ArrayList<>();
		
		Calendar datexp = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		try {
			java.util.Date parsedDate =  dateFormat.parse(assemtmentyearFromRequest);
			datexp.setTime(parsedDate);
			datexp.set(datexp.get(Calendar.YEAR), datexp.get(Calendar.MONTH), datexp.get(Calendar.DATE), 23, 59, 59);
		}catch (ParseException e) {

			e.printStackTrace();
		}

		if(null!=dmr.getDemands() &&!dmr.getDemands().isEmpty()) {
			for(Demand dm:dmr.getDemands()) {
				
				System.out.println(dm.getTaxPeriodTo());
				System.out.println(datexp.getTimeInMillis());
				System.out.println(dm.getTaxPeriodTo().compareTo(datexp.getTimeInMillis()) < 0);
				//if(dm.getTaxPeriodTo().compareTo(System.currentTimeMillis()) < 0)
				if(dm.getTaxPeriodTo().compareTo(datexp.getTimeInMillis()) < 0)
				dm.setStatus(StatusEnum.CANCELLED);
				demaListToBeUpdated.add(dm);
			}
			demRequest.setDemands(demaListToBeUpdated);
			demRequest.setRequestInfo(request.getRequestInfo());
			DemandResponse resp = billingService.updateDemand(demRequest);
			}
	}
	
	public Assessment updateAssessment(AssessmentRequest request) {

		Assessment assessment = request.getAssessment();
		RequestInfo requestInfo = request.getRequestInfo();
		Property property = utils.getPropertyForAssessment(request);
		assessmentEnrichmentService.enrichAssessmentUpdate(request, property);
		Assessment assessmentFromSearch = repository.getAssessmentFromDB(request.getAssessment());
		Boolean isWorkflowTriggered = isWorkflowTriggered(request.getAssessment(),assessmentFromSearch,"");
		validator.validateAssessmentUpdate(request, assessmentFromSearch, property, isWorkflowTriggered);

		if ((request.getAssessment().getStatus().equals(Status.INWORKFLOW) || isWorkflowTriggered)
				&& config.getIsAssessmentWorkflowEnabled()){

			BusinessService businessService = workflowService.getBusinessService(request.getAssessment().getTenantId(),
					ASSESSMENT_BUSINESSSERVICE,request.getRequestInfo());

			assessmentEnrichmentService.enrichAssessmentProcessInstance(request, property);

			Boolean isStateUpdatable = workflowService.isStateUpdatable(request.getAssessment().getWorkflow().getState().getState(),businessService);

			if(isStateUpdatable){

				assessmentEnrichmentService.enrichAssessmentUpdate(request, property);
				/*
				calculationService.getMutationFee();
				producer.push(topic1,request);*/
			}
			ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(requestInfo, Collections.singletonList(assessment.getWorkflow()));
			State state = workflowService.callWorkFlow(workflowRequest);
			String status = state.getApplicationStatus();
			request.getAssessment().getWorkflow().setState(state);
			//assessmentEnrichmentService.enrichStatus(status, assessment, businessService);
			assessment.setStatus(Status.fromValue(status));
			if(assessment.getWorkflow().getState().getState().equalsIgnoreCase(config.getDemandTriggerState()))
				calculationService.calculateTax(request, property);

			producer.push(props.getUpdateAssessmentTopic(), request);


			/*
			 *
			 * if(stateIsUpdatable){
			 *
			 *
			 *  }
			 *
			 *  else {
			 *  	producer.push(stateUpdateTopic, request);
			 *
			 *  }
			 *
			 *
			 * */


		}
		else if(!config.getIsAssessmentWorkflowEnabled()){
			calculationService.calculateTax(request, property);
			producer.push(props.getUpdateAssessmentTopic(), request);
		}
		return request.getAssessment();
	}

	public List<Assessment> searchAssessments(AssessmentSearchCriteria criteria, RequestInfo requestInfo){

		List<Assessment> assessments;
		assessments=repository.getAssessments(criteria);
		boolean isInternal=false;

		Boolean isOpenSearch = isInternal ? false : util.isPropertySearchOpen(requestInfo.getUserInfo());

		if (CollectionUtils.isEmpty(assessments))
			return Collections.emptyList();

		Set<String> ownerIds = assessments.stream().map(Assessment::getOwners).flatMap(List::stream)
				.map(OwnerInfo::getUuid).collect(Collectors.toSet());

		UserSearchRequest userSearchRequest = userService.getBaseUserSearchRequest(criteria.getTenantId(), requestInfo);
		userSearchRequest.setUuid(ownerIds);
		UserDetailResponse userDetailResponse = userService.getUser(userSearchRequest);

		util.enrichAssesmentOwner(userDetailResponse, assessments, isOpenSearch);

		return assessments;
	}

	public List<Assessment> getAssessmenPlainSearch(AssessmentSearchCriteria criteria) {
		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			criteria.setLimit(config.getMaxSearchLimit());
		if(criteria.getLimit()==null)
			criteria.setLimit(config.getDefaultLimit());
		if(criteria.getOffset()==null)
			criteria.setOffset(config.getDefaultOffset());
		AssessmentSearchCriteria assessmentSearchCriteria = new AssessmentSearchCriteria();
		if (criteria.getIds() != null || criteria.getPropertyIds() != null || criteria.getAssessmentNumbers() != null) {
			if (criteria.getIds() != null)
				assessmentSearchCriteria.setIds(criteria.getIds());
			if (criteria.getPropertyIds() != null)
				assessmentSearchCriteria.setPropertyIds(criteria.getPropertyIds());
			if (criteria.getAssessmentNumbers() != null)
				assessmentSearchCriteria.setAssessmentNumbers(criteria.getAssessmentNumbers());

		} else {
			List<String> assessmentNumbers = repository.fetchAssessmentNumbers(criteria);
			if (assessmentNumbers.isEmpty())
				return Collections.emptyList();
			assessmentSearchCriteria.setAssessmentNumbers(new HashSet<>(assessmentNumbers));
		}
		assessmentSearchCriteria.setLimit(criteria.getLimit());
		return repository.getAssessmentPlainSearch(assessmentSearchCriteria);
	}

	/**
	 * Checks if the fields modified can trigger a workflow
	 * @param mutationProcessConstant 
	 * @return true if workflow is triggered else false
	 */
	private Boolean isWorkflowTriggered(Assessment assessment, Assessment assessmentFromSearch, String mutationProcessConstant){

		Boolean isWorkflowTriggeredByFieldChange = false;
		List<String> fieldsUpdated = diffService.getUpdatedFields(assessment, assessmentFromSearch, mutationProcessConstant);
		
		if(!CollectionUtils.isEmpty(fieldsUpdated))
			isWorkflowTriggeredByFieldChange = intersection(new LinkedList<>(Arrays.asList(config.getAssessmentWorkflowTriggerParams().split(","))), fieldsUpdated);

		// third variable is needed only for mutation
		List<String> objectsAdded = diffService.getObjectsAdded(assessment, assessmentFromSearch, "");

		Boolean isWorkflowTriggeredByObjectAddition = false;
		if(!CollectionUtils.isEmpty(objectsAdded))
			isWorkflowTriggeredByObjectAddition = intersection(new LinkedList<>(Arrays.asList(config.getAssessmentWorkflowObjectTriggers().split(","))), objectsAdded);

		return (isWorkflowTriggeredByFieldChange || isWorkflowTriggeredByObjectAddition);
	}

	/**
	 * Checks if list2 has any element in list1
	 * @param list1
	 * @param list2
	 * @return true if list2 have any element in list1 else false
	 */
	private Boolean intersection(List<String> list1, List<String> list2){
		list1.retainAll(list2);
		return !CollectionUtils.isEmpty(list1);

	}



}
