# State Manual for Ingestion Pipeline

## Architecture Overview

The Ingestion Pipeline is responsible for extracting, transforming, validating, and loading metrics from local state services into the National Dashboard. It is split into two primary components:

1. **dashboard-data-extractor**: Contains module-specific extractor logic that fetches metrics data from database/APIs and passes it to the generic transformation layer. Includes `DailyIngestionScheduler` and `LegacyIngestionScheduler`.
2. **dashboard-data-engine**: Contains module-agnostic transformation and loading abstractions. Uses standard transformers (`PTTransformer`, `PGRTransformer`) and validators (`PTValidator`, `PGRValidator`) to massage raw metric structures into `NationalDashboardIngestRequest` and push to the ingest endpoint using `HttpLoader`.

The system persists logs to a database via `egov-persister`, making use of Kafka topics (`save-adapter-ingestion-detail`, `save-adapter-module-ingestion-detail`, `save-adapter-error-log`).

## Persistence Strategy

The service supports two persistence modes, toggled via `dashboard-data.persister.enabled`:

| Mode | Value | Implementation class |
|------|-------|----------------------|
| **Kafka (default)** | `true` | `KafkaIngestionPersistenceServiceImpl` — publishes to Kafka topics; `egov-persister` consumes and writes to DB. |
| **JDBC (direct)** | `false` | `JdbcIngestionPersistenceServiceImpl` — writes to DB directly via `JdbcTemplate`. |

Both implementations satisfy the `IngestionPersistenceService` interface and are loaded conditionally via `@ConditionalOnProperty`. All fields use constructor injection (`@RequiredArgsConstructor`).

## Ingestion Statuses & Enum Mapping

The pipeline uses the type-safe `IngestionStatus` enum (`org.upyog.dashboard.enums.IngestionStatus`) to evaluate ingestion execution outcomes:

| Status Enum | Description / Behavior |
| :--- | :--- |
| `SUCCESS` | Ingestion succeeded and data was pushed to the National Dashboard API. |
| `SUCCESS_ZERO_METRICS` | All extracted metrics for the date are zero. Downstream HTTP push is skipped to save bandwidth, but module tracker date advances. |
| `SUCCESS_DUPLICATE` | The target date was already ingested (`EG_DS_RECORD_ALREADY_INGESTED_ERR`). Handled as success so module tracker advances. |
| `FAILURE` | Ingestion failed (HTTP 4xx/5xx, timeout, or database exception). Halts catch-up loop. |
| `SKIPPED` | Ingestion was skipped (e.g. module already up to date). |
| `UNKNOWN` | Fallback status for unrecognized status strings (`@JsonCreator` fallback). |

Both `SUCCESS`, `SUCCESS_ZERO_METRICS`, and `SUCCESS_DUPLICATE` return `isSuccess() = true`, enabling `last_successful_date` in `ingestion_module_summary` to advance cleanly.

## Database Schema

### Tables

| Table | Purpose |
|-------|---------|
| `ingestion_module_detail` | ULB-module configuration and schedule metadata |
| `ingestion_detail` | Daily ingestion audit records per module/date |
| `legacy_data_ingestion_detail` | Legacy (historical daily) ingestion audit records |
| `ingestion_module_summary` | Tracks last successful and last attempted date per tenant/module |
| `adapter_ingestion_error_log` | Error log for ingestion pipeline issues |

### `exception_code` Column (Added: V20260818140000)

Both `ingestion_detail` and `legacy_data_ingestion_detail` now carry an `exception_code VARCHAR(128)` column. It stores a short error/exception code captured when `ingestion_status = FAILURE`. This was added via migration `V20260818140000__add_exception_code_to_ingestion_detail.sql` using `ADD COLUMN IF NOT EXISTS` (safe for existing deployments).

## Key Service Classes

### `DailyIngestionService`
- Iterates over all enabled modules (from `SchemaMappingConfig`) and determines the next date to ingest using `IngestionSummaryRepository.findLastSuccessfulDate(...)`.
- Performs **catch-up ingestion**: if the last success date is more than one day behind yesterday, it fills in the gap date-by-date until it either catches up or a failure halts the loop.
- Enforces a configurable catch-up limit (`dashboard-data.daily-catch-up-limit-days`); if the gap exceeds the limit, it logs an error and recommends using legacy migration.
- Supports two overloads: `ingestDailyData()` (uses yesterday) and `ingestDailyData(LocalDate targetDate)` (for on-demand backfill).

