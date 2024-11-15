package org.egov.pqm.web.model.idgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdGenerationResponse {

  private ResponseInfo responseInfo;

  private List<IdResponse> idResponses;

}
