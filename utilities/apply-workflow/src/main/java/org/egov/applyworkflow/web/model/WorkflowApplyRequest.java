package org.egov.applyworkflow.web.model;

import org.egov.common.contract.request.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;

import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Contract class to receive request. Array of items are used in case of create,
 * whereas single item is used for update
 */
@ApiModel(description = "Contract class to receive request. Array of items are used in case of create, whereas single item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowApplyRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo RequestInfo = null;

    @JsonProperty("BusinessService")
    @Valid
    private BusinessServiceDto BusinessServiceDto = null;

}
