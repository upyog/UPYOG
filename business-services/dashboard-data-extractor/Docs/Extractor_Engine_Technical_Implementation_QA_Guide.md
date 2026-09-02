# Extractor and Ingestion Engine Services: Technical Implementation & QA Reference Manual

**Author:** Principal Software Engineer & Technical Writing Team  
**Audience:** Quality Assurance (QA) Engineers, Software Engineers, Data Engineers, Technical Support Teams  
**Status:** Production-Ready  
**Date:** August 19, 2026  

---

## 1. Document Overview & Purpose

This document serves as the authoritative, end-to-end technical reference for the **QA and Testing Engineering Team**. Its purpose is to provide a complete breakdown of the architectural design, ingestion logic, data transformation pipelines, validation rules, error handling strategies, and database schemas governing the **Dashboard Data Extractor** (`dashboard-data-extractor`) and **Dashboard Data Engine** (`dashboard-data-engine`) services.

### Core Objectives for QA Verification
1. **Understand Data Lineage:** Map how operational domain records (e.g., Property Tax, PGR complaints, Public Booking/CHB, Finance, Advertisement) are ingested from source DBs, parsed, transformed into unified metrics, validated, and pushed to the National Dashboard Ingestion API.
2. **Verify Business Logic & Edge Case Handling:** Gain complete visibility into automated catch-up windowing, legacy historical back-fills, token generation, backoff retry policies, and exception handling.
3. **Database & Schema Auditing:** Inspect table DDLs, indexes, nullability constraints, and primary/foreign keys across all audit logging and tracking tables to formulate SQL-based test assertions.
4. **End-to-End Test Design:** Execute test plans covering happy path runs, partial batch failures, API connection timeouts, payload validation errors, and state summary rollbacks.

---

## 2. Architecture & Service Breakdown

The analytics ingestion architecture follows a decoupled **Extractor-Engine** pattern. The pipeline isolates database querying and raw metric assembly (Extractor) from payload transformation, schema validation, security token management, HTTP delivery, and audit logging (Engine).

```
                      +------------------------------------------+
                      |           Core Domain DBs                |
                      |   (eg_pt_property, eg_pgr_service, etc.)  |
                      +------------------------------------------+
                                           |
                                           v
                       +----------------------------------------+
                       |       dashboard-data-extractor         |
                       |  - Daily / Legacy Schedulers           |
                       |  - ModuleExtractor implementations     |
                       |  - ExtractorRegistry                   |
                       |  - Catch-up Window & Date Tracking     |
                       +----------------------------------------+
                                           |
                                ( Ingestion Payload )
                                           v
                       +----------------------------------------+
                       |        dashboard-data-engine           |
                       |  - DashboardClientImpl Orchestrator    |
                       |  - TransformerRegistry & Transformers  |
                       |  - Common & Module Validators          |
                       |  - OAuthTokenService (User Authentication)|
                       |  - DashboardDataLoader (Feign + Retry) |
                       +----------------------------------------+
                                  /                  \
   (HTTP Ingest Metrics Payload) v                    v (Async Audit Event)
+------------------------------------+   +------------------------------------+
|  National Dashboard Ingest API     |   |   Audit Service / Kafka Topic      |
|  (External Analytics Gateway)      |   |   (ingestion_detail, summary DB)   |
+------------------------------------+   +------------------------------------+
```

### High-Level Service Responsibilities

| Service | Primary Responsibility | Key Components | Tech Stack / Libraries |
| :--- | :--- | :--- | :--- |
| **`dashboard-data-extractor`** | Data Ingestion & Source Querying | Schedulers, `ModuleExtractor` implementations (`PtModuleExtractor`, `PgrModuleExtractor`, `ChbModuleExtractor`), `ExtractorRegistry`, Row Mappers, `IngestionSummaryRepository`. | Java 17, Spring Boot, Spring JDBC / NamedParameterJdbcTemplate, Cron Scheduler, Jackson. |
| **`dashboard-data-engine`** | Business Logic, Transformation, Validation, Delivery & Auditing | `DashboardClientImpl`, `TransformerRegistry`, `ModuleTransformer` beans (`PTTransformer`, `PGRTransformer`, etc.), `CommonValidator`, `OAuthTokenService`, `DashboardDataLoaderImpl`, `RetryUtil`, `JdbcAuditServiceImpl`. | Java 17, Spring Boot, Spring Cloud OpenFeign, Gson, Jackson, Kafka Producer, PostgreSQL JDBC. |

