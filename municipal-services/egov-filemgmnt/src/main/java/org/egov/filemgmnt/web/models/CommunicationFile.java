package org.egov.filemgmnt.web.models;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Office communication file details")

@Validated

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommunicationFile {

    @Schema(type = "string", format = "uuid", description = "Communication file id")
    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @Schema(type = "string", description = "Subject id")
    @Size(max = 64)
    @NotNull
    @JsonProperty("subjectTypeId")
    private String subjectTypeId;

    @Schema(type = "string", description = "Sender id")
    @Size(max = 64)
    @NotNull
    @JsonProperty("senderId")
    private String senderId;

    @Schema(type = "string", description = "Priority id")
    @Size(max = 64)
    @NotNull
    @JsonProperty("priorityId")
    private String priorityId;

    @Schema(type = "string", description = "File store id")
    @Size(max = 64)
    @NotNull
    @JsonProperty("fileStoreId")
    private String fileStoreId;

    @Schema(type = "string", description = "Details")
    @Size(max = 64)
    @NotNull
    @JsonProperty("details")
    private String details;

//    @Schema(type = "string", description = "Tenant identification number")
//    @Size(max = 64)
//    @NotNull
//    @JsonProperty("tenantId")
//    private String tenantId;

//    @Schema(type = "int", format = "int64", description = "Document date  in milliseconds")
//    @JsonProperty("DocumentTime")
//    private Long DocumentTime;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
