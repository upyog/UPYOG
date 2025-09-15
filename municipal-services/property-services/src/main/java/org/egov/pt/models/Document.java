package org.egov.pt.models;

import javax.validation.constraints.NotNull;

import org.egov.pt.models.enums.Status;
import org.egov.tracer.annotations.CustomSafeHtml;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(of= {"fileStoreId","documentUid","id"})
public class Document {

  @CustomSafeHtml
  @JsonProperty("id")
  private String id ;

  @JsonProperty("documentType")
  @CustomSafeHtml
  @NotNull
  private String documentType ;

  @JsonProperty("fileStoreId")
  @CustomSafeHtml
  @NotNull
  private String fileStoreId ;

  @CustomSafeHtml
  @JsonProperty("documentUid")
  private String documentUid ;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails;

  @JsonProperty("status")
  private Status status;
}

