# egov-common-masters

> Generated from repository path `core-services/egov-common-masters`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Shared common master data service.

## Responsibilities

- Own the `egov-common-masters` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **1.8**
- Spring Boot version: **service-specific**
- HTTP port: **8889**
- Servlet/context path: **/egov-common-masters**
- Detected controllers/API mappings: **31**
- Detected migrations: **48**
- Detected tests: **37** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| builder | 13 source file(s) | Package area detected from source tree. |
| commons | 1 source file(s) | Package area detected from source tree. |
| config | 1 source file(s) | Spring beans, properties, and runtime configuration. |
| consumer | 5 source file(s) | Kafka/event consumers. |
| contract | 50 source file(s) | Package area detected from source tree. |
| controller | 14 source file(s) | HTTP endpoints and request/response orchestration. |
| enums | 5 source file(s) | Package area detected from source tree. |
| errorhandlers | 4 source file(s) | Package area detected from source tree. |
| factory | 2 source file(s) | Package area detected from source tree. |
| interceptor | 1 source file(s) | Package area detected from source tree. |
| mapper | 16 source file(s) | DTO/entity conversion. |
| model | 25 source file(s) | Request, response, DTO, and domain models. |
| repository | 14 source file(s) | Database or remote-service data access. |
| service | 13 source file(s) | Business orchestration and domain logic. |
| util | 2 source file(s) | Reusable helpers and cross-cutting functions. |
| validator | 1 source file(s) | Input and domain validation. |

## Folder Structure

- `core-services/egov-common-masters`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `core-services/egov-common-masters/src/main/java/org/egov/commons/EgovCommonMastersApplication.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /businessCategory/_create | BusinessCategoryController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /businessCategory/_update | BusinessCategoryController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /businessCategory/_search | BusinessCategoryController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /businessDetails/_create | BusinessDetailsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /businessDetails/_update | BusinessDetailsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /businessDetails/_search | BusinessDetailsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| ANY | /businessDetails/_getBusinessTypes | BusinessDetailsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /calendaryears/_search | CalendarYearController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /calendaryears/_searchfutureyears | CalendarYearController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /calendaryears/_create | CalendarYearController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /calendaryears/{calendarYearId}/_update | CalendarYearController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /categories/_search | CategoryController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /communities/_search | CommunityController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /departments/_search | DepartmentController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /departments/v1/_search | DepartmentController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /departments/v1/_create | DepartmentController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /departments/v1/_update | DepartmentController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /bloodgroups/_search | EnumsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /maritalstatuses/_search | EnumsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /relationships/_search | EnumsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /genders/_search | EnumsController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /holidays/_create | HolidayController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /holidays/{holidayId}/_update | HolidayController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /holidays/_search | HolidayController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /holidays/_searchprefixsuffix | HolidayController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /holidaytypes/_search | HolidayTypeController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /languages/_search | LanguageController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /modules/_search | ModuleController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /religions/_search | ReligionController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /uomcategories/_search | UOMCategoryController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /uoms/_search | UOMController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as egov_common_masters
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

- **Tables detected from migrations:** eg_business_accountdetails, eg_business_subledgerinfo, eg_businessdetails, eg_calendarYear, eg_category, eg_community, eg_department, eg_holiday, eg_holidaytype, eg_language, eg_module, eg_religion, eg_servicecategory, eg_uom, eg_uomCategory
- **Migration files:** 48
- **Repositories/JDBC classes:** 15
- **Entity/table-mapped classes:** 0

### Migration locations

- `core-services/egov-common-masters/src/main/resources/db/migration`
- `core-services/egov-common-masters/src/main/resources/db/migration/dev`
- `core-services/egov-common-masters/src/main/resources/db/migration/main`
- `core-services/egov-common-masters/src/main/resources/db/migration/qa`
- `core-services/egov-common-masters/src/main/resources/db/migration/seed`

### Repository locations

- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/BusinessCategoryRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/BusinessDetailsRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/CalendarYearRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/CategoryRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/CommunityRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/DepartmentRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/ElasticSearchRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/HolidayRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/HolidayTypeRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/LanguageRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/ModuleRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/ReligionRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/UOMCategoryRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/repository/UOMRepository.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/service/ModuleService.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| spring.kafka.bootstrap.servers | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | <secret-value> |
| spring.kafka.consumer.group-id | common-masters-group |
| spring.kafka.producer.key-serializer | <secret-value> |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| kafka.topics.holiday.name | egov-common-holiday |
| kafka.topics.holiday.id | holiday-save |
| kafka.topics.holiday.group | holiday-group1 |
| kafka.topics.calendaryear.create.name | egov-common-calendaryear-create |
| kafka.topics.calendaryear.create.key | <secret-value> |
| kafka.topics.calendaryear.update.name | egov-common-calendaryear-update |
| kafka.topics.calendaryear.update.key | <secret-value> |

### Producers

- `core-services/egov-common-masters/src/main/java/org/egov/commons/service/BusinessCategoryService.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/service/BusinessDetailsService.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/service/CalendarYearService.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/service/DepartmentService.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/service/HolidayService.java`

### Consumers

- `core-services/egov-common-masters/src/main/java/org/egov/commons/consumers/BusinessCategoryConsumer.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/consumers/BusinessDetailsConsumer.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/consumers/CalendarYearConsumer.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/consumers/DepartmentConsumer.java`
- `core-services/egov-common-masters/src/main/java/org/egov/commons/consumers/HolidayConsumers.java`

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
| flyway.url | jdbc:postgresql://localhost:5432/egov-common-masters_db |
| egov.services.esindexer.host | http://localhost:9200/ |
| egov.mdms.host | http://egov-mdms-service:8080/ |
| egov.mdms.search.endpoint | egov-mdms-service/v1/_search |

```mermaid
sequenceDiagram
    participant S as egov_common_masters
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

