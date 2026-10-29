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
public class Action {

  @JsonProperty("uuid")
  private String uuid;

  @JsonProperty("tenantId")
  private String tenantId;

  @JsonProperty("currentState")
  private String currentState;

  @JsonProperty("action")
  private String actionCode;

  @JsonProperty("nextState")
  private String nextState;

  @JsonProperty("roles")
  private List<String> roles;

  @JsonProperty("active")
  private Boolean active;
}
