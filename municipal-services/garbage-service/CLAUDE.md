# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean install -DskipTests

# Run
mvn spring-boot:run

# Or run the jar
java -jar target/garbage-service-1.0.0.jar

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=ClassName

# Package without tests
mvn package -DskipTests
```

The service starts on port **1235** at context path `/garbage-service`. Swagger UI is at `http://localhost:1235/garbage-service/index.html`.

## Architecture

This is a Spring Boot 3.2.2 / Java 17 microservice in the UPYOG/eGov platform for managing municipal garbage collection accounts for Himachal Pradesh (HP UDD).

### Key dependencies
- **egov-tracer 2.9.1-SNAPSHOT** (`TracerConfiguration`) — imported at app startup for distributed tracing and `CustomException` handling.
- **enc-client 2.9.0** — field-level PII encryption via `egov-enc-service`.
- **mdms-client 2.9.0-SNAPSHOT** — master data lookups (rates, categories).
- **javers-core 7.8.6** — audit diffing for account change history.
- **poi-ooxml 5.4.1** — Excel export (`GarbageExcelService`).
- **eGov common contracts** — `RequestInfo`, `ResponseInfo`, `Role`, `User` from `org.egov.common.contract` are used on every request/response.
- **Kafka** — async persistence for account create/update flows and bill payment events.
- **Flyway** — DB migrations under `src/main/resources/db/migration/ddl/`; tracked in table `garbage_service_schema`.
- **Spring Data JDBC** — all repositories use `JdbcTemplate`; no JPA/ORM.

### Request/response pattern
All APIs follow the eGov platform convention: requests carry a `RequestInfo` wrapper, responses carry a `ResponseInfo` wrapper built by `ResponseInfoFactory`. Validation exceptions use `CustomException` which the tracer serializes into platform-standard error responses.

### Layer structure
```
controller/   → REST endpoints; delegates immediately to service layer
service/      → business logic, orchestration of downstream calls
repository/   → JDBC queries; one repository per aggregate root
contract/     → POJOs for external service APIs (billing, workflow, user)
model/        → domain model and request/response DTOs
consumer/     → Kafka listeners (BillTrackerStatusUpdateConsumer, FailureLogSanatizerConsumer)
producer/     → GarbageProducer wraps KafkaTemplate
util/         → GrbgConstants, GrbgUtils, ResponseInfoFactory, RestCallRepository
```

### Core domain
- **GarbageAccount** (`eg_grbg_account`) — the primary aggregate. Each account has a unique `garbage_id` (sequence), optional `parent_account` for sub-accounts, `business_service` for workflow routing, and `additional_detail` (JSONB) for extensible metadata.
- **GrbgApplication** — workflow application linked to an account; status transitions via `egov-workflow-v2`.
- **Billing** — demands/bills are created in `egov-billing-service` (`egov.bill.*` properties). `DemandService` and `BillService` call the billing service over HTTP. `GarbageBillRepository` tracks bill metadata locally in `eg_grbg_bill`.
- **Kafka topics** — `save-grbg-account` / `update-grbg-account` for account persistence; `egov.core.notification.sms` for SMS notifications; `egov.collection.payment-create` consumed by `BillTrackerStatusUpdateConsumer` to update payment status.

### External service integrations
| Service | Purpose | Config prefix |
|---------|---------|---------------|
| egov-workflow-v2 | State machine for application lifecycle | `workflow.*` |
| egov-billing-service | Demand creation and bill fetch | `egov.bill.*`, `egov.demand.*` |
| egov-mdms-service / mdms-v2 | Master data lookup | `egov.mdms.*`, `mdms.v2.*` |
| egov-user-service | Citizen user create/search | `egov.user.*` |
| egov-enc-service | PII encryption/decryption | `egov.enc.*` |
| hpud-dms-service (Alfresco) | Document storage | `egov.alfresco.*` |
| egov-url-shortening | Short links for pay-now SMS | `egov.url.shortning.*` |
| pdf-service | Bill/receipt PDF generation | `egov.report.*` |
| notification-sms | SMS tracker create | `egov.sms.*` |

### Scheduler
`GarbageAccountSchedulerService` / `GarbageAccountSchedulerController` handle batch operations (e.g., bulk bill generation, arrear generation). These are triggered via HTTP POST and store scheduled job state in `eg_grbg_scheduled_requests`.

### API endpoints

**Garbage Accounts** — `/garbage-accounts`
- `POST /_create` — register a new account
- `POST /_update` — update account details
- `POST /_update_status` — workflow status transition
- `POST /_search` — search accounts (`?IsIndex=true` for index-backed search)
- `POST /open/_search` — unauthenticated citizen search; requires at least one of: mobileNumber, applicationNumber, propertyId, oldGarbageIds, name
- `POST /fetch/{CALCULATEFEE|ACTIONS}` — fee calculation or available workflow actions
- `POST /_payNow` — initiate pay-now for a bill
- `POST /_createUserForGarbage` — provision a citizen user
- `POST /_counts` — dashboard aggregate counts
- `POST /_generateGrbgTaxBillReceipt` — generate PDF tax bill receipt
- `POST /_createArear` — generate arrear demand

**Garbage Bills** — `/garbage-bills`
- `POST /_create` / `/_update` / `/_search` / `/_cancelbill`

**Scheduler** — `/garbage-accounts-scheduler`
- `POST /bill-generator` — bulk monthly bill generation
- `POST /on-demand-generation` — on-demand bill generation
- `POST /penalty/_update` — run penalty processing
- `POST /reverse-rebate-amount` — reverse applied rebates
- `POST /extract-tracker` — fetch bill tracker by bill ID

**Common** — `/garbage-common`: create/update master data, aggregate counts
**Excel** — `/garbage-excel`: report exports
**SMS** — `/garbage-sms`: notification management

### Database tables

| Table | Description |
|-------|-------------|
| `eg_grbg_account` | Core account records; JSONB `additional_detail` |
| `eg_grbg_bill` | Bill records (amounts, due dates, penalty, discount) |
| `eg_grbg_application` | Workflow application per account action |
| `eg_grbg_commercial_details` | Commercial property details |
| `eg_grbg_collection` | Collection records |
| `eg_grbg_collection_unit` | Collection unit configuration |
| `eg_grbg_collection_staff` | Staff assignments per unit |
| `eg_grbg_document` | Document attachments |
| `eg_grbg_charge` | Charge/rate master data |
| `eg_grbg_address` | Account addresses |
| `eg_grbg_scheduled_requests` | Scheduler job queue |
| `eg_grbg_old_details` | Legacy ID mapping |
| `eg_grbg_declaration` | Declaration statements |
| `eg_grbg_bill_tracker` | Bill tracker with penalty/rebate state |
| `eg_grbg_account_audit` | Account change audit trail |
| `eg_bill_failure` | Failed bill generation logs |