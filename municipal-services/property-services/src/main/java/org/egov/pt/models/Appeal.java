package org.egov.pt.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.egov.pt.models.enums.CreationReason;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.workflow.ProcessInstance;
import org.hibernate.validator.constraints.SafeHtml;
import org.javers.core.metamodel.annotation.DiffIgnore;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Appeal {
	
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("propertyId")
	private String propertyId;
	
	@JsonProperty("documents")
	@Valid
	@DiffIgnore
	private List<Document> documents;
	
	@JsonProperty("tenantId")
	private String tenantId;
	
	
	@JsonProperty("acknowldgementNumber")
	@SafeHtml
	private String acknowldgementNumber;
	
	@JsonProperty("creationReason")
	@NotNull(message="The value provided is either Invald or null")
	private CreationReason creationReason;
	
	
	@JsonProperty("workflow")
	@DiffIgnore
	private ProcessInstance workflow;
	
	@JsonProperty("status")
	private Status status;
	
	
	@JsonProperty("appealId")
	@SafeHtml
	private String appealId;
	
	@JsonProperty("propertyAddress")
	private String propertyaddress;
	
	@JsonProperty("assessmentYear")
	private String assesmnetyear;
	
	@JsonProperty("nameOfAssessingOfficer")
	private String nameofassigningofficer;
	
	@JsonProperty("assessingOfficerDesignation")
	private String designation;
	
	@JsonProperty("ruleUnderOrderPassed")
	private String ruleunderorderpassed;
	
	@JsonProperty("dateOfOrder")
	private String dateoforder;
	
	@JsonProperty("dateOfService")
	private String dateofservice;
	
	@JsonProperty("dateOfPayment")
	private String dateofpayment;
	
	@JsonProperty("ownerName")
	private String ownername;
	
	@JsonProperty("applicantAddress")
	private String applicantaddress;
	
	@JsonProperty("reliefClaimedInAppeal")
	private String reliefclaimed;
	
	@JsonProperty("statementOfFacts")
	private String statementoffacts;
	
	@JsonProperty("groundOfAppeal")
	private String groundofappeal;
	
	public Appeal addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}

		if (null != documentsItem)
			this.documents.add(documentsItem);
		return this;
	}
	
	

}
