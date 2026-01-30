# egf-instrument service

egf-instrument service used to create, search, update and delete instruments.

## Version
2.0.0

## Technology Stack
- **Java**: 17
- **Spring Boot**: 3.2.12
- **Database**: PostgreSQL 42.7.1
- **Search Engine**: Elasticsearch 6.2.4
- **Message Queue**: Apache Kafka
- **Build Tool**: Maven
- **Validation**: Jakarta Validation API 3.1.1
- **Documentation**: Swagger/OpenAPI 3.0

## Service Dependencies
- **egf-master** (2.0.0) - Finance master data service
- **tracer** (2.9.0-SNAPSHOT) - Distributed tracing service
- **services-common** (1.0.1-SNAPSHOT) - Common utilities and contracts
- PostgreSQL Database
- Elasticsearch Cluster
- Apache Kafka

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Elasticsearch 6.2.4
- Apache Kafka 2.8+

### API Documentation

- **Swagger UI**: http://localhost:8480/egf-instrument/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8480/egf-instrument/v3/api-docs
- Access the interactive API documentation at: `/swagger-ui.html` (when service is running)

### DB UML Diagram

NA

### Swagger API Contract

NA

## Service Details

egf-instrument service provides comprehensive instrument management functionality including creation, search, update and deletion of financial instruments. It supports various instrument types like cheques, demand drafts, and other financial instruments used in government financial operations.

### API Details

**Instrument Management APIs:**
- `POST /instruments/_create` : API to create new instrument
- `POST /instruments/_update` : API to update existing instrument
- `POST /instruments/_delete` : API to delete instrument
- `POST /instruments/_search` : API to search instruments with various filters

**Instrument Type Management APIs:**
- `POST /instrumenttypes/_create` : API to create instrument type
- `POST /instrumenttypes/_update` : API to update instrument type
- `POST /instrumenttypes/_search` : API to search instrument types

**Additional APIs:**
- `POST /surrenderreasons/_create` : API to create surrender reason
- `POST /surrenderreasons/_search` : API to search surrender reasons
- `POST /dishonorreasons/_create` : API to create dishonor reason
- `POST /dishonorreasons/_search` : API to search dishonor reasons

### Kafka Consumers

- **kafka.topics.egf.instrument.validated.topic**: `egov.egf.instrument.validated.topic`
  - Kafka Consumer listens to this topic and create/update/delete instrument and publish an event to kafka

### Kafka Producers

- **kafka.topics.egf.instrument.completed.topic**: `egov.egf.instrument.completed`
  - Kafka Producer publishes an event to this topic with the instrument information after successful processing

### Configuration

Key configuration properties:
- `server.port`: Service port (default: 8480)
- `spring.datasource.url`: PostgreSQL database connection URL
- `spring.kafka.bootstrap-servers`: Kafka broker addresses
- `elasticsearch.host`: Elasticsearch cluster host

### Error Handling

The service implements comprehensive error handling with proper HTTP status codes and error messages for various scenarios including validation errors, data not found, and system errors.
