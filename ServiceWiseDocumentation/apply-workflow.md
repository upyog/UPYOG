# apply-workflow

> Generated from repository path `utilities/apply-workflow`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Bulk workflow application utility.

## Responsibilities

- Own the `apply-workflow` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **1.8**
- Spring Boot version: **2.2.6.RELEASE**
- HTTP port: **8095**
- Servlet/context path: **/apply-workflow**
- Detected controllers/API mappings: **1**
- Detected migrations: **1**
- Detected tests: **0** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| config | 1 source file(s) | Spring beans, properties, and runtime configuration. |
| controller | 1 source file(s) | HTTP endpoints and request/response orchestration. |
| model | 7 source file(s) | Request, response, DTO, and domain models. |
| repository | 2 source file(s) | Database or remote-service data access. |
| service | 2 source file(s) | Business orchestration and domain logic. |
| util | 5 source file(s) | Reusable helpers and cross-cutting functions. |
| workflow | 7 source file(s) | Workflow transition/search integration and state handling. |

## Folder Structure

- `utilities/apply-workflow`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `utilities/apply-workflow/src/main/java/org/egov/applyworkflow/ApplyWorkflowApplication.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /api/v1/_process | WorkflowController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as apply_workflow
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

- **Tables detected from migrations:** None detected; persistence may be managed by another service, YAML, or legacy schema.
- **Migration files:** 1
- **Repositories/JDBC classes:** 2
- **Entity/table-mapped classes:** 1

### Migration locations

- `utilities/apply-workflow/src/main/resources/db/migration`
- `utilities/apply-workflow/src/main/resources/db/migration/main`

### Repository locations

- `utilities/apply-workflow/src/main/java/org/egov/applyworkflow/repository/EgMdmsDataRepository.java`
- `utilities/apply-workflow/src/main/java/org/egov/applyworkflow/repository/ServiceRequestRepository.java`

### Entity mapping locations

- `utilities/apply-workflow/src/main/java/org/egov/applyworkflow/web/model/EgMdmsData.java`

## Kafka

- No explicit Kafka topics/properties detected.

### Producers

- Not present in this repository or not detected.

### Consumers

- Not present in this repository or not detected.

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
| spring.flyway.url | jdbc:postgresql://localhost:5432/my-upyog |
| egov.mdms.host | https://upyog.niua.org |
| egov.mdms.search.endpoint | /mdms-v2/v1/_search |
| egov.idgen.host | https://upyog.niua.org/ |

```mermaid
sequenceDiagram
    participant S as apply_workflow
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

- `utilities/apply-workflow/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| server.contextPath | /apply-workflow |
| server.servlet.context-path | /apply-workflow |
| server.port | 8095 |
| app.timezone | UTC |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/my-upyog |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| spring.jpa.properties.hibernate.dialect | org.hibernate.dialect.PostgreSQLDialect |
| spring.jpa.show-sql | true |
| spring.jpa.hibernate.ddl-auto | none |
| spring.flyway.url | jdbc:postgresql://localhost:5432/my-upyog |
| spring.flyway.user | postgres |
| spring.flyway.password | <secret-value> |
| spring.flyway.table | flyway-asset-public |
| spring.flyway.baseline-on-migrate | true |
| spring.flyway.outOfOrder | true |
| spring.flyway.locations | classpath:/db/migration/main |
| spring.flyway.enabled | false |
| egov.mdms.host | https://upyog.niua.org |
| egov.mdms.search.endpoint | /mdms-v2/v1/_search |
| egov.idgen.host | https://upyog.niua.org/ |
| egov.idgen.path | egov-idgen/id/_generate |
| egov.idgen.asset.applicationNum.name | asset.applicationNo |
| egov.idgen.asset.applicationNum.format | PG-[CITY.CODE]-[cy:yyyy]-[SEQ_EG_WFA_WFAID] |
| workflow.context.path | http://localhost:8280 |
| workflow.create.path | /egov-workflow-v2/egov-wf/businessservice/_create |
| workflow.update.path | /egov-workflow-v2/egov-wf/businessservice/_update |
| workflow.search.path | /egov-workflow-v2/egov-wf/businessservice/_search |
| workflow.transition.path | /egov-workflow-v2/egov-wf/process/_transition |

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

- `utilities/apply-workflow/src/main/resources/db/Dockerfile`

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
