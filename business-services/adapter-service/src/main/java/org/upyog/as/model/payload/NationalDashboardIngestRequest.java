package org.upyog.as.model.payload;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NationalDashboardIngestRequest {
    @JsonProperty("RequestInfo")
	private RequestInfo RequestInfo;
    @JsonProperty("Data")
	private List<ModuleData> Data;
}
