package org.egov.ndc.service;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.PlainAccessRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ndc.config.NDCConfiguration;
import org.egov.ndc.repository.IdGenRepository;
import org.egov.ndc.util.NDCConstants;
import org.egov.ndc.util.NDCUtil;
import org.egov.ndc.web.model.*;
import org.egov.ndc.web.model.idgen.IdResponse;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.egov.ndc.web.model.workflow.BusinessService;
import org.egov.ndc.web.model.workflow.ProcessInstance;
import org.egov.ndc.web.model.workflow.SearchCriteria;
import org.egov.ndc.web.model.workflow.State;
import org.egov.ndc.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnrichmentService {

	@Autowired
	private NDCConfiguration config;

	@Autowired
	private NDCUtil ndcUtil;

	@Autowired
	private IdGenRepository idGenRepository;

	@Autowired
	private WorkflowService workflowService;


	/**
	 * called on success of the workflow action. setting the staus based on
	 * applicationStatus updated by workflow and generting the ndc number
	 * 
	 * @param ndcRequest
	 * @param businessServiceValue
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void postStatusEnrichment(NdcApplicationRequest ndcRequest, String businessServiceValue) {
		List<Application> ndc = ndcRequest.getApplications();
		BusinessService businessService = workflowService.getBusinessService(ndcRequest, ndcRequest.getRequestInfo(),
				businessServiceValue);
		ndc.forEach(application -> {

					if (businessService != null) {

						State stateObj = workflowService.getCurrentState(application.getApplicationStatus(), businessService);
						String state = stateObj != null ? stateObj.getState() : StringUtils.EMPTY;

						if (state.equalsIgnoreCase(NDCConstants.APPROVED_STATE)
								|| state.equalsIgnoreCase(NDCConstants.AUTOAPPROVED_STATE)) {

							List<IdResponse> idResponses = idGenRepository
									.getId(ndcRequest.getRequestInfo(), application.getTenantId(), config.getApplicationNoIdgenName(), 1)
									.getIdResponses();
							application.setApplicationNo(idResponses.get(0).getId());
						}
						if (state.equalsIgnoreCase("CANCEL")) {
							application.setActive(false);
						}
					}
				});

	}


	public void enrichApplicationCriteriaWithOwnerids(NdcApplicationSearchCriteria criteria, List<Application> applications){
		Set<String> ownerids = new HashSet<>();
		for(Application application : applications){
			if(application.getOwners()!=null){
			for(OwnerInfo ownerInfo : application.getOwners()){
				ownerids.add(ownerInfo.getUuid());
			}
			}
		}
		criteria.setOwnerIds(ownerids);
	}

	public void enrichOwner(UserResponse userDetailResponse, List<Application> applications){
		List<OwnerInfo> users = userDetailResponse.getUser();
		Map<String,OwnerInfo> userIdToOwnerMap = new HashMap<>();
		users.forEach(user -> userIdToOwnerMap.put(user.getUuid(),user));
		applications.forEach(application -> {
			List<OwnerInfo> enrichedOwners = new ArrayList<>();

			if(application.getOwners()!=null) {
				for (OwnerInfo owner : application.getOwners()) {
					OwnerInfo userDetail = userIdToOwnerMap.get(owner.getUuid());
					if(userDetail!=null) {
						owner.addUserDetail(userDetail);
						enrichedOwners.add(userDetail);
					}
				}
			}

			application.setOwners(enrichedOwners);



		});
	}

	public void enrichProcessInstance(List<Application> applications, SearchCriteria criteria,
									  RequestInfo requestInfo) {
		if (CollectionUtils.isEmpty(applications))
			return;

		PlainAccessRequest apiPlainAccessRequest = requestInfo.getPlainAccessRequest();

		Map<String, ProcessInstance> processInstances = null;
		Set<String> applicationNumbers = applications.stream().map(Application::getApplicationNo).collect(Collectors.toSet());

		if (criteria.getTenantId() != null)
			processInstances = workflowService.getProcessInstances(requestInfo, applicationNumbers,
					criteria.getTenantId(), null);
		else
			processInstances = workflowService.getProcessInstances(requestInfo, applicationNumbers,
					requestInfo.getUserInfo().getTenantId(), null);
		for (Application application : applications) {
			if (!org.apache.commons.lang3.ObjectUtils.isEmpty(processInstances.get(application.getApplicationNo()))) {
				ProcessInstance processInstance = processInstances.get(application.getApplicationNo());
				application.setProcessInstance(new ProcessInstance());
				application.getProcessInstance().setBusinessService(processInstance.getBusinessService());
				application.getProcessInstance().setModuleName(processInstance.getModuleName());
				application.getProcessInstance().setComment(processInstance.getComment());
				application.getProcessInstance().setDocuments(processInstance.getDocuments());
				application.getProcessInstance().setAssignes(processInstance.getAssignes());
				application.getProcessInstance().setAction(processInstance.getAction());
				application.getProcessInstance().setState(processInstance.getState());
				application.getProcessInstance().setId(processInstance.getId());
				application.getProcessInstance().setTenantId(processInstance.getTenantId());
				if (!org.apache.commons.lang3.ObjectUtils.isEmpty(processInstance.getAssignes()))
					application.getProcessInstance().setAssignes(processInstance.getAssignes());
				Workflow workflow = new Workflow();
				workflow.setAction(processInstance.getAction());
				workflow.setComment(processInstance.getComment());
				workflow.setDocuments(processInstance.getDocuments());
				application.setWorkflow(workflow);
			}
		}
		requestInfo.setPlainAccessRequest(apiPlainAccessRequest);
	}

}