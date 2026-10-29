package org.upyog.Automation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Data Transfer Object encapsulating the result of executing a single module or test case.
 *
 * <p>Tracks test metadata including module name, test case name, outcome status (PASSED/FAILED),
 * descriptive error message if any, and the specific step that failed.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ModuleExecutionResult {

    /**
     * Name of the module executed (e.g., "PET_REGISTRATION", "TRADE_LICENSE").
     */
    private String module;

    /**
     * Name or identifier of the executed test case.
     */
    private String testCase;

    /**
     * Execution status outcome (e.g., "PASSED", "FAILED").
     */
    private String status;

    /**
     * Informational or error message describing the execution result.
     */
    private String message;

    /**
     * Step name where failure occurred, or null if test passed.
     */
    private String failedStep;

    /**
     * Convenience constructor for general module-level execution outcomes.
     *
     * @param module the module name
     * @param status the execution outcome status
     * @param message description or error message
     */
    public ModuleExecutionResult(String module,
                                 String status,
                                 String message) {
        this.module = module;
        this.status = status;
        this.message = message;
    }
}