package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.CNDInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.CNDServiceConstants.*;
@Slf4j
@Service
public class CNDModuleHandler implements ModuleInboxHandler {

    @Autowired
    private CNDInboxFilterService cndService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName The name of the module to check.
     * @return true if the module name matches "CND", false otherwise.
     */
    @Override
    public boolean supports(String moduleName) {
        return CND.equals(moduleName);
    }

    /**
     * Fetches application IDs for the CND module based on the provided context.
     * This method retrieves application numbers from the searcher and updates the context
     * with the retrieved IDs.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = cndService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }
        ctx.getCriteria().getModuleSearchCriteria().put(APPLICATION_NO_PARAM, ids);
        ctx.addBusinessKeys(ids);
        ctx.getCriteria().getModuleSearchCriteria().remove(STATUS_PARAM);
        if (ctx.getCriteria().getModuleSearchCriteria().containsKey(APPLICATION_STATUS)) {
            ctx.getCriteria().getModuleSearchCriteria().put(
                    STATUS_PARAM,
                    ctx.getCriteria().getModuleSearchCriteria().get(APPLICATION_STATUS));
        }
    }

    @Override
    public String getApplicationIdParamKey() {
        return APPLICATION_NO_PARAM;
    }

    /**
     * Returns a list of parameters to be removed from the search criteria.
     * For the CND module, specific parameters like offset and status are removed.
     *
     * @return A list of parameter keys to be removed.
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(OFFSET_PARAM,APPLICATION_STATUS);
    }
}
