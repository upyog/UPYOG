# chatbot

> Generated from repository path `core-services/chatbot`. This page documents detected runtime configuration and source-code structure. Validate deployment-specific values against the environment/Helm chart used outside this repository.

## Purpose

Chatbot orchestration service.

## Responsibilities

- Own the `chatbot` business or platform capability within the UPYOG ecosystem.
- Expose synchronous APIs when controllers are present and publish/consume asynchronous events when Kafka configuration is present.
- Persist service-owned state through PostgreSQL/Flyway or delegate persistence through `egov-persister` YAML mappings.
- Integrate with common platform services such as gateway, user, MDMS, workflow, ID generation, localization, billing, collection, notification, audit, indexer, and searcher as configured.

## Features

- Stack: **Java/Spring Boot**
- Java version: **1.8**
- Spring Boot version: **2.2.13.RELEASE**
- HTTP port: **8012**
- Servlet/context path: **/chatbot**
- Detected controllers/API mappings: **3**
- Detected migrations: **4**
- Detected tests: **10** files

## Packages

| Package area | Files | Role |
| --- | --- | --- |
| authorization | 3 source file(s) | Package area detected from source tree. |
| chat | 2 source file(s) | Package area detected from source tree. |
| config | 5 source file(s) | Spring beans, properties, and runtime configuration. |
| controller | 4 source file(s) | HTTP endpoints and request/response orchestration. |
| egovchatserdes | 3 source file(s) | Package area detected from source tree. |
| formatter | 3 source file(s) | Package area detected from source tree. |
| graph | 2 source file(s) | Package area detected from source tree. |
| localization | 1 source file(s) | Package area detected from source tree. |
| mapper | 1 source file(s) | DTO/entity conversion. |
| model | 6 source file(s) | Request, response, DTO, and domain models. |
| querybuilder | 1 source file(s) | Package area detected from source tree. |
| repository | 2 source file(s) | Database or remote-service data access. |
| restendpoint | 4 source file(s) | Package area detected from source tree. |
| service | 11 source file(s) | Business orchestration and domain logic. |
| streams | 4 source file(s) | Package area detected from source tree. |
| systeminitiated | 2 source file(s) | Package area detected from source tree. |
| util | 9 source file(s) | Reusable helpers and cross-cutting functions. |
| validation | 2 source file(s) | Package area detected from source tree. |
| valuefetch | 6 source file(s) | Package area detected from source tree. |
| valuefirst | 4 source file(s) | Package area detected from source tree. |

## Folder Structure

- `core-services/chatbot`: service root.
- `src/main/java`: Java source, package areas listed above when present.
- `src/main/resources`: application configuration, Flyway migrations, persister/indexer/searcher YAML, message resources.
- `src/test`: automated tests when present.
- `migration` or `db/migration`: Node/legacy SQL migrations when present.
- Dockerfiles are listed in the Deployment section.

## Entry Points

- `core-services/chatbot/src/main/java/org/egov/chat/ChatBot.java`

## APIs

| Method | Endpoint | Controller | Input | Output | Authentication | Exceptions |
| --- | --- | --- | --- | --- | --- | --- |
| POST | /removetestdata | RemoveTestData.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| POST | /messages | PreChatController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |
| GET | /messages | PreChatController.java | Request body follows service model/Swagger contract; validation is typically Bean Validation plus service validators. | Response follows DIGIT ResponseInfo pattern or service-specific model. | Gateway-authenticated unless listed in gateway open/mixed whitelist or explicitly anonymous. | Controller/service/repository/custom validation exceptions propagate through tracer/global handlers. |

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
    participant S as chatbot
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

- **Tables detected from migrations:** eg_chat_conversation_state, eg_chat_message
- **Migration files:** 4
- **Repositories/JDBC classes:** 3
- **Entity/table-mapped classes:** 0

### Migration locations

- `core-services/chatbot/src/main/resources/db/migration`
- `core-services/chatbot/src/main/resources/db/migration/main`

### Repository locations

- `core-services/chatbot/src/main/java/org/egov/chat/RemoveTestData.java`
- `core-services/chatbot/src/main/java/org/egov/chat/repository/ConversationStateRepository.java`
- `core-services/chatbot/src/main/java/org/egov/chat/repository/MessageRepository.java`

### Entity mapping locations

- Not present in this repository or not detected.

## Kafka

