package org.egov.garbageservice.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.egov.garbageservice.contract.bill.Bill;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GarbageAccountDetail {

	private String applicationNumber;
    private List<String> action;
    private BigDecimal totalPayableAmount;
	private String feeCalculationFormula;
	private Map<Object, Object> billDetails;
	private Map<Object, Object> userDetails;
	private GarbageAccount garbageAccount;
	private List<Bill> bills;
	
}
