package org.egov.bpa.web.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Validated
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PreapprovedPlan {

	@JsonProperty("id")
	private String id = null;

	@JsonProperty("drawingNo")
	private String drawingNo = null;

	@JsonProperty("plotLength")
	private BigDecimal plotLength = null;

	@JsonProperty("plotWidth")
	private BigDecimal plotWidth = null;

	@JsonProperty("roadWidth")
	private BigDecimal roadWidth = null;
	
	
	@JsonProperty("plotLengthInFeet")
	private BigDecimal plotLengthInFeet = null;

	@JsonProperty("plotWidthInFeet")
	private BigDecimal plotWidthInFeet = null;

   

	@JsonProperty("drawingDetail")
	private Object drawingDetail = null;

	@JsonProperty("active")
	private boolean active;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
	
	@JsonProperty("documents")
	@Valid
	private List<Document> documents = null;
	
	@JsonProperty("preApprovedCode")
	private String preApprovedCode = null;

	public PreapprovedPlan addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<Document>();
		}
		this.documents.add(documentsItem);
		return this;
	}
}
