package org.egov.filemgmnt.web.models;

import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Audit details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditDetails {

    @Schema(type = "string", format = "uuid", description = "Created by user id")
    @Size(max = 64)
    @JsonProperty("createdBy")
    private String createdBy;

    @Schema(type = "int", format = "int64", description = "Created time  in milliseconds")
    @JsonProperty("createdTime")
    private Long createdTime;

    @Schema(type = "string", format = "uuid", description = "Last modified user id")
    @Size(max = 64)
    @JsonProperty("lastModifiedBy")
    private String lastModifiedBy;

    @Schema(type = "int", format = "int64", description = "Last modified time in milliseconds")
    @JsonProperty("lastModifiedTime")
    private Long lastModifiedTime;
}
