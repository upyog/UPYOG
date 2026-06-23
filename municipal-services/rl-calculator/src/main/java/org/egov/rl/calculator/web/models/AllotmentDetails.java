package org.egov.rl.calculator.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.egov.rl.calculator.web.models.property.AuditDetails;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AllotmentDetails {
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("demandId")
	private String demandId;

	@JsonProperty("status")
	private String status;
	
	@JsonProperty("applicationNumber")
	private String applicationNumber;
	
	@JsonProperty("previousApplicationNumber")
	private String previousApplicationNumber;

	@JsonProperty("applicationType")
	private String applicationType;

	/* ------------Allotment Section---------------------- */
	@JsonProperty("startDate")
	private Long startDate;

	@JsonProperty("endDate")
	private Long endDate;
	
	@JsonProperty("isGSTApplicable")
	private boolean isGSTApplicable;
	
	@JsonProperty("isCowCessApplicable")
	private boolean isCowCessApplicable;
	
	@JsonProperty("isRefundApplicableOnDiscontinuation")
	private boolean isRefundApplicableOnDiscontinuation;

	
	@JsonProperty("termAndCondition")
	private String termAndCondition;;
	
	@JsonProperty("penaltyType")
	private String penaltyType;;
	
	@JsonProperty("witnessDetails")
	private String witnessDetails;
	
	@JsonProperty("createdTime")
	private long createdTime;
	
	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("Document")
	private List<Document> documents;
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
	@JsonProperty("OwnerInfo")
    private List<OwnerInfo> ownerInfo;
	
	@JsonProperty("additionalDetails")
    private JsonNode additionalDetails;
	
	@JsonProperty("workflow")
	private Workflow workflow;
	
	// closure
	@JsonProperty("reasonForClosure")
	private String reasonForClosure;
	
	@JsonProperty("amountToBeDeducted")
	private BigDecimal amountToBeDeducted;
	
	@JsonProperty("notesComments")
	private String notesComments;
	
	@JsonProperty("amountToBeRefund")
	private BigDecimal amountToBeRefund;
	
	@JsonProperty("registrationNumber")
	private String registrationNumber;
	
	@JsonProperty("tradeLicenseNumber")
	private String tradeLicenseNumber;
	
	public AllotmentDetails addOwnersItem(OwnerInfo ownersItem) {
		if (this.ownerInfo == null) {
			this.ownerInfo = new ArrayList<>();
		}

		if (null != ownersItem)
			this.ownerInfo.add(ownersItem);
		return this;
	}
	
	public AllotmentDetails addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}

		if (null != documentsItem)
			this.documents.add(documentsItem);
		return this;
	}
	
	public Long getStartDate() {
		return startDate != null ? startDate : -2208988800000L;
	}

	public Long getEndDate() {
		return endDate != null ? endDate : 32503680000000L;
	}

	@Override
	public String toString() {
	    return "AllotmentDetails{" +
	            "id='" + id + '\'' +
	            ", propertyId='" + propertyId + '\'' +
	            ", tenantId='" + tenantId + '\'' +
	            ", status=" + status +
	            ", applicationNumber='" + applicationNumber + '\'' +
	            ", previousApplicationNumber='" + previousApplicationNumber + '\'' +
	            ", applicationType='" + applicationType + '\'' +
	            ", startDate=" + startDate +
	            ", endDate=" + endDate +
	            ", isGSTApplicable=" + isGSTApplicable +
	            ", isCowCessApplicable=" + isCowCessApplicable +
	            ", isRefundApplicableOnDiscontinuation='" + isRefundApplicableOnDiscontinuation + '\'' +
	            ", termAndCondition='" + termAndCondition + '\'' +
	            ", penaltyType='" + penaltyType + '\'' +
	            ", witnessDetails=" + witnessDetails +
	            ", createdTime=" + createdTime +
	            ", createdBy='" + createdBy + '\'' +
	            ", documents=" + documents +
	            ", auditDetails=" + auditDetails +
	            ", ownerInfo=" + ownerInfo +
	            ", additionalDetails=" + additionalDetails +
	            ", workflow=" + workflow +
	            ", reasonForClosure=" + reasonForClosure +
	            ", amountToBeDeducted=" + amountToBeDeducted +
	            ", notesComments=" + notesComments +
	            ", amountToBeRefund=" + amountToBeRefund +
	            ", registrationNumber=" + registrationNumber +
	            ", tradeLicenseNumber=" + tradeLicenseNumber +
	            '}';
	}
}