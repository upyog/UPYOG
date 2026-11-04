package org.egov.inbox.service.handler.impl;

import lombok.extern.slf4j.Slf4j;
import org.egov.inbox.service.PtrInboxFilterService;
import org.egov.inbox.service.handler.InboxContext;
import org.egov.inbox.service.handler.ModuleInboxHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import static org.egov.inbox.util.PTRConstants.*;
/**
 * PTRModuleHandler is responsible for handling
 * inbox operations specific to PTR module.
 */
@Slf4j
@Service
public class PTRModuleHandler implements ModuleInboxHandler {

    @Autowired
    private PtrInboxFilterService ptrService;

    /**
     * Checks if this handler supports the given module name.
     *
     * @param moduleName module name
     * @return true if module is PTR
     */
    @Override
    public boolean supports(String moduleName) {
        return PTR.equals(moduleName);
    }

    /**
     * Indicates whether workflow nearing SLA count
     * is required for this module.
     *
     * @return false for PTR module
     */
    @Override
    public boolean isWorkflowNearingSlaCountRequired() {
        return false;
    }

    /**
     * Fetches application ids for inbox search
     * and updates inbox context.
     *
     * @param ctx inbox context
     */
    @Override
    public void fetchApplicationIds(InboxContext ctx) {
        List<String> ids = ptrService.fetchApplicationNumbersFromSearcher(
                ctx.getCriteria(), ctx.getStatusIdNameMap(), ctx.getRequestInfo());

        if (CollectionUtils.isEmpty(ids)) {
            ctx.setSearchResultEmpty(true);
            return;
        }

        ctx.getCriteria().getModuleSearchCriteria().put(APPLICATION_NUMBERS, ids);
        ctx.addBusinessKeys(ids);
    }

    /**
     * Returns application id parameter key.
     *
     * @return application id parameter key
     */
    @Override
    public String getApplicationIdParamKey() {
        return APPLICATION_NUMBERS;
    }

    /**
     * Returns module search params
     * which should be removed.
     *
     * @return list of params to remove
     */
    @Override
    public List<String> paramsToRemove() {
        return List.of(LOCALITY_PARAM, OFFSET_PARAM,STATUS_PARAM);
    }
}
