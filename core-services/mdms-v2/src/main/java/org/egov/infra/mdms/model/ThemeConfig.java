package org.egov.infra.mdms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents live theme configuration.
 *
 * Theme configuration is maintained at state tenant level.
 * Separate configurations can be maintained for different theme types
 * like EMPLOYEE and CITIZEN.
 *
 * The actual theme details are stored as JSON structure to support
 * dynamic UI configurations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThemeConfig {

    /**
     * Unique identifier of theme configuration record.
     */
    private String id;

    /**
     * State level tenant identifier.
     *
     * Example:
     * pg
     */
    private String tenantId;

    /**
     * Type of theme configuration.
     *
     * Supported values:
     * EMPLOYEE
     * CITIZEN
     */
    private String themeType;

    /**
     * JSON configuration containing theme details.
     *
     * Stores dynamic fields like:
     * theme
     * common
     * pages
     */
    private Map<String, Object> config;

    /**
     * Indicates whether this configuration is active.
     */
    private Boolean isActive;

    /**
     * Workflow status of theme configuration.
     * Values: PENDING, APPROVED, REJECTED
     */
    private String status;

    /**
     * Workflow instance identifier.
     */
    private String workflowId;

    /**
     * User who created the configuration.
     */
    private String createdBy;

    /**
     * Timestamp when configuration was created.
     */
    private Long createdTime;

    /**
     * User who last modified the configuration.
     */
    private String lastModifiedBy;

    /**
     * Timestamp when configuration was last modified.
     */
    private Long lastModifiedTime;
}