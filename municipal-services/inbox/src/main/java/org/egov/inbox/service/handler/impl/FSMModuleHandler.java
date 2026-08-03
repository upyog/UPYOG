package org.egov.inbox.service.handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.config.InboxConfiguration;
import org.egov.inbox.model.vehicle.*;
import org.egov.inbox.repository.ServiceRequestRepository;
import org.egov.inbox.service.FSMInboxFilterService;
import org.egov.inbox.service.WorkflowService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.egov.inbox.service.InboxService;
import org.egov.inbox.util.ErrorConstants;
import org.egov.inbox.util.FSMConstants;
import org.egov.inbox.web.model.*;
import org.egov.inbox.web.model.workflow.BusinessService;
import org.egov.inbox.web.model.workflow.ProcessInstance;
import org.egov.inbox.web.model.workflow.ProcessInstanceResponse;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.egov.inbox.web.model.workflow.State;
import org.egov.tracer.model.CustomException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.egov.inbox.util.FSMConstants.*;
/**
 * FSMModuleHandler is responsible for handling
 * inbox operations specific to FSM module.
 */
@Slf4j
@Service
public class FSMModuleHandler implements ModuleInboxHandler {

    @Autowired
    private FSMInboxFilterService fsmService;
    @Autowired
    private InboxConfiguration config;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ServiceRequestRepository serviceRequestRepository;
    @Autowired
    private ObjectMapper mapper;

    @Lazy
    @Autowired
    private InboxService inboxService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is FSM
     */
    @Override
    public boolean supports(String moduleName) {
        return FSM_MODULE.equals(moduleName);
    }

