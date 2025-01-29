package org.egov.ptr.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A Object holds the basic data for a Pet Registration Application
 */
@ApiModel(description = "A Object holds the basic data for a Pet Registration Application")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-08-20T09:30:27.617+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PetRegistrationApplication {
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("applicationNumber")
	private String applicationNumber = null;

	@JsonProperty("applicantName")
	private String applicantName = null;

	@JsonProperty("fatherName")
	private String fatherName = null;

	@JsonProperty("mobileNumber")
	private String mobileNumber = null;

	@JsonProperty("emailId")
	private String emailId = null;

	@JsonProperty("address")
	private Address address = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("petDetails")
	private PetDetails petDetails = null;

	private PetRenewalAuditDetails renewalAuditDetails = null;

	@JsonProperty("applicationType")
	private String applicationType = null;

	@JsonProperty("validityDate")
	private Long validityDate = null;

	@JsonProperty("status")
	private String status = null;

	@JsonProperty("expireFlag")
	private Boolean expireFlag = null;

	@JsonProperty("petToken")
	private String petToken = null;

	@JsonProperty("previousApplicationNumber")
	private String previousApplicationNumber = null;

	@JsonProperty("propertyId")
	private String propertyId = null;

	@JsonProperty("documents")
	@Valid
	@DiffIgnore
	private List<Document> documents;

	@Valid
	@JsonProperty("workflow")
	private Workflow workflow = null;

	public PetRegistrationApplication addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}

		if (null != documentsItem)
			this.documents.add(documentsItem);
		return this;
	}
}
