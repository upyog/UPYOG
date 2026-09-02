# QA Test Suite & Test Cases Reference Document
## Extractor & Engine Microservices Ingestion Pipeline

**Document Version:** 1.1  
**Author:** Technical Engineering & QA Team  
**Date:** August 20, 2026  
**Target Audience:** QA Lead, Software Engineers, Systems Integration Testers  

---

## 1. Test Suite Overview

This document contains the complete set of formal **QA Test Cases**, end-to-end integration workflows, test matrices, boundary conditions, edge cases, and verification queries for testing the **Dashboard Data Extractor** (`dashboard-data-extractor`) and **Dashboard Data Engine** (`dashboard-data-engine`) services.

### Test Coverage Areas
- **Suite 1:** Daily Ingestion Scheduling & Catch-up Window Boundary Testing
- **Suite 2:** Historical / Legacy Bulk Ingestion Back-fill
- **Suite 3:** Data Extraction, JDBC Query Mapping & Null Handling
- **Suite 4:** Engine Validation, Invariants & Payload Transformations
- **Suite 5:** Downstream Delivery, Feign Connection Timeout & Exponential Backoff Retries
- **Suite 6:** PostgreSQL Database Audit & State Persistence Verification
- **Suite 7:** End-to-End Real-World Multi-Tenant Application Ingestion Flow

---

## 2. End-to-End Hands-On Integration Testing & Validation Procedure

This section documents the explicit, real-world verification methodology used to validate multi-tenant data extraction across operational states, database audit tables, gateway logs, Elasticsearch indices, and the National Dashboard UI.

```
+-----------------------------------------------------------------------------------------+
| STEP 1: Multi-Tenant Data Setup                                                         |
| Create PGR & PT applications in 'citya' and 'cityb' across varied workflow states       |
| (Completed, In-Progress/Workflow Pending).                                              |
+-----------------------------------------------------------------------------------------+
                                             |
                                             v
+-----------------------------------------------------------------------------------------+
| STEP 2: Pre-Execution DB Query Validation                                               |
| Run raw extractor SQL queries against core domain DBs to verify target date counts.     |
+-----------------------------------------------------------------------------------------+
                                             |
                                             v
+-----------------------------------------------------------------------------------------+
| STEP 3: Manual Pipeline Execution                                                       |
| Trigger ingestion code/REST endpoint manually for the specific target date.              |
+-----------------------------------------------------------------------------------------+
                                             |
                                             v
+-----------------------------------------------------------------------------------------+
| STEP 4: Multi-Layer Database & Gateway Log Auditing                                     |
| Verify rows in:                                                                         |
|  - ingestion_detail (Status = SUCCESS, JSON payloads)                                   |
|  - ingestion_module_summary (last_successful_date & last_attempted_date updated)        |
|  - ug_external_api_message_detail (Outbound API audit message detail)                   |
|  - ug_external_api_message_raw_detail (Raw HTTP payload & response body)                |
+-----------------------------------------------------------------------------------------+
                                             |
                                             v
+-----------------------------------------------------------------------------------------+
| STEP 5: Downstream Elasticsearch & National Dashboard Verification                      |
|  - Query Elasticsearch index to confirm metric document indexing.                       |
|  - Log into National Dashboard UI to visually confirm matching metrics.                 |
+-----------------------------------------------------------------------------------------+
```

### Detailed Operational Step-by-Step Procedure

#### Step 1: Multi-Tenant Test Data Creation Across Operational States
1. Navigate to the application portal for **Property Tax (PT)** and **Public Grievance Redressal (PGR)** modules.
2. Generate transactional records for multiple ULBs (e.g., `citya` and `cityb`):
   - **PGR Complaints:** Create grievances with status `PENDING_FOR_ASSIGNMENT`, `IN_PROGRESS`, and `RESOLVED`.
   - **PT Applications:** Create property tax assessments with status `WORKFLOW_IN_PROGRESS`, `APPROVED`, and `PAID` (completed collection).
3. Note the target creation date `T` (or backdated target date).

#### Step 2: Pre-Execution Database Extractor Query Verification
Before executing the application code, run the extractor SQL queries directly against the core domain database using PostgreSQL client (pgAdmin / psql):
```sql
-- Verify PGR application counts for target date across tenants and workflow states
SELECT tenantid, status, COUNT(*) 
FROM eg_pgr_service 
WHERE date_trunc('day', to_timestamp(createdtime/1000)) = '2026-08-20'
GROUP BY tenantid, status;

-- Verify PT assessment counts and collections for target date
SELECT tenantid, status, COUNT(*) 
FROM eg_pt_property 
WHERE date_trunc('day', to_timestamp(createdtime/1000)) = '2026-08-20'
GROUP BY tenantid, status;
```
*Goal:* Confirm that the extraction query accurately aggregates application counts according to business logic rules before pipeline execution.

