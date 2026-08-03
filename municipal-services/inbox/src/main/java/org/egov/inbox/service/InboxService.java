package org.egov.inbox.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.inbox.config.InboxConfiguration;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.egov.inbox.service.handler.*;
import org.egov.inbox.util.ErrorConstants;
import org.egov.inbox.web.model.*;
import org.egov.inbox.web.model.workflow.*;
import org.egov.tracer.model.CustomException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * InboxService is responsible for fetching
 * and assembling inbox response data.
 */
@Slf4j
@Service
public class InboxService {

    private final InboxConfiguration config;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ObjectMapper mapper;

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ModuleHandlerRegistry registry;
    @Autowired
    private InboxAssembler inboxAssembler;

    /**
     * Initializes InboxService dependencies.
     *
     * @param config inbox configuration
     * @param serviceRequestRepository service request repository
     * @param mapper object mapper
     */
    @Autowired
    public InboxService(InboxConfiguration config,
            ServiceRequestRepository serviceRequestRepository,
            ObjectMapper mapper) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
        this.mapper = mapper;
        this.mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    /**
     * Fetches inbox response data.
     *
     * @param criteria inbox search criteria
     * @param requestInfo request info
     * @return inbox response
     */
    public InboxResponse fetchInboxData(InboxSearchCriteria criteria, RequestInfo requestInfo) {

        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        processCriteria.setTenantId(criteria.getTenantId());

        processCriteria.setModuleName(registry.getModuleName(processCriteria.getModuleName()));

        if(StringUtils.isEmpty(processCriteria.getModuleName()))
            throw new CustomException(ErrorConstants.MODULE_NAME_MISSING, "Module name is missing in the request");

        String moduleName = processCriteria.getModuleName();
        log.info("fetchInboxData moduleName: {}", moduleName);

        Integer totalCount = 0;
        if (registry.isWorkflowTotalCountRequired(moduleName))
            totalCount = workflowService.getProcessCount(criteria.getTenantId(), requestInfo, processCriteria);

        Integer nearingSlaCount = 0;
        if (registry.isWorkflowNearingSlaCountRequired(moduleName))
            nearingSlaCount = workflowService.getNearingSlaProcessCount(
                    criteria.getTenantId(), requestInfo, processCriteria);

        List<String> inputStatuses = CollectionUtils.isEmpty(processCriteria.getStatus())
                ? new ArrayList<>()
                : new ArrayList<>(processCriteria.getStatus());

        StringBuilder assigneeUuid = new StringBuilder();
        if (!ObjectUtils.isEmpty(processCriteria.getAssignee())) {
            assigneeUuid.append(processCriteria.getAssignee());
            processCriteria.setStatus(null);
        }

        processCriteria.setAssignee(null);
        processCriteria.setStatus(null);

        List<String> roles = requestInfo.getUserInfo().getRoles()
                .stream().map(Role::getCode).collect(Collectors.toList());

        List<HashMap<String, Object>> statusCountMap = workflowService.getProcessStatusCount(requestInfo,
                processCriteria);

        processCriteria.setModuleName(moduleName);
        processCriteria.setStatus(inputStatuses);
        processCriteria.setAssignee(assigneeUuid.toString());

        List<String> businessServiceName = processCriteria.getBusinessService();
        if (CollectionUtils.isEmpty(businessServiceName))
            throw new CustomException(ErrorConstants.MODULE_SEARCH_INVLAID, "Business Service is mandatory");

        Map<String, String> srvMap = fetchAppropriateServiceMapPublic(businessServiceName, moduleName);
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();

        if (moduleSearchCriteria == null) {
            moduleSearchCriteria = new HashMap<>();
            criteria.setModuleSearchCriteria(moduleSearchCriteria);
        }

        Map<String, Long> businessServiceSlaMap = new HashMap<>();
        List<Inbox> inboxes = new ArrayList<>();
        InboxResponse response = new InboxResponse();

        if (CollectionUtils.isEmpty(moduleSearchCriteria)) {

            processCriteria.setOffset(criteria.getOffset());
            processCriteria.setLimit(criteria.getLimit());

            ProcessInstanceResponse piResp = workflowService.getProcessInstance(processCriteria, requestInfo);
            Map<String, ProcessInstance> piMap = piResp.getProcessInstances().stream()
                    .collect(Collectors.toMap(ProcessInstance::getBusinessId, Function.identity()));

            moduleSearchCriteria.put(srvMap.get("applNosParam"),
                    StringUtils.arrayToDelimitedString(piMap.keySet().toArray(), ","));
            moduleSearchCriteria.put("tenantId", criteria.getTenantId());
            moduleSearchCriteria.put("limit", -1);

            JSONArray bObjs = fetchModuleObjectsPublic(moduleSearchCriteria, businessServiceName,
                    criteria.getTenantId(), requestInfo, srvMap);

            String bizIdParam = srvMap.get("businessIdProperty");
            Map<String, Object> bizMap = StreamSupport.stream(bObjs.spliterator(), false)
                    .collect(Collectors.toMap(s -> ((JSONObject) s).get(bizIdParam).toString(), s -> s));

            if (bObjs.length() > 0 && !piMap.isEmpty()) {
                for (String k : piMap.keySet()) {
                    Inbox inbox = new Inbox();
                    inbox.setProcessInstance(piMap.get(k));
                    inbox.setBusinessObject(toMap((JSONObject) bizMap.get(k)));
                    inboxes.add(inbox);
                }
            }

        } else {

            moduleSearchCriteria.put("tenantId", criteria.getTenantId());
            moduleSearchCriteria.put("offset", criteria.getOffset());
            moduleSearchCriteria.put("limit", criteria.getLimit());

            List<BusinessService> bServices = new ArrayList<>();
            for (String bs : businessServiceName) {

                /**
                 * Fetch business service configuration
                 * and prepare SLA mapping.
                 */
                BusinessService bSrv = workflowService.getBusinessService(criteria.getTenantId(), requestInfo, bs);
                bServices.add(bSrv);
                businessServiceSlaMap.put(bSrv.getBusinessService(), bSrv.getBusinessServiceSla());
            }

            /**
             * Fetch workflow actionable statuses
             * for current logged-in user roles.
             */
            HashMap<String, String> statusIdNameMap = workflowService.getActionableStatusesForRole(requestInfo,
                    bServices, processCriteria);

            String appStatusParam = srvMap.getOrDefault("applsStatusParam", "applicationStatus");

            /**
             * Populate application statuses
             * into module search criteria.
             */
            if (!statusIdNameMap.isEmpty()) {
                if (!CollectionUtils.isEmpty(processCriteria.getStatus())) {
                    List<String> statuses = processCriteria.getStatus().stream()
                            .map(statusIdNameMap::get).collect(Collectors.toList());
                    moduleSearchCriteria.put(appStatusParam,
                            StringUtils.arrayToDelimitedString(statuses.toArray(), ","));
                } else {
                    moduleSearchCriteria.put(appStatusParam,
                            StringUtils.arrayToDelimitedString(statusIdNameMap.values().toArray(), ","));
                }
            }

            /**
             * Prepare inbox context object
             * shared across handlers.
             */
            InboxContext ctx = InboxContext.builder()
                    .criteria(criteria)
                    .requestInfo(requestInfo)
                    .statusIdNameMap(statusIdNameMap)
                    .srvMap(srvMap)
                    .totalCount(totalCount)
                    .build();

            Map<String, List<String>> tenantAndApplnNumbersMap = new HashMap<>();

            /**
             * Execute module-specific handler logic.
             */
            if (!ObjectUtils.isEmpty(moduleName) && registry.hasHandler(moduleName)) {
                ModuleInboxHandler handler = registry.getHandler(moduleName).get();

                statusCountMap = handler.enrichStatusCountPreFetch(
                        ctx, statusCountMap, tenantAndApplnNumbersMap, roles, inputStatuses);

                int handlerCount = handler.fetchCount(ctx);
                if (handlerCount >= 0)
                    totalCount = handlerCount;

                handler.fetchApplicationIds(ctx);

                handler.paramsToRemove().forEach(ctx::removeModuleSearchCriteria);
            }

            /**
             * Assemble final inbox response.
             */
            inboxes = inboxAssembler.assemble(ctx, processCriteria, businessServiceName,
                    srvMap, businessServiceSlaMap, tenantAndApplnNumbersMap, roles, requestInfo, workflowService);

            if (ctx.getTotalCount() != null)
                totalCount = ctx.getTotalCount();

            /**
             * Execute post assembly enrichment.
             */
            if (!ObjectUtils.isEmpty(moduleName) && registry.hasHandler(moduleName)) {
                ModuleInboxHandler.PostAssembleResult result = registry.getHandler(moduleName).get()
                        .enrichStatusCountPostAssemble(ctx, statusCountMap, inputStatuses, inboxes, totalCount);
                statusCountMap = result.statusCountMap;
                totalCount = result.totalCount;
            }
        }

        log.info("statusCountMap size: {}", statusCountMap.size());

        response.setTotalCount(totalCount);
        response.setNearingSlaCount(nearingSlaCount);
        response.setStatusMap(statusCountMap);
        response.setItems(inboxes);

        return response;
    }

