package org.egov.applyworkflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class State {
    private String sla;
    private String state;
    private String applicationStatus;
    private boolean docUploadRequired;
    private boolean isStartState;
    private boolean isTerminateState;
    private boolean isStateUpdatable;
    private List<Action> actions;
}