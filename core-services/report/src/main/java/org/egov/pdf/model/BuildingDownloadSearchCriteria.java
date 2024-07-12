package org.egov.pdf.model;

import java.util.List;
import javax.validation.Valid;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Validated
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor

public class BuildingDownloadSearchCriteria {


	@SafeHtml
	@JsonProperty("applicationNo")
	private String applicationNo = null;

	@SafeHtml
	@JsonProperty("Status")
	private List<String> Status = null;

	@SafeHtml
	@JsonProperty("businessService")
	private String businessService = null;

	@SafeHtml
	@JsonProperty("applicationType")
	private String applicationType = null; 

}
