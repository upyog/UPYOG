# State Manual for Metrics Ingestion Pipeline

## Architecture Overview

The Ingestion Pipeline is responsible for extracting, transforming, validating, staging, and loading metrics from local state and ULB databases into the National Dashboard. The ecosystem consists of three coordinated components:

1. **`dashboard-data-extractor` (Spring Boot Service)**:
   - Houses module-specific extractor logic that queries state databases (PT, PGR, CHB) using high-performance multi-tenant batch SQL.
   - Generates memory-safe streaming Excel workbooks (`.xlsx`) or generic flat delimited dataset files (`.psv`, `.csv`, `.txt`) via `DelimitedFileGeneratorService` in a mutually exclusive manner based on configuration properties.
   - Stages datasets to Amazon S3 / FileStore under structured folder hierarchies.
   - Coordinates daily incremental schedulers (`DailyIngestionScheduler`) and historical bulk runs (`LegacyBatchIngestionOrchestrator`, `LegacyIngestionScheduler`) with distributed ShedLock synchronization.
   - Synchronizes active city/ULB tenant registries from eGov MDMS into `ug_ingestion_module_detail`.

2. **`dashboard-data-engine` (Shared Pipeline Library)**:
   - Houses module-agnostic transformation and validation abstractions.
   - Uses dedicated transformers (`PTTransformer`, `PGRTransformer`, `CHBTransformer`) and validators (`PTValidator`, `PGRValidator`) to format raw metrics into `NationalDashboardIngestRequest`.
   - Handles S3 upload routing via `S3DashboardDataLoaderImpl` and `DashboardIngestionClient`.
   - Bridges extractor staging with national ingestion through `BulkIngestionInitService`, notifying the national dashboard upon file upload completion.

3. **`national-dashboard-ingest` (Core Ingestion Service)**:
   - Exposes the bulk ingest initialization API (`POST /bulk/v1/_init`) hosted in `BulkDataIngestController`.
   - Decouples file processing asynchronously using Kafka topic `national-dashboard-bulk-ingest-init`.
   - Guarantees **file-level idempotency** via the `ug_bulk_ingest_job` tracking table in PostgreSQL, skipping duplicate or in-flight processing.
   - Consumes init events via `BulkIngestListener` and downloads S3 files using `S3FileDownloader`.
   - Streams Excel rows in chunks using `ExcelStreamingBatchReader` and ingests records row-by-row via `BatchIngestionProcessor`.
   - Persists detailed external API audit logs via `external-api-audit-persister.yml`.

```
+----------------------------------------------------------------------------------------------------+
|                                     STATE / ULB INFRASTRUCTURE                                     |
|                                                                                                    |
|  +--------------------+        +-----------------------------+        +-------------------------+  |
|  | State DB (Postgres)| -----> |  dashboard-data-extractor   | -----> |   Amazon S3 / FileStore |  |
|  | PT, PGR, CHB data  |        |  - SQL multi-tenant batch   |        |   - <bucket>/<folder>/  |  |
|  +--------------------+        |  - SXSSF Excel streaming    |        |     <state>/<module>/...|  |
|                                +--------------+--------------+        +------------+------------+  |
+-----------------------------------------------|------------------------------------|---------------+
                                                |                                    |
                                                | (triggers bulk init)               | (downloads file)
                                                v                                    v
+----------------------------------------------------------------------------------------------------+
|                                 NATIONAL DASHBOARD CORE INGEST                                     |
|                                                                                                    |
|  +-----------------------------------------------------------+                                     |
|  | national-dashboard-ingest: BulkDataIngestController       |                                     |
|  | - POST /bulk/v1/_init (RequestInfo + S3 fileStoreId)      |                                     |
|  +-----------------------------+-----------------------------+                                     |
|                                |                                                                   |
|                                | publishes to Kafka                                                |
|                                v                                                                   |
|  +-----------------------------------------------------------+                                     |
|  | Kafka Topic: national-dashboard-bulk-ingest-init          |                                     |
|  +-----------------------------+-----------------------------+                                     |
|                                |                                                                   |
|                                | consumed by BulkIngestListener                                    |
|                                v                                                                   |
|  +-----------------------------------------------------------+                                     |
|  | BulkFileProcessorServiceImpl                              |                                     |
|  | 1. Check idempotency: ug_bulk_ingest_job (skip if done)   |                                     |
|  | 2. Download from S3: S3FileDownloader                     |                                     |
|  | 3. Stream Excel rows: ExcelStreamingBatchReader (100 rows)|                                     |
|  | 4. Ingest rows & fallback: BatchIngestionProcessor        |                                     |
|  | 5. Mark status COMPLETED in ug_bulk_ingest_job            |                                     |
|  | 6. Audit logging: external-api-audit-persister.yml        |                                     |
|  | 7. Cleanup temporary disk file in finally block           |                                     |
|  +-----------------------------------------------------------+                                     |
+----------------------------------------------------------------------------------------------------+
```

