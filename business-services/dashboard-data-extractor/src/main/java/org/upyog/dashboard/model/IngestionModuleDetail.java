package org.upyog.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity model representing a configured ULB-module ingestion stream in {@code ug_ingestion_module_detail}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IngestionModuleDetail {

    @JsonProperty("detailId")
    private String detailId;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("moduleName")
    private String moduleName;

    @JsonProperty("active")
    private boolean active;

    @JsonIgnore
    private String createdBy;

    @JsonIgnore
    private Long createdTime;

    @JsonIgnore
    private String lastModifiedBy;

    @JsonIgnore
    private Long lastModifiedTime;
}
