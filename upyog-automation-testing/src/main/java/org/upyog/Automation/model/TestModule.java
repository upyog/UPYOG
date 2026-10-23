package org.upyog.Automation.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Represents a complete test module loaded dynamically from a JSON configuration file.
 *
 * <p>Encapsulates module metadata and the sequential list of {@link TestInstruction}
 * steps to be executed by the test engine.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TestModule {

    /**
     * Name or identifier of the test module.
     */
    @JsonProperty("moduleName")
    private String moduleName;

    /**
     * Description explaining the test module purpose and flow.
     */
    @JsonProperty("description")
    private String description;

    /**
     * Target application base URL for executing this module.
     */
    @JsonProperty("baseUrl")
    private String baseUrl;

    /**
     * Sequential list of test step instructions comprising this module.
     */
    @JsonProperty("instructions")
    private List<TestInstruction> instructions;

}

