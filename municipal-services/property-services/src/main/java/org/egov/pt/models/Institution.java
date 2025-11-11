package org.egov.pt.models;

import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Institution
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Institution {

	@CustomSafeHtml
	@JsonProperty("id")
	private String id;

	@CustomSafeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@CustomSafeHtml
	@JsonProperty("name")	
	private String name;

	@CustomSafeHtml
	@JsonProperty("type")
	private String type;

	@CustomSafeHtml
	@JsonProperty("designation")
	private String designation;

	@CustomSafeHtml
	@JsonProperty("nameOfAuthorizedPerson")
	private String nameOfAuthorizedPerson;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;
}
