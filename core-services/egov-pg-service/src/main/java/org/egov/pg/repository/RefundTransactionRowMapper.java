package org.egov.pg.repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.pg.models.AuditDetails;
import org.egov.pg.models.Refund;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static java.util.Objects.isNull;

public class RefundTransactionRowMapper implements RowMapper<Refund> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Refund mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        AuditDetails auditDetails = new AuditDetails(
                resultSet.getString("created_by"),
                resultSet.getLong("created_time"),
                resultSet.getString("last_updated_by"),
                resultSet.getLong("last_updated_time")
        );

        JsonNode additionalDetails = null;
        
        if( ! isNull(resultSet.getObject("additional_details"))){
            String additionalDetailsJson = ((PGobject) resultSet.getObject("additional_details")).getValue();
            try {
                additionalDetails = objectMapper.readTree(additionalDetailsJson);
            } catch (IOException e) {
                throw new CustomException("REFUND_FETCH_FAILED", "Failed to deserialize data");
            }
        }

        return Refund.builder()
                .id(resultSet.getString("id"))
                .refundId(resultSet.getString("refund_id"))
                .originalTxnId(resultSet.getString("original_txn_id"))
                .serviceCode(resultSet.getString("service_code"))
                .originalAmount(resultSet.getString("original_amount"))
                .refundAmount(resultSet.getString("refund_amount"))
                .gatewayTxnId(resultSet.getString("gateway_txn_id"))
                .gateway(resultSet.getString("gateway"))
                .status(Refund.RefundStatusEnum.fromValue(
                        resultSet.getString("status")))
                .tenantId(resultSet.getString("tenant_id"))
                .gatewayStatusCode(resultSet.getString("gateway_status_code"))
                .gatewayStatusMsg(resultSet.getString("gateway_status_msg"))
                .additionalDetails(additionalDetails)
                .auditDetails(auditDetails)
                .build();
    }
}

