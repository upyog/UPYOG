# street-vending

> Generated from repository path `municipal-services/street-vending`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Street vending application and workflow service.

## Responsibilities

- Own the `street-vending` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **17**
- Spring Boot version: **3.2.2**
- HTTP port: **8080**
- Servlet/context path: **/sv-services**
- Detected controllers/API mappings: **7**
- Detected migrations: **11**
- Detected tests: **4** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| billing | 9 source file(s) | Package area detected from source tree. |
| common | 9 source file(s) | Package area detected from source tree. |
| config | 3 source file(s) | Spring beans, properties, and runtime configuration. |
| constants | 2 source file(s) | Package area detected from source tree. |
| consumer | 2 source file(s) | Kafka/event consumers. |
| controller | 2 source file(s) | HTTP endpoints and request/response orchestration. |
| enums | 3 source file(s) | Package area detected from source tree. |
| events | 8 source file(s) | Package area detected from source tree. |
| idgen | 4 source file(s) | Package area detected from source tree. |
| impl | 3 source file(s) | Package area detected from source tree. |
| mapper | 3 source file(s) | DTO/entity conversion. |
| model | 21 source file(s) | Request, response, DTO, and domain models. |
| notification | 3 source file(s) | Package area detected from source tree. |
| producer | 1 source file(s) | Kafka/event producers. |
| querybuilder | 1 source file(s) | Package area detected from source tree. |
| repository | 4 source file(s) | Database or remote-service data access. |
| service | 13 source file(s) | Business orchestration and domain logic. |
| sv | 1 source file(s) | Package area detected from source tree. |
| transaction | 4 source file(s) | Package area detected from source tree. |
| util | 8 source file(s) | Reusable helpers and cross-cutting functions. |
| validation | 2 source file(s) | Package area detected from source tree. |
| validator | 7 source file(s) | Input and domain validation. |
| workflow | 8 source file(s) | Workflow transition/search integration and state handling. |

## Folder Structure

- `municipal-services/street-vending`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `municipal-services/street-vending/src/main/java/org/upyog/sv/StreetVendingApplication.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /street-vending/_create | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /street-vending/_search | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /street-vending/_update | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /street-vending/_deletedraft | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /street-vending/_createdemand | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /street-vending/trigger-expire-streetvendingapplications | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /street-vending/trigger-payment-schedule | StreetVendingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as street_vending
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

- **Tables detected from migrations:** eg_sv_address_detail, eg_sv_bank_detail, eg_sv_beneficiary_schemes_detail, eg_sv_document_detail, eg_sv_operation_time_detail, eg_sv_street_vending_detail, eg_sv_street_vending_detail_auditdetails, eg_sv_street_vending_draft_detail, eg_sv_vendor_detail, eg_sv_vendor_detail_auditdetails, eg_sv_vendor_payment_schedule, eg_sv_vendor_payment_schedule_auditdetails, shedlock
- **Migration files:** 11
- **Repositories/JDBC classes:** 6
- **Entity/table-mapped classes:** 0

### Migration locations

- `municipal-services/street-vending/src/main/resources/db/migration`
- `municipal-services/street-vending/src/main/resources/db/migration/main`

### Repository locations

- `municipal-services/street-vending/src/main/java/org/upyog/sv/StreetVendingApplication.java`
- `municipal-services/street-vending/src/main/java/org/upyog/sv/repository/DemandRepository.java`
- `municipal-services/street-vending/src/main/java/org/upyog/sv/repository/IdGenRepository.java`
- `municipal-services/street-vending/src/main/java/org/upyog/sv/repository/ServiceRequestRepository.java`
- `municipal-services/street-vending/src/main/java/org/upyog/sv/repository/StreetVendingRepository.java`
- `municipal-services/street-vending/src/main/java/org/upyog/sv/repository/impl/StreetVendingRepositoryImpl.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | <secret-value> |
| spring.kafka.consumer.group-id | street-vending |
| spring.kafka.producer.key-serializer | <secret-value> |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| kafka.consumer.config.auto_commit | true |
| kafka.consumer.config.auto_commit_interval | 100 |
| kafka.consumer.config.session_timeout | 15000 |
| kafka.consumer.config.auto_offset_reset | earliest |
| kafka.producer.config.retries_config | 0 |
| kafka.producer.config.batch_size_config | 16384 |
| kafka.producer.config.linger_ms_config | 1 |
| kafka.producer.config.buffer_memory_config | 33554432 |
| egov.sms.notification.topic | egov.core.notification.sms |
| kafka.topics.receipt.create | egov.collection.payment-create |
| kafka.topics.consumer | service-consumer-topic |
| persister.create.street-vending.topic | create-street-vending |
| persister.update.street-vending.topic | update-street-vending |
| persister.create.draft.street-vending.topic | create-draft-street-vending |
| persister.update.draft.street-vending.topic | update-draft-street-vending |
| persister.delete.draft.street-vending.topic | delete-draft-street-vending |
| persister.create.payment-schedule.topic | create-payment-schedule |
| persister.update.payment-schedule.topic | update-payment-schedule |
| kafka.topics.notification.sms | egov.core.notification.sms |
| kafka.topics.notification.email | egov.core.notification.email |
| kafka.topics.save.pg.txns | save-pg-txns |
| kafka.topics.update.pg.txns | update-pg-txns |
| egov.usr.events.create.topic | persist-user-events-async |
| municipal-services/street-vending/src/main/resources/script-backup/sv-services-persister.yml topic | create-street-vending |
| municipal-services/street-vending/src/main/resources/script-backup/sv-services-persister.yml topic | update-street-vending |
| municipal-services/street-vending/src/main/resources/script-backup/sv-services-persister.yml topic | create-draft-street-vending |
| municipal-services/street-vending/src/main/resources/script-backup/sv-services-persister.yml topic | update-draft-street-vending |
| municipal-services/street-vending/src/main/resources/script-backup/sv-services-persister.yml topic | delete-draft-street-vending |

