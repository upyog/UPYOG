# pgr-ai-services

> Generated from repository path `municipal-services/pgr-ai-services`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

AI-assisted grievance classification/search service.

## Responsibilities

- Own the `pgr-ai-services` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **17**
- Spring Boot version: **3.2.2**
- HTTP port: **8080**
- Servlet/context path: **/pgr-ai-services**
- Detected controllers/API mappings: **10**
- Detected migrations: **1**
- Detected tests: **2** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| annotation | 2 source file(s) | Package area detected from source tree. |
| config | 3 source file(s) | Spring beans, properties, and runtime configuration. |
| consumer | 3 source file(s) | Kafka/event consumers. |
| controller | 3 source file(s) | HTTP endpoints and request/response orchestration. |
| grievanceclient | 3 source file(s) | Package area detected from source tree. |
| idgen | 4 source file(s) | Package area detected from source tree. |
| mapper | 2 source file(s) | DTO/entity conversion. |
| model | 18 source file(s) | Request, response, DTO, and domain models. |
| notification | 8 source file(s) | Package area detected from source tree. |
| pgrai | 1 source file(s) | Package area detected from source tree. |
| pgrv1 | 14 source file(s) | Package area detected from source tree. |
| producer | 1 source file(s) | Kafka/event producers. |
| repository | 3 source file(s) | Database or remote-service data access. |
| service | 7 source file(s) | Business orchestration and domain logic. |
| user | 3 source file(s) | Package area detected from source tree. |
| util | 8 source file(s) | Reusable helpers and cross-cutting functions. |
| validation | 2 source file(s) | Package area detected from source tree. |
| validator | 1 source file(s) | Input and domain validation. |
| workflow | 7 source file(s) | Workflow transition/search integration and state handling. |

## Folder Structure

- `municipal-services/pgr-ai-services`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/PGRAiApp.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /migration/_transform | MigrationController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /mock/requests/_create | MockController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /mock/requests/_search | MockController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /mock/requests/_update | MockController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /mock/requests/_test | MockController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /v1/request/_create | RequestsApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /v1/request/_search | RequestsApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /v1/request/_plainsearch | RequestsApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /v1/request/_update | RequestsApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /v1/request/_count | RequestsApiController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as pgr_ai_services
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

- **Tables detected from migrations:** ug_pgr_address, ug_pgr_service
- **Migration files:** 1
- **Repositories/JDBC classes:** 5
- **Entity/table-mapped classes:** 0

### Migration locations

- `municipal-services/pgr-ai-services/src/main/resources/db/migration`
- `municipal-services/pgr-ai-services/src/main/resources/db/migration/main`

### Repository locations

- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/repository/IdGenRepository.java`
- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/repository/PGRRepository.java`
- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/repository/ServiceRequestRepository.java`
- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/repository/rowmapper/PGRQueryBuilder.java`
- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/repository/rowmapper/PGRRowMapper.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | <secret-value> |
| spring.kafka.consumer.group-id | egov-pgr-ai-services |
| spring.kafka.producer.key-serializer | <secret-value> |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| kafka.consumer.config.auto_commit | true |
| kafka.consumer.config.auto_commit_interval | 100 |
| kafka.consumer.config.session_timeout | 15000 |
| kafka.consumer.config.auto_offset_reset | earliest |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| kafka.producer.config.retries_config | 0 |
| kafka.producer.config.batch_size_config | 16384 |
| kafka.producer.config.linger_ms_config | 1 |
| kafka.producer.config.buffer_memory_config | 33554432 |
| pgr.kafka.create.topic | save-pgr-ai-request |
| pgr.kafka.update.topic | update-pgr-ai-request |
| egov.usr.events.create.topic | persist-user-events-async |
| kafka.topics.notification.sms | egov.core.notification.sms |
| persister.save.transition.wf.topic | save-wf-transitions |
| pgr.kafka.migration.topic | pgr-migration |
| pgr.kafka.migration.persister.topic | save-pgr-request-batch |
| persister.save.transition.wf.migration.topic | save-wf-transitions-batch |
| upyog.grievance.es.consumer.create.topic | save-es-grievance |
| upyog.grievance.es.consumer.update.topic | update-es-grievance |
| grievance.consumer.enabled | true |
| pgr.kafka.notification.topic.pattern | ((^[a-zA-Z]+-)?save-pgr-ai-request\|(^[a-zA-Z]+-)?update-ai-pgr-request) |
| municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml topic | save-pgr-ai-request |
| municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml topic | update-pgr-ai-request |
| municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml topic | pgr-auto-escalation |
| municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml topic | save-pgr-ai-request |
| municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml topic | update-pgr-ai-request |
| municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml topic | pgr-auto-escalation |

