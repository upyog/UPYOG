package org.egov.bpa.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.bpa.repository.ServiceRequestRepository;
import org.egov.bpa.util.BPAErrorConstants;
import org.egov.bpa.util.BPAUtil;
import org.egov.bpa.web.model.BPA;
import org.egov.bpa.web.model.BPARequest;
import org.egov.bpa.web.model.BPASearchCriteria;
import org.egov.bpa.web.model.RequestInfoWrapper;
import org.egov.bpa.web.model.Workflow;
import org.egov.bpa.web.model.landInfo.OwnerInfo;
import org.egov.bpa.web.model.workflow.Action;
import org.egov.bpa.web.model.workflow.BusinessService;
import org.egov.bpa.web.model.workflow.ProcessInstance;
import org.egov.bpa.web.model.workflow.ProcessInstanceResponse;
import org.egov.bpa.web.model.workflow.State;
import org.egov.bpa.workflow.WorkflowService;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;


/**
 * Service to perform the Auto Escalation process
 * 
 *  @author Roshan chaudhary
 */
@Service
@Slf4j
public class BPAAutoEscalationService {

	private ServiceRequestRepository serviceRequestRepository;
	
	private ObjectMapper mapper;
	
	private BPAUtil bpaUtil;
		
	private WorkflowService workflowService;
	
	private UserService userService;
	
	private BPAService bpaService;
	
	@Autowired
	public BPAAutoEscalationService(ServiceRequestRepository serviceRequestRepository, ObjectMapper mapper,
			BPAUtil bpaUtil, WorkflowService workflowService, UserService userService, BPAService bpaService) {
		super();
		this.serviceRequestRepository = serviceRequestRepository;
		this.mapper = mapper;
		this.bpaUtil = bpaUtil;
		this.workflowService = workflowService;
		this.userService = userService;
		this.bpaService = bpaService;
	}

	/**
	 * Start the Auto Escalation process to automatic jump to next state
	 * 
	 * @param processInstances
	 * @param autoEscalationMdmsData
	 * @param requestInfo
	 */
	public void startProcess(List<ProcessInstance> processInstances, Map<String, Object> autoEscalationMdmsData,
			RequestInfo requestInfo) {

		processInstances.forEach(processInstance -> {
			BPASearchCriteria criteria = new BPASearchCriteria();
			criteria.setTenantId(processInstance.getTenantId());
			criteria.setApplicationNo(processInstance.getBusinessId());

			try {
				List<BPA> bpas = bpaService.search(criteria, requestInfo);

				bpas.forEach(bpa -> {
					bpa.setWorkflow(Workflow.builder().action(autoEscalationMdmsData.get("action").toString())
							.comments(autoEscalationMdmsData.get("comment").toString())
							.build());
					bpa.getWorkflow().setAssignes(getAssignees(bpa, requestInfo));
				});

				bpaService.update(BPARequest.builder().requestInfo(requestInfo).BPA(bpas.get(0)).build());
			} catch (Exception e) {
				log.error("Error While Auto Escalating Application : " + processInstance.getBusinessId()
						+ " For Action : " + autoEscalationMdmsData.get("action").toString());
			}

		});

	}
	
	/**
	 * Fetch all the applications those are eligible for the Auto escalation
	 * 
	 * @param autoEscalationMdmsData
	 * @param holidayList
	 * @return Process instances
	 */
	public List<ProcessInstance> fetchAutoEscalationApplications(Map<String, Object> autoEscalationMdmsData, Set<LocalDate> holidayList, RequestInfo requestInfo) {

		StringBuilder url = bpaUtil.getAutoEscalationApplicationsURL(autoEscalationMdmsData, holidayList);
		List<ProcessInstance> processInstances = new ArrayList<>();
		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo)
				.build();
		LinkedHashMap<String, Object> responseMap = null;
		try {
			responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(url, requestInfoWrapper);
			ProcessInstanceResponse instanceResponse = mapper.convertValue(responseMap, ProcessInstanceResponse.class);
			processInstances = instanceResponse.getProcessInstances();
		} catch (Exception e) {
			throw new CustomException(BPAErrorConstants.EG_WF_ERROR, " Unable to fetch the Auto Escalation Eligible Applications records");
		}
		
