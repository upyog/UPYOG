package org.egov.infra.mdms.model;

import lombok.Data;
import org.egov.common.contract.request.RequestInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;


/**
 * Request model used for updating theme configuration
 * status after workflow action.
 *
 * Workflow actions:
 *  - APPROVE
 *  - REJECT
 */
@Data
public class ThemeConfigWorkflowRequest {


    /**
     * Theme configuration on which
     * workflow action is performed.
     */
    @JsonAlias({"themeConfigStaging", "themeConfig"})
    private ThemeConfig themeConfig;

    private String action;

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

}