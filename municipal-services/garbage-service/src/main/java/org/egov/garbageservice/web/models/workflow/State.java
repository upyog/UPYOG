package org.egov.garbageservice.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"tenantId", "businessServiceId", "state"})
/**
 * Contract model for one node in a workflow {@link BusinessService} state machine.
 *
 * Behavior:
 * - Represents a workflow state (state name, applicationStatus shown on the application).
 * - Flags control behavior: isStartState, isTerminateState, isStateUpdatable, docUploadRequired.
 * - Holds outgoing transitions as a list of {@link Action} (action name, next state, allowed roles).
 * - {@link #addActionsItem(Action)} appends an allowed transition from this state.
 *
 * Notes:
 * - Nested under {@link BusinessService#getStates()}; not persisted by garbage-service.
 * - applicationStatus is what garbage-service maps to garbage account status after workflow transition.
 * - Field names must align with the workflow service API schema.
 */
public class State {

    @JsonProperty("uuid")
    @CustomSafeHtml
    private String uuid;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId;

    @JsonProperty("businessServiceId")
    @CustomSafeHtml
    private String businessServiceId;

    @JsonProperty("sla")
    private Long sla;

    @JsonProperty("state")
    @CustomSafeHtml
    private String state;

    @JsonProperty("applicationStatus")
    @CustomSafeHtml
    private String applicationStatus;

    @JsonProperty("docUploadRequired")
    private Boolean docUploadRequired;

    @JsonProperty("isStartState")
    private Boolean isStartState;

    @JsonProperty("isTerminateState")
    private Boolean isTerminateState;

    @JsonProperty("isStateUpdatable")
    private Boolean isStateUpdatable;

    @JsonProperty("actions")
    private List<Action> actions;

    private AuditDetails auditDetails;


    /**
     * Appends an action to the list of permissible actions for this state.
     *
     * @param actionsItem the action to append
     * @return this State instance for chaining
     */

    public State addActionsItem(Action actionsItem) {
        if (this.actions == null) {
            this.actions = new ArrayList<>();
        }
        this.actions.add(actionsItem);
        return this;
    }

}

