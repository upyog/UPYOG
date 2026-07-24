package org.egov.loadgenerator.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobStatus {

    private String jobId;
    private String module;
    private String tenantId;
    private int totalRecords;
    private int successCount;
    private int failureCount;
    private String status;           // RUNNING, COMPLETED, FAILED
    private long startTimeMs;
    private long endTimeMs;
    private double throughputPerSec;
    private double avgResponseTimeMs;
    private String errorSummary;
}