---

## Persistence Strategy

The extractor service supports two persistence modes, toggled via `dashboard-data.persister.enabled`:

| Mode | Property Value | Implementation Class | Execution Description |
| :--- | :--- | :--- | :--- |
| **Kafka (Default)** | `true` | `KafkaIngestionPersistenceServiceImpl` | Publishes audit records to Kafka topics; `egov-persister` consumes and executes batch SQL writes. |
| **Direct JDBC** | `false` | `JdbcIngestionPersistenceServiceImpl` | Writes directly to the PostgreSQL database via Spring `JdbcTemplate`. |

Both implementations satisfy the `IngestionPersistenceService` interface and are loaded conditionally via `@ConditionalOnProperty`. All beans use constructor injection (`@RequiredArgsConstructor`).

### Active Kafka Topics

| Event / Entity | Kafka Topic Name | Target Database Table |
| :--- | :--- | :--- |
| Daily Ingestion Detail | `save-dashboard-ingestion-detail` | `ug_ingestion_detail` |
| Legacy Job Registration | `save-dashboard-data-module-ingestion-detail` | `ug_legacy_data_ingestion_detail` |
| Legacy Job Status Update | `update-dashboard-data-module-ingestion-detail` | `ug_legacy_data_ingestion_detail` |
| Ingestion Error Logging | `save-dashboard-data-error-log` | `ug_adapter_ingestion_error_log` |
| Module Summary Checkpoint | `update-dashboard-module-summary` | `ug_ingestion_module_summary` |
| Bulk Ingest Job Create | `save-bulk-ingest-job` | `ug_bulk_ingest_job` |
| Bulk Ingest Job Update | `update-bulk-ingest-job` | `ug_bulk_ingest_job` |
| External API Call Audit | `save-external-api-audit` | `external_api_audit_log` |

---

## Ingestion Statuses & Enum Mapping

The pipeline uses the type-safe `IngestionStatus` enum (`org.upyog.dashboard.enums.IngestionStatus`) to evaluate ingestion execution outcomes:

| Status Enum | Description / Behavior |
| :--- | :--- |
| `SUCCESS` | Ingestion succeeded and data was accepted by the National Dashboard. |
| `SUCCESS_ZERO_METRICS` | All extracted metrics for the date are zero. Downstream transmission is skipped to conserve bandwidth, but module tracker date advances cleanly. |
| `SUCCESS_DUPLICATE` | The target date was already ingested (`EG_DS_RECORD_ALREADY_INGESTED_ERR`). Handled as success so the module summary tracker advances. |
| `FAILURE` | Ingestion failed (HTTP 4xx/5xx, timeout, or database exception). Halts catch-up loop. |
| `SKIPPED` | Ingestion was skipped (e.g. module already up to date or already processed). |
| `MISSED_DATE` | Default candidate status when a date in a range has no recorded transaction metrics. |
| `UNKNOWN` | Fallback status for unrecognized status strings (`@JsonCreator` fallback). |

Both `SUCCESS`, `SUCCESS_ZERO_METRICS`, and `SUCCESS_DUPLICATE` return `isSuccess() = true`, enabling `last_successful_date` in `ug_ingestion_module_summary` to advance cleanly without manual intervention.

---

