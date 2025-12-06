package org.egov.applyworkflow.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {

    @JsonProperty("sla")
    private Long sla;

    @JsonProperty("state")
    private String state;

    @Size(max=256)
    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("docUploadRequired")
    private Boolean docUploadRequired;

    @JsonProperty("isStartState")
    private Boolean isStartState;

    @JsonProperty("isTerminateState")
    private Boolean isTerminateState;

    @JsonProperty("isStateUpdatable")
    private Boolean isStateUpdatable;

    @JsonProperty("actions")
    @Valid
    private List<Action> actions;
}