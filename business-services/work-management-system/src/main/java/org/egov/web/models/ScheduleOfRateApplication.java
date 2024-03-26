package org.egov.web.models;

import java.util.Date;
import java.util.UUID;

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
public class ScheduleOfRateApplication {
	
	@JsonProperty("sor_id")
	private String sorId = null;
	
	@JsonProperty("tenantId")
    private String tenantId = null;

	@JsonProperty("sor_name")
	 private String sorName = null;
	
	@JsonProperty("start_date")
	 private String startDate = null;
	
	@JsonProperty("end_date")
	 private String endDate = null;
	
	@JsonProperty("chapter")
	 private String chapter = null;
	
	@JsonProperty("item_no")
	 private String itemNo = null;
	
	@JsonProperty("description_of_item")
	 private String descOfItem = null;
	
	@JsonProperty("unit")
	 private Integer unit = null;
	 
	 @JsonProperty("rate")
	 private Long rate = null;

	 @JsonProperty("auditDetails")
	 private AuditDetails auditDetails = null;
}
