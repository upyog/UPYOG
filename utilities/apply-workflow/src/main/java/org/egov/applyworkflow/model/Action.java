package org.egov.applyworkflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Action {
    private String action;
    private String nextState;
    private List<String> roles;
    private boolean active;
}