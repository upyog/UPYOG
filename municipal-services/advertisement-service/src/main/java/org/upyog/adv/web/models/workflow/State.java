package org.upyog.adv.web.models.workflow;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class State {

  @JsonProperty("applicationStatus")
  private String applicationStatus;

  @JsonProperty("state")
  private String state;

  @JsonProperty("isStateUpdatable")
  private Boolean isStateUpdatable;

  @JsonProperty("isTerminateState")
  private Boolean isTerminateState;

  @JsonProperty("actions")
  private List<Action> actions;
}
