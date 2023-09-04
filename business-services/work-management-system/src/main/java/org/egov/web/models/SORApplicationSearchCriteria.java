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
public class SORApplicationSearchCriteria {

	@JsonProperty("sor_name")
    private String sorName;
	
	@JsonProperty("start_date")
    private String sorStartDate;
	
	@JsonProperty("end_date")
    private String sorEndDate;
	

}
