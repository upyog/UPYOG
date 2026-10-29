# Dashboard Data Extractor Service

The **Dashboard Data Extractor** (`dashboard-data-extractor`) is the primary metrics extraction, transformation staging, and orchestration service in the UPYOG National Dashboard data pipeline. It extracts application, transaction, and collection metrics from state/ULB databases, packages them into structured and streaming datasets, stages them to Amazon S3 / FileStore, and triggers the asynchronous bulk ingestion pipeline on the `national-dashboard-ingest` core service.

---

## Key Responsibilities

- **Multi-Tenant Batch Extraction**: Executes high-performance parameterized SQL queries across multiple ULBs using PostgreSQL `UNNEST(string_to_array(:tenantId, ','))` across configurable tenant batches (`dashboard-data.extractor.tenant-batch-size`).
- **Dynamic MDMS Synchronization**: Synchronizes active ULB tenants and enabled module mappings from eGov MDMS into `ug_ingestion_module_detail` with automatic in-memory caching.
- **Memory-Safe Streaming Excel Generation**: Utilizes Apache POI `SXSSFWorkbook` to stream large volumes of historical and daily records into `.xlsx` files without heap memory exhaustion.
- **Unified S3 Staging & Dynamic Naming**: Automatically uploads generated daily and legacy workbooks to Amazon S3 / FileStore under standardized folder structures.
- **Downstream Bulk Ingest Trigger**: Integrates with the `national-dashboard-ingest` service by notifying its `_init` endpoint upon S3 upload completion via `BulkIngestionInitService`.
- **Automated Daily Catch-Up & Zero-Metric Handling**: Automatically detects missing dates up to yesterday, extracts metrics, advances checkpoints for zero-metric tenants, and prevents redundant pushes.
- **Dual-Mode Persistence**: Supports asynchronous persistence via Kafka (`egov-persister`) and direct database persistence via JDBC (`JdbcTemplate`).
- **Distributed Scheduling**: Uses ShedLock to coordinate cron executions and manual bulk jobs across multi-instance cluster deployments.

---

## Directory Structure

```
dashboard-data-extractor
 ├── src/main/java/org/upyog/dashboard/
 │    ├── chb/              # Community Hall Booking (CHB) mappers, models, constants
 │    ├── config/           # AppConfig, ShedLockConfig, DashboardExtractorProperties
 │    ├── constants/        # System constants and status codes
 │    ├── controller/       # REST API endpoints (Tenant, Legacy, Daily Test)
 │    ├── entity/           # JPA entities for daily and legacy ingestion data
 │    ├── enums/            # IngestionStatus enums
 │    ├── extractor/        # ModuleExtractor, LegacyBatchExtractor, and implementations (PT, PGR, CHB)
 │    ├── mdms/             # MDMS client service, model, and parser
 │    ├── model/            # DTOs (IngestionModuleDetail, IngestionSchedulerDetail, results, responses)
 │    ├── pgr/              # Public Grievance Redressal (PGR) mappers and constants
 │    ├── pt/               # Property Tax (PT) mappers and constants
 │    ├── repository/       # IngestionSummaryRepository and IngestionSummaryQueryBuilder
 │    ├── scheduler/        # DailyIngestionScheduler and LegacyIngestionScheduler
 │    ├── service/          # Orchestration services (Daily, Legacy, Batch Orchestrator, Tenant Sync)
 │    ├── util/             # CommonUtils
 │    └── DashboardDataExtractorApplication.java  # Spring Boot Main Class
 ├── src/main/resources/
 │    ├── application.properties                 # Base properties & Kafka topics
 │    ├── application-local.properties           # Local profile properties
 │    ├── application-qa.properties              # QA profile properties
 │    ├── dashboard-data-extractor-persister.yml # egov-persister mapping configuration
 │    └── db/migration/main/
 │         └── V20260713050754__extractor_tables.sql  # Database schema definition
 └── pom.xml
```

---

## Prerequisites

- **Java**: JDK 17 or higher
- **Build Tool**: Apache Maven 3.8+
- **Core Library**: `dashboard-data-engine` (version `1.0.6`) must be compiled and installed locally or present in your Maven repository:

```bash
cd ../dashboard-data-engine
mvn clean install
```

---

## How to Build & Run

1. **Package the application JAR**:
   ```bash
   mvn clean package -DskipTests
   ```

2. **Run with Spring Boot (specifying active profile)**:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=qa
   ```

3. **Run executable JAR**:
   ```bash
   java -Dspring.profiles.active=qa -jar target/dashboard-data-extractor-1.0.0.jar
   ```

---

## End-to-End Ingestion Flow

```
+----------------------------+
|  eGov / State Database     |
+-------------+--------------+
              |
              | 1. Parameterized SQL Extraction (UNNEST tenant batching)
              v
