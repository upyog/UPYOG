package org.egov.noc.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;


import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.request.Role;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.repository.NOCRepository;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.util.NOCConstants;
import org.egov.noc.util.NOCUtil;
import org.egov.noc.validator.NOCValidator;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.NocRequest;
import org.egov.noc.web.model.NocSearchCriteria;
import org.egov.noc.web.model.RequestInfoWrapper;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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
		String tenantId = nocRequest.getNoc().getTenantId().split("\\.")[0];
		Object mdmsData = nocUtil.mDMSCall(nocRequest.getRequestInfo(), tenantId);
		Map<String, String> additionalDetails  ;
		if(!ObjectUtils.isEmpty(nocRequest.getNoc().getAdditionalDetails()))  {
			additionalDetails = (Map) nocRequest.getNoc().getAdditionalDetails();
		} else {
			additionalDetails = nocValidator.getOrValidateBussinessService(nocRequest.getNoc(), mdmsData);
		}
		Noc searchResult = getNocForUpdate(nocRequest);
	    log.info("Application status in DB: {}", searchResult.getApplicationStatus());
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

	
		public List<Noc> search(NocSearchCriteria criteria, RequestInfo requestInfo) {
		    if (criteria.getTenantId() == null) {
		        throw new CustomException("INVALID_SEARCH", "tenantId is mandatory");
		    }
		    User user = requestInfo.getUserInfo();
		    String userType = user.getType();
		    List<String> roleCodes = user.getRoles()
		            .stream()
		            .map(Role::getCode)
		            .collect(Collectors.toList());
		    
		    if ("CITIZEN".equalsIgnoreCase(userType)) {
		        criteria.setAccountId(Collections.singletonList(user.getUuid()));
		        criteria.setStatus(null);
		    }
		  else if ("EMPLOYEE".equalsIgnoreCase(userType)) {
			  
			  List<Role> tenantRoles = user.getRoles().stream()
					    .filter(role -> criteria.getTenantId().equals(role.getTenantId()))
					    .collect(Collectors.toList());

					if (tenantRoles.isEmpty()) {
					    throw new CustomException(
					        "UNAUTHORIZED",
					        "Employee not authorized for this tenant"
					    );
					}

			    Set<String> statuses = new HashSet<>();
			
			    if (tenantRoles.stream().anyMatch(r -> "NOC_NODAL".equals(r.getCode()))) {
			        statuses.add("PENDINGFORVERIFICATION");
			        statuses.add("PENDINGFORMODIFICATION");
			    }

			    if (tenantRoles.stream().anyMatch(r -> "NOC_APPROVER".equals(r.getCode()))) {
			        statuses.add("PENDINGFORAPPROVAL");
			        statuses.add("APPROVED");
			    }
			
			    if (statuses.isEmpty()) {
			        throw new CustomException(
			            "UNAUTHORIZED",
			            "Employee not authorized to search NOCs"
			        );
			    }

    criteria.setStatus(new ArrayList<>(statuses));
}

		    else {
		        throw new CustomException("UNAUTHORIZED",
		                "User not authorized to search NOCs");
		    }
		    List<Noc> nocs = nocRepository.getNocData(criteria);
		    if (CollectionUtils.isEmpty(nocs)) {
		        return Collections.emptyList();
		    }
		    RequestInfoWrapper requestInfoWrapper =
		            RequestInfoWrapper.builder().requestInfo(requestInfo).build();
		    for (Noc noc : nocs) {
		        Map<String, Object> additionalDetails = new HashMap<>();
		        if (noc.getAdditionalDetails() instanceof Map) {
		            additionalDetails = (Map<String, Object>) noc.getAdditionalDetails();
		        }
		        StringBuilder url = new StringBuilder(config.getWfHost())
		                .append(config.getWfProcessPath())
		                .append("?businessIds=").append(noc.getApplicationNo())
		                .append("&tenantId=").append(noc.getTenantId());
		        Object result = serviceRequestRepository.fetchResult(url, requestInfoWrapper);
		        ProcessInstanceResponse response;
		        try {
		            response = mapper.convertValue(result, ProcessInstanceResponse.class);
		        } catch (IllegalArgumentException e) {
		            throw new CustomException(
		                    NOCConstants.PARSING_ERROR,
		                    "Failed to parse workflow response"
		            );
		        }
		        if (response.getProcessInstances() != null
		                && !response.getProcessInstances().isEmpty()
		                && response.getProcessInstances().get(0).getAssignee() != null) {
		            additionalDetails.put(
		                    "currentOwner",
		                    response.getProcessInstances().get(0).getAssignee().getName()
		            );
		        } else {
		            additionalDetails.put("currentOwner", null);
		        }
		        noc.setAdditionalDetails(additionalDetails);
		    }
		    return nocs;
		}

	/**
	 * Fetch the noc based on the id to update the NOC record
	 * @param nocRequest
	 * @return
	 */
	public Noc getNocForUpdate(NocRequest nocRequest) {		
		List<String> ids = Arrays.asList(nocRequest.getNoc().getId());
		NocSearchCriteria criteria = new NocSearchCriteria();
		criteria.setIds(ids);
		criteria.setTenantId(nocRequest.getNoc().getTenantId());
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
