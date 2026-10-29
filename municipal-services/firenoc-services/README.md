# Fire NOC Service

A Node.js microservice that issues **Fire No Objection Certificates (NOC)** — official documents confirming a building complies with fire safety norms and regulations. Part of the UPYOG municipal services platform.

---

## What This Service Does

- Accepts Fire NOC applications from citizens or ULB employees
- Validates application data against MDMS masters (building type, fire stations, UOMs, etc.)
- Drives applications through a configurable workflow (INITIATED → PENDINGPAYMENT → DOCUMENTVERIFY → FIELDINSPECTION → PENDINGAPPROVAL → APPROVED)
- Generates unique application numbers and certificate numbers via the IDGen service
- Persists data to PostgreSQL and publishes events to Kafka
- Sends SMS and in-app notifications at each workflow stage
- Listens to payment events from the collection service to auto-advance workflow on payment

---

## Tech Stack

| Layer | Technology |
|---|---|
| Runtime | Node.js 22 (LTS) |
| Language | ES2022+ with Babel 7 transpilation |
| Framework | Express.js 4 |
| Database | PostgreSQL (via `pg` connection pool) |
| Messaging | Apache Kafka (via `kafkajs` v2) |
| Validation | AJV (JSON Schema) + XSS sanitization |
| HTTP Client | Axios v1 |
| Logging | Winston |
| Health Checks | @godaddy/terminus (Kubernetes liveness/readiness) |
| API Docs | Swagger UI (`/api-docs`) |
| Containerization | Docker (node:22.11.0-alpine) |

---

## Folder Structure

```
firenoc-services/
├── src/
│   ├── api/                    # Route handlers — thin layer, delegates to utils
│   │   ├── create.js           # POST /_create endpoint
│   │   ├── search.js           # POST /_search endpoint
│   │   ├── update.js           # POST /_update endpoint
│   │   └── index.js            # Mounts all routes under /firenoc-services/v1
│   ├── config/
│   │   ├── httpClient.js       # Axios instance factory with request/response logging
│   │   └── logger.js           # Winston logger instance
│   ├── kafka/
│   │   ├── producer.js         # kafkajs producer with backward-compatible send() adapter
│   │   └── consumer.js         # kafkajs consumer — handles SMS, payment, workflow events
│   ├── lib/
│   │   └── util.js             # Generic Express response helper
│   ├── middleware/
│   │   ├── index.js            # Middleware registry
│   │   └── tracer.js           # Correlation ID injection and request logging
│   ├── model/
│   │   ├── fireNOC.js          # AJV JSON schema for FireNOC create/update validation
│   │   └── fireNOCSearch.js    # AJV JSON schema for search query validation
│   ├── services/
│   │   ├── firenocCalculatorService.js   # Calls firenoc-calculator to compute fees
│   │   └── userService.js               # Calls egov-user for create/search/update
│   ├── utils/
│   │   ├── api.js              # Generic httpRequest wrapper around Axios
│   │   ├── create.js           # UUID generation, audit details, user enrichment
│   │   ├── health.js           # Terminus health check — verifies DB connectivity
│   │   ├── index.js            # Shared utilities: IDGen, workflow, location, topic helpers
│   │   ├── mdmsData.js         # Fetches all required MDMS masters in one call
│   │   ├── modelValidation.js  # AJV validation with custom MDMS-backed keywords
│   │   ├── notificationUtil.js # Builds and sends SMS/event notifications via Kafka
│   │   ├── search.js           # DB row → FireNOC object mapper, user enrichment
│   │   └── update.js           # Approved list builder for certificate generation
│   ├── config.json             # Static config: port, body limit, CORS headers
│   ├── db.js                   # PostgreSQL connection pool
│   ├── envVariables.js         # All environment variable definitions with defaults
│   ├── index.js                # Express app entry point
│   └── swagger.json            # OpenAPI spec served at /api-docs
├── docs/
│   ├── contract/
│   │   ├── fire_noc_contract.yaml      # Swagger API contract
│   │   └── firenoc_persiter.yaml       # Kafka persister configuration
│   └── workflowconfigs/
│       └── PostmanScriptWorkflowFireNoc.json
├── migration/
│   └── ddl/                    # Flyway SQL migration scripts for DB schema
├── .babelrc                    # Babel 7 config targeting Node 22
├── Dockerfile                  # Multi-stage Docker build using node:22.11.0-alpine
├── docker-compose.yml          # Local Kafka setup for development
├── package.json
└── CHANGELOG.md
```

---

## API Endpoints

Base path: `/firenoc-services/v1`

### POST `/_create`
Creates a new Fire NOC application with `INITIATED` status.

- Validates request body against AJV schema and MDMS masters
- Generates application number via IDGen
- Triggers workflow transition
- Calculates fees via firenoc-calculator
- Publishes to Kafka topic `save-fn-firenoc`

Allowed roles: `NOC_CEMP`, `CITIZEN`

### POST `/_update`
Updates an existing application — used by employees to take workflow actions.

- Enriches assignees for `SENDBACKTOCITIZEN` action
- Triggers workflow transition
- On `APPROVE` action: generates Fire NOC certificate number and sets validity dates
- Publishes to Kafka topic `update-fn-firenoc`

Allowed roles: `NOC_CEMP`, `CITIZEN`, `NOC_DOC_VERIFIER`, `NOC_FIELD_INSPECTOR`, `NOC_APPROVER`

### POST `/_search`
Searches Fire NOC applications with filters.

- Citizens only see their own applications (filtered by mobile number / UUID)
- Employees can search by `tenantId`, `applicationNumber`, `status`, `mobileNumber`, date range, etc.
- Supports pagination via `offset` and `limit`

Allowed roles: `NOC_CEMP`, `CITIZEN`, `NOC_DOC_VERIFIER`, `NOC_FIELD_INSPECTOR`, `NOC_APPROVER`, `EMPLOYEE`

### GET `/health`
Kubernetes liveness/readiness probe — checks DB connectivity.

### GET `/api-docs`
Swagger UI for interactive API exploration.

---

## Service Dependencies

| Service | Purpose |
|---|---|
| `egov-mdms-service` | Fetches masters: BuildingType, FireStations, UOMs, OwnerShipCategory, FinancialYear |
| `egov-user` | Create, search and update citizen/owner user records |
| `egov-idgen` | Generates application numbers and certificate numbers |
| `egov-workflow-v2` | Drives application through approval workflow |
| `egov-location` | Fetches boundary/locality data for validation |
| `firenoc-calculator` | Calculates Fire NOC fees |
| `PostgreSQL` | Persists all Fire NOC application data |
| `Apache Kafka` | Async event publishing and consuming |

---

## Kafka Topics

### Producers (publishes to)

| Topic | Purpose |
|---|---|
| `save-fn-firenoc` | Persist new Fire NOC application |
| `update-fn-firenoc` | Persist updated Fire NOC application |
| `update-fn-workflow` | Notify workflow update for approved applications |
| `save-fn-firenoc-sms` | Trigger SMS notification on create |
| `update-fn-firenoc-sms` | Trigger SMS notification on update |
| `update-fn-workflow-sms` | Trigger SMS notification on workflow change |
| `egov.core.notification.sms` | Send SMS to citizen |
| `persist-user-events-async` | Send in-app event notification |

### Consumers (listens to)

| Topic | Purpose |
|---|---|
| `egov.collection.payment-create` | Auto-advance workflow when payment is received |
| `save-fn-firenoc-sms` | Send SMS after application creation |
| `update-fn-firenoc-sms` | Send SMS after application update |
| `update-fn-workflow-sms` | Send SMS after workflow transition |