    /**
     * Fetches appropriate service mapping
     * for the provided business service.
     */
    public Map<String, String> fetchAppropriateServiceMapPublic(
            List<String> businessServiceName, String moduleName) {

        StringBuilder appropriateKey = new StringBuilder();
        for (String key : config.getServiceSearchMapping().keySet()) {
            if (key.contains(businessServiceName.get(0))) {
                appropriateKey.append(key);
                break;
            }
        }

        if (ObjectUtils.isEmpty(appropriateKey))
            throw new CustomException("EG_INBOX_SEARCH_ERROR",
                    "Inbox service is not configured for the provided business services");

        for (String inputBusinessService : businessServiceName) {
            if (!org.egov.inbox.util.FSMConstants.FSM_MODULE.equalsIgnoreCase(moduleName)) {
                if (!appropriateKey.toString().contains(inputBusinessService))
                    throw new CustomException("EG_INBOX_SEARCH_ERROR", "Cross module search is NOT allowed.");
            }
        }

        return config.getServiceSearchMapping().get(appropriateKey.toString());
    }

    /**
     * Fetches module business objects.
     */
    public JSONArray fetchModuleObjectsPublic(
            HashMap<String, Object> moduleSearchCriteria,
            List<String> businessServiceName,
            String tenantId,
            RequestInfo requestInfo,
            Map<String, String> srvMap) {
        return fetchModuleObjects(moduleSearchCriteria, businessServiceName, tenantId, requestInfo, srvMap);
    }