---

## 3. Extractor Service Implementation

The `dashboard-data-extractor` service acts as the data extraction gateway. It queries operational microservice databases, aggregates raw transactional counts/amounts, constructs un-transformed metric representations, and manages date-based ingestion windows.

### 3.1 Input Sources & Trigger Modes

Data extraction occurs via three trigger mechanisms:

1. **Daily Scheduled Ingestion (`DailyIngestionScheduler`):**
   - **Trigger:** Cron schedule (e.g., `0 0 2 * * ?` at 2:00 AM daily).
   - **Target Window:** Processes data for `yesterday` (`LocalDate.now().minusDays(1)`).
   - **Catch-up Capability:** If the service was offline, it determines the `last_successful_date` from `ingestion_module_summary` and attempts to catch up day-by-day.
   - **Catch-up Safety Guard:** Enforces a configurable limit (`dashboardProperties.getDailyCatchUpLimitDays()`). If the gap exceeds this threshold, daily catch-up aborts with an error directing administrators to use legacy back-fill.

2. **Legacy Historical Ingestion (`LegacyIngestionScheduler` & `LegacyIngestionController`):**
   - **Trigger:** REST API (`POST /dashboard/v1/legacy/_ingest`) or bulk migration jobs.
   - **Target Window:** Date ranges for historical periods (monthly/daily back-fills).
   - **Audit Tracking:** Writes initial `NOT_STARTED` rows to `legacy_data_ingestion_detail` before processing.

3. **On-Demand Manual Trigger (`IngestionTestController`):**
   - **Trigger:** API invocation for specific dates or modules for QA verification.

### 3.2 Processing & Extraction Logic Flow

```
   [ DailyIngestionScheduler / REST Request ]
                       |
                       v
         DailyIngestionService.ingestDailyData()
                       |
                       +---> Query enabled modules from SchemaMappingConfig
                       |
                       +---> Fetch last_successful_date from ingestion_module_summary
                       |
                       +---> Calculate Catch-Up Range (startDate to yesterday)
                       |
                       +---> Check Catch-Up Limit (If days > limit -> ABORT & LOG)
                       |
                       v
   [ For Each Target Date in Range ]
                       |
                       +---> Update last_attempted_date in ingestion_module_summary
                       |
                       +---> Fetch registered ModuleExtractor from ExtractorRegistry
                       |
                       +---> Invoke ModuleExtractor.extractData(date)
                       |         |
                       |         +---> Execute SQL via NamedParameterJdbcTemplate
                       |         +---> Map ResultSet using custom RowMapper (e.g., PTRowmapper)
                       |         +---> Return Domain Metric Objects / DashboardData list
                       v
         Pass Extracted Data to Engine (dashboardClient.execute(request))
```

### 3.3 Extractor Code Snippet

```java
// Extractor Service Execution Loop (DailyIngestionService.java)
public List<IngestionResult> ingestDailyData() {
    List<IngestionResult> allResults = new ArrayList<>();
    List<Module> enabledModules = schemaMappingConfig.getEnabledModules();
    LocalDate yesterday = LocalDate.now().minusDays(1);
    LocalDate defaultStartDate = parseDefaultStartDate();

    for (Module module : enabledModules) {
        ModuleExtractor<?> extractor = extractorRegistry.get(module);
        Optional<LocalDate> lastSuccessOpt = summaryRepository.findLastSuccessfulDate(tenantId, module.name());
        LocalDate startDate = lastSuccessOpt.map(date -> date.plusDays(1)).orElse(defaultStartDate);

        if (startDate.isAfter(yesterday)) {
            log.info("Module {} is already up-to-date up to yesterday ({}). Skipping.", module, yesterday);
            continue;
        }

        long daysToIngest = ChronoUnit.DAYS.between(startDate, yesterday) + 1;
        if (daysToIngest > dashboardProperties.getDailyCatchUpLimitDays()) {
            log.error("Catch-up gap of {} days exceeds max limit of {} days for module {}.", daysToIngest, catchUpLimit, module);
            allResults.add(buildResult("FAILURE", module, yesterday, "Catch-up gap exceeded limit", null));
            continue;
        }

        processDateRange(module, extractor, startDate, yesterday, allResults);
    }
    return allResults;
}
```

