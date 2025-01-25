package org.egov.pt.web.contracts;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.Property;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Contains the ResponseHeader and the created/updated property
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PropertyResponse {

	@JsonProperty("ResponseInfo")
	private ResponseInfo responseInfo;

	@JsonProperty("Properties")
	private List<Property> properties;

	@JsonProperty("count")
	private Integer count;

	@Builder.Default
	@JsonProperty("applicationInitiated")
	private Integer applicationInitiated = 0;
	
	@Builder.Default
	@JsonProperty("applicationPendingForVerification")
	private Integer applicationPendingForVerification = 0;
	
	@Builder.Default
	@JsonProperty("applicationPendingForModification")
	private Integer applicationPendingForModification = 0;
	
	@Builder.Default
	@JsonProperty("applicationPendingForApproval")
	private Integer applicationPendingForApproval = 0;
	
	@Builder.Default
	@JsonProperty("applicationApproved")
	private Integer applicationApproved = 0;
	
	@Builder.Default
	@JsonProperty("applicationRejected")
	private Integer applicationRejected = 0;
	
}
