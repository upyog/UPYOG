package org.egov.inbox.service.handler;

import org.egov.common.contract.request.RequestInfo;
import org.egov.inbox.service.WorkflowService;
import org.egov.inbox.web.model.Inbox;
import org.egov.inbox.web.model.workflow.ProcessInstanceResponse;
import org.egov.inbox.web.model.workflow.ProcessInstanceSearchCriteria;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ModuleInboxHandler defines contract
 * for module specific inbox handling.
 */
public interface ModuleInboxHandler {

    /**
     * Checks whether handler supports the module.
     *
     * @param moduleName module name
     * @return true if handler supports module
     */
    boolean supports(String moduleName);

    /**
     * Fetches application ids and updates inbox context.
     *
     * @param ctx inbox context
     */
    void fetchApplicationIds(InboxContext ctx);

    /**
     * Fetches total application count.
     *
     * @param ctx inbox context
     * @return total application count
     */
    default int fetchCount(InboxContext ctx) {
        return -1;
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    String getApplicationIdParamKey();

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    default List<String> paramsToRemove() {
        return List.of();
    }

    /**
     * Returns internal workflow module name.
     *
     * @param moduleName module name
     * @return internal module name
     */
    default String getInternalModuleName(String moduleName) {
        return moduleName;
    }

    /**
     * Checks whether workflow total count
     * is required for the module.
     *
     * @return workflow total count required flag
     */
    default boolean isWorkflowTotalCountRequired() {
        return true;
    }

    /**
     * Checks whether workflow nearing SLA count
     * is required for the module.
     *
     * @return workflow nearing SLA count required flag
     */
    default boolean isWorkflowNearingSlaCountRequired() {
        return true;
    }

    /**
     * Enriches status count before inbox fetch.
     *
     * @param ctx inbox context
     * @param statusCountMap status count map
     * @param tenantAndApplnNumbersMap tenant application mapping
     * @param roles user roles
     * @param inputStatuses input statuses
     * @return enriched status count map
     */
    default List<HashMap<String, Object>> enrichStatusCountPreFetch(
            InboxContext ctx,
            List<HashMap<String, Object>> statusCountMap,
            Map<String, List<String>> tenantAndApplnNumbersMap,
            List<String> roles,
            List<String> inputStatuses) {
        return statusCountMap;
    }

    /**
     * Enriches status count after inbox assembly.
     *
     * @param ctx inbox context
     * @param statusCountMap status count map
     * @param inputStatuses input statuses
     * @param inboxes inbox response
     * @param totalCount total count
     * @return enriched post assemble result
     */
    default PostAssembleResult enrichStatusCountPostAssemble(
            InboxContext ctx,
            List<HashMap<String, Object>> statusCountMap,
            List<String> inputStatuses,
            List<Inbox> inboxes,
            Integer totalCount) {
        return new PostAssembleResult(statusCountMap, totalCount);
    }

    /**
     * Fetches workflow process instances.
     *
     * @param processCriteria workflow process criteria
     * @param requestInfo request info
     * @param workflowService workflow service
     * @param tenantAndApplnNumbersMap tenant application mapping
     * @param businessIds business ids
     * @param roles user roles
     * @return workflow process instances
     */
    default ProcessInstanceResponse getProcessInstances(
            ProcessInstanceSearchCriteria processCriteria,
            RequestInfo requestInfo,
            WorkflowService workflowService,
            Map<String, List<String>> tenantAndApplnNumbersMap,
            List<Object> businessIds,
            List<String> roles) {

        return workflowService.getProcessInstance(processCriteria, requestInfo);
    }

    /**
     * Builds business object map using business id.
     *
     * @param businessObjects business objects response
     * @param businessIdParam business id parameter
     * @return mapped business object data
     */
    default Map<String, Object> buildBusinessMap(
            JSONArray businessObjects,
            String businessIdParam) {

        return StreamSupport.stream(
                businessObjects.spliterator(), false)
                .collect(Collectors.toMap(
                        s -> ((JSONObject) s)
                                .get(businessIdParam)
                                .toString(),
                        s -> s,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    /**
     * Post assemble response wrapper.
     */
    class PostAssembleResult {

        public final List<HashMap<String, Object>> statusCountMap;

        public final Integer totalCount;

        /**
         * Initializes post assemble result.
         *
         * @param statusCountMap status count map
         * @param totalCount total count
         */
        public PostAssembleResult(List<HashMap<String, Object>> statusCountMap, Integer totalCount) {
            this.statusCountMap = statusCountMap;
            this.totalCount = totalCount;
        }
    }
}