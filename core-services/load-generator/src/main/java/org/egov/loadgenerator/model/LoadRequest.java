/**
 * Represents a request to initiate a load generation job.
 *
 * <p>This model contains the input parameters required to start
 * load generation for a specific module and tenant. It is received
 * as the request body by the Load Generator REST APIs.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Identify the target module.</li>
 *   <li>Specify the tenant for which data should be generated.</li>
 *   <li>Define the number of records to generate.</li>
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadRequest {

    /**
     * Name of the target module for which load is generated.
     */
    @JsonProperty("module")
    private String module;

    /**
     * Tenant identifier against which the load generation job is executed.
     */
    @JsonProperty("tenantId")
    private String tenantId;

    /**
     * Total number of records to be generated.
     */
    @JsonProperty("count")
    private int count;
}
