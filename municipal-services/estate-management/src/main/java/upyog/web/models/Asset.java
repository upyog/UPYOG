package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.egov.common.contract.models.AuditDetails;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {
    private String assetId;
    private String estateNo;
    @NotBlank
    @Size(max = 100, message = "Tenant ID must be less than or equal to 100 characters")
    private String tenantId;
    @NotBlank
    @Size(max = 200, message = "Asset name must be less than or equal to 200 characters")
    private String buildingName;
    @NotBlank
    @Size(min=2, max = 100, message = "Building type must be between 2 and 100 characters")
    private String buildingNo;
    @NotBlank
    private Integer floor;
    @NotBlank
    @Size(max = 100, message = "Locality must be less than or equal to 100 characters")
    private String locality;
    @NotBlank
    @Size(max = 100, message = "Locality code must be less than or equal to 100 characters")
    private String localityCode;

    @NotBlank
    private BigDecimal totalFloorArea;
    @NotBlank
    private BigDecimal dimensionLength;
    @NotBlank
    private BigDecimal dimensionWidth;
    @NotBlank
    private BigDecimal rate;

    @JsonProperty("assetName")
    private String assetName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("assetClassification")
    private String assetClassification;

    @JsonProperty("assetParentCategory")
    private String assetParentCategory;

    @JsonProperty("assetCategory")
    private String assetCategory;

    @JsonProperty("assetSubCategory")
    private String assetSubCategory;

    @JsonProperty("department")
    private String department;


    @NotBlank
    @Size(max = 100, message = "Asset type must be less than or equal to 100 characters")
    private String assetType;
    @NotBlank
    private String assetStatus;
    //Rent,lease
    private String assetAllotmentType;
    //Vacant, Allotted, Expired
    private String assetAllotmentStatus;
    //Asset no from asset module else null
    private String refAssetNo;

    private AuditDetails auditDetails;

    private Object additionalDetails;
}