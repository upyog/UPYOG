package org.egov.applyworkflow.web.model.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;
import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Contract class to receive request. Array of items are used in case of create,
 * whereas single item is used for update
 */
@ApiModel(description = "Contract class to receive request. Array of items are used in case of create, whereas single item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Data
@ToString
public class BusinessServiceRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("BusinessServices")
    @Valid
    @NotNull
    private List<BusinessService> businessServices;


    public BusinessServiceRequest addBusinessServiceItem(BusinessService businessServiceItem) {
        if (this.businessServices == null) {
            this.businessServices = new ArrayList<>();
        }
        this.businessServices.add(businessServiceItem);
        return this;
    }

}
