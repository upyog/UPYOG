package org.egov.vendor.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

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
public class VendorAdditionalDetailsRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("VendorAdditionalDetails")
    @Valid
    private VendorAdditionalDetails vendorAdditionalDetails = null;

}
