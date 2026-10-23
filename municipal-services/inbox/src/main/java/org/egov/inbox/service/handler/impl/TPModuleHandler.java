package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.TPInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.RequestServiceConstants.*;

/**
 * TPModuleHandler is responsible for handling
 * inbox operations specific to Tree Pruning module.
 */
@Slf4j
@Service
public class TPModuleHandler implements ModuleInboxHandler {

    @Autowired
    private TPInboxFilterService tpService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is Tree Pruning
     */
    @Override
    public boolean supports(String moduleName) {
        return REQUEST_SERVICE_TREE_PRUNING.equals(moduleName);
    }

    /**
     * Fetches application ids for inbox search
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = tpService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        ctx.getCriteria().getModuleSearchCriteria().put(BOOKING_NO_PARAM, ids);
        ctx.addBusinessKeys(ids);
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return BOOKING_NO_PARAM;
    }

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(LOCALITY_PARAM, OFFSET_PARAM, STATUS_PARAM);
    }
}
