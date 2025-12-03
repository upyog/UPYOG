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
	private List<String> applicationNumber;

	@JsonProperty("petRegistrationNumber")
	private List<String> petRegistrationNumber;

	@JsonProperty("mobileNumber")
	private String mobileNumber;

	@JsonProperty("fromDate")
	private String fromDate;

	@JsonProperty("toDate")
	private String toDate;

	@JsonProperty("petType")
	@Builder.Default
	private String petType = null;

	@JsonProperty("breedType")
	@Builder.Default
	private String breedType = null;

	@JsonProperty("validityDate")
	@Builder.Default
	private Long validityDate = null;

	@JsonProperty("expireFlag")
	@Builder.Default
	private Boolean expireFlag = null;

	@JsonProperty("applicationType")
	@Builder.Default
	private String applicationType = null;

	@JsonProperty("ownerUuids")
	private List<String> ownerUuids;

	@JsonProperty("limit")
	private Integer limit;

	@JsonProperty("offset")
	private Integer offset;

}