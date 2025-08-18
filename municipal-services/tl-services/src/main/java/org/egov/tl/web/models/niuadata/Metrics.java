package org.egov.tl.web.models.niuadata;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Metrics {
	private long transactions;
	private long todaysApplications;
	private long tlTax;
	private long adhocPenalty;
	private long adhocRebate;
	private long todaysLicenseIssuedWithinSLA;
	private long todaysApprovedApplications;
	private long pendingApplicationsBeyondTimeline;
	private long todaysApprovedApplicationsWithinSLA;
	private double avgDaysForApplicationApproval;
	@JsonProperty("StipulatedDays")
	private long stipulatedDays;
	private List<GroupedData> todaysCollection;
	private List<GroupedData> todaysTradeLicenses;
	private List<GroupedData> applicationsMovedToday;

}
