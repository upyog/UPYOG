package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.FirenocInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.FirenocConstants.*;



/**
 * Handler implementation for the Garbage Collection module inbox.
 *
 * <p>This handler is responsible for:
 * <ul>
 *     <li>Identifying whether the requested module is Garbage Collection.</li>
 *     <li>Fetching application IDs based on the inbox search criteria.</li>
 *     <li>Fetching the total application count for pagination.</li>
 *     <li>Providing module-specific search parameter configuration.</li>
 * </ul>
 */
@Slf4j
@Service
public class FirenocModuleHandler implements ModuleInboxHandler {

    @Autowired
    private FirenocInboxFilterService firenocService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName The name of the module to check.
     * @return true if the module name matches "garbage-service", false otherwise.
     */
    @Override
    public boolean supports(String moduleName) {
        return FIRENOC_SERVICE.equals(moduleName);
    }

    /**
     * Fetches application IDs for the Garbage Service module based on the provided context.
     * This method retrieves application numbers from the searcher and updates the context
     * with the retrieved IDs.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = firenocService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }
        ctx.getCriteria().getModuleSearchCriteria().put(FIRENOC_APPLICATION_NUMBER_PARAM, ids);
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
        return firenocService.fetchApplicationCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
    }

    /**
     * Returns the parameter key used for application IDs in the Garbage Service module.
     *
     * @return The parameter key for application IDs.
     */
    @Override
    public String getApplicationIdParamKey() {
        return FIRENOC_APPLICATION_NUMBER_PARAM;
    }

    /**
     * Returns a list of parameters to be removed from the search criteria.
     *
     * @return A list of parameter keys to be removed.
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(APPLICATION_STATUS, LOCALITY_PARAM, OFFSET_PARAM, SORT_ORDER_PARAM);
    }
}