### Producers

- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/producer/Producer.java`

### Consumers

- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/consumer/GrievanceConsumer.java`
- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/consumer/MigrationConsumer.java`
- `municipal-services/pgr-ai-services/src/main/java/org/upyog/pgrai/consumer/NotificationConsumer.java`

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
| flyway.url | jdbc:postgresql://localhost:5432/pgr_db |
| egov.localization.host | https://dev.digit.org/ |
| egov.localization.search.endpoint | _search |
| mdms.v2.host | http://localhost:8094/ |
| mdms.v2.search.endpoint | mdms-v2/v1/_search |
| egov.hrms.host | https://dev.digit.org/ |
| egov.hrms.search.endpoint | egov-hrms/employees/_search |
| egov.user.host | http://localhost:8281/ |
| egov.idgen.host | http://localhost:8083/ |
| egov.workflow.host | http://localhost:8282/ |
| egov.ui.app.host.map | {"in":"https://central-instance.digit.org/","in.statea":"https://statea.digit.org/"} |
| egov.url.shortner.host | https://dev.digit.org/ |
| egov.url.shortner.endpoint | egov-url-shortening/shortener |
| grievance.api.url | https://samar.iitk.ac.in/iitk_upyog_api/ |
| management.endpoints.web.base-path | / |

```mermaid
sequenceDiagram
    participant S as pgr_ai_services
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

- `municipal-services/pgr-ai-services/src/main/resources/application.properties`
- `municipal-services/pgr-ai-services/src/main/resources/pgr-ai-services-persister.yml`

### Key properties

| Property | Value / meaning |
| --- | --- |
| server.contextPath | /pgr-ai-services |
| server.context-path | /pgr-ai-services |
| server.servlet.context-path | /pgr-ai-services |
| server.port | 8080 |
| app.timezone | UTC |
| kafka.config.bootstrap_server_config | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| spring.kafka.consumer.group-id | egov-pgr-ai-services |
| spring.kafka.producer.key-serializer | org.apache.kafka.common.serialization.StringSerializer |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| kafka.consumer.config.auto_commit | true |
| kafka.consumer.config.auto_commit_interval | 100 |
| kafka.consumer.config.session_timeout | 15000 |
| kafka.consumer.config.auto_offset_reset | earliest |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| kafka.producer.config.retries_config | 0 |
| kafka.producer.config.batch_size_config | 16384 |
| kafka.producer.config.linger_ms_config | 1 |
| kafka.producer.config.buffer_memory_config | 33554432 |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/pgr_db |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| flyway.url | jdbc:postgresql://localhost:5432/pgr_db |
| flyway.user | postgres |
| flyway.password | <secret-value> |
| flyway.table | public |
| flyway.baseline-on-migrate | true |
| flyway.outOfOrder | true |
| flyway.locations | classpath:/db/migration/main |
| flyway.enabled | true |
| egov.localization.host | https://dev.digit.org/ |
| egov.localization.workDir.path | localization/messages/v1 |

## Logging

- Platform services use Spring logging plus `tracer` for correlation IDs and structured exception responses.
- Gateway filters are responsible for request correlation; services should propagate correlation/user headers downstream.
- Audit events are emitted to Kafka/audit-service where configured.

## Exception Handling

- Common pattern: validation errors become `CustomException`/domain exceptions and are rendered by `tracer` or service-specific `GlobalExceptionHandler`.
- Controller-level `@Valid` handles Bean Validation for request models where annotations exist.
- Kafka consumers should be monitored for poison messages and retry loops.

## Testing

- Test files detected: **2**.
- Unit tests typically cover validators, services, query builders, and controllers.
- Integration tests require PostgreSQL, Kafka, Redis, and dependent services or mocks.

## Deployment

- `municipal-services/pgr-ai-services/src/main/resources/db/Dockerfile`

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
