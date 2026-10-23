# Changelog
All notable changes to this module will be documented in this file.

## 2.3.0 - 2026-08-21

### Jakarta EE & Framework Modernization (Spring 6 & Hibernate 6 Migration)

Major platform and framework upgrade migrating from legacy `javax.*` to `jakarta.*` namespace, alongside Spring 6 and Hibernate 6 modernization.

#### Jakarta EE Migration (`javax.*` → `jakarta.*`)
- **Jakarta Persistence (JPA 3.1.0)**: Migrated all entity mappings, repositories, converters, and Envers auditing from `javax.persistence.*` to `jakarta.persistence.*`.
- **Jakarta Servlet (Servlet 6.0.0)**: Upgraded servlet API, updated `web.xml` deployment descriptors to Servlet 6.0 schema (`https://jakarta.ee/xml/ns/jakartaee`), and migrated filters/listeners/wrappers.
- **Jakarta Validation (Bean Validation 3.0.2)**: Upgraded from `javax.validation.*` to `jakarta.validation.*` across all model annotations and custom validators.
- **Jakarta Annotation & JMS**: Migrated `@PostConstruct`, `@PreDestroy`, `@Resource`, and messaging interfaces to `jakarta.annotation.*` and `jakarta.jms.*`.

#### Framework & Dependency Upgrades
- **Spring Framework**: Upgraded from 5.3.31 to **6.1.21** (Spring BOM `6.1.21`).
- **Spring Data JPA & Commons**: Upgraded from 2.7.18 to **3.2.10** (Spring Data BOM `2023.1.9`).
- **Spring Security**: Upgraded from 5.7.11 to **6.2.4**; added `spring-security-oauth2-core` (6.2.4) in preparation for built-in Spring Security 6 OAuth2 migration.
- **Spring Session**: Upgraded from 2.7.4 to **3.2.4**.
- **Hibernate ORM**: Upgraded from 5.6.15.Final to **6.4.8.Final** with `jackson-datatype-hibernate6`.
- **Hibernate Validator**: Upgraded from 6.2.5.Final to **8.0.1.Final**.
- **Infinispan Cache Provider**: Updated to `infinispan-hibernate-cache-v62` (`14.0.35.Final`).
- **DisplayTag**: Replaced legacy `displaytag:1.2` with Jakarta-compatible `com.github.hazendaz:displaytag:3.7.0` (integrated POI export support).
- **Jackson**: Upgraded from 2.13.5 to **2.17.3**.
- **AntiSamy**: Upgraded from 1.7.5 to **1.7.8**.
- **Logging**: Upgraded **Log4j 2** to `2.24.0` and **SLF4J** to `2.0.18`.
- **Maven Compiler Plugin**: Enabled `-parameters` compilation flag for Spring 6.1 parameter name discovery.

#### Core Infra & Web Enhancements
- **ApplicationTenantResolverFilter**: Refactored filter logic for Jakarta Servlet 6; eliminated duplicate header extraction outside conditionals.
- **MultiReadRequestWrapper**: Updated and optimized cached request input stream handling for multiple read operations under Jakarta Servlet 6.
- **EdcrRestService**: Added robust null checks and defensive guards for incoming request JSON parsing and error handling.
- **Persistence & Pagination**:
  - Introduced `JpaConstants` for standardized JPA queries, sorting keys, and pagination defaults.
  - Refactored `Page.java` pagination utility for compatibility with Hibernate 6 query execution.
  - Updated `JpaConfiguration` and `MultiTenantSchemaConnectionProvider` for Hibernate 6 / Jakarta persistence lifecycle.
- **Caching & Properties**:
  - Resolved caching initialization and lookup issues in `EgovMasterDataCaching` and `CacheConfiguration`.
  - Updated `application-config.properties` and `persistence-config.properties` for Spring 6 and Hibernate 6 compatibility.

#### Code Quality & Documentation
- Cleaned up redundant `@SuppressWarnings` annotations in `UniqueCheckValidator` and validator classes.
- Added comprehensive Javadocs across core infra, persistence, and REST service classes.
- Added updated documentation and module README files.

