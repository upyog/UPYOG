# External API Audit — National Dashboard Ingest test plan

How to add this library to another module: [external-api-audit README](../../libraries/external-api-audit/README.md).

Use this to confirm inbound metric ingest and bulk-row ingest write the expected rows in `ug_external_api_*`.

Tables live in the National Dashboard Ingest database. Kafka topic `external-api-audit-details` is consumed by egov-persister using `external-api-audit-persister.yml` from the `external-api-audit` library.

## 1. Automated tests (table-backed)

These tests start embedded PostgreSQL, apply the production audit DDL, and run the **same upsert SQL** as persister. Kafka is replaced by a synchronous publisher so you can assert rows without a full stack.

```bash
cd core-services/national-dashboard-ingest
mvn test -Dtest=MetricIngestAuditIntegrationTest,BatchIngestAuditIntegrationTest,ExternalApiAuditLifecycleIntegrationTest
```

First run downloads an embedded Postgres binary (needs network). Later runs are local.

| Test class | Use cases covered | Tables asserted |
|---|---|---|
| `MetricIngestAuditIntegrationTest` | inbound SUCCESS, FAILED + error row, HTTP 400 classification, two calls = two rows, payload capture off, Kafka down fail-open | `ug_external_api_message_detail`, `_raw_detail`, `_error_detail` |
| `BatchIngestAuditIntegrationTest` | each Excel row = one audit row; mixed success/fail does not abort the batch | same |
| `ExternalApiAuditLifecycleIntegrationTest` | INITIATED then SUCCESS = one row; SUCCESS sticky unless `retry_count` increases; 401-style retry overwrite; reconciliation `TIMED_OUT`; cleanup deletes all three tables | same |

Expected highlights:

- `external_api_name` = `national-dashboard-metric-ingest`
- `direction` = `INBOUND`
- RequestInfo `authToken` is stored as `********`
- Originating RequestInfo `correlationId` is **not** the table `correlation_id` (audit UUID is generated per call)

## 2. Manual / deployed environment

Prerequisites: ingest service, Kafka, egov-persister loaded with the library `external-api-audit-persister.yml`, Postgres with Flyway applied from `classpath:/db/migration/external-api-audit`. `external_api_name` identifies the integration.

### 2.1 Inbound metric SUCCESS

```bash
curl -s -X POST "$INGEST_HOST/national-dashboard-ingest/metric/_ingest" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": { "correlationId": "manual-origin-1", "authToken": "do-not-store-plain" },
    "Data": [{
      "date": "05-10-2026", "module": "PT", "ward": "W1",
      "ulb": "pb.amritsar", "region": "North", "state": "Punjab",
      "metrics": { "transactions": 12 }
    }]
  }'
```

Confirm:

```sql
SELECT correlation_id, tenant_id, external_api_name, direction, status,
       http_status_code, retry_count
FROM ug_external_api_message_detail
ORDER BY created_time DESC
LIMIT 5;

SELECT correlation_id,
       request_payload->>'originatingCorrelationId' AS originating_id,
       request_payload
FROM ug_external_api_message_raw_detail
ORDER BY created_time DESC
LIMIT 1;
```

Pass criteria:

- One new `message_detail` row, `status=SUCCESS`, `http_status_code=200`, `retry_count=0`
- `tenant_id=pb.amritsar`, `external_api_name=national-dashboard-metric-ingest`
- `correlation_id` is a new UUID, not `manual-origin-1`
- Raw payload `originatingCorrelationId=manual-origin-1`
- `authToken` / secrets appear as `********`, never the plain token
- Matching raw row; **zero** error rows for this `correlation_id`

### 2.2 Inbound metric FAILED

Send invalid data that makes ingest throw. Expect `status=FAILED`, one `ug_external_api_error_detail` row (`error_code`, `error_type`, `error_message`).

### 2.3 Two calls, same originating id

Repeat the SUCCESS curl twice with the same `RequestInfo.correlationId`. Expect **two** `message_detail` rows (new audit UUID each time).

### 2.4 Bulk Excel ingest

Upload / trigger bulk ingest with two valid rows and one invalid row.

```sql
SELECT tenant_id, status, correlation_id
FROM ug_external_api_message_detail
WHERE external_api_name = 'national-dashboard-metric-ingest'
ORDER BY created_time DESC
LIMIT 10;
```

Pass: one row per Excel row; failed row has `FAILED` + error detail; remaining rows still `SUCCESS`.

### 2.5 Payload capture off

Set `external.api.audit.capture-payload.enabled=false`, restart, call `_ingest`.

Raw JSON must contain `"payloadCaptured": false` and must not contain metric bodies.

### 2.6 Kafka down (fail-open)

Stop Kafka (or point to a dead broker). Call `_ingest` with valid data.

Pass: HTTP 200 from ingest, **no new audit rows** (or delayed rows after Kafka returns). Business call must not fail because audit publish failed.

### 2.7 Reconciliation (`TIMED_OUT`)

Jobs are enabled only on ingest. Multiple ingest pods share a ShedLock row (`external-api-audit-reconciliation` / `external-api-audit-cleanup`); only one replica runs each tick.

```
external.api.audit.reconciliation.enabled=true
external.api.audit.stale.threshold.ms=600000
```

Insert or leave an `INITIATED` row with `request_time` older than the threshold, then wait for the 06:00 Asia/Kolkata job (or call the job in a local run).

```sql
SELECT correlation_id, status, request_time
FROM ug_external_api_message_detail
WHERE status IN ('INITIATED', 'TIMED_OUT');
```

Pass: stale `INITIATED` becomes `TIMED_OUT`; recent `INITIATED` and `SUCCESS`/`FAILED` are unchanged.

### 2.8 Cleanup

```
external.api.audit.cleanup.enabled=true
external.api.audit.cleanup.retention-ms=2592000000
```

Rows with `created_time` older than retention must disappear from **all three** tables (`error_detail` and `raw_detail` first, then `message_detail`).

## 3. Quick row checklist

| Use case | `message_detail` | `raw_detail` | `error_detail` |
|---|---|---|---|
| SUCCESS ingest | 1 row `SUCCESS` | request + response | 0 |
| FAILED ingest | 1 row `FAILED` | request (+ error body) | 1 |
| Two API calls | 2 rows | 2 rows | 0 unless failed |
| Bulk 3 rows mixed | 3 rows | 3 rows | 1 for the failed row |
| Kafka down | 0 | 0 | 0 |
| Capture off | 1 metadata row | envelope only | 0 |
| Stale INITIATED | status `TIMED_OUT` | unchanged | unchanged |
