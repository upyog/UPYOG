# eGov Finance ERP (`egov-erp`)

Maven multi-module EAR for UPYOG municipal finance coexistence: financials (EGF), collections, employee information (EIS), and shared infrastructure (EGI). It is a **Spring Framework** application deployed to **WildFly**, not a Spring Boot service.

This directory is the Maven project root (`org.egov:egov-erp:3.0.2-COE-SNAPSHOT`). Setup scripts (`Makefile`, Ansible) live one level up in `finance/`.

For the JDK 17 / Spring 6 / Hibernate 6 upgrade, see [CHANGE_ME.md](CHANGE_ME.md).

## Purpose

The ERP supports Urban Local Body (ULB) back-office operations:

- Chart of accounts, vouchers, bills, budget, payments, and financial reports (EGF)
- Receipts, remittance, and payment-gateway integration (Collection)
- Employees, positions, and assignments (EIS)
- Masters such as funds, functions, financial years, and UOM (Commons)
- Shared security, workflow, file store, multi-tenancy, and session management (EGI)

Citizen portal modules (`egov-portal`, `egov-portalweb`) exist in this tree but are **not** part of the active Maven reactor.

## Technology stack

| Area | Current version (from parent `pom.xml`) |
| --- | --- |
| Java | 17 (`maven-compiler-plugin` source/target) |
| Spring Framework | 6.1.21 |
| Spring Security | 6.2.4 |
| Spring Data JPA | 3.2.12 |
| Spring Session Data Redis | 3.2.6 |
| Spring Data Elasticsearch | 5.2.12 (see known issues) |
| Hibernate ORM | 6.4.4.Final (`org.hibernate.orm`) |
| Hibernate Validator | 8.0.1.Final |
| Jakarta Persistence | 3.1.0 |
| Jakarta Servlet | 6.0.0 |
| Jakarta Validation | 3.0.2 |
| Struts 2 | 7.1.1 |
| Flyway | 9.22.3 |
| PostgreSQL JDBC | 42.7.13 |
| Redis client (Jedis) | 5.1.2 |
| Elasticsearch client library | 2.4.6 |
| Quartz | 2.5.2 |
| Jackson | 2.21.5 |
| Log4j 2 | 2.26.0 |
| Lombok | 1.18.30 |
| JUnit | 4.13.2 |
| Mockito | 5.23.0 |

This is **not** Spring Boot. Persistence, security, and MVC are XML/Java config on a Jakarta EE application server.

### Important libraries and why they are present

| Library | Role |
| --- | --- |
| Hibernate 6 + Envers | JPA persistence and revision auditing |
| Infinispan Hibernate cache v60 | Second-level cache integration (Hibernate 6 split this out of `hibernate-infinispan`) |
| Struts 2 + convention/json/spring plugins | Legacy action-based UI |
| Spring MVC / Security | REST and remaining Spring controllers, authentication |
| Spring Session Redis | HTTP sessions (indexed sessions for concurrent-login control) |
| Flyway | SQL migrations under `src/main/resources/db/migration` |
| JasperReports + DynamicJasper | Financial PDF/Excel reports |
| Displaytag (`com.github.hazendaz`) | HTML table tags compatible with Jakarta Servlet 6 |
| Apache Tiles fork (`com.github.tntim96.apache.tiles`) | JSP layouts; Spring's tiles3 module was removed |
| SiteMesh Jakarta (`org.egov:sitemesh-jakarta`) | Page decoration, transformed for `jakarta.servlet` |
| Jasypt Hibernate 6 | Encrypted column types |
| AntiSamy + ESAPI (Jakarta classifier) | XSS / HTML sanitization |
| GeoTools + JTS | GIS / shapefile support in EGI |
| Quartz | Scheduled jobs (disabled by default in config) |

## Module structure

```
egov/
├── pom.xml                 Parent BOM, versions, shared dependencies
├── settings.xml            Maven mirror / Nexus credentials
├── egov-config             Shared Spring XML and config resources
├── egov-database           Flyway packaging helper (usually excluded from `package`)
├── egov-egi                Infrastructure: security, JPA, Redis, workflow, file store
├── egov-commons            Shared masters (fund, function, bank, UOM, …)
├── egov-eis                Employee information
├── egov-egf                Financials domain (vouchers, budget, bills, reports)
├── egov-collection         Collections / receipts
├── egov-egiweb             WAR  context /services/egi
├── egov-commonsweb         WAR  context /services/common
├── egov-eisweb             WAR  context /services/eis
├── egov-egfweb             WAR  context /services/EGF
├── egov-collectionweb      WAR  context /services/collection
└── egov-ear                EAR  skinny WARs + shared /lib
```

