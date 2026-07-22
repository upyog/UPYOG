package org.upyog.adapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a single retry attempt during data ingestion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetryAttempt {
    private int attemptNumber;
    private String status;
    private String failureReason;
    private long timestamp;
}
