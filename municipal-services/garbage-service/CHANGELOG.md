# Changelog

All notable changes to the Garbage Service are documented here.

Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [Unreleased]

### Added
- Open search endpoint `/garbage-accounts/open/_search` for citizen pay preview without authentication
- `_update_status` endpoint for workflow-only status transitions
- `_createArear` endpoint for generating arrear demands
- `GarbageAccountSchedulerController` with on-demand bill generation, penalty update, rebate reversal, and tracker extraction endpoints
- `GarbageCommonController` with master data create and aggregate count endpoints
- Excel export via `GarbageExcelController` and `GarbageExcelService`
- SMS notification management via `GarbageSmsController` and `GarbageSmsService`
- Bill cancellation endpoint `_cancelbill` on `/garbage-bills`
- `eg_grbg_bill_tracker` table with penalty, rebate, demand ID, and status columns
- `eg_grbg_account_audit` table with indexed lookups by application number, status, and type
- `eg_bill_failure` table for failed bill generation diagnostics
- `eg_grbg_old_details` table for legacy garbage ID mapping
- `eg_grbg_scheduled_requests` table for billing schedule tracking
- `eg_grbg_declaration` table for declaration statement management
- Flyway schema migration `V20260627143000__grbg_schema.sql` covering all tables
- Kafka topic `save-grbg-account` and `update-grbg-account` for async account persistence
- Bill tracker Kafka consumer `BillTrackerStatusUpdateConsumer` on `egov.collection.payment-create`
- Sanitization failure consumer `FailureLogSanatizerConsumer`
- Encryption/decryption integration via `EncryptionService` and `egov-enc-service`
- URL shortening integration for payment links via `UrlShorteningService`
- Alfresco/DMS document upload integration via `AlfrescoService`
- PDF receipt generation via `ReportService` and `PDFRequestGenerator`
- MDMS v2 support alongside legacy MDMS v1
- `GrbgBillTracker` and `EgGrbgBillTracker` models with full row mapper
- `GarbageCountRepository` for dashboard aggregate queries
- `BillSmsView` for SMS-linked bill data views
- SpringDoc OpenAPI 2.3.0 for Swagger UI at `/garbage-service/index.html`
- OpenTelemetry exporters disabled; Spring Web instrumentation auto-configurations excluded for Spring Boot 3.2.2 compatibility

### Changed
- Migrated from Spring Boot 2.x to Spring Boot 3.2.2
- Java version upgraded to 17
- Package renamed from `hp-garbage-service` to `garbage-service`
- Database tables renamed from `hpudd_grbg_*` / `grbg_*` prefix to `eg_grbg_*` for platform consistency
- `garbage_id` column changed to `INT8` with a dedicated sequence `seq_eg_grbg_account_id`
- `/fetch` endpoint now requires path variable (`/fetch/{CALCULATEFEE|ACTIONS}`); returns HTTP 400 for unknown values
- `/_search` on garbage accounts supports optional `?IsIndex=true` query parameter for index-backed search
- Workflow and billing service host configuration separated into individual properties
- Bill expiry configurable via `egov.grbg.bill.expiry.after` (default 30 days)
- Tracer upgraded to `2.9.1-SNAPSHOT`; `enc-client` and `mdms-client` upgraded to `2.9.0`

### Removed
- Legacy `hpudd_grbg_account` and `hpudd_grbg_bill` table names
- Commented-out collection-unit CRUD endpoints from `GarbageCommonController`
- `spring.application.name=hp-garbage-service` (commented out in favour of artifact ID)

### Fixed
- Missing RequestInfo on open search requests now synthesised with `CITIZEN` user type instead of throwing NPE
- Kafka consumer group ID set to `egov-grbg-services` to prevent consumer group conflicts
- `spring.kafka.listener.missing-topics-fatal=false` to allow startup without all topics pre-created

---

## [1.0.0] — LTS Dependency Upgrade

