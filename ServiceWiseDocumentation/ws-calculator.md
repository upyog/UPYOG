# ws-calculator

> Generated from repository path `municipal-services/ws-calculator`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Water charge calculator and meter reading service.

## Responsibilities

- Own the `ws-calculator` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **17**
- Spring Boot version: **3.2.2**
- HTTP port: **8083**
- Servlet/context path: **/ws-calculator**
- Detected controllers/API mappings: **10**
- Detected migrations: **3**
- Detected tests: **0** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| builder | 1 source file(s) | Package area detected from source tree. |
| config | 1 source file(s) | Spring beans, properties, and runtime configuration. |
| constants | 2 source file(s) | Package area detected from source tree. |
| consumer | 3 source file(s) | Kafka/event consumers. |
| controller | 2 source file(s) | HTTP endpoints and request/response orchestration. |
| enums | 7 source file(s) | Package area detected from source tree. |
| idgen | 4 source file(s) | Package area detected from source tree. |
| mapper | 6 source file(s) | DTO/entity conversion. |
| model | 79 source file(s) | Request, response, DTO, and domain models. |
| producer | 1 source file(s) | Kafka/event producers. |
| repository | 5 source file(s) | Database or remote-service data access. |
| service | 12 source file(s) | Business orchestration and domain logic. |
| users | 4 source file(s) | Package area detected from source tree. |
| util | 6 source file(s) | Reusable helpers and cross-cutting functions. |
| validator | 3 source file(s) | Input and domain validation. |
| workflow | 7 source file(s) | Workflow transition/search integration and state handling. |
| wscalculation | 1 source file(s) | Package area detected from source tree. |

## Folder Structure

- `municipal-services/ws-calculator`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/WsCalculationApplication.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /waterCalculator/_estimate | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /waterCalculator/_calculate | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /waterCalculator/_updateDemand | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /waterCalculator/_jobscheduler | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /waterCalculator/_getConnectionForDemand | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /waterCalculator/_generateDemand | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /waterCalculator/_applyAdhocTax | CalculatorController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /meterConnection/_create | MeterReadingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /meterConnection/_bulkReading | MeterReadingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /meterConnection/_search | MeterReadingController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as ws_calculator
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

- **Tables detected from migrations:** eg_ws_bulkbill_audit, eg_ws_meterreading
- **Migration files:** 3
- **Repositories/JDBC classes:** 4
- **Entity/table-mapped classes:** 0

### Migration locations

- `municipal-services/ws-calculator/src/main/resources/db/migration`
- `municipal-services/ws-calculator/src/main/resources/db/migration/ddl`

### Repository locations

- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/repository/DemandRepository.java`
- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/repository/Repository.java`
- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/repository/ServiceRequestRepository.java`
- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/repository/WSCalculationDaoImpl.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | <secret-value> |
| spring.kafka.consumer.group-id | egov-ws-calc-services |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| spring.kafka.producer.key-serializer | <secret-value> |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| egov.watercalculatorservice.createdemand.topic | ws-generate-demand |
| ws.calculator.demand.successful.topic | ws-demand-saved |
| kafka.topics.notification.sms | egov.core.notification.sms |
| kafka.topics.notification.mail.name | egov.core.notification.email |
| kafka.topics.billgen.topic | bill-generation |
| egov.usr.events.create.topic | persist-user-events-async |
| persister.demand.based.dead.letter.topic.batch | ws-dead-letter-topic-batch |
| persister.demand.based.dead.letter.topic.single | ws-dead-letter-topic-single |
| kafka.topics.bulk.bill.generation | bulk-bill-generator |
| kafka.topics.bulk.bill.generation.audit | bulk-bill-generator-audit-ws |
| spring.kafka.listener.missing-topics-fatal | false |

### Producers

- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/producer/WSCalculationProducer.java`
- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/service/BulkDemandAndBillGenService.java`

### Consumers

- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/consumer/BillingNotificationConsumer.java`
- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/consumer/DemandGenerationConsumer.java`
- `municipal-services/ws-calculator/src/main/java/org/egov/wscalculation/consumer/DemandNotificationConsumer.java`

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
| egov.mdms.host | http://localhost:8088/ |
| egov.mdms.search.endpoint | egov-mdms-service/v1/_search |
| egov.billingservice.host | http://billing-service.egov:8080/ |
| egov.taxhead.search.endpoint | billing-service/taxheads/_search |
| egov.taxperiod.search.endpoint | billing-service/taxperiods/_search |
| egov.demand.create.endpoint | billing-service/demand/_create |
| egov.demand.update.endpoint | billing-service/demand/_update |
| egov.demand.search.endpoint | billing-service/demand/_search |
| egov.bill.gen.endpoint | billing-service/bill/_generate |
| egov.bill.fetch.endpoint | billing-service/bill/v2/_fetchbill |
| egov.bill.search.endpoint | billing-service/bill/v2/_search |
| spring.flyway.url | jdbc:postgresql://localhost:5432/rainmaker_new |
| egov.ws.host | http://localhost:8090/ |
| egov.wc.search.endpoint | ws-services/wc/_search |
| egov.localization.host | https://dev.digit.org/ |
| egov.localization.search.endpoint | _search |
| egov.user.host | http://localhost:8081/ |
| egov.ui.app.host | https://dev.digit.org/ |
| notification.url | https://dev.digit.org/ |
| egov.property.service.host | https://dev.digit.org/ |
| egov.property.searchendpoint | property-services/property/_search |
| egov.shortener.url | egov-url-shortening/shortener |

```mermaid
sequenceDiagram
    participant S as ws_calculator
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

- `municipal-services/ws-calculator/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| server.port | 8083 |
| server.context.path | /ws-calculator |
| server.servlet.context-path | /ws-calculator |
| app.timezone | UTC |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/rainmaker_new |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| egov.meterservice.createmeterconnection | save-ws-meter |
| egov.mdms.host | http://localhost:8088/ |
| egov.mdms.search.endpoint | egov-mdms-service/v1/_search |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| spring.kafka.consumer.group-id | egov-ws-calc-services |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| spring.kafka.producer.key-serializer | org.apache.kafka.common.serialization.StringSerializer |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| egov.watercalculatorservice.createdemand.topic | ws-generate-demand |
| ws.calculator.demand.successful.topic | ws-demand-saved |
| ws.calculator.demand.failed | ws-demand-failure |
| egov.ws.search.meterReading.pagination.default.limit | 50 |
| egov.ws_calculation.meterReading.default.offset | 0 |
| egov.billingservice.host | http://billing-service.egov:8080/ |
| egov.taxhead.search.endpoint | billing-service/taxheads/_search |
| egov.taxperiod.search.endpoint | billing-service/taxperiods/_search |
| egov.demand.create.endpoint | billing-service/demand/_create |
| egov.demand.update.endpoint | billing-service/demand/_update |
| egov.demand.search.endpoint | billing-service/demand/_search |
| egov.bill.gen.endpoint | billing-service/bill/_generate |
| egov.bill.fetch.endpoint | billing-service/bill/v2/_fetchbill |
| egov.bill.search.endpoint | billing-service/bill/v2/_search |
| ws.module.code | ws-services-calculation |
| ws.module.minpayable.amount | 1 |
| ws.financialyear.start.month | 04 |

## Logging

- Platform services use Spring logging plus `tracer` for correlation IDs and structured exception responses.
- Gateway filters are responsible for request correlation; services should propagate correlation/user headers downstream.
- Audit events are emitted to Kafka/audit-service where configured.

## Exception Handling

- Common pattern: validation errors become `CustomException`/domain exceptions and are rendered by `tracer` or service-specific `GlobalExceptionHandler`.
- Controller-level `@Valid` handles Bean Validation for request models where annotations exist.
- Kafka consumers should be monitored for poison messages and retry loops.

## Testing

- Test files detected: **0**.
- Unit tests typically cover validators, services, query builders, and controllers.
- Integration tests require PostgreSQL, Kafka, Redis, and dependent services or mocks.

## Deployment

- `municipal-services/ws-calculator/src/main/resources/db/Dockerfile`

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
