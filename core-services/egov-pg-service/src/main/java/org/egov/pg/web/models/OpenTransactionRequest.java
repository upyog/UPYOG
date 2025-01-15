package org.egov.pg.web.models;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;

import javax.validation.Valid;

import org.egov.pg.models.Transaction;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The payment object, containing all necessary information for initiating a
 * payment and the request body metadata
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-06-05T12:58:12.679+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OpenTransactionRequest {

	@JsonProperty("userUuid")
	@Valid
	private String userUuid;

	@JsonProperty("Transactions")
	@Valid
	private List<Transaction> transactions;

	public boolean isValid() {
		return isNotEmpty(userUuid);
	}

}
