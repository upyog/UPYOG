package org.egov.noc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.utils.MultiStateInstanceUtil;
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
import org.egov.noc.web.model.workflow.BusinessService;
import org.egov.noc.web.model.workflow.ProcessInstance;
import org.egov.noc.web.model.workflow.ProcessInstanceResponse;
import org.egov.noc.workflow.WorkflowIntegrator;
import org.egov.noc.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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
	private NOCRepository nocRepository;
	
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
	private MultiStateInstanceUtil centralInstanceUtil;
	/**
	 * entry point from controller, takes care of next level logic from controller to create NOC application
	 * @param nocRequest
	 * @return
	 */
	public List<Noc> create(NocRequest nocRequest) {
		String tenantId = centralInstanceUtil.getStateLevelTenant(nocRequest.getNoc().getTenantId());
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);
		Map<String, String> additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getNoc(), mdmsData);
		nocValidator.validateCreate(nocRequest,  mdmsData);
		enrichmentService.enrichCreateRequest(nocRequest, mdmsData);
		if(!ObjectUtils.isEmpty(nocRequest.getNoc().getWorkflow()) && !StringUtils.isEmpty(nocRequest.getNoc().getWorkflow().getAction())) {
		  wfIntegrator.callWorkFlow(nocRequest, additionalDetails.get(NOCConstants.WORKFLOWCODE));
		}else{
		  nocRequest.getNoc().setApplicationStatus(NOCConstants.CREATED_STATUS);
		}
		nocRepository.save(nocRequest);
		return Arrays.asList(nocRequest.getNoc());
	}
	/**
	 * entry point from controller, takes care of next level logic from controller to update NOC application
	 * @param nocRequest
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<Noc> update(NocRequest nocRequest) {
		String tenantId = centralInstanceUtil.getStateLevelTenant(nocRequest.getNoc().getTenantId());
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);
		Map<String, String> additionalDetails  ;
		if(!ObjectUtils.isEmpty(nocRequest.getNoc().getAdditionalDetails()))  {
			additionalDetails = (Map) nocRequest.getNoc().getAdditionalDetails();
		} else {
			additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getNoc(), mdmsData);
		}
		Noc searchResult = getNocForUpdate(nocRequest);
		if(searchResult.getApplicationStatus().equalsIgnoreCase("AUTO_APPROVED")
				&& nocRequest.getNoc().getApplicationStatus().equalsIgnoreCase("INPROGRESS"))
		{
			log.info("NOC_UPDATE_ERROR_AUTO_APPROVED_TO_INPROGRESS_NOTALLOWED");
			throw new CustomException("AutoApproveException","NOC_UPDATE_ERROR_AUTO_APPROVED_TO_INPROGRESS_NOTALLOWED");
		}
		nocValidator.validateUpdate(nocRequest, searchResult, additionalDetails.get(NOCConstants.MODE), mdmsData);
		enrichmentService.enrichNocUpdateRequest(nocRequest, searchResult);
		
		if(!ObjectUtils.isEmpty(nocRequest.getNoc().getWorkflow())
				&& !StringUtils.isEmpty(nocRequest.getNoc().getWorkflow().getAction())) {
		   wfIntegrator.callWorkFlow(nocRequest, additionalDetails.get(NOCConstants.WORKFLOWCODE));
		   enrichmentService.postStatusEnrichment(nocRequest, additionalDetails.get(NOCConstants.WORKFLOWCODE));
		   BusinessService businessService = workflowService.getBusinessService(nocRequest.getNoc(),
				   nocRequest.getRequestInfo(), additionalDetails.get(NOCConstants.WORKFLOWCODE));
		   if(businessService == null)
			   nocRepository.update(nocRequest, true);
		   else
			   nocRepository.update(nocRequest, workflowService.isStateUpdatable(nocRequest.getNoc().getApplicationStatus(), businessService));
		}else {
           nocRepository.update(nocRequest, Boolean.FALSE);
		}
		
		return Arrays.asList(nocRequest.getNoc());
	}
	/**
	 * entry point from controller,applies the quired fileters and encrich search criteria and
	 * return the noc application matching the search criteria
	 * @param criteria
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
		if (criteria.getMobileNumber() != null) {
			StringBuilder uri = new StringBuilder(config.getBpaHost()).append(config.getBpaContextPath())
					.append(config.getBpaSearchEndpoint());
			uri.append("?tenantId=").append(criteria.getTenantId());

			if (criteria.getSourceRefId() != null)
			{   uri.append("&applicationNo=").append(criteria.getSourceRefId());
				uri.append("&mobileNumber=").append(criteria.getMobileNumber());
			}else
			{   uri.append("&mobileNumber=").append(criteria.getMobileNumber());}
			log.info("BPA CALL STARTED");
			LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(uri, requestInfoWrapper);
			BPAResponse bpaResponse = mapper.convertValue(responseMap, BPAResponse.class);
			List<BPA> bpas = bpaResponse.getBPA();
			Map<String, String> bpaDetails = new HashMap<String, String>();
			bpas.forEach(bpa -> {
				bpaDetails.put("applicantName", bpa.getLandInfo().getOwners().get(0).getName());
				bpaDetails.put("sourceRef", bpa.getApplicationNo());
				sourceRef.add(bpa.getApplicationNo());
			});
			if (!sourceRef.isEmpty()) {
				criteria.setSourceRefId(sourceRef.toString());
			}
			if(criteria.getMobileNumber() != null && CollectionUtils.isEmpty(bpas)){
				return nocs;
			}
			log.info("NOC CALL STARTED" + criteria.getSourceRefId());
			nocs = nocRepository.getNocData(criteria);
			nocs.forEach(noc -> {
				Map<String, String> additionalDetails = noc.getAdditionalDetails() != null
						? (Map<String, String>) noc.getAdditionalDetails()
						: new HashMap<String, String>();
				for (BPA bpa : bpas) {
					if (bpa.getApplicationNo().equals(noc.getSourceRefId())) {
						additionalDetails.put("applicantName", bpa.getLandInfo().getOwners().get(0).getName());
					}
				}
				StringBuilder url = new StringBuilder(config.getWfHost());
				url.append(config.getWfProcessPath());
				url.append("?businessIds=");
				url.append(noc.getApplicationNo());
				url.append("&tenantId=");
				url.append(noc.getTenantId());
					
				log.info("Process CALL STARTED" + url);
				Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
				ProcessInstanceResponse response = null;
				try {
					response = mapper.convertValue(result, ProcessInstanceResponse.class);
				} catch (IllegalArgumentException e) {
					throw new CustomException(NOCConstants.PARSING_ERROR, "Failed to parse response of Workflow");
				}
				if(response.getProcessInstances()!=null && !response.getProcessInstances().isEmpty()) {
					ProcessInstance nocProcess = response.getProcessInstances().get(0);
					if (nocProcess.getAssignee() != null) {
						additionalDetails.put("currentOwner", nocProcess.getAssignee().getName());
					} else {
						additionalDetails.put("currentOwner", null);
					}
				} else {
					additionalDetails.put("currentOwner", null);
				}
			});

		} else {
			log.info("IN 2 NOC CALL STARTED" + criteria.getSourceRefId());
			nocs = nocRepository.getNocData(criteria);
			nocs.forEach(noc -> {
				Map<String, String> additionalDetails = noc.getAdditionalDetails() != null
						? (Map<String, String>) noc.getAdditionalDetails()
						: new HashMap<String, String>();

				// BPA CALL
				StringBuilder uri = new StringBuilder(config.getBpaHost()).append(config.getBpaContextPath())
						.append(config.getBpaSearchEndpoint());

				uri.append("?tenantId=").append(noc.getTenantId());
				uri.append("&applicationNo=").append(noc.getSourceRefId());
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
		NocSearchCriteria criteria = new NocSearchCriteria();
		criteria.setTenantId(nocRequest.getNoc().getTenantId());
		criteria.setIds(ids);
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

	/**
	 * Fetches newly created AAI NOC applications for given tenant
	 * If tenantId is null, fetches from default tenant 'as'
	 *
	 * @param tenantId Tenant ID or null for default
	 * @return List of NOC records in CREATED status
	 */
	public List<Noc> fetchNewAAINOCs(String tenantId) {
		NocSearchCriteria criteria = new NocSearchCriteria();
		String applicationStatuses = NOCConstants.APPLICATION_STATUS_INPROGRESS + "," + NOCConstants.AAI_STATUS_INPROCESS;
		criteria.setApplicationStatus(applicationStatuses);
		criteria.setNocType(NOCConstants.CIVIL_AVIATION_NOC_TYPE);
		criteria.setTenantId(tenantId);
		criteria.setLimit(config.getMaxSearchLimit());
		criteria.setOffset(0);
		return nocRepository.getNocData(criteria);
	}

	/**
	 * Retrieves BPA details for each NOC by calling the BPA service.
	 *
	 * @param nocList            list of NOC applications
	 * @param requestInfoWrapper request info for service call
	 * @return list of BPA objects mapped from service responses
	 */
	public List<BPA> getBPADetails(List<Noc> nocList, RequestInfoWrapper requestInfoWrapper) {

		List<BPA> bpaList = new ArrayList<>();

		for (Noc noc : nocList) {

			StringBuilder uri = new StringBuilder(config.getBpaHost());
			uri.append(config.getBpaContextPath());
			uri.append(config.getBpaSearchEndpoint());
			uri.append("?tenantId=").append(noc.getTenantId());
			uri.append("&applicationNo=").append(noc.getSourceRefId());

			Object result = serviceRequestRepository.fetchResult(uri, requestInfoWrapper);
			try {
				BPAResponse bpaResponse = mapper.convertValue(result, BPAResponse.class);
				List<BPA> bpa = bpaResponse.getBPA();
				bpaList.addAll(bpa);
			} catch (IllegalArgumentException e) {
				throw new CustomException(NOCConstants.PARSING_ERROR, "Failed to parse response of BPA");
			}
		}
		return bpaList;
	}

	/**
	 *
	 * Batch Fetch BPAs by mobile number
	 * @param criteria
	 * @param wrapper
	 * @return List of BPA
	 */
	@SuppressWarnings("unchecked")
	private List<BPA> fetchBpasByMobile(NocSearchCriteria criteria, RequestInfoWrapper wrapper) {
		String uri = config.getBpaHost() + config.getBpaContextPath() + config.getBpaSearchEndpoint()
				+ "?tenantId=" + criteria.getTenantId()
				+ "&mobileNumber=" + criteria.getMobileNumber();

		LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(new StringBuilder(uri), wrapper);
		BPAResponse response = mapper.convertValue(responseMap, BPAResponse.class);
		return response.getBPA();
	}

	/**
	 * Batch fetch Owner Names from BPA (Fixes URI corruption)
	 * @param bpaNos
	 * @param tenantId
	 * @param wrapper
	 * @return Map of BPA No to Owner Name
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> fetchBpaOwnerMap(List<String> bpaNos, String tenantId, RequestInfoWrapper wrapper) {
		if (CollectionUtils.isEmpty(bpaNos)) return Collections.emptyMap();

		StringBuilder uri = new StringBuilder(config.getBpaHost())
				.append(config.getBpaContextPath())
				.append(config.getBpaSearchEndpoint())
				.append("?tenantId=").append(tenantId)
				.append("&applicationNo=").append(String.join(",", bpaNos));

		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) serviceRequestRepository.fetchResult(uri, wrapper);
			BPAResponse response = mapper.convertValue(responseMap, BPAResponse.class);

			return response.getBPA().stream().collect(Collectors.toMap(BPA::getApplicationNo,
					bpa -> (bpa.getLandInfo() != null && !bpa.getLandInfo().getOwners().isEmpty())
							? bpa.getLandInfo().getOwners().get(0).getName() : "NA",
					(existing, replacement) -> existing));

		} catch (Exception e) {
			log.error("Error fetching BPA owner names", e);
			return Collections.emptyMap();
		}
	}

	/**
	 * Batch fetch Workflow Assignees
	 * @param nocNos
	 * @param tenantId
	 * @param wrapper
	 * @return Map of NOC No to Assignee Name
	 */
	private Map<String, String> fetchWorkflowAssigneeMap(List<String> nocNos, String tenantId, RequestInfoWrapper wrapper) {
		if (CollectionUtils.isEmpty(nocNos)) return Collections.emptyMap();

		StringBuilder url = new StringBuilder(config.getWfHost())
				.append(config.getWfProcessPath())
				.append("?businessIds=").append(String.join(",", nocNos))
				.append("&tenantId=").append(tenantId);

		Object result = serviceRequestRepository.fetchResult(url, wrapper);
		ProcessInstanceResponse response = mapper.convertValue(result, ProcessInstanceResponse.class);

		return response.getProcessInstances().stream().collect(Collectors.toMap(
				ProcessInstance::getBusinessId,
				pi -> pi.getAssignee() != null ? pi.getAssignee().getName() : "NA",
				(a, b) -> a
		));
	}
}
