package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResponse {

  @JsonProperty("responseInfo")
  private ResponseInfo responseInfo = null;

  @JsonProperty("tests")
  @Valid
  private List<Test> tests = null;

  @JsonProperty("pagination")
  private Pagination pagination = null;

  @JsonProperty("workflow")
  private Workflow workflow = null;
}
