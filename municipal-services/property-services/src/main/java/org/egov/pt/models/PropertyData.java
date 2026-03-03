package org.egov.pt.models;

import java.util.List;

import javax.validation.Valid;

import org.egov.pt.models.collection.Payment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyData {
	
	private String propertyId;
	@JsonProperty("tenantId")
	private String tenantId;
	@JsonProperty("owners")
	@Valid
	private List<OwnerInfo> owners;
	@JsonProperty("address")
	private Address address;
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	@JsonProperty("Assessments")
	private List<AssessmentData> assessments;
	@JsonProperty("Payments")
	private List<Payment> payments;
	@JsonProperty("Appeals")
	private List<AppealData> appealDatas;

}
