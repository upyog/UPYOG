
package org.egov.infra.config.persistence;

/**
 * Constants for JPA and Hibernate Persistence Configuration.
 */
public final class JpaConstants {

    // Persistence Unit & Component Scanning
    public static final String PERSISTENCE_UNIT_NAME = "EgovPersistenceUnit";
    public static final String PACKAGES_TO_SCAN = "org.egov.**.entity";
    public static final String HBM_SCAN_PACKAGE = "org.egov";
    public static final String HBM_FILE_PATTERN = "**/*hbm.xml";
    // public static final String DEPENDS_ON_FLYWAY = "flyway";
    // public static final String PERSISTENCE_CONFIG_LOCATION = "classpath:config/persistence-config.properties";

    // Spring Property Keys
    // public static final String PROP_JPA_SHOW_SQL = "${jpa.showSql}";
    // public static final String PROP_MULTITENANCY_ENABLED = "${multitenancy.enabled}";
    // public static final String PROP_HIBERNATE_CACHE_USE_QUERY_CACHE = "${hibernate.cache.use_query_cache}";
    // public static final String PROP_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = "${hibernate.cache.use_second_level_cache}";
    // public static final String PROP_HIBERNATE_GENERATE_STATISTICS = "${hibernate.generate_statistics}";
    // public static final String PROP_HIBERNATE_JDBC_BATCH_SIZE = "${hibernate.jdbc.batch_size}";
    public static final String JPA_DATABASE = "jpa.database";
    public static final String JPA_GENERATE_DDL = "jpa.generateDdl";

    // Hibernate Validator & Metadata Settings
    public static final String HIBERNATE_VALIDATOR_APPLY_TO_DDL = "hibernate.validator.apply_to_ddl";
    public static final String HIBERNATE_VALIDATOR_AUTOREGISTER_LISTENERS = "hibernate.validator.autoregister_listeners";
    public static final String HIBERNATE_TEMP_USE_JDBC_METADATA_DEFAULTS = "hibernate.temp.use_jdbc_metadata_defaults";

    // Hibernate Cache Settings
    public static final String HIBERNATE_CACHE_DEFAULT_CONCURRENCY_STRATEGY = "hibernate.cache.default_cache_concurrency_strategy";
    public static final String CACHE_CONCURRENCY_STRATEGY_READ_WRITE = "read-write";
    public static final String HIBERNATE_CACHE_INFINISPAN_CACHEMANAGER = "hibernate.cache.infinispan.cachemanager";

    // Hibernate Connection & Handling Mode Settings
    public static final String HIBERNATE_CONNECTION_HANDLING_MODE = "hibernate.connection.handling_mode";
    public static final String DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION = "DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION";

    // Jadira Usertype Settings
    public static final String JADIRA_USERTYPE_AUTO_REGISTER_USER_TYPES = "jadira.usertype.autoRegisterUserTypes";
    public static final String JADIRA_USERTYPE_DATABASE_ZONE = "jadira.usertype.databaseZone";
    public static final String DATABASE_ZONE_JVM = "jvm";

    // Multitenancy Settings
    public static final String HIBERNATE_MULTI_TENANCY = "hibernate.multiTenancy";
    public static final String HIBERNATE_DATABASE_TYPE = "hibernate.database.type";
    // public static final String HIBERNATE_MULTI_TENANT_CONNECTION_PROVIDER = "hibernate.multi_tenant_connection_provider";
    // public static final String HIBERNATE_TENANT_IDENTIFIER_RESOLVER = "hibernate.tenant_identifier_resolver";

    private JpaConstants() {
        // Utility class - prevent instantiation
    }
}
