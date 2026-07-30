package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.annotations.CustomSafeHtml;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Holds workflow action from the new payload structure.
 * action is mapped to GarbageAccount.workflowAction during enrichment.
 */
public class WorkflowRequest {

    @CustomSafeHtml
    private String action;
}