WARs are packaged as skinny wars; libraries go in the EAR `lib` directory.

## Prerequisites

Verified from the compiler plugin, persistence config, and deployment descriptors. Older docs that mentioned JDK 8 and WildFly 10 are outdated.

- JDK 17
- Maven 3.6+ (verified with 3.6.3; 3.2.x is no longer sufficient for the current plugin versions)
- PostgreSQL (driver 42.7.13; server version is not pinned in this repo — historically 9.4+)
- Redis (standalone). Embedded Redis is **disabled** by default (`redis.enable.embedded=false`)
- Elasticsearch 2.4.x if you still run ES-backed features (transport port 9300). Dashboard ES sources are currently excluded from compilation
- WildFly 40 (Jakarta EE 10). Comments in `egov-ear` and `jboss-deployment-structure.xml` assume WildFly 40, not WildFly 10
- Git
- Access to UPYOG Nexus (`https://upyog-nexus.niua.org/nexus/`) for first-party and transformed artifacts (`sitemesh-jakarta`, `joda-jsptags-jakarta`)

OS: Linux (recommended), macOS, Windows (standalone Redis required).

## Setup

1. Clone the UPYOG repository and work from `finance/egov`.
2. Create `egov-egi/src/main/resources/config/egov-erp-<username>.properties` (or WildFly `egov-erp-override.properties`) and override environment values. `<username>` is `id -un`.
3. Create a PostgreSQL database and schema `generic`, then:

   ```sql
   ALTER ROLE <your_login_role> SET search_path TO generic,public;
   ```

4. Point WildFly datasources at that database (`READWRITE_DS`, optional `READONLY_DS`, optional `QUARTZ_NO_TXN_DS`).
5. Run Redis on `localhost:6379` (or set `redis.host.name` / `redis.host.port`).

### Auto installer (from `finance/`)

```bash
cd finance
make install   # stacks
make build     # Maven package, skips tests and egov-database
make deploy    # copy EAR to WildFly
# or: make all
```

`make build` runs `dev-utils/build.sh`, which executes:

```bash
mvn clean package -pl '!egov-database' -DskipTests -Dmaven.javadoc.skip=true
```

## Build

From this directory (`finance/egov`):

```bash
mvn clean package -s settings.xml \
  -Ddb.user=<db_username> \
  -Ddb.password=<db_password> \
  -Ddb.driver=org.postgresql.Driver \
  -Ddb.url=<jdbc_url>
```

Nexus credentials are read from `settings.xml` as `${nexus.user}` / `${nexus.password}`.

The compiler is configured with `-parameters` because Spring 6 no longer recovers parameter names from bytecode without that flag.

Artifact: `egov-ear/target/egov-ear-3.0.2-COE-SNAPSHOT.ear`.

## Run

1. Copy the exploded EAR into `<JBOSS_HOME>/standalone/deployments`.
2. Touch `egov-ear-3.0.2-COE-SNAPSHOT.ear.dodeploy`.
3. Start WildFly:

   ```bash
   cd <JBOSS_HOME>/bin
   nohup ./standalone.sh -b 0.0.0.0 &
   ```

4. Open `http://localhost:<http_port>/services/egi` (context roots are under `/services/…`, not `/egi`).

Default demo login used historically: username `egovernments`, password `demo`. Confirm against the loaded sample data; this repo does not hard-code that password in application code.

Access by IP or domain requires a matching `eg_city.domainurl` row and starting WildFly with `-b 0.0.0.0`.

## Tests

```bash
mvn test -s settings.xml
```

Tests are JUnit 4 + Mockito 5 + EasyMock 5 + `spring-test`. Several modules have Spring integration tests that expect a container-like context; `make build` skips tests.

JaCoCo is bound to `prepare-package`.

## Configuration

Primary file: `egov-egi/src/main/resources/config/application-config.properties`.