    /**
     * Fetches module search objects.
     */
    public JSONArray fetchModuleSearchObjectsPublic(
            HashMap<String, Object> moduleSearchCriteria,
            List<String> businessServiceName,
            String tenantId,
            RequestInfo requestInfo,
            Map<String, String> srvSearchMap) {
        return fetchModuleSearchObjects(moduleSearchCriteria, businessServiceName, tenantId, requestInfo, srvSearchMap);
    }

    /**
     * Converts JSONObject into Map.
     */
    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        if (object == null)
            return map;

        Iterator<String> keysItr = object.keys();

        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray)
                value = toList((JSONArray) value);
            else if (value instanceof JSONObject)
                value = toMap((JSONObject) value);

            map.put(key, value);
        }

        return map;
    }

    /**
     * Converts JSONArray into List.
     */
    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<>();

        for (int i = 0; i < array.length(); i++) {

            Object value = array.get(i);

            if (value instanceof JSONArray)
                value = toList((JSONArray) value);
            else if (value instanceof JSONObject)
                value = toMap((JSONObject) value);

            list.add(value);
        }

        return list;
    }

    /**
     * Fetches business objects
     * from configured search API.
     */
    private JSONArray fetchModuleObjects(
            HashMap<String, Object> moduleSearchCriteria,
            List<String> businessServiceName,
            String tenantId,
            RequestInfo requestInfo,
            Map<String, String> srvMap) {

        if (CollectionUtils.isEmpty(srvMap) || StringUtils.isEmpty(srvMap.get("searchPath")))
            throw new CustomException(ErrorConstants.INVALID_MODULE_SEARCH_PATH,
                    "search path not configured for the businessService : " + businessServiceName);

        StringBuilder url = new StringBuilder(srvMap.get("searchPath")).append("?tenantId=").append(tenantId);

        moduleSearchCriteria.keySet().forEach(param -> {
            if (param.equalsIgnoreCase("tenantId"))
                return;

            if (moduleSearchCriteria.get(param) instanceof Collection) {
                url.append("&").append(param).append("=");
                url.append(StringUtils.arrayToDelimitedString(
                        ((Collection<?>) moduleSearchCriteria.get(param)).toArray(), ","));

            } else if (param.equalsIgnoreCase("appStatus")) {

                url.append("&applicationStatus=").append(moduleSearchCriteria.get(param));

            } else if (param.equalsIgnoreCase("consumerNo")) {

                url.append("&connectionNumber=").append(moduleSearchCriteria.get(param));

            } else if (moduleSearchCriteria.get(param) != null) {

                url.append("&").append(param).append("=").append(moduleSearchCriteria.get(param));
            }
        });

        log.info("\nfetchModuleObjects URL :::: {}", url);

        Object result = serviceRequestRepository.fetchResult(url,
                RequestInfoWrapper.builder().requestInfo(requestInfo).build());

        LinkedHashMap responseMap;

        try {
            responseMap = mapper.convertValue(result, LinkedHashMap.class);

        } catch (IllegalArgumentException e) {

            throw new CustomException(ErrorConstants.PARSING_ERROR,
                    "Failed to parse response of ProcessInstance Count");
        }

        try {

            return new JSONObject(responseMap).getJSONArray(srvMap.get("dataRoot"));

        } catch (Exception e) {

            throw new CustomException(ErrorConstants.INVALID_MODULE_DATA,
                    "search api could not find data in dataroot " + srvMap.get("dataRoot"));
        }
    }

    /**
     * Fetches module search objects
     * using configured search API.
     */
    private JSONArray fetchModuleSearchObjects(
            HashMap<String, Object> moduleSearchCriteria,
            List<String> businessServiceName,
            String tenantId,
            RequestInfo requestInfo,
            Map<String, String> srvMap) {

        if (CollectionUtils.isEmpty(srvMap) || StringUtils.isEmpty(srvMap.get("searchPath")))
            throw new CustomException(ErrorConstants.INVALID_MODULE_SEARCH_PATH,
                    "search path not configured for the businessService : " + businessServiceName);

        StringBuilder url = new StringBuilder(srvMap.get("searchPath")).append("?tenantId=").append(tenantId);

        moduleSearchCriteria.keySet().forEach(param -> {

            if (param.equalsIgnoreCase("tenantId") || param.equalsIgnoreCase("limit"))
                return;

            if (moduleSearchCriteria.get(param) instanceof Collection) {

                url.append("&").append(param).append("=");

                url.append(StringUtils.arrayToDelimitedString(
                        ((Collection<?>) moduleSearchCriteria.get(param)).toArray(), ","));

            } else if (moduleSearchCriteria.get(param) != null) {

                url.append("&").append(param).append("=").append(moduleSearchCriteria.get(param));
            }
        });

        log.info("\nfetchModuleSearchObjects URL :::: {}", url);

        Object result = serviceRequestRepository.fetchResult(url,
                RequestInfoWrapper.builder().requestInfo(requestInfo).build());

        LinkedHashMap responseMap;

        try {

            responseMap = mapper.convertValue(result, LinkedHashMap.class);

        } catch (IllegalArgumentException e) {

            throw new CustomException(ErrorConstants.PARSING_ERROR,
                    "Failed to parse response of ProcessInstance Count");
        }

        try {

            return new JSONObject(responseMap).getJSONArray(srvMap.get("dataRoot"));

        } catch (Exception e) {

            throw new CustomException(ErrorConstants.INVALID_MODULE_DATA,
                    "search api could not find data in serviceMap " + srvMap.get("dataRoot"));
        }
    }
}
