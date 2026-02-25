package org.egov.noc.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.NOCRepository;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.util.NOCUtil;
import org.egov.noc.validator.NOCValidator;
import org.egov.noc.web.model.*;
import org.egov.noc.web.model.bpa.BPA;
import org.egov.noc.web.model.bpa.BPAResponse;
import org.egov.noc.web.model.bpa.BPASearchCriteria;
import org.egov.noc.web.model.calculator.CalculationCriteria;
import org.egov.noc.web.model.calculator.CalculationReq;
import org.egov.noc.web.model.calculator.CalculationRes;
import org.egov.noc.web.model.workflow.Action;
import org.egov.noc.web.model.workflow.BusinessService;
import org.egov.noc.web.model.workflow.ProcessInstance;
import org.egov.noc.web.model.workflow.ProcessInstanceResponse;
import org.egov.noc.web.model.workflow.State;
import org.egov.noc.workflow.WorkflowIntegrator;
import org.egov.noc.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NOCService {
	
	@Autowired
	private NOCValidator nocValidator;
	
	@Autowired
	private WorkflowIntegrator wfIntegrator;
	
	@Autowired
	private NOCUtil nocUtil;

	@Autowired
	UserService userService;


	@Autowired
	private NOCRepository nocRepository;

	@Autowired
	private NOCConfiguration nocConfiguration;
	
	@Autowired
	private EnrichmentService enrichmentService;
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private NOCConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private NOCPropertyService nocPropertyService;

	/**
	 * entry point from controller, takes care of next level logic from controller to create NOC application
	 * @param nocRequest
	 * @return
	 */
	public List<Noc> create(NocRequest nocRequest) {
		String tenantId = nocRequest.getNoc().getTenantId().split("\\.")[0];
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);
		Map<String, String> additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getNoc(), mdmsData);
		nocValidator.validateCreate(nocRequest,  mdmsData);
		enrichmentService.enrichCreateRequest(nocRequest, mdmsData);
		if(!ObjectUtils.isEmpty(nocRequest.getNoc().getWorkflow()) && !StringUtils.isEmpty(nocRequest.getNoc().getWorkflow().getAction())) {
//		  wfIntegrator.callWorkFlow(nocRequest, additionalDetails.get(NOCConstants.WORKFLOWCODE));
			String businessService = JsonPath.read(nocRequest.getNoc().getNocDetails().getAdditionalDetails(), "$.businessService");
			wfIntegrator.callWorkFlow(nocRequest, businessService);
		}else{
		  nocRequest.getNoc().setApplicationStatus(NOCConstants.CREATED_STATUS);
		}


			userService.createUser(nocRequest.getRequestInfo(),nocRequest.getNoc());

		if(nocRequest.getNoc().getOwners().get(0).getUuid() != null)
			nocRequest.getNoc().setAccountId(nocRequest.getNoc().getOwners().get(0).getUuid());

		if(!CollectionUtils.isEmpty(nocRequest.getNoc().getOwners())) {
			nocRequest.getNoc().getOwners().forEach(owner -> {
				if(owner.getStatus() == null)
					owner.setStatus(true);
			});
		}
		
		nocRepository.save(nocRequest);
		return Arrays.asList(nocRequest.getNoc());
	}

	public List<DocumentCheckList> saveDocumentCheckLists(CheckListRequest checkListRequest){
		Long currentTime = System.currentTimeMillis();
		String userUUID = checkListRequest.getRequestInfo().getUserInfo().getUuid();

		checkListRequest.getCheckList().forEach(document -> {
			document.setId(UUID.randomUUID().toString());
			document.setCreatedtime(currentTime);
			document.setLastmodifiedtime(currentTime);
			document.setCreatedby(userUUID);
			document.setLastmodifiedby(userUUID);
		});
		nocRepository.saveDocumentCheckList(checkListRequest);
		return checkListRequest.getCheckList();
	}
	public List<DocumentCheckList> updateDocumentCheckLists(CheckListRequest checkListRequest){
		Long currentTime = System.currentTimeMillis();
		String userUUID = checkListRequest.getRequestInfo().getUserInfo().getUuid();

		checkListRequest.getCheckList().forEach(document -> {
			document.setLastmodifiedtime(currentTime);
			document.setLastmodifiedby(userUUID);
		});
		nocRepository.updateDocumentCheckList(checkListRequest);
		return checkListRequest.getCheckList();
	}
	public void getCalculation(NocRequest request){

		List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
		CalculationCriteria calculationCriteria = CalculationCriteria.builder()
				.noc(request.getNoc())
				.tenantId(request.getNoc().getTenantId())
				.applicationNumber(request.getNoc().getApplicationNo())
				.build();
		calculationCriteriaList.add(calculationCriteria);

		CalculationReq calculationReq = CalculationReq.builder()
				.requestInfo(request.getRequestInfo())
				.calculationCriteria(calculationCriteriaList)
				.build();

		StringBuilder url = new StringBuilder().append(nocConfiguration.getNocCalculatorHost())
				.append(nocConfiguration.getNocCalculatorEndpoint());
		Object response = serviceRequestRepository.fetchResult(url, calculationReq);
		CalculationRes calculationRes = mapper.convertValue(response, CalculationRes.class);
		log.info("Calculation Response: " + calculationRes);
	}


	public List<DocumentCheckList> searchDocumentCheckLists(String applicatioinNo, String tenantId){
		if(net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isEmpty(applicatioinNo))
			throw new CustomException(NOCConstants.INVALID_REQUEST, "Application number should not be null or Empity.");
		return nocRepository.getDocumentCheckList(applicatioinNo, tenantId);
	}


	/**
	 * entry point from controller, takes care of next level logic from controller to update NOC application
	 * @param nocRequest
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Noc> update(NocRequest nocRequest) {
		String tenantId = nocRequest.getNoc().getTenantId().split("\\.")[0];
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);

		Noc noc = nocRequest.getNoc();


		Map<String, String> additionalDetails  ;
		if(!ObjectUtils.isEmpty(nocRequest.getNoc().getNocDetails().getAdditionalDetails()))  {
			additionalDetails = (Map) nocRequest.getNoc().getNocDetails().getAdditionalDetails();
		} else {
			additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getNoc(), mdmsData);
		}
		String businessServiceName = JsonPath.read(additionalDetails, "$.businessService");
		BusinessService businessServicename = workflowService.getBusinessService(nocRequest.getNoc(),
				nocRequest.getRequestInfo(), businessServiceName);
		Noc searchResult= null;
		List<OwnerInfo> owners = nocRequest.getNoc().getOwners();
		if (owners != null) {
			owners.forEach(owner -> {
				if(owner.getStatus() == null)
					owner.setStatus(true);
			});
			userService.createUser(nocRequest.getRequestInfo(),nocRequest.getNoc());
		}
		Object additionalDetailsData = nocRequest.getNoc().getNocDetails().getAdditionalDetails();
		Map<String, Object> additionalDetailsMap = (Map<String, Object>) additionalDetailsData;

		// Get siteDetails as a Map
		Map<String, Object> siteDetails = (Map<String, Object>) additionalDetailsMap.get("siteDetails");

		// Create Property if Property not available
		List<String> propertyId = JsonPath.read(additionalDetailsData, "$.applicationDetails.owners.*.propertyId");
		propertyId = propertyId.stream().filter(id -> !StringUtils.isEmpty(id)).collect(Collectors.toList());
		Boolean isPropertyAvailable = JsonPath.read(additionalDetailsData, "$.applicationDetails.isPropertyAvailable.value");
		if(CollectionUtils.isEmpty(propertyId) && !isPropertyAvailable && noc.getWorkflow().getAction().equals(NOCConstants.ACTION_APPLY))
			nocPropertyService.createProperty(nocRequest, mdmsData);

		if(nocRequest.getNoc().getWorkflow().getAction().equals(NOCConstants.ACTION_INITIATE) || nocRequest.getNoc().getWorkflow().getAction().equals(NOCConstants.ACTION_APPLY)){
			searchResult = new Noc();
			searchResult.setAuditDetails(nocRequest.getNoc().getAuditDetails());
			searchResult.setApplicationNo(nocRequest.getNoc().getApplicationNo());
			enrichmentService.enrichNocUpdateRequest(nocRequest, searchResult);

			State currentState = workflowService.getCurrentState(noc.getApplicationStatus(), businessServicename);
			String nextStateId = currentState.getActions().stream()
					.filter(act -> act.getAction().equalsIgnoreCase(noc.getWorkflow().getAction()))
					.findFirst().orElse(new Action()).getNextState();
			State nextState = businessServicename.getStates().stream().filter(st -> st.getUuid().equalsIgnoreCase(nextStateId)).findFirst().orElse(null);

			String action = noc.getWorkflow() != null ? noc.getWorkflow().getAction() : "";

			if (nextState != null && nextState.getState().equalsIgnoreCase(NOCConstants.FI_STATUS)
					&& (NOCConstants.ACTION_APPLY.equalsIgnoreCase(action) || NOCConstants.ACTION_RESUBMIT.equalsIgnoreCase(action))) {
				List<String> roles = new ArrayList<>();
				nextState.getActions().forEach(stateAction -> {
					roles.addAll(stateAction.getRoles());
				});
				List<String> assignee = userService.getAssigneeFromNOC(noc, roles, nocRequest.getRequestInfo());
				noc.getWorkflow().setAssignes(assignee);
			}

			if(!ObjectUtils.isEmpty(nocRequest.getNoc().getWorkflow())
					&& !StringUtils.isEmpty(nocRequest.getNoc().getWorkflow().getAction())) {


				wfIntegrator.callWorkFlow(nocRequest, businessServiceName);
				enrichmentService.postStatusEnrichment(nocRequest, businessServiceName);
				BusinessService businessService = workflowService.getBusinessService(nocRequest.getNoc(),
						nocRequest.getRequestInfo(), businessServiceName);
				if(businessService == null)
					nocRepository.update(nocRequest, true);
				else
					nocRepository.update(nocRequest, workflowService.isStateUpdatable(nocRequest.getNoc().getApplicationStatus(), businessService));
			}else {
				nocRepository.update(nocRequest, Boolean.FALSE);
			}
			nocRepository.update(nocRequest, Boolean.TRUE);

		}else{
			 searchResult = getNocForUpdate(nocRequest);
			if(searchResult.getApplicationStatus().equalsIgnoreCase("AUTO_APPROVED")
					&& nocRequest.getNoc().getApplicationStatus().equalsIgnoreCase("INPROGRESS"))
			{
				log.info("NOC_UPDATE_ERROR_AUTO_APPROVED_TO_INPROGRESS_NOTALLOWED");
				throw new CustomException("AutoApproveException","NOC_UPDATE_ERROR_AUTO_APPROVED_TO_INPROGRESS_NOTALLOWED");
			}
//			nocValidator.validateUpdate(nocRequest, searchResult, additionalDetails.get(NOCConstants.MODE), mdmsData);

			enrichmentService.enrichNocUpdateRequest(nocRequest, searchResult);
			if(!ObjectUtils.isEmpty(nocRequest.getNoc().getWorkflow())
					&& !StringUtils.isEmpty(nocRequest.getNoc().getWorkflow().getAction())) {
				
				if (nocRequest.getNoc().getWorkflow().getAction().equalsIgnoreCase(NOCConstants.ACTION_APPROVE)) {
					((Map<String, Object>)nocRequest.getNoc().getNocDetails()
							.getAdditionalDetails()).put("approvedOn", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
					getCalculation(nocRequest);
				}else if (nocRequest.getNoc().getWorkflow().getAction().equalsIgnoreCase(NOCConstants.ACTION_UPDATE_FEE))
					getCalculation(nocRequest);
				
				State currentState = workflowService.getCurrentState(noc.getApplicationStatus(), businessServicename);
				String nextStateId = currentState.getActions().stream()
						.filter(act -> act.getAction().equalsIgnoreCase(noc.getWorkflow().getAction()))
						.findFirst().orElse(new Action()).getNextState();
				State nextState = businessServicename.getStates().stream().filter(st -> st.getUuid().equalsIgnoreCase(nextStateId)).findFirst().orElse(null);

				String action = noc.getWorkflow() != null ? noc.getWorkflow().getAction() : "";

				if (nextState != null && nextState.getState().equalsIgnoreCase(NOCConstants.FI_STATUS)
						&& (NOCConstants.ACTION_APPLY.equalsIgnoreCase(action) || NOCConstants.ACTION_RESUBMIT.equalsIgnoreCase(action))) {
					List<String> roles = new ArrayList<>();
					nextState.getActions().forEach(stateAction -> {
						roles.addAll(stateAction.getRoles());
					});
					List<String> assignee = userService.getAssigneeFromNOC(noc, roles, nocRequest.getRequestInfo());
					noc.getWorkflow().setAssignes(assignee);
				}


				wfIntegrator.callWorkFlow(nocRequest,businessServiceName);
				enrichmentService.postStatusEnrichment(nocRequest, businessServiceName);
				BusinessService businessService = workflowService.getBusinessService(nocRequest.getNoc(),
						nocRequest.getRequestInfo(), businessServiceName);

				if(businessService == null)
					nocRepository.update(nocRequest, true);
				else
					nocRepository.update(nocRequest, workflowService.isStateUpdatable(nocRequest.getNoc().getApplicationStatus(), businessService));
			}else {
				nocRepository.update(nocRequest, Boolean.FALSE);
			}
		}


		return Arrays.asList(nocRequest.getNoc());
	}
	/**
	 * entry point from controller,applies the quired fileters and encrich search criteria and
	 * return the noc application matching the search criteria
	 * @param nocRequest
	 * @return
	 */
	public List<Noc> search(NocSearchCriteria criteria, RequestInfo requestInfo) {
		/*
		 * List<String> uuids = new ArrayList<String>();
		 * uuids.add(requestInfo.getUserInfo().getUuid()); criteria.setAccountId(uuids);
		 */
		BPASearchCriteria bpaCriteria = new BPASearchCriteria();
		ArrayList<String> sourceRef = new ArrayList<String>();
		List<Noc> nocs = new ArrayList<Noc>();

		RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

		criteria.setCreatedBy(requestInfo.getUserInfo().getUuid());
		if (criteria.getMobileNumber() != null) {
//			StringBuilder uri = new StringBuilder(config.getBpaHost()).append(config.getBpaContextPath())
//					.append(config.getBpaSearchEndpoint());
//			uri.append("?tenantId=").append(criteria.getTenantId());
//
//			if (criteria.getSourceRefId() != null)
//			{   uri.append("&applicationNo=").append(criteria.getSourceRefId());
//				uri.append("&mobileNumber=").append(criteria.getMobileNumber());
//			}else
//			{   uri.append("&mobileNumber=").append(criteria.getMobileNumber());
//
//			}


				UserResponse userDetailResponse = userService.getUser(criteria, requestInfo);
				// If user not found with given user fields return empty list
				if (userDetailResponse.getUser().isEmpty()) {
					return Collections.emptyList();
				} else {
					criteria.setOwnerIds(userDetailResponse.getUser().stream().map(OwnerInfo::getUuid).collect(Collectors.toList()));
				}
//			log.info("BPA CALL STARTED");
//			LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri, requestInfoWrapper);
//			BPAResponse bpaResponse = mapper.convertValue(responseMap, BPAResponse.class);
//			List<BPA> bpas = bpaResponse.getBPA();
//			Map<String, String> bpaDetails = new HashMap<String, String>();
//			bpas.forEach(bpa -> {
//				bpaDetails.put("applicantName", bpa.getLandInfo().getOwners().get(0).getName());
//				bpaDetails.put("sourceRef", bpa.getApplicationNo());
//				sourceRef.add(bpa.getApplicationNo());
//			});
//			if (!sourceRef.isEmpty()) {
//				criteria.setSourceRefId(sourceRef.toString());
//			}
//			if(criteria.getMobileNumber() != null && CollectionUtils.isEmpty(bpas)){
//				return nocs;
//			}
			log.info("NOC CALL STARTED" + criteria.getSourceRefId());
			nocs = nocRepository.getNocData(criteria);
			nocs.forEach(noc -> {
				Map<String, String> additionalDetails = noc.getNocDetails().getAdditionalDetails() != null
						? (Map<String, String>) noc.getNocDetails().getAdditionalDetails()
						: new HashMap<String, String>();


					noc.setOwners(userDetailResponse.getUser());


//				for (BPA bpa : bpas) {
//
//					Object additionalDetailsObj = noc.getNocDetails().getAdditionalDetails();
//
//					if (additionalDetailsObj instanceof Map) {
//						Map<String, String> details = (Map<String, String>) additionalDetailsObj;
//
//						String sourceRefId = details.get(NOCConstants.SOURCE_RefId);
//						if (bpa.getApplicationNo().equals(sourceRefId)) {
//							additionalDetails.put("applicantName", bpa.getLandInfo().getOwners().get(0).getName());
//						}
//					}
//
//
//				}
//				StringBuilder url = new StringBuilder(config.getWfHost());
//				url.append(config.getWfProcessPath());
//				url.append("?businessIds=");
//				url.append(noc.getApplicationNo());
//				url.append("&tenantId=");
//				url.append(noc.getTenantId());
//
//				log.info("Process CALL STARTED" + url);
//				Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
//				ProcessInstanceResponse response = null;
//				try {
//					response = mapper.convertValue(result, ProcessInstanceResponse.class);
//				} catch (IllegalArgumentException e) {
//					throw new CustomException(NOCConstants.PARSING_ERROR, "Failed to parse response of Workflow");
//				}
//				if(response.getProcessInstances()!=null && !response.getProcessInstances().isEmpty()) {
//					ProcessInstance nocProcess = response.getProcessInstances().get(0);
//					if (nocProcess.getAssignee() != null) {
//						additionalDetails.put("currentOwner", nocProcess.getAssignee().getName());
//					} else {
//						additionalDetails.put("currentOwner", null);
//					}
//				} else {
//					additionalDetails.put("currentOwner", null);
//				}
			});


		} else {
			log.info("IN 2 NOC CALL STARTED" + criteria.getSourceRefId());
			nocs = nocRepository.getNocData(criteria);
			nocs.forEach(noc -> {
				Map<String, String> additionalDetails = noc.getNocDetails().getAdditionalDetails() != null
						? (Map<String, String>) noc.getNocDetails().getAdditionalDetails()
						: new HashMap<String, String>();

				List<String> accountid = nocRepository.getOwnerUserIdsByNocId(noc.getId());
				List<OwnerInfo> owner = new ArrayList<>();
				
				if(!CollectionUtils.isEmpty(accountid)) {
					criteria.setAccountId(accountid);
					UserResponse userDetailResponse = userService.getUser(criteria, requestInfo);
					owner = userDetailResponse.getUser();
				}

				Map<String, Object>adByUuid = Optional.ofNullable(noc.getOwners())
						.orElse(Collections.emptyList())
						.stream()
						.filter(oi -> oi.getUuid() != null && oi.getAdditionalDetails() != null)
						.collect(Collectors.toMap(
								OwnerInfo::getUuid,
								OwnerInfo::getAdditionalDetails,
								(a, b) -> a // keep first on duplicate uuid
						));


// Merge by uuid
				for (OwnerInfo oi : owner) {
					String uuid = oi.getUuid(); // ensure this getter exists
					oi.setStatus(true);
					if (uuid != null) {
						Object ad = adByUuid.get(uuid);
						if (ad != null) {
							oi.setAdditionalDetails(ad);
						}
					}
				}


				noc.setOwners(owner);

				// BPA CALL
				StringBuilder uri = new StringBuilder(config.getBpaHost()).append(config.getBpaContextPath())
						.append(config.getBpaSearchEndpoint());

				uri.append("?tenantId=").append(noc.getTenantId());


				Object additionalDetailsObj = noc.getNocDetails().getAdditionalDetails();

				if (additionalDetailsObj instanceof Map) {
					Map<String, String> details = (Map<String, String>) additionalDetailsObj;

					String sourceRefId = details.get(NOCConstants.SOURCE_RefId);
					if (sourceRefId != null) {
						uri.append("&applicationNo=").append(sourceRefId);
					}
				}

//					uri.append("&applicationNo=").append(noc.getSourceRefId());


				System.out.println("BPA CALL STARTED");
				LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri,
						requestInfoWrapper);
				BPAResponse bpaResponse = mapper.convertValue(responseMap, BPAResponse.class);
				List<BPA> bpaList = new ArrayList<BPA>();
				bpaList = bpaResponse.getBPA();
				bpaList.forEach(bpa -> {
					additionalDetails.put("applicantName", bpa.getLandInfo().getOwners().get(0).getName());
				});
				log.info("ADDITIONAL DETAILS :: " + additionalDetails.get("applicantName"));
				// PROCESS CALL
				StringBuilder url = new StringBuilder(config.getWfHost());
				url.append(config.getWfProcessPath());
				url.append("?businessIds=");
				url.append(noc.getApplicationNo());
				url.append("&tenantId=");
				url.append(noc.getTenantId());
							
				log.info("Process 2 CALL STARTED" + url);
				Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
				ProcessInstanceResponse response = null;
				try {
					response = mapper.convertValue(result, ProcessInstanceResponse.class);
				} catch (IllegalArgumentException e) {
					throw new CustomException(NOCConstants.PARSING_ERROR, "Failed to parse response of Workflow");
				}
				log.info("ProcessInstance :: " + response.getProcessInstances());
				if(response.getProcessInstances()!=null && !response.getProcessInstances().isEmpty()) {
					ProcessInstance nocProcess = response.getProcessInstances().get(0);
					if (nocProcess.getAssignee() != null) {
						additionalDetails.put("currentOwner", nocProcess.getAssignee().getName());
					} else {
						additionalDetails.put("currentOwner", null);
					}
				}else {
					additionalDetails.put("currentOwner", null);
				}
				log.info("ADDITIONAL DETAILS :: " + additionalDetails.get("currentOwner"));
			});
		}
		return nocs.isEmpty() ? Collections.emptyList() : nocs;
	}
	
	/**
	 * Fetch the noc based on the id to update the NOC record
	 * @param nocRequest
	 * @return
	 */
	public Noc getNocForUpdate(NocRequest nocRequest) {		
		List<String> ids = Arrays.asList(nocRequest.getNoc().getId());
//		String mobileNumber = nocRequest.getRequestInfo().getUserInfo().getMobileNumber();
        String tenantid = nocRequest.getNoc().getTenantId();
		String nocNo = nocRequest.getNoc().getNocNo();
		String noctype = nocRequest.getNoc().getNocType();

		String applicationNo = nocRequest.getNoc().getApplicationNo();

		NocSearchCriteria criteria = new NocSearchCriteria();
		criteria.setTenantId(tenantid);
		criteria.setIds(ids);
		criteria.setNocNo(nocNo);
		criteria.setNocType(noctype);

		criteria.setApplicationNo(applicationNo);


//		criteria.setMobileNumber(mobileNumber);
		List<Noc> nocList = search(criteria, nocRequest.getRequestInfo());
		if (CollectionUtils.isEmpty(nocList) ) {
			StringBuilder builder = new StringBuilder();
			builder.append("Noc Application not found for: ").append(nocRequest.getNoc().getId()).append(" :ID");
			throw new CustomException("INVALID_NOC_SEARCH", builder.toString());
		}else if( nocList.size() > 1) {
			StringBuilder builder = new StringBuilder();
			builder.append("Multiple Noc Application(s) not found for: ").append(nocRequest.getNoc().getId()).append(" :ID");
			throw new CustomException("INVALID_NOC_SEARCH", builder.toString());
		}
		return nocList.get(0);
	}
	
	/**
         * entry point from controller,applies the quired fileters and encrich search criteria and
         * return the noc application count the search criteria
         * @param nocRequest
         * @return
         */
        public Integer getNocCount(NocSearchCriteria criteria, RequestInfo requestInfo) {
                /*List<String> uuids = new ArrayList<String>();
                uuids.add(requestInfo.getUserInfo().getUuid());
                criteria.setAccountId(uuids);*/
                return nocRepository.getNocCount(criteria);
        }
	
}
