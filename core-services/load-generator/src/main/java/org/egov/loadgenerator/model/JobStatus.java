/**
 * Represents the execution status and runtime metrics of a load generation job.
 *
 * <p>This model maintains information about the progress, performance,
 * and outcome of a load generation request. It is used for tracking job
 * execution and reporting status through the Load Generator APIs.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Store job execution metadata.</li>
 *   <li>Track success and failure statistics.</li>
 *   <li>Capture execution timing and performance metrics.</li>
 *   <li>Provide error information for failed executions.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobStatus {

    /**
     * Unique identifier of the load generation job.
     */
    private String jobId;

    /**
     * Name of the module for which the job is executed.
     */
    private String module;

    /**
     * Tenant against which the load generation job is executed.
     */
    private String tenantId;

    /**
     * Total number of records requested for processing.
     */
    private int totalRecords;

    /**
     * Number of records processed successfully.
     */
    private int successCount;

    /**
     * Number of records that failed during processing.
     */
    private int failureCount;

    /**
     * Current execution status of the job
     * (for example, RUNNING, COMPLETED, or FAILED).
     */
    private String status;

    /**
     * Job start time in milliseconds since the Unix epoch.
     */
    private long startTimeMs;

    /**
     * Job completion time in milliseconds since the Unix epoch.
     */
    private long endTimeMs;

    /**
     * Average number of records processed per second.
     */
    private double throughputPerSec;

    /**
     * Average response time per request in milliseconds.
     */
    private double avgResponseTimeMs;

    /**
     * Summary of errors encountered during execution, if any.
     */
    private String errorSummary;
}
