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
public class Action {

  @JsonProperty("uuid")
  private String uuid;

  @JsonProperty("tenantId")
  private String tenantId;

  @JsonProperty("currentState")
  private String currentState;

  @JsonProperty("action")
  private String action;

  @JsonProperty("nextState")
  private String nextState;

  @JsonProperty("roles")
  private List<String> roles;

  @JsonProperty("active")
  private Boolean active;
}
