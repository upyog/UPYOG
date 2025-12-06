package org.egov.pqm.anomaly.finder.web.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("tests")
	private List<Test> tests;
}
