package org.upyog.web.models;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel(description = "A Object holds the basic data for any Module")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommonDetails {

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("applicationNumber")
	private String applicationNumber = null;

	@JsonProperty("moduleName")
	private String moduleName = null;

	@JsonProperty("fromDate")
	private String fromDate = null;

	@JsonProperty("toDate")
	private String toDate = null;

	@JsonProperty("status")
	private String status = null;

	@JsonProperty("createdTime")
	private Long createdTime = null;

	@JsonProperty("address")
	private String address = null;
	
	@JsonProperty("name")
	private String name = null;
	
	@JsonProperty("mobileNumber")
	private String mobileNumber = null;

}