#### Step 3: Manual API Execution & Pipeline Trigger
1. Launch `dashboard-data-extractor` and `dashboard-data-engine` services.
2. Invoke the manual execution endpoint for target date `2026-08-20`:
   ```bash
   curl -X POST "http://localhost:8080/dashboard/v1/daily/_ingest?date=2026-08-20" \
        -H "Content-Type: application/json"
   ```

#### Step 4: Multi-Layer Database & Gateway Log Auditing
Verify data persistence across all audit tables:
```sql
-- 4a. Verify ingestion execution status and JSON request/response
SELECT module_ingestion_id, tenant_id, module_name, push_date, ingestion_status, exception_code
FROM ingestion_detail
WHERE push_date = '2026-08-20'
ORDER BY created_time DESC;

-- 4b. Verify tracker summary updates
SELECT tenant_id, module_name, last_successful_date, last_attempted_date
FROM ingestion_module_summary
WHERE last_successful_date = '2026-08-20';

-- 4c. Verify external gateway API audit tables
SELECT message_id, tenant_id, module_name, status, created_time
FROM ug_external_api_message_detail
ORDER BY created_time DESC LIMIT 5;

SELECT message_id, request_payload, response_payload
FROM ug_external_api_message_raw_detail
ORDER BY created_time DESC LIMIT 5;
```

#### Step 5: Downstream Elasticsearch & National Dashboard Verification
1. **Elasticsearch Index Inspection:**
   Query the Elasticsearch cluster to confirm document indexing for target metrics:
   ```bash
   GET /national-dashboard-metrics/_search
   {
     "query": {
       "term": { "date.keyword": "2026-08-20" }
     }
   }
   ```
2. **National Dashboard UI Validation:**
   Log into the National Dashboard Web Application, select `citya` / `cityb` filter, select date `2026-08-20`, and verify that displayed Property Tax and PGR metrics match the test applications created in Step 1.

---

## 3. Detailed Test Cases Matrix

### Suite 1: Daily Ingestion Scheduling & Catch-Up Logic

#### TC-EXT-001: Standard Daily Ingestion (Happy Path)
- **Priority:** High (P0)
- **Module:** Daily Ingestion Scheduler / `DailyIngestionService`
- **Pre-conditions:** Active configuration in `ingestion_module_detail`. `last_successful_date` in `ingestion_module_summary` set to `T-2`. Current date is `T`. Target date is `T-1` (yesterday).
- **Test Steps:**
  1. Trigger daily ingestion via scheduler or invoke `GET /dashboard/v1/daily/_ingest`.
  2. Monitor execution log.
  3. Query `ingestion_detail` and `ingestion_module_summary`.
- **Expected Results:**
  - Scheduler extracts data for `T-1` (yesterday).
  - Row created in `ingestion_detail` with `ingestion_status = 'SUCCESS'`.
  - `last_successful_date` in `ingestion_module_summary` updated to `T-1`.
- **Verification Query:**
  ```sql
  SELECT tenant_id, module_name, last_successful_date, last_attempted_date 
  FROM ingestion_module_summary 
  WHERE tenant_id = 'pg.citya' AND module_name = 'PT';
  ```

---

#### TC-EXT-002: Multi-Day Sequential Catch-Up Run
- **Priority:** High (P1)
- **Module:** `DailyIngestionService`
- **Pre-conditions:** Service was offline for 4 days. `last_successful_date` is set to `2026-08-15`. Current date is `2026-08-20` (Target range: `2026-08-16` to `2026-08-19`). `dailyCatchUpLimitDays = 7`.
- **Test Steps:**
  1. Trigger `DailyIngestionService.ingestDailyData()`.
  2. Observe execution log for date sequence.
  3. Verify database records for each date.
- **Expected Results:**
  - Service detects 4-day gap and sequentially ingests `2026-08-16`, `2026-08-17`, `2026-08-18`, `2026-08-19`.
  - 4 separate audit entries inserted in `ingestion_detail`.
  - `last_successful_date` sequentially advances and finishes at `2026-08-19`.

---