### Producers

- `municipal-services/street-vending/src/main/java/org/upyog/sv/kafka/producer/Producer.java`

### Consumers

- `municipal-services/street-vending/src/main/java/org/upyog/sv/kafka/consumer/NotificationConsumer.java`
- `municipal-services/street-vending/src/main/java/org/upyog/sv/kafka/consumer/PaymentUpdateConsumer.java`

### Retry and dead-letter handling

- Standard services rely on Spring Kafka retry/container settings or the platform `tracer` library.
- `egov-persister` has an explicit dead-letter pattern (`egov-persister-deadletter`). Service-specific DLQ topics should be configured in deployment properties if required.

## Redis

- No explicit Redis configuration detected.

Cache strategy, TTLs, and key naming are normally configured in code/properties. When Redis is absent above, the service does not advertise a direct Redis dependency in its checked-in config.

## Workflow

Workflow integration is indicated by workflow packages/classes or egov-workflow-v2 host configuration.

Typical workflow-enabled services use `WorkflowIntegrator` or call `/egov-wf/process/_transition` with tenant, business service, action, assignee, and audit information. States/actions/transitions are owned centrally by `egov-workflow-v2` business service definitions.

## External Integrations

| Config key | Endpoint/host |
| --- | --- |
| spring.flyway.url | jdbc:postgresql://localhost:5432/postgres |
| egov.localization.host | http://localhost:1234/ |
| egov.localization.search.endpoint | _search |
| egov.mdms.host | http://localhost:8094/ |
| egov.mdms.search.endpoint | egov-mdms-service/v1/_search |
| upyog.mdms.v2.host | https://upyog.niua.org/ |
| upyog.mdms.v2.search.endpoint | mdms-v2/v1/_search |
| egov.hrms.host | https://dev.digit.org/ |
| egov.hrms.search.endpoint | egov-hrms/employees/_search |
| egov.user.host | http://localhost:6161/ |
| egov.ui.app.host | https://upyog.niua.org/ |
| egov.idgen.host | http://localhost:8087/ |
| egov.workflow.host | http://localhost:8280/ |
| egov.url.shortner.host | http://localhost:8282/ |
| egov.url.shortner.endpoint | egov-url-shortening/shortener |
| egov.billingservice.host | http://localhost:8077/ |
| egov.taxhead.search.endpoint | billing-service/taxheads/_search |
| egov.taxperiod.search.endpoint | billing-service/taxperiods/_search |
| egov.demand.create.endpoint | billing-service/demand/_create |
| egov.demand.update.endpoint | billing-service/demand/_update |
| egov.demand.search.endpoint | billing-service/demand/_search |
| egov.bill.gen.endpoint | billing-service/bill/v2/_fetchbill |
| egov.enc.host | http://localhost:8083/ |
| egov.enc.encrypt.endpoint | egov-enc-service/crypto/v1/_encrypt |
| egov.enc.decypt.endpoint | egov-enc-service/crypto/v1/_decrypt |
| egov.location.host | http://localhost:8989/ |

```mermaid
sequenceDiagram
    participant S as street_vending
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

- `municipal-services/street-vending/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| server.context-path | /sv-services |
| server.servlet.context-path | /sv-services |
| server.port | 8080 |
| app.timezone | UTC |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/postgres |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| spring.flyway.url | jdbc:postgresql://localhost:5432/postgres |
| spring.flyway.user | postgres |
| spring.flyway.password | <secret-value> |
| spring.flyway.table | public |
| spring.flyway.baseline-on-migrate | true |
| spring.flyway.outOfOrder | true |
| spring.flyway.locations | classpath:/db/migration/main |
| spring.flyway.enabled | true |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| spring.kafka.consumer.group-id | street-vending |
| spring.kafka.producer.key-serializer | org.apache.kafka.common.serialization.StringSerializer |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| kafka.consumer.config.auto_commit | true |
| kafka.consumer.config.auto_commit_interval | 100 |
| kafka.consumer.config.session_timeout | 15000 |
| kafka.consumer.config.auto_offset_reset | earliest |
| kafka.producer.config.retries_config | 0 |
| kafka.producer.config.batch_size_config | 16384 |
| kafka.producer.config.linger_ms_config | 1 |
| kafka.producer.config.buffer_memory_config | 33554432 |
| egov.localization.host | http://localhost:1234/ |
| egov.localization.workDir.path | localization/messages/v1 |
| egov.localization.context.path | localization/messages/v1 |

## Logging

- Platform services use Spring logging plus `tracer` for correlation IDs and structured exception responses.
- Gateway filters are responsible for request correlation; services should propagate correlation/user headers downstream.
- Audit events are emitted to Kafka/audit-service where configured.

## Exception Handling

- Common pattern: validation errors become `CustomException`/domain exceptions and are rendered by `tracer` or service-specific `GlobalExceptionHandler`.
- Controller-level `@Valid` handles Bean Validation for request models where annotations exist.
- Kafka consumers should be monitored for poison messages and retry loops.

## Testing

- Test files detected: **4**.
- Unit tests typically cover validators, services, query builders, and controllers.
- Integration tests require PostgreSQL, Kafka, Redis, and dependent services or mocks.

## Deployment

- `municipal-services/street-vending/src/main/resources/db/Dockerfile`

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
