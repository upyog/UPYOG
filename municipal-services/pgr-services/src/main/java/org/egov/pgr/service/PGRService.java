package org.egov.pgr.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pgr.config.PGRConfiguration;
import org.egov.pgr.producer.Producer;
import org.egov.pgr.repository.PGRRepository;
import org.egov.pgr.repository.rowmapper.PGRQueryBuilder;
import org.egov.pgr.util.MDMSUtils;
import org.egov.pgr.util.PGRConstants;
import org.egov.pgr.validator.ServiceRequestValidator;
import org.egov.pgr.web.models.CountStatusRequest;
import org.egov.pgr.web.models.CountStatusResponse;
import org.egov.pgr.web.models.CountStatusUpdate;
import org.egov.pgr.web.models.PGRNotification;
import org.egov.pgr.web.models.PGRNotificationRequest;
import org.egov.pgr.web.models.PgrNotificationSearchCriteria;
import org.egov.pgr.web.models.RequestSearchCriteria;
import org.egov.pgr.web.models.Service;
import org.egov.pgr.web.models.ServiceRequest;
import org.egov.pgr.web.models.ServiceResponse;
import org.egov.pgr.web.models.ServiceStatusUpdateRequest;
import org.egov.pgr.web.models.ServiceWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@org.springframework.stereotype.Service
public class PGRService {



    private EnrichmentService enrichmentService;

    private UserService userService;

    private WorkflowService workflowService;

    private ServiceRequestValidator serviceRequestValidator;

    private ServiceRequestValidator validator;

    private Producer producer;

    private PGRConfiguration config;

    private PGRRepository repository;

    private MDMSUtils mdmsUtils;


    @Autowired
    public PGRService(EnrichmentService enrichmentService, UserService userService, WorkflowService workflowService,
                      ServiceRequestValidator serviceRequestValidator, ServiceRequestValidator validator, Producer producer,
                      PGRConfiguration config, PGRRepository repository, MDMSUtils mdmsUtils) {
        this.enrichmentService = enrichmentService;
        this.userService = userService;
        this.workflowService = workflowService;
        this.serviceRequestValidator = serviceRequestValidator;
        this.validator = validator;
        this.producer = producer;
        this.config = config;
        this.repository = repository;
        this.mdmsUtils = mdmsUtils;
    }


    /**
     * Creates a complaint in the system
     * @param request The service request containg the complaint information
     * @return
     */
    public ServiceRequest create(ServiceRequest request){
        Object mdmsData = mdmsUtils.mDMSCall(request);
        validator.validateCreate(request, mdmsData);
        enrichmentService.enrichCreateRequest(request);
        workflowService.updateWorkflowStatus(request);
        producer.push(config.getCreateTopic(),request);
        return request;
    }


    /**
     * Searches the complaints in the system based on the given criteria
     * @param requestInfo The requestInfo of the search call
     * @param criteria The search criteria containg the params on which to search
     * @return
     */
    public List<ServiceWrapper> search(RequestInfo requestInfo, RequestSearchCriteria criteria){
        validator.validateSearch(requestInfo, criteria);

        enrichmentService.enrichSearchRequest(requestInfo, criteria);

        if(criteria.isEmpty())
            return new ArrayList<>();

        if(criteria.getMobileNumber()!=null && CollectionUtils.isEmpty(criteria.getUserIds()))
            return new ArrayList<>();

        criteria.setIsPlainSearch(false);

        List<ServiceWrapper> serviceWrappers = repository.getServiceWrappers(criteria);

        if(CollectionUtils.isEmpty(serviceWrappers))
            return new ArrayList<>();;

        userService.enrichUsers(serviceWrappers,requestInfo,criteria.getTenantId());
        List<ServiceWrapper> enrichedServiceWrappers = workflowService.enrichWorkflow(requestInfo,serviceWrappers);
        Map<Long, List<ServiceWrapper>> sortedWrappers = new TreeMap<>(Collections.reverseOrder());
        for(ServiceWrapper svc : enrichedServiceWrappers){
            if(sortedWrappers.containsKey(svc.getService().getAuditDetails().getCreatedTime())){
                sortedWrappers.get(svc.getService().getAuditDetails().getCreatedTime()).add(svc);
            }else{
                List<ServiceWrapper> serviceWrapperList = new ArrayList<>();
                serviceWrapperList.add(svc);
                sortedWrappers.put(svc.getService().getAuditDetails().getCreatedTime(), serviceWrapperList);
            }
        }
        List<ServiceWrapper> sortedServiceWrappers = new ArrayList<>();
        for(Long createdTimeDesc : sortedWrappers.keySet()){
            sortedServiceWrappers.addAll(sortedWrappers.get(createdTimeDesc));
        }
        return sortedServiceWrappers;
    }


    /**
     * Updates the complaint (used to forward the complaint from one application status to another)
     * @param request The request containing the complaint to be updated
     * @return
     */
    public ServiceRequest update(ServiceRequest request){
        Object mdmsData = mdmsUtils.mDMSCall(request);
        validator.validateUpdate(request, mdmsData);
        enrichmentService.enrichUpdateRequest(request);
        workflowService.updateWorkflowStatus(request);
        producer.push(config.getUpdateTopic(),request);
        return request;
    }

    /**
     * Returns the total number of comaplaints matching the given criteria
     * @param requestInfo The requestInfo of the search call
     * @param criteria The search criteria containg the params for which count is required
     * @return
     */
    public Integer count(RequestInfo requestInfo, RequestSearchCriteria criteria){
        criteria.setIsPlainSearch(false);
        Integer count = repository.getCount(criteria);
        return count;
    }