+----------------------------+
| dashboard-data-extractor   |
| - Daily / Legacy Service   |
| - SXSSF Excel Streaming    |
+-------------+--------------+
              |
              | 2. Generate .xlsx & Upload to S3
              v
+----------------------------+
|   Amazon S3 / FileStore    |
| (Structured bucket layout) |
+-------------+--------------+
              |
              | 3. POST /national-dashboard/bulk/v1/_init (via BulkIngestionInitService)
              v
+-----------------------------------------------------------+
| national-dashboard-ingest (Core Service)                  |
| - BulkDataIngestController receives init request          |
| - Publishes to Kafka: national-dashboard-bulk-ingest-init |
+-----------------------------+-----------------------------+
                              |
                              | 4. Asynchronous Consumer (BulkIngestListener)
                              v
+-----------------------------------------------------------+
| BulkFileProcessorServiceImpl                              |
| - Idempotency Check (ug_bulk_ingest_job)                  |
| - Download S3 file via S3FileDownloader                   |
| - Streaming Batch Reader (ExcelStreamingBatchReader)      |
| - Row Processing & Ingestion (BatchIngestionProcessor)    |
| - Audit Logging (external-api-audit-persister.yml)        |
+-----------------------------------------------------------+
```

---

## REST API Reference

### 1. MDMS Tenant Synchronization & Search

#### Sync Tenants from MDMS
Fetches active city/ULB tenants and enabled module information from MDMS and synchronizes them into `ug_ingestion_module_detail`.

```http
POST /extractor/v1/tenants/_sync?stateTenantId=pg
```

**Response (200 OK):**
```json
{
  "status": "SUCCESS",
  "message": "Successfully synced 8 module details from MDMS",
  "totalRecordsSynced": 8,
  "details": [
    {
      "detailId": "9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d",
      "tenantId": "pg.citya",
      "moduleName": "PT",
      "isActive": true
    }
  ]
}
```

#### Search Active Tenants
Returns all active tenant IDs and module mappings currently stored in the database or cache.

```http
GET /extractor/v1/tenants/_search
```

---

### 2. Historical & Legacy Batch Ingestion

#### Streaming Batch Ingestion (Recommended)
Streams historical data for a module across a date range directly into a single Excel workbook, uploads it to S3, and triggers bulk ingestion on `national-dashboard-ingest`.

```http
POST /api/v1/legacy/batch-ingest
Content-Type: application/json

{
  "moduleName": "PT",
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "tenantIds": ["pg.citya", "pg.cityb"],
  "tenantId": "pg",
  "async": false
}
```

**Request Parameters:**
- `moduleName` (*required*): Target module (e.g. `PT`, `PGR`, `CHB`).
- `startDate` (*required*): Start date (`YYYY-MM-DD`).
- `endDate` (*required*): End date (`YYYY-MM-DD`).
- `tenantIds` (*optional*): Explicit list of ULB tenant IDs.
- `tenantId` (*optional*): Single ULB ID, comma-separated ULB IDs, or state code (e.g. `pg`). If omitted or set to the state code, the orchestrator automatically resolves all active ULBs configured for the module in `ug_ingestion_module_detail`.
- `async` (*optional*): Whether to run the job asynchronously in the background.

#### Two-Phase Scheduler Ingestion
Pre-populates missing legacy dates in `ug_legacy_data_ingestion_detail` and processes them via schedulers.

```http
POST /api/v1/legacy/ingest?startDate=2024-01-01&endDate=2024-05-31&module=PT
POST /api/v1/legacy/ingest/last-months?months=5&module=PT
```

#### Legacy Jobs Status
Retrieves pending or failed legacy ingestion jobs for a tenant and module.

```http
GET /api/v1/legacy/jobs/status?tenantId=pg&moduleName=PT&limit=100
```

---

### 3. Daily Ingestion Testing

#### Trigger Daily Ingestion Manually
Triggers daily metrics extraction for all configured modules for a specific date or runs daily catch-up up to yesterday.

```http
GET /api/v1/test?date=2026-09-15
```

---

## Configuration Reference

The service is configured via `application.properties` with environment-specific overrides in `application-local.properties`, `application-qa.properties`, etc.:

| Configuration Property | Default / QA Value | Description |
| :--- | :--- | :--- |
| `server.port` | `9999` | HTTP port on which the extractor service listens. |
| `server.servlet.context-path` | `/dashboard-data-extractor` | Servlet context path for all extractor endpoints. |
| `spring.profiles.active` | `qa` | Active Spring environment profile (`local`, `qa`, `prod`). |
| `extractor.enabled-modules` | `PT,PGR,CHB` | Comma-separated list of enabled business modules. |
| `dashboard-data.daily.upload-mode` | `S3` (or `API`) | Ingestion upload strategy for daily runs (`S3` triggers bulk init; `API` performs direct HTTP push). |
| `dashboard-data.legacy.upload-mode` | `S3` | Ingestion upload strategy for legacy runs (`S3` uploads workbook and calls bulk init). |
| `national.dashboard.bulk.init.url` | `http://localhost:8280/national-dashboard/bulk/v1/_init` | Downstream bulk ingest init endpoint on `national-dashboard-ingest`. |
| `national.dashboard.ingest.url` | `http://localhost:8280/national-dashboard/metric/_ingest` | Direct metric ingestion endpoint. |
| `dashboard-data.persister.enabled` | `true` | Persistence mode: `true` for Kafka + `egov-persister`; `false` for direct JDBC. |
| `dashboard-data.extractor.tenant-batch-size` | `50` | Maximum number of ULBs grouped together in a single SQL extraction query. |
| `dashboard-data.legacy.batch-size` | `500` | Number of database records processed per streaming batch chunk during legacy extraction. |
| `dashboard-data.ingestion.batch-size` | `10` | Ingestion batch size for direct HTTP requests and detail database records. |
| `dashboard-data.daily.catch-up-limit-days` | `7` | Maximum number of days allowed for automatic catch-up before halting. |
| `daily.ingestion.cron` | `0 0 1 * * ?` | Cron expression for the daily incremental extraction scheduler. |
| `legacy.ingestion.enabled` | `false` | Enables or disables the legacy background schedulers. |
| `legacy.ingestion.execute.cron` | `0 */3 * * * ?` | Cron expression for executing pending legacy ingestion jobs. |
| `legacy.ingestion.populate.cron` | `0 */30 * * * ?` | Cron expression for populating missing legacy date jobs. |
| `state.level.tenant.id` | `pg` | State-level tenant code fallback. |
| `aws.s3.bucket` | `niuatt-filestore` | Target AWS S3 bucket name. |
| `aws.s3.folder` | `niuatt-internaltech` | Base S3 directory / prefix for uploaded datasets. |
| `aws.s3.region` | `ap-south-1` | AWS region where S3 bucket resides. |
| `egov.mdms.host` | `http://localhost:8181` | Base URL of eGov MDMS service. |
| `egov.mdms.search.endpoint` | `/egov-mdms-service/v1/_search` | Search endpoint for MDMS tenant data. |
| `dashboard-data.timeout.enabled` | `true` | Enables custom Feign / HTTP client timeouts. |
| `dashboard-data.timeout.connect-ms` | `5000` | Connection timeout in milliseconds. |
| `dashboard-data.timeout.read-ms` | `15000` | Read timeout in milliseconds. |

---

## Database Schema & Tables

All extractor database tables are unified under the `ug_` prefix in the state PostgreSQL database:

| Table Name | Description |
| :--- | :--- |
| `ug_ingestion_module_detail` | Registry of active ULB-to-module mappings synchronized from MDMS. |
| `ug_ingestion_scheduler_detail` | Audit history of scheduled cron and manual ingestion executions (`scheduler_id`, status, record counts). |
| `ug_ingestion_detail` | Detailed log of every daily ingestion attempt (`module_ingestion_id`, `push_date`, status, request/response JSON). |
| `ug_legacy_data_ingestion_detail` | Historical and batch legacy ingestion records (`start_date`, `end_date`, status, file reference). |
| `ug_ingestion_module_summary` | Checkpoint tracking table storing `last_successful_date` and `last_attempted_date` per tenant/module. |
| `ug_adapter_ingestion_error_log` | Error log capturing failure diagnostics and stack traces. |
| `shedlock` | Distributed locking table coordinating schedulers across multiple pods. |

---

## egov-persister Integration

When `dashboard-data.persister.enabled=true`, database inserts and updates are published to Kafka topics and written to PostgreSQL by `egov-persister`.

Ensure the persister mappings are configured in `egov-persister`'s `application.properties`:

```properties
egov.persist.yml.repo.path=\
classpath:dashboard-data-extractor-persister.yml,\
classpath:bulk-ingest-job-persister.yml,\
classpath:external-api-audit-persister.yml
```

### Kafka Topics Reference

| Event / Operation | Kafka Topic | Target Table |
| :--- | :--- | :--- |
| Daily Ingestion Push Log | `save-dashboard-ingestion-detail` | `ug_ingestion_detail` |
| Legacy Job Creation | `save-dashboard-data-module-ingestion-detail` | `ug_legacy_data_ingestion_detail` |
| Legacy Job Status Update | `update-dashboard-data-module-ingestion-detail` | `ug_legacy_data_ingestion_detail` |
| Error Logging | `save-dashboard-data-error-log` | `ug_adapter_ingestion_error_log` |
| Summary Checkpoint Upsert | `update-dashboard-module-summary` | `ug_ingestion_module_summary` |
| Bulk Ingest Init Job | `save-bulk-ingest-job` | `ug_bulk_ingest_job` |
| Bulk Ingest Job Update | `update-bulk-ingest-job` | `ug_bulk_ingest_job` |
| External API Call Audit | `save-external-api-audit` | `external_api_audit_log` |

