package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.StreetVendingInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.StreetVendingConstants.*;

/**
 * SVModuleHandler is responsible for handling
 * inbox operations specific to Street Vending module.
 */
@Slf4j
@Service
public class SVModuleHandler implements ModuleInboxHandler {

    @Autowired
    private StreetVendingInboxFilterService svService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is Street Vending
     */
    @Override
    public boolean supports(String moduleName) {
        return SV_SERVICES.equals(moduleName);
    }

    /**
     * Fetches application ids for inbox search
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = svService.fetchApplicationIdsFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        ctx.getCriteria().getModuleSearchCriteria().put(SV_APPLICATION_NUMBER_PARAM, ids);
        ctx.addBusinessKeys(ids);

        if (ctx.getCriteria().getModuleSearchCriteria().containsKey(APPLICATION_STATUS)) {
            ctx.getCriteria().getModuleSearchCriteria().put(
                    STATUS_PARAM,
                    ctx.getCriteria().getModuleSearchCriteria().get(APPLICATION_STATUS));
        }
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return SV_APPLICATION_NUMBER_PARAM;
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
