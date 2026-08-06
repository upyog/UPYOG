package org.egov.garbageservice.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"tenantId", "businessService"})
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Contract model for a workflow definition returned from the eGov workflow service.
 *
 * Behavior:
 * - Describes one business process (businessService, business, tenantId) with SLA and API URIs.
 * - Contains the full state machine as a list of {@link State} nodes (start, intermediate, terminal).
 * - Maps to/from JSON via Jackson for workflow business-service search responses.
 * - {@link #addStatesItem(State)} appends a state; {@link #getStateFromUuid(String)} finds a state by uuid.
 *
 * Notes:
 * - Fetched via {@link WorkflowService#businessServiceSearch} and used in GarbageAccountService to resolve
 *   valid application statuses and transitions for garbage accounts.
 * - Data-only model; workflow execution is handled by the external workflow engine.
 * - equals/hashCode uses tenantId and businessService only.
 */
public class BusinessService {

    //        @NotNull
    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId = null;

    @JsonProperty("uuid")
    @CustomSafeHtml
    private String uuid = null;

    //        @NotNull
    @JsonProperty("businessService")
    @CustomSafeHtml
    private String businessService = null;

    //        @NotNull
    @JsonProperty("business")
    @CustomSafeHtml
    private String business = null;

    @JsonProperty("getUri")
    @CustomSafeHtml
    private String getUri = null;

    @JsonProperty("postUri")
    @CustomSafeHtml
    private String postUri = null;

    @JsonProperty("businessServiceSla")
    private Long businessServiceSla = null;

    //        @NotNull
//        @Valid
    @JsonProperty("states")
    private List<State> states = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    /**
     * Returns the currentState with the given uuid if not present returns null
     *
     * @param uuid the uuid of the currentState to be returned
     * @return
     */
    public State getStateFromUuid(String uuid) {
        State state = null;
        if (this.states != null) {
            for (State s : this.states) {
                if (s.getUuid().equalsIgnoreCase(uuid)) {
                    state = s;
                    break;
                }
            }
        }
        return state;
    }


}