| Kafka/property | Topic or value |
| --- | --- |
| kafka.bootstrap.server | localhost:9092 |
| kafka.consumer.poll.ms | 10 |
| kafka.producer.linger.ms | 5 |
| kafka.topics.partition.count | 3 |
| kafka.topics.replication.factor | 1 |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |

### Producers

- `core-services/chatbot/src/main/java/org/egov/chat/config/KafkaConfig.java`
- `core-services/chatbot/src/main/java/org/egov/chat/repository/ConversationStateRepository.java`
- `core-services/chatbot/src/main/java/org/egov/chat/repository/MessageRepository.java`
- `core-services/chatbot/src/main/java/org/egov/chat/service/InputSegregator.java`
- `core-services/chatbot/src/main/java/org/egov/chat/service/WelcomeMessageHandler.java`
- `core-services/chatbot/src/main/java/org/egov/chat/util/CommonAPIErrorMessage.java`
- `core-services/chatbot/src/main/java/org/egov/chat/util/Telemetry.java`
- `core-services/chatbot/src/main/java/org/egov/chat/pre/service/MessageWebhook.java`
- `core-services/chatbot/src/main/java/org/egov/chat/service/streams/CreateStepStream.java`

### Consumers

- `core-services/chatbot/src/main/java/org/egov/chat/config/KafkaConfig.java`
- `core-services/chatbot/src/main/java/org/egov/chat/controller/ChatController.java`
- `core-services/chatbot/src/main/java/org/egov/chat/post/controller/PostChatController.java`

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
| user.service.chatbot.host | https://dev.digit.org/ |
| egov.external.host | https://dev.digit.org/ |
| elasticsearch.host | http://localhost:9200/ |
| spring.flyway.url | jdbc:postgresql://localhost:5432/chat |

```mermaid
sequenceDiagram
    participant S as chatbot
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

- `core-services/chatbot/src/main/resources/application.properties`

### Key properties

| Property | Value / meaning |
| --- | --- |
| server.servlet.context-path | /chatbot |
| server.port | 8012 |
| app.timezone | UTC |
| kafka.bootstrap.server | localhost:9092 |
| kafka.consumer.poll.ms | 10 |
| kafka.producer.linger.ms | 5 |
| kafka.topics.partition.count | 3 |
| kafka.topics.replication.factor | 1 |
| spring.kafka.listener.missing-topics-fatal | false |
| spring.kafka.consumer.properties.spring.json.use.type.headers | false |
| user.service.chatbot.host | https://dev.digit.org/ |
| user.service.oauth.path | <secret-value> |
| user.service.create.citizen.path | user/citizen/_create |
| user.service.chatbot.citizen.passwrord | 123456 |
| user.login.authorization.header | <secret-value> |
| egov.external.host | https://dev.digit.org/ |
| state.level.tenant.id | pb |
| flow.reset.keywords | mseva |
| contact.card.whatsapp.number | 918744960111 |
| contact.card.whatsapp.name | mSeva Punjab |
| test.data.cleanup.enabled | false |
| elasticsearch.host | http://localhost:9200/ |
| elasticsearch.chatbot.messages.index.name | chatbot-messages |
| spring.datasource.driver-class-name | org.postgresql.Driver |
| spring.datasource.url | jdbc:postgresql://localhost:5432/chat |
| spring.datasource.username | postgres |
| spring.datasource.password | <secret-value> |
| spring.flyway.url | jdbc:postgresql://localhost:5432/chat |
| spring.flyway.user | postgres |
| spring.flyway.password | <secret-value> |
| spring.flyway.table | flyway |
| spring.flyway.baseline-on-migrate | true |
| spring.flyway.outOfOrder | true |
| spring.flyway.locations | classpath:/db/migration/main |
| spring.flyway.enabled | true |

## Logging

- Platform services use Spring logging plus `tracer` for correlation IDs and structured exception responses.
- Gateway filters are responsible for request correlation; services should propagate correlation/user headers downstream.
- Audit events are emitted to Kafka/audit-service where configured.

## Exception Handling

- Common pattern: validation errors become `CustomException`/domain exceptions and are rendered by `tracer` or service-specific `GlobalExceptionHandler`.
- Controller-level `@Valid` handles Bean Validation for request models where annotations exist.
- Kafka consumers should be monitored for poison messages and retry loops.

## Testing

- Test files detected: **10**.
- Unit tests typically cover validators, services, query builders, and controllers.
- Integration tests require PostgreSQL, Kafka, Redis, and dependent services or mocks.

## Deployment

- `core-services/chatbot/src/main/resources/db/Dockerfile`

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
