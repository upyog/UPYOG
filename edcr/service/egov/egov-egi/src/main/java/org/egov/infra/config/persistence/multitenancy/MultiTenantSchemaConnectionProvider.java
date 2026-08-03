package org.egov.infra.config.persistence.multitenancy;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MultiTenantSchemaConnectionProvider implements MultiTenantConnectionProvider {
    private static final long serialVersionUID = -6022082859572861041L;
    private static final Logger LOG = LoggerFactory.getLogger(MultiTenantSchemaConnectionProvider.class);

    @Autowired
    private transient DataSource dataSource;

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantId) {
        try {
            Connection connection = getAnyConnection();
            connection.setSchema(tenantId);
            return connection;
        } catch (SQLException e) {
            LOG.error("Error occurred while switching tenant schema upon getting connection. " +
                      "Could not alter JDBC connection to specified schema [" + tenantId + "]", e);
        }
        return null;
    }

    @Override
    public void releaseConnection(String tenantId, Connection connection) {
        try {
            /*
              FIX: Reset to public schema before release.
              Wrapped in try-catch because in JTA environments the transaction
              may already be committed (STATUS_COMMITTED) when this is called,
              making setSchema() illegal. Safe to ignore — connection pool
              will reset the schema on next borrow anyway.
            */
            try {
                connection.setSchema("public");
            } catch (SQLException e) {
                LOG.debug("Could not reset schema on connection release (JTA already committed) - ignoring: {}", 
                          e.getMessage());
            }
            releaseAnyConnection(connection);
        } catch (SQLException e) {
            LOG.warn("Error occurred while releasing connection", e);
        }
    }

    @Override
    public boolean supportsAggressiveRelease() {
        /*
          FIX: Must be FALSE in JTA/WildFly environment.
          TRUE causes Hibernate to release connections mid-transaction,
          leading to "Transaction cannot proceed: STATUS_COMMITTED" errors.
        */
        return Boolean.FALSE;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return MultiTenantConnectionProvider.class.equals(unwrapType)
                || AbstractMultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType))
            return (T) this;
        else
            throw new UnknownUnwrapTypeException(unwrapType);
    }
}