### 3.4 Exception Handling & Error Boundaries

- **Missing Extractor:** If a module is enabled in configuration but lacks a registered Spring `ModuleExtractor` bean, the engine logs an error and skips the module without crashing the job.
- **Sequential Halt on Error:** During multi-day catch-up, if date `T` fails, processing for dates `T+1`, `T+2` for that specific module is immediately halted. This prevents out-of-order date ingestion gaps in downstream analytics systems.

---

## 4. Engine Service Implementation

The `dashboard-data-engine` service executes business transformations, payload validations, authorization context injection, downstream HTTP posting, retry exponential backoffs, and audit persistence.

### 4.1 Engine Processing Pipeline Architecture

The main entry point is `DashboardClientImpl.execute(DashboardRequest request)`, which coordinates four primary components:

```
+-----------------------------------------------------------------------------------+
|                            DashboardClientImpl                                    |
|                                                                                   |
|  1. ModuleTransformer     2. CommonValidator    3. OAuthTokenService             |
|     (Raw -> Metric DTO)      (Field Validation)     (Fetch Bearer Token & User)   |
|            |                        |                      |                      |
|            v                        v                      v                      |
|  +-----------------------------------------------------------------------------+  |
|  |                NationalDashboardIngestRequest Payload Builder                |  |
|  +-----------------------------------------------------------------------------+  |
|                                     |                                             |
|  4. DashboardDataLoaderImpl          v                                             |
|     +---> Serializes Request JSON via ObjectMapper                                 |
|     +---> POST to National Dashboard URL via DashboardFeignClient                  |
|     +---> Executes Exponential Backoff + Jitter Retry Loop on Error                |
|     +---> Emits Audit Data to AuditService / Kafka Persistence Topic               |
+-----------------------------------------------------------------------------------+
```

### 4.2 Core Engine Subsystems

#### A. Transformers (`ModuleTransformer`)
Transforms raw SQL row maps / DTOs into standardized `DashboardPayload` objects containing structured metrics (`totalApplications`, `todaysCompletedApplications`, `totalCollection`, usage categorizations, payment modes, etc.).
- Active Transformers: `PTTransformer`, `PGRTransformer`, `CHBTransformer`, `ADVTransformer`, `FinanceTransformer`.

#### B. Validation (`CommonValidator` & `ModuleValidator`)
Prior to dispatch, `CommonValidator` inspects the output payload and enforces cross-module invariants:
- `module` must not be null or blank.
- `state` / `tenantId` must be valid tenant strings.
- `metrics` array must be non-empty and contain valid non-negative operational values.

#### C. Authentication & Context Injection (`OAuthTokenService`)
Generates OAuth2 authentication tokens using superuser credentials via User Service (`UserFeignClient`). Injects a standard `RequestInfo` header containing:
- `apiId`: `"Rainmaker"`
- `authToken`: Valid OAuth2 bearer token
- `userInfo`: System user context object
- `msgId`: Epoch millis timestamp string formatted as `<epochMillis>|en_IN`

