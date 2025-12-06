package org.egov.applyworkflow.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @JsonProperty("action")
    private String action;

    @JsonProperty("currentState")
    private String currentState;

    @JsonProperty("nextState")
    private String nextState;

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty("active")
    private Boolean active;
}