## Storage & S3 Folder Hierarchy

When files are generated and uploaded to AWS S3 (or FileStore), the object key is built via `CommonUtils.buildS3Key(folder, tenantId, moduleName, fileName)`.

The folder structure is organized as follows:

### 1. Daily Ingestion: State-Level Grouping
- **Hierarchy Pattern:**
  `<awsS3Folder>/<state>/<module>/<uuid>_daily_<module>_<timestamp>.xlsx`
- **Parent Folder:** **State Code** (e.g. `pg`).
  - In `S3DashboardDataLoaderImpl`, the ULB identifier (e.g. `pg.citya`) is split at `.` (`payloadUlb.split("\\.")[0]`) to resolve the parent state code.
- **Inner Folder:** **Module Code** (e.g. `PT`, `PGR`, `CHB`).
- **File Prefix:** `daily_` (defined in `DashboardConstants.DAILY`).
- **Sheet Name:** `{MODULE}_daily`.
- **Example S3 Key:**
  `niuatt-internaltech/pg/PT/a89f41b2-3f1d-4b89-9a07-8e6f3328dc41_daily_PT_1725960000.xlsx`

### 2. Legacy Batch Ingestion: ULB / State-Level Grouping
- **Hierarchy Pattern:**
  `<awsS3Folder>/<jobTenantId>/<module>/<uuid>_legacy_<module>_<timestamp>.xlsx`
- **Parent Folder:**
  - **Single ULB Extraction:** Uses the specific ULB tenant ID (e.g. `pg.citya`) when the request targets exactly one ULB.
  - **Multi-Tenant / State-Wide Extraction:** Uses the parent state code (e.g. `pg`) when targeting multiple ULBs, comma-separated ULBs, or when tenant is omitted / set to state code (resolving all active ULBs).
- **Inner Folder:** **Module Code** (e.g. `PT`, `PGR`, `CHB`).
- **File Prefix:** `legacy_` (defined in `DashboardConstants.LEGACY`).
- **Sheet Name:** `{MODULE}_legacy`.
- **Example S3 Key (Single ULB):**
  `niuatt-internaltech/pg.citya/PT/b91c73e1-4c2e-4e90-8b18-7f5e2217cb32_legacy_PT_1725960000.xlsx`
- **Example S3 Key (Multi-Tenant State-Wide):**
  `niuatt-internaltech/pg/PT/c02d84e2-5d3f-5f01-9c29-8a6f3328dc42_legacy_PT_1725960000.xlsx`

### Summary Comparison

| Ingestion Pipeline | Parent Folder | Inner Subfolder | File Prefix | Sheet Name | Example Path |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Daily** | **State** (`pg`) | **Module** (`PT`) | `daily_` | `{MODULE}_daily` | `niuatt-internaltech/pg/PT/<uuid>_daily_PT_...xlsx` |
| **Legacy (Single ULB)** | **ULB** (`pg.citya`) | **Module** (`PT`) | `legacy_` | `{MODULE}_legacy` | `niuatt-internaltech/pg.citya/PT/<uuid>_legacy_PT_...xlsx` |
| **Legacy (Multi-Tenant)** | **State** (`pg`) | **Module** (`PT`) | `legacy_` | `{MODULE}_legacy` | `niuatt-internaltech/pg/PT/<uuid>_legacy_PT_...xlsx` |

---

## Dataset File Generation & Formats

The pipeline supports two high-performance streaming dataset generation formats that operate in a **mutually exclusive** manner based on system configuration:

- **When `dashboard-data.delimited-file.enabled=true`**: Bypasses Excel workbook creation and generates **only** a flat delimited dataset file (e.g. `.psv`).
- **When `dashboard-data.delimited-file.enabled=false`**: Generates **only** an Apache POI SXSSF Excel workbook (`.xlsx`).

