package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

/**
 * Asset inventory details
 */
@ApiModel(description = "Asset inventory details")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssetInventory {

    @JsonProperty("inventoryId")
    private String inventoryId;

    @JsonProperty("assetId")
    private String assetId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("purchaseDate")
    private Long purchaseDate;

    @JsonProperty("purchaseMode")
    private String purchaseMode;

    @JsonProperty("vendorId")
    private String vendorId;

    @JsonProperty("vendorNumber")
    private String vendorNumber;

    @JsonProperty("deliveryDate")
    private Long deliveryDate;

    @JsonProperty("endOfLife")
    private Long endOfLife;

    @JsonProperty("endOfSupport")
    private Long endOfSupport;

    @JsonProperty("quantity")
    private Integer quantity;

    @JsonProperty("unitPrice")
    private BigDecimal unitPrice;

    @JsonProperty("totalPrice")
    private BigDecimal totalPrice;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("inventoryStatus")
    private String inventoryStatus;

    @JsonProperty("procurementRequestId")
    private String procurementRequestId;

    @JsonProperty("insuranceApplicability")
    private String insuranceApplicability; // YES or NO
}