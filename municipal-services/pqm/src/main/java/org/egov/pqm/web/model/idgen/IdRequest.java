package org.egov.pqm.web.model.idgen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Builder
public class IdRequest {

  @JsonProperty("idName")
  @NotNull
  private String idName;

  @NotNull
  @JsonProperty("tenantId")
  private String tenantId;

  @JsonProperty("format")
  private String format;


}
