package org.upyog.Automation.model;

import java.util.List;

public class WorkflowData {

    private String moduleName;

    private List<WorkflowStep> steps;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<WorkflowStep> getSteps() {
        return steps;
    }

    public void setSteps(List<WorkflowStep> steps) {
        this.steps = steps;
    }
}