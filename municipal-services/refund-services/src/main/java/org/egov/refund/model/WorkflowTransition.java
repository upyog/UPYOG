package org.egov.refund.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowTransition {

    private boolean valid;

    private String currentState;

    private String action;

    private String nextState;

    private String businessService;
    
    private String tenantId;
    
    private String applicationStatus;
    
}