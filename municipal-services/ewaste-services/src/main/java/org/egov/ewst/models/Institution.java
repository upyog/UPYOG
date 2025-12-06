package org.egov.ewst.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
<<<<<<< HEAD
=======
import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS

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

<<<<<<< HEAD
	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("type")
	private String type;

	@JsonProperty("designation")
	private String designation;

=======
	@SafeHtml
	@JsonProperty("id")
	private String id;

	@SafeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@SafeHtml
	@JsonProperty("name")
	private String name;

	@SafeHtml
	@JsonProperty("type")
	private String type;

	@SafeHtml
	@JsonProperty("designation")
	private String designation;

	@SafeHtml
>>>>>>> master-LTS
	@JsonProperty("nameOfAuthorizedPerson")
	private String nameOfAuthorizedPerson;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;
}
