package org.egov.infra.mdms.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.infra.mdms.model.ThemeConfig;

public interface WorkflowService {


    /**
     * Creates workflow instance for theme configuration update.
     */
    String createWorkflow(
            ThemeConfig themeConfig,
            RequestInfo requestInfo
    );


    /**
     * Transitions workflow state for approve/reject action.
     */
    String transitionWorkflow(
            ThemeConfig themeConfig,
            RequestInfo requestInfo,
            String action
    );

}