Overrides (later wins):

1. `egov-erp-<username>.properties` on the classpath
2. WildFly module `org.egov.settings` → `egov-erp-override.properties`

| Property | Purpose | Default in repo |
| --- | --- | --- |
| `dev.mode` | Enables sample Flyway scripts and extra diagnostics | `true` |
| `master.server` | Login-audit and similar master-only work | `true` |
| `redis.enable.embedded` | Embedded Redis (not used in LTS deploys) | `false` |
| `redis.host.name` / `redis.host.port` | Standalone Redis | `localhost` / `6379` |
| `multitenancy.enabled` | PostgreSQL schema-per-tenant | `true` |
| `tenant.schemas` | Comma-separated schema names | `generic,citya,…` |
| `tenant.localhost` | Legacy host→schema map (still supported) | `generic` |
| `default.schema.name` | Fallback schema | `generic` |
| `default.jdbc.jndi.datasource` | JTA datasource | `java:/READWRITE_DS` |
| `db.migration.enabled` | Flyway on startup | `true` |
| `scheduler.enabled` | Quartz | `false` |
| `elasticsearch.hosts` / `port` / `cluster.name` | ES 2.4 transport client | `localhost` / `9300` / `elasticsearch` |
| `hibernate.cache.use_second_level_cache` | L2 cache | `false` |
| `jpa.showSql` | SQL logging | `false` |
| `egov.default.services.endpoint` | UPYOG microservice base URL | environment-specific |

Hibernate dialect is `org.hibernate.dialect.PostgreSQLDialect` in `persistence-config.properties` (the Hibernate 5 `PostgreSQL94Dialect` class was removed).

## Database

- PostgreSQL with schema `generic` (and additional tenant schemas when multi-tenancy is on).
- Flyway locations: `db/migration/main`, `sample` (dev only), per-tenant folders, optional `statewide`.
- Naming: `V<YYYYMMDDHHMMSS>__<module>_<description>.sql`.
- Hibernate 6 identifier optimizer is set to `none` with `hibernate.id.sequence.default_allocation_size=1` so existing increment-by-1 sequences do not produce negative IDs.

## Application / API surface

| Context | Module | Role |
| --- | --- | --- |
| `/services/egi` | egov-egiweb | Login, admin masters, workflow inbox |
| `/services/EGF` | egov-egfweb | Financials UI and EGF APIs |
| `/services/collection` | egov-collectionweb | Receipts and remittance |
| `/services/eis` | egov-eisweb | Employee screens |
| `/services/common` | egov-commonsweb | Shared masters |

UI is a mix of Struts 2 actions, Spring MVC controllers, and JSP/Tiles. Collection exposes integration APIs for billing systems (`CollectionIntegrationService`).

## Upgrade notes

See [CHANGE_ME.md](CHANGE_ME.md). Short version:

- JDK 17, Jakarta namespace (`jakarta.*`), Spring 6, Hibernate 6, Struts 7, WildFly 40
- Redis must be standalone
- Compiler `-parameters` is required
- Elasticsearch dashboard Java sources are excluded until the ES client is upgraded

## Troubleshooting

| Symptom | What to check |
| --- | --- |
| `Could not resolve parameter name` in Spring MVC | Rebuild with the parent compiler `-parameters` flag |
| Negative primary keys on insert | Confirm `hibernate.id.sequence.default_allocation_size=1` and optimizer `none` in `JpaConfiguration` |
| Session lost / cannot log in | Redis reachable; `redis.enable.embedded=false`; Spring Session uses indexed sessions |
| Flyway checksum errors | `db.flyway.migration.repair` (use only when the change is understood) |
| `sitemesh-jakarta` / `joda-jsptags-jakarta` missing | Nexus credentials in `settings.xml` |
| Hibernate 7 classes at runtime | Do not export WildFly's `org.hibernate` module; the EAR ships Hibernate 6.4.4 |
| ES dashboard compile errors | Those packages are excluded on purpose; do not re-enable without an ES client upgrade |
| Wrong city data | Tenant thread-local not cleared, or `tenant.schemas` / `eg_city.domainurl` mismatch |

## License

UPYOG / eGovernments GPL 3.0 with additional attribution terms in each source header. See the parent [finance/README.md](../README.md) license link.
