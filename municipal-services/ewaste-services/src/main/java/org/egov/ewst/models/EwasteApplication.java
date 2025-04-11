package org.egov.ewst.models;

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
 * Represents the basic data for an E-waste Application.
 * This class contains details such as the application ID, tenant ID, request ID,
 * calculated amount, vendor UUID, pickup date, transaction ID, final amount,
 * request status, and associated details like applicant, address, and workflow.
 */
@ApiModel(description = "A Object holds the basic data for a E-waste Application")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-08-20T09:30:27.617+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EwasteApplication {

	@JsonProperty("id")
	private String id = null;

	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("requestId")
	private String requestId = null;

	@JsonProperty("calculatedAmount")
	private String calculatedAmount = null;

	@JsonProperty("vendorUuid")
	private String vendorUuid = null;

	@JsonProperty("pickUpDate")
	private String pickUpDate = null;

	@JsonProperty("transactionId")
	private String transactionId = null;

	@JsonProperty("finalAmount")
	private String finalAmount = null;

	@JsonProperty("requestStatus")
	private String requestStatus = null;

	@JsonProperty("ewasteDetails")
	private List<EwasteDetails> ewasteDetails = null;

	@JsonProperty("applicant")
	private Applicant applicant = null;

	@JsonProperty("address")
	private Address address = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("documents")
	@Valid
	@DiffIgnore
	private List<Document> documents;

	@Valid
	@JsonProperty("workflow")
	private Workflow workflow = null;

	/**
	 * Adds a document to the list of documents associated with the application.
	 *
	 * @param documentsItem the document to add
	 * @return the updated EwasteApplication object
	 */
	public EwasteApplication addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}

		if (null != documentsItem)
			this.documents.add(documentsItem);
		return this;
	}

	/**
	 * Adds an E-waste detail to the list of E-waste details associated with the application.
	 *
	 * @param ewasteDetails the E-waste detail to add
	 * @return the updated EwasteApplication object
	 */
	public EwasteApplication addEwasteDetailItem(EwasteDetails ewasteDetails) {
		if (this.ewasteDetails == null) {
			this.ewasteDetails = new ArrayList<>();
		}

		if (null != ewasteDetails)
			this.ewasteDetails.add(ewasteDetails);
		return this;
	}
}