    public List<ServiceWrapper> plainSearch(RequestInfo requestInfo, RequestSearchCriteria criteria) {
        validator.validatePlainSearch(criteria);

        criteria.setIsPlainSearch(true);

        if(criteria.getLimit()==null)
            criteria.setLimit(config.getDefaultLimit());

        if(criteria.getOffset()==null)
            criteria.setOffset(config.getDefaultOffset());

        if(criteria.getLimit()!=null && criteria.getLimit() > config.getMaxLimit())
            criteria.setLimit(config.getMaxLimit());

        List<ServiceWrapper> serviceWrappers = repository.getServiceWrappers(criteria);

        if(CollectionUtils.isEmpty(serviceWrappers)){
            return new ArrayList<>();
        }

        userService.enrichUsers(serviceWrappers,requestInfo,criteria.getTenantId());
        List<ServiceWrapper> enrichedServiceWrappers = workflowService.enrichWorkflow(requestInfo, serviceWrappers);

        Map<Long, List<ServiceWrapper>> sortedWrappers = new TreeMap<>(Collections.reverseOrder());
        for(ServiceWrapper svc : enrichedServiceWrappers){
            if(sortedWrappers.containsKey(svc.getService().getAuditDetails().getCreatedTime())){
                sortedWrappers.get(svc.getService().getAuditDetails().getCreatedTime()).add(svc);
            }else{
                List<ServiceWrapper> serviceWrapperList = new ArrayList<>();
                serviceWrapperList.add(svc);
                sortedWrappers.put(svc.getService().getAuditDetails().getCreatedTime(), serviceWrapperList);
            }
        }
        List<ServiceWrapper> sortedServiceWrappers = new ArrayList<>();
        for(Long createdTimeDesc : sortedWrappers.keySet()){
            sortedServiceWrappers.addAll(sortedWrappers.get(createdTimeDesc));
        }
        return sortedServiceWrappers;
    }


	public Map<String, Integer> getDynamicData(String tenantId) {
		
		Map<String,Integer> dynamicData = repository.fetchDynamicData(tenantId);

		return dynamicData;
	}


	public int getComplaintTypes() {
		
		return Integer.valueOf(config.getComplaintTypes());
	}


	public @Valid CountStatusRequest getStatusCount(@Valid CountStatusRequest request) {
	
		List<CountStatusUpdate> count = null;
		CountStatusResponse response = null;
		try {
			
			 count = repository.countSearch(request);
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		request.setCountStatusUpdate(count);
		return request;
	}
	
	public ServiceRequest updateStatus(ServiceStatusUpdateRequest request) {

		RequestSearchCriteria requestSearchCriteria = RequestSearchCriteria.builder()
				.serviceRequestId(request.getServiceRequestId()).tenantId(request.getTenantId()).build();

		List<ServiceWrapper> serviceWrappers = search(request.getRequestInfo(), requestSearchCriteria);

		if (null == serviceWrappers || CollectionUtils.isEmpty(serviceWrappers)
				|| null == serviceWrappers.get(0).getService()) {
			throw new CustomException("SERVICE NOT FOUND", "No service found with given service request id.");
		}

		Service service = serviceWrappers.get(0).getService();
		service.setApplicationStatus(request.getApplicationStatus());
//		if(request.getApplicationStatus().equals("RESOLVE")) {
		ObjectNode additionalDetailNode;
		ObjectMapper objectMapper = new ObjectMapper();
		if (service.getAdditionalDetail() == null) {
			additionalDetailNode = objectMapper.createObjectNode();
		} else {
			additionalDetailNode = objectMapper.convertValue(service.getAdditionalDetail(), ObjectNode.class);
		}
		additionalDetailNode.put("resolutionDate", request.getResolutionDate());
		service.setAdditionalDetail(additionalDetailNode);
//		}

		ServiceRequest serviceRequest = ServiceRequest.builder().service(service).requestInfo(request.getRequestInfo())
				.workflow(request.getWorkflow()).build();

		PGRNotificationRequest pgrNotificationRequest = enrichmentService
				.enrichNotificationCreateRequest(serviceRequest);

		producer.push(config.getCreateNotificationTopic(), pgrNotificationRequest);

		return update(serviceRequest);
	}


	public List<PGRNotification> searchPgrNotification(PgrNotificationSearchCriteria pgrNotificationSearchCriteria) {

		return repository.getPgrNotifications(pgrNotificationSearchCriteria);
	}


	public void deletePgrNotification(List<String> uuidList) {

		repository.deletePgrNotifications(uuidList);
	}

	public void setAllCount(List<ServiceWrapper> services, ServiceResponse response) {
	    if (!CollectionUtils.isEmpty(services)) {
	        int totalCount = 0; // Initialize total count

	        for (ServiceWrapper service : services) {
	            String status = service.getService().getApplicationStatus();
	            totalCount++; // Increment total count for each service

	            if (PGRConstants.PENDINGATLME.equals(status)) {
	                response.setApplicationPendingAtLME(response.getApplicationPendingAtLME() + 1);
	            } else if (PGRConstants.RESOLVED.equals(status)) {
	                response.setApplicationResolved(response.getApplicationResolved() + 1);
	            } else if (PGRConstants.PENDINGATLMHE.equals(status)) {
	                response.setApplicationPendingAtLMHE(response.getApplicationPendingAtLMHE() + 1);
	            } else if (PGRConstants.CLOSED_AFTER_RESOLUTION.equals(status)) {
	                response.setApplicationResolvedAfterResolution(response.getApplicationResolvedAfterResolution() + 1);
	            } else if (PGRConstants.CLOSED_AFTER_REJECTION.equals(status)) {
	                response.setApplicationRejected(response.getApplicationRejected() + 1);
	            }
	        }

	        // Set the total count in the response
	        response.setTotalCount(totalCount);
	    }
	}

	
}