### 1. SXSSF Excel Workbooks (`.xlsx`)
- **Generator**: `SXSSFExcelGeneratorService`
- **Memory Safety**: Uses Apache POI `SXSSFWorkbook` with periodic row flushing (window size 100) to disk.
- **Cell Size Limits**: Enforces `EXCEL_MAX_CELL_CHAR_LIMIT` (32,767 characters) for cell contents, ensuring `payload_json` strings do not breach Excel specifications.
- **Column Order**:
  1. `date` — Calendar date (`YYYY-MM-DD`)
  2. `module` — Business module code (`PT`, `PGR`, `CHB`)
  3. `state` — State identifier (e.g. `PG`)
  4. `Tenant` — ULB identifier (e.g. `pg.citya`, renamed from `ulb` for national consistency)
  5. `ward` — Ward name or identifier
  6. `region` — Region name or identifier
  7. `payload_json` — Serialized `NationalDashboardIngestRequest` JSON payload for the row
- **Filtered Composite Objects**: Internal composite metrics objects (`combinedMetrics`, `collectionMetrics`) are strictly excluded from Excel headers and rows to prevent data redundancy and column bloat.

### 2. Flat Delimited Dataset Files (`.psv`, `.csv`, `.txt`)
- **Generator**: `DelimitedFileGeneratorService`
- **System User UUID**: The first column (`uuid`) in delimited files is populated with the authenticated System User UUID (retrieved via `OAuthTokenService.getUserInfo().getUuid()`, matching system credentials in `application.properties`, e.g. `b3177c56-a9f2-4b59-a00c-9e9c38b5f28f`). If `OAuthTokenService` is unavailable, it falls back to `CommonUtils.generateUUID()`.
- **Configurable Delimiters**: Supports state-chosen delimiter characters (e.g. `|`, `,`, `\t`) and file extensions (`.psv`, `.csv`, `.txt`).
- **Dynamic Metric Flattening**: Recursively flattens complex nested KPI metric structures into dot-notation column headers across all business modules:
  - Header structure: `uuid|date|module|state|ulb|ward|region|<metric.path...>`
  - Examples:
    - `cess.usageCategory.RESIDENTIAL`
    - `rebate.usageCategory.COMMERCIAL`
    - `penalty.usageCategory.INDUSTRIAL`
    - `interest.usageCategory.RESIDENTIAL`
    - `propertyTax.usageCategory.COMMERCIAL`
    - `transactions.usageCategory.INDUSTRIAL`
    - `todaysCollection.usageCategory.RESIDENTIAL`

#### Flat Delimited Configuration Options
```properties
# Toggle to enable generic flat delimited dataset file generation (mutually exclusive with Excel)
dashboard-data.delimited-file.enabled=true

# Custom delimiter symbol (e.g. |, ,, \t)
dashboard-data.delimited-file.delimiter=|

# Custom file extension suffix
dashboard-data.delimited-file.file-extension=.psv

# Option to retain generated local files on disk after S3 upload (default: false)
dashboard-data.delimited-file.keep-file=false
```

---

## National Dashboard Bulk Ingestion (`national-dashboard-ingest`)

The bulk ingestion subsystem resides in `national-dashboard-ingest` and processes staged spreadsheets asynchronously.

### 1. Ingestion Initialization (`POST /bulk/v1/_init`)
- Hosted by `BulkDataIngestController` in the `national-dashboard-ingest` service.
- Invoked automatically by `BulkIngestionInitService` (inside `dashboard-data-engine`) immediately following a successful S3 upload:
  ```http
  POST /national-dashboard/bulk/v1/_init
  Content-Type: application/json

  {
    "RequestInfo": {
      "apiId": "Rainmaker",
      "authToken": "...",
      "userInfo": { "uuid": "..." },
      "msgId": "1726484000000|en_IN"
    },
    "details": {
      "fileName": "niuatt-internaltech/pg/PT/a89f41b2_daily_PT_1725960000.xlsx",
      "stateCode": "pg"
    }
  }
  ```
- **Validation & Kafka Dispatch**:
  - Request is validated with `@Valid`.
  - `BulkIngestServiceImpl.init(...)` generates a UUID `jobId` and publishes the `BulkIngestInitRequest` envelope to Kafka topic `national-dashboard-bulk-ingest-init`.
  - Returns `BulkIngestInitResponse` with status `SUCCESS` and HTTP 200 OK.

