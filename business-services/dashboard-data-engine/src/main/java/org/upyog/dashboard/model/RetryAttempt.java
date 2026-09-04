package org.upyog.dashboard.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Model representing a single retry attempt during data ingestion.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RetryAttempt {
    private int attemptNumber;
    private String status;
    private String failureReason;
    private long timestamp;
}
