package org.egov.garbageservice.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrbgBillFailure {

	private UUID  id;
	private String consumer_code;
	private String module_name;
	private String failure_reason;
	private String tenant_id;
	private String year;
	private String month;
	private String from_date;
	private String to_date;
    private JsonNode request_payload = null;
    private JsonNode response_payload = null;
    private List<String> error_json = null;
	private String status_code;
	private Long created_time;
	private Long last_modified_time;
//	private String billId;
//	private BigDecimal grbgBillAmount;
//	private AuditDetails auditDetails;


}
