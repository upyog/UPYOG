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

package org.egov.finance.master.config.JpaDatasource;

import static org.hibernate.cfg.BatchSettings.BATCH_VERSIONED_DATA;
import static org.hibernate.cfg.BatchSettings.ORDER_INSERTS;
import static org.hibernate.cfg.BatchSettings.ORDER_UPDATES;
import static org.hibernate.cfg.CacheSettings.USE_MINIMAL_PUTS;
import static org.hibernate.cfg.FetchSettings.DEFAULT_BATCH_FETCH_SIZE;
import static org.hibernate.cfg.JdbcSettings.AUTOCOMMIT;
import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER;
import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER;
import static org.hibernate.cfg.TransactionSettings.AUTO_CLOSE_SESSION;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.egov.finance.master.config.MultiTenant.DomainBasedSchemaTenantIdentifierResolver;
import org.egov.finance.master.config.MultiTenant.MultiTenantSchemaConnectionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "org.egov.finance.repository",
    entityManagerFactoryRef = "entityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class JpaConfiguration {

    @Autowired
    private Environment env;

    @Autowired
    private DataSource dataSource;

    @Value("${jpa.showSql:false}")
    private boolean showSQL;

    @Autowired
    private MultiTenantSchemaConnectionProvider multiTenantConnectionProvider;

    @Autowired
    private DomainBasedSchemaTenantIdentifierResolver tenantIdentifierResolver;

  //  @Bean
    @DependsOn("flyway")
    public EntityManagerFactory entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource);
        factoryBean.setPersistenceUnitName("EgovPersistenceUnit");
        factoryBean.setPackagesToScan("org.egov.**.entity");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaPropertyMap(additionalProperties());
        factoryBean.setValidationMode(ValidationMode.NONE);
        factoryBean.setSharedCacheMode(SharedCacheMode.DISABLE_SELECTIVE);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        return adapter;
    }

    private Map<String, Object> additionalProperties() {
        Map<String, Object> props = new HashMap<>();

        // Only bean references and complex props stay here
        if (Boolean.parseBoolean(env.getProperty("multitenancy.enabled", "false"))) {
            props.put(MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
            props.put(MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
        }

        return props;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new JpaTransactionManager(entityManagerFactory());
    }
}
