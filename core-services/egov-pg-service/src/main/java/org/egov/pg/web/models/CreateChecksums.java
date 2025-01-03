package org.egov.pg.web.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
//import org.egov.pg.models.Transaction;
//import org.egov.pg.web.models.CheckSumTransactions;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChecksums {
	@JsonProperty("RequestInfo")
	@Valid
	private RequestInfo requestInfo;
	
	@JsonProperty("Transactions")
	@Valid
	private List<CheckSumTransaction> transactions;
	


}
