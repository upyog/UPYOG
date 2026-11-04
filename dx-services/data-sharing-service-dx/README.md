# UPYOG Data Exchange Service

## Overview

This service acts as a **data bridge between State ULB systems and the PFMS (Public Financial Management System)** department of the Government of India.

States push their financial transaction data (vouchers) to this service via a REST API. The service validates, persists the data, and then asynchronously forwards each transaction to the PFMS external API using scheduled jobs.

---

## Why This Service Was Built

State ULBs maintain their own financial systems and need to report voucher-level transaction data to PFMS for central government visibility and fund tracking. PFMS exposes its own API that requires authentication via a Bearer token and accepts data in a specific form-urlencoded format.

This service was built to:
- Accept bulk transaction data from states in a standardised JSON format
- Validate for duplicates before accepting (both within the request and against the DB)
- Decouple ingestion from forwarding using Kafka + a persister
- Handle token expiry transparently during PFMS forwarding
- Retry failed transactions automatically via a separate scheduler

---

## Architecture Flow

```
State System
    │
    │  POST /data-sharing-service-dx/v1/transactions/_create
    ▼
PFMSController
    │
    ▼
PFMSServiceImpl
    ├── validateDuplicates()  ──► PostgreSQL (duplicate check)
    ├── PFMSDataEnricher / PFMSTransactionTransformer
    │       (API contract → PfmsTransactionEntity + additionalDetails JSONB)
    └── PFMSProducer.push()  ──► Kafka Topic: state-to-upyog-data-exchange
                                        │
                                        ▼
                                 egov-persister
                                        │
                                        ▼
                                  PostgreSQL
                              (ug_pfms_data + ug_pfms_transactions)

                                        │
                          ┌─────────────┴──────────────┐
                          │  Cron: 4:00 PM daily        │  Cron: 6:00 PM daily
                          ▼                             ▼
              PFMSForwardingScheduler        PFMSRetryScheduler
                          │                             │
                          └──────────┬──────────────────┘
                                     ▼
                          PFMSForwardingServiceImpl
                                     │
                              fetchAccessToken()
                                     │
                                     ▼
                          PFMS Auth API (Bearer token)
                                     │
                              per-transaction loop
                                     │
                                     ▼
                          PFMS Data API (form-urlencoded)
                                     │
                              update status in DB
```

---

## Data models (API vs persistence)

| Layer | Type | Purpose |
|---|---|---|
| Inbound API | `PFMSTransaction`, `PFMSData`, `PFMSCreateTransactionRequest` | Public contract for state systems. No `additionalDetails` field. |
| Kafka / DB | `PfmsTransactionEntity`, `PfmsTransactionAdditionalDetails`, `PFMSCreateTransactionKafkaMessage` | Internal model: flat columns + JSONB for extensible attributes. |

Optional and variable voucher fields from the API are copied into `additionalDetails` during enrichment before Kafka publish. egov-persister writes them to `ug_pfms_transactions.additionaldetails`.

---

## Phase 1 — State Pushes Data (Ingestion)

### API

```
POST http://localhost:8281/data-sharing-service-dx/v1/transactions/_create
Content-Type: application/json
```

### Sample cURL

```bash
curl --location 'http://localhost:8281/data-sharing-service-dx/v1/transactions/_create' \
--header 'Content-Type: application/json' \
--data '{
    "RequestInfo": {
        "apiId": "Rainmaker",
        "authToken": "<auth-token>",
        "userInfo": {
            "id": 42710,
            "uuid": "<user-uuid>",
            "userName": "PFMS_ADMIN",
            "name": "Pfms Dept Admin",
            "mobileNumber": "<mobile>",
            "type": "EMPLOYEE",
            "roles": [
                { "name": "PFMS Data Ingest Admin", "code": "PFMS_DATA_INGEST_ADMIN", "tenantId": "pg.citya" }
            ],
            "active": true,
            "tenantId": "pg.citya"
        },
        "msgId": "1790847295512|en_IN",
        "plainAccessRequest": {}
    },
    "data": [
        {
            "state": "JK",
            "ward": "WARD1",
            "module": "FINANCE",
            "date": "21-09-2026",
            "ulb": "jk.srinagar",
            "targetDestination": "PFMS",
            "transactions": [
                {
                    "ulbCodeOrPFMSAgencyCode": "12345",
                    "voucherNumber": "6/CSL/00000005/09/2025-26",
                    "voucherDate": "21/08/2026",
                    "voucherType": "Journal Voucher",
                    "financialYear": "2026",
                    "accountHeadCode": ["2305202"],
                    "functionCode": ["202404"],
                    "schemeCode": ["20"],
                    "debitAmount": 10000,
                    "creditAmount": 10000,
                    "narrationOrDescription": "Payment",
                    "voucherStatus": "Created",
                    "ulbBankAccountNumber": "<account-number>",
                    "ulbIFSCCode": "SBIN0001262",
                    "instrumentReference": "RTGS",
                    "modeOfTransaction": "cheque",
                    "beneficiaryOrPayeeName": "Test",
                    "beneficiaryAccountNumber": "<beneficiary-account>",
                    "beneficiaryIFSCCode": "SBIN0001263",
                    "beneficiaryType": "individual",
                    "challanNumber": "2022",
                    "fromAccount": "<from-account>",
                    "toAccount": "<to-account>",
                    "contraNature": "ok",
                    "transferInstructionReference": "tdhtd",
                    "referenceVoucherNumber": "1/CSL/00000005/09/2024-25",
                    "adjustmentType": "cash",
                    "relatedAssetOrLiabilityCode": "ASSET001",
                    "debtorOrCreditorReference": "REF001",
                    "basisOfAdjustment": "ADJUSTMENT",
                    "periodCovered": "20112"
                }
            ]
        }
    ]
}'
```