#### D. Delivery & Exponential Retry Mechanism (`DashboardDataLoaderImpl` & `RetryUtil`)
Sends the transformed payload via `DashboardFeignClient`. If downstream calls fail (HTTP 5xx, network timeouts, Feign exceptions):
1. Captures HTTP status and error body.
2. Checks if retry is enabled (`dashboardProperties.isIngestRetryEnabled()`) and current attempt < max attempts (`dashboardProperties.getIngestMaxAttempts()`).
3. Calculates backoff with randomized jitter using `RetryUtil.calculateBackoffWithJitter(attempt, baseDelayMs, maxDelayMs)`.
4. Sleeps thread for calculated backoff time before retrying.
5. If all retries fail, logs a `FAILURE` status and writes the failure reason and exception stack trace to database audit logs.

```java
// Exponential Backoff with Jitter Calculation (RetryUtil.java)
public static long calculateBackoffWithJitter(int attempt, long baseDelayMs, long maxDelayMs) {
    long expBackoff = baseDelayMs * (long) Math.pow(2, attempt - 1);
    long cappedBackoff = Math.min(expBackoff, maxDelayMs);
    // Apply +/- 20% jitter
    double jitterFactor = 0.8 + (Math.random() * 0.4);
    return (long) (cappedBackoff * jitterFactor);
}
```

---

## 5. Database Schema & Table Details

This section details all PostgreSQL database tables managed by Flyway migrations (`V20260713050754__add_scheduler_tables.sql` and `V20260818140000__add_exception_code_to_ingestion_detail.sql`).

```
+--------------------------+       1:N       +----------------------------+
|  ingestion_module_detail |<----------------|      ingestion_detail      |
|  (Configuration & Cron)  |                 |   (Daily Audit Log Runs)   |
+--------------------------+                 +----------------------------+
            |
            | 1:N                            +----------------------------+
            +------------------------------->| legacy_data_ingestion_detail|
                                             |   (Legacy Back-fill Logs)  |
                                             +----------------------------+

+--------------------------+                 +----------------------------+
| ingestion_module_summary |                 | adapter_ingestion_error_log|
| (Latest Ingest Trackers) |                 | (Execution Error Traces)   |
+--------------------------+                 +----------------------------+
```

---

### Table 1: `ingestion_module_detail`
**Purpose:** Stores configuration and schedule metadata for each ULB-module ingestion pipeline stream. Serves as the master record for ULB module enablement.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `detail_id` | `VARCHAR(64)` | `PRIMARY KEY, NOT NULL` | Application-generated UUID uniquely identifying the ULB-module mapping. |
| `tenant_id` | `VARCHAR(64)` | `NOT NULL` | Tenant identifier for the Urban Local Body (e.g., `pg.citya`). |
| `ulb_name` | `VARCHAR(256)` | `NOT NULL` | Human-readable name of the Urban Local Body (e.g., `City A Municipal Corporation`). |
| `module_name` | `VARCHAR(64)` | `NOT NULL` | Short code identifying the business module (e.g., `PT`, `TL`, `PGR`, `CHB`). |
| `is_active` | `BOOLEAN` | `NOT NULL, DEFAULT TRUE` | Controls whether this ULB-module stream is enabled for automated scheduled runs. |
| `is_legacy_data_ingested` | `BOOLEAN` | `NOT NULL, DEFAULT FALSE` | Flag indicating whether historical/legacy data back-fill has been completed. |
| `last_ingested_date` | `DATE` | `NULLABLE` | The most recent calendar date for which daily ingestion successfully finished. |
| `schedule_cron` | `VARCHAR(128)` | `NULLABLE` | Cron expression for module execution (NULL if manually triggered or default). |
| `created_by` | `VARCHAR(256)` | `NOT NULL, DEFAULT 'SYSTEM'` | Audit field tracking the creator user ID or system component. |
| `created_time` | `BIGINT` | `NOT NULL` | Epoch timestamp (in milliseconds) when the configuration row was created. |
| `last_modified_by` | `VARCHAR(256)` | `NOT NULL, DEFAULT 'SYSTEM'` | Audit field tracking who last updated the row configuration. |
| `last_modified_time` | `BIGINT` | `NOT NULL` | Epoch timestamp (in milliseconds) of the most recent modification. |

