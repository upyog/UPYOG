# Changelog

All notable changes to the Audit Service will be documented in this file.

## [2.9.0] - 2025-04-21 updated

### Updated

| Dependency | Before | After |
|---|---|---|
| Java | 1.8 | 17 |
| Spring Boot | 2.2.6.RELEASE | 3.2.2 |
| Lombok | 1.18.8 | 1.18.32 |
| tracer | 2.1.1-SNAPSHOT | 2.9.0-SNAPSHOT |
| mdms-client | 0.0.2-SNAPSHOT | 2.9.0-SNAPSHOT |
| postgresql | 42.2.2.jre7 | 42.7.1 |
| flyway-core | no version | 9.22.3 |

### Added
- `spring-boot-starter-validation` — needed for `@Valid`, `@NotNull`, `@Validated` annotations
- `net.minidev:json-smart:2.5.0` — needed for `JSONArray` in `PersisterAuditClientService.java`

### Changed
- `javax.annotation.PostConstruct` → `jakarta.annotation.PostConstruct`
- `javax.validation.*` → `jakarta.validation.*`
- Removed `@javax.annotation.Generated` from model classes

---

## [1.0.0] - 2022-07-01 created
- Initial release of Audit Service
- Audit log creation and search API
- Digital signing of audit logs to prevent tampering
- Database integration with PostgreSQL
- Flyway database migration support
- Kafka integration for processing audit records
- HMAC based signing implementation
