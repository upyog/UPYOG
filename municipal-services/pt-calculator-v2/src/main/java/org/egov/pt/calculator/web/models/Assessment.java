package org.egov.pt.calculator.web.models;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.pt.calculator.web.models.property.AuditDetails;
import org.egov.pt.calculator.web.models.property.Channel;
import org.egov.pt.calculator.web.models.propertyV2.DocumentV2;
import org.egov.pt.calculator.web.models.propertyV2.AssessmentV2.Source;
import org.egov.pt.calculator.web.models.propertyV2.UnitUsage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Assessment {

	@JsonProperty("id")
	private String id;

	@JsonProperty("tenantId")
	@NotNull
	private String tenantId;

	@JsonProperty("assessmentNumber")
	private String assessmentNumber;

	@JsonProperty("financialYear")
	@NotNull
	private String financialYear;

	@JsonProperty("propertyId")
	@NotNull
	private String propertyId;

	@JsonProperty("assessmentDate")
	@NotNull
	private Long assessmentDate;

	@JsonProperty("status")
	private Status status;

	@JsonProperty("source")
	private Source source;

	@JsonProperty("unitUsageList")
	@Valid
	private List<UnitUsage> unitUsageList;

	@JsonProperty("documents")
	@Valid
	private Set<DocumentV2> documents = new HashSet<>();

	@JsonProperty("additionalDetails")
	private JsonNode additionalDetails;

	@JsonProperty("channel")
	private Channel channel;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
	private String demandId;
	
	private String assessmentYear;
	
	private String uuid;

	public Assessment addDocumentsItem(DocumentV2 documentsItem) {
		if (this.documents == null) {
			this.documents = new HashSet<>();
		}
		this.documents.add(documentsItem);
		return this;
	}

}
