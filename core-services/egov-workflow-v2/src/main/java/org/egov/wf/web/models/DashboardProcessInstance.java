package org.egov.wf.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class representing a single workflow process instance specifically
 * for the dashboard view.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardProcessInstance {

    /**
     * Unique identifier of the process instance.
     */
    @JsonProperty("id")
    private String id;

    /**
     * Tenant ID associated with the process instance.
     */
    @JsonProperty("tenantId")
    private String tenantId;

    /**
     * Name of the workflow business service.
     */
    @JsonProperty("businessService")
    private String businessService;

    /**
     * The business identifier (e.g. application number) of the record.
     */
    @JsonProperty("businessId")
    private String businessId;

    /**
     * The current workflow action status.
     */
    @JsonProperty("action")
    private String action;

    /**
     * The module name associated with this workflow record.
     */
    @JsonProperty("moduleName")
    private String moduleName;
}
