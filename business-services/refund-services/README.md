# Refund Service

Manages refund applications across UPYOG modules, including workflow verification and approval, online refund initiation, offline completion and refund status tracking.

## Service Dependencies

- Workflow Service (`egov-workflow-v2`)
- ID Generation Service (`egov-idgen`)
- Collection Service (`collection-services`)
- Payment Gateway Service (`egov-pg-service`)
- MDMS Service (`egov-mdms-service`)
- PDF Service (`pdf-service`)
- Persister Service (`egov-persister`)
- PostgreSQL and Kafka

## Service Details

- Generates a refund reference and validates application details and amounts.
- Processes verification, approval and rejection through the configured workflow.
- Supports optional Finance handoff and approval/rejection callbacks.
- Initiates online refunds through PG service and consumes refund status events.
- Supports offline completion through the `COMPLETE_REFUND` action.
- Reuses an existing receipt file-store ID or generates a payment receipt using MDMS, Collection and PDF services.
- Publishes refund records and audit snapshots for persistence.

Workflow states, actions and roles are configured in Workflow Service for each business service.

## API Details

**Base path:** `/refund-services/refund/v1`

All endpoints use **POST** with `Content-Type: application/json`. Requests contain `RequestInfo`; create/update requests also contain a `refund` object.

| Endpoint | Description |
|---|---|
| `/_create` | Validates and creates a refund, generates its reference and initiates workflow. |
| `/_update` | Updates refund details and processes workflow actions, approval/rejection callbacks or offline completion. |
| `/_get` | Retrieves a refund by `id` or `refundNo`. |
| `/_search` | Searches refunds using application, payment and status filters. |

Implemented search filters: `tenantId`, `moduleName`, `businessService`, `consumerCode`, `paymentId`, `refundNo`, `status`, `refundCategory`, `gatewayRefundId` and `sanctionRef`. Results are ordered by creation time, newest first.

### Validation

- Create requires tenant, module, business service, consumer code, payment ID, refund category, refund reason and workflow action.
- Amount paid and refund amount must be non-negative; refund amount cannot exceed amount paid.
- Update requires refund ID, refund amount, refund category and refund reason.
- Refund modes are `ONLINE` and `OFFLINE`. When omitted, the mode is derived from the original payment mode.
- Initial Finance handoff does not require a sanction reference or approval date; an approval callback requires both.

## Kafka Consumers

| Topic | Purpose |
|---|---|
| `update-pg-refund-txns` | Receives PG refund status updates and processes terminal workflow actions. |

The consumed `PaymentRefundRequest` contains a `Refund` object. Successful completion requires both `status` and `gatewayStatusMsg` to equal `SUCCESS`; `FAILURE` or `FAILED` triggers rejection.

## Kafka Producers

| Topic | Purpose |
|---|---|
| `save-refund` | Persists new refund applications. |
| `update-refund` | Persists refund updates. |
| `audit-refund` | Persists refund audit snapshots. |
| `egov.refund.finance.process` | Publishes applications for approval handoff. |
| `egov.refund.finance.payment` | Publishes completed refunds for downstream processing. |

Topic names are configurable. Finance events contain `RequestInfo` and `refund` in a `RefundRequest` envelope. Database writes are asynchronous and require the corresponding external persister mappings.

## Configurable Properties

Configure these in `src/main/resources/application.properties` or deployment overrides.

| Property | Purpose |
|---|---|
| `refund.workflow.send-to-finance` | Controls initial Finance handoff after approval. |
| `workflow.wf-host`, `workflow.wf-transition-path` | Workflow transition endpoint. |
| `idgen.host`, `idgen.endpoint`, `refund.idgen.name`, `refund.idgen.format` | Refund reference generation. |
| `egov.collectionservice.host`, `egov.collectionservice.payment.search` | Original payment lookup. |
| `pg.service.host`, `pg.initiate.refund.endpoint` | Online refund initiation. |
| `mdms.host`, `mdms.search` | Receipt key lookup from `common-masters.uiCommonPay`. |
| `pdf.host`, `pdf.create` | Receipt PDF generation. |
| `egov.system.user.uuid`, `statelevel.tenant.id` | System user for automatic workflow actions. |
| `spring.datasource.*`, `spring.flyway.*` | Database and migration configuration. |
| `spring.kafka.bootstrap.servers`, `spring.kafka.consumer.group-id` | Kafka connection and consumer group. |
| `egov.persister.save.refund.topic`, `egov.persister.update.refund.topic`, `egov.persister.audit.refund.topic` | Persistence topics. |
| `egov.refund.finance-topic`, `egov.refund.finance.payment.topic` | Handoff and completion topics. |
| `refund.kafka.payment-refund-topic` | Incoming PG refund status topic. |

The handoff flag controls the initial approval path; completion-event publishing is invoked separately.

## Database

- `eg_rs_refund`: Current refund application details.
- `eg_rs_refund_audit`: Refund snapshots and workflow actions.

Flyway migrations are located under `src/main/resources/db/migration/main`.

## Build and Run

Requires **Java 17**, Maven and configured service dependencies.

```bash
mvn clean package
mvn spring-boot:run
```

Default local base URL: `http://localhost:8280/refund-services`.

Override database credentials, integration hosts, Kafka settings and system-user configuration for the target environment.
