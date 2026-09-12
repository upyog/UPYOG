package org.egov.refund.querybuilder;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class RefundQueryBuilder {

	private static final String REFUND_COLUMNS = """
			id,
			refund_no,
			tenant_id,
			module_name,
			business_service,
			consumer_code,
			payment_id,
			applicant_name,
			mobile_number,
			refund_category,
			refund_reason,
			payment_mode_original,
			amount_paid,
			refund_amount,
			refund_mode,
			status,
			sanction_ref,
			finance_approval_date,
			gateway_refund_id,
			beneficiary_details,
			additional_details,
			created_by,
			created_time,
			last_modified_by,
			last_modified_time,
			file_store_id
			""";

	public String getInsertQuery() {
		return """
				INSERT INTO eg_refund (
				    id,
				    refund_no,
				    tenant_id,
				    module_name,
				    business_service,
				    consumer_code,
				    payment_id,
				    applicant_name,
				    mobile_number,
				    refund_category,
				    refund_reason,
				    payment_mode_original,
				    amount_paid,
				    refund_amount,
				    refund_mode,
				    status,
				    sanction_ref,
				    finance_approval_date,
				    gateway_refund_id,
				    beneficiary_details,
				    additional_details,
				    created_by,
				    created_time,
				    last_modified_by,
				    last_modified_time,
				    file_store_id
				)
				VALUES (
				    ?, ?, ?, ?, ?, ?, ?,
				    ?, ?, ?, ?, ?, ?, ?,
				    ?, ?, ?, ?, ?, ?::jsonb,
				    ?::jsonb, ?, ?, ?, ?,?
				)
				""";
	}

	public String getUpdateQuery() {
		return """
				UPDATE eg_refund
				SET
				    refund_no = ?,
				    tenant_id = ?,
				    module_name = ?,
				    business_service = ?,
				    consumer_code = ?,
				    payment_id = ?,
				    applicant_name = ?,
				    mobile_number = ?,
				    refund_category = ?,
				    refund_reason = ?,
				    payment_mode_original = ?,
				    amount_paid = ?,
				    refund_amount = ?,
				    refund_mode = ?,
				    status = ?,
				    sanction_ref = ?,
				    finance_approval_date = ?,
				    gateway_refund_id = ?,
				    beneficiary_details = ?::jsonb,
				    additional_details = ?::jsonb,
				    last_modified_by = ?,
				    last_modified_time = ?,
				    file_store_id = ?
				WHERE id = ?
				""";
	}

	public String getFindByIdQuery() {
		return """
				SELECT
				    %s
				FROM eg_refund
				WHERE id = ?
				""".formatted(REFUND_COLUMNS);
	}

	public String getFindByRefundNoQuery() {
		return """
				SELECT
				    %s
				FROM eg_refund
				WHERE refund_no = ?
				""".formatted(REFUND_COLUMNS);
	}

	public String getUpdateStatusQuery() {
		return """
				UPDATE eg_refund
				SET
				    status = ?,
				    last_modified_by = ?,
				    last_modified_time = ?
				WHERE id = ?
				""";
	}

	public String getFinanceApprovalQuery() {
		return """
				UPDATE eg_refund
				SET
				    status = ?,
				    sanction_ref = ?,
				    finance_approval_date = ?,
				    last_modified_by = ?,
				    last_modified_time = ?
				WHERE id = ?
				""";
	}

	public String getGatewayRefundUpdateQuery() {
		return """
				UPDATE eg_refund
				SET
				    gateway_refund_id = ?,
				    status = ?,
				    last_modified_by = ?,
				    last_modified_time = ?
				WHERE id = ?
				""";
	}

	public RefundSearchQuery buildSearchQuery(RefundSearchCriteria criteria) {

		StringBuilder query = new StringBuilder("""
				SELECT
				    %s
				FROM eg_refund
				WHERE 1 = 1
				""".formatted(REFUND_COLUMNS));

		List<Object> params = new ArrayList<>();

		addCondition(query, params, "tenant_id", criteria.getTenantId());

		addCondition(query, params, "module_name", criteria.getModuleName());

		addCondition(query, params, "business_service", criteria.getBusinessService());

		addCondition(query, params, "consumer_code", criteria.getConsumerCode());

		addCondition(query, params, "payment_id", criteria.getPaymentId());

		addCondition(query, params, "refund_no", criteria.getRefundNo());

		addCondition(query, params, "status", criteria.getStatus());

		addCondition(query, params, "refund_category", criteria.getRefundCategory());

		addCondition(query, params, "gateway_refund_id", criteria.getGatewayRefundId());

		addCondition(query, params, "sanction_ref", criteria.getSanctionRef());

		query.append(" ORDER BY created_time DESC");

		return RefundSearchQuery.builder().query(query.toString()).params(params.toArray()).build();
	}

	private void addCondition(StringBuilder query, List<Object> params, String column, String value) {

		if (hasValue(value)) {
			query.append(" AND ").append(column).append(" = ?");

			params.add(value);
		}
	}

	private boolean hasValue(String value) {
		return value != null && !value.isBlank();
	}

	public String getInsertAuditLogQuery() {
		return """
				INSERT INTO eg_refund_audit (
				    id,
				    refund_id,
				    refund_no,
				    tenant_id,
				    module_name,
				    business_service,
				    consumer_code,
				    payment_id,
				    applicant_name,
				    mobile_number,
				    refund_category,
				    refund_reason,
				    payment_mode_original,
				    amount_paid,
				    refund_amount,
				    refund_mode,
				    status,
				    sanction_ref,
				    finance_approval_date,
				    gateway_refund_id,
				    file_store_id,
				    beneficiary_details,
				    additional_details,
				    created_by,
				    created_time,
				    last_modified_by,
				    last_modified_time,
				    audit_created_time,
				    workflow_action
				)
				VALUES (
				    ?, ?, ?, ?, ?, ?, ?,
				    ?, ?, ?, ?, ?, ?, ?,
				    ?, ?, ?, ?, ?, ?, ?,
				    ?::jsonb, ?::jsonb, ?, ?, ?, ?, ?, ?
				)
				""";
	}
}