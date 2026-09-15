package org.egov.refund.rowmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.refund.model.AuditDetails;
import org.egov.refund.model.Refund;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;

@Component
public class RefundRowMapper implements RowMapper<Refund> {

	private final ObjectMapper objectMapper;

	public RefundRowMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public Refund mapRow(ResultSet rs, int rowNum) throws SQLException {

		Refund refund = new Refund();

		refund.setId(UUID.fromString(rs.getString("id")));

		refund.setRefundNo(rs.getString("refund_no"));

		refund.setTenantId(rs.getString("tenant_id"));

		refund.setModuleName(rs.getString("module_name"));

		refund.setBusinessService(rs.getString("business_service"));

		refund.setConsumerCode(rs.getString("consumer_code"));

		refund.setPaymentId(rs.getString("payment_id"));

		refund.setApplicantName(rs.getString("applicant_name"));

		refund.setMobileNumber(rs.getString("mobile_number"));

		refund.setRefundCategory(rs.getString("refund_category"));

		refund.setRefundReason(rs.getString("refund_reason"));

		refund.setPaymentModeOriginal(rs.getString("payment_mode_original"));

		refund.setAmountPaid(rs.getBigDecimal("amount_paid"));

		refund.setRefundAmount(rs.getBigDecimal("refund_amount"));

		refund.setRefundMode(rs.getString("refund_mode"));

		refund.setStatus(rs.getString("status"));

		refund.setSanctionRef(rs.getString("sanction_ref"));

		Timestamp financeApprovalDate = rs.getTimestamp("finance_approval_date");

		if (financeApprovalDate != null) {

			refund.setFinanceApprovalDate(financeApprovalDate.toLocalDateTime());
		}

		refund.setFileStoreId(rs.getString("file_store_id"));
		refund.setGatewayRefundId(rs.getString("gateway_refund_id"));

		refund.setBeneficiaryDetails(readJson(rs, "beneficiary_details"));

		refund.setAdditionalDetails(readJson(rs, "additional_details"));

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("created_by"))
				.createdTime(getNullableLong(rs, "created_time")).lastModifiedBy(rs.getString("last_modified_by"))
				.lastModifiedTime(getNullableLong(rs, "last_modified_time")).build();

		refund.setAuditDetails(auditDetails);

		return refund;
	}

	private Map<String, Object> readJson(ResultSet rs, String column) throws SQLException {

		Object value = rs.getObject(column);

		if (value == null) {
			return null;
		}

		try {

			return objectMapper.readValue(value.toString(), Map.class);

		} catch (JsonProcessingException e) {

			throw new SQLException("Unable to parse JSONB column: " + column, e);
		}
	}

	private Long getNullableLong(ResultSet rs, String column) throws SQLException {

		Object value = rs.getObject(column);

		if (value == null) {
			return null;
		}

		return ((Number) value).longValue();
	}
}