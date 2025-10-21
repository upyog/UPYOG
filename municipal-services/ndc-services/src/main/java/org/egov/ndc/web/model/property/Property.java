package org.egov.ndc.web.model.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import org.egov.ndc.web.model.AuditDetails;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Property
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-05-11T14:12:44.497+05:30")
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Property extends PropertyInfo {


	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;


	public enum CreationReasonEnum {
		NEWPROPERTY("NEWPROPERTY"),

		SUBDIVISION("SUBDIVISION");

		private String value;

		CreationReasonEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return String.valueOf(value);
		}

		@JsonCreator
		public static CreationReasonEnum fromValue(String text) {
			for (CreationReasonEnum b : CreationReasonEnum.values()) {
				if (String.valueOf(b.value).equals(text)) {
					return b;
				}
			}
			return null;
		}
	}

	@JsonProperty("creationReason")
	private CreationReasonEnum creationReason;

	@JsonProperty("occupancyDate")
	private Long occupancyDate;

	@Valid
	@JsonProperty("propertyDetails")
	private List<PropertyDetail> propertyDetails;

	@JsonProperty("additionalDetails")
	private Object additionalDetails;


	public Property addpropertyDetailsItem(PropertyDetail propertyDetailsItem) {
		if (this.propertyDetails == null) {
			this.propertyDetails = new ArrayList<>();
		}
		this.propertyDetails.add(propertyDetailsItem);
		return this;
	}

	@Builder
	public Property(String propertyId, String tenantId, String acknowldgementNumber, String oldPropertyId, StatusEnum status, Address address, AuditDetails auditDetails, CreationReasonEnum creationReason, Long occupancyDate, List<PropertyDetail> propertyDetails,Object additionalDetails) {
		super(propertyId, tenantId, acknowldgementNumber, oldPropertyId, status, address);
		this.auditDetails = auditDetails;
		this.creationReason = creationReason;
		this.occupancyDate = occupancyDate;
		this.propertyDetails = propertyDetails;
		this.additionalDetails = additionalDetails;
	}

}





