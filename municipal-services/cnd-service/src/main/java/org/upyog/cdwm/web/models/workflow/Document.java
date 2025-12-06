package org.upyog.cdwm.web.models.workflow;

import java.util.Objects;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import org.upyog.cdwm.validation.SanitizeHtml;
=======

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.models.coremodels.AuditDetails;
<<<<<<< HEAD
=======
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * This object holds list of documents attached during the transaciton for a
 * property
 */
<<<<<<< HEAD

@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-23T05:54:07.373Z[GMT]")
=======
@ApiModel(description = "This object holds list of documents attached during the transaciton for a property")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-23T05:54:07.373Z[GMT]")
>>>>>>> master-LTS
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {
<<<<<<< HEAD
	@SanitizeHtml
	@JsonProperty("id")
	private String id = null;

	@SanitizeHtml
	@JsonProperty("documentType")
	private String documentType = null;

	@SanitizeHtml
	@JsonProperty("fileStoreId")
	private String fileStoreId = null;

	@SanitizeHtml
=======
	@SafeHtml
	@JsonProperty("id")
	private String id = null;

	@SafeHtml
	@JsonProperty("documentType")
	private String documentType = null;

	@SafeHtml
	@JsonProperty("fileStoreId")
	private String fileStoreId = null;

	@SafeHtml
>>>>>>> master-LTS
	@JsonProperty("documentUid")
	private String documentUid = null;

	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;

	public Document id(String id) {
		this.id = id;
		return this;
	}

	/**
	 * system id of the Document.
	 * 
	 * @return id
	 **/
<<<<<<< HEAD
	 @Schema(description = "system id of the Document.")
=======
	@ApiModelProperty(value = "system id of the Document.")
>>>>>>> master-LTS

	@Size(max = 64)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Document documentType(String documentType) {
		this.documentType = documentType;
		return this;
	}

	/**
	 * unique document type code, should be validated with document type master
	 * 
	 * @return documentType
	 **/
<<<<<<< HEAD
	 @Schema(description = "unique document type code, should be validated with document type master")
=======
	@ApiModelProperty(value = "unique document type code, should be validated with document type master")
>>>>>>> master-LTS

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public Document fileStoreId(String fileStoreId) {
		this.fileStoreId = fileStoreId;
		return this;
	}

	/**
	 * File store reference key.
	 * 
	 * @return fileStoreId
	 **/
<<<<<<< HEAD
	@Schema(description = "File store reference key.")
=======
	@ApiModelProperty(value = "File store reference key.")
>>>>>>> master-LTS

	public String getFileStoreId() {
		return fileStoreId;
	}

	public void setFileStoreId(String fileStoreId) {
		this.fileStoreId = fileStoreId;
	}

	public Document documentUid(String documentUid) {
		this.documentUid = documentUid;
		return this;
	}

	/**
	 * The unique id(Pancard Number,Adhar etc.) of the given Document.
	 * 
	 * @return documentUid
	 **/
<<<<<<< HEAD
	@Schema(description = "The unique id(Pancard Number,Adhar etc.) of the given Document.")
=======
	@ApiModelProperty(value = "The unique id(Pancard Number,Adhar etc.) of the given Document.")
>>>>>>> master-LTS

	@Size(max = 64)
	public String getDocumentUid() {
		return documentUid;
	}

	public void setDocumentUid(String documentUid) {
		this.documentUid = documentUid;
	}

	public Document additionalDetails(Object additionalDetails) {
		this.additionalDetails = additionalDetails;
		return this;
	}

	/**
	 * Json object to capture any extra information which is not accommodated by
	 * model
	 * 
	 * @return additionalDetails
	 **/
<<<<<<< HEAD
	@Schema(description = "Json object to capture any extra information which is not accommodated by model")
=======
	@ApiModelProperty(value = "Json object to capture any extra information which is not accommodated by model")
>>>>>>> master-LTS

	public Object getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(Object additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public Document auditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
		return this;
	}

	/**
	 * Get geoLocation
	 * 
	 * @return geoLocation
	 **/
<<<<<<< HEAD
	@Schema(description = "")
=======
	@ApiModelProperty(value = "")
>>>>>>> master-LTS

	@Valid
	public AuditDetails getAuditDetails() {
		return auditDetails;
	}

	public void setAuditDetails(AuditDetails auditDetails) {
		this.auditDetails = auditDetails;
	}

	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Document document = (Document) o;
		return Objects.equals(this.id, document.id) && Objects.equals(this.documentType, document.documentType)
				&& Objects.equals(this.fileStoreId, document.fileStoreId)
				&& Objects.equals(this.documentUid, document.documentUid)
				&& Objects.equals(this.additionalDetails, document.additionalDetails)
				&& Objects.equals(this.auditDetails, document.auditDetails);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, documentType, fileStoreId, documentUid, additionalDetails, auditDetails);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Document {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    documentType: ").append(toIndentedString(documentType)).append("\n");
		sb.append("    fileStoreId: ").append(toIndentedString(fileStoreId)).append("\n");
		sb.append("    documentUid: ").append(toIndentedString(documentUid)).append("\n");
		sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
		sb.append("    auditDetails: ").append(toIndentedString(auditDetails)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}
