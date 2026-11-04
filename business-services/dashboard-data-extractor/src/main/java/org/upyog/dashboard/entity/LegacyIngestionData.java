package org.upyog.dashboard.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data-transfer / entity object representing a single legacy ingestion
 * record persisted in the {@code ug_legacy_data_ingestion_detail} PostgreSQL table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LegacyIngestionData {

    private String moduleIngestionId;
    private String moduleDetailId;
    private String schedulerId;
    private String tenantId;
    private String ulbName;
    private String moduleName;
    private String pushDate;
    private String startDate;
    private String endDate;
    private String userId;
    private String requestData;
    private String responseData;
    private String ingestionStatus;
    private String exceptionCode;
    private String createdBy;
    private Long createdTime;
    private String lastModifiedBy;
    private Long lastModifiedTime;
}
