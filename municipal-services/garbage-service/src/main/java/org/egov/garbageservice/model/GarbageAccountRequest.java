package org.egov.garbageservice.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GarbageAccountRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	private List<GarbageAccount> garbageAccounts;

	@Builder.Default
	private Boolean fromMigration = false;

	@Builder.Default
	private Boolean createChildAccountOnly = false;

}
