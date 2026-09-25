package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.AssetInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.AssetConstants.*;
/**
 * ASSETModuleHandler is responsible for handling the inbox operations
 * specific to the "ASSET" module. It implements the ModuleInboxHandler interface
 * to provide module-specific behavior.
 */
@Slf4j
@Service
public class ASSETModuleHandler implements ModuleInboxHandler {

    // Service to handle asset-specific inbox filtering logic
    @Autowired
    private AssetInboxFilterService assetService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName The name of the module to check.
     * @return true if the module name matches "ASSET", false otherwise.
     */
    @Override
    public boolean supports(String moduleName) {
        return ASSET.equals(moduleName);
    }

    /**
     * Indicates whether the workflow nearing SLA count is required for this module.
     *
     * @return false, as the ASSET module does not require SLA count.
     */
    @Override
    public boolean isWorkflowNearingSlaCountRequired() {
        return false;
    }

    /**
     * Fetches application IDs for the ASSET module based on the provided context.
     * This method retrieves application numbers from the searcher and updates the context
     * with the retrieved IDs.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        // Fetch application numbers using the asset service
        List<String> ids = assetService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        // If no IDs are found, mark the search result as empty and return
        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        // Add the fetched IDs to the module search criteria and context
        ctx.getCriteria().getModuleSearchCriteria().put(APPLICATION_NO, ids);
        ctx.addBusinessKeys(ids);
    }

    /**
     * Returns the parameter key used for application IDs in the ASSET module.
     *
     * @return The parameter key for application IDs.
     */
    @Override
    public String getApplicationIdParamKey() {
        return APPLICATION_NO;
    }

    /**
     * Returns a list of parameters to be removed from the search criteria.
     * For the ASSET module, specific parameters like status, and offset are removed.
     *
     * @return A list of parameter keys to be removed.
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(OFFSET_PARAM, STATUS_PARAM);
    }
}