### Changed
- **Java** upgraded from 11 to **17 (LTS)** — long-term support release; enables records, sealed classes, pattern matching preview, and improved GC
- **Spring Boot** upgraded from 2.7.x to **3.2.2 (LTS)** — built on Spring Framework 6.x, requires Jakarta EE 10 namespace (`jakarta.*`)
- **PostgreSQL driver** (`org.postgresql:postgresql`) upgraded to **42.7.1** — latest stable/LTS-aligned release with security patches
- **Flyway** upgraded to version managed by Spring Boot 3.2.2 parent — supports PostgreSQL 15+
- **Apache POI** (`poi-ooxml`) upgraded to **5.4.1** — LTS-aligned release with CVE fixes over 4.x
- **JaVers** (`javers-core`) upgraded to **7.8.6** — compatible with Java 17 and Spring Boot 3
- **SpringDoc OpenAPI** upgraded to **2.3.0** (`springdoc-openapi-starter-webmvc-ui`) — replaces `springfox` and springdoc 1.x; compatible with Spring Boot 3
- **jsoup** upgraded to **1.17.2** — LTS-aligned with security hardening
- **commons-io** upgraded to **2.8.0** — compatible with Java 17 module system
- **Jackson** (`jackson-datatype-jsr310`) version managed by Spring Boot 3.2.2 parent (2.16.x) — Java 8 date/time module aligned with LTS Jackson release
- **egov-tracer** upgraded to `2.9.1-SNAPSHOT` — compatible with Spring Boot 3 and Java 17
- **enc-client** upgraded to `2.9.0` — Jakarta namespace compatible; Kafka transitive deps excluded to avoid version conflicts
- **mdms-client** upgraded to `2.9.0-SNAPSHOT` — aligned with Spring Boot 3 parent
- Maven compiler source and target explicitly set to `17` in `pom.xml`
- `javax.*` imports replaced with `jakarta.*` across all controllers, models, and services (Spring Boot 3 / Jakarta EE 10 requirement)
- OpenTelemetry auto-configurations excluded (`SpringWebMvc6InstrumentationAutoConfiguration`, `SpringWebInstrumentationAutoConfiguration`) — incompatibility between `opentelemetry-instrumentation-api 2.1.0` pulled by tracer and the version managed by Spring Boot 3.2.2
- Management tracing disabled (`management.tracing.enabled=false`) and all OTEL exporters set to `none` for local/non-observability deployments

### Removed
- Spring Boot 2.x parent and all transitive Spring Framework 5.x dependencies
- `springfox-swagger` replaced entirely by SpringDoc OpenAPI 2.x
- `javax.validation` and `javax.servlet` imports (superseded by `jakarta.*`)

---

## [0.0.1-SNAPSHOT] — Initial Release

### Added
- `GarbageAccountController` with create, update, search, fetch, and pay-now endpoints
- `GarbageBillController` with create, update, and search endpoints
- Core domain models: `GarbageAccount`, `GarbageBill`, `GrbgApplication`, `GrbgAddress`, `GrbgCharge`, `GrbgCollection`, `GrbgCollectionUnit`, `GrbgCollectionStaff`, `GrbgDocument`, `GrbgCommercialDetails`
- `GarbageAccountService` with full lifecycle management and workflow integration
- `GarbageBillService` with billing-service demand and bill orchestration
- `DemandService` and `BillService` for billing-service API integration
- `WorkflowService` for egov-workflow-v2 process transition and action lookup
- `UserService` for citizen user creation and search
- `MdmsService` for master data lookups
- `NotificationService` for SMS and email dispatch
- PostgreSQL persistence with JDBC and Spring Data JPA
- Flyway database migration support
- Kafka producer `GarbageProducer` for account save/update events
- `GarbageAccountRowMapper`, `GarbageBillRowMapper` for result set mapping
- Query builders for all entity types
- `GrbgConstants` and `GrbgUtils` utility classes
- `ResponseInfoFactory` for standard eGov response wrapping
- `RequestInfoWrapper` for inbound request deserialization
- HTML email template `GrbgBillEmailTemplate.html`
- Apache POI Excel utility `ExcelUtils` for report generation
- JaVers core for object-level audit diffing
