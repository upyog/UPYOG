/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.config.persistence;

import org.egov.infra.config.persistence.multitenancy.DomainBasedSchemaTenantIdentifierResolver;
import org.egov.infra.config.persistence.multitenancy.MultiTenantSchemaConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.support.ClasspathScanningPersistenceUnitPostProcessor;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.AvailableSettings;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@PropertySource(JpaConstants.PERSISTENCE_CONFIG_LOCATION)
public class JpaConfiguration {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Value(JpaConstants.PROP_JPA_SHOW_SQL)
    private boolean showSQL;

    @Value(JpaConstants.PROP_MULTITENANCY_ENABLED)
    private boolean multiTenancyEnabled;

    @Value(JpaConstants.PROP_HIBERNATE_CACHE_USE_QUERY_CACHE)
    private String enableQueryCache;

    @Value(JpaConstants.PROP_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE)
    private String enableSecondLevelCache;

    @Value(JpaConstants.PROP_HIBERNATE_GENERATE_STATISTICS)
    private String generateStatistics;

    @Value(JpaConstants.PROP_HIBERNATE_JDBC_BATCH_SIZE)
    private Integer batchUpdateSize;

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JtaTransactionManager();
    }

    @Bean
    @DependsOn(JpaConstants.DEPENDS_ON_FLYWAY)
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setJtaDataSource(dataSource);
        entityManagerFactory.setPersistenceUnitName(JpaConstants.PERSISTENCE_UNIT_NAME);
        entityManagerFactory.setPackagesToScan(JpaConstants.PACKAGES_TO_SCAN);
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter());
        entityManagerFactory.setJpaPropertyMap(additionalProperties());
        entityManagerFactory.setValidationMode(ValidationMode.NONE);
        entityManagerFactory.setSharedCacheMode(SharedCacheMode.ENABLE_SELECTIVE);
        ClasspathScanningPersistenceUnitPostProcessor hbmScanner = new ClasspathScanningPersistenceUnitPostProcessor(
                JpaConstants.HBM_SCAN_PACKAGE);
        hbmScanner.setMappingFileNamePattern(JpaConstants.HBM_FILE_PATTERN);
        entityManagerFactory.setPersistenceUnitPostProcessors(hbmScanner);
        entityManagerFactory.afterPropertiesSet();
        return entityManagerFactory.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(env.getProperty(JpaConstants.JPA_DATABASE, Database.class));
        vendorAdapter.setShowSql(showSQL);
        vendorAdapter.setGenerateDdl(env.getProperty(JpaConstants.JPA_GENERATE_DDL, Boolean.class));
        return vendorAdapter;
    }

    private Map<String, Object> additionalProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(JpaConstants.HIBERNATE_VALIDATOR_APPLY_TO_DDL, false);
        properties.put(JpaConstants.HIBERNATE_VALIDATOR_AUTOREGISTER_LISTENERS, false);
        properties.put(JpaConstants.HIBERNATE_TEMP_USE_JDBC_METADATA_DEFAULTS, false);
        properties.put(AvailableSettings.DIALECT, env.getProperty(AvailableSettings.DIALECT));
        properties.put(AvailableSettings.GENERATE_STATISTICS, generateStatistics);
        properties.put(AvailableSettings.CACHE_REGION_FACTORY, env.getProperty(AvailableSettings.CACHE_REGION_FACTORY));

        // FIX: Fallback strategy for .hbm.xml cache tags in Hibernate 6
        properties.put(JpaConstants.HIBERNATE_CACHE_DEFAULT_CONCURRENCY_STRATEGY, JpaConstants.CACHE_CONCURRENCY_STRATEGY_READ_WRITE);

        properties.put(JpaConstants.HIBERNATE_CONNECTION_HANDLING_MODE, JpaConstants.DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION);
        properties.put(AvailableSettings.USE_SECOND_LEVEL_CACHE, enableSecondLevelCache);
        properties.put(AvailableSettings.USE_QUERY_CACHE, enableQueryCache);
        properties.put(AvailableSettings.USE_MINIMAL_PUTS, env.getProperty(AvailableSettings.USE_MINIMAL_PUTS));
        properties.put(JpaConstants.HIBERNATE_CACHE_INFINISPAN_CACHEMANAGER,
                env.getProperty(JpaConstants.HIBERNATE_CACHE_INFINISPAN_CACHEMANAGER));
        properties.put(AvailableSettings.JTA_PLATFORM, env.getProperty(AvailableSettings.JTA_PLATFORM));
        properties.put(AvailableSettings.AUTO_CLOSE_SESSION, env.getProperty(AvailableSettings.AUTO_CLOSE_SESSION));
        properties.put(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, batchUpdateSize);
        properties.put(AvailableSettings.BATCH_VERSIONED_DATA, true);
        properties.put(AvailableSettings.ORDER_INSERTS, true);
        properties.put(AvailableSettings.ORDER_UPDATES, true);
        properties.put(AvailableSettings.AUTOCOMMIT, false);
        properties.put(JpaConstants.JADIRA_USERTYPE_AUTO_REGISTER_USER_TYPES, true);
        properties.put(JpaConstants.JADIRA_USERTYPE_DATABASE_ZONE, JpaConstants.DATABASE_ZONE_JVM);

        // Multitenancy Configuration (Updated for Hibernate 6.x)
        if (multiTenancyEnabled) {
            properties.put(JpaConstants.HIBERNATE_MULTI_TENANCY, env.getProperty(JpaConstants.HIBERNATE_MULTI_TENANCY));
            properties.put(JpaConstants.HIBERNATE_DATABASE_TYPE, env.getProperty(JpaConstants.JPA_DATABASE));
            properties.put(JpaConstants.HIBERNATE_MULTI_TENANCY_CONNECTION_PROVIDER, multiTenantSchemaConnectionProvider());
            properties.put(JpaConstants.HIBERNATE_TENANT_IDENTIFIER_RESOLVER, domainBasedSchemaTenantIdentifierResolver());
        }
        return properties;
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager());
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return transactionTemplate;
    }

    @Bean
    @Lazy
    public MultiTenantSchemaConnectionProvider multiTenantSchemaConnectionProvider() {
        return new MultiTenantSchemaConnectionProvider();
    }

    @Bean
    @Lazy
    public DomainBasedSchemaTenantIdentifierResolver domainBasedSchemaTenantIdentifierResolver() {
        return new DomainBasedSchemaTenantIdentifierResolver();
    }
}