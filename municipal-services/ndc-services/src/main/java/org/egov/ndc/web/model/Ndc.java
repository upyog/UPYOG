package org.egov.ndc.web.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.egov.ndc.web.model.enums.ApplicationType;
import org.egov.ndc.web.model.enums.Status;
import org.egov.ndc.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

/**
 * NDC applications object to capture the details of ndc related information, landid and related documents.
 */
@Schema(description = "NDC applications object to capture the details of ndc related information, landid and related documents.")
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-30T05:26:25.138Z[GMT]")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ndc {
  @SanitizeHtml
  @JsonProperty("id")
  private String id = null;

  @SanitizeHtml
  @JsonProperty("tenantId")
  private String tenantId = null;

  @SanitizeHtml
  @JsonProperty("applicationNo")
  private String applicationNo = null;

  @SanitizeHtml
  @JsonProperty("ndcNo")
  private String ndcNo = null;

  
  @JsonProperty("applicationType")
  private ApplicationType applicationType = null;

  @SanitizeHtml
  @JsonProperty("ndcType")
  private String ndcType = null;

  @SanitizeHtml
  @JsonProperty("accountId")
  private String accountId = null;

  @SanitizeHtml
  @JsonProperty("source")
  private String source = null;

  @SanitizeHtml
  @JsonProperty("sourceRefId")
  private String sourceRefId = null;

  @SanitizeHtml
  @JsonProperty("landId")
  private String landId = null;

  
  @JsonProperty("status")
  private Status status = null;

  @SanitizeHtml
  @JsonProperty("applicationStatus")
  private String applicationStatus = null;

  @JsonProperty("documents")
  @Valid
  private List<Document> documents = null;

  @JsonProperty("workflow")
  private Workflow workflow = null;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails = null;

  @JsonProperty("additionalDetails")
  private Object additionalDetails = null;

  public Ndc id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Unique Identifier(UUID) of the bpa applications for internal reference.
   * @return id
  **/
  @Schema(readOnly = true, description = "Unique Identifier(UUID) of the bpa applications for internal reference.")
  
  @Size(min=1,max=64)   public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Ndc tenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  /**
   * Unique ULB identifier.
   * @return tenantId
  **/
  @Schema(description= "Unique ULB identifier.")
  
  @Size(min=2,max=256)   public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public Ndc applicationNo(String applicationNo) {
    this.applicationNo = applicationNo;
    return this;
  }

  /**
   * Generate formatted Unique Identifier of the Ndc. Keep the format in mdms
   * @return applicationNo
  **/
  @Schema(readOnly = true, description = "Generate formatted Unique Identifier of the Ndc. Keep the format in mdms")
  
  @Size(min=1,max=64)   public String getApplicationNo() {
    return applicationNo;
  }

  public void setApplicationNo(String applicationNo) {
    this.applicationNo = applicationNo;
  }

  public Ndc ndcNo(String ndcNo) {
    this.ndcNo = ndcNo;
    return this;
  }

  /**
   * Generate Ndc number based on wf status. When to generate Ndcno will be depends on wf state so make it configurable at applications level
   * @return ndcNo
  **/
  @Schema(readOnly = true, description = "Generate Ndc number based on wf status. When to generate Ndcno will be depends on wf state so make it configurable at applications level")
  
  @Size(min=1,max=64)   public String getNdcNo() {
    return ndcNo;
  }

  public void setNdcNo(String ndcNo) {
    this.ndcNo = ndcNo;
  }

  public Ndc applicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
    return this;
  }

  /**
   * ndc applications type.
   * @return applicationType
  **/
  @Schema(description= "ndc applications type.")
  
    public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public Ndc ndcType(String ndcType) {
    this.ndcType = ndcType;
    return this;
  }

  /**
   * Mdms master data to configure types of ndc(ex:fire ndc, airport authority etc) 
   * @return ndcType
  **/
  @Schema(description = "Mdms master data to configure types of ndc(ex:fire ndc, airport authority etc) ")
  
  @Size(min=1,max=64)   public String getNdcType() {
    return ndcType;
  }

  public void setNdcType(String ndcType) {
    this.ndcType = ndcType;
  }

  public Ndc accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Initiator User UUID
   * @return accountId
  **/
  @Schema(description = "Initiator User UUID")
  
  @Size(min=1,max=64)   public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

  public Ndc source(String source) {
    this.source = source;
    return this;
  }

  /**
   * Who is creating the record in the system(ex:BPA,Property etc)
   * @return source
  **/
  @Schema(description= "Who is creating the record in the system(ex:BPA,Property etc)")
  
  @Size(min=1,max=64)   public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public Ndc sourceRefId(String sourceRefId) {
    this.sourceRefId = sourceRefId;
    return this;
  }

  /**
   * Unique Identifier of integrator(Source system) to link the ndc applications.
   * @return sourceRefId
  **/
  @Schema(description = "Unique Identifier of integrator(Source system) to link the ndc applications.")
  
  @Size(min=1,max=64)   public String getSourceRefId() {
    return sourceRefId;
  }

  public void setSourceRefId(String sourceRefId) {
    this.sourceRefId = sourceRefId;
  }

  public Ndc landId(String landId) {
    this.landId = landId;
    return this;
  }

  /**
   * Unique Identifier(UUID) of the land for internal reference.
   * @return landId
  **/
  @Schema(description = "Unique Identifier(UUID) of the land for internal reference.")
  
  @Size(min=1,max=64)   public String getLandId() {
    return landId;
  }

  public void setLandId(String landId) {
    this.landId = landId;
  }

  public Ndc status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * state of the record.
   * @return status
  **/
  @Schema(description = "state of the record.")
  
    public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Ndc applicationStatus(String applicationStatus) {
    this.applicationStatus = applicationStatus;
    return this;
  }

  /**
   * Application status should get populate from wf engine
   * @return applicationStatus
  **/
  @Schema(readOnly = true, description= "Application status should get populate from wf engine")
  
  @Size(min=1,max=64)   public String getApplicationStatus() {
    return applicationStatus;
  }

  public void setApplicationStatus(String applicationStatus) {
    this.applicationStatus = applicationStatus;
  }

  public Ndc documents(List<Document> documents) {
    this.documents = documents;
    return this;
  }

  public Ndc addDocumentsItem(Document documentsItem) {
    if (this.documents == null) {
      this.documents = new ArrayList<Document>();
    }
    this.documents.add(documentsItem);
    return this;
  }

  /**
   * The documents attached by owner for exemption.
   * @return documents
  **/
  @Schema(description = "The documents attached by owner for exemption.")
      @Valid
    public List<Document> getDocuments() {
    return documents;
  }

  public void setDocuments(List<Document> documents) {
    this.documents = documents;
  }

  public Ndc workflow(Workflow workflow) {
    this.workflow = workflow;
    return this;
  }

  /**
   * Get workflow
   * @return workflow
  **/
  @Schema(description = "")
  
    @Valid
    public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public Ndc auditDetails(AuditDetails auditDetails) {
    this.auditDetails = auditDetails;
    return this;
  }

  /**
   * Get auditDetails
   * @return auditDetails
  **/
  @Schema(description= "")
  
    @Valid
    public AuditDetails getAuditDetails() {
    return auditDetails;
  }

  public void setAuditDetails(AuditDetails auditDetails) {
    this.auditDetails = auditDetails;
  }

  public Ndc additionalDetails(Object additionalDetails) {
    this.additionalDetails = additionalDetails;
    return this;
  }

  /**
   * The json to capturing the custom fields
   * @return additionalDetails
  **/
  @Schema(description = "The json to capturing the custom fields")
  
    public Object getAdditionalDetails() {
    return additionalDetails;
  }

  public void setAdditionalDetails(Object additionalDetails) {
    this.additionalDetails = additionalDetails;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Ndc ndc = (Ndc) o;
    return Objects.equals(this.id, ndc.id) &&
        Objects.equals(this.tenantId, ndc.tenantId) &&
        Objects.equals(this.applicationNo, ndc.applicationNo) &&
        Objects.equals(this.ndcNo, ndc.ndcNo) &&
        Objects.equals(this.applicationType, ndc.applicationType) &&
        Objects.equals(this.ndcType, ndc.ndcType) &&
        Objects.equals(this.accountId, ndc.accountId) &&
        Objects.equals(this.source, ndc.source) &&
        Objects.equals(this.sourceRefId, ndc.sourceRefId) &&
        Objects.equals(this.landId, ndc.landId) &&
        Objects.equals(this.status, ndc.status) &&
        Objects.equals(this.applicationStatus, ndc.applicationStatus) &&
        Objects.equals(this.documents, ndc.documents) &&
        Objects.equals(this.workflow, ndc.workflow) &&
        Objects.equals(this.auditDetails, ndc.auditDetails) &&
        Objects.equals(this.additionalDetails, ndc.additionalDetails);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, tenantId, applicationNo, ndcNo, applicationType, ndcType, accountId, source, sourceRefId, landId, status, applicationStatus, documents, workflow, auditDetails, additionalDetails);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Ndc {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    applicationNo: ").append(toIndentedString(applicationNo)).append("\n");
    sb.append("    ndcNo: ").append(toIndentedString(ndcNo)).append("\n");
    sb.append("    applicationType: ").append(toIndentedString(applicationType)).append("\n");
    sb.append("    ndcType: ").append(toIndentedString(ndcType)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    sourceRefId: ").append(toIndentedString(sourceRefId)).append("\n");
    sb.append("    landId: ").append(toIndentedString(landId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    applicationStatus: ").append(toIndentedString(applicationStatus)).append("\n");
    sb.append("    documents: ").append(toIndentedString(documents)).append("\n");
    sb.append("    workflow: ").append(toIndentedString(workflow)).append("\n");
    sb.append("    auditDetails: ").append(toIndentedString(auditDetails)).append("\n");
    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
