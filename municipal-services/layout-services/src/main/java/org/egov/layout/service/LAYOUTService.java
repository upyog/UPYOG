package org.egov.layout.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.layout.config.LAYOUTConfiguration;
import org.egov.layout.repository.LAYOUTRepository;
import org.egov.layout.repository.ServiceRequestRepository;
import org.egov.layout.util.LAYOUTConstants;
import org.egov.layout.util.LAYOUTUtil;
import org.egov.layout.validator.LAYOUTValidator;
import org.egov.layout.web.model.*;
import org.egov.layout.web.model.bpa.BPASearchCriteria;
import org.egov.layout.web.model.calculator.CalculationCriteria;
import org.egov.layout.web.model.calculator.CalculationReq;
import org.egov.layout.web.model.calculator.CalculationRes;
import org.egov.layout.web.model.workflow.BusinessService;
import org.egov.layout.web.model.workflow.ProcessInstance;
import org.egov.layout.web.model.workflow.ProcessInstanceResponse;
import org.egov.layout.web.model.workflow.*;
import org.egov.layout.workflow.WorkflowIntegrator;
import org.egov.layout.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LAYOUTService {
	
	@Autowired
	private LAYOUTValidator nocValidator;
	
	@Autowired
	private WorkflowIntegrator wfIntegrator;
	
	@Autowired
	private LAYOUTUtil nocUtil;

	@Autowired
	UserService userService;


	@Autowired
	private LAYOUTRepository nocRepository;

	@Autowired
	private LAYOUTConfiguration nocConfiguration;
	
	@Autowired
	private EnrichmentService enrichmentService;
	
	@Autowired
	private WorkflowService workflowService;
	
	@Autowired
	private LAYOUTConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private ObjectMapper mapper;

	/**
	 * entry point from controller, takes care of next level logic from controller to create NOC application
	 * @param nocRequest
	 * @return
	 */
	public String businessServiceVal(LayoutRequest nocRequest){
		String tenantId = nocRequest.getLayout().getTenantId().split("\\.")[0];
		Object additionalDetailsData = nocRequest.getLayout().getNocDetails().getAdditionalDetails();

		// Cast to LinkedHashMap
		Map<String, Object> additionalDetailsMap = (Map<String, Object>) additionalDetailsData;

		// Get siteDetails as a Map
		Map<String, Object> siteDetails = (Map<String, Object>) additionalDetailsMap.get("siteDetails");
		String acres=null;
// Access values
		String ulbType = (String) siteDetails.get("ulbType");
		Map<String, Object> buildingCategory = (Map<String, Object>) siteDetails.get("buildingCategory");
		String buildingCategoryType = (String) buildingCategory.get("code");
		if(buildingCategoryType.equals("RESIDENTIAL")){
			acres = (String) siteDetails.get("areaUnderResidentialUseInSqM");
		}else if(buildingCategoryType.equals("INDUSTRIAL_WAREHOUSE")){
			acres = (String) siteDetails.get("areaUnderInstutionalUseInSqM");
		}
		else{
			acres = (String) siteDetails.get("areaUnderCommercialUseInSqM");
		}


		BigDecimal acresBD = BigDecimal.ZERO;
		if ( acres!= null && !acres.isEmpty()) {
			String sanitized = acres.replace(",", "").trim(); // remove thousands separators
			BigDecimal sqmBD = new BigDecimal(sanitized);
			BigDecimal SQM_PER_ACRE = new BigDecimal("4046.8564224");
			acresBD = sqmBD.divide(SQM_PER_ACRE, 6, RoundingMode.HALF_UP); // 6 decimal places
		}

		 acres = acresBD.toPlainString();



		LinkedHashMap<String, Object> mdmData = (LinkedHashMap<String, Object>) nocUtil.mDMSLayoutCall(nocRequest.getRequestInfo(),tenantId,ulbType,buildingCategoryType,acres);
		LinkedHashMap<String, Object> mdmsRes = (LinkedHashMap<String, Object>) mdmData.get("MdmsRes");
		LinkedHashMap<String, Object> layout_data = (LinkedHashMap<String, Object>) mdmsRes.get("LAYOUT");
		List<Object>  workflow_config =  (List<Object>)layout_data.get("WorkflowConfig");
		LinkedHashMap<String, Object> workflowconfig_data = (LinkedHashMap<String, Object> ) workflow_config.get(0);
		String businessService = (String) workflowconfig_data.get("businessService");
		return businessService;
	}

	public List<Layout> create(LayoutRequest nocRequest) {
		String tenantId = nocRequest.getLayout().getTenantId().split("\\.")[0];
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);

		String businessService = businessServiceVal(nocRequest);
		Object additionalDetailsData = nocRequest.getLayout().getNocDetails().getAdditionalDetails();

		// Cast to LinkedHashMap
		LinkedHashMap<String, Object> additionalDetailsMap = (LinkedHashMap<String, Object>) additionalDetailsData;
		Map<String, Object> siteDetails = (Map<String, Object>) additionalDetailsMap.get("siteDetails");
		siteDetails.put("businessService",businessService);


		Map<String, String> additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getLayout(), mdmsData);

