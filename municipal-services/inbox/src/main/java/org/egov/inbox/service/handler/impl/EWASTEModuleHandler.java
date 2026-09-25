package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.EwasteInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.egov.inbox.util.EwasteConstants.*;

/**
 * EWASTEModuleHandler is responsible for handling the inbox operations
 * specific to the "E-Waste" module. It implements the ModuleInboxHandler interface
 * to provide module-specific behavior.
 */
@Slf4j
@Service
public class EWASTEModuleHandler implements ModuleInboxHandler {

    @Autowired
    private EwasteInboxFilterService ewasteService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName The name of the module to check.
     * @return true if the module name matches "EWASTE", false otherwise.
     */
    @Override
    public boolean supports(String moduleName) {
        return EWASTE.equals(moduleName);
    }

    /**
     * Indicates whether the workflow nearing SLA count is required for this module.
     *
     * @return false, as the E-Waste module does not require SLA count.
     */
    @Override
    public boolean isWorkflowNearingSlaCountRequired() {
        return false;
    }

    /**
     * Fetches application IDs for the E-Waste module based on the provided context.
     * This method retrieves application numbers from the searcher and updates the context
     * with the retrieved IDs.
     *
     * @param ctx The InboxContext containing the search criteria and other details.
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = ewasteService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());
        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }
        ctx.getCriteria().getModuleSearchCriteria().put(REQUEST_IDS, ids);
        ctx.addBusinessKeys(ids);
    }

    /**
     * Returns the parameter key used for application IDs in the E-Waste module.
     *
     * @return The parameter key for application IDs.
     */
    @Override
    public String getApplicationIdParamKey() {
        return REQUEST_IDS;
    }

    /**
     * Returns a list of parameters to be removed from the search criteria.
     * For the E-Waste module, specific parameters like status, locality, and offset are removed.
     *
     * @return A list of parameter keys to be removed.
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(STATUS_PARAM, LOCALITY_PARAM, OFFSET_PARAM);
    }
}
