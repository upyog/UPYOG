# External API Audit Library

Reusable UPYOG library for auditing **explicitly identified** external / third-party integrations.

Use this when a UPYOG service talks to (or is called by) a system outside the UPYOG mesh — for example National Dashboard inbound ingest, or PFMS inbound/outbound in Data Sharing. Do **not** wrap internal UPYOG APIs (MDMS, persister, user, workflow, and so on).

Persistence path is unchanged from the original National Dashboard Ingest design. Business services never JDBC-insert audit rows:

```
Service
  → ExternalApiAuditLogger.execute(...)   (explicit wrap, not an HTTP interceptor)
  → Kafka topic external-api-audit-details  (async, fail-open)
  → egov-persister  (external-api-audit-persister.yml, ON CONFLICT upsert)
  → ug_external_api_message_detail
     ug_external_api_message_raw_detail
     ug_external_api_error_detail
```

Each logical request publishes **two Kafka events** (`INITIATED`, then `SUCCESS` or `FAILED`) that upsert **one** database row on `correlation_id`.

---

## When to wrap a call

| Wrap | Do not wrap |
|---|---|
| Inbound API that an **external** system calls (state → DX `_create`, national dashboard `_ingest`) | Internal UPYOG REST between services |
| Outbound HTTP/Feign to a **third party** (PFMS auth, PFMS data push) | Kafka produce/consume of business topics |
| Retry of the **same** third-party request (reuse `correlationId`) | Global `RestTemplate` / Feign interceptors for audit |

HTTP tracing for Feign (correlation-id header + request/response logs) lives in **tracer**. That is logging only. Kafka audit is always an **explicit** `ExternalApiAuditLogger` wrap.

---

## How to integrate a new external API service module

Follow these steps in the consuming Spring Boot service (the “external API service module”).

### 1. Maven dependency

Install / publish `external-api-audit` `2.9.0-SNAPSHOT`, then add:

```xml
<dependency>
    <groupId>org.egov.services</groupId>
    <artifactId>external-api-audit</artifactId>
    <version>2.9.0-SNAPSHOT</version>
</dependency>
```

The library auto-configures when `KafkaTemplate` is on the classpath (`ExternalApiAuditAutoConfiguration`). You do **not** `@Import` it unless auto-config is disabled.

Also depend on tracer `2.9.0-SNAPSHOT` if the service uses Feign, so outbound Feign calls get the same correlation-id / logging behaviour as `RestTemplate`.

### 2. Ensure Kafka is available

The logger bean is `@ConditionalOnBean(ExternalApiAuditProducer.class)`, and the producer is `@ConditionalOnBean(KafkaTemplate.class)`.

Typical UPYOG services already have Kafka via tracer / spring-kafka. If `ExternalApiAuditLogger` does not inject, check that `KafkaTemplate` is a Spring bean.

### 3. Application properties

```properties
# Topic consumed by egov-persister (must match external-api-audit-persister.yml fromTopic)
external.api.audit.detail.topic=external-api-audit-details

# Persist request/response bodies inside the JSON envelope (masked). false = metadata only
external.api.audit.capture-payload.enabled=true
external.api.audit.max.payload.bytes=204800

# Jobs: enable ONLY on the service whose Postgres hosts ug_external_api_* (NDI today)
external.api.audit.reconciliation.enabled=false
external.api.audit.cleanup.enabled=false
```

| Property | Default | Meaning |
|---|---|---|
| `external.api.audit.detail.topic` | `external-api-audit-details` | Kafka topic for both INITIATED and SUCCESS/FAILED |
| `external.api.audit.capture-payload.enabled` | `true` | When false, envelope has `payloadCaptured: false` and no body |
| `external.api.audit.max.payload.bytes` | `204800` | Oversize bodies are replaced with a truncated marker |
| `external.api.audit.stale.threshold.ms` | `600000` | INITIATED older than this become `TIMED_OUT` |
| `external.api.audit.reconciliation.enabled` | `false` | NDI only |
| `external.api.audit.cleanup.enabled` | `false` | NDI only |
| `external.api.audit.cleanup.retention-ms` | `2592000000` | 30 days |

### 4. Add an API name constant (optional but recommended)

Reuse names in `ExternalApiAuditConstants`, or add a new constant so `external_api_name` stays stable for reporting:

```java
public static final String API_GSTN_VERIFY = "gstn-verify-pan";
```

### 5. Wrap inbound (external system → UPYOG)

Use `logInboundApi`. Direction is set to `INBOUND`. A **new audit UUID** is generated unless you pass `correlationId`. Put the caller’s RequestInfo / tracer id in `originatingCorrelationId`.

