# CHANGE_ME — Finance ERP dependency upgrade

This file records the JDK 17 / Jakarta EE / Spring 6 / Hibernate 6 upgrade that is already present in `finance/egov`. Versions below are taken from the parent `pom.xml`, module POMs, and in-repo comments. Previous versions are listed only when the repository itself states them (old README, commented POM coordinates, or source comments). Unstated previous versions are marked as not verified.

Project coordinates: `org.egov:egov-erp:3.0.2-COE-SNAPSHOT`.

## What was upgraded

The stack moved from a Java EE 7 / JDK 8 / WildFly 10 era (as documented in the former `finance/README.md`) to Jakarta EE 10 / JDK 17 / WildFly 40, with Spring Framework 6 and Hibernate ORM 6.

This is still a **WildFly EAR**, not Spring Boot.

## Version comparison

### Platform (verified previous values from the former README)

| Component | Previous (former README) | Current (this tree) |
| --- | --- | --- |
| JDK | 8 (update 112+) | 17 |
| Application server | JBoss WildFly 10.x | WildFly 40 (from `jboss-deployment-structure.xml` and `egov-ear/pom.xml` comments) |
| Maven | 3.2.x | 3.6.3 verified in this environment; 3.2.x is too old for current plugins |
| PostgreSQL | 9.4 | JDBC driver 42.7.13; server version not pinned |
| Elasticsearch | 2.4.x | Client library still 2.4.6 |
| Redis | Embedded on by default | Standalone; `redis.enable.embedded=false` |

### Libraries (current values from parent `pom.xml`)

| Component | Previous (only if stated in this repo) | Current |
| --- | --- | --- |
| Spring Framework | Not stated numerically; tiles3 / session 1.3 APIs imply Spring 4.x | 6.1.21 |
| Spring Security | Not stated | 6.2.4 |
| Spring Data JPA | Not stated | 3.2.12 |
| Spring Data Envers | Property `1.0.6.RELEASE` left unused; now aligned to Spring Data JPA version | 3.2.12 |
| Spring Session Data Redis | 1.3.2.RELEASE (comment on `UserSessionDestroyListener`) | 3.2.6 |
| Spring Data Elasticsearch | Not stated | 5.2.12 |
| Hibernate ORM | `org.hibernate:hibernate-core` + `hibernate-entitymanager` (commented out as merged in v6) | `org.hibernate.orm:hibernate-core` 6.4.4.Final |
| Hibernate Envers | `org.hibernate:hibernate-envers` | `org.hibernate.orm:hibernate-envers` 6.4.4.Final |
| Hibernate Infinispan | `hibernate-infinispan` (commented out) | `infinispan-hibernate-cache-v60` 14.0.21.Final |
| Hibernate Validator | Not stated | 8.0.1.Final |
| Hibernate dialect | `PostgreSQL94Dialect` (commented in `persistence-config.properties`) | `PostgreSQLDialect` |
| Jakarta Servlet | `javax.servlet` / Servlet 3.1 `web.xml` | `jakarta.servlet-api` 6.0.0 (`web.xml` still declares 3.1 — see follow-up) |
| JSTL | `org.apache.taglibs:taglibs-standard-*` 1.2.6-RC1 | `org.glassfish.web:jakarta.servlet.jsp.jstl` 2.0.0 (parent); `egov-egi` also declares 3.0.1 |
| Struts 2 | Not stated | 7.1.1 |
| Jackson Hibernate module | `jackson-datatype-hibernate` | `jackson-datatype-hibernate6` 2.21.5 |
| Jasypt | `jasypt` / `jasypt-hibernate4` | `jasypt-hibernate6` 1.9.6 |
| Displaytag | `displaytag:displaytag` | `com.github.hazendaz:displaytag` 3.7.0 |
| Apache Tiles | `org.apache.tiles` | `com.github.tntim96.apache.tiles` 3.1.0.0 |
| SiteMesh | `opensymphony:sitemesh` 2.5.0 | `org.egov:sitemesh-jakarta` 2.5.0 classifier `jakarta` |
| Joda JSP tags | `joda-time-jsptags` 1.1.1 | `org.egov:joda-jsptags-jakarta` 1.0.0 classifier `jakarta` |
| Commons Configuration | `commons-configuration` | `commons-configuration2` 2.13.0 |
| Commons DBCP | `commons-dbcp` | `commons-dbcp2` 2.14.0 |
| JTS | `com.vividsolutions:jts` | `org.locationtech.jts:jts-core` 1.20.0 |
| Embedded Redis | `com.orange.redis-embedded` | `it.ozimov:embedded-redis` 0.7.3 |
| BouncyCastle | Not stated | `bcprov-jdk18on` 1.84 |
| Flyway | Not stated | 9.22.3 |
| Log4j | log4j 1.x / commented 2.17.1 | Log4j 2.26.0 (`log4j-api`, `log4j-core`, `log4j-1.2-api`) |
| Mockito | Not stated | 5.23.0 |
| EasyMock | `easymock` + `easymockclassextension` | EasyMock 5.2.0 (class mocking is built in) |
| ESAPI | 2.2.3.x in some modules | Parent: 2.5.3.0 classifier `jakarta`; `egov-egf` still overrides 2.2.3.1 |
| GeoTools | Not stated | 34.4 |
| Quartz | Not stated | 2.5.2 (+ explicit `mchange-commons-java` 0.5.0) |
| Javassist | WildFly module `org.javassist` | Packaged in EAR, 3.29.1-GA (WildFly 40 no longer provides the module) |

