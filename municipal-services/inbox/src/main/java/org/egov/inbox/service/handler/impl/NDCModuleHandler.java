package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.NDCInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.NdcConstants.*;

/**
 * NDCModuleHandler is responsible for handling
 * inbox operations specific to NDC module.
 */
@Slf4j
@Service
public class NDCModuleHandler implements ModuleInboxHandler {

    @Autowired
    private NDCInboxFilterService ndcService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is NDC
     */
    @Override
    public boolean supports(String moduleName) {
        return NDC_MODULE.equals(moduleName);
    }

    /**
     * Fetches application ids for inbox search
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = ndcService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        ctx.getCriteria().getModuleSearchCriteria().put(NDC_APPLICATION_NO_PARAM, ids);
        ctx.addBusinessKeys(ids);
        ctx.getCriteria().getModuleSearchCriteria().remove(STATUS_PARAM);
        if (ctx.getCriteria().getModuleSearchCriteria().containsKey(APPLICATION_STATUS)) {
            ctx.getCriteria().getModuleSearchCriteria().put(
                    STATUS_PARAM,
                    ctx.getCriteria().getModuleSearchCriteria().get(APPLICATION_STATUS));
        }
    }

    /**
     * Fetches total application count from searcher.
     *
     * @param ctx inbox context
     * @return total application count
     */
    @Override
    public int fetchCount(InboxContext ctx) {
        return ndcService.fetchApplicationCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return NDC_APPLICATION_NO_PARAM;
    }

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(APPLICATION_STATUS, LOCALITY_PARAM, OFFSET_PARAM);
    }
}
