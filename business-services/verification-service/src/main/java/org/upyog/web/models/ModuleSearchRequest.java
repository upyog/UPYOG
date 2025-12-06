package org.upyog.web.models;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contract class to receive request. Array of items are used in case of create,
 * whereas single item is used for update
 */
@Validated
<<<<<<< HEAD
@Schema(description = "Contract class to receive request. Array of items are used in case of create, whereas single item is used for update")
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")
=======
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ModuleSearchRequest {
<<<<<<< HEAD
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo = null;

	@JsonProperty("ModuleSearchCriteria")
	@Valid
	private ModuleSearchCriteria moduleSearchCriteria = null;
=======
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("ModuleSearchCriteria")
    @Valid
    private ModuleSearchCriteria moduleSearchCriteria = null;
>>>>>>> master-LTS


}