### What Happens Internally

1. **Duplicate validation** — rejects duplicate `agencyCode + voucherNumber`:
   - Within the same request (`HashSet`)
   - Against `ug_pfms_transactions` via JDBC
   - Returns HTTP **400** with `{ "message": "..." }` (`IllegalArgumentException`)
2. **Transform** — `PFMSTransactionTransformer` maps API models to `PfmsTransactionEntity`:
   - Assigns `id` and `correlationId`
   - Sets `status = INITIATED`, `ingestionDate = null`
   - Moves non-flat fields into `additionalDetails` for JSONB
3. **Kafka publish** — `PFMSCreateTransactionKafkaMessage` sent to `state-to-upyog-data-exchange`
   - On failure: HTTP **500**, `PfmsKafkaPublishException`, error logged with cause
4. **egov-persister** (`upyog-data-dx.yml`) inserts:
   - `ug_pfms_data` — one row per batch
   - `ug_pfms_transactions` — flat columns + `additionaldetails` JSONB

### Error responses (ingest)

| Condition | HTTP | Body |
|---|---|---|
| Bean validation (`@NotBlank`, etc.) | 400 | `{ "message": ["field: error"] }` |
| Duplicate voucher | 400 | `{ "message": "Duplicate vouchers..." }` |
| Invalid JSON / date format | 400 | `{ "message": "Invalid request body: ..." }` |
| Kafka publish failure | 500 | `{ "message": "Failed to publish PFMS data to Kafka" }` |
| Database error on duplicate check | 500 | `{ "message": "Database error while processing PFMS request" }` |

Structured logs use prefixes such as `PFMS ingest`, `PFMS Kafka`, and `PFMS duplicate validation` at INFO/WARN/ERROR.

---

## Phase 2 — Forwarding to PFMS (Scheduler)

### Schedulers

| Scheduler | Cron | Purpose |
|---|---|---|
| `PFMSForwardingScheduler` | `0 0 16 * * *` (4:00 PM daily, see `application.properties`) | Picks up `INITIATED` transactions and forwards to PFMS |
| `PFMSRetryScheduler` | `0 0 18 * * *` (6:00 PM daily) | Retries `FAILED` transactions |

Both schedulers call the same `processBatch()` logic in `PFMSForwardingServiceImpl`.

### Step-by-Step

1. Fetch transaction IDs from DB:
   - Forward scheduler: `WHERE status = 'INITIATED' ORDER BY created_time ASC LIMIT 50`
   - Retry scheduler: `WHERE status = 'FAILED' ORDER BY last_modified_time ASC LIMIT 50`
2. If no IDs found → skip cycle
3. **Fetch Bearer token** from PFMS Auth API:
   ```
   POST https://training.pfms.gov.in/UMGAPI/API/Auth/ValidLogin
   Body: { "username": "UMANG_API", "password": "<password>" }
   Response: { "access_token": "<bearer-token>" }
   ```
   If auth fails → abort entire cycle, no transactions processed
4. For each transaction ID:
   - Load full transaction from DB
   - Set `status = PROCESSING` in DB (marks it as in-flight)
   - Push to PFMS Data API:
     ```
     POST https://training.pfms.gov.in/UMGAPI/API/ULBAPI/GetTransactionalDataFetchedFromULBSystem
     Authorization: Bearer <token>
     Content-Type: application/x-www-form-urlencoded
     Body: <transaction fields as form data>
     ```
   - If PFMS returns **401 Unauthorized** → refresh token silently and retry once
   - On success → `status = <PFMS response string>`, `ingestion_date = now()`
   - On failure → `status = FAILED`, `ingestion_date = now()`
5. After all transactions processed → insert one row into `ug_pfms_scheduler_log`

### PFMS Push cURL (internal reference)

