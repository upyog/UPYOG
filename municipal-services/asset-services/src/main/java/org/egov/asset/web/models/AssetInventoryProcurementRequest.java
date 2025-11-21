package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetInventoryProcurementRequest {

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("item")
    @NotBlank(message = "Item is mandatory")
    private String item;

    @JsonProperty("itemType")
    @NotBlank(message = "Item type is mandatory")
    private String itemType;

    @JsonProperty("quantity")
    @NotNull(message = "Quantity is mandatory")
    private Integer quantity;

    @JsonProperty("assetApplicationNumber")
    @NotBlank(message = "Asset application number is mandatory")
    private String assetApplicationNumber;

    @JsonProperty("tenantId")
    @NotBlank(message = "Tenant ID is mandatory")
    private String tenantId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("businessService")
    private String businessService;

    @JsonProperty("workflow")
    private Workflow workflow;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;
}