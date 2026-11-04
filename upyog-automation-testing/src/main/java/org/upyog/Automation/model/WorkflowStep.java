package org.upyog.Automation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an individual step within a multi-stakeholder workflow configuration.
 *
 * <p>Specifies the step name, stakeholder type (e.g., "CITIZEN", "EMPLOYEE", "VENDOR"),
 * module identifier, and stakeholder role (e.g., "INITIATOR", "APPROVER", "VERIFIER").</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkflowStep {

    /**
     * Display name of the workflow step.
     */
    private String name;

    /**
     * Stakeholder type executing this step (e.g., "CITIZEN", "EMPLOYEE", "VENDOR").
     */
    private String type;

    /**
     * Target module identifier to run for this step.
     */
    private String module;

    /**
     * Stakeholder role required to perform this step (e.g., "INITIATOR", "VERIFIER", "APPROVER").
     */
    private String role;

}