package org.egov.garbageservice.contract.workflow;

import java.util.ArrayList;
import java.util.List;

import org.egov.garbageservice.model.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = {"tenantId","currentState","action"})
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
public class Action   {

//        @Size(max=256)
        @JsonProperty("uuid")
        @CustomSafeHtml
        private String uuid;

//        @Size(max=256)
        @JsonProperty("tenantId")
        @CustomSafeHtml
        private String tenantId;

//        @Size(max=256)
        @JsonProperty("currentState")
        @CustomSafeHtml
        private String currentState;

//        @NotNull
//        @Size(max=256)
        @JsonProperty("action")
        @CustomSafeHtml
        private String action;

//        @NotNull
//        @Size(max=256)
        @JsonProperty("nextState")
        @CustomSafeHtml
        private String nextState;

//        @NotNull
//        @Size(max=1024)
        @JsonProperty("roles")
//        @Valid
        private List<String> roles;

        private AuditDetails auditDetails;

        @JsonProperty("active")
        private Boolean active;


        public Action addRolesItem(String rolesItem) {
            if (this.roles == null) {
            this.roles = new ArrayList<>();
            }
        this.roles.add(rolesItem);
        return this;
        }

}

