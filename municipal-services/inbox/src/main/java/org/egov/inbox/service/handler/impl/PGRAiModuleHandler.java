package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.PGRAiInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.egov.inbox.util.PGRAiConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.egov.inbox.util.CommonConstants.*;

/**
 * PGRAiModuleHandler is responsible for handling
 * inbox operations specific to PGR AI module.
 */
@Slf4j
@Service
public class PGRAiModuleHandler implements ModuleInboxHandler {

    @Autowired
    private PGRAiInboxFilterService pgrAiService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is PGR AI
     */
    @Override
    public boolean supports(String moduleName) {
        return PGRAiConstants.PGR_MODULE.equals(moduleName);
    }

    /**
     * Fetches application ids for inbox search
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = pgrAiService.fetchApplicationIdsFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }
        ctx.getCriteria().getModuleSearchCriteria().put(getApplicationIdParamKey(), ids);

        ctx.addBusinessKeys(ids);
    }

    /**
     * Fetches total application count from searcher.
     *
     * @param ctx inbox context
     * @return total application count
     */
    @Override
    public int fetchCount(InboxContext ctx) {
        return pgrAiService.fetchApplicationIdsCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return "serviceRequestIds";
    }

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(OFFSET_PARAM, STATUS_PARAM,NO_OF_RECORDS_PARAM);
    }

    /**
     * Builds business object map using service request id.
     *
     * @param businessObjects business objects response
     * @param businessIdParam business id parameter
     * @return mapped business object data
     */
    @Override
    public Map<String, Object> buildBusinessMap(
            JSONArray businessObjects,
            String businessIdParam) {

        return StreamSupport.stream(
                businessObjects.spliterator(), false)
                .collect(Collectors.toMap(
                        s -> ((JSONObject) s)
                                .getJSONObject("service")
                                .get(businessIdParam)
                                .toString(),
                        s -> s,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }
}