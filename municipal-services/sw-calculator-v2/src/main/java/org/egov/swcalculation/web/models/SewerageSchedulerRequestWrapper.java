package org.egov.swcalculation.web.models;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SewerageSchedulerRequestWrapper {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("ScheduledFor")
	private SchedulerLevel schedulerLevel;
	
	@JsonProperty("TenantId")
	private String tenantId;
}
