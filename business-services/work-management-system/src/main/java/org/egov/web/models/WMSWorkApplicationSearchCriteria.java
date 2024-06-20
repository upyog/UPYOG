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
public class WMSWorkApplicationSearchCriteria {

	@JsonProperty("work_id")
    private List<String> workId;
	
	@JsonProperty("work_no")
    private Integer workNo;

	
}
