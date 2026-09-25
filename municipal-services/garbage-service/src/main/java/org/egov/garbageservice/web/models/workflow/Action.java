package org.egov.garbageservice.web.models.workflow;

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
@EqualsAndHashCode(of = {"tenantId", "currentState", "action"})
/**
 * Contract model for one allowed workflow transition from a {@link State}.
 *
 * Behavior:
 * - Defines action label, currentState, nextState, and roles permitted to perform the action.
 * - Included on {@link State#getActions()} in business-service definitions and on
 *   {@link ProcessInstance#getNextActions()} after a transition.
 * - {@link #addRolesItem(String)} appends a role allowed to trigger this action.
 * - Maps to/from JSON with Jackson for workflow search and transition responses.
 *
 * Notes:
 * - Also returned in {@link ValidActionResponce#getNextValidAction()} for UI/action validation.
 * - active flag indicates whether the transition is currently enabled in the definition.
 * - Data-only model; transition execution is done via {@link WorkflowService#callWf}.
 */
public class Action {

    @JsonProperty("uuid")
    @CustomSafeHtml
    private String uuid;

    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId;

    @JsonProperty("currentState")
    @CustomSafeHtml
    private String currentState;

    //        @NotNull
    @JsonProperty("action")
    @CustomSafeHtml
    private String action;

    //        @NotNull
    @JsonProperty("nextState")
    @CustomSafeHtml
    private String nextState;

    //        @NotNull
    @JsonProperty("roles")
//        @Valid
    private List<String> roles;

    private AuditDetails auditDetails;

    @JsonProperty("active")
    private Boolean active;

}

