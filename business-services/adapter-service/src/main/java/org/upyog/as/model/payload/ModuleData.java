package org.upyog.as.model.payload;

import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ModuleData {
	@com.fasterxml.jackson.annotation.JsonProperty("module")
	private String module;
	private String date;
	private String ward;
	private String ulb;
	private String region;
	private String state;
	private Map<String, Object> metrics;
}
