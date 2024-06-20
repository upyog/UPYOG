package org.egov.web.models;

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
public class WMSPhysicalFinancialMilestoneApplication {
	
	@JsonProperty("milestone_id")
	private String milestoneId=null;
	@JsonProperty("project_name")
	private String projectName=null;
	@JsonProperty("work_name")
	private String workName=null;
	@JsonProperty("milestone_name")
	private String milestoneName=null;
	@JsonProperty("physicalMileStoneActivity")
	private PhysicalMileStoneActivity physicalMileStoneActivity=null;
	//@JsonProperty("milestone_percentage")
		//private String milestonePercentage=null;
	//@JsonProperty("sr_no")
	//private Integer srNo=null;
	//@JsonProperty("activity_description")
	//private String activityDescription=null;
	/*
	 * @JsonProperty("percentage_weightage") private String
	 * percentageWeightage=null;
	 */
	//@JsonProperty("planned_start_date")
	//private String plannedStartDate=null;
	//@JsonProperty("planned_end_date")
	//private String plannedEndDate=null;
	/*
	 * @JsonProperty("total_weightage") private Integer totalWeightage=null;
	 * 
	 * @JsonProperty("milestone_description") private String
	 * milestoneDescription=null;
	 * 
	 * @JsonProperty("actual_start_date") private String actualStartDate=null;
	 * 
	 * @JsonProperty("actual_end_date") private String actualEndDate=null;
	 * 
	 * @JsonProperty("progress_update_date") private String progressUpdateDate=null;
	 * 
	 * @JsonProperty("completed_percentage") private String
	 * completedPercentage=null;
	 */
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	@JsonProperty("tenantId")
	private String tenantId = null;
	
}
