package org.egov.feedback.model;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import com.fasterxml.jackson.databind.JsonNode;
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
public class FeedbackSearchRequest {
	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@JsonProperty("FeedbackSearchCriteria")
	private FeedbackSearchCriteria FeedbackSearchCriteria;

}
