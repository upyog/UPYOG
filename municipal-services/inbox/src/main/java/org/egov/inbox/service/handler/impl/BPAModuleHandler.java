package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.service.BPAInboxFilterService;
import org.egov.inbox.service.WorkflowService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.egov.inbox.web.model.Inbox;
import org.egov.inbox.web.model.workflow.ProcessInstanceResponse;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.egov.inbox.util.BpaConstants.*;
/**
 * BPAModuleHandler is responsible for handling the inbox operations
 * specific to the "BPA" module. It implements the ModuleInboxHandler interface
 * to provide module-specific behavior.
 */
@Slf4j
@Service
public class BPAModuleHandler implements ModuleInboxHandler {

        @Autowired
        private BPAInboxFilterService bpaService;

        @Autowired
        private WorkflowService workflowService;

        /**
         * Checks if this handler supports the given module name.
         *
         * @param moduleName The name of the module to check.
         * @return true if the module name matches "BPA", false otherwise.
         */
        @Override
        public boolean supports(String moduleName) {
                return BPA.equals(moduleName);
        }

        /**
         * Fetches application IDs for the BPA module based on the provided context.
         * This method retrieves application numbers from the searcher and updates the context
         * with the retrieved IDs.
         *
         * @param ctx The InboxContext containing the search criteria and other details.
         */
        @Override
        public void fetchApplicationIds(InboxContext ctx) {
                // Fetch application numbers using the BPA service
                List<String> ids = bpaService.fetchApplicationNumbersFromSearcher(
                                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

                // If no IDs are found, mark the search result as empty and return
                if (CollectionUtils.isEmpty(ids)) {
                        ctx.setSearchResultEmpty(true);
                        return;
                }

                // Add the fetched IDs to the module search criteria
                ctx.getCriteria().getModuleSearchCriteria().put(BPA_APPLICATION_NUMBER_PARAM, ids);

                // Add the fetched IDs as business keys in the context
                ctx.addBusinessKeys(ids);
        }

        /**
         * Fetches the count of applications for the BPA module based on the provided context.
         *
         * @param ctx The InboxContext containing the search criteria and other details.
         * @return The count of applications matching the criteria.
         */
        @Override
        public int fetchCount(InboxContext ctx) {
                // Fetch the application count using the BPA service
                return bpaService.fetchApplicationCountFromSearcher(
                                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
        }

        /**
         * Returns the parameter key used for application IDs in the BPA module.
         *
         * @return The parameter key for application IDs.
         */
        @Override
        public String getApplicationIdParamKey() {
                return BPA_APPLICATION_NUMBER_PARAM;
        }

        /**
         * Returns a list of parameters to be removed from the search criteria.
         * For the BPA module, specific parameters like status, mobile number, locality, and offset are removed.
         *
         * @return A list of parameter keys to be removed.
         */
        @Override
        public List<String> paramsToRemove() {
                return List.of(STATUS_PARAM, MOBILE_NUMBER_PARAM, LOCALITY_PARAM, OFFSET_PARAM);
        }

        /**
         * Enriches the status count map before fetching data for the BPA module.
         * This method handles citizen-specific and locality-specific status counts.
         *
         * @param ctx                     The InboxContext containing the search criteria and other details.
         * @param statusCountMap          The current status count map to be enriched.
         * @param tenantAndApplnNumbersMap A map of tenant IDs to application numbers.
         * @param roles                   The roles of the user.
         * @param inputStatuses           The input statuses provided in the criteria.
         * @return The enriched status count map.
         */
        @Override
        public List<HashMap<String, Object>> enrichStatusCountPreFetch(
                        InboxContext ctx,
                        List<HashMap<String, Object>> statusCountMap,
                        Map<String, List<String>> tenantAndApplnNumbersMap,
                        List<String> roles,
                        List<String> inputStatuses) {

                // Handle citizen-specific status counts
                statusCountMap = handleBpaCitizenStatusCount(
                                ctx, statusCountMap, tenantAndApplnNumbersMap, roles);

                // Handle locality-specific status counts
                statusCountMap = handleBpaLocalityStatusCount(
                                ctx, statusCountMap, inputStatuses);

                return statusCountMap;
        }

        /**
         * Enriches the status count map after assembling data for the BPA module.
         *
         * @param ctx            The InboxContext containing the search criteria and other details.
         * @param statusCountMap The current status count map to be enriched.
         * @param inputStatuses  The input statuses provided in the criteria.
         * @param inboxes        The list of inbox items.
         * @param totalCount     The total count of applications.
         * @return A PostAssembleResult containing the enriched status count map and total count.
         */
        @Override
        public PostAssembleResult enrichStatusCountPostAssemble(
                        InboxContext ctx,
                        List<HashMap<String, Object>> statusCountMap,
                        List<String> inputStatuses,
                        List<Inbox> inboxes,
                        Integer totalCount) {

                return new PostAssembleResult(statusCountMap, totalCount);
        }

        /**
         * Fetches process instances for the BPA module based on the provided criteria.
         * This method handles citizen-specific logic for fetching process instances.
         *
         * @param processCriteria         The criteria for fetching process instances.
         * @param requestInfo             The request information containing user details.
         * @param workflowService         The workflow service to fetch process instances.
         * @param tenantAndApplnNumbersMap A map of tenant IDs to application numbers.
         * @param businessIds             The list of business IDs.
         * @param roles                   The roles of the user.
         * @return A ProcessInstanceResponse containing the fetched process instances.
         */
        @Override
        public ProcessInstanceResponse getProcessInstances(
                        ProcessInstanceSearchCriteria processCriteria,
                        RequestInfo requestInfo,
                        WorkflowService workflowService,
                        Map<String, List<String>> tenantAndApplnNumbersMap,
                        List<Object> businessIds,
                        List<String> roles) {

                if (!roles.contains(CITIZEN)) {
                        return workflowService.getProcessInstance(processCriteria, requestInfo);
                }

                Map<String, List<String>> tenantApplicationMap = new HashMap<>();

                for (Object businessId : businessIds) {

                        for (Map.Entry<String, List<String>> entry : tenantAndApplnNumbersMap.entrySet()) {

                                if (entry.getValue().contains(businessId)) {

                                        tenantApplicationMap
                                                        .computeIfAbsent(
                                                                        entry.getKey(),
                                                                        k -> new ArrayList<>())
                                                        .add(String.valueOf(businessId));
                                }
                        }
                }

                ProcessInstanceResponse mergedResponse = new ProcessInstanceResponse();

                for (Map.Entry<String, List<String>> entry : tenantApplicationMap.entrySet()) {

                        processCriteria.setTenantId(entry.getKey());

                        processCriteria.setBusinessIds(entry.getValue());

                        ProcessInstanceResponse response = workflowService.getProcessInstance(
                                        processCriteria,
                                        requestInfo);

                        mergedResponse.setResponseInfo(
                                        response.getResponseInfo());

                        if (mergedResponse.getProcessInstances() == null) {

                                mergedResponse.setProcessInstances(
                                                response.getProcessInstances());

                        } else {

                                mergedResponse.getProcessInstances()
                                                .addAll(response.getProcessInstances());
                        }
                }

                return mergedResponse;
        }

        /**
         * Handles citizen-specific status counts for the BPA module.
         *
         * @param ctx                     The InboxContext containing the search criteria and other details.
         * @param statusCountMap          The current status count map to be enriched.
         * @param tenantAndApplnNumbersMap A map of tenant IDs to application numbers.
         * @param roles                   The roles of the user.
         * @return The enriched status count map.
         */
        private List<HashMap<String, Object>> handleBpaCitizenStatusCount(
                        InboxContext ctx,
                        List<HashMap<String, Object>> statusCountMap,
                        Map<String, List<String>> tenantAndApplnNumbersMap,
                        List<String> roles) {

                if (roles.contains(CITIZEN)) {

                        List<Map<String, String>> tenantWiseApplns = bpaService
                                        .fetchTenantWiseApplicationNumbersForCitizenInboxFromSearcher(
                                                        ctx.getCriteria(),
                                                        ctx.getStatusIdNameMap(),
                                                        ctx.getRequestInfo());

                        if (!CollectionUtils.isEmpty(tenantWiseApplns)) {

                                tenantWiseApplns.forEach(applicationMap -> {

                                        String tenant = applicationMap.get("tenantid");

                                        String applnNo = applicationMap.get("applicationno");

                                        if (tenantAndApplnNumbersMap.containsKey(tenant)) {

                                                List<String> applnNos = tenantAndApplnNumbersMap.get(tenant);

                                                applnNos.add(applnNo);

                                                tenantAndApplnNumbersMap.put(
                                                                tenant,
                                                                applnNos);

                                        } else {

                                                List<String> l = new ArrayList<>();

                                                l.add(applnNo);

                                                tenantAndApplnNumbersMap.put(
                                                                tenant,
                                                                l);
                                        }
                                });
                        }
                }

                if (!roles.contains(CITIZEN)
                                || tenantAndApplnNumbersMap.isEmpty())
                        return statusCountMap;

                List<HashMap<String, Object>> bpaCitizenMap = new ArrayList<>();

                var processCriteria = ctx.getCriteria().getProcessSearchCriteria();

                String savedTenant = processCriteria.getTenantId();

                List<String> savedBizIds = processCriteria.getBusinessIds();

                List<String> savedStatus = processCriteria.getStatus();

                if (!ctx.getStatusIdNameMap().isEmpty())
                        processCriteria.setStatus(
                                        new ArrayList<>(
                                                        ctx.getStatusIdNameMap().keySet()));

                for (Map.Entry<String, List<String>> entry : tenantAndApplnNumbersMap.entrySet()) {

                        processCriteria.setTenantId(entry.getKey());

                        processCriteria.setBusinessIds(entry.getValue());

                        List<HashMap<String, Object>> tenantCount = workflowService.getProcessStatusCount(
                                        ctx.getRequestInfo(),
                                        processCriteria);

                        if (bpaCitizenMap.isEmpty()) {

                                bpaCitizenMap.addAll(tenantCount);

                        } else {

                                for (HashMap<String, Object> tenantMap : tenantCount) {

                                        for (HashMap<String, Object> citizenMap : bpaCitizenMap) {

                                                if (citizenMap.containsValue(
                                                                tenantMap.get(STATUS_ID))) {

                                                        citizenMap.put(
                                                                        COUNT,
                                                                        Integer.parseInt(
                                                                                        String.valueOf(
                                                                                                        citizenMap.get(COUNT)))
                                                                                        + Integer.parseInt(
                                                                                                        String.valueOf(
                                                                                                                        tenantMap.get(COUNT))));
                                                }
                                        }
                                }
                        }
                }

                processCriteria.setTenantId(savedTenant);

                processCriteria.setBusinessIds(savedBizIds);

                processCriteria.setStatus(savedStatus);

                return bpaCitizenMap.isEmpty()
                                ? statusCountMap
                                : bpaCitizenMap;
        }

        



        /**
         * Handles locality-specific status counts for the BPA module.
         *
         * @param ctx            The InboxContext containing the search criteria and other details.
         * @param statusCountMap The current status count map to be enriched.
         * @param inputStatuses  The input statuses provided in the criteria.
         * @return The enriched status count map.
         */
        private List<HashMap<String, Object>> handleBpaLocalityStatusCount(
                        InboxContext ctx,
                        List<HashMap<String, Object>> statusCountMap,
                        List<String> inputStatuses) {

                HashMap<String, Object> moduleSearchCriteria = ctx.getCriteria().getModuleSearchCriteria();

                if (moduleSearchCriteria.get(LOCALITY_PARAM) == null)
                        return statusCountMap;

                for (Map<String, Object> statusWiseCount : statusCountMap) {

                        List<String> statusList = Collections.singletonList(
                                        String.valueOf(
                                                        statusWiseCount.get(STATUS_ID)));

                        ctx.getCriteria()
                                        .getProcessSearchCriteria()
                                        .setStatus(statusList);

                        Integer count = bpaService.fetchApplicationCountFromSearcher(
                                        ctx.getCriteria(),
                                        ctx.getStatusIdNameMap(),
                                        ctx.getRequestInfo());

                        if (count == 0)
                                statusWiseCount.clear();

                        else
                                statusWiseCount.put(COUNT, count);
                }

                ctx.getCriteria()
                                .getProcessSearchCriteria()
                                .setStatus(inputStatuses);

                return statusCountMap.stream()
                                .filter(map -> !map.isEmpty())
                                .collect(Collectors.toList());
        }
}
