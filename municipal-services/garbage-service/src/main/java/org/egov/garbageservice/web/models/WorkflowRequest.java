package org.egov.garbageservice.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.List;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Holds workflow action and documents from the new payload structure.
 * action and documents are mapped to GarbageAccount fields during enrichment.
 */
public class WorkflowRequest {

    @CustomSafeHtml
    private String action;

    private List<GrbgDocument> documents;
}
