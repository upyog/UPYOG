package org.upyog.Automation.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Encapsulates full end-to-end workflow execution data loaded from a workflow JSON file.
 *
 * <p>Contains the overall module name and the list of {@link WorkflowStep} steps
 * across different stakeholders (Citizen, Employee, Vendor) to be executed in sequence.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkflowData {

    /**
     * Name or identifier of the end-to-end workflow module.
     */
    private String moduleName;

    /**
     * Ordered list of workflow steps to execute.
     */
    private List<WorkflowStep> steps;

}