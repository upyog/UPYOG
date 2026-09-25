package org.upyog.Automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Represents a complete test module loaded from JSON.
 * Contains module metadata and a list of instructions to execute sequentially.
 */
public class TestModule {

    @JsonProperty("moduleName")
    private String moduleName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("baseUrl")
    private String baseUrl;

    @JsonProperty("instructions")
    private List<TestInstruction> instructions;

    public TestModule() {}

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<TestInstruction> getInstructions() {
        return instructions;
    }

    public void setInstructions(List<TestInstruction> instructions) {
        this.instructions = instructions;
    }
}
