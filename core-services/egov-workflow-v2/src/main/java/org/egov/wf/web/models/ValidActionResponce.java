package org.egov.wf.web.models;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ValidActionResponce {
	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("nextValidAction")
	@Builder.Default
	private List<Action> nextValidAction = new ArrayList<>();

	@JsonProperty("action")
	@Builder.Default
	private List<String> action = new ArrayList<>();

	@JsonProperty("isUpdatable")
	@Builder.Default
	private Boolean isUpdatable = false;

	@JsonProperty("businessService")
	private String businessService;

	@JsonProperty("moduleName")
	private String moduleName;
}