```java
@Autowired
private ExternalApiAuditLogger auditLogger;

@PostMapping("/v1/gstn/_verify")
public ResponseEntity<?> verify(@RequestBody GstnRequest request) {
    Object result = auditLogger.logInboundApi(ExternalApiAuditDetail.builder()
            .tenantId(request.getTenantId())          // required for the table; fallback "unknown"
            .externalApiName("gstn-verify-pan")       // stable name for this integration
            .requestPayload(request)                  // masked before persist
            .originatingCorrelationId(request.getRequestInfo().getCorrelationId())
            .method("POST")
            .endpoint("/my-service/v1/gstn/_verify")
            .build(),
            () -> gstnService.verify(request));       // business call
    return ResponseEntity.ok(result);
}
```

National Dashboard Ingest uses the same `ExternalApiAuditDetail` builder so the raw JSON envelope includes endpoint and method:

```java
integrationAuditLogger.logInboundApi(
        ExternalApiAuditDetail.builder()
                .originatingCorrelationId(requestInfo.getCorrelationId())
                .tenantId(ulbTenantId)
                .externalApiName(ExternalApiAuditConstants.API_NATIONAL_DASHBOARD_METRIC_INGEST)
                .requestPayload(ingestRequest)
                .endpoint("/national-dashboard/metric/_ingest")
                .method("POST")
                .build(),
        () -> ingestService.ingestData(ingestRequest));
```

### 6. Wrap outbound (UPYOG → third party)

Use `logAndExecute`. Direction is set to `OUTBOUND`. Return the **object you want stored as the response payload** from the lambda (for example the full auth response, not only the extracted token) so masking can apply to field names such as `AccessToken`.

```java
String token = auditLogger.logAndExecute(ExternalApiAuditDetail.builder()
        .tenantId("GSTN")
        .externalApiName("gstn-auth")
        .requestPayload(authRequest)
        .endpoint(authUrl)
                .method("POST")
        .build(), () -> {
    GstnAuthResponse response = gstnFeign.authenticate(authRequest);
    if (response == null || response.getAccessToken() == null) {
        throw new RuntimeException("GSTN auth token missing");
    }
    return response;   // audited (tokens masked); extract token after the wrap
}).getAccessToken();
```

### 7. Retries vs new requests

| Situation | `correlationId` | `retryCount` | Table result |
|---|---|---|---|
| First attempt | omit / `null` → new UUID | `0` | New row, INITIATED then SUCCESS/FAILED |
| Same request retried in-cycle (e.g. HTTP 401 then token refresh) | **reuse** the UUID | increment (`1`) | **Same row** updated; higher `retry_count` may overwrite FAILED |
| Next scheduler cycle / next day / new user call | **new** UUID | `0` | **New row** |

Example (DX PFMS data push):

```java
String auditCorrelationId = UUID.randomUUID().toString();
try {
    return pfmsApiClient.pushTransaction(txn, token, auditCorrelationId, 0);
} catch (Exception e) {
    if (!isUnauthorized(e)) {
        throw e;
    }
    token = pfmsApiClient.fetchAccessToken();
    return pfmsApiClient.pushTransaction(txn, token, auditCorrelationId, 1);
}
```

Persister `ON CONFLICT (correlation_id)` keeps `SUCCESS`/`FAILED` sticky unless the incoming `retry_count` is **greater** than the stored value.

### 8. Fail-open and original exceptions

- Kafka send is async (`whenComplete`). Broker downtime is logged; the business method still returns or throws its **original** exception.
- The logger never swallows the supplier exception.
- Audit publish failures must not change HTTP status of the business API.

### 9. Persister and database

Audit tables live in the **National Dashboard Ingest** Postgres schema (Flyway). Existing tables are reused:

- `ug_external_api_message_detail`
- `ug_external_api_message_raw_detail`
- `ug_external_api_error_detail`

Do **not** create replacement tables. Flyway scripts and persister YAML live in this library:

- `src/main/resources/db/migration/external-api-audit/` — table DDL (`V20250721130000`) and unique raw-detail index (`V20260918160000`)
- `src/main/resources/external-api-audit-persister.yml`

The service that owns the audit database (National Dashboard Ingest today) must include the library location:

```properties
spring.flyway.locations=classpath:/db/migration/main,classpath:/db/migration/external-api-audit
```

Leave that extra location off DX and other adapters so they do not create `ug_external_api_*` in their own databases.

Integration identity is `external_api_name`. Call sites use `ExternalApiAuditDetail` (`$.apiAuditDetail`), plus `ExternalApiAuditDetailWrapper` and `ExternalApiErrorDetails`.

`external-api-audit-persister.yml` maps `externalApiName` onto `ug_external_api_message_detail`. Endpoint and HTTP method are written into the raw JSON envelope, not as table columns. Kafka `basePath` remains `$.apiAuditDetail`.

Every producer (NDI, DX, future adapters) publishes to the same Kafka topic. `egov-persister` must load `core-services/libraries/external-api-audit/src/main/resources/external-api-audit-persister.yml`.

