package org.egov.echallan.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

  @JsonProperty("applicationStatus")
  private String applicationStatus;

  @JsonProperty("state")
  private String stateCode;

  @JsonProperty("isStateUpdatable")
  private Boolean isStateUpdatable;

  @JsonProperty("isTerminateState")
  private Boolean isTerminateState;

  @JsonProperty("actions")
  private List<Action> actions;
}
