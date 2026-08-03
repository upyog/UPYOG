# Custom Consumer Service (egov-custom-consumer)

Custom consumer service listens to a Kafka topic and invokes a co-existence API to clear the auth token present in the co-existence finance ERP when a user logs out of the rainmaker system.

## Prerequisites

- Java 17
- Maven 3.6+
- Kafka (running on `localhost:9092` by default)

## Technology Stack

- Java 17
- Spring Boot 3.4.4
- Spring Framework 6.2.6
- Spring Kafka
- Lombok 1.18.38
- Log4j2 2.24.3

## Dependencies

| Dependency | Version |
|---|---|
| spring-boot-starter-web | 3.4.4 (managed) |
| spring-beans | 6.2.6 |
| commons-lang3 | 3.14.0 |
| lombok | 1.18.38 |
| json-path | 2.9.0 |
| org.json | 20240303 |
| jackson-databind | managed |
| tracer | 2.0.0-SNAPSHOT |
| services-common | 1.0.0-RELEASE |

## Service Details

Custom Consumer Service (egov-custom-consumer) is a Kafka consumer that listens to the `res-custom-filter` topic. When a user logout event is detected (sourceUri = `/user/_logout`), it extracts the access token from the message payload and calls the co-existence finance ERP's ClearToken REST endpoint to invalidate the session there as well.

### Flow

```
Rainmaker User Logout
        |
        v
Kafka Topic: res-custom-filter
        |
        v
CustomConsumer.listen()
        |
  sourceUri == /user/_logout?
        |
        v
SignOutService.callFinanceForSignOut()
        |
        v
POST {egov.coexistence.hostname}/services/EGF/rest/ClearToken
```

## Kafka Topics

### Consumers

| Topic | Property Key | Purpose |
|---|---|---|
| `res-custom-filter` | `egov.custom.async.filter.topic` | Listens for user logout events from the API gateway filter. Triggers co-existence finance ERP token clearance when `sourceUri` is `/user/_logout`. |

### Producers

None. This service only consumes messages and does not produce to any Kafka topic.

## Kafka Consumer Configuration

| Property | Value |
|---|---|
| `kafka.config.bootstrap_server_config` | `localhost:9092` |
| `spring.kafka.consumer.group-id` | `egov-api-gateway` |
| `spring.kafka.consumer.key-deserializer` | `StringDeserializer` |
| `spring.kafka.consumer.value-deserializer` | `HashMapDeserializer` |
| `spring.kafka.listener.missing-topics-fatal` | `false` |

## Application Properties

| Property | Description | Example Value |
|---|---|---|
| `kafka.config.bootstrap_server_config` | Kafka broker address | `localhost:9092` |
| `spring.kafka.consumer.group-id` | Kafka consumer group ID | `egov-api-gateway` |
| `egov.custom.async.filter.topic` | Kafka topic to consume from | `res-custom-filter` |
| `egov.coexistence.hostname` | Base URL of the co-existence finance ERP | `https://jalandhar-dev.egovernments.org` |
| `egov.coexistence.singout.uri` | URI path for the ClearToken endpoint | `/services/EGF/rest/ClearToken` |

## Kafka Message Payload

The service expects the following JSON structure in the consumed Kafka message:

```json
{
  "sourceUri": "/user/_logout",
  "request": {
    "access_token": "<user-access-token>",
    "RequestInfo": {
      "userInfo": { ... }
    }
  }
}
```

Key JsonPath expressions used internally:

| Constant | JsonPath | Description |
|---|---|---|
| `signOutUriJsonPath` | `$.sourceUri` | Identifies the type of request |
| `signOutAccessToken` | `$.request.access_token` | Extracts the access token |
| `userInfo` | `$.request.RequestInfo.userInfo` | Removed before forwarding to ERP |
| `requestInfo` | `$.request.RequestInfo` | Used to inject `authToken` |
| `request` | `$.request` | Full request body sent to ERP |

## External API Call

When a logout event is detected, the service makes the following call:

```
POST {egov.coexistence.hostname}/services/EGF/rest/ClearToken
Content-Type: application/json

{
  "RequestInfo": {
    "authToken": "<access_token>"
  },
  ...
}
```

## Building and Running

### Build JAR

Navigate to the project root (where `pom.xml` exists) and run:

```bash
mvn clean package
```

This generates `target/egov-custom-consumer-1.1.1-SNAPSHOT.jar`.

### Run Locally

```bash
java -jar target/egov-custom-consumer-1.1.1-SNAPSHOT.jar
```

Update `application.properties` before running:

```ini
egov.coexistence.hostname=https://<your-coexistence-erp-host>
kafka.config.bootstrap_server_config=<kafka-broker>:9092
```

### Docker

```bash
docker build -t egov-custom-consumer .
docker run -e egov.coexistence.hostname=https://<host> egov-custom-consumer
```

## DB UML Diagram

NA — This service has no database dependency.

## Swagger API Contract

NA — This service exposes no REST APIs. It is a Kafka consumer only.
