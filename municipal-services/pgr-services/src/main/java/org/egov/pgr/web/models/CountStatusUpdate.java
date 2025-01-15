package org.egov.pgr.web.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountStatusUpdate {

	private int pendingForAssignment;
	private int pendingForReAssignment;
	private int pendingAtLME;
	private int rejected;
	private int resolved;
	private int closedAfterRejection;
	private int closedAfterResolution;
	private String tenantId;
	private String serviceCode;
	private Object additionalDetails;
	private String dateRange;
	private Long fromDate;
	private Long endDate;
}