### 2. Idempotency Tracking & Lifecycle Management
- Implemented in `BulkFileProcessorServiceImpl` and backed by the PostgreSQL `ug_bulk_ingest_job` table.
- **Idempotency Guard**:
  ```java
  Optional<BulkIngestJob> existingJobOpt = bulkIngestJobRepository.findByFileName(fileName);
  if (existingJobOpt.isPresent()) {
      BulkIngestJob existingJob = existingJobOpt.get();
      if ("COMPLETED".equalsIgnoreCase(existingJob.getStatus())) {
          log.warn("Bulk file [{}] already COMPLETED. Skipping duplicate execution.", fileName);
          return;
      }
      if ("IN_PROGRESS".equalsIgnoreCase(existingJob.getStatus())) {
          log.warn("Bulk file [{}] is currently IN_PROGRESS. Skipping duplicate execution.", fileName);
          return;
      }
  }
  ```
- **Lifecycle States**:
  - `INITIATED`: Job initialized upon API request.
  - `IN_PROGRESS`: File download and parsing started.
  - `COMPLETED`: All rows parsed and pushed successfully.
  - `FAILED`: S3 download failure, parsing failure, or unhandled exception.

### 3. Streaming S3 Download & Chunked Parsing
- **S3 Download**: `S3FileDownloader` fetches the file directly into a local temporary file (e.g. `/tmp/bulk_...xlsx`).
- **Batch Processing**: `ExcelStreamingBatchReader` reads the spreadsheet in chunks (default 100 rows) using the SAX event API, avoiding loading the entire workbook into memory.
- **Row Ingestion & Fallback**: `BatchIngestionProcessor.processBatchWithFallback` deserializes `payload_json` from each row and pushes metrics to the dashboard backend.
- **Resource Cleanup**: In the `finally` block of `BulkFileProcessorServiceImpl`, `Files.deleteIfExists(tempFile.toPath())` ensures temporary files on disk are deleted immediately.

---

## Database Schema & Migrations

All tables are prefixed with `ug_` to avoid collisions across shared databases:

| Table Name | Description | Key Indexes |
| :--- | :--- | :--- |
| `ug_ingestion_module_detail` | Active ULB-module mapping registry synchronized from MDMS. | `(module_name, tenant_id)`, `(is_active, tenant_id, module_name)` |
| `ug_ingestion_scheduler_detail` | Execution history log for cron jobs and manual runs. | `(scheduler_name, start_time DESC)`, `(status)` |
| `ug_ingestion_detail` | Daily push execution records per module, date, and tenant. | `(tenant_id, module_name, push_date DESC, ingestion_status)`, `(scheduler_id)`, `(module_detail_id)` |
| `ug_legacy_data_ingestion_detail` | Legacy historical batch push execution records. | `(tenant_id, module_name, push_date DESC, ingestion_status)`, `(tenant_id, module_name, ingestion_status, start_date, end_date)` |
| `ug_ingestion_module_summary` | Checkpoints of last successful and last attempted dates. | `(module_name, last_successful_date)`, UNIQUE `(tenant_id, module_name)` |
| `ug_adapter_ingestion_error_log` | Error log for diagnostics and failure tracking. | `(tenant_id, module_name, error_date)` |
| `shedlock` | Distributed scheduler lock table. | PRIMARY KEY `(name)` |
| `ug_bulk_ingest_job` | File-level idempotency and row count tracker in `national-dashboard-ingest`. | `(file_name)`, `(status)` |

### Schema Evolutions

- **`exception_code` Column (`V20260818140000`):** Present on `ug_ingestion_detail` and `ug_legacy_data_ingestion_detail` to store standardized exception codes upon failure.
- **Foreign Keys to Scheduler Audit (`V20260713050754`):** `module_detail_id` and `scheduler_id` foreign keys tie detail records to `ug_ingestion_module_detail` and `ug_ingestion_scheduler_detail`.
- **Streamlined `ug_ingestion_module_detail` (`V20260907150000`):** Pure active registry mapping ULBs to enabled modules; legacy progress is tracked cleanly in `ug_legacy_data_ingestion_detail` and `ug_ingestion_module_summary`.

---

## Multi-Tenant & MDMS Synchronization

### `TenantSyncService` & `TenantController`
- Synchronizes active city/ULB tenant IDs from eGov MDMS (`tenant` module, `nationalInfo` master) into `ug_ingestion_module_detail`.
- Manages in-memory caching via `@Cacheable` and `@CacheEvict` using centralized cache names (`active_tenants`, `tenant_module_details`).
- REST Endpoints:
  - `POST /extractor/v1/tenants/_sync?stateTenantId={state}` — Triggers MDMS pull and atomically updates `ug_ingestion_module_detail`.
  - `GET /extractor/v1/tenants/_search` — Returns active tenants and module mappings from cache/database.

---

## Key Service Classes

### `DailyIngestionService`
- Fetches active ULB tenants per module via `TenantSyncService.getActiveTenants(...)`.
- Queries `ug_ingestion_module_summary.findAllLastSuccessfulDatesByModule(...)` to bulk fetch checkpoints for all tenants in a single query.
- Executes **multi-tenant batch extraction** (`ModuleExtractor.extractData(List<String> tenantIds, LocalDate targetDate)`) using parameterized SQL queries with `UNNEST(string_to_array(:tenantId, ','))` across configured batch chunks (`dashboard-data.extractor.tenant-batch-size`).
- Performs **catch-up ingestion** across missing date ranges up to yesterday, automatically handling and advancing zero-metric tenants.
- Uses reflection caching (`ConcurrentHashMap`) in `extractTenantId` to eliminate runtime reflection overhead.
- Invokes `saveOrUpdateLastAttemptedDatesBatch` to eliminate N+1 database roundtrips during catch-up iterations.

### `LegacyBatchIngestionOrchestrator`
- Orchestrates high-throughput, memory-safe streaming historical batch ingestion triggered via `POST /api/v1/legacy/batch-ingest`.
- Acquires a module-level distributed ShedLock (`manual_batch_extraction_{MODULE}`) to prevent concurrent conflicting runs.
- **Pre-flight Registry Check**: Verifies `summaryRepository.hasAnyModuleDetails()` to ensure tenant sync has been executed prior to running extraction.
- **Multi-Tenant Resolution**:
  - Accepts a list of ULBs (`"tenantIds": ["pg.citya", "pg.cityb"]`), a single ULB (`"tenantId": "pg.citya"`), or comma-separated ULBs (`"tenantId": "pg.citya,pg.cityb"`).
  - If omitted or specified as the state code (`"pg"`), automatically resolves all active ULBs configured for the module from `ug_ingestion_module_detail`.
- Streams non-zero records into a single combined Excel workbook via `SXSSFExcelGeneratorService.StreamingExcelSession`.
- Uploads the workbook using `DashboardIngestionClient` to S3 under `dashboard/<jobTenantId>/<module>/...` and triggers bulk initialization.
- Saves audit logs in `ug_legacy_data_ingestion_detail` (job summary) and `ug_ingestion_detail` (per-date and per-tenant detail records with specific `module_detail_id` per ULB).

### `LegacyIngestionService`
- Manages bulk historical ingestion via a **two-phase scheduler** approach:
  1. **Populate phase** (`populateLegacyJobs` / `populateLegacyJobsForRange`): Determines which dates in the given range have not yet been ingested and creates `NOT_STARTED` rows in `ug_legacy_data_ingestion_detail`.
  2. **Execute phase** (`executeLegacyJobs`): Fetches pending/failed legacy job rows and runs them through the extractor + dashboard client pipeline.

### `BulkIngestionInitService`
- In `dashboard-data-engine`: Assembles UPYOG `RequestInfo` with OAuth token from `OAuthTokenService` and calls `POST /national-dashboard/bulk/v1/_init`.

### `BulkFileProcessorService` & `ExcelStreamingBatchReader`
- In `national-dashboard-ingest`: Enforces idempotency via `ug_bulk_ingest_job`, downloads S3 files, streams rows in chunks, and delegates to `BatchIngestionProcessor`.

