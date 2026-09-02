package org.egov.refund.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.egov.refund.model.AuditDetails;
import org.egov.refund.model.Refund;
import org.egov.refund.querybuilder.RefundQueryBuilder;
import org.egov.refund.querybuilder.RefundSearchCriteria;
import org.egov.refund.querybuilder.RefundSearchQuery;
import org.egov.refund.rowmapper.RefundRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class RefundRepository {

	private final JdbcTemplate jdbcTemplate;
	private final ObjectMapper objectMapper;
	private final RefundQueryBuilder refundQueryBuilder;
	private final RefundRowMapper refundRowMapper;

	public RefundRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, RefundQueryBuilder refundQueryBuilder,
			RefundRowMapper refundRowMapper) {

		this.jdbcTemplate = jdbcTemplate;
		this.objectMapper = objectMapper;
		this.refundQueryBuilder = refundQueryBuilder;
		this.refundRowMapper = refundRowMapper;
	}

	public int save(Refund refund) {

		String query = refundQueryBuilder.getInsertQuery();
		AuditDetails audit = refund.getAuditDetails();

		return jdbcTemplate.update(connection -> {

			PreparedStatement ps = connection.prepareStatement(query);

			ps.setObject(1, refund.getId());
			ps.setString(2, refund.getRefundNo());
			ps.setString(3, refund.getTenantId());
			ps.setString(4, refund.getModuleName());
			ps.setString(5, refund.getBusinessService());
			ps.setString(6, refund.getConsumerCode());
			ps.setString(7, refund.getPaymentId());
			ps.setString(8, refund.getApplicantName());
			ps.setString(9, refund.getMobileNumber());
			ps.setString(10, refund.getRefundCategory());
			ps.setString(11, refund.getRefundReason());
			ps.setString(12, refund.getPaymentModeOriginal());
			ps.setBigDecimal(13, refund.getAmountPaid());
			ps.setBigDecimal(14, refund.getRefundAmount());
			ps.setString(15, refund.getRefundMode());
			ps.setString(16, refund.getStatus());
			ps.setString(17, refund.getSanctionRef());

			if (refund.getFinanceApprovalDate() != null) {
				ps.setTimestamp(18, Timestamp.valueOf(refund.getFinanceApprovalDate()));
			} else {
				ps.setTimestamp(18, null);
			}

			ps.setString(19, refund.getGatewayRefundId());

			ps.setString(20, toJson(refund.getBeneficiaryDetails()));

			ps.setString(21, toJson(refund.getAdditionalDetails()));

			ps.setString(22, audit != null ? audit.getCreatedBy() : null);

			ps.setObject(23, audit != null ? audit.getCreatedTime() : System.currentTimeMillis());

			ps.setString(24, audit != null ? audit.getLastModifiedBy() : null);

			ps.setObject(25, audit != null ? audit.getLastModifiedTime() : System.currentTimeMillis());
			ps.setString(26, refund.getFileStoreId());
			return ps;
		});
	}

	public int update(Refund refund) {

		String query = refundQueryBuilder.getUpdateQuery();
		AuditDetails audit = refund.getAuditDetails();

		return jdbcTemplate.update(connection -> {

			PreparedStatement ps = connection.prepareStatement(query);

			ps.setString(1, refund.getRefundNo());
			ps.setString(2, refund.getTenantId());
			ps.setString(3, refund.getModuleName());
			ps.setString(4, refund.getBusinessService());
			ps.setString(5, refund.getConsumerCode());
			ps.setString(6, refund.getPaymentId());
			ps.setString(7, refund.getApplicantName());
			ps.setString(8, refund.getMobileNumber());
			ps.setString(9, refund.getRefundCategory());
			ps.setString(10, refund.getRefundReason());
			ps.setString(11, refund.getPaymentModeOriginal());

			ps.setBigDecimal(12, refund.getAmountPaid());
			ps.setBigDecimal(13, refund.getRefundAmount());

			ps.setString(14, refund.getRefundMode());
			ps.setString(15, refund.getStatus());
			ps.setString(16, refund.getSanctionRef());

			if (refund.getFinanceApprovalDate() != null) {
				ps.setTimestamp(17, Timestamp.valueOf(refund.getFinanceApprovalDate()));
			} else {
				ps.setTimestamp(17, null);
			}

			ps.setString(18, refund.getGatewayRefundId());

			ps.setString(19, toJson(refund.getBeneficiaryDetails()));

			ps.setString(20, toJson(refund.getAdditionalDetails()));

			ps.setString(21, audit != null ? audit.getLastModifiedBy() : null);

			ps.setObject(22, audit != null ? audit.getLastModifiedTime() : System.currentTimeMillis());

			ps.setString(23, refund.getFileStoreId());

			
			// WHERE id = ?
			ps.setObject(24, refund.getId());

			return ps;
		});
	}

	public Refund findById(UUID id) {

		return jdbcTemplate.query(refundQueryBuilder.getFindByIdQuery(), refundRowMapper, id).stream().findFirst()
				.orElse(null);
	}

	public Refund findByRefundNo(String refundNo) {

		return jdbcTemplate.query(refundQueryBuilder.getFindByRefundNoQuery(), refundRowMapper, refundNo).stream()
				.findFirst().orElse(null);
	}

	public int updateStatus(UUID id, String status, String modifiedBy) {

		return jdbcTemplate.update(refundQueryBuilder.getUpdateStatusQuery(), status, modifiedBy,
				System.currentTimeMillis(), id);
	}

	public int updateFinanceApproval(UUID id, String sanctionRef, LocalDateTime approvalDate, String modifiedBy) {

		return jdbcTemplate.update(refundQueryBuilder.getFinanceApprovalQuery(), "FINANCE_APPROVED", sanctionRef,
				approvalDate != null ? Timestamp.valueOf(approvalDate) : null, modifiedBy, System.currentTimeMillis(),
				id);
	}

	public int updateGatewayRefund(UUID id, String gatewayRefundId, String status, String modifiedBy) {

		return jdbcTemplate.update(refundQueryBuilder.getGatewayRefundUpdateQuery(), gatewayRefundId, status,
				modifiedBy, System.currentTimeMillis(), id);
	}

	public List<Refund> search(RefundSearchCriteria criteria) {

		RefundSearchQuery searchQuery = refundQueryBuilder.buildSearchQuery(criteria);

		return jdbcTemplate.query(searchQuery.getQuery(), refundRowMapper, searchQuery.getParams());
	}

	private String toJson(Object value) {

		if (value == null) {
			return null;
		}

		try {
			return objectMapper.writeValueAsString(value);

		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Unable to convert object to JSON", e);
		}
	}
}