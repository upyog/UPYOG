package org.egov.refund.Repository;

import org.egov.refund.model.RefundAudit;
import org.egov.refund.querybuilder.RefundQueryBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class RefundAuditRepository {

	private final RefundQueryBuilder refundQueryBuilder;
	private final JdbcTemplate jdbcTemplate;
	private final ObjectMapper objectMapper;

	public RefundAuditRepository(JdbcTemplate jdbcTemplate, RefundQueryBuilder refundQueryBuilder,
			ObjectMapper objectMapper) {
		this.jdbcTemplate = jdbcTemplate;
		this.refundQueryBuilder = refundQueryBuilder;
		this.objectMapper = objectMapper;
	}

	public void create(RefundAudit audit) {

		String beneficiaryDetails = convertToJson(audit.getBeneficiaryDetails());

		String additionalDetails = convertToJson(audit.getAdditionalDetails());

		jdbcTemplate.update(refundQueryBuilder.getInsertAuditLogQuery(),

				audit.getAuditId(), audit.getId(), audit.getRefundNo(),

				audit.getTenantId(), audit.getModuleName(), audit.getBusinessService(),

				audit.getConsumerCode(), audit.getPaymentId(),

				audit.getApplicantName(), audit.getMobileNumber(),

				audit.getRefundCategory(), audit.getRefundReason(),

				audit.getPaymentModeOriginal(),

				audit.getAmountPaid(), audit.getRefundAmount(),

				audit.getRefundMode(), audit.getStatus(),

				audit.getSanctionRef(),

				audit.getFinanceApprovalDate(),

				audit.getGatewayRefundId(), audit.getFileStoreId(),

				beneficiaryDetails, additionalDetails,

				audit.getAuditDetails() != null ? audit.getAuditDetails().getCreatedBy() : null,

				audit.getAuditDetails() != null ? audit.getAuditDetails().getCreatedTime() : null,

				audit.getAuditDetails() != null ? audit.getAuditDetails().getLastModifiedBy() : null,

				audit.getAuditDetails() != null ? audit.getAuditDetails().getLastModifiedTime() : null,

				audit.getAuditCreatedTime(), audit.getAction());
	}

	private String convertToJson(Object value) {

		if (value == null) {
			return null;
		}

		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Unable to convert audit details to JSON", e);
		}
	}
}