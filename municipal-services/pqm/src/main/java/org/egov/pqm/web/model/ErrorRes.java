package org.egov.pqm.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRes {

  @JsonProperty("ResponseInfo")
  private ResponseInfo responseHeader = null;

  @JsonProperty("Errors")
  @Valid
  private List<Error> errors = null;
}
