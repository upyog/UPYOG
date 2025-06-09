package org.egov.pt.models;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.pt.models.enums.BillStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PtTaxCalculatorTracker {

	private String uuid;
	private String propertyId;
	private String tenantId;
	private String financialYear;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date fromDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date toDate;
	private String fromDateString;
	private String toDateString;
	private BigDecimal propertyTax;
	private BigDecimal rebateAmount;
	private BigDecimal propertyTaxWithoutRebate;
	private BillStatus billStatus;
	private AuditDetails auditDetails;
	@JsonProperty("additionalDetails")
	private JsonNode additionalDetails;
	private String billId;
}
