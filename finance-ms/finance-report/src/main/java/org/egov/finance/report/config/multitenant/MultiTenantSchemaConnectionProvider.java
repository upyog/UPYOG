/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.report.config.multitenant;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.engine.jdbc.connections.spi.AbstractMultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.service.UnknownUnwrapTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MultiTenantSchemaConnectionProvider implements MultiTenantConnectionProvider {
  
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Autowired
    private transient DataSource dataSource;

   
	@Override
	public Connection getConnection(Object tenantIdentifier) throws SQLException {
	    Connection connection = dataSource.getConnection();
	    String schema = tenantIdentifier.toString();

	    try {
	    	connection.setSchema(schema);
	        log.info(" Switched schema using SET SCHEMA '{}'", schema);
	    } catch (SQLException e) {
	        log.error("Schema switch failed for tenant: " + schema, e);
	        throw e;
	    }

	    return connection;
	}
    @Override
    public void releaseConnection(Object tenantId, Connection connection) throws SQLException {
    	String schema = tenantId.toString();
        try {
            connection.setSchema(schema);
        } catch (SQLException e) {
            log.warn("Failed to reset schema on connection release", e);
        } finally {
            connection.close();
        }
    }
    @Override
    public boolean supportsAggressiveRelease() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isUnwrappableAs(Class unwrapType) {
        return MultiTenantConnectionProvider.class.equals(unwrapType)
                || AbstractMultiTenantConnectionProvider.class.isAssignableFrom(unwrapType);
    }

    @Override
    public <T> T unwrap(Class<T> unwrapType) {
        if (isUnwrappableAs(unwrapType))
            return (T) this;
        else
            throw new UnknownUnwrapTypeException(unwrapType);
    }
    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

	
	
}
