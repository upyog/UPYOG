package org.egov.demand.web.contract;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.demand.model.BillList;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CancelBillCriteria {
	
	@JsonProperty("requestInfo")
	private RequestInfo RequestInfo;
	
	@JsonProperty("BillList")
	private List<BillList> BillList = new ArrayList<>();
	
	@JsonProperty("tenantId")
	private String tenantId;
	

}
