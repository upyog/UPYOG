package org.egov.asset.web.models.disposal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.asset.web.models.AuditDetails;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class AssetDisposal {

    @Id
    @JsonProperty("disposalId")
    private String disposalId;

    @JsonProperty("assetId")
    private String assetId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("lifeOfAsset")
    private Long lifeOfAsset;

    @JsonProperty("currentAgeOfAsset")
    private Long currentAgeOfAsset;

    @JsonProperty("isAssetDisposedInFacility")
    private Boolean isAssetDisposedInFacility;

    @JsonProperty("disposalDate")
    private Long disposalDate;

    @JsonProperty("reasonForDisposal")
    private String reasonForDisposal;

    @JsonProperty("amountReceived")
    private double amountReceived;

    @JsonProperty("purchaserName")
    private String purchaserName;

    @JsonProperty("paymentMode")
    private String paymentMode;

    @JsonProperty("receiptNumber")
    private String receiptNumber;

    @JsonProperty("comments")
    private String comments;

    @JsonProperty("glCode")
    private String glCode;

//    @JsonProperty("additionalDetails")
//    private Object additionalDetails;

    @JsonIgnore
    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
