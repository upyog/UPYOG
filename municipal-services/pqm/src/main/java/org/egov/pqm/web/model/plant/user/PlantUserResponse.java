package org.egov.pqm.web.model.plant.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pqm.web.model.Pagination;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlantUserResponse {

  @JsonProperty("responseInfo")
  private ResponseInfo responseInfo;

  @JsonProperty("plantUsers")
  @Valid
  private List<PlantUser> plantUsers;

  @JsonProperty("pagination")
  private Pagination pagination;

  @JsonProperty("totalCount")
  private Integer totalCount = 0;
}