    /**
     * Fetches FSM application count
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        String dsoId = getDsoId(ctx);

        Integer count = fsmService.fetchApplicationCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo(), dsoId);
        ctx.setTotalCount(count);
    }

    /**
     * Fetches FSM application count from searcher.
     *
     * @param ctx inbox context
     * @return total application count
     */
    @Override
    public int fetchCount(InboxContext ctx) {
        String dsoId = getDsoId(ctx);
        return fsmService.fetchApplicationCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo(), dsoId);
    }

    /**
     * Returns FSM application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return FSMConstants.APPLICATION_NUMBER_PARAM;
    }

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(LOCALITY_PARAM, OFFSET_PARAM);
    }

    /**
     * Enriches FSM status count and total count
     * after inbox assembly.
     *
     * @param ctx inbox context
     * @param statusCountMap status count details
     * @param inputStatuses requested statuses
     * @param inboxes inbox response
     * @param totalCount current total count
     * @return enriched post assemble result
     */
    @Override
    public PostAssembleResult enrichStatusCountPostAssemble(
            InboxContext ctx,
            List<HashMap<String, Object>> statusCountMap,
            List<String> inputStatuses,
            List<Inbox> inboxes,
            Integer totalCount) {

        List<HashMap<String, Object>> enriched = enrichStatusCount(ctx, statusCountMap, inputStatuses, inboxes);
        Integer updatedCount = getTotalCount(enriched, inputStatuses, totalCount);
        return new PostAssembleResult(enriched, updatedCount);
    }

    /**
     * Enriches FSM inbox status count
     * with vehicle trip details.
     *
     * @param ctx inbox context
     * @param statusCountMap status count details
     * @param inputStatuses requested statuses
     * @param inboxes inbox response
     * @return enriched status count map
     */
    public List<HashMap<String, Object>> enrichStatusCount(
            InboxContext ctx,
            List<HashMap<String, Object>> statusCountMap,
            List<String> inputStatuses,
            List<Inbox> inboxes) {

        InboxSearchCriteria criteria = ctx.getCriteria();
        ProcessInstanceSearchCriteria processCriteria = criteria.getProcessSearchCriteria();
        HashMap<String, Object> moduleSearchCriteria = criteria.getModuleSearchCriteria();

        List<String> appStatus = Arrays.asList(WAITING_FOR_DISPOSAL_STATE, DISPOSED_STATE);

        List<Map<String, Object>> vehicleResponse = fetchVehicleTripResponse(criteria, ctx.getRequestInfo(), appStatus);

        BusinessService businessService = workflowService.getBusinessService(
                criteria.getTenantId(), ctx.getRequestInfo(), FSM_VEHICLE_TRIP_MODULE);

        populateStatusCountMap(statusCountMap, vehicleResponse, businessService);

        List<String> requiredApps = inboxes.stream()
                .filter(inbox -> inbox.getProcessInstance() != null
                        && inbox.getProcessInstance().getState() != null)
                .filter(inbox -> {
                    String state = inbox.getProcessInstance().getState().getApplicationStatus();
                    return DSO_INPROGRESS_STATE.equals(state)
                            || CITIZEN_FEEDBACK_PENDING_STATE.equals(state)
                            || COMPLETED_STATE.equals(state);
                })
                .map(inbox -> inbox.getProcessInstance().getBusinessId())
                .collect(Collectors.toList());

        List<VehicleTripDetail> tripDetails = fetchVehicleStatusForApplication(requiredApps, ctx.getRequestInfo(),
                criteria.getTenantId());

        inboxes.forEach(inbox -> {
            if (inbox.getProcessInstance() != null
                    && inbox.getProcessInstance().getBusinessId() != null) {
                List<VehicleTripDetail> trips = tripDetails.stream()
                        .filter(t -> inbox.getProcessInstance().getBusinessId().equals(t.getReferenceNo()))
                        .collect(Collectors.toList());
                inbox.getBusinessObject().put(VEHICLE_LOG, trips);
            }
        });

        if (CollectionUtils.isEmpty(inboxes) && !moduleSearchCriteria.containsKey("applicationNos")) {

            List<String> fsmApplications = fetchVehicleStateMap(
                    inputStatuses.stream().filter(Objects::nonNull).collect(Collectors.toList()),
                    ctx.getRequestInfo(), criteria.getTenantId(),
                    criteria.getLimit(), criteria.getOffset());

            moduleSearchCriteria.put("applicationNos", fsmApplications);
            moduleSearchCriteria.put("applicationStatus", requiredApps);
            processCriteria.setBusinessIds(fsmApplications);
            processCriteria.setStatus(null);

            ProcessInstanceResponse processResponse = workflowService.getProcessInstance(processCriteria,
                    ctx.getRequestInfo());

            Map<String, ProcessInstance> processMap = processResponse.getProcessInstances().stream()
                    .collect(Collectors.toMap(ProcessInstance::getBusinessId, Function.identity()));

            JSONArray vehicleObjects = inboxService.fetchModuleObjectsPublic(
                    moduleSearchCriteria, processCriteria.getBusinessService(),
                    criteria.getTenantId(), ctx.getRequestInfo(), ctx.getSrvMap());

            String businessIdParam = ctx.getSrvMap().get("businessIdProperty");

            Map<String, Object> vehicleBusinessMap = StreamSupport.stream(vehicleObjects.spliterator(), false)
                    .collect(Collectors.toMap(
                            obj -> ((JSONObject) obj).get(businessIdParam).toString(),
                            obj -> obj, (e1, e2) -> e1, LinkedHashMap::new));

            if (vehicleObjects.length() > 0 && !processResponse.getProcessInstances().isEmpty()) {
                fsmApplications.forEach(key -> {
                    Inbox inbox = new Inbox();
                    inbox.setProcessInstance(processMap.get(key));
                    inbox.setBusinessObject(InboxService.toMap((JSONObject) vehicleBusinessMap.get(key)));
                    inboxes.add(inbox);
                });
            }
        }

        return aggregateFsmStatuses(statusCountMap);
    }

    /**
     * Calculates the total inbox count by including
     * additional FSM vehicle trip application counts.
     *
     * @param statusCountMap status wise count details
     * @param inputStatuses requested workflow statuses
     * @param current existing total count
     * @return updated total count
     */
    public Integer getTotalCount(
            List<HashMap<String, Object>> statusCountMap,
            List<String> inputStatuses,
            Integer current) {

        int extra = 0;
        for (HashMap<String, Object> map : statusCountMap) {
            if ((WAITING_FOR_DISPOSAL_STATE.equals(map.get(APPLICATIONSTATUS))
                    || DISPOSED_STATE.equals(map.get(APPLICATIONSTATUS)))
                    && inputStatuses != null
                    && inputStatuses.contains(map.get(STATUSID))) {
                extra += (int) map.get(COUNT);
            }
        }
        return current + extra;
    }

    /**
     * Fetches vehicle trip application status response.
     *
     * @param criteria inbox search criteria
     * @param requestInfo request info
     * @param applicationStatus application statuses
     * @return vehicle application status response
     */
    private List<Map<String, Object>> fetchVehicleTripResponse(
            InboxSearchCriteria criteria, RequestInfo requestInfo, List<String> applicationStatus) {
        VehicleSearchCriteria vsc = new VehicleSearchCriteria();
        vsc.setApplicationStatus(applicationStatus);
        vsc.setTenantId(criteria.getTenantId());
        VehicleCustomResponse resp = fetchVehicleApplicationCount(vsc, requestInfo);
        if (resp != null && resp.getApplicationStatusCount() != null)
            return resp.getApplicationStatusCount();
        return new ArrayList<>();
    }

    /**
     * Populates FSM status count map using vehicle response.
     *
     * @param statusCountMap status count map
     * @param vehicleResponse vehicle response
     * @param businessService workflow business service
     */
    private void populateStatusCountMap(
            List<HashMap<String, Object>> statusCountMap,
            List<Map<String, Object>> vehicleResponse,
            BusinessService businessService) {
        if (CollectionUtils.isEmpty(vehicleResponse) || businessService == null)
            return;
        for (State appState : businessService.getStates()) {
            vehicleResponse.forEach(trip -> {
                HashMap<String, Object> map = new HashMap<>();
                if (trip.get(APPLICATIONSTATUS).equals(appState.getApplicationStatus())) {
                    map.put(COUNT, trip.get(COUNT));
                    map.put(APPLICATIONSTATUS, appState.getApplicationStatus());
                    map.put(STATUSID, appState.getUuid());
                    map.put(FSMConstants.BUSINESS_SERVICE_PARAM, FSM_VEHICLE_TRIP_MODULE);
                }
                if (!map.isEmpty())
                    statusCountMap.add(map);
            });
        }
    }

    /**
     * Fetches vehicle trip details for applications.
     *
     * @param applicationNos application numbers
     * @param requestInfo request info
     * @param tenantId tenant id
     * @return vehicle trip details
     */
    private List<VehicleTripDetail> fetchVehicleStatusForApplication(
            List<String> applicationNos, RequestInfo requestInfo, String tenantId) {
        VehicleTripSearchCriteria criteria = new VehicleTripSearchCriteria();
        criteria.setApplicationNos(applicationNos);
        criteria.setTenantId(tenantId);
        return fetchVehicleTripDetailsByReferenceNo(criteria, requestInfo);
    }

    /**
     * Fetches vehicle trip details by reference number.
     *
     * @param criteria vehicle trip search criteria
     * @param requestInfo request info
     * @return vehicle trip details
     */
    private List<VehicleTripDetail> fetchVehicleTripDetailsByReferenceNo(
            VehicleTripSearchCriteria criteria, RequestInfo requestInfo) {
        StringBuilder url = new StringBuilder(config.getVehicleHost())
                .append(config.getVehicleSearchTripPath());
        Object result = serviceRequestRepository.fetchResult(url, criteria);
        try {
            VehicleTripDetailResponse resp = mapper.convertValue(result, VehicleTripDetailResponse.class);
            if (resp != null && resp.getVehicleTripDetail() != null)
                return resp.getVehicleTripDetail();
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorConstants.PARSING_ERROR, "Failed to parse VehicleTripDetailResponse");
        }
        return new ArrayList<>();
    }

    /**
     * Fetches FSM application ids from vehicle states.
     *
     * @param inputStatuses workflow statuses
     * @param requestInfo request info
     * @param tenantId tenant id
     * @param limit result limit
     * @param offset result offset
     * @return FSM application ids
     */
    private List<String> fetchVehicleStateMap(
            List<String> inputStatuses, RequestInfo requestInfo,
            String tenantId, Integer limit, Integer offset) {
        VehicleTripSearchCriteria criteria = new VehicleTripSearchCriteria();
        criteria.setApplicationStatus(inputStatuses);
        criteria.setTenantId(tenantId);
        criteria.setLimit(limit);
        criteria.setOffset(offset);
        StringBuilder url = new StringBuilder(config.getFsmHost()).append(config.getFetchApplicationIds());
        Object result = serviceRequestRepository.fetchResult(url, criteria);
        try {
            VehicleCustomResponse resp = mapper.convertValue(result, VehicleCustomResponse.class);
            if (resp != null && resp.getApplicationIdList() != null)
                return resp.getApplicationIdList();
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorConstants.PARSING_ERROR, "Failed to parse VehicleCustomResponse");
        }
        return new ArrayList<>();
    }

    /**
     * Fetches vehicle application status count.
     *
     * @param criteria vehicle search criteria
     * @param requestInfo request info
     * @return vehicle custom response
     */
    private VehicleCustomResponse fetchVehicleApplicationCount(
            VehicleSearchCriteria criteria, RequestInfo requestInfo) {
        StringBuilder url = new StringBuilder(config.getVehicleHost())
                .append(config.getVehicleApplicationStatusCountPath());
        Object result = serviceRequestRepository.fetchResult(url, criteria);
        try {
            return mapper.convertValue(result, VehicleCustomResponse.class);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorConstants.PARSING_ERROR, "Failed to parse VehicleCustomResponse");
        }
    }

    /**
     * Fetches DSO id for logged-in FSM DSO user.
     *
     * @param ctx inbox context
     * @return DSO id if user is FSM DSO else null
     */
    private String getDsoId(InboxContext ctx) {

        if (CollectionUtils.isEmpty(
                ctx.getRequestInfo().getUserInfo().getRoles())) {
            return null;
        }

        if (!ctx.getRequestInfo().getUserInfo().getRoles().get(0).getCode()
                .equals(FSMConstants.FSM_DSO)) {
            return null;
        }
        Map<String, Object> searcherRequest = new HashMap<>();
        Map<String, Object> searchCriteria = new HashMap<>();
        searchCriteria.put(TENANT_ID_PARAM, ctx.getCriteria().getTenantId());
        searchCriteria.put(OWNER_ID, ctx.getRequestInfo().getUserInfo().getUuid());
        searcherRequest.put(REQUESTINFO_PARAM, ctx.getRequestInfo());
        searcherRequest.put(SEARCH_CRITERIA_PARAM, searchCriteria);

        StringBuilder dsoUri = new StringBuilder();
        dsoUri.append(config.getSearcherHost()).append(config.getFsmInboxDSoIDEndpoint());

        Object dsoResult = restTemplate.postForObject(dsoUri.toString(), searcherRequest, Map.class);
        return JsonPath.read(dsoResult, "$.vendor[0].id");
    }

    /**
     * Aggregates FSM status counts by application status.
     *
     * @param statusCountMap status count map
     * @return aggregated status count map
     */
    private List<HashMap<String, Object>> aggregateFsmStatuses(
            List<HashMap<String, Object>> statusCountMap) {

        List<HashMap<String, Object>> aggregated = new ArrayList<>();

        for (HashMap<String, Object> entry : statusCountMap) {
            boolean matchFound = false;
            HashMap<String, Object> temp = new HashMap<>();

            for (HashMap<String, Object> agg : aggregated) {
                String entryStatus = (String) entry.get(APPLICATIONSTATUS);
                String aggStatus = (String) agg.get(APPLICATIONSTATUS);

                if (aggStatus != null && aggStatus.equalsIgnoreCase(entryStatus)) {
                    agg.put(COUNT, (Integer) entry.get(COUNT) + (Integer) agg.get(COUNT));
                    agg.put(APPLICATIONSTATUS, entry.get(APPLICATIONSTATUS));
                    agg.put(FSMConstants.BUSINESS_SERVICE_PARAM,
                            entry.get(FSMConstants.BUSINESS_SERVICE_PARAM)
                                    + "," + agg.get(FSMConstants.BUSINESS_SERVICE_PARAM));
                    agg.put(STATUSID, entry.get(STATUSID) + "," + agg.get(STATUSID));
                    matchFound = true;
                    break;
                } else {
                    temp.put(COUNT, entry.get(COUNT));
                    temp.put(APPLICATIONSTATUS, entry.get(APPLICATIONSTATUS));
                    temp.put(FSMConstants.BUSINESS_SERVICE_PARAM, entry.get(FSMConstants.BUSINESS_SERVICE_PARAM));
                    temp.put(STATUSID, entry.get(STATUSID));
                }
            }

            if (aggregated.isEmpty()) {
                aggregated.add(entry);
            } else if (!matchFound) {
                aggregated.add(temp);
            }
        }

        return aggregated;
    }
}
