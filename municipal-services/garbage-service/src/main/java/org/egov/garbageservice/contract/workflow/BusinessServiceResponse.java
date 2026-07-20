package org.egov.garbageservice.contract.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import org.egov.common.contract.response.ResponseInfo;

//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
/**
 * Wrapper for workflow service responses when searching business service definitions.
 *
 * Behavior:
 * - Carries {@link org.egov.common.contract.response.ResponseInfo} and list of {@link BusinessService} definitions.
 * - Returned from {@link WorkflowService#businessServiceSearch} after GET on businessServices search API.
 * - {@link #addBusinessServiceItem(BusinessService)} appends a definition to the list.
 *
 * Notes:
 * - GarbageAccountService uses the first business service’s states to validate status transitions.
 * - JSON property name is {@code BusinessServices} (PascalCase).
 */
public class BusinessServiceResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("BusinessServices")
//    @Valid
//    @NotNull
    private List<BusinessService> businessServices;


    public BusinessServiceResponse addBusinessServiceItem(BusinessService businessServiceItem) {
        if (this.businessServices == null) {
            this.businessServices = new ArrayList<>();
        }
        this.businessServices.add(businessServiceItem);
        return this;
    }



}
