package org.egov.web.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class WMSTenderEntryApplicationSearchCriteria {

	@JsonProperty("tender_id")
    private List<String> tenderId;
	
	@JsonProperty("department_name")
    private List<String> departmentName;
	
	@JsonProperty("resolution_no")
    private List<Integer> resolutionNo;
	
	@JsonProperty("prebid_meeting_date")
    private List<String> prebidMeetingDate;

}
