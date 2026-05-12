package org.egov.wscalculation.web.models;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.wscalculation.web.models.MeterReading.MeterStatusEnum;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * CalculationCriteria
 */
@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CalculationCriteria {

	@JsonProperty("waterConnection")
	private WaterConnection waterConnection;

	@JsonProperty("connectionNo")
	private String connectionNo;

	@JsonProperty("assessmentYear")
	private String assessmentYear;

	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("lastReading")
	private Double lastReading = null;

	@JsonProperty("currentReading")
	private Double currentReading = null;
	
	//Demand Generation
	@JsonProperty("from")
	private Long from;

	@JsonProperty("to")
	private Long to;
	
	
	//Fee Estimation
	@JsonProperty("applicationNo")
	private String applicationNo;
	
	private MeterStatusEnum meterStatus;
	
	// ✅ isBulkMeter: passed from MeterReading request to drive Reset consumption formula.
	// true  → use bulkMeterMaxReading from MDMS billingPeriod master
	// false → use meterMaxReading from MDMS billingPeriod master
	// null  → treated as false (normal meter)
	private Boolean isBulkMeter;
	
	@JsonIgnore
    private List<MeterReading> meterReadings;

    public List<MeterReading> getMeterReadings() {
        return meterReadings;
    }

    public void setMeterReadings(List<MeterReading> meterReadings) {
        this.meterReadings = meterReadings;
    }
	
	

}
