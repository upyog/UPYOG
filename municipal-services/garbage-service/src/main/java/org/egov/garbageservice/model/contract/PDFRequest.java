package org.egov.garbageservice.model.contract;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;

import org.egov.tracer.annotations.CustomSafeHtml;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