Removed / no longer referenced:

- `hibernate-entitymanager` (folded into `hibernate-core`)
- `hibernate-infinispan`
- `springockito` / `springockito-annotations`
- `easymockclassextension`
- `ehcache-web`
- `jsoup` (comment: sanitization via AntiSamy / ESAPI)
- `hamcrest-all` (unused)

## Breaking / behavioural changes that are in the code

### Java / Jakarta

- Imports and APIs use `jakarta.servlet`, `jakarta.persistence`, `jakarta.validation`, `jakarta.mail`, `jakarta.jms`, `jakarta.inject`.
- Compiler target is 17. Lombok annotation processing is 1.18.30 (required for JDK 17). `egov-egf` previously declared Lombok 1.18.8 and was aligned to 1.18.30 to match the parent compiler plugin.

### Spring 6

- Parameter names: `maven-compiler-plugin` sets `<parameters>true</parameters>`.
- `AuditorAware` returns `Optional<User>`.
- `CachingConfigurerSupport` is no longer used; cache config implements `CachingConfigurer`.
- Spring Session uses `@EnableRedisIndexedHttpSession` (not `@EnableRedisHttpSession`) so `FindByIndexNameSessionRepository` still works.
- Session destroy handling is an `ApplicationListener<AbstractSessionEvent>` (`SessionDestroyedEvent` / `SessionExpiredEvent`), not `HttpSessionListener`.
- Redis connections use `RedisStandaloneConfiguration` / `JedisClientConfiguration` (Jedis shard APIs removed).
- Redis cache uses `RedisCacheManager.builder(...)`.
- Shared parent context: Spring 6 dropped `locatorFactorySelector` / `parentContextKey`. `SharedParentApplicationContext` restores EAR parent-context loading.
- Tiles: Spring removed `org.springframework.web.servlet.view.tiles3`. Vendored replacements live in `org.egov.infra.web.spring.tiles`.
- Struts 7 `SessionAware` uses `withSession(...)` instead of `setSession(...)`.

### Hibernate 6

- GroupId `org.hibernate.orm`.
- Typed `createQuery(hql, Class)` and `setParameter` replace `setString` / `setInteger` / `setLong` / `createCriteria` / `LockMode.UPGRADE` in migrated DAOs.
- Spring Data `findOne(id)` was replaced with `findById(id).orElse(null)` where repositories are Spring Data interfaces. Custom service methods named `findOne` remain.
- Identifier generator: `hibernate.id.optimizer.pooled.preferred=none` and `hibernate.id.sequence.default_allocation_size=1` to avoid negative IDs against increment-by-1 sequences.
- Dialect: `org.hibernate.dialect.PostgreSQLDialect`.
- Multi-tenancy: SCHEMA mode; `Connection.setSchema(tenantId)`. Connection release swallows IronJacamar “already committed” errors on WildFly 40.
- `EntityManagerFactory` is exposed explicitly from `LocalContainerEntityManagerFactoryBean`.

### WildFly 40

- Do not export server module `org.hibernate` (server ships Hibernate 7.x; the EAR ships 6.4.4).
- `org.javassist` is packaged in the EAR.
- Old modules commented out: `org.apache.log4j`, `javax.servlet.jstl.api`, `org.apache.xerces`, etc.

### Redis

- Embedded Redis is off. Deployments must run standalone Redis.
- Default in `application-config.properties`: `redis.enable.embedded=false`.

### Flyway

- Flyway 9.22.3. The Flyway 10 PostgreSQL module is commented out and **not** used.
- Preferred tenant list: `tenant.schemas`. Legacy `tenant.<host>=schema` is still read.

### Elasticsearch

- `elastic-search-version` is still 2.4.6 while `spring-data-elasticsearch` is 5.2.12.
- EGI and EGF exclude ES Java sources from compile (`**/elasticsearch/**`, `org/egov/egf/es/**`, dashboard ES services). Those features are not built.

## Code / configuration changes required by the upgrade

Already applied in this tree (do not re-do blindly):

- Jakarta namespace across active modules (`egov-egi`, `commons`, `eis`, `egf`, `collection`, and their WARs).
- Hibernate 6 query/session APIs in migrated DAOs/services.
- Spring Session 3 indexed sessions and Redis 3 connection factories.
- Vendored Tiles + Jakarta SiteMesh / Joda JSP tags from Nexus.
- Displaytag Hazendaz fork.
- `jboss-deployment-structure.xml` adjusted for WildFly 40.
- Compiler `-parameters`.

