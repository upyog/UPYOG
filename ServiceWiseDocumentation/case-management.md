# case-management

> Generated from repository path `utilities/case-management`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Case management utility service.

## Responsibilities

- Own the `case-management` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **1.8**
- Spring Boot version: **2.2.6.RELEASE**
- HTTP port: **8080**
- Servlet/context path: **/**
- Detected controllers/API mappings: **8**
- Detected migrations: **1**
- Detected tests: **1** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| config | 2 source file(s) | Spring beans, properties, and runtime configuration. |
| controller | 4 source file(s) | HTTP endpoints and request/response orchestration. |
| cova | 2 source file(s) | Package area detected from source tree. |
| egov | 1 source file(s) | Package area detected from source tree. |
| idgen | 4 source file(s) | Package area detected from source tree. |
| model | 18 source file(s) | Request, response, DTO, and domain models. |
| producer | 1 source file(s) | Kafka/event producers. |
| repository | 5 source file(s) | Database or remote-service data access. |
| service | 7 source file(s) | Business orchestration and domain logic. |
| sign | 2 source file(s) | Package area detected from source tree. |
| user | 4 source file(s) | Package area detected from source tree. |
| util | 3 source file(s) | Reusable helpers and cross-cutting functions. |

## Folder Structure

- `utilities/case-management`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `utilities/case-management/src/main/java/egov/Main.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /case/v1/_create | CaseApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /case/v1/_search | CaseApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /case/v1/_update | CaseApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /case/v1/getDefaulterCases | CaseApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /cova/v1/_create | CovaApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /cova/v1/healthdetail/_create | CovaApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /employee/v1/_create | EmployeeController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /healthdetail/v1/_create | HealthdetailApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as case_management
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

- **Tables detected from migrations:** eg_cms_case
- **Migration files:** 1
- **Repositories/JDBC classes:** 5
- **Entity/table-mapped classes:** 0

### Migration locations

- `utilities/case-management/src/main/resources/db/migration`
- `utilities/case-management/src/main/resources/db/migration/main`

### Repository locations

- `utilities/case-management/src/main/java/egov/casemanagement/repository/ElasticSearchRepository.java`
- `utilities/case-management/src/main/java/egov/casemanagement/repository/IdGenRepository.java`
- `utilities/case-management/src/main/java/egov/casemanagement/repository/SearchRepository.java`
- `utilities/case-management/src/main/java/egov/casemanagement/repository/ServiceRequestRepository.java`
- `utilities/case-management/src/main/java/egov/casemanagement/repository/SignatureRepository.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | <secret-value> |
| spring.kafka.consumer.group-id | case-management |
| spring.kafka.producer.key-serializer | <secret-value> |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| kafka.consumer.config.auto_commit | true |
| kafka.consumer.config.auto_commit_interval | 100 |
| kafka.consumer.config.session_timeout | 15000 |
| kafka.consumer.config.auto_offset_reset | earliest |
| kafka.producer.config.retries_config | 0 |
| kafka.producer.config.batch_size_config | 16384 |
| kafka.producer.config.linger_ms_config | 1 |
| kafka.producer.config.buffer_memory_config | 33554432 |
| persister.save.case.topic | save-cms-case |
| persister.update.case.topic | update-cms-case |
| es.case.topic | es-cms-case |
| cova.health.record.topic | cms-cova-healthrecord |
| send.email.topic | home.isolation.notification.email |
| send.sms.topic | egov.core.notification.sms |

### Producers

- `utilities/case-management/src/main/java/egov/casemanagement/producer/Producer.java`

### Consumers

- Not present in this repository or not detected.

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
| egov.user.host | http://localhost:8081/ |
| egov.idgen.host | http://localhost:8088/ |
| egov.mdms.host | https://egov-micro-dev.egovernments.org |
| egov.mdms.search.endpoint | /egov-mdms-service/v1/_search |
| egov.enc.service.host | http://localhost:8089 |
| es.host | localhost |
| cova.fetch.url | http://20.44.43.72/api/cova/citizen/services/v1/fetch-health-record |
| cova.create.health.record.url | http://20.44.43.72/api/cova/citizen/services/v1/insert-blo-data |

```mermaid
sequenceDiagram
    participant S as case_management
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

- `utilities/case-management/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| server.servlet.contextPath | /case-management |
| server.port | 8080 |
| app.timezone | UTC |
| egov.root.tenant.id | pb |
| health.detail.schema | classpath:healthDetailsSchema.json |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| spring.kafka.consumer.group-id | case-management |
| spring.kafka.producer.key-serializer | org.apache.kafka.common.serialization.StringSerializer |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| kafka.consumer.config.auto_commit | true |
| kafka.consumer.config.auto_commit_interval | 100 |
| kafka.consumer.config.session_timeout | 15000 |
| kafka.consumer.config.auto_offset_reset | earliest |
| kafka.producer.config.retries_config | 0 |
| kafka.producer.config.batch_size_config | 16384 |
| kafka.producer.config.linger_ms_config | 1 |
| kafka.producer.config.buffer_memory_config | 33554432 |
| spring.main.allow-bean-definition-overriding | true |
| persister.save.case.topic | save-cms-case |
| persister.update.case.topic | update-cms-case |
| es.case.topic | es-cms-case |
| cova.health.record.topic | cms-cova-healthrecord |
| egov.user.host | http://localhost:8081/ |
| egov.user.workDir.path | /user/users |
| egov.user.context.path | /user/users |
| egov.user.create.path | /_createnovalidate |
| egov.user.search.path | /user/v1/_search |
| egov.user.update.path | /_updatenovalidate |
| egov.user.username.prefix | CASE |
| egov.idgen.host | http://localhost:8088/ |
| egov.idgen.path | egov-idgen/id/_generate |
| egov.idgen.case.applicationNum.name | tl.aplnumber |
| egov.idgen.case.applicationNum.format | PB-TL-[cy:yyyy-MM-dd]-[SEQ_EG_TL_APL] |

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

- `utilities/case-management/src/main/resources/db/Dockerfile`

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
