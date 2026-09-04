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

/**
 * Multi-tenant schema connection provider for Hibernate.
 *
 * <p>Note (Hibernate 6 Migration):
 * In Hibernate 6, {@link MultiTenantConnectionProvider} is parameterized with the tenant identifier type {@code <T>}.
 * Since UPYOG uses {@link String} tenant identifiers (schema names like "pb.amritsar"),
 * {@code MultiTenantConnectionProvider<String>} is explicitly specified for compile-time type safety.
 */
public class MultiTenantSchemaConnectionProvider implements MultiTenantConnectionProvider<String> {
    private static final long serialVersionUID = -6022082859572861041L;
    private static final Logger LOG = LoggerFactory.getLogger(MultiTenantSchemaConnectionProvider.class);

    @Autowired
    private transient DataSource dataSource;

    /**
     * Obtains an unconfigured JDBC connection from the underlying data source.
     *
     * @return an open {@link Connection}
     * @throws SQLException if a database access error occurs
     */
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Closes and releases an unconfigured JDBC connection back to the pool.
     *
     * @param connection the JDBC connection to release
     * @throws SQLException if an error occurs while closing the connection
     */
    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    /**
     * Obtains a JDBC connection configured with the specific tenant schema.
     *
     * @param tenantId the schema identifier for the target tenant
     * @return the configured {@link Connection} targeting the tenant schema
     */
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

    /**
     * Resets the schema and releases the tenant-specific connection back to the pool.
     *
     * @param tenantId the tenant schema identifier
     * @param connection the JDBC connection to release
     */
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

    /**
     * Determines whether the connection provider supports aggressive release of connections.
     *
     * @return {@code false} to prevent connection release mid-transaction in JTA environments
     */
    @Override
    public boolean supportsAggressiveRelease() {
        /*
          FIX: Must be FALSE in JTA/WildFly environment.
          TRUE causes Hibernate to release connections mid-transaction,
          leading to "Transaction cannot proceed: STATUS_COMMITTED" errors.
        */
        return Boolean.FALSE;
    }

    /**
     * Checks if this connection provider can be unwrapped as the specified target type.
     *
     * @param unwrapType the class type to unwrap
     * @return {@code true} if unwrap is supported, {@code false} otherwise
     */
    @Override
    public boolean isUnwrappableAs(Class<?> unwrapType) {
        return MultiTenantConnectionProvider.class.equals(unwrapType)
                || AbstractMultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    /**
     * Unwraps this instance as the requested type.
     *
     * @param <T> the target type
     * @param unwrapType the target class to unwrap
     * @return this instance cast to the target type
     * @throws UnknownUnwrapTypeException if unwrapping to the specified type is not supported
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType))
            return (T) this;
        else
            throw new UnknownUnwrapTypeException(unwrapType);
    }
}