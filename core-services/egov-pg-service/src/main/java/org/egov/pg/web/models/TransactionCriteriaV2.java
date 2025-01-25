package org.egov.pg.web.models;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TransactionCriteriaV2 {

	private Set<String> tenantIds;

	private Set<String> txnIds;

	private Set<String> billIds;

	private Set<String> userUuids;

	private Set<String> receipts;

	private Set<String> consumerCodes;

	@JsonIgnore
	private Long createdTime;

	private Set<String> txnStatus;

	@JsonIgnore
	@Builder.Default
	private int limit = 100;

	@JsonIgnore
	@Builder.Default
	private int offset = 0;

}
