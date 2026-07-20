# Garbage Service

A Spring Boot microservice for managing garbage collection accounts, billing, scheduling, and notifications in the UPYOG/NIUA municipal platform.

## Overview

The Garbage Service handles the full lifecycle of municipal solid waste management:
- Citizen and commercial garbage account registration
- Workflow-based application processing
- Monthly/on-demand bill generation and demand management
- Payment tracking, penalty, and rebate processing
- SMS/email notifications and PDF receipt generation
- Excel exports and dashboard counts

## Tech Stack

| Component | Version |
|-----------|---------|
| Java | 17 |
| Spring Boot | 3.2.2 |
| Database | PostgreSQL |
| Migration | Flyway |
| Message Broker | Apache Kafka |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |

## Dependencies

- `egov-tracer` 2.9.1-SNAPSHOT — request tracing
- `enc-client` 2.9.0 — field-level encryption
- `mdms-client` 2.9.0-SNAPSHOT — master data lookups
- `poi-ooxml` 5.4.1 — Excel export
- `javers-core` 7.8.6 — audit diffing

## Configuration

Copy `src/main/resources/application.properties` and override the following:

```properties
# Server
server.port=1235
server.servlet.context-path=/garbage-service

# Database
spring.datasource.url=jdbc:postgresql://<host>:5432/<db>
spring.datasource.username=<username>
spring.datasource.password=<password>

# Kafka
kafka.config.bootstrap_server_config=<kafka-host>:9092
spring.kafka.consumer.group-id=egov-grbg-services

# Dependent Services
egov.mdms.host=http://<mdms-host>
workflow.context.path=http://<workflow-host>
egov.bill.context.host=http://<billing-host>
egov.user.host=http://<user-service-host>
egov.enc.host=http://<enc-service-host>
egov.sms.host=http://<sms-service-host>
```

## Building & Running

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run

# Or run the jar
java -jar target/garbage-service-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Garbage Accounts — `/garbage-accounts`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/_create` | Register a new garbage account |
| POST | `/_update` | Update account details |
| POST | `/_update_status` | Workflow status transition |
| POST | `/_search` | Search accounts (supports index-backed search via `?IsIndex=true`) |
| POST | `/open/_search` | Citizen open search/pay preview (no auth required; at least one of mobileNumber, applicationNumber, propertyId, oldGarbageIds, or name required) |
| POST | `/fetch/{CALCULATEFEE\|ACTIONS}` | Get fee calculation or available workflow actions |
| POST | `/_payNow` | Initiate pay-now for a bill |
| POST | `/_createUserForGarbage` | Provision a citizen user for an account |
| POST | `/_counts` | Dashboard aggregate counts |
| POST | `/_generateGrbgTaxBillReceipt` | Generate PDF tax bill receipt |
| POST | `/_createArear` | Generate arrear demand |

### Garbage Bills — `/garbage-bills`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/_create` | Persist new bill records |
| POST | `/_update` | Update bill records |
| POST | `/_search` | Search bills |
| POST | `_cancelbill` | Cancel a bill |

### Scheduler — `/garbage-accounts-scheduler`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/bill-generator` | Bulk monthly bill generation |
| POST | `/on-demand-generation` | On-demand bill generation |
| POST | `/penalty/_update` | Run penalty processing |
| POST | `/reverse-rebate-amount` | Reverse applied rebates |
| POST | `/extract-tracker` | Fetch bill tracker by bill ID |

### Common — `/garbage-common`

| Method | Path | Description |
|--------|------|-------------|
| POST | `/_create` | Create/update common master data |
| GET | `/getAllCounts` | Aggregate count metrics |

### Excel — `/garbage-excel`

Excel report export endpoints.

### SMS — `/garbage-sms`

SMS notification management endpoints.

## Swagger UI

After starting the service, API docs are available at:

```
http://localhost:1235/garbage-service/index.html
```

## Database Schema

Flyway migrations run automatically on startup from `src/main/resources/db/migration/ddl/`.

Key tables:

| Table | Description |
|-------|-------------|
| `eg_grbg_account` | Core garbage account records |
| `eg_grbg_bill` | Bill records linked to accounts |
| `eg_grbg_application` | Workflow application tracking |
| `eg_grbg_commercial_details` | Commercial property details |
| `eg_grbg_collection_unit` | Collection unit configuration |
| `eg_grbg_collection_staff` | Staff assignments per unit |
| `eg_grbg_document` | Document attachments |
| `eg_grbg_charge` | Charge/rate master data |
| `eg_grbg_collection` | Collection records |
| `eg_grbg_address` | Account addresses |
| `eg_grbg_scheduled_requests` | Scheduled billing requests |
| `eg_grbg_old_details` | Legacy ID mapping |
| `eg_grbg_declaration` | Declaration statements |
| `eg_grbg_bill_tracker` | Bill generation tracker with penalty/rebate |
| `eg_grbg_account_audit` | Account change audit trail |
| `eg_bill_failure` | Failed bill generation logs |

## Kafka Topics

| Topic | Purpose |
|-------|---------|
| `save-grbg-account` | Persist new garbage accounts |
| `update-grbg-account` | Update garbage accounts |
| `egov.collection.payment-create` | Bill tracker status updates |
| `egov.core.notification.sms` | SMS notifications |
| `sanatize.failure` | Sanitization failure logging |

## Integrated Services

| Service | Purpose |
|---------|---------|
| egov-workflow-v2 | Application lifecycle workflow |
| billing-service | Demand and bill management |
| egov-mdms-service / mdms-v2 | Master data (rates, categories) |
| user-service | Citizen user provisioning |
| egov-enc-service | PII field encryption/decryption |
| pdf-service | Bill receipt PDF generation |
| hpud-dms-service (Alfresco) | Document storage |
| notification-sms | SMS delivery |
| egov-url-shortening | Short URLs for payment links |

## Project Structure

```
src/main/java/org/egov/garbageservice/
├── controller/        # REST endpoints
├── service/           # Business logic
├── repository/        # JDBC data access + query builders + row mappers
├── model/             # Domain models and request/response DTOs
├── contract/          # External service contracts (bill, workflow)
├── consumer/          # Kafka consumers
├── producer/          # Kafka producer
├── enums/             # Domain enumerations
├── config/            # Swagger configuration
└── util/              # Constants, utilities, helpers
```
