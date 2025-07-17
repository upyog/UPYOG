package org.egov.pt.calculator.web.models.propertyV2;

import java.util.List;

import javax.validation.Valid;

import org.egov.pt.calculator.web.models.Address;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * This is lightweight property object that can be used as reference by
 * definitions needing property linking. Actual PropertyV2 Object extends this to
 * include more elaborate attributes of the property.
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyInfoV2 {

	public PropertyInfoV2(String id2, String propertyId2, String surveyId2, List<String> linkedProperties2,
			String tenantId2, String accountId2, String oldPropertyId2, String status2,
			org.egov.pt.calculator.web.models.property.Address addressV2) {
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("surveyId")
	private String surveyId;

	@JsonProperty("linkedProperties")
	@Valid
	private List<String> linkedProperties;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("accountId")
	private String accountId;

	@JsonProperty("oldPropertyId")
	private String oldPropertyId;

	@JsonProperty("status")
	private String status;

	@JsonProperty("address")
	private Address address;
}
