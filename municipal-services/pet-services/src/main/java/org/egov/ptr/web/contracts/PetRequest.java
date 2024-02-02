package org.egov.ptr.web.contracts;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.models.PetRegistrationApplication;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Contract class to receive request. Array of Property items  are used in case of create . Where as single Property item is used for update
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetRequest   {
	
  @JsonProperty("RequestInfo")
  private RequestInfo requestInfo;

  @JsonProperty("PetApplications")
  @Valid
  private PetRegistrationApplication petApplication;
}
