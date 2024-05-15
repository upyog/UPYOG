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
	
	@JsonProperty("propertyaddress")
	private String propertyaddress;
	
	@JsonProperty("assesmnetyear")
	private String assesmnetyear;
	
	@JsonProperty("nameofassigningofficer")
	private String nameofassigningofficer;
	
	@JsonProperty("designation")
	private String designation;
	
	@JsonProperty("ruleunderorderpassed")
	private String ruleunderorderpassed;
	
	@JsonProperty("dateoforder")
	private String dateoforder;
	
	@JsonProperty("dateofservice")
	private String dateofservice;
	
	@JsonProperty("dateofpayment")
	private String dateofpayment;
	
	@JsonProperty("copyofchallan")
	private String copyofchallan;
	
	@JsonProperty("applicantaddress")
	private String applicantaddress;
	
	@JsonProperty("reliefclaimed")
	private String reliefclaimed;
	
	@JsonProperty("statementoffacts")
	private String statementoffacts;
	
	@JsonProperty("groundofappeal")
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
