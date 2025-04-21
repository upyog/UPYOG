package org.egov.pt.calculator.web.models;

import java.math.BigDecimal;
import java.util.List;

import org.apache.kafka.common.protocol.types.Field.Bool;
import org.egov.pt.calculator.web.models.property.Unit;
import org.egov.pt.calculator.web.models.property.Vacantland;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Calculation
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonPropertyOrder({"tenantId","totalAmount","taxAmount","exemption","rebate","exemption","penalty"})
public class Calculation   {
	
        @JsonProperty("serviceNumber")
        private String serviceNumber;

        @JsonProperty("fromDate")
        private Long fromDate;

        @JsonProperty("toDate")
        private Long toDate;

        @JsonProperty("tenantId")
        private String tenantId;
        
        @JsonProperty("totalAmount")
        private BigDecimal totalAmount;
        
        @JsonProperty("taxAmount")
        private BigDecimal taxAmount; 

        @JsonProperty("penalty")
        private BigDecimal penalty;

        @JsonProperty("exemption")
        private BigDecimal exemption;

        @JsonProperty("rebate")
        private BigDecimal rebate;

        List<TaxHeadEstimate> taxHeadEstimates;

        @JsonProperty("billingSlabIds")
        private List<String> billingSlabIds;
        
        private List<Unit> units;
        
        private BigDecimal remainAdvanceamount;
        
        private List<Vacantland> vacantland;
        
        private List<ModeOfPaymentDetails> modeOfPaymentDetails;
        
        private BigDecimal PropertyTaxValue;
        
        private BigDecimal MunicipalLowerLimit;
}

