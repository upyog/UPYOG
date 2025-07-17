package org.egov.demand.model;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.demand.validation.SanitizeHtml;

/**
 * A object holds a demand and collection values for a tax head and period.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DemandDetail   {

        @SanitizeHtml
        @JsonProperty("id")
        private String id;

        @SanitizeHtml
        @JsonProperty("demandId")
        private String demandId;

        @SanitizeHtml
        @NotNull @JsonProperty("taxHeadMasterCode")
        private String taxHeadMasterCode;

        @NotNull @JsonProperty("taxAmount")
        private BigDecimal taxAmount;

        @NotNull @JsonProperty("collectionAmount") @Default 
        private BigDecimal collectionAmount = BigDecimal.ZERO;

        @JsonProperty("additionalDetails")
        private Object additionalDetails;

        @SanitizeHtml
        @JsonProperty("auditDetails")
        private AuditDetails auditDetails;

        @SanitizeHtml
        @JsonProperty("tenantId")
        private String tenantId;
}
