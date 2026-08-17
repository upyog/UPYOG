package org.egov.infra.mdms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Criteria class used for searching theme configuration.
 *
 * Theme configuration is maintained at state tenant level.
 * Configuration is separated based on theme type:
 *
 * EMPLOYEE
 * CITIZEN
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeConfigCriteria {

    /**
     * State level tenant identifier.
     */
    private String tenantId;


    /**
     * Theme configuration type.
     *
     * Example:
     * EMPLOYEE
     * CITIZEN
     */
    private String themeType;


    /**
     * Status of staging configuration.
     *
     * Example:
     * PENDING
     * APPROVED
     * REJECTED
     */
    private String status;

}