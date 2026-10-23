package org.upyog.dx.upyogdata.pfms.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SchedulerLog {
    private String schedulerType;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private long durationMs;
    private String status;
    private Integer totalPicked;
    private Integer successCount;
    private Integer failedCount;
    private String createdBy;
}