**Indexes & Constraints:**
- `CONSTRAINT pk_ingestion_module_detail PRIMARY KEY (detail_id)`
- `CREATE INDEX idx_ingestion_module_detail_tenant_module ON ingestion_module_detail (tenant_id, module_name) WHERE is_active = TRUE;`

---

### Table 2: `ingestion_detail`
**Purpose:** Audit log of every daily ingestion push attempt made to the National Dashboard. Stores full JSON request payloads, response payloads, status outcomes, and error codes.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `module_ingestion_id` | `VARCHAR(64)` | `PRIMARY KEY, NOT NULL` | Application-generated UUID for the ingestion attempt. |
| `module_detail_id` | `VARCHAR(64)` | `FOREIGN KEY, NULLABLE` | References `ingestion_module_detail(detail_id)` ON DELETE SET NULL. |
| `tenant_id` | `VARCHAR(64)` | `NULLABLE` | Tenant identifier for the ULB whose data was pushed. |
| `module_name` | `VARCHAR(64)` | `NULLABLE` | Short module code (e.g., `PT`, `PGR`, `CHB`). |
| `push_date` | `DATE` | `NULLABLE` | Specific calendar date for which the operational metrics were extracted. |
| `request_data` | `JSONB` | `NULLABLE` | Full outbound JSON payload sent (`NationalDashboardIngestRequest`). |
| `response_data` | `JSONB` | `NULLABLE` | HTTP response body (on SUCCESS) or exception message payload (on FAILURE). |
| `ingestion_status` | `VARCHAR(32)` | `NULLABLE` | Execution status outcome: `SUCCESS` or `FAILURE`. |
| `exception_code` | `VARCHAR(128)` | `NULLABLE` | Short exception/error code captured when `ingestion_status = FAILURE`. |
| `created_by` | `VARCHAR(256)` | `NULLABLE` | Identifier of user/system that triggered the daily ingestion push. |
| `created_time` | `BIGINT` | `NULLABLE` | Epoch timestamp (ms) when this audit record was inserted. |
| `last_modified_by` | `VARCHAR(256)` | `NULLABLE` | Identifier of user/system that last updated the record status. |
| `last_modified_time` | `BIGINT` | `NULLABLE` | Epoch timestamp (ms) of the last update to this audit record. |

**Indexes & Constraints:**
- `CONSTRAINT pk_ingestion_detail PRIMARY KEY (module_ingestion_id)`
- `CONSTRAINT fk_ingestion_detail_module_detail FOREIGN KEY (module_detail_id) REFERENCES ingestion_module_detail (detail_id) ON DELETE SET NULL`
- `CREATE INDEX idx_ingestion_detail_tenant_module_date ON ingestion_detail (tenant_id, module_name, push_date DESC);`
- `CREATE INDEX idx_ingestion_detail_status ON ingestion_detail (ingestion_status) WHERE ingestion_status IN ('FAILURE', 'NOT_STARTED');`

---

### Table 3: `legacy_data_ingestion_detail`
**Purpose:** Audit log of historical and bulk legacy data ingestion push attempts.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `module_ingestion_id` | `VARCHAR(64)` | `PRIMARY KEY, NOT NULL` | Unique UUID generated for the legacy ingestion execution run. |
| `module_detail_id` | `VARCHAR(64)` | `FOREIGN KEY, NULLABLE` | References `ingestion_module_detail(detail_id)` ON DELETE SET NULL. |
| `tenant_id` | `VARCHAR(64)` | `NULLABLE` | Tenant identifier for the target ULB. |
| `ulb_name` | `VARCHAR(256)` | `NULLABLE` | Human-readable name of the Urban Local Body. |
| `module_name` | `VARCHAR(64)` | `NULLABLE` | Short module code (e.g., `PT`, `PGR`, `CHB`). |
| `push_date` | `DATE` | `NULLABLE` | Target historical calendar date being back-filled. |
| `user_id` | `VARCHAR(64)` | `NULLABLE` | Identifier of the operator/admin who initiated the legacy migration API call. |
| `request_data` | `JSONB` | `NULLABLE` | Full outbound `NationalDashboardIngestRequest` JSON payload. |
| `response_data` | `JSONB` | `NULLABLE` | Downstream HTTP response body or exception traceback. |
| `ingestion_status` | `VARCHAR(32)` | `NOT NULL, DEFAULT 'NOT_STARTED'` | Migration lifecycle state: `NOT_STARTED`, `SUCCESS`, or `FAILURE`. |
| `exception_code` | `VARCHAR(128)` | `NULLABLE` | Categorized error code if the historical push failed. |
| `created_by` | `VARCHAR(256)` | `NULLABLE` | Audit user/system creator identifier. |
| `created_time` | `BIGINT` | `NULLABLE` | Epoch timestamp (ms) of creation. |
| `last_modified_by` | `VARCHAR(256)` | `NULLABLE` | Audit user/system modifier identifier. |
| `last_modified_time` | `BIGINT` | `NULLABLE` | Epoch timestamp (ms) of last modification. |