```bash
curl --location 'https://training.pfms.gov.in/UMGAPI/API/ULBAPI/GetTransactionalDataFetchedFromULBSystem' \
--header 'Authorization: Bearer <access-token>' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'agencyCode=<ulbCodeOrPFMSAgencyCode>' \
--data-urlencode 'voucherNo=<voucherNumber>' \
--data-urlencode 'voucherDate=<voucherDate>' \
--data-urlencode 'voucherType=<voucherType>' \
--data-urlencode 'financialYear=<financialYear>' \
--data-urlencode 'accountHeadCode=<accountHeadCode>' \
--data-urlencode 'functionCode=<functionCode>' \
--data-urlencode 'schemeCode=<schemeCode>' \
--data-urlencode 'debitAmount=<debitAmount>' \
--data-urlencode 'creditAmount=<creditAmount>' \
--data-urlencode 'clientIp=<clientIp>'
```

---

## Database Tables

### `ug_pfms_data`
Stores the top-level metadata for each batch pushed by a state.

| Column | Type | Description |
|---|---|---|
| `id` | VARCHAR(64) PK | Correlation ID assigned by this service |
| `state` | VARCHAR | State code (e.g. JK) |
| `ward` | VARCHAR | Ward identifier |
| `module` | VARCHAR | Module name (e.g. FINANCE) |
| `data_date` | DATE | Date of the data batch |
| `ulb` | VARCHAR | ULB tenant ID |
| `target_destination` | VARCHAR | Target system (e.g. PFMS) |
| `created_time` | TIMESTAMP | Auto-set on insert |
| `last_modified_time` | TIMESTAMP | Updated on any change |

### `ug_pfms_transactions`
Stores individual voucher transactions. FK to `ug_pfms_data` via `correlation_id`.

| Column | Type | Description |
|---|---|---|
| `id` | VARCHAR(64) PK | Transaction id assigned at ingest |
| `correlation_id` | VARCHAR(64) FK | Links to `ug_pfms_data.id` |
| `agencyCode` | VARCHAR | ULB / PFMS agency code (`ulbCodeOrPFMSAgencyCode`) |
| `voucher_number` | VARCHAR | Voucher number from state |
| `voucher_date` | DATE | Voucher date |
| `voucherType` | VARCHAR | Voucher type |
| `financial_year` | VARCHAR | Financial year |
| `additionaldetails` | JSONB | Optional / extensible fields (`PfmsTransactionAdditionalDetails`) |
| `status` | VARCHAR | `INITIATED` → `PROCESSING` → PFMS response / `FAILED` |
| `ingestion_date` | TIMESTAMP | Set when PFMS responds |
| `created_time` / `last_modified_time` | TIMESTAMP | Audit timestamps |

> **Unique constraint:** `uk_pfms_ulb_voucher_number` on `(agencyCode, voucher_number)`.

### `ug_pfms_scheduler_log`
One row inserted per scheduler cycle run.

| Column | Type | Description |
|---|---|---|
| `id` | UUID PK | Auto-generated |
| `scheduler_type` | VARCHAR | `DAILY` or `RETRY` |
| `started_at` | TIMESTAMP | When the cycle started |
| `ended_at` | TIMESTAMP | When the cycle finished |
| `duration_ms` | BIGINT | Total cycle duration in milliseconds |
| `status` | VARCHAR | `COMPLETED` / `PARTIAL` / `ALL_FAILED` |
| `total_picked` | INT | Number of transactions picked in this cycle |
| `success_count` | INT | Successfully forwarded to PFMS |
| `failed_count` | INT | Failed to forward |
| `created_by` | VARCHAR | `SCHEDULER` or `MANUAL` |
| `last_modified_time` | TIMESTAMP | Set to `startedAt` on insert |

---

## Transaction Status Lifecycle

```
[Persister inserts]
        │
        ▼
    INITIATED
        │
        │  Scheduler picks up
        ▼
   PROCESSING  ◄── transient; if service crashes here, transaction gets stuck
        │
        ├── PFMS responds successfully ──► <PFMS response string>  (e.g. "SUCCESS")
        │
        └── Exception / PFMS error ──────► FAILED  ◄── picked up by retry scheduler
```

## Configuration Reference

| Property | Default | Description |
|---|---|---|
| `server.port` | `8281` | Service port |
| `upyog.data.kafka.topic` | `state-to-upyog-data-exchange` | Kafka topic for persister |
| `pfms.auth.url` | PFMS auth endpoint | Bearer token fetch URL |
| `pfms.data.url` | PFMS data endpoint | Transaction push URL |
| `pfms.auth.username` | `UMANG_API` | PFMS auth username |
| `pfms.auth.password` | — | PFMS auth password (hashed) |
| `pfms.client.ip` | — | Client IP sent to PFMS |
| `pfms.scheduler.cron` | `0 0 16 * * *` | Forward scheduler cron (4:00 PM) |
| `pfms.retry.scheduler.cron` | `0 0 18 * * *` | Retry scheduler cron (6:00 PM) |
| `pfms.scheduler.batch.size` | `50` | Max transactions per cycle |

---

## Author

**Shivank Shukla**

