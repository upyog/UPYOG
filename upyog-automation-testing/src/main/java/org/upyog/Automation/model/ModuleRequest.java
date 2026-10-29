package org.upyog.Automation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object representing module execution parameters.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModuleRequest {

    /**
     * Name of the module to execute, or comma-separated list of module names.
     */
    private String moduleName;

    /**
     * Target application base URL for the automation tests.
     */
    private String baseUrl;

}
