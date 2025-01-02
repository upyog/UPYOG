package org.egov.asset.calculator.web.models.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;

/**
 * An object representing an asset
 */
@ApiModel(description = "Object representing an asset")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "eg_asset_assetdetails")
public class Asset {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    @JsonProperty("id")
    private String id;

    @Column(name = "tenantid", length = 256)
    @JsonProperty("tenantId")
    private String tenantId;

    @Column(name = "bookrefno", length = 256, nullable = false)
    @JsonProperty("assetBookRefNo")
    private String assetBookRefNo;

    @Column(name = "name", length = 256, nullable = false)
    @JsonProperty("assetName")
    private String assetName;

    @Column(name = "description", columnDefinition = "text")
    @JsonProperty("description")
    private String description;

    @Column(name = "classification", length = 256)
    @JsonProperty("assetClassification")
    private String assetClassification;

    @Column(name = "parentcategory", length = 256)
    @JsonProperty("assetParentCategory")
    private String assetParentCategory;

    @Column(name = "category", length = 256)
    @JsonProperty("assetCategory")
    private String assetCategory;

    @Column(name = "subcategory", length = 256)
    @JsonProperty("assetSubCategory")
    private String assetSubCategory;

    @Column(name = "department", length = 256)
    @JsonProperty("department")
    private String department;

    @Column(name = "applicationno", length = 64)
    @JsonProperty("applicationNo")
    private String applicationNo;

    @Column(name = "approvalno", length = 64)
    @JsonProperty("approvalNo")
    private String approvalNo;

    @Column(name = "approvaldate")
    @JsonProperty("approvalDate")
    private Long approvalDate;

    @Column(name = "applicationdate")
    @JsonProperty("applicationDate")
    private Long applicationDate;

    @Column(name = "status", length = 64)
    @JsonProperty("status")
    private String status;

    @Column(name = "businessservice", length = 64)
    @JsonProperty("businessService")
    private String businessService;

    @Transient
    @Column(name = "additionaldetails", columnDefinition = "jsonb")
    @JsonProperty("additionalDetails")
    private Object additionalDetails;

    @Column(name = "accountid", length = 64)
    @JsonProperty("accountId")
    private String accountId;

    @Column(name = "remarks", length = 256)
    @JsonProperty("remarks")
    private String remarks;

    @Column(name = "financialyear", length = 64)
    @JsonProperty("financialYear")
    private String financialYear;

    @Column(name = "sourceoffinance", length = 256)
    @JsonProperty("sourceOfFinance")
    private String sourceOfFinance;

    @Column(name = "scheme", length = 256)
    @JsonProperty("scheme")
    private String scheme;

    @Column(name = "subscheme", length = 256)
    @JsonProperty("subScheme")
    private String subScheme;

    @Column(name = "purchasecost")
    @JsonProperty("purchaseCost")
    private double purchaseCost;

    @Column(name = "acquisitioncost")
    @JsonProperty("acquisitionCost")
    private double acquisitionCost;

    @Column(name = "bookvalue")
    @JsonProperty("bookValue")
    private double bookValue;

    @Column(name = "invoicedate")
    @JsonProperty("invoiceDate")
    private Long invoiceDate;

    @Column(name = "invoicenumber", length = 64)
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @Column(name = "purchasedate")
    @JsonProperty("purchaseDate")
    private Long purchaseDate;

    @Column(name = "purchaseordernumber", length = 64)
    @JsonProperty("purchaseOrderNumber")
    private String purchaseOrderNumber;

    @Column(name = "location", length = 256)
    @JsonProperty("location")
    private String location;

    @Column(name = "oldcode")
    @JsonProperty("oldCode")
    private Long oldCode;

    @Column(name = "modeofpossessionoracquisition", length = 256)
    @JsonProperty("modeOfPossessionOrAcquisition")
    private String modeOfPossessionOrAcquisition;

    @Column(name = "unitofmeasurement")
    @JsonProperty("unitOfMeasurement")
    private Long unitOfMeasurement;

    @Column(name = "lifeofasset", length = 64)
    @JsonProperty("lifeOfAsset")
    private String lifeOfAsset;

    @Column(name = "assetusage", length = 256)
    @JsonProperty("assetUsage")
    private String assetUsage;

    @Column(name = "assettype", length = 256)
    @JsonProperty("assetType")
    private String assetType;

    @Column(name = "assetstatus")
    @JsonProperty("assetStatus")
    private Long assetStatus;

    @Column(name = "originalbookvalue")
    @JsonProperty("originalBookValue")
    private double originalBookValue;

    @Column(name = "minimumvalue")
    @JsonProperty("minimumValue")
    private double minimumValue;

    @Column(name = "islegacydata")
    @JsonProperty("isLegacyData")
    private Boolean isLegacyData;

    @Transient
    @JsonProperty("accumulatedDepreciation")
    private String accumulatedDepreciation;

//      @JsonProperty("parentAssetSpecificDetails")
//      private AssetParentAssetSpecificDetails parentAssetSpecificDetails = null;

    public Asset(String type, Object additionalInformation) {
        this.assetParentCategory = type;
        this.additionalDetails = additionalInformation;
    }


}