**Indexes & Constraints:**
- `CONSTRAINT pk_legacy_ingestion_detail PRIMARY KEY (module_ingestion_id)`
- `CONSTRAINT fk_legacy_ingestion_detail_module_detail FOREIGN KEY (module_detail_id) REFERENCES ingestion_module_detail (detail_id) ON DELETE SET NULL`
- `CREATE INDEX idx_legacy_ingestion_detail_tenant_module_date ON legacy_data_ingestion_detail (tenant_id, module_name, push_date DESC);`
- `CREATE INDEX idx_legacy_ingestion_detail_status ON legacy_data_ingestion_detail (ingestion_status) WHERE ingestion_status IN ('NOT_STARTED', 'FAILURE');`

---

### Table 4: `ingestion_module_summary`
**Purpose:** High-performance lookup table tracking the single latest successful date and latest attempted date per tenant and module. Used by `DailyIngestionService` to determine catch-up start dates without querying heavy audit tables.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `VARCHAR(64)` | `PRIMARY KEY, NOT NULL` | Application-generated UUID for the summary record. |
| `tenant_id` | `VARCHAR(64)` | `NOT NULL` | Tenant identifier (e.g., `pg.citya` or state code `pg`). |
| `module_name` | `VARCHAR(64)` | `NOT NULL` | Short code identifying the module (e.g., `PT`, `PGR`, `CHB`). |
| `last_successful_date` | `DATE` | `NOT NULL` | Most recent calendar date for which metrics were successfully ingested. |
| `last_attempted_date` | `DATE` | `NULLABLE` | Most recent calendar date for which metrics ingestion was attempted. |
| `created_by` | `VARCHAR(256)` | `NOT NULL, DEFAULT 'SYSTEM'` | Audit creator field. |
| `created_time` | `BIGINT` | `NOT NULL` | Epoch timestamp (ms) when the summary entry was created. |
| `last_modified_by` | `VARCHAR(256)` | `NOT NULL, DEFAULT 'SYSTEM'` | Audit modifier field. |
| `last_modified_time` | `BIGINT` | `NOT NULL` | Epoch timestamp (ms) of the last update to the summary entry. |

**Indexes & Constraints:**
- `CONSTRAINT pk_ingestion_module_summary PRIMARY KEY (id)`
- `CONSTRAINT uk_ingestion_module_summary_tenant_module UNIQUE (tenant_id, module_name)`
- `CREATE INDEX idx_ingestion_module_summary_tenant_module ON ingestion_module_summary (tenant_id, module_name);`

---

