package org.egov.ewst.models;

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
public class EwasteApplicationSearchCriteria {
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("requestStatus")
	private String requestStatus;

	@JsonProperty("ids")
	private List<String> ids;

	@JsonProperty("requestId")
	private String requestId;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("fromDate")
	private String fromDate;

	@JsonProperty("toDate")
	private String toDate;

	private String userUuidString;
}
