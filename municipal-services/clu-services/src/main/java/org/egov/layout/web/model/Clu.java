package org.egov.layout.web.model;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import org.egov.layout.web.model.enums.ApplicationType;
import org.egov.layout.web.model.enums.Status;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * NOC application object to capture the details of layout related information, landid and related documents.
 */
@ApiModel(description = "NOC application object to capture the details of layout related information, landid and related documents.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-07-30T05:26:25.138Z[GMT]")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Clu {
  @SafeHtml
  @JsonProperty("id")
  private String id = null;

  @SafeHtml
  @JsonProperty("tenantId")
  private String tenantId = null;

  @SafeHtml
  @JsonProperty("applicationNo")
  private String applicationNo = null;

  @SafeHtml
  @JsonIgnore
  @JsonProperty("cluNo")
  private String cluNo = null;

  
  @JsonProperty("applicationType")
  private ApplicationType applicationType = null;


  @JsonProperty("owners")
  private List<OwnerInfo> owners;

  @SafeHtml
  @JsonProperty("cluType")
  private String cluType = null;

  @SafeHtml
  @JsonIgnore
  @JsonProperty("accountId")
  private String accountId = null;

  @SafeHtml
  @NotNull(message = "Vasika Number is required")
//  @Size(min = 1, max = 15)
  @JsonProperty("vasikaNumber")
  private String vasikaNumber = null;

  @NotNull(message = "Vasika Date is required in dd-MM-yyyy format")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
  @JsonProperty("vasikaDate")
  private LocalDate vasikaDate = null;

//  @SafeHtml
//
//  @JsonProperty("source")
//  private String source = null;

//  @SafeHtml
//  @JsonProperty("sourceRefId")
//  private String sourceRefId = null;

//  @SafeHtml
//  @JsonProperty("landId")
//  private String landId = null;

  
  @JsonProperty("status")
  private Status status = null;

  @SafeHtml
  @JsonProperty("applicationStatus")
  private String applicationStatus = null;

  @JsonProperty("documents")
  @Valid
  private List<Document> documents = null;

  @JsonProperty("workflow")
  private Workflow workflow = null;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails = null;

//  @JsonProperty("additionalDetails")
//  private Object additionalDetails = null;

  @JsonProperty("cluDetails")
  private CluDetails nocDetails = null;


  public Clu id(String id) {
    this.id = id;
    return this;
  }

  public CluDetails getNocDetails() {
    return nocDetails;
  }

  public Clu nocDetails(CluDetails nocDetails) {
    this.nocDetails = nocDetails;
    return this;
  }

  /**
   * Unique Identifier(UUID) of the bpa application for internal reference.
   * @return id
  **/
  @ApiModelProperty(readOnly = true, value = "Unique Identifier(UUID) of the bpa application for internal reference.")
  
  @Size(min=1,max=64)   public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Clu tenantId(String tenantId) {
    this.tenantId = tenantId;
    return this;
  }

  /**
   * Unique ULB identifier.
   * @return tenantId
  **/
  @ApiModelProperty(value = "Unique ULB identifier.")
  
  @Size(min=2,max=256)   public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public Clu applicationNo(String applicationNo) {
    this.applicationNo = applicationNo;
    return this;
  }

  /**
   * Generate formatted Unique Identifier of the Clu. Keep the format in mdms
   * @return applicationNo
  **/
  @ApiModelProperty(readOnly = true, value = "Generate formatted Unique Identifier of the Clu. Keep the format in mdms")
  
  @Size(min=1,max=64)   public String getApplicationNo() {
    return applicationNo;
  }

  public void setApplicationNo(String applicationNo) {
    this.applicationNo = applicationNo;
  }

  public Clu nocNo(String nocNo) {
    this.cluNo = nocNo;
    return this;
  }

  /**
   * Generate Clu number based on wf status. When to generate Nocno will be depends on wf state so make it configurable at application level
   * @return nocNo
  **/
  @ApiModelProperty(readOnly = true, value = "Generate Clu number based on wf status. When to generate Nocno will be depends on wf state so make it configurable at application level")
  
  @Size(min=1,max=64)   public String getCluNo() {
    return cluNo;
  }

  public void setCluNo(String cluNo) {
    this.cluNo = cluNo;
  }

  public Clu applicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
    return this;
  }

  /**
   * layout application type.
   * @return applicationType
  **/
  @ApiModelProperty(value = "layout application type.")
  
    public ApplicationType getApplicationType() {
    return applicationType;
  }

  public void setApplicationType(ApplicationType applicationType) {
    this.applicationType = applicationType;
  }

  public Clu nocType(String nocType) {
    this.cluType = nocType;
    return this;
  }

  /**
   * Mdms master data to configure types of layout(ex:fire layout, airport authority etc)
   * @return nocType
  **/
  @ApiModelProperty(value = "Mdms master data to configure types of layout(ex:fire layout, airport authority etc) ")
  
  @Size(min=1,max=64)   public String getCluType() {
    return cluType;
  }

  public void setCluType(String cluType) {
    this.cluType = cluType;
  }

  public Clu accountId(String accountId) {
    this.accountId = accountId;
    return this;
  }

  /**
   * Initiator User UUID
   * @return accountId
  **/
  @ApiModelProperty(value = "Initiator User UUID")
  
  @Size(min=1,max=64)   public String getAccountId() {
    return accountId;
  }

  public void setAccountId(String accountId) {
    this.accountId = accountId;
  }

