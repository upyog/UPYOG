/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.voucher.config.jpadatasource;

import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_CONNECTION_PROVIDER;
import static org.hibernate.cfg.MultiTenancySettings.MULTI_TENANT_IDENTIFIER_RESOLVER;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.egov.finance.voucher.config.multitenant.DomainBasedSchemaTenantIdentifierResolver;
import org.egov.finance.voucher.config.multitenant.MultiTenantSchemaConnectionProvider;
import org.egov.finance.voucher.util.MasterConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.support.SharedEntityManagerBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = MasterConstants.ORG_EGOV_FINANCE)
public class JpaConfiguration {

	private DataSource dataSource;

	@Value("${jpa.showSql:false}")
	private boolean showSQL;

	@Value("${multiTenancy.schema.enabled}")
	private boolean enabledMultiTenant;

	private MultiTenantSchemaConnectionProvider multiTenantConnectionProvider;

	private DomainBasedSchemaTenantIdentifierResolver tenantIdentifierResolver;

	@Autowired
	public JpaConfiguration(DataSource dataSource, MultiTenantSchemaConnectionProvider multiTenantConnectionProvider,
			DomainBasedSchemaTenantIdentifierResolver tenantIdentifierResolver) {
		this.dataSource = dataSource;
		this.multiTenantConnectionProvider = multiTenantConnectionProvider;
		this.tenantIdentifierResolver = tenantIdentifierResolver;
	}

	@Bean
	//@DependsOn("flyway")
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
		factoryBean.setDataSource(dataSource);
		factoryBean.setPersistenceUnitName(MasterConstants.EGOV_PERSISTENCE_UNIT);
		factoryBean.setPackagesToScan(MasterConstants.ORG_EGOV_FINANCE);
		factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
		factoryBean.setJpaPropertyMap(additionalProperties());
		factoryBean.setValidationMode(ValidationMode.NONE);
		factoryBean.setSharedCacheMode(SharedCacheMode.DISABLE_SELECTIVE);
		return factoryBean;
	}

	@Bean
	JpaVendorAdapter jpaVendorAdapter() {
		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
		adapter.setDatabase(Database.POSTGRESQL);
		adapter.setShowSql(true);
		return adapter;
	}

	private Map<String, Object> additionalProperties() {
		Map<String, Object> props = new HashMap<>();
		if (enabledMultiTenant) {
			props.put(MULTI_TENANT_CONNECTION_PROVIDER, multiTenantConnectionProvider);
			props.put(MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
		}

		return props;
	}

	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		return new JpaTransactionManager(emf);
	}

	@Bean
	SharedEntityManagerBean entityManager(EntityManagerFactory emf) {
		SharedEntityManagerBean sem = new SharedEntityManagerBean();
		sem.setEntityManagerFactory(emf);
		return sem;
	}
}