### Table 5: `adapter_ingestion_error_log`
**Purpose:** Detailed error log table storing un-handled runtime exceptions, validation errors, and stack traces encountered during extraction or engine execution.

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `VARCHAR(64)` | `PRIMARY KEY, NOT NULL` | Application-generated UUID for the error log entry. |
| `tenant_id` | `VARCHAR(64)` | `NOT NULL` | Tenant identifier associated with the failed run. |
| `module_name` | `VARCHAR(64)` | `NOT NULL` | Module short code (e.g., `PT`, `PGR`). |
| `error_date` | `VARCHAR(64)` | `NOT NULL` | Target ingestion/error date string (formatted `YYYY-MM-DD` or `DD-MM-YYYY`). |
| `issue_description` | `TEXT` | `NULLABLE` | Detailed error message, root cause exception string, or stack trace. |
| `created_time` | `BIGINT` | `NOT NULL` | Epoch timestamp (ms) when the error log was inserted. |
| `created_by` | `VARCHAR(64)` | `NOT NULL` | System user or component identifier that logged the failure. |

**Indexes & Constraints:**
- `CONSTRAINT pk_adapter_ingestion_error_log PRIMARY KEY (id)`

---

## 6. End-to-End Data Flow & Test Scenarios

### 6.1 Life-Cycle of an Ingestion Record

```
[ Step 1: Trigger ]
DailyIngestionScheduler fires at 02:00 AM.
                    |
[ Step 2: Date Window Determination ]
Queries ingestion_module_summary for tenant 'pg.citya', module 'PT'.
Finds last_successful_date = '2026-08-17'. Target = yesterday ('2026-08-18').
                    |
[ Step 3: DB Logging - Attempt Started ]
Updates ingestion_module_summary.last_attempted_date = '2026-08-18'.
                    |
[ Step 4: Extraction ]
PtModuleExtractor executes SQL query against Property Tax database for date '2026-08-18'.
PTRowmapper parses ResultSet into PTDTO.
                    |
[ Step 5: Engine Processing ]
DashboardClientImpl invokes PTTransformer.transform(ptDto) -> DashboardPayload.
CommonValidator asserts non-null module, tenantId, and non-empty metrics.
                    |
[ Step 6: Auth Token Generation ]
OAuthTokenService checks cached token; if expired, fetches new Bearer token via UserFeignClient.
                    |
[ Step 7: Downstream Delivery & Retry ]
DashboardDataLoaderImpl constructs NationalDashboardIngestRequest.
POSTs payload to National Dashboard URL via DashboardFeignClient.
If HTTP 503 received -> Retries with exponential backoff + jitter (Attempt 1, 2...).
Receives HTTP 200 OK -> Sets status = 'SUCCESS'.
                    |
[ Step 8: Audit Persistence & Summary Rollback/Commit ]
Publishes DailyIngestionData record to AuditService / Kafka topic.
Persists row into ingestion_detail (ingestion_status = 'SUCCESS', request_data, response_data).
Updates ingestion_module_summary.last_successful_date = '2026-08-18'.
Updates ingestion_module_detail.last_ingested_date = '2026-08-18'.
```

---

### 6.2 QA Test Matrix & Checkpoints

The QA team should execute the following test scenarios to ensure complete operational readiness.

