package org.egov.ptr.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class PetApplicationSearchCriteria {

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("status")
	private String status;

	@JsonProperty("ids")
	private List<String> ids;

	@JsonProperty("applicationNumber")
	private String applicationNumber;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("fromDate")
	private String fromDate;

	@JsonProperty("toDate")
	private String toDate;

	@JsonProperty("petType")
	private String petType = null;

	@JsonProperty("breedType")
	private String breedType = null;

	@JsonProperty("createdBy")
	private String createdBy;

}