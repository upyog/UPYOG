package org.egov.waterconnection.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentDetails {

  @JsonProperty("id")
  private String id ;

  @JsonProperty("documentType")
  private String documentType ;

  @JsonProperty("fileStoreId")
  @NotNull
  private String fileStoreId ;

  @JsonProperty("documentUid")
  private String documentUid ;

  @JsonProperty("auditDetails")
  private AuditDetails auditDetails;

  @JsonProperty("status")
  private Status status;
  
  @NotNull
  @JsonProperty("connectionNo")
  private String connectionNo;
  
  @JsonProperty("connectionUid")
  private String connectionUid;

  @JsonProperty("userUid")
  private String userUid;
  
  @NotNull
  @JsonProperty("tenantId")
  private String tenantId;
}