#### TC-EXT-003: Catch-Up Gap Limit Breach Guard
- **Priority:** High (P1)
- **Module:** `DailyIngestionService`
- **Pre-conditions:** Service was offline for 15 days. `last_successful_date` is `2026-08-01`. Current date is `2026-08-20`. `dailyCatchUpLimitDays = 7`.
- **Test Steps:**
  1. Trigger `DailyIngestionService.ingestDailyData()`.
  2. Check application logs and returned `IngestionResult`.
- **Expected Results:**
  - Catch-up gap (18 days) exceeds maximum limit (7 days).
  - Daily ingestion halts immediately without pushing metrics.
  - Error message returned: `"Catch-up gap of 18 days exceeds max limit of 7 days. Use legacy migration endpoint."`
  - Status returned as `FAILURE`. No partial data written to `ingestion_detail`.

---

#### TC-EXT-004: Sequential Halt on Intermittent Date Failure
- **Priority:** High (P1)
- **Module:** `DailyIngestionService`
- **Pre-conditions:** Catchup range is `2026-08-16` to `2026-08-18`. Mock downstream API to succeed on `2026-08-16`, fail on `2026-08-17`, and succeed on `2026-08-18`.
- **Test Steps:**
  1. Execute catchup ingestion run.
  2. Verify records in `ingestion_detail` and `ingestion_module_summary`.
- **Expected Results:**
  - `2026-08-16` succeeds; `last_successful_date` updated to `2026-08-16`.
  - `2026-08-17` fails; `ingestion_detail` records `FAILURE`.
  - Processing for `2026-08-18` is **halted/skipped** to prevent out-of-order date gaps.
  - `last_successful_date` remains `2026-08-16`.

---

#### TC-EXT-005: Zero-Metrics Downstream Skip (`SUCCESS_ZERO_METRICS`)
- **Priority:** Medium (P1)
- **Module:** `DailyIngestionService`
- **Pre-conditions:** All metrics extracted for target date `2026-08-19` for module `PT` evaluate to 0 (e.g. `assessments = 0`, `collection = 0`).
- **Test Steps:**
  1. Trigger daily ingestion run for target date `2026-08-19`.
  2. Inspect execution logs and `ingestion_detail` table.
- **Expected Results:**
  - Log records `"All metrics for module PT on date 2026-08-19 are zero. Skipping downstream API push."`
  - Downstream HTTP push is skipped.
  - Audit log inserted with `ingestion_status = 'SUCCESS_ZERO_METRICS'`.
  - `last_successful_date` in `ingestion_module_summary` updates to `2026-08-19`.

---

#### TC-EXT-006: Duplicate Date Record Handling (`SUCCESS_DUPLICATE`)
- **Priority:** High (P1)
- **Module:** `DailyIngestionService`
- **Pre-conditions:** Re-running daily ingestion for date `2026-08-19` which was already ingested. National API returns `EG_DS_RECORD_ALREADY_INGESTED_ERR`.
- **Test Steps:**
  1. Trigger daily ingestion run for date `2026-08-19`.
  2. Observe HTTP response status handling in `DailyIngestionService`.
- **Expected Results:**
  - System identifies error code `"EG_DS_RECORD_ALREADY_INGESTED_ERR"`.
  - Status converted to `SUCCESS_DUPLICATE`.
  - `last_successful_date` in `ingestion_module_summary` updates to `2026-08-19`.

---

#### TC-EXT-007: Downstream Timeout Flag Toggle (`dashboard-data.timeout.enabled`)
- **Priority:** Medium (P2)
- **Module:** `DashboardFeignClient` / Feign Client Config
- **Pre-conditions:** Set `dashboard-data.timeout.enabled=false` in `application.properties`. Mock downstream API with 20s latency.
- **Test Steps:**
  1. Trigger HTTP ingestion request.
  2. Observe Feign client execution behavior.
- **Expected Results:**
  - Timeout is NOT enforced when `dashboard-data.timeout.enabled=false`.
  - Request waits for response without throwing early timeout exception.

---

### Suite 2: Legacy Historical Bulk Migration Testing

#### TC-LEG-001: Historical Legacy Ingestion (Range Ingestion)
- **Priority:** High (P1)
- **Module:** `LegacyIngestionService` / `LegacyIngestionController`
- **Pre-conditions:** REST Endpoint `POST /dashboard/v1/legacy/_ingest`.
- **Test Steps:**
  1. Send HTTP request:
     ```json
     {
       "tenantId": "pg.citya",
       "moduleName": "PT",
       "startDate": "2026-01-01",
       "endDate": "2026-01-31"
     }
     ```
  2. Check response status and database audit tables.
