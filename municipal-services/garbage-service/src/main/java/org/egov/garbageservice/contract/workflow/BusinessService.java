package org.egov.garbageservice.contract.workflow;

import java.util.ArrayList;
import java.util.List;

import org.egov.garbageservice.model.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@EqualsAndHashCode(of = {"tenantId","businessService"})
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
public class BusinessService   {

//        @NotNull
//        @Size(max=256)
        @JsonProperty("tenantId")
        @CustomSafeHtml
        private String tenantId = null;

//        @Size(max=256)
        @JsonProperty("uuid")
        @CustomSafeHtml
        private String uuid = null;

//        @NotNull
//        @Size(max=256)
        @JsonProperty("businessService")
        @CustomSafeHtml
        private String businessService = null;

//        @NotNull
//        @Size(max=256)
        @JsonProperty("business")
        @CustomSafeHtml
        private String business = null;

//        @Size(max=1024)
        @JsonProperty("getUri")
        @CustomSafeHtml
        private String getUri = null;

//        @Size(max=1024)
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


        public BusinessService addStatesItem(State statesItem) {
            if (this.states == null) {
            this.states = new ArrayList<>();
            }
        this.states.add(statesItem);
        return this;
        }


        /**
         * Returns the currentState with the given uuid if not present returns null
         * @param uuid the uuid of the currentState to be returned
         * @return
         */
        public State getStateFromUuid(String uuid) {
               State state = null;
               if(this.states!=null){
                       for(State s : this.states){
                               if(s.getUuid().equalsIgnoreCase(uuid)){
                                       state = s;
                                       break;
                               }
                       }
               }
               return state;
        }



}

