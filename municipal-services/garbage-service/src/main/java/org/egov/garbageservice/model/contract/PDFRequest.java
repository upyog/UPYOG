package org.egov.garbageservice.model.contract;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.Map;

@Getter
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
/**
 * Request payload for the eGov PDF generation service (bills, receipts, certificates).
 * Supplies template key, tenantId, dynamic data map, optional inline HTML, and RequestInfo.
 * Used by PDFRequestGenerator and ReportService when generating garbage tax documents.
 */
@Builder
@ToString
public class PDFRequest {

    private RequestInfo RequestInfo;

    @NotEmpty
    @CustomSafeHtml
    private String key;

    @NotEmpty
    @CustomSafeHtml
    private String tenantId;

    @JsonProperty("data")
    private Map data;

    @CustomSafeHtml
    private String htmlTemplateContent;

    @Builder.Default
    private Boolean isHeaderFooterSkip = false;
}
