package org.egov.ptr.web.contracts;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.models.oldProperty.MigrationCount;

import javax.validation.Valid;

/**
 * Contract class to receive request. Array of Property items  are used in case of create . Where as single Property item is used for update
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyMigrationCountRequest {
	
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("MigrationCount")
  @Valid
  private MigrationCount migrationCount;
}
