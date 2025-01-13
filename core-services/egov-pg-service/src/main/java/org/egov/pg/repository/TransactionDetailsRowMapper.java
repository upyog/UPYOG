package org.egov.pg.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.pg.models.AuditDetails;
import org.egov.pg.models.TransactionDetails;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TransactionDetailsRowMapper implements RowMapper<TransactionDetails> {

	@Override
	public TransactionDetails mapRow(ResultSet resultSet, int i) throws SQLException {

		AuditDetails auditDetails = new AuditDetails(resultSet.getString("created_by"),
				resultSet.getLong("created_time"), resultSet.getString("last_modified_by"),
				resultSet.getLong("last_modified_time"));

		return TransactionDetails.builder().uuid(resultSet.getString("uuid")).txnId(resultSet.getString("txn_id"))
				.txnAmount(resultSet.getString("txn_amount")).billId(resultSet.getString("bill_id"))
				.consumerCode(resultSet.getString("consumer_code")).auditDetails(auditDetails).build();
	}
}
