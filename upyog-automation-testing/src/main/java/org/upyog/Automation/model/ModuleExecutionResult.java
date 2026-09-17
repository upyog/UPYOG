package org.upyog.Automation.model;

public class ModuleExecutionResult {

    private String module;
    private String status;
    private String message;

    public ModuleExecutionResult() {
    }

    public ModuleExecutionResult(String module,
                                 String status,
                                 String message) {
        this.module = module;
        this.status = status;
        this.message = message;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}