package org.egov.pg.web.models;

import java.util.Set;

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
public class TransactionDetailsCriteria {

	private Set<String> txnIds;

	private Set<String> billIds;

	private Set<String> consumerCodes;

}
