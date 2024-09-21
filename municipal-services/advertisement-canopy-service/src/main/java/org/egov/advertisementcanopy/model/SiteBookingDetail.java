package org.egov.advertisementcanopy.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.egov.advertisementcanopy.contract.bill.Bill;

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
public class SiteBookingDetail {

	private String applicationNumber;
    private List<String> action;
    private BigDecimal totalPayableAmount;
	private String feeCalculationFormula;
	private Map<Object, Object> billDetails;
	private Map<Object, Object> userDetails;
	private SiteBooking garbageAccount;
	private List<Bill> bills;
	
}
