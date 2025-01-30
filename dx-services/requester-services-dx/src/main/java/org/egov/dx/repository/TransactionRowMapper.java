package org.egov.dx.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.egov.dx.web.models.Transaction;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.jdbc.core.RowMapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static java.util.Objects.isNull;

public class TransactionRowMapper implements RowMapper<Transaction> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Transaction mapRow(ResultSet resultSet, int i) throws SQLException {



        return Transaction.builder()
                .txnId(resultSet.getString("txn_id"))
                .tenantId(resultSet.getString("tenant_Id"))
                .fileStoreId(resultSet.getString("filestore_id"))
                .signedFilestoreId(resultSet.getString("signed_filestore_id"))
                .tenantId(resultSet.getString("module"))
                .consumerCode(resultSet.getString("consumercode"))
                .createdTime(resultSet.getLong("created_time"))
                .createdBy(resultSet.getString("created_by"))
                .lastModifiedBy(resultSet.getString("last_modified_by"))
                .lastModifiedTime(resultSet.getLong("last_modified_time"))
                .build();
    }
}
