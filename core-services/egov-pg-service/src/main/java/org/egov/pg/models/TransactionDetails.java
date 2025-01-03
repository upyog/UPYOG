package org.egov.pg.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TransactionDetails {

	private String uuid;

	private String txnId;

	private String txnAmount;

	private String billId;

	private String consumerCode;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}
