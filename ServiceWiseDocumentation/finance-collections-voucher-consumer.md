# finance-collections-voucher-consumer

> Generated from repository path `business-services/finance-collections-voucher-consumer`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Kafka consumer that bridges collection payments to finance vouchers.

## Responsibilities

- Own the `finance-collections-voucher-consumer` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **17**
- Spring Boot version: **3.2.2**
- HTTP port: **deployment-defined**
- Servlet/context path: **/**
- Detected controllers/API mappings: **0**
- Detected migrations: **2**
- Detected tests: **1** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| builder | 1 source file(s) | Package area detected from source tree. |
| config | 1 source file(s) | Spring beans, properties, and runtime configuration. |
| egov | 1 source file(s) | Package area detected from source tree. |
| entity | 1 source file(s) | Database/table mapped domain state. |
| exception | 1 source file(s) | Custom exceptions and handlers. |
| model | 90 source file(s) | Request, response, DTO, and domain models. |
| repository | 2 source file(s) | Database or remote-service data access. |
| service | 9 source file(s) | Business orchestration and domain logic. |
| util | 1 source file(s) | Reusable helpers and cross-cutting functions. |

## Folder Structure

- `business-services/finance-collections-voucher-consumer`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `business-services/finance-collections-voucher-consumer/src/main/java/org/egov/FinanceCollectionsVoucherConsumerApplication.java`

## APIs

- No HTTP controllers were detected; this module is likely Kafka-only, utility-only, frontend-only, or legacy configuration-driven.

### API conventions

- Most backend services use DIGIT-style POST endpoints ending in `/_create`, `/_search`, `/_update`, `/_delete`, `/_count`, or `/_plainsearch`.
- Request payloads normally include `RequestInfo`; responses normally include `ResponseInfo` and one or more domain payload arrays/objects.
- Authentication is generally enforced at the gateway. Service-level security varies by service and must be checked before exposing routes directly.

## Business Flow

1. Client or another service reaches this service through Zuul/Spring Cloud Gateway or an internal cluster URL.
2. Gateway validates token state, enriches request headers such as user/correlation information, and performs RBAC checks where configured.
3. Controller validates the request and calls service-layer orchestration.
4. Service layer loads MDMS/configuration, performs domain validation, calls workflow/billing/idgen/user/location/localization/file-store integrations as required, and writes through repositories or Kafka topics.
5. Persistence events are consumed by `egov-persister`; indexing events are consumed by `egov-indexer`; notification events go to SMS/mail/user-event services.
6. The service returns a DIGIT-style response or publishes an asynchronous completion event.

```mermaid
sequenceDiagram
    autonumber
    participant Client
    participant Gateway
    participant S as finance_collections_voucher_consumer
    participant WF as egov-workflow-v2
    participant DB as PostgreSQL
    participant K as Kafka
    participant N as Notification/Audit/Indexer
    Client->>Gateway: HTTP request
    Gateway->>Gateway: auth, RBAC, correlation ID
    Gateway->>S: routed request
    S->>S: validate and orchestrate
    opt workflow-enabled action
      S->>WF: transition/search workflow
      WF-->>S: workflow state
    end
    opt synchronous persistence
      S->>DB: read/write via repository
      DB-->>S: result
    end
    opt async persistence/event
      S->>K: publish domain event
      K-->>N: consume for notification/audit/index
    end
    S-->>Gateway: response
    Gateway-->>Client: response
```

## Database

- **Tables detected from migrations:** egf_voucher_integration_log
- **Migration files:** 2
- **Repositories/JDBC classes:** 2
- **Entity/table-mapped classes:** 0

### Migration locations

- `business-services/finance-collections-voucher-consumer/src/main/resources/db/migration`
- `business-services/finance-collections-voucher-consumer/src/main/resources/db/migration/main`

### Repository locations

- `business-services/finance-collections-voucher-consumer/src/main/java/org/egov/receipt/consumer/repository/ServiceRequestRepository.java`
- `business-services/finance-collections-voucher-consumer/src/main/java/org/egov/receipt/consumer/repository/VoucherIntegartionLogRepository.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.group-id | fin-collection-receipt-voucher-consumer |
| spring.kafka.producer.key-deserializer | <secret-value> |
| spring.kafka.consumer.value-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| egov.collection.receipt.voucher.save.topic | egov.collection.receipt-create |
| egov.collection.receipt.voucher.cancel.topic | egov.collection.receipt-cancel |
| kafka.topics.egf.instrument.completed.topic | egov.egf.instrument.completed |
| kafka.topics.egf.instrument.completed.group | egov.egf.instrument.completed.group |
| kafka.topics.payment.create.name | egov.collection.payment-create |
| kafka.topics.payment.create.key | <secret-value> |
| kafka.topics.payment.cancel.name | egov.collection.payment-cancel |
| kafka.topics.payment.cancel.key | <secret-value> |
| kafka.topics.payment.update.name | egov.collection.payment-update |
| kafka.topics.payment.update.key | <secret-value> |

### Producers

- Not present in this repository or not detected.

### Consumers

- `business-services/finance-collections-voucher-consumer/src/main/java/org/egov/FinanceCollectionsVoucherConsumerApplication.java`
- `business-services/finance-collections-voucher-consumer/src/main/java/org/egov/receipt/consumer/service/EgfKafkaListener.java`
- `business-services/finance-collections-voucher-consumer/src/main/java/org/egov/receipt/consumer/service/InstrumentUpdateConsumer.java`

### Retry and dead-letter handling

- Standard services rely on Spring Kafka retry/container settings or the platform `tracer` library.
- `egov-persister` has an explicit dead-letter pattern (`egov-persister-deadletter`). Service-specific DLQ topics should be configured in deployment properties if required.

## Redis

- No explicit Redis configuration detected.

Cache strategy, TTLs, and key naming are normally configured in code/properties. When Redis is absent above, the service does not advertise a direct Redis dependency in its checked-in config.

## Workflow

No service-local workflow package was detected. The service may still participate indirectly through central workflow topics or gateway flows.

Typical workflow-enabled services use `WorkflowIntegrator` or call `/egov-wf/process/_transition` with tenant, business service, action, assignee, and audit information. States/actions/transitions are owned centrally by `egov-workflow-v2` business service definitions.

## External Integrations

| Config key | Endpoint/host |
| --- | --- |
| spring.flyway.url | jdbc:postgresql://localhost:5432/egf-collectionvoucher |
| management.endpoints.web.base-path | / |
| egov.services.egov.user.host | https://dev.digit.org/ |
| egov.services.mdms.hostname | https://dev.digit.org/ |
| egov.services.egfinstrument.hostname | https://dev.digit.org/ |
| egov.services.egfmaster.hostname | https://dev.digit.org/ |
| egov.services.collections.hostname | https://dev.digit.org/ |
| egov.services.businessservice.hostname | https://dev.digit.org/ |
| egov.services.instrument.search.accountcodes.uri | egf-instrument/instrumentaccountcodes/_search |
| egov.services.master.mdms.search.url | /egov-mdms-service/v1/_search |
| egov.services.egf.voucher.manualreceiptdate.config.url | services/EGF/rest/voucher/_ismanualreceiptdateenabled |
| egov.services.collection.receipts.view.source.url | /services/collection/receipts/receipt-viewReceipts.action |

```mermaid
sequenceDiagram
    participant S as finance_collections_voucher_consumer
    participant MDMS
    participant User
    participant IdGen
    participant Billing
    participant Collection
    participant FileStore
    participant External
    S->>MDMS: master data lookup when configured
    S->>User: user/profile lookup when configured
    S->>IdGen: ID generation when configured
    S->>Billing: demand/bill integration when configured
    S->>Collection: payment/receipt integration when configured
    S->>FileStore: file metadata/object access when configured
    S->>External: third-party REST/SOAP/provider calls when configured
```

## Security

- Authentication is primarily gateway-mediated using OAuth/JWT/opaque-token flows and `x-user-info` request enrichment.
- Authorization uses RBAC metadata from `egov-accesscontrol`; endpoint whitelists exist in `zuul`/`gateway` properties.
- Validate whether this service has local security configuration before direct exposure; several services assume gateway isolation.
- Sensitive properties must be supplied through Kubernetes secrets or external config, not committed literal values.

## Configuration

- `business-services/finance-collections-voucher-consumer/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| app.timezone | UTC |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/egf-collectionvoucher |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| spring.flyway.enabled | true |
| spring.flyway.user | postgres |
| spring.flyway.password | <secret-value> |
| spring.flyway.outOfOrder | true |
| spring.flyway.baseline-on-migrate | true |
| spring.flyway.url | jdbc:postgresql://localhost:5432/egf-collectionvoucher |
| spring.flyway.locations | classpath:/db/migration/main |
| spring.jpa.hibernate.naming.implicit-strategy | org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl |
| spring.jpa.hibernate.naming.physical-strategy | org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl |
| spring.jackson.deserialization.fail-on-unknown-properties | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| spring.kafka.listener.missing-topics-fatal | false |
| management.endpoints.web.base-path | / |
| spring.main.allow-bean-definition-overriding | false |
| spring.kafka.consumer.group-id | fin-collection-receipt-voucher-consumer |
| spring.kafka.producer.key-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| spring.kafka.consumer.value-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| egov.collection.receipt.voucher.save.topic | egov.collection.receipt-create |
| egov.collection.receipt.voucher.save.group | egov.collection.receipt-create.group |
| egov.collection.receipt.voucher.save.id | egov.collection.receipt-create.id |
| egov.collection.receipt.voucher.cancel.topic | egov.collection.receipt-cancel |
| egov.collection.receipt.voucher.cancel.group | egov.collection.receipt-cancel.group |
| egov.collection.receipt.voucher.cancel.id | egov.collection.receipt-cancel.id |
| kafka.topics.egf.instrument.completed.topic | egov.egf.instrument.completed |
| kafka.topics.egf.instrument.completed.group | egov.egf.instrument.completed.group |
| kafka.topics.payment.create.name | egov.collection.payment-create |
| kafka.topics.payment.create.key | payment-create |
| kafka.topics.payment.cancel.name | egov.collection.payment-cancel |
| kafka.topics.payment.cancel.key | payment-cancel |
| kafka.topics.payment.update.name | egov.collection.payment-update |

## Logging

- Platform services use Spring logging plus `tracer` for correlation IDs and structured exception responses.
- Gateway filters are responsible for request correlation; services should propagate correlation/user headers downstream.
- Audit events are emitted to Kafka/audit-service where configured.

## Exception Handling

- Common pattern: validation errors become `CustomException`/domain exceptions and are rendered by `tracer` or service-specific `GlobalExceptionHandler`.
- Controller-level `@Valid` handles Bean Validation for request models where annotations exist.
- Kafka consumers should be monitored for poison messages and retry loops.

## Testing

- Test files detected: **1**.
- Unit tests typically cover validators, services, query builders, and controllers.
- Integration tests require PostgreSQL, Kafka, Redis, and dependent services or mocks.

## Deployment

- `business-services/finance-collections-voucher-consumer/src/main/resources/db/Dockerfile`

- Most Java services are built as executable JAR containers using Maven and the shared `core-services/build/maven/Dockerfile` pattern.
- Database migrations are packaged separately where `src/main/resources/db/Dockerfile` exists and run Flyway with `DB_URL`, `FLYWAY_USER`, `FLYWAY_PASSWORD`, `FLYWAY_LOCATIONS`, and `SCHEMA_TABLE`.
- Kubernetes/Helm manifests are not checked into this repository; deployment values are managed externally.

## Monitoring

- Health endpoints are usually Spring Actuator-backed, frequently exposed at `/health` because many services set `management.endpoints.web.base-path=/`.
- Gateway has additional OpenTelemetry/Jaeger-related configuration.
- Production deployments should scrape actuator/Prometheus endpoints, Kafka consumer lag, DB pool metrics, and JVM metrics.

## Performance

- Primary bottlenecks are database query complexity, Kafka consumer lag, synchronous inter-service calls, external provider latency, and JVM heap limits.
- Prefer indexed search columns, bounded page sizes, connection pool sizing, Redis for hot reference data, and async publication for slow side effects.
- Check thread pools and Kafka concurrency for write-heavy services.

## Common Problems

- Missing dependent service host property or DNS entry.
- Flyway migration order/table mismatch.
- Kafka topic not created or wrong consumer group.
- Gateway whitelist/RBAC misconfiguration.
- Redis/PostgreSQL connectivity issues.
- Java 17 services run with Java 8 images or legacy Java 8 services run with Java 17 images.

## Improvement Suggestions

- Add/refresh OpenAPI contracts for controllers that lack contract YAML.
- Add integration tests around workflow, billing, collection, and persister events.
- Externalize all secrets and remove defaults from deployment overlays.
- Standardize health, metrics, logging, and correlation-ID propagation.
- Normalize package names and remove duplicate/legacy code where the service has modern equivalents.