## 2.2.0 - 2026-04-15

### Platform Upgrade (Java 8 → Java 17)

Major runtime and dependency upgrade to modernize the EDCR service stack.

#### Runtime
- Upgraded JDK from 8 to **17** (Maven compiler source/target set to 17)
- Upgraded application server from WildFly 11 to **WildFly 26**
- Docker build now uses `amazoncorretto:17-alpine`; runtime image `nudmcdg/edcr-wildfly26:07`

#### Framework & Persistence
- **Spring Framework** 5.3.31, **Spring Security** 5.7.11, **Spring Data JPA** 2.7.18
- **Hibernate ORM** 5.6.15.Final with EhCache region factory (replaces Infinispan cache on WildFly 26)
- **Hibernate Validator** 6.2.5.Final, **javax.validation** 2.0.1.Final
- JTA platform updated to `JBossAppServerJtaPlatform` for WildFly 26 compatibility
- Connection handling mode set to `DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION`
- Multi-tenant schema provider: `supportsAggressiveRelease` set to `false` to prevent JTA transaction errors

#### Reporting
- **JasperReports** upgraded to 6.20.0
- Added Java 17-safe `jasperreports.properties` (subreport runner factory, JDT compiler target 17, font overrides)
- Bundled custom font extensions (Arial) for PDF report generation

#### Dependencies
- **Jackson** 2.13.5, **PostgreSQL JDBC** 42.7.10, **Flyway** 9.22.3
- **JUnit 5** 5.10.2 and **Mockito** 5.23.0 (replaced JUnit 4 / springockito)
- **Log4j 2** 2.17.1 (replaced legacy log4j 1.x usage)
- Apache Commons libraries upgraded (lang3, io, codec, dbcp, fileupload2, etc.)
- WildFly server modules for Hibernate, Jackson, and Infinispan excluded via `jboss-deployment-structure.xml` to avoid classloading conflicts

#### Code Changes
- Introduced `ICityService` interface for Spring JDK-proxy compatibility in filters and custom impl provider
- `LayerNames` cache: lazy-loaded inside JTA transaction to fix WildFly datasource enlistment errors
- `EgovMasterDataCaching`: local `DefaultCacheManager` replaces removed WildFly Infinispan JNDI lookup
- Embedded Redis startup disabled; standalone Redis configuration required for production
- Report template compilation no longer cached to reflect template changes without restart

## 2.1.2 - 2023-09-14
Central Instance Library Integration

## 2.1.1

- Data push for edcr

## 2.1.0

- Dimension validation feature
- DXF to PDF conversion feature
- Enhance search filters by fromdate, todate, application number, status and added pagination in fetch API

## 2.0.0

- The Extracting data from dxf file module has been made **open source**.
- The new module named **egov-edcr-extract** has been added as part of **eGov-dcr-serivce** repo, it contains all extraction code.
- Enhance river, to support with color code. The color code used to identify river type.
- Capture the level of basement under the ground as dimension with color code 3
- Capture height from floor to the bottom of beam of the stilt floor

## 1.2.0

- Security audit issues fix
- Cleanup unused code and database tables

## 1.1.1

- Enhanced Door, to support door widths with color code. The color code is used to identify type of door.

## 1.1.0
- Added portico feature
- Added glass facade openings feature
- Added Information and Communication Technology landing point feature
- Added capturing mezzanine area at room level
- Enhanced accessory block feature to accommodate units with color code, and multiple distances with color code support
- Enhanced chimney feature to accommodate multiple area and height
- Enhanced parapet feature to accommodate multiple area and height

## 1.0.2

- Extract dimesion values for road reserve
- Extract the room type dynamically based on config available in the database
- Set color code for floor unit
- Extract slope for vehicle ramp

## 1.0.1

- Added Drainage feature
- Added Footpath feauture
- Added supplyline Utility (waterline, electricline, sewerageline, etc...) feature
- Added Road Reserve (In Front, Rear, Side1, and Side2 setback sides) feature

## 1.0.0

- Base version
