package org.upyog.dashboard.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain entity representing a single execution instance of a scheduled or manual ingestion job.
 * Stored in the {@code ug_ingestion_scheduler_detail} database table.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IngestionSchedulerDetail {

    private String schedulerId;
    private String schedulerName;
    private String cronExpression;
    private Long startTime;
    private Long endTime;
    private Long durationMs;
    private String status;
    private Integer totalRecordsProcessed;
    private Integer successRecordsCount;
    private Integer failureRecordsCount;
    private String errorMessage;
    private String createdBy;
    private Long createdTime;
    private String lastModifiedBy;
    private Long lastModifiedTime;
}
