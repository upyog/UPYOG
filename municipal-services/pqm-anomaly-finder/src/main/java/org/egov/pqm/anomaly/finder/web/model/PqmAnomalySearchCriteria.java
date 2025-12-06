package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
