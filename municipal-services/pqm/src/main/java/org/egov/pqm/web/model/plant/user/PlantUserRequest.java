package org.egov.pqm.web.model.plant.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PlantUserRequest {

  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("plantUsers")
  private List<PlantUser> plantUsers;
}
