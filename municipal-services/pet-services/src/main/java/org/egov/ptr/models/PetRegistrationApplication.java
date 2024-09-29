package org.egov.ptr.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import org.egov.ptr.models.Address;
import org.egov.ptr.models.Applicant;
import org.egov.ptr.models.AuditDetails;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

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

	@JsonProperty("aadharNumber")
	private String aadharNumber = null;

	@JsonProperty("address")
	private Address address = null;

	@JsonProperty("applicant")
	private Applicant applicant = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	@JsonProperty("petDetails")
	private PetDetails petDetails = null;

	@JsonProperty("documents")
	@Valid
	@DiffIgnore
	private List<Document> documents;

	@Valid
	@JsonProperty("workflow")
	private Workflow workflow = null;
	
	private Boolean isOnlyWorkflowCall = false;

    private JsonNode additionalDetail = null;

	@JsonProperty("status")
	private String status;

	public PetRegistrationApplication addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}

		if (null != documentsItem)
			this.documents.add(documentsItem);
		return this;
	}
}
