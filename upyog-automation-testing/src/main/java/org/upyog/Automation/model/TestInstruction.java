package org.upyog.Automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents a single test step instruction within a module workflow.
 * All fields are populated dynamically from JSON configuration files.
 *
 * <p>The framework uses this model to determine:</p>
 * <ul>
 *     <li>Which element to locate ({@code locatorStrategy} + {@code locatorValue})</li>
 *     <li>What action to perform ({@code action})</li>
 *     <li>What data to input ({@code inputValue})</li>
 *     <li>How long to wait after executing the action ({@code dynamicSleep})</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestInstruction {

    /**
     * Human-readable name or label describing this test step.
     */
    @JsonProperty("stepName")
    private String stepName;

    /**
     * Locator strategy used to find the web element (e.g., "XPATH", "CSS", "ID", "NAME").
     */
    @JsonProperty("locatorStrategy")
    private String locatorStrategy;

    /**
     * Locator value or expression corresponding to the locator strategy.
     */
    @JsonProperty("locatorValue")
    private String locatorValue;

    /**
     * Action type to perform (e.g., "TYPE", "CLICK", "CLICK_JS", "UPLOAD_FILE", "SELECT_DROPDOWN_BY_INDEX").
     */
    @JsonProperty("action")
    private String action;

    /**
     * Input value, data store key, or parameter supplied to the action.
     */
    @JsonProperty("inputValue")
    private String inputValue;

    /**
     * Optional sleep/wait duration in milliseconds applied after executing this step.
     */
    @JsonProperty("dynamicSleep")
    private long dynamicSleep;

}