---

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `SERVER_PORT` | `8081` | Port the service listens on |
| `KAFKA_BROKER_HOST` | `localhost:9092` | Kafka broker address |
| `DB_HOST` | — | PostgreSQL host |
| `DB_NAME` | — | PostgreSQL database name |
| `DB_USER` | — | PostgreSQL user |
| `DB_PASSWORD` | — | PostgreSQL password |
| `DB_PORT` | `5432` | PostgreSQL port |
| `DB_SSL` | `false` | Enable SSL for DB connection |
| `EGOV_MDMS_HOST` | `http://egov-mdms-service:8080/` | MDMS service host |
| `EGOV_USER_HOST` | `http://egov-user:8080/` | User service host |
| `EGOV_IDGEN_HOST` | `http://egov-idgen:8080/` | IDGen service host |
| `EGOV_WORKFLOW_HOST` | `http://egov-workflow-v2:8080/` | Workflow service host |
| `EGOV_LOCATION_HOST` | `http://egov-location:8080/` | Location service host |
| `EGOV_FN_CALCULATOR_HOST` | `http://firenoc-calculator:8080/` | Calculator service host |
| `EGOV_DEFAULT_STATE_ID` | `pg` | Default state tenant ID |
| `IS_ENVIRONMENT_CENTRAL_INSTANCE` | `false` | Enable multi-state central instance mode |
| `TRACER_ENABLE_REQUEST_LOGGING` | `false` | Log full request/response bodies |
| `HTTP_CLIENT_DETAILED_LOGGING_ENABLED` | `false` | Log detailed HTTP client calls |
| `BUSINESS_SERVICE` | `FIRENOC` | Business service identifier for workflow |

---


### Swagger API Contract

http://localhost:8081/api-docs

---

## Local Setup

### Prerequisites
- Node.js 22+
- Docker (for Kafka)
- PostgreSQL instance

### 1. Start Kafka
```bash
docker compose up -d
```

### 2. Install dependencies
```bash
npm install
```

### 3. Set environment variables
```bash
export DB_HOST=localhost
export DB_NAME=firenoc
export DB_USER=postgres
export DB_PASSWORD=postgres
export KAFKA_BROKER_HOST=localhost:9092
```

### 4. Run in development mode (with hot reload)
```bash
npm run dev
```

### 5. Run in production mode
```bash
npm start
```

### 6. Build only
```bash
npm run build
```

---

## Database

The service uses PostgreSQL with the following core tables:

| Table | Description |
|---|---|
| `eg_fn_firenoc` | Root Fire NOC record |
| `eg_fn_firenocdetail` | Application details, status, workflow action |
| `eg_fn_address` | Property address |
| `eg_fn_owner` | Owner/applicant records |
| `eg_fn_buidlings` | Building records linked to application |
| `eg_fn_buildinguoms` | Unit of measurement values per building |
| `eg_fn_buildingdocuments` | Uploaded documents per building |

Migration scripts are in `migration/ddl/` and follow Flyway versioning conventions.

---

## Workflow States

```
INITIATED → PENDINGPAYMENT → DOCUMENTVERIFY → FIELDINSPECTION → PENDINGAPPROVAL → APPROVED
                                    ↓                  ↓                 ↓
                            CITIZENACTIONREQUIRED  CITIZENACTIONREQUIRED  REJECTED
```

---

## Swagger API Contract

Interactive docs available at `/api-docs` when the service is running.

Full contract: [fire_noc_contract.yaml](docs/contract/fire_noc_contract.yaml)

---

## Docker

```bash
# Build image
docker build --build-arg WORK_DIR=. -t firenoc-services .

# Run container
docker run -p 8081:8081 \
  -e DB_HOST=<host> \
  -e DB_NAME=<db> \
  -e DB_USER=<user> \
  -e DB_PASSWORD=<password> \
  -e KAFKA_BROKER_HOST=<broker> \
  firenoc-services
```