## Deprecated APIs / configuration that were replaced

| Removed / deprecated | Replacement in this repo |
| --- | --- |
| `javax.*` EE APIs | `jakarta.*` |
| `hibernate-entitymanager` | `hibernate-core` |
| `hibernate-infinispan` | `infinispan-hibernate-cache-v60` |
| `PostgreSQL94Dialect` | `PostgreSQLDialect` |
| `Query.setString/setInteger/...` | `Query.setParameter` |
| `Session.createCriteria` | HQL `createQuery(..., Class)` |
| `LockMode.UPGRADE` | `LockMode.PESSIMISTIC_WRITE` |
| Spring Data `findOne(ID)` | `findById(ID).orElse(null)` |
| `CachingConfigurerSupport` | `CachingConfigurer` |
| `@EnableRedisHttpSession` | `@EnableRedisIndexedHttpSession` |
| `HttpSessionListener` for Redis sessions | Spring Session application events |
| Spring `tiles3` view classes | `org.egov.infra.web.spring.tiles.*` |
| `locatorFactorySelector` parent context | `SharedParentApplicationContext` |
| `commons-configuration` / `commons-dbcp` | `commons-configuration2` / `commons-dbcp2` |
| `taglibs-standard` 1.2 | Jakarta JSTL 2.x/3.x |
| `displaytag:displaytag` | `com.github.hazendaz:displaytag` |
| `com.vividsolutions:jts` | `org.locationtech.jts:jts-core` |

## Database / Hibernate migration considerations

- No Hibernate schema auto-DDL (`jpa.generateDdl=false`). Schema changes remain Flyway SQL.
- Existing sequences must keep increment 1; the persistence config disables pooled optimizers globally.
- Flyway 9 remains; do not assume Flyway 10 `flyway-database-postgresql` is on the classpath.
- Multi-tenancy still means **one database, many schemas**.
- Sample migrations run only when `dev.mode=true`.

No additional Flyway version-table rewrite was found in this repository. If checksums fail after pulling SQL changes, that is an operational Flyway issue, not a Hibernate 6 DDL rewrite.

## Known compatibility issues (verified in tree)

1. **Elasticsearch mismatch** — Spring Data ES 5.2.12 vs Elasticsearch 2.4.6. ES Java sources are excluded from compilation. Dashboards that depended on those classes are not in the current artifact.
2. **`egov-portal` / `egov-portalweb`** — still `javax.*`; **not** in the parent `<modules>` list.
3. **`web.xml`** files still declare Java EE Servlet 3.1 (`http://xmlns.jcp.org/xml/ns/javaee`). Runtime is Jakarta Servlet 6 / WildFly 40. This has not been switched in-repo.
4. **JSTL versions** — parent manages 2.0.0; `egov-egi` also depends on 3.0.1.
5. **ESAPI** — parent 2.5.3.0 `jakarta`; `egov-egf` overrides 2.2.3.1 without classifier.
6. **`javax.servlet.jsp-api` 2.3.3** is still a provided dependency in `egov-egi` alongside Jakarta JSP 3.1.
7. **`spring-security-oauth2` 2.2.0.RELEASE** is a legacy Spring Security OAuth library on Spring 6.
8. **Hibernate Search 5.5.8.Final** remains a property; related ES usage is excluded.
9. **JasperReports 6.1.0** and **Apache POI 3.10.1** are old relative to JDK 17; they remain because reports still compile against them.
10. **`hibernate-search-version` / Elasticsearch 2.4 transport client** are unchanged; they will not work with modern ES clusters.

## Remaining technical debt / follow-up

- Re-enable financial ES dashboards against a single supported Elasticsearch version and a matching client.
- Add `egov-portal*` to the Jakarta migration or remove them from the tree if they are retired.
- Align JSTL, ESAPI, and JSP API dependencies to one Jakarta set.
- Move `web.xml` to the Jakarta 6 schema when WildFly deployment has been regression-tested.
- Replace `spring-security-oauth2` 2.2 with Spring Security 6 OAuth support if OAuth is still required.
- Continue replacing remaining Hibernate Criteria / typed-setter usage if any compile warnings remain in less-used report classes.
- Expand JavaDoc beyond infrastructure and a few public services; most domain classes still have only the GPL header.
- Decide whether Flyway 10 + `flyway-database-postgresql` is needed (currently commented, not active).
- Confirm WildFly 40 custom module set (`org.postgres`, `org.egov.settings`) against the actual server distribution used in deployment — that zip is not in this repository.

## Changes made in this documentation pass

- Added `README.md` and this file.
- JavaDoc on core persistence, cache, session, security, Struts base action, and selected services.
- `CacheConfiguration` now implements `CachingConfigurer` (Spring 6 replacement for `CachingConfigurerSupport`).
- Removed accidental `org.checkerframework.checker.units.qual.A` import from `DBMigrationConfiguration`.
- Removed debug `System.out.println` from Redis session bean creation.
- Aligned `egov-egf` Lombok to 1.18.30 (same as the parent annotation processor / JDK 17).
