# Requester Services DX

## Overview

The `requester-services-dx` is a Spring Boot application designed to act as an integration layer and API service for Data Exchange (DX) and external services. It provides core functionalities for integrating with DigiLocker, handling DigiPin generation, and managing eSign (eMudhra) document signing capabilities within the UPYOG platform.

## Key Features

- **DigiLocker Integration**: Handles user authorization, token generation, user details retrieval, and fetching issued documents directly from DigiLocker APIs.
- **DigiPin Generation**: Provides endpoints to generate DigiPins.
- **eSign (eMudhra) Integration**: Facilitates electronic document signing via eMudhra, including redirect and callback handling for successful signatures.
- **Data Exchange & Encryption**: Integrates with `egov-enc-service` for cryptographic operations (encryption/decryption).
- **ID Generation**: Integrates with `egov-idgen` service to generate transactional IDs for eSign requests.

## Tech Stack

- **Java**: 17
- **Framework**: Spring Boot 3.2.2
- **Database**: PostgreSQL
- **Migrations**: Flyway
- **Messaging**: Apache Kafka (Spring Kafka)
- **Tracing**: eGov Tracer, OpenTelemetry
- **Documentation**: SpringDoc OpenAPI (Swagger)

## Project Structure

- `org.egov.dx.web.controller`: Contains the REST API endpoints.
- `org.egov.dx.service`: Contains business logic for external integrations (DigiLocker, eSign, IDGen).
- `org.egov.dx.web.models`: Data Transfer Objects (DTOs) and request/response models.

## API Endpoints

### DigiLocker Endpoints (`/digilocker`)
- `POST /authorization/url`: Generates authorization URL for DigiLocker OAuth flow.
- `POST /authorization/url/citizen`: Generates authorization URL specifically for citizens.
- `POST /token`: Exchanges authorization code for an access token.
- `POST /token/citizen`: Exchanges authorization code for an access token for citizens.
- `POST /details`: Fetches user details from DigiLocker.
- `POST /issuedfiles`: Retrieves a list of files issued to the user.
- `POST /file`: Downloads a specific issued file as a PDF.

### DigiPin Endpoints (`/digipin`)
- `POST /v1/_generate`: Generates a new DigiPin.

### eSign Endpoints (eMudhra)
*(Note: Some endpoints may be currently under LTS migration)*
- Handled primarily by `eMudraController`, providing document signing and redirect callbacks (`/eSign/redirect`).

## Configuration

The service configuration is managed in `src/main/resources/application.properties`. 

### Key Dependencies to Run:
1. **PostgreSQL Database**:
   - `spring.datasource.url`: Database connection URL.
   - `spring.flyway.enabled`: Set to `true` to run migrations on startup.
2. **Kafka**:
   - `kafka.config.bootstrap_server_config`: Typically `localhost:9092`.
   - Topics: `save-tl-esign-txns`, `update-tl-esign-txns`.
3. **External Services**:
   - `egov-enc-service` (Encryption)
   - `egov-user` (User authentication)
   - `egov-idgen` (ID Generation)
   - `api.digitallocker.gov.in` (DigiLocker API)

## Build and Run

### Prerequisites
- Java 17
- Maven 3.x
- Running instances of PostgreSQL and Kafka.

### Build the Project
To compile and package the application:
```bash
mvn clean install -DskipTests
```

### Run the Application
You can run the application directly using Maven:
```bash
mvn spring-boot:run
```
Or run the built JAR:
```bash
java -jar target/requester-services-dx-1.1.6-SNAPSHOT.jar
```

The application starts by default on port `8280` with the context path `/requester-services-dx`.
