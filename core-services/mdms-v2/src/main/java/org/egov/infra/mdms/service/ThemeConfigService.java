package org.egov.infra.mdms.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.infra.mdms.model.ThemeConfig;


/**
 * Service interface for managing Theme Configuration.
 *
 * Handles:
 * 1. Theme configuration creation
 * 2. Theme configuration updates through workflow
 * 3. Workflow status updates
 */
public interface ThemeConfigService {


    /**
     * Creates a new theme configuration.
     *
     * @param themeConfig theme configuration details
     * @return created theme configuration
     */
    ThemeConfig create(ThemeConfig themeConfig);


    /**
     * Creates a new pending theme configuration.
     *
     * A new row is created in the same ug_theme_config table.
     * Existing approved configuration remains unchanged.
     *
     * @param themeConfig updated theme configuration
     * @param requestInfo request information
     * @return pending theme configuration
     */
    ThemeConfig update(
            ThemeConfig themeConfig,
            RequestInfo requestInfo
    );


    /**
     * Updates workflow status of theme configuration.
     *
     * @param themeConfig theme configuration
     * @param action workflow action
     * @param requestInfo request information
     * @return updated theme configuration
     */
    ThemeConfig updateWorkflowStatus(
            ThemeConfig themeConfig,
            String action,
            RequestInfo requestInfo
    );


    /**
     * Fetches theme configuration.
     *
     * @param tenantId tenant identifier
     * @param themeType EMPLOYEE/CITIZEN
     * @return theme configuration
     */
    ThemeConfig search(
            String tenantId,
            String themeType
    );
}