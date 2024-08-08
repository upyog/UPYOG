package com.example.hpgarbageservice.contract.workflow;

import java.util.ArrayList;
import java.util.List;

import com.example.hpgarbageservice.model.AuditDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
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
@EqualsAndHashCode(of = {"tenantId","businessServiceId","state"})
public class State   {

        @JsonProperty("uuid")
        private String uuid;

        @JsonProperty("tenantId")
        private String tenantId;

        @JsonProperty("businessServiceId")
        private String businessServiceId;

        @JsonProperty("sla")
        private Long sla;

        @JsonProperty("state")
        private String state;

        @JsonProperty("applicationStatus")
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


        public State addActionsItem(Action actionsItem) {
            if (this.actions == null) {
            this.actions = new ArrayList<>();
            }
        this.actions.add(actionsItem);
        return this;
        }

}

