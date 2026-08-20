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

/**
 * Constants for JPA and Hibernate Persistence Configuration.
 */
public final class JpaConstants {

    // Persistence Unit & Component Scanning
    public static final String PERSISTENCE_UNIT_NAME = "EgovPersistenceUnit";
    public static final String PACKAGES_TO_SCAN = "org.egov.**.entity";
    public static final String HBM_SCAN_PACKAGE = "org.egov";
    public static final String HBM_FILE_PATTERN = "**/*hbm.xml";
    public static final String DEPENDS_ON_FLYWAY = "flyway";
    public static final String PERSISTENCE_CONFIG_LOCATION = "classpath:config/persistence-config.properties";

    // Spring Property Keys
    public static final String PROP_JPA_SHOW_SQL = "${jpa.showSql}";
    public static final String PROP_MULTITENANCY_ENABLED = "${multitenancy.enabled}";
    public static final String PROP_HIBERNATE_CACHE_USE_QUERY_CACHE = "${hibernate.cache.use_query_cache}";
    public static final String PROP_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE = "${hibernate.cache.use_second_level_cache}";
    public static final String PROP_HIBERNATE_GENERATE_STATISTICS = "${hibernate.generate_statistics}";
    public static final String PROP_HIBERNATE_JDBC_BATCH_SIZE = "${hibernate.jdbc.batch_size}";
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
    public static final String HIBERNATE_MULTI_TENANCY_CONNECTION_PROVIDER = "hibernate.multi_tenancy_connection_provider";
    public static final String HIBERNATE_TENANT_IDENTIFIER_RESOLVER = "hibernate.tenant_identifier_resolver";

    private JpaConstants() {
        // Utility class - prevent instantiation
    }
}
