package org.upyog.dx.upyogdata.pfms.repository.row_mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionAdditionalDetails;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionEntity;
import org.upyog.dx.upyogdata.pfms.enums.Status;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Maps {@code ug_pfms_transactions} rows to {@link PfmsTransactionEntity},
 * deserializing {@code additionaldetails} JSONB into {@link PfmsTransactionAdditionalDetails}.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PFMSTransactionRowMapper implements RowMapper<PfmsTransactionEntity> {

    private final ObjectMapper objectMapper;

    @Override
    public PfmsTransactionEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        String id = rs.getString("id");
        PfmsTransactionEntity entity = PfmsTransactionEntity.builder()
                .id(id)
                .correlationId(rs.getString("correlation_id"))
                .ulbCodeOrPFMSAgencyCode(rs.getString("agencyCode"))
                .voucherNumber(rs.getString("voucher_number"))
                .voucherDate(rs.getDate("voucher_date").toLocalDate())
                .voucherType(rs.getString("voucherType"))
                .financialYear(rs.getString("financial_year"))
                .build();

        String status = rs.getString("status");
        if (status != null) {
            try {
                entity.setStatus(Status.valueOf(status));
            } catch (IllegalArgumentException ex) {
                log.debug("PFMS row status is not enum INITIATED/FAILED | id={} status={}", id, status);
                entity.setStatus(null);
            }
        }

        var ingestionTimestamp = rs.getTimestamp("ingestion_date");
        if (ingestionTimestamp != null) {
            entity.setIngestionDate(ingestionTimestamp.toLocalDateTime());
        }

        String json = rs.getString("additionaldetails");
        if (json != null && !json.isBlank()) {
            try {
                entity.setAdditionalDetails(objectMapper.readValue(json, PfmsTransactionAdditionalDetails.class));
            } catch (Exception e) {
                log.error("Failed to deserialize PFMS additionaldetails | id={}", id, e);
                throw new SQLException("Failed to parse additionaldetails JSON for transaction id=" + id, e);
            }
        }
        return entity;
    }
}