### `IngestionSummaryRepository`
- Queries `ug_ingestion_module_summary` for the last successful date and last attempted date per tenant/module.
- `findSuccessfullyIngestedDates(...)` performs a UNION query across both `ug_ingestion_detail` and `ug_legacy_data_ingestion_detail` to determine already-ingested dates in a range.
- `isLegacyIngestionComplete(...)` verifies legacy completion status from `ug_legacy_data_ingestion_detail`.
- Supports batch attempted date updates via `saveOrUpdateLastAttemptedDatesBatch(...)`.
- Pre-flight check via `hasAnyModuleDetails()` ensures that tenant metadata is present before running legacy batch jobs.

### `IngestionSummaryQueryBuilder`
- Central SQL query factory for all queries against `ug_ingestion_module_summary`, `ug_ingestion_module_detail`, and `ug_legacy_data_ingestion_detail`.
- All query builder methods and constants are documented with Javadoc describing parameters and behavior.

## Utility Classes

### `CommonUtils`
- Provides `getCurrentEpochMillis()` — a single source of truth for timestamps across all persistence operations.
- Provides `buildS3Key(folder, tenantId, moduleName, fileName)` for standardized S3 key generation.

### `HierarchyParser`
- Spring component that parses a dot-notation tenant ID (`state.ulb[.region[.ward]]`) into a `Map<String, String>` of hierarchy levels.
- Default ward and region values are injected from `dashboard-data.metric.ward` and `dashboard-data.metric.region` application properties.
- Used by extractors that need to populate metric hierarchy fields in the `NationalDashboardIngestRequest`.

---

## Onboarding a New State / Module

### 1. Extractor
Create a new Extractor class implementing `ModuleExtractor<T>` inside `dashboard-data-extractor` (`org.upyog.dashboard.extractor.impl`):
- Implement `extractData(List<String> tenantIds, LocalDate targetDate)` using `UNNEST(string_to_array(:tenantId, ','))`.
- Implement `extractData(List<String> tenantIds, LocalDate startDate, LocalDate endDate)`.
- Register the extractor using the `@Component` annotation so that `ExtractorRegistry` automatically discovers it.

### 2. Transformer
Implement `ModuleTransformer<T>` inside `dashboard-data-engine` (`org.upyog.dashboard.transformer.impl`):
- Maps raw database entities/DTOs into `DashboardData` / `NationalDashboardIngestRequest`.
- Register with `@Component` for automatic discovery by `TransformerRegistry`.

### 3. Validator
Implement `ModuleValidator<T>` inside `dashboard-data-engine` (`org.upyog.dashboard.validator.impl`):
- Validates metric consistency (non-null identifiers, valid amounts).
- Register with `@Component` for automatic discovery by `ValidatorRegistry`.

### 4. Application Configuration
Enable the module in `application.properties`:
```properties
extractor.enabled-modules=PT,PGR,CHB,NEW_MODULE
```
Configure module-specific tax heads or classification properties if applicable.

### 5. MDMS Synchronization
Trigger tenant sync so that the active ULB mappings for the new module are registered in `ug_ingestion_module_detail`:
```http
POST /extractor/v1/tenants/_sync?stateTenantId=pg
```

### 6. Historical Data Backfill
Run the streaming legacy batch extraction API to ingest historical records:
```http
POST /api/v1/legacy/batch-ingest
Content-Type: application/json

{
  "moduleName": "NEW_MODULE",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "tenantId": "pg",
  "async": false
}
```

---

## Coding & Architectural Conventions

- **Constructor Injection**: All Spring beans must use constructor injection via `@RequiredArgsConstructor` (Lombok). Field-level `@Autowired` is prohibited.
- **Resource Management**: All streaming sessions, workbooks, and disk-staged files (`File`, `InputStream`) must be closed and deleted in explicit `finally` blocks or try-with-resources.
- **Clean Logging**: Log statements do not prepend class names; MDC and logging formatters supply logger context automatically.
- **Static Formatters & Constants**: `DateTimeFormatter` instances and repeated string constants (`"SYSTEM"`, `"SUCCESS"`, `"FAILURE"`) are maintained as `private static final` fields.

