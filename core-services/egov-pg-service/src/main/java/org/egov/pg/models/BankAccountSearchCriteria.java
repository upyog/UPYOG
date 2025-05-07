package org.egov.pg.models;

import java.util.Set;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BankAccountSearchCriteria {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private Set<String> tenantIds;

	private Boolean active;

}
