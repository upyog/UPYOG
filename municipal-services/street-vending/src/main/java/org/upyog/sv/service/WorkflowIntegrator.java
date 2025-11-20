package org.upyog.sv.service;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class WorkflowIntegrator {

	/*
	 * private static final String TENANTIDKEY = "tenantId";
	 * 
	 * private static final String BUSINESSSERVICEKEY = "businessService";
	 * 
	 * private static final String ACTIONKEY = "action";
	 * 
	 * private static final String COMMENTKEY = "comment";
	 * 
	 * private static final String MODULENAMEKEY = "moduleName";
	 * 
	 * private static final String BUSINESSIDKEY = "businessId";
	 * 
	 * private static final String DOCUMENTSKEY = "documents";
	 * 
	 * private static final String ASSIGNEEKEY = "assignes";
	 * 
	 * private static final String MODULENAMEVALUE = "chb";
	 * 
	 * private static final String UUIDKEY = "uuid";
	 * 
	 * private static final String WORKFLOWREQUESTARRAYKEY = "ProcessInstances";
	 * 
	 * private static final String REQUESTINFOKEY = "RequestInfo";
	 * 
	 * private static final String PROCESSINSTANCESJOSNKEY = "$.ProcessInstances";
	 * 
	 * private static final String BUSINESSIDJOSNKEY = "$.businessId";
	 * 
	 * private static final String STATUSJSONKEY = "$.state.applicationStatus";
	 * 
	 * private RestTemplate rest;
	 * 
	 * private StreetVendingConfiguration config;
	 * 
	 * @Autowired public WorkflowIntegrator(RestTemplate rest,
	 * StreetVendingConfiguration config) { this.rest = rest; this.config = config;
	 * }
	 * 
	 * public void callWorkFlow(StreetVendingRequest vendingRequest) { String
	 * wfTenantId = vendingRequest.getVendingDetails().getTenantId(); JSONArray
	 * array = new JSONArray(); StreetVendingDetail vendingDetail =
	 * vendingRequest.getVendingDetails(); JSONObject obj = new JSONObject();
	 * obj.put(BUSINESSIDKEY, vendingDetail.getApplicationNo());
	 * obj.put(TENANTIDKEY, wfTenantId); obj.put(BUSINESSSERVICEKEY,
	 * vendingDetail.getWorkflow().getBusinessService()); obj.put(MODULENAMEKEY,
	 * MODULENAMEVALUE); obj.put(ACTIONKEY,
	 * vendingDetail.getWorkflow().getAction()); obj.put(COMMENTKEY,
	 * vendingDetail.getWorkflow().getComment());
	 * 
	 * if (!CollectionUtils.isEmpty(bookingDetail.getWorkflow().getAssignes())) {
	 * List<Map<String, String>> uuidmaps = new LinkedList<>();
	 * bookingDetail.getWorkflow().getAssignes().forEach(assignee -> { Map<String,
	 * String> uuidMap = new HashMap<>(); uuidMap.put(UUIDKEY, assignee.getUuid());
	 * uuidmaps.add(uuidMap); }); obj.put(ASSIGNEEKEY, uuidmaps); }
	 * 
	 * obj.put(DOCUMENTSKEY, bookingDetail.getWorkflow().getDocuments());
	 * array.add(obj); JSONObject workFlowRequest = new JSONObject();
	 * workFlowRequest.put(REQUESTINFOKEY, bookingRequest.getRequestInfo());
	 * workFlowRequest.put(WORKFLOWREQUESTARRAYKEY, array); String response = null;
	 * try { response =
	 * rest.postForObject(config.getWfHost().concat(config.getWfTransitionPath()),
	 * workFlowRequest, String.class); } catch (HttpClientErrorException e) {
	 * 
	 * 
	 * extracting message from client error exception
	 * 
	 * DocumentContext responseContext =
	 * JsonPath.parse(e.getResponseBodyAsString()); List<Object> errros = null; try
	 * { errros = responseContext.read("$.Errors"); } catch (PathNotFoundException
	 * pnfe) { log.error(LocalizationConstants.CHB_WORKFLOW_ERROR,
	 * " Unable to read the json path in error object : " + pnfe.getMessage());
	 * throw new CustomException(LocalizationConstants.CHB_WORKFLOW_ERROR,
	 * " Unable to read the json path in error object : " + pnfe.getMessage()); }
	 * throw new CustomException(LocalizationConstants.CHB_WORKFLOW_ERROR,
	 * errros.toString()); } catch (Exception e) { throw new
	 * CustomException(LocalizationConstants.CHB_WORKFLOW_ERROR,
	 * " Exception occured while integrating with workflow : " + e.getMessage()); }
	 * 
	 * 
	 * on success result from work-flow read the data and set the status back to CHB
	 * object
	 * 
	 * DocumentContext responseContext = JsonPath.parse(response); List<Map<String,
	 * Object>> responseArray = responseContext.read(PROCESSINSTANCESJOSNKEY);
	 * Map<String, String> idStatusMap = new HashMap<>();
	 * responseArray.forEach(object -> {
	 * 
	 * DocumentContext instanceContext = JsonPath.parse(object);
	 * idStatusMap.put(instanceContext.read(BUSINESSIDJOSNKEY),
	 * instanceContext.read(STATUSJSONKEY)); }); // setting the status back to CHB
	 * object from wf response
	 * bookingDetail.setBookingStatus(idStatusMap.get(bookingDetail.getBookingNo()))
	 * ;
	 * 
	 * }
	 */
}
