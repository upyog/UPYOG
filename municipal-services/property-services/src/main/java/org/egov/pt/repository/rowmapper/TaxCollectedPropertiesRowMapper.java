package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.egov.pt.models.TaxCollectedProperties;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TaxCollectedPropertiesRowMapper implements RowMapper<TaxCollectedProperties> {

    @Override
    public TaxCollectedProperties mapRow(ResultSet rs, int rowNum) throws SQLException {

        TaxCollectedProperties taxCollectedProperties = new TaxCollectedProperties();
        taxCollectedProperties.setTransactionId(rs.getString("txn_id"));
        taxCollectedProperties.setAmount(rs.getBigDecimal("txn_amount"));
        taxCollectedProperties.setPropertyId(rs.getString("consumer_code"));
        taxCollectedProperties.setTenantId(rs.getString("tenant_id"));

        return taxCollectedProperties;
    }
}
