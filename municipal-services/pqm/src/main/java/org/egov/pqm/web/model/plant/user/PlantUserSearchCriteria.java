package org.egov.pqm.web.model.plant.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantUserSearchCriteria {

  @JsonProperty("ids")
  @Valid
  private List<String> ids;

  @JsonProperty("tenantId")
  private String tenantId;

  @JsonProperty("plantCodes")
  @Valid
  private List<String> plantCodes;

  @JsonProperty("plantUserUuids")
  @Valid
  private List<String> plantUserUuids;
  
  @JsonProperty("isActive")
  private Boolean isActive;

  @JsonProperty("plantUserTypes")
  @Valid
  private List<String> plantUserTypes;

  @JsonProperty("fromDate")
  private Long fromDate;

  @JsonProperty("toDate")
  private Long toDate;
}
