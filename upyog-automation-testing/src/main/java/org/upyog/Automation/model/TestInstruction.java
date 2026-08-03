package org.upyog.Automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single test step instruction.
 * All fields are populated from JSON configuration—no hardcoding in Java.
 *
 * The framework uses this POJO to dynamically determine:
 * - Which element to locate (locatorStrategy + locatorValue)
 * - What action to perform (action)
 * - What data to input (inputValue)
 * - How long to wait after the action (dynamicSleep)
 */
public class TestInstruction {

    @JsonProperty("stepName")
    private String stepName;

    @JsonProperty("locatorStrategy")
    private String locatorStrategy;

    @JsonProperty("locatorValue")
    private String locatorValue;

    @JsonProperty("action")
    private String action;

    @JsonProperty("inputValue")
    private String inputValue;

    @JsonProperty("dynamicSleep")
    private long dynamicSleep;

    // Default constructor for Jackson deserialization
    public TestInstruction() {}

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getLocatorStrategy() {
        return locatorStrategy;
    }

    public void setLocatorStrategy(String locatorStrategy) {
        this.locatorStrategy = locatorStrategy;
    }

    public String getLocatorValue() {
        return locatorValue;
    }

    public void setLocatorValue(String locatorValue) {
        this.locatorValue = locatorValue;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public long getDynamicSleep() {
        return dynamicSleep;
    }

    public void setDynamicSleep(long dynamicSleep) {
        this.dynamicSleep = dynamicSleep;
    }

    @Override
    public String toString() {
        return String.format("TestInstruction{stepName='%s', action='%s', locator='%s:%s'}",
                stepName, action, locatorStrategy, locatorValue);
    }
}