//		nocValidator.validateCreate(nocRequest,  mdmsData);
		enrichmentService.enrichCreateRequest(nocRequest, mdmsData);
		if(!ObjectUtils.isEmpty(nocRequest.getLayout().getWorkflow()) && !StringUtils.isEmpty(nocRequest.getLayout().getWorkflow().getAction())) {
//		  wfIntegrator.callWorkFlow(nocRequest, additionalDetails.get(LAYOUTConstants.WORKFLOWCODE));
			wfIntegrator.callWorkFlow(nocRequest, businessService);
			//wfIntegrator.callWorkFlow(nocRequest, LAYOUTConstants.NOC_BUSINESS_SERVICE);
		}else{
		  nocRequest.getLayout().setApplicationStatus(LAYOUTConstants.CREATED_STATUS);
		}


			userService.createUser(nocRequest.getRequestInfo(),nocRequest.getLayout());

		if(nocRequest.getLayout().getOwners().get(0).getUuid() != null)
			nocRequest.getLayout().setAccountId(nocRequest.getLayout().getOwners().get(0).getUuid());

		if(!CollectionUtils.isEmpty(nocRequest.getLayout().getOwners())) {
			nocRequest.getLayout().getOwners().forEach(owner -> {
				if(owner.getStatus() == null)
					owner.setStatus(true);
			});
		}
		
		nocRepository.save(nocRequest);
		return Arrays.asList(nocRequest.getLayout());
	}

	public void getCalculation(LayoutRequest request,String feeType ){

		List<CalculationCriteria> calculationCriteriaList = new ArrayList<>();
		CalculationCriteria calculationCriteria = CalculationCriteria.builder()
				.layout(request.getLayout())
				.tenantId(request.getLayout().getTenantId())
				.applicationNumber(request.getLayout().getApplicationNo())
				.feeType(feeType)
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



	/**
	 * entry point from controller, takes care of next level logic from controller to update NOC application
	 * @param nocRequest
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Layout> update(LayoutRequest nocRequest) {
		String tenantId = nocRequest.getLayout().getTenantId().split("\\.")[0];
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);
		String businessServicedata = businessServiceVal(nocRequest);
		BusinessService businessServicename = workflowService.getBusinessService(nocRequest.getLayout(),
				nocRequest.getRequestInfo(), businessServicedata);
		Layout layout = nocRequest.getLayout();
		Map<String, String> additionalDetails  ;
		if(!ObjectUtils.isEmpty(nocRequest.getLayout().getNocDetails().getAdditionalDetails()))  {
			additionalDetails = (Map) nocRequest.getLayout().getNocDetails().getAdditionalDetails();
		} else {
			additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getLayout(), mdmsData);
		}
		Layout searchResult= null;
		List<OwnerInfo> owners = nocRequest.getLayout().getOwners();
		if (owners != null) {
			owners.forEach(owner -> {
				if(owner.getStatus() == null)
					owner.setStatus(true);
			});
			userService.createUser(nocRequest.getRequestInfo(),nocRequest.getLayout());
		}

		State currentState = workflowService.getCurrentState(layout.getApplicationStatus(), businessServicename);
		String nextStateId = currentState.getActions().stream()
				.filter(act -> act.getAction().equalsIgnoreCase(layout.getWorkflow().getAction()))
				.findFirst().orElse(new Action()).getNextState();
		State nextState = businessServicename.getStates().stream().filter(st -> st.getUuid().equalsIgnoreCase(nextStateId)).findFirst().orElse(null);

		if (nextState != null && CollectionUtils.isEmpty(layout.getWorkflow().getAssignes())) {
			List<String> roles = new ArrayList<>();
			nextState.getActions().forEach(stateAction -> {
				roles.addAll(stateAction.getRoles());
			});
			List<String> assignee = userService.getAssigneeFromLayout(layout, roles, nocRequest.getRequestInfo());
			layout.getWorkflow().setAssignes(assignee);
		}
		if(nocRequest.getLayout().getWorkflow().getAction().equals(LAYOUTConstants.ACTION_INITIATE) || nocRequest.getLayout().getWorkflow().getAction().equals(LAYOUTConstants.ACTION_APPLY)){
			searchResult = new Layout();
			searchResult.setAuditDetails(nocRequest.getLayout().getAuditDetails());
			searchResult.setApplicationNo(nocRequest.getLayout().getApplicationNo());
			enrichmentService.enrichNocUpdateRequest(nocRequest, searchResult);
			if(!ObjectUtils.isEmpty(nocRequest.getLayout().getWorkflow())
					&& !StringUtils.isEmpty(nocRequest.getLayout().getWorkflow().getAction())) {
				wfIntegrator.callWorkFlow(nocRequest, businessServicedata);
				enrichmentService.postStatusEnrichment(nocRequest, businessServicedata);
				BusinessService businessService = workflowService.getBusinessService(nocRequest.getLayout(),
						nocRequest.getRequestInfo(), businessServicedata);
				if(businessService == null)
					nocRepository.update(nocRequest, true);
				else
					nocRepository.update(nocRequest, workflowService.isStateUpdatable(nocRequest.getLayout().getApplicationStatus(), businessService));
			}else {
				nocRepository.update(nocRequest, Boolean.FALSE);
			}
			nocRepository.update(nocRequest, Boolean.TRUE);

		}else{
			 searchResult = getNocForUpdate(nocRequest);
			if(searchResult.getApplicationStatus().equalsIgnoreCase("AUTO_APPROVED")
					&& nocRequest.getLayout().getApplicationStatus().equalsIgnoreCase("INPROGRESS"))
			{
				log.info("NOC_UPDATE_ERROR_AUTO_APPROVED_TO_INPROGRESS_NOTALLOWED");
				throw new CustomException("AutoApproveException","NOC_UPDATE_ERROR_AUTO_APPROVED_TO_INPROGRESS_NOTALLOWED");
			}
//			nocValidator.validateUpdate(nocRequest, searchResult, additionalDetails.get(LAYOUTConstants.MODE), mdmsData);

			enrichmentService.enrichNocUpdateRequest(nocRequest, searchResult);
			if(!ObjectUtils.isEmpty(nocRequest.getLayout().getWorkflow())
					&& !StringUtils.isEmpty(nocRequest.getLayout().getWorkflow().getAction())) {



				wfIntegrator.callWorkFlow(nocRequest, businessServicedata);
				enrichmentService.postStatusEnrichment(nocRequest, businessServicedata);
				BusinessService businessService = workflowService.getBusinessService(nocRequest.getLayout(),
						nocRequest.getRequestInfo(), businessServicedata);

				if(businessService == null)
					nocRepository.update(nocRequest, true);
				else
					nocRepository.update(nocRequest, workflowService.isStateUpdatable(nocRequest.getLayout().getApplicationStatus(), businessService));
			}else {
				nocRepository.update(nocRequest, Boolean.FALSE);
			}
		}
		if (LAYOUTConstants.ACTION_STATUS_APPLICATION_FEE.equalsIgnoreCase(nocRequest.getLayout().getApplicationStatus()))
			getCalculation(nocRequest, "PAY1");

		if (LAYOUTConstants.ACTION_STATUS_SANCTION_FEE.equalsIgnoreCase(nocRequest.getLayout().getApplicationStatus()))
			getCalculation(nocRequest, "PAY2");


		return Arrays.asList(nocRequest.getLayout());
	}
	/**
	 * entry point from controller,applies the quired fileters and encrich search criteria and
	 * return the layout application matching the search criteria
	 * @param nocRequest
	 * @return
	 */
	public List<Layout> search(LayoutSearchCriteria criteria, RequestInfo requestInfo) {
		/*
		 * List<String> uuids = new ArrayList<String>();
		 * uuids.add(requestInfo.getUserInfo().getUuid()); criteria.setAccountId(uuids);
		 */
		BPASearchCriteria bpaCriteria = new BPASearchCriteria();
		ArrayList<String> sourceRef = new ArrayList<String>();
		List<Layout> nocs = new ArrayList<Layout>();

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
//					Object additionalDetailsObj = layout.getNocDetails().getAdditionalDetails();
//
//					if (additionalDetailsObj instanceof Map) {
//						Map<String, String> details = (Map<String, String>) additionalDetailsObj;
//
//						String sourceRefId = details.get(LAYOUTConstants.SOURCE_RefId);
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
//				url.append(layout.getApplicationNo());
//				url.append("&tenantId=");
//				url.append(layout.getTenantId());
//
//				log.info("Process CALL STARTED" + url);
//				Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
//				ProcessInstanceResponse response = null;
//				try {
//					response = mapper.convertValue(result, ProcessInstanceResponse.class);
//				} catch (IllegalArgumentException e) {
//					throw new CustomException(LAYOUTConstants.PARSING_ERROR, "Failed to parse response of Workflow");
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

				List<String> accountid = nocRepository.getOwnerUserIdsByLayoutId(noc.getId());
//				accountid.add(noc.getAccountId());
				
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
//				StringBuilder uri = new StringBuilder(config.getBpaHost()).append(config.getBpaContextPath())
//						.append(config.getBpaSearchEndpoint());
//
//				uri.append("?tenantId=").append(noc.getTenantId());
//
//
//				Object additionalDetailsObj = noc.getNocDetails().getAdditionalDetails();
//
//				if (additionalDetailsObj instanceof Map) {
//					Map<String, String> details = (Map<String, String>) additionalDetailsObj;
//
//					String sourceRefId = details.get(LAYOUTConstants.SOURCE_RefId);
//					if (sourceRefId != null) {
//						uri.append("&applicationNo=").append(sourceRefId);
//					}
//				}
//
////					uri.append("&applicationNo=").append(layout.getSourceRefId());
//
//
//				System.out.println("BPA CALL STARTED");
//				LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri,
//						requestInfoWrapper);
//				BPAResponse bpaResponse = mapper.convertValue(responseMap, BPAResponse.class);
//				List<BPA> bpaList = new ArrayList<BPA>();
//				bpaList = bpaResponse.getBPA();
//				bpaList.forEach(bpa -> {
//					additionalDetails.put("applicantName", bpa.getLandInfo().getOwners().get(0).getName());
//				});
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
					throw new CustomException(LAYOUTConstants.PARSING_ERROR, "Failed to parse response of Workflow");
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
	 * Fetch the layout based on the id to update the NOC record
	 * @param nocRequest
	 * @return
	 */
	public Layout getNocForUpdate(LayoutRequest nocRequest) {
		List<String> ids = Arrays.asList(nocRequest.getLayout().getId());
//		String mobileNumber = nocRequest.getRequestInfo().getUserInfo().getMobileNumber();
        String tenantid = nocRequest.getLayout().getTenantId();
		String nocNo = nocRequest.getLayout().getLayoutNo();
		String noctype = nocRequest.getLayout().getLayoutType();

		String applicationNo = nocRequest.getLayout().getApplicationNo();

		LayoutSearchCriteria criteria = new LayoutSearchCriteria();
		criteria.setTenantId(tenantid);
		criteria.setIds(ids);
		criteria.setLayoutNo(nocNo);
		criteria.setNocType(noctype);

		criteria.setApplicationNo(applicationNo);


//		criteria.setMobileNumber(mobileNumber);
		List<Layout> nocList = search(criteria, nocRequest.getRequestInfo());
		if (CollectionUtils.isEmpty(nocList) ) {
			StringBuilder builder = new StringBuilder();
			builder.append("Layout Application not found for: ").append(nocRequest.getLayout().getId()).append(" :ID");
			throw new CustomException("INVALID_NOC_SEARCH", builder.toString());
		}else if( nocList.size() > 1) {
			StringBuilder builder = new StringBuilder();
			builder.append("Multiple Layout Application(s) not found for: ").append(nocRequest.getLayout().getId()).append(" :ID");
			throw new CustomException("INVALID_NOC_SEARCH", builder.toString());
		}
		return nocList.get(0);
	}
	
	/**
         * entry point from controller,applies the quired fileters and encrich search criteria and
         * return the layout application count the search criteria
         * @param nocRequest
         * @return
         */
        public Integer getNocCount(LayoutSearchCriteria criteria, RequestInfo requestInfo) {
                /*List<String> uuids = new ArrayList<String>();
                uuids.add(requestInfo.getUserInfo().getUuid());
                criteria.setAccountId(uuids);*/
                return nocRepository.getNocCount(criteria);
        }

	public List<DocumentCheckList> searchDocumentCheckLists(String applicatioinNo, String tenantId){
		if(net.logstash.logback.encoder.org.apache.commons.lang.StringUtils.isEmpty(applicatioinNo))
			throw new CustomException(LAYOUTConstants.INVALID_REQUEST, "Application number should not be null or Empity.");
		return nocRepository.getDocumentCheckList(applicatioinNo, tenantId);
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
	
}