//  public Clu source(String source) {
//    this.source = source;
//    return this;
//  }

  /**
   * Who is creating the record in the system(ex:BPA,Property etc)
   * @return source
  **/
//  @ApiModelProperty(value = "Who is creating the record in the system(ex:BPA,Property etc)")
//
//  @Size(min=1,max=64)   public String getSource() {
//    return source;
//  }
//
//  public void setSource(String source) {
//    this.source = source;
//  }

//  public Clu sourceRefId(String sourceRefId) {
//    this.sourceRefId = sourceRefId;
//    return this;
//  }

  /**
   * Unique Identifier of integrator(Source system) to link the layout application.
   * @return sourceRefId
  **/
//  @ApiModelProperty(value = "Unique Identifier of integrator(Source system) to link the layout application.")
  
//  @Size(min=1,max=64)   public String getSourceRefId() {
//    return sourceRefId;
//  }

//  public void setSourceRefId(String sourceRefId) {
//    this.sourceRefId = sourceRefId;
//  }

//  public Clu landId(String landId) {
//    this.landId = landId;
//    return this;
//  }

  /**
   * Unique Identifier(UUID) of the land for internal reference.
   * @return landId
  **/
  @ApiModelProperty(value = "Unique Identifier(UUID) of the land for internal reference.")
