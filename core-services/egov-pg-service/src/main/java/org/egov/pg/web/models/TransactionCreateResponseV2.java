package org.egov.pg.web.models;

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
public class TransactionCreateResponseV2 {

	@JsonProperty("ResponseInfo")
	@Valid
	private ResponseInfo responseInfo;

	@JsonProperty("Transactions")
	@Valid
	private List<Transaction> transactions;
}