Do not JDBC-insert from the business service.

### 10. Schedulers (one database owner, one pod)

Jobs are packaged in this library but stay **disabled by default**.

Enable only on National Dashboard Ingest today (the service whose Postgres hosts `ug_external_api_*`):

```properties
external.api.audit.reconciliation.enabled=true
external.api.audit.reconciliation.cron=0 0 6 * * *
external.api.audit.cleanup.enabled=true
external.api.audit.cleanup.cron=0 30 3 * * *
external.api.audit.cleanup.retention-ms=2592000000
```

Leave them `false` on DX and any new adapter unless that service is the one hosting `ug_external_api_*`.

When NDI is scaled to multiple pods, JDBC **ShedLock** (`shedlock` table, Flyway `V20261007160000`) lets only one replica acquire `external-api-audit-reconciliation` / `external-api-audit-cleanup`. Other pods skip that tick. DX never starts these beans, so it does not compete for the lock.

---

## Payload envelope and masking

Endpoint and HTTP method are written into the raw JSON envelope in `ug_external_api_message_raw_detail.request_payload` / `response_payload`:

```json
{
  "originatingCorrelationId": "from-RequestInfo-or-MDC",
  "businessReferenceId": "txn-id",
  "endpoint": "https://...",
  "httpMethod": "POST",
  "method": "POST",
  "retryCount": 0,
  "payload": { }
}
```

When capture is off: `{ "endpoint": "...", "httpMethod": "POST", "method": "POST", "payloadCaptured": false, "retryCount": 0, ... }`.

`SensitivePayloadMasker` replaces values whose JSON field names match (case-insensitive, non-alphanumerics stripped): `password`, `authToken`, `AccessToken`, `RefreshToken`, `ULBBankAccountNumber`, `BeneficiaryAccountNumber`, `FromAccount`, `ToAccount`, and the rest of `external.api.audit.sensitive-fields`. Masked value is `********`.

Return structured objects from the audited lambda so token/account fields exist as named keys. A bare token `String` cannot be masked by field name.

---

## Reference integrations

| Service | Direction | `externalApiName` | Wrap site |
|---|---|---|---|
| national-dashboard-ingest | INBOUND | `national-dashboard-metric-ingest` | `MetricIngestController`, `BatchIngestionProcessorImpl` (one row per Excel row) |
| data-sharing-service-dx | INBOUND | `state-pfms-transaction-create` | `PFMSController#_create`, `tenantId=PFMS` |
| data-sharing-service-dx | OUTBOUND | `pfms-auth` | `PFMSApiClient#fetchAccessToken` |
| data-sharing-service-dx | OUTBOUND | `pfms-data-push` | `PFMSApiClient#pushTransaction` (401 retry reuses correlation id) |

DX application class imports `TracerConfiguration` so Feign tracing matches RestTemplate. That is **not** Kafka audit.

---

## Public API (JavaDoc in source)

| Type | Role |
|---|---|
| `ExternalApiAuditLogger` | Explicit executor. `logInboundApi` / `logAndExecute` / `execute` |
| `ExternalApiAuditDetail` | Existing production model used as the call-site payload |
| `ExternalApiAuditPublisher` | SPI; production impl is async Kafka |
| `SensitivePayloadMasker` | Field-name masking |
| `ExternalApiAuditProperties` | `external.api.audit.*` |
| `ExternalApiAuditReconciliationJob` / `CleanupJob` | Optional, NDI-only |

---

## Testing

Table-backed integration tests apply the same persister SQL against embedded PostgreSQL:

- [National Dashboard Ingest test plan](../../national-dashboard-ingest/docs/EXTERNAL_API_AUDIT_TESTING.md)
- [Data Sharing Service (DX/PFMS) test plan](../../../dx-services/data-sharing-service-dx/docs/EXTERNAL_API_AUDIT_TESTING.md)

```bash
cd core-services/libraries/external-api-audit && mvn test

cd core-services/national-dashboard-ingest
mvn test -Dtest=MetricIngestAuditIntegrationTest,BatchIngestAuditIntegrationTest,ExternalApiAuditLifecycleIntegrationTest

cd dx-services/data-sharing-service-dx
mvn test -Dtest=PFMSInboundAuditIntegrationTest,PFMSOutboundAuditIntegrationTest,PFMSForwardingRetryAuditIntegrationTest
```

### Integration checklist for a new module

1. Dependency + Kafka bean present; logger injects.
2. Jobs **off** unless this service owns the audit DB.
3. Every external inbound/outbound path wrapped (no global interceptor).
4. New logical call → new UUID; in-cycle retry → same UUID + higher `retryCount`.
5. Secrets/PII field names masked in `ug_external_api_message_raw_detail`.
6. Kafka down does not fail the business API.
7. After a call, one `message_detail` row + one `raw_detail` row; `error_detail` only on FAILED.
