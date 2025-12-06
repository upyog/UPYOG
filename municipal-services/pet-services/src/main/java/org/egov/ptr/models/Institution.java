package org.egov.ptr.models;

<<<<<<< HEAD
import org.egov.ptr.validator.SanitizeHtml;
=======
import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS

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

	@SanitizeHtml
	@JsonProperty("id")
	private String id;

	@SanitizeHtml
	@JsonProperty("tenantId")
	private String tenantId;

	@SanitizeHtml
	@JsonProperty("name")
	private String name;

	@SanitizeHtml
	@JsonProperty("type")
	private String type;

	@SanitizeHtml
	@JsonProperty("designation")
	private String designation;

	@SanitizeHtml
	@JsonProperty("nameOfAuthorizedPerson")
	private String nameOfAuthorizedPerson;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;
}
