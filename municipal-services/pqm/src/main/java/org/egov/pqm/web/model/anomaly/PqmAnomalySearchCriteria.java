package org.egov.pqm.web.model.anomaly;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PqmAnomalySearchCriteria {

	@JsonProperty("ids")
	private List<String> ids = null;

	@JsonProperty("testIds")
	@Valid
	private List<String> testIds = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("anomalyType")
	private AnomalyType anomalyType = null;

	@JsonProperty("fromDate")
	private Long fromDate = null;

	@JsonProperty("toDate")
	private Long toDate = null;

	@JsonProperty("offset")
	private Integer offset;

	@JsonProperty("limit")
	private Integer limit;

}