- **Expected Results:**
  - Initial rows created in `legacy_data_ingestion_detail` with `ingestion_status = 'NOT_STARTED'`.
  - Service ingests data for each date in range.
  - `ingestion_status` updated to `'SUCCESS'`.
  - `is_legacy_data_ingested` set to `TRUE` in `ingestion_module_detail`.

---

### Suite 3: Data Engine Validation & Downstream Delivery

#### TC-ENG-001: Payload Field Validation (`CommonValidator`)
- **Priority:** Medium (P2)
- **Module:** `CommonValidator` / `DashboardClientImpl`
- **Pre-conditions:** Extracted data contains null `tenantId` or empty `metrics` list.
- **Test Steps:**
  1. Pass invalid `DashboardPayload` to `DashboardClientImpl.execute()`.
- **Expected Results:**
  - `ValidationException` thrown immediately.
  - No downstream HTTP Feign call is attempted.
  - Error logged in `adapter_ingestion_error_log`.

---

#### TC-ENG-002: Downstream HTTP Exponential Backoff & Jitter Retry
- **Priority:** High (P0)
- **Module:** `DashboardDataLoaderImpl` / `RetryUtil`
- **Pre-conditions:** Downstream National Dashboard API configured to return HTTP 503 Service Unavailable for first 2 attempts, then HTTP 200 OK.
- **Test Steps:**
  1. Initiate metric load via `DashboardDataLoaderImpl.load()`.
  2. Capture attempt logs and execution timestamps.
- **Expected Results:**
  - Attempt 1 fails (HTTP 503) -> Sleeps for base backoff with randomized jitter (~1000ms).
  - Attempt 2 fails (HTTP 503) -> Sleeps for exponentially increased backoff (~2000ms).
  - Attempt 3 succeeds (HTTP 200 OK).
  - Final status recorded as `SUCCESS` with retry history array.

---

#### TC-ENG-003: Retry Exhaustion & Fallback Logging
- **Priority:** High (P1)
- **Module:** `DashboardDataLoaderImpl`
- **Pre-conditions:** Downstream API returns persistent HTTP 500 Internal Server Error. `ingestMaxAttempts = 3`.
- **Test Steps:**
  1. Execute metric load.
- **Expected Results:**
  - System retries 3 times and exhausts retry budget.
  - Final result status returned as `FAILURE`.
  - Exception string and status logged into `ingestion_detail` (`ingestion_status = 'FAILURE'`) and `adapter_ingestion_error_log`.

---

### Suite 4: Database Audit & SQL Assertions

#### TC-DB-001: Database Ingestion Detail Audit Integrity
- **Priority:** High (P0)
- **Module:** PostgreSQL Ingestion Tables
- **Test Query:**
  ```sql
  SELECT module_ingestion_id, tenant_id, module_name, push_date, 
         ingestion_status, exception_code, request_data, response_data 
  FROM ingestion_detail 
  ORDER BY created_time DESC 
  LIMIT 5;
  ```
- **Assertions:**
  - `module_ingestion_id` is a non-null valid UUID.
  - `request_data` is valid non-empty JSONB.
  - `push_date` matches target execution date.

---

#### TC-DB-002: Summary Record Timestamp Consistency
- **Priority:** Medium (P2)
- **Module:** PostgreSQL Ingestion Tables
- **Test Query:**
  ```sql
  SELECT tenant_id, module_name, last_successful_date, last_attempted_date 
  FROM ingestion_module_summary 
  WHERE last_attempted_date < last_successful_date;
  ```
- **Expected Result:**
  - 0 rows returned (`last_attempted_date` must always be greater than or equal to `last_successful_date`).

---

## 4. Summary Test Results Sign-off Template

| Test Suite | Total Cases | Passed | Failed | Blocked | Pass Rate (%) |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **Suite 1: Daily Ingestion & Catch-Up** | 4 | -- | -- | -- | -- |
| **Suite 2: Legacy Migration** | 1 | -- | -- | -- | -- |
| **Suite 3: Engine Validation & Retry** | 3 | -- | -- | -- | -- |
| **Suite 4: Database Audit Verification** | 2 | -- | -- | -- | -- |
| **Suite 5: Real-World E2E Validation Flow** | 1 | -- | -- | -- | -- |
| **TOTAL** | **11** | **--** | **--** | **--** | **--** |
