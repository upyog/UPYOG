package org.egov.pg.web.models;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.egov.pg.models.Transaction;

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
public class TransactionResponseV2 {

	@JsonProperty("ResponseInfo")
	@Valid
	private ResponseInfo responseInfo;

	@JsonProperty("Transactions")
	@Valid
	private List<Transaction> transactions;

	private BigDecimal totalPayableAmount;
	private String callbackUrl;
	private List<String> orderIdArray;
	private List<String> consumerCodeArray;
	private User user;
}