### `LegacyIngestionService`
- Manages bulk historical ingestion via a **two-phase scheduler** approach:
  1. **Populate phase** (`populateLegacyJobs` / `populateLegacyJobsForRange`): Determines which dates in the given range have not yet been ingested and creates `NOT_STARTED` rows in `legacy_data_ingestion_detail`.
  2. **Execute phase** (`executeLegacyJobs`): Fetches pending/failed legacy job rows and runs them through the extractor + dashboard client pipeline.
- Extracts logic into private helpers: `processLegacyJob(...)` for ingestion execution, `serializeRequest(...)` for audit JSON, and `sanitizeResponse(...)`/`sanitizeJson(...)` for safe JSONB storage.
- Uses `@RequiredArgsConstructor` constructor injection instead of `@Autowired` field injection.
- Removed `DashboardProducer` direct dependency; persistence is now fully delegated to `IngestionPersistenceService`.

### `IngestionSummaryRepository`
- Queries `ingestion_module_summary` for the last successful date and last attempted date per tenant/module.
- `findSuccessfullyIngestedDates(...)` performs a UNION query across both `ingestion_detail` and `legacy_data_ingestion_detail` to determine already-ingested dates in a range.
- All persistence side-effects are delegated to `IngestionPersistenceService` (not direct JDBC writes).

### `IngestionSummaryQueryBuilder`
- Central SQL query factory for all queries against `ingestion_module_summary` and the legacy job tables.
- All query builder methods are documented with Javadoc describing parameter order and behaviour.

## Utility Classes

### `CommonUtils`
- Provides `getCurrentEpochMillis()` — a single source of truth for audit timestamps across all persistence operations.

### `HierarchyParser`
- Spring component that parses a dot-notation tenant ID (`state.ulb[.region[.ward]]`) into a `Map<String, String>` of hierarchy levels.
- Default ward and region values are injected from `dashboard-data.metric.ward` and `dashboard-data.metric.region` application properties.
- Used by extractors that need to populate metric hierarchy fields in the `NationalDashboardIngestRequest`.

## Onboarding a New State / Module

### 1. Extractors
- Create a new Extractor class implementing `ModuleExtractor<T>` inside `dashboard-data-extractor` (`org.upyog.dashboard.extractor.impl`).
- Register it using the `@Component` annotation so that `ExtractorRegistry` automatically picks it up based on its implemented generic module type.

### 2. Transformers
- Implement `ModuleTransformer<T>` inside `dashboard-data-engine` (`org.upyog.adapter.transformer.impl`).
- The `TransformerRegistry` automatically maps the Module ENUM to this transformer.

### 3. Validators
- Implement `ModuleValidator<T>` inside `dashboard-data-engine` (`org.upyog.adapter.validator.impl`).
- The `ValidatorRegistry` automatically handles execution before transformation.

### 4. Application Properties
Enable the new module in `application.properties`:
```properties
extractor.enabled-modules=PT,PGR,NEW_MODULE
```

### 5. DB Migration (Optional)
If your state requires new tracking tables, place your migration scripts in `dashboard-data-extractor/src/main/resources/db/migration/main/`. Ensure the new tables are tied to the Kafka producer config within `adapter-service-persister.yml`.

### 6. Legacy Ingestion
You can ingest historical data using the newly built API:
`POST /api/v1/legacy/ingest?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&module=NEW_MODULE`
Check the job statuses using:
`GET /api/v1/legacy/jobs/status?tenantId=pg&moduleName=NEW_MODULE`

## Coding Conventions

- All Spring beans use **constructor injection** via `@RequiredArgsConstructor` (Lombok). Do not use `@Autowired` field injection.
- Repeated string constants (`"SYSTEM"`, `"SUCCESS"`, `"FAILURE"`) are extracted into `private static final` fields within each class.
- Log messages do **not** repeat the class name prefix (e.g., avoid `"ClassName | message"`); the MDC/log format provides the class context automatically.
- Repeated `DateTimeFormatter` instances are stored as `private static final` fields instead of being created inline.
