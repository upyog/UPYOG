package org.egov.garbageservice.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
/**
 * Captures failed bill generation attempts for retry and operational reporting.
 * Stores error context alongside application and period identifiers from the scheduler.
 */
@AllArgsConstructor
public class GrbgBillFailure {

	private UUID  id;
	@CustomSafeHtml
	private String consumer_code;
	@CustomSafeHtml
	private String module_name;
	@CustomSafeHtml
	private String failure_reason;
	@CustomSafeHtml
	private String tenant_id;
	@CustomSafeHtml
	private String year;
	@CustomSafeHtml
	private String month;
	@CustomSafeHtml
	private String from_date;
	@CustomSafeHtml
	private String to_date;
    private JsonNode request_payload = null;
    private JsonNode response_payload = null;
    private List<String> error_json = null;
	@CustomSafeHtml
	private String status_code;
	private Long created_time;
	private Long last_modified_time;
//	private String billId;
//	private BigDecimal grbgBillAmount;
//	private AuditDetails auditDetails;


}
