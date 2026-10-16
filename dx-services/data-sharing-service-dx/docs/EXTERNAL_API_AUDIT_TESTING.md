# External API Audit — Data Sharing Service (DX / PFMS) test plan

How to add this library to another module: [external-api-audit README](../../../core-services/libraries/external-api-audit/README.md).

Use this to confirm state inbound `_create` and PFMS outbound auth/data-push write the expected rows in `ug_external_api_*`.

DX publishes to Kafka topic `external-api-audit-details`. Persister (`external-api-audit-persister.yml` in the `external-api-audit` library) writes the tables in the **ingest** database. DX does not JDBC-insert audit rows.

## 1. Automated tests (table-backed)

These tests start embedded PostgreSQL, apply the production audit DDL, and run the **same upsert SQL** as persister. Kafka is replaced by a synchronous publisher.

```bash
cd dx-services/data-sharing-service-dx
mvn test -Dtest=PFMSInboundAuditIntegrationTest,PFMSOutboundAuditIntegrationTest,PFMSForwardingRetryAuditIntegrationTest
```

First run downloads an embedded Postgres binary (needs network). Later runs are local.

| Test class | Use cases covered |
|---|---|
| `PFMSInboundAuditIntegrationTest` | state `_create` SUCCESS, validation 400 + FAILED row, two calls = two rows, payload capture off, Kafka down fail-open |
| `PFMSOutboundAuditIntegrationTest` | PFMS auth SUCCESS with password/token masking, missing token FAILED, data push SUCCESS with bank-account masking, 502 error row, two scheduler cycles = two rows |
| `PFMSForwardingRetryAuditIntegrationTest` | 401 retry updates the **same** data-push row (`retry_count=1`, `SUCCESS`); new cycle = new row; non-401 failure stays `FAILED` with no retry |

Expected highlights:

- `tenant_id` = `PFMS` for inbound and outbound
- Inbound API name `state-pfms-transaction-create`, direction `INBOUND`
- Outbound API names `pfms-auth` and `pfms-data-push`, direction `OUTBOUND`
- Password, AccessToken, RefreshToken, bank account fields stored as `********`

## 2. Manual / deployed environment

Prerequisites: DX service, Kafka, egov-persister, ingest Flyway from the `external-api-audit` library. Query the **ingest** DB. Identity is `external_api_name`.

### 2.1 Inbound state `_create` SUCCESS

```bash
curl -s -X POST "$DX_HOST/upyog-data-dx/v1/transactions/_create" \
  -H "Content-Type: application/json" \
  -d '{
    "RequestInfo": { "correlationId": "state-origin-1", "authToken": "do-not-store-plain" },
    "data": [{
      "ulb": "pb.amritsar",
      "transactions": [{
        "ulbCodeOrPFMSAgencyCode": "ULB1",
        "voucherNumber": "VCH-1",
        "voucherDate": "05/10/2026",
        "voucherType": "Journal Voucher",
        "financialYear": "2026",
        "accountHeadCode": "AH1",
        "functionCode": "FN1",
        "schemeCode": "SC1",
        "ulbBankAccountNumber": "123456789012",
        "beneficiaryAccountNumber": "998877665544"
      }]
    }]
  }'
```

```sql
SELECT correlation_id, tenant_id, external_api_name, direction, status, http_status_code, retry_count
FROM ug_external_api_message_detail
WHERE external_api_name = 'state-pfms-transaction-create'
ORDER BY created_time DESC
LIMIT 5;
```

Pass:

- One row, `tenant_id=PFMS`, `external_api_name=state-pfms-transaction-create`, `direction=INBOUND`, `status=SUCCESS`
- `correlation_id` ≠ `state-origin-1` (originating id is inside raw JSON)
- Auth token and bank accounts masked as `********`

### 2.2 Inbound validation failure

Send a body that fails DX validation (`IllegalArgumentException`). HTTP 400 **and** `status=FAILED` with an `ug_external_api_error_detail` row.

### 2.3 Two inbound calls

Repeat SUCCESS twice with the same originating correlation id. Expect **two** audit rows.

### 2.4 Outbound PFMS auth

Trigger the scheduler (or wait for the daily job):

```bash
curl -s -X POST "$DX_HOST/upyog-data-dx/v1/transactions/scheduler/_trigger"
```

```sql
SELECT correlation_id, status, http_status_code, retry_count
FROM ug_external_api_message_detail
WHERE external_api_name = 'pfms-auth'
ORDER BY created_time DESC
LIMIT 5;

SELECT request_payload, response_payload
FROM ug_external_api_message_raw_detail
WHERE correlation_id = '<pfms-auth-correlation-id>';
```

Pass: `OUTBOUND` `SUCCESS`; `Password` / `AccessToken` / `RefreshToken` are `********`. Never store the live PFMS password or bearer token.

### 2.5 Outbound PFMS data push SUCCESS

After a successful forward of one transaction:

```sql
SELECT correlation_id, status, retry_count, http_status_code
FROM ug_external_api_message_detail
WHERE external_api_name = 'pfms-data-push'
ORDER BY created_time DESC
LIMIT 5;
```

Pass: one row per transaction attempt-cycle, `SUCCESS`, `retry_count=0` on first try. Raw JSON contains `businessReferenceId` (transaction id) and `originatingCorrelationId`. Bank fields (`ULBBankAccountNumber`, `BeneficiaryAccountNumber`, `FromAccount`, `ToAccount`) are `********`.

### 2.6 401 retry updates the same row

Force PFMS data API to return 401 once, then succeed after token refresh (test stub or expired token).

Pass for `pfms-data-push`:

- **Still one row** for that transaction cycle
- `retry_count=1`
- final `status=SUCCESS`
- one `error_detail` row from the first 401 (`HTTP_CLIENT_ERROR` / Client)

Auth will have **two** rows (initial token + refresh), different `correlation_id`s.

### 2.7 Next-day / next scheduler cycle = new row

Trigger `_trigger` again for the same business transaction (or a new day). Expect a **new** `pfms-data-push` `correlation_id`. Do not reuse yesterday's audit id.

### 2.8 Non-401 failure

PFMS 5xx on data push: one `FAILED` row, `retry_count=0`, error detail present. Scheduler records the transaction as failed; it is **not** an in-cycle 401 retry.

Use `_retryTrigger` later: that is a **new** logical request and must create a **new** audit row.

### 2.9 Payload capture off / Kafka fail-open

- `external.api.audit.capture-payload.enabled=false`: envelope has `payloadCaptured: false`, no PFMS bodies.
- Kafka down: `_create` still returns 200; no audit rows until Kafka/persister recover. Business must not fail because audit publish failed.

### 2.10 Jobs stay disabled on DX

```
external.api.audit.reconciliation.enabled=false
external.api.audit.cleanup.enabled=false
```

Timeout and cleanup run on national-dashboard-ingest only (that is where the tables live).

## 3. Quick row checklist

| Use case | `pfms-auth` | `pfms-data-push` | `state-pfms-transaction-create` |
|---|---|---|---|
| Inbound SUCCESS | — | — | 1 `SUCCESS` |
| Inbound 400 | — | — | 1 `FAILED` + error |
| Auth SUCCESS | 1 `SUCCESS`, secrets masked | — | — |
| Push SUCCESS | 1 (per cycle) | 1 `SUCCESS` `retry_count=0` | — |
| 401 then success | 2 rows | **1** row `retry_count=1` `SUCCESS` + 1 error | — |
| Next scheduler cycle | +1 | **+1 new** `correlation_id` | — |
| Push 502 | 1 | 1 `FAILED` `retry_count=0` | — |