		return processInstances;
	}
	
	/**
	 * Fetch all the config for the states from the MDMS
	 * 
	 * @param mdmsData
	 * @return List of auto escalation config for states 
	 */
	public List<Map<String, Object>> fetchAutoEscalationMdmsData(Object mdmsData){
		List<Map<String, Object>> autoEscalationMdmsData = new ArrayList<>();
		try {
			autoEscalationMdmsData = JsonPath.read(mdmsData, "$.MdmsRes.Workflow.AutoEscalation");
		} catch (Exception e) {
			throw new CustomException("MDMS_SEARCH_ERROR", " Unable to fetch the Auto Escalation data from MDMS.");
		}
		
		return autoEscalationMdmsData;
	}
	
	/**
	 * Fetch all the holidays from the MDMS data and covert into LOcalDate objects
	 * 
	 * @param mdmsData
	 * @return List of Holidays
	 */
	public Set<LocalDate> getHolidayList(Object mdmsData){
		Set<LocalDate> holidayList = new HashSet<>();
		List<Map<String, Object>> holidayMdmsData = new ArrayList<>();
		try {
			holidayMdmsData = JsonPath.read(mdmsData, "$.MdmsRes.common-masters.Holidays");
			
			holidayMdmsData.forEach(holidayYear -> {
				int year = (int)holidayYear.get("year");
				((List<Map<String, Object>>)holidayYear.get("months")).forEach(holidayMonth -> {
					int month = (int)holidayMonth.get("month");
					((List<Integer>)holidayMonth.get("holidays")).forEach(holiday -> {
						holidayList.add(LocalDate.of(year, month, holiday));
					});
				});
				
			});
		} catch (Exception e) {
			throw new CustomException("MDMS_SEARCH_ERROR", " Unable to fetch the Auto Escalation data from MDMS.");
		}
		
		return holidayList;
	}
	
	/**
	 * Call the MDMS API to fetch the data from MDMS
	 * 
	 * @param requestInfo
	 * @return MDMS data for Auto escalation
	 */
	public Object autoEscalationMdmsCall(RequestInfo requestInfo) {

		StringBuilder url = bpaUtil.getMdmsSearchUrl();
		MdmsCriteriaReq mdmsCriteriaReq = bpaUtil.getMDMSRequestForAutoEscalationData(requestInfo, "pb");
		Object result = null;
		try {
			result = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
		} catch (Exception e) {
			throw new CustomException("MDMS_SEARCH_ERROR", " Unable to fetch data from MDMS.");
		}
		
		return result;
	}
	
	/**
	 * Search all the assignees for the BPA application
	 * 
	 * @param bpa Application for which we need assignees
	 * @param requestInfo 
	 * @return
	 */
	public  List<String> getAssignees(BPA bpa, RequestInfo requestInfo) {
		// Add Assignees in application workflow 
		BusinessService businessService = workflowService.getBusinessService(bpa, requestInfo,
				bpa.getApplicationNo());
		State currentState = workflowService.getCurrentStateObj(bpa.getStatus(), businessService);
		String nextStateId = currentState.getActions().stream()
				.filter(act -> act.getAction().equalsIgnoreCase(bpa.getWorkflow().getAction()))
				.findFirst().orElse(new Action()).getNextState();
		State nextState = businessService.getStates().stream().filter(st -> st.getUuid().equalsIgnoreCase(nextStateId)).findFirst().orElse(null);		

		List<String> roles = new ArrayList<String>();
		if(nextState != null && !CollectionUtils.isEmpty(nextState.getActions()))
			roles = nextState.getActions().stream()
			.filter(stateAction -> !stateAction.getNextState().equalsIgnoreCase(nextStateId))
			.flatMap(stateAction -> stateAction.getRoles().stream())
			.distinct()
			.filter(role -> !role.equalsIgnoreCase("OBPAS_UPDATE_ZONE")
					&& !role.equalsIgnoreCase("SYSTEM"))
			.collect(Collectors.toList());
		
		return userService.getAssigneeFromBPA(bpa, roles, requestInfo, false);	
	}
	
	/**
	 * Create the RequestInfo object of the System user
	 * 
	 * @return RequestInfo object
	 */
	public RequestInfo getDefaultRequestInfo() {

		RequestInfo requestInfo = new RequestInfo();

		OwnerInfo ownerInfo = userService.searchSystemUser();
		User user = mapper.convertValue(ownerInfo, User.class);

		requestInfo.setApiId("Rainmaker");
		requestInfo.setAuthToken("f2761e66-1e35-4b3f-ac9a-a29e9968763c");
		requestInfo.setUserInfo(user);
		return requestInfo;

	}

	
}
