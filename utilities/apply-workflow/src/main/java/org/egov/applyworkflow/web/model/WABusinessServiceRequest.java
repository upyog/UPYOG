package org.egov.applyworkflow.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.*;

/**
 * Contract class to receive request. Array of items are used in case of create,
 * whereas single item is used for update
 */
@Schema(description = "Contract class to receive request. Array of items are used in case of create, whereas single item is used for update")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Data
@ToString
public class WABusinessServiceRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("BusinessServices")
    @Valid
    @NotNull
    private List<BusinessService> businessServices;


    public WABusinessServiceRequest addBusinessServiceItem(BusinessService businessServiceItem) {
        if (this.businessServices == null) {
            this.businessServices = new ArrayList<>();
        }
        this.businessServices.add(businessServiceItem);
        return this;
    }

}