| Test Case ID | Scenario Name | Test Steps & Verification Steps | Expected Result |
| :--- | :--- | :--- | :--- |
| **TC-ING-01** | Standard Daily Ingestion (Happy Path) | 1. Trigger `DailyIngestionScheduler` or invoke daily ingestion API.<br>2. Inspect database `ingestion_detail`.<br>3. Inspect database `ingestion_module_summary`. | - Row inserted in `ingestion_detail` with status `SUCCESS`.<br>- `request_data` contains valid JSON with OAuth token.<br>- `last_successful_date` updated to yesterday's date. |
| **TC-ING-02** | Multi-Day Catch-Up Window | 1. Set `last_successful_date` in `ingestion_module_summary` to 3 days prior.<br>2. Run `DailyIngestionService.ingestDailyData()`. | - Engine iteratively processes dates `T-3`, `T-2`, `T-1` in exact chronological sequence.<br>- 3 separate audit rows added to `ingestion_detail`. |
| **TC-ING-03** | Catch-Up Limit Exceeded Guard | 1. Set `last_successful_date` to 15 days ago (configured limit = 7 days).<br>2. Run daily ingestion. | - Ingestion halts immediately.<br>- Log reflects limit breach message.<br>- Result status returned as `FAILURE`. No corrupt partial data pushed. |
| **TC-ING-04** | Sequential Halt on Date Failure | 1. Mock downstream endpoint to fail on date `T-2` during a 3-day catchup.<br>2. Execute catchup run. | - Date `T-3` succeeds and updates `last_successful_date` to `T-3`.<br>- Date `T-2` fails, writes `ingestion_detail` status `FAILURE`.<br>- Processing for `T-1` is skipped. `last_successful_date` remains `T-3`. |
| **TC-ING-05** | Downstream HTTP Retry Backoff | 1. Configure downstream mock API to return HTTP 500 for initial 2 attempts, then HTTP 200.<br>2. Run ingestion. | - `DashboardDataLoaderImpl` executes retries using `RetryUtil` exponential backoff + jitter.<br>- Retries succeed on 3rd attempt.<br>- Audit row recorded as `SUCCESS` with retry attempt count. |
| **TC-ING-06** | Max Retry Exhaustion Logging | 1. Mock downstream API to consistently return HTTP 500 error.<br>2. Run ingestion. | - Service retries up to `ingestMaxAttempts` (e.g., 3 attempts).<br>- Final status logged as `FAILURE`.<br>- Error stack trace and exception code stored in `ingestion_detail` and `adapter_ingestion_error_log`. |
| **TC-ING-07** | Payload Structural Validation | 1. Pass empty `metrics` list or blank `tenantId` into `CommonValidator`. | - `ValidationException` thrown immediately before network transmission.<br>- Downstream HTTP endpoint is not called. |
| **TC-ING-08** | Legacy Back-fill Migration Flow | 1. Invoke `POST /dashboard/v1/legacy/_ingest` with target date range.<br>2. Query `legacy_data_ingestion_detail`. | - Audit entries initialized with status `NOT_STARTED`.<br>- Upon execution completion, status transitions to `SUCCESS`.<br>- `is_legacy_data_ingested` updated to `TRUE` in `ingestion_module_detail`. |

---

## 7. Verification Queries for QA Database Auditing

QA Engineers can execute the following SQL queries in the PostgreSQL database to verify pipeline runs:

```sql
-- 1. Check latest ingestion summary across all active modules
SELECT tenant_id, module_name, last_successful_date, last_attempted_date, last_modified_time 
FROM ingestion_module_summary
ORDER BY last_modified_time DESC;

-- 2. Audit recent daily ingestion execution runs and JSON payloads
SELECT module_ingestion_id, tenant_id, module_name, push_date, ingestion_status, exception_code, request_data, response_data, created_time
FROM ingestion_detail
WHERE push_date >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY created_time DESC;

-- 3. Audit failed ingestion runs
SELECT module_ingestion_id, tenant_id, module_name, push_date, exception_code, response_data
FROM ingestion_detail
WHERE ingestion_status = 'FAILURE'
ORDER BY created_time DESC;

-- 4. Check error log traces for adapter exception analysis
SELECT id, tenant_id, module_name, error_date, issue_description, created_time
FROM adapter_ingestion_error_log
ORDER BY created_time DESC
LIMIT 20;

-- 5. Audit historical legacy migration run progress
SELECT module_ingestion_id, tenant_id, ulb_name, module_name, push_date, ingestion_status, user_id
FROM legacy_data_ingestion_detail
ORDER BY created_time DESC;
```

---

## 8. Summary & Technical Approval

This document details the operational mechanics, database constraints, data structures, and resilience features of the **Extractor** and **Engine** microservices. By adhering to the verification metrics and test matrices outlined herein, the QA team can validate current implementations, troubleshoot execution failures, and design test suites for new modules.

- **Extractor Project Base Path:** `business-services/dashboard-data-extractor`
- **Engine Project Base Path:** `business-services/dashboard-data-engine`
- **Database Migrations Path:** `business-services/dashboard-data-extractor/src/main/resources/db/migration/main/`
