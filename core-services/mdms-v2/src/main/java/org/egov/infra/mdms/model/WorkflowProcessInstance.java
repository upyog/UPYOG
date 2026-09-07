package org.egov.infra.mdms.model;

import lombok.Data;

@Data
public class WorkflowProcessInstance {

    private String businessId;

    private String tenantId;

    private String businessService;

    private String moduleName;

    private String action;

    private String comment;
}
