package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.ChallanInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.ChallanConstants.*;

/**
 * ChallanModuleHandler is responsible for handling the inbox operations
 * specific to the "CHALLAN" module. It implements the ModuleInboxHandler interface
 * to provide module-specific behavior.
 */
@Slf4j
@Service
public class ChallanModuleHandler implements ModuleInboxHandler {

    // Service to handle challan-specific inbox filtering logic
    @Autowired
    private ChallanInboxFilterService challanService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName The name of the module to check.
     * @return true if the module name matches "CHALLAN_GENERATION", false otherwise.
     */
    @Override
    public boolean supports(String moduleName) {
        return CHALLAN_GENERATION.equals(moduleName);
    }

    /**
     * Fetches application IDs for the CHALLAN module based on the provided context.
     * This method retrieves application numbers from the searcher and updates the context
     * with the retrieved IDs.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        // Fetch application numbers using the challan service
        List<String> ids = challanService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        // If no IDs are found, mark the search result as empty and return
        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        // Retrieve the application numbers parameter from the service map
        String applNosParam = ctx.getSrvMap() != null ? ctx.getSrvMap().get("applNosParam") : null;

        if (applNosParam != null) {
            // Add the fetched IDs to the module search criteria
            ctx.getCriteria().getModuleSearchCriteria().put(applNosParam, ids);

            // Remove the application status parameter from the module search criteria if it exists
            String applsStatusParam = ctx.getSrvMap().get("applsStatusParam");
            if (applsStatusParam != null) {
                ctx.removeModuleSearchCriteria(applsStatusParam);
            }
        }

        // Add the fetched IDs as business keys in the context
        ctx.addBusinessKeys(ids);
    }

    /**
     * Fetches the count of applications for the CHALLAN module based on the provided context.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     * @return The count of applications matching the criteria.
     */
    @Override
    public int fetchCount(InboxContext ctx) {
        // Fetch the application count using the challan service
        return challanService.fetchApplicationCountFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
    }

    /**
     * Returns the parameter key used for application IDs in the CHALLAN module.
     *
     * @return The parameter key for application IDs ("applNosParam").
     */
    @Override
    public String getApplicationIdParamKey() {
        return "applNosParam";
    }

    @Override
    public List<String> paramsToRemove() {
        return List.of(STATUS_PARAM, LOCALITY_PARAM, OFFSET_PARAM);
    }
}
