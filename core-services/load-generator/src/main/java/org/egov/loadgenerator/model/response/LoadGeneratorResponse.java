/**
 * Represents the response returned by the Load Generator APIs.
 *
 * <p>This model encapsulates the execution status of a load generation
 * request, including the generated job identifier, a descriptive message,
 * and the current processing status of the job.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Return the unique job identifier.</li>
 *   <li>Provide a human-readable response message.</li>
 *   <li>Expose the current job execution status.</li>
 * </ul>
 *
 * <p>This class is serialized as a JSON response and is intended for
 * communication between the REST API and clients.
 *
 * @see JobStatus
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadGeneratorResponse {

    /**
     * Unique identifier assigned to the load generation job.
     */
    @JsonProperty("jobId")
    private String jobId;

    /**
     * Human-readable message describing the result of the request.
     */
    @JsonProperty("message")
    private String message;

    /**
     * Current execution status of the load generation job.
     */
    @JsonProperty("status")
    private JobStatus status;
}
