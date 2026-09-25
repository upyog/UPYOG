package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.TLInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.TLConstants.*;
import static org.egov.inbox.util.BpaConstants.BPAREG;

/**
 * TLModuleHandler is responsible for handling
 * inbox operations specific to TL and BPA Trade License modules.
 */
@Slf4j
@Service
public class TLModuleHandler implements ModuleInboxHandler {

    @Autowired
    private TLInboxFilterService tlService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is TL or BPAREG
     */
    @Override
    public boolean supports(String moduleName) {
        return TL.equals(moduleName) || BPAREG.equals(moduleName);
    }

    /**
     * Fetches application ids for inbox search
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = tlService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        ctx.getCriteria().getModuleSearchCriteria().put(APPLICATION_NUMBER_PARAM, ids);
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
        return tlService.fetchApplicationCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return APPLICATION_NUMBER_PARAM;
    }

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(STATUS_PARAM, LOCALITY_PARAM, OFFSET_PARAM);
    }
}
