package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

/**
 * Contract class to receive request. Array of items are used in case of create,
 * whereas single item is used for update
 */
<<<<<<< HEAD
@Schema(description = "Contract class to receive request. Array of items are used in case of create, whereas single item is used for update")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
=======
@ApiModel(description = "Contract class to receive request. Array of items are used in case of create, whereas single item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo = null;

    @JsonProperty("Asset")
    @Valid
    private Asset asset = null;

}