//
//  @Size(min=1,max=64)   public String getLandId() {
//    return landId;
//  }
//
//  public void setLandId(String landId) {
//    this.landId = landId;
//  }

  public Clu status(Status status) {
    this.status = status;
    return this;
  }

  /**
   * state of the record.
   * @return status
  **/
  @ApiModelProperty(value = "state of the record.")
  
    public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Clu applicationStatus(String applicationStatus) {
    this.applicationStatus = applicationStatus;
    return this;
  }

  /**
   * Application status should get populate from wf engine
   * @return applicationStatus
  **/
  @ApiModelProperty(readOnly = true, value = "Application status should get populate from wf engine")
  
  @Size(min=1,max=64)   public String getApplicationStatus() {
    return applicationStatus;
  }

  public void setApplicationStatus(String applicationStatus) {
    this.applicationStatus = applicationStatus;
  }

  public Clu documents(List<Document> documents) {
    this.documents = documents;
    return this;


  }

  public Clu addDocumentsItem(Document documentsItem) {
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
  @ApiModelProperty(value = "The documents attached by owner for exemption.")
      @Valid
    public List<Document> getDocuments() {
    return documents;
  }

  public void setDocuments(List<Document> documents) {
    this.documents = documents;
  }

  public Clu workflow(Workflow workflow) {
    this.workflow = workflow;
    return this;
  }

  /**
   * Get workflow
   * @return workflow
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public Workflow getWorkflow() {
    return workflow;
  }

  public void setWorkflow(Workflow workflow) {
    this.workflow = workflow;
  }

  public Clu auditDetails(AuditDetails auditDetails) {
    this.auditDetails = auditDetails;
    return this;
  }

  /**
   * Get auditDetails
   * @return auditDetails
  **/
  @ApiModelProperty(value = "")
  
    @Valid
    public AuditDetails getAuditDetails() {
    return auditDetails;
  }

  public void setAuditDetails(AuditDetails auditDetails) {
    this.auditDetails = auditDetails;
  }

//  public Clu additionalDetails(Object additionalDetails) {
//    this.additionalDetails = additionalDetails;
//    return this;
//  }

  /**
   * The json to capturing the custom fields
   * @return additionalDetails
  **/
  @ApiModelProperty(value = "The json to capturing the custom fields")
  
//    public Object getAdditionalDetails() {
//    return additionalDetails;
//  }
//
//  public void setAdditionalDetails(Object additionalDetails) {
//    this.additionalDetails = additionalDetails;
//  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Clu noc = (Clu) o;
    return Objects.equals(this.id, noc.id) &&
        Objects.equals(this.tenantId, noc.tenantId) &&
        Objects.equals(this.applicationNo, noc.applicationNo) &&
        Objects.equals(this.cluNo, noc.cluNo) &&
        Objects.equals(this.applicationType, noc.applicationType) &&
        Objects.equals(this.cluType, noc.cluType) &&
        Objects.equals(this.accountId, noc.accountId) &&
//        Objects.equals(this.source, layout.source) &&
//        Objects.equals(this.sourceRefId, layout.sourceRefId) &&
//        Objects.equals(this.landId, layout.landId) &&
        Objects.equals(this.status, noc.status) &&
        Objects.equals(this.applicationStatus, noc.applicationStatus) &&
        Objects.equals(this.documents, noc.documents) &&
        Objects.equals(this.workflow, noc.workflow) &&
        Objects.equals(this.auditDetails, noc.auditDetails);
//        Objects.equals(this.additionalDetails, layout.additionalDetails);
  }

  @Override
  public int hashCode() {
//    return Objects.hash(id, tenantId, applicationNo, nocNo, applicationType, nocType, accountId, source, sourceRefId, landId, status, applicationStatus, documents, workflow, auditDetails, additionalDetails);
    return Objects.hash(id, tenantId, applicationNo, cluNo, applicationType, cluType, accountId, status, applicationStatus, documents, workflow, auditDetails);

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Clu {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    tenantId: ").append(toIndentedString(tenantId)).append("\n");
    sb.append("    applicationNo: ").append(toIndentedString(applicationNo)).append("\n");
    sb.append("    cluNo: ").append(toIndentedString(cluNo)).append("\n");
    sb.append("    applicationType: ").append(toIndentedString(applicationType)).append("\n");
    sb.append("    cluType: ").append(toIndentedString(cluType)).append("\n");
    sb.append("    accountId: ").append(toIndentedString(accountId)).append("\n");
//    sb.append("    source: ").append(toIndentedString(source)).append("\n");
//    sb.append("    sourceRefId: ").append(toIndentedString(sourceRefId)).append("\n");
//    sb.append("    landId: ").append(toIndentedString(landId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    applicationStatus: ").append(toIndentedString(applicationStatus)).append("\n");
    sb.append("    documents: ").append(toIndentedString(documents)).append("\n");
    sb.append("    workflow: ").append(toIndentedString(workflow)).append("\n");
    sb.append("    auditDetails: ").append(toIndentedString(auditDetails)).append("\n");
//    sb.append("    additionalDetails: ").append(toIndentedString(additionalDetails)).append("\n");
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
