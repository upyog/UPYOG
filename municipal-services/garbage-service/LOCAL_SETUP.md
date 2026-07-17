# Garbage Service — Local Setup Guide

How to run `garbage-service` locally with all its dependencies. The service runs on
**port 1235** at context path **`/garbage-service`**.

---

## 1. Prerequisites (install once)

| Tool | Version |
|------|---------|
| JDK | 17 |
| Maven | 3.8+ |
| PostgreSQL | 12+ (running locally) |
| Apache Kafka | 3.x (running locally) |
| kubectl | configured with access to the dev cluster (for port-forwarding shared services) |

Verify:
```bash
java -version      # should be 17
mvn -version
kubectl get pods -n egov   # confirm cluster access (adjust namespace)
```

---

## 2. Local infrastructure (must be UP before the service starts)

The service connects to **PostgreSQL** and **Kafka** at startup (Flyway runs DB migrations on boot).

### PostgreSQL
- URL: `jdbc:postgresql://localhost:5432/postgres`
- user / pass: `postgres` / `postgres`

```bash
# example with docker
docker run -d --name pg -e POSTGRES_PASSWORD=postgres -p 5432:5432 postgres:14
```

### Kafka
- Bootstrap server: `localhost:9092`

```bash
# start zookeeper + kafka (however you normally run it locally)
# confirm it's listening:
nc -zv localhost 9092
```

> The service will not start if Postgres or Kafka is unreachable.

---

## 3. Dependent services — `kubectl port-forward`

These are called at **request time** (not startup). Port-forward each one so the local
`application.properties` URLs resolve. The in-cluster port is usually `8080` — adjust if different.

```bash
# State machine for application lifecycle
kubectl port-forward -n egov svc/egov-workflow-v2 8280:8080

# Citizen user create/search (needed by _create)
kubectl port-forward -n egov svc/egov-user-service 8081:8080

# PII encryption/decryption (needed by _create)
kubectl port-forward -n egov svc/egov-enc-service 1234:8080

# Master data lookup (rates, categories)
kubectl port-forward -n egov svc/egov-mdms-service 8094:8080
kubectl port-forward -n egov svc/mdms-v2 9080:8080

# Billing — demand creation & bill fetch
kubectl port-forward -n egov svc/billing-service 8086:8080

# PDF generation (bill/receipt)
kubectl port-forward -n egov svc/pdf-service 7275:8080

# Short links for pay-now SMS
kubectl port-forward -n egov svc/egov-url-shortening 8093:8080

# SMS notification tracker
kubectl port-forward -n egov svc/notification-sms 1237:8080
```

### Port reference (from `application.properties`)

| Service | Local port | Property |
|---------|-----------|----------|
| egov-workflow-v2 | 8280 | `workflow.context.path` |
| egov-user-service | 8081 | `egov.user.host` |
| egov-enc-service | 1234 | `egov.enc.host` |
| egov-mdms-service | 8094 | `egov.mdms.host` |
| mdms-v2 | 9080 | `mdms.v2.host` |
| billing-service | 8086 | `egov.bill.context.host` |
| pdf-service | 7275 | `egov.report.host` |
| egov-url-shortening | 8093 | `egov.url.shortning.host` |
| notification-sms | 1237 | `egov.sms.host` |
| hpud-dms-service (Alfresco) | `192.168.29.220:8095` | `egov.alfresco.host` — external IP, may need VPN |

> **Minimum to run `_create`:** workflow-v2, user-service, enc-service, mdms. The rest are
> only hit by their specific APIs (billing, PDF, SMS, etc.).

---

## 4. egov-persister (run locally)

Account create/update is **async** — `garbage-service` publishes to Kafka and **egov-persister**
writes the rows to the DB. Without persister running, `_create` returns success but nothing is
saved.

Location: `core-services/egov-persister`

1. Point persister at the **same database** as garbage-service (`localhost:5432/postgres`).
   In `egov-persister/src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
   spring.datasource.username=postgres
   spring.datasource.password=postgres
   ```
   > ⚠️ If persister and garbage-service point at different DB names, records published to Kafka
   > land in a different DB than the service reads from — looks like "save did nothing."

2. Make sure the **garbage persister YAML** (the one that maps `save-grbg-account` /
   `update-grbg-account` topics to the `eg_grbg_*` tables) is loaded via:
   ```properties
   egov.persist.yml.repo.path=file:///absolute/path/to/garbage-services.yml
   ```
   (or point it at a folder containing that file).

3. Run it:
   ```bash
   cd core-services/egov-persister
   mvn spring-boot:run
   ```
   Persister listens on port **8082** (`/common-persist`) and consumes:
   - `save-grbg-account`
   - `update-grbg-account`

---

## 5. Build & run garbage-service

```bash
cd municipal-services/garbage-service

# build
mvn clean install -DskipTests

# run
mvn spring-boot:run
# or
java -jar target/garbage-service-1.0.0.jar
```

---

## 6. Verify

- Swagger UI: <http://localhost:1235/garbage-service/index.html>
- Health: hit any `_search` endpoint, or watch the startup log for
  `Started ... on port 1235`.

---

## 7. Startup order (summary)

1. PostgreSQL (5432) + Kafka (9092) — up first
2. All `kubectl port-forward` sessions (section 3)
3. egov-persister (`mvn spring-boot:run` in core-services/egov-persister)
4. garbage-service (`mvn spring-boot:run`)

---

## 8. Common gotchas

- **Save "did nothing":** persister not running, or persister DB ≠ garbage-service DB (section 4).
- **`Connection refused` to a port:** the matching `kubectl port-forward` is down — re-run it.
- **Workflow errors on `_create`** (`No businessService for ...`, `INVALID ROLE`): the workflow
  business service for the user's role isn't seeded in egov-workflow-v2 for tenant `pg`.
- **`Application number` / idgen errors:** if the egov-idgen integration is enabled, also
  port-forward `egov-idgen` to `8285` and register the `garbage.application.num` format in MDMS.
- **`MISSING_ADDRESS_DETAILS`:** the address payload is missing required fields for that build.