- `core-services/egov-common-masters/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/egov-common-masters_db |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| flyway.url | jdbc:postgresql://localhost:5432/egov-common-masters_db |
| flyway.user | postgres |
| flyway.password | <secret-value> |
| flyway.table | egov_common_masters_schema_version |
| flyway.baseline-on-migrate | false |
| flyway.outOfOrder | true |
| flyway.locations | db/migration/main,db/migration/seed |
| flyway.enabled | false |
| server.contextPath | /egov-common-masters |
| server.port | 8889 |
| app.timezone | UTC |
| egov.services.esindexer.host | http://localhost:9200/ |
| spring.kafka.bootstrap.servers | localhost:9092 |
| spring.kafka.consumer.value-deserializer | org.egov.tracer.kafka.deserializer.HashMapDeserializer |
| spring.kafka.consumer.key-deserializer | org.apache.kafka.common.serialization.StringDeserializer |
| spring.kafka.consumer.group-id | common-masters-group |
| spring.kafka.producer.key-serializer | org.apache.kafka.common.serialization.StringSerializer |
| spring.kafka.producer.value-serializer | org.springframework.kafka.support.serializer.JsonSerializer |
| egov.mdms.host | http://egov-mdms-service:8080/ |
| egov.mdms.search.endpoint | egov-mdms-service/v1/_search |
| kafka.topics.holiday.name | egov-common-holiday |
| kafka.topics.holiday.id | holiday-save |
| kafka.topics.holiday.group | holiday-group1 |
| kafka.topics.calendaryear.create.name | egov-common-calendaryear-create |
| kafka.topics.calendaryear.create.key | calendaryear-create |
| kafka.topics.calendaryear.update.name | egov-common-calendaryear-update |
| kafka.topics.calendaryear.update.key | calendaryear-update |
| logging.pattern.console | %clr(%X{CORRELATION_ID:-}) %clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} %clr(${LOG_LEVEL_PATTERN:-%5p}) %clr(${PID:- }){m... |

## Logging

- Platform services use Spring logging plus `tracer` for correlation IDs and structured exception responses.
- Gateway filters are responsible for request correlation; services should propagate correlation/user headers downstream.
- Audit events are emitted to Kafka/audit-service where configured.

## Exception Handling

- Common pattern: validation errors become `CustomException`/domain exceptions and are rendered by `tracer` or service-specific `GlobalExceptionHandler`.
- Controller-level `@Valid` handles Bean Validation for request models where annotations exist.
- Kafka consumers should be monitored for poison messages and retry loops.

## Testing

- Test files detected: **37**.
- Unit tests typically cover validators, services, query builders, and controllers.
- Integration tests require PostgreSQL, Kafka, Redis, and dependent services or mocks.

## Deployment

- `core-services/egov-common-masters/src/main/resources/db/Dockerfile`

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
