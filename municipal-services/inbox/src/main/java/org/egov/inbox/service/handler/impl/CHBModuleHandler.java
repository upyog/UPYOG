package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.CommunityHallInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.CommunityHallConstants.*;

@Slf4j
@Service
public class CHBModuleHandler implements ModuleInboxHandler {

    @Autowired
    private CommunityHallInboxFilterService chbService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName The name of the module to check.
     * @return true if the module name matches "CHB", false otherwise.
     */
    @Override
    public boolean supports(String moduleName) {
        return CHB.equals(moduleName);
    }

    /**
     * Fetches application IDs for the CHB module based on the provided context.
     * This method retrieves application numbers from the searcher and updates the context
     * with the retrieved IDs.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = chbService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }
        ctx.getCriteria().getModuleSearchCriteria().put(CHB_BOOKING_NO_PARAM, ids);
        ctx.addBusinessKeys(ids);
    }

    /**
     * Returns the parameter key used for application IDs in the CHB module.
     *
     * @return The parameter key for application IDs.
     */
    @Override
    public String getApplicationIdParamKey() {
        return CHB_BOOKING_NO_PARAM;
    }

    /**
     * Returns a list of parameters to be removed from the search criteria.
     * For the CHB module, specific parameters like offset and status are removed.
     *
     * @return A list of parameter keys to be removed.
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(OFFSET_PARAM, STATUS_PARAM);
    }
}
