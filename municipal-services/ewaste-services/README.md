# E-Waste Management Service

## Overview

The E-Waste Management Service is a microservice designed to facilitate the management of e-waste in urban local bodies. It provides functionalities for booking, tracking, and managing e-waste disposal requests.
This service is part of the larger e-Governance framework and integrates with various other services for user management, notifications, and billing.
<<<<<<< HEAD

## Java 17 Migration

This service has been upgraded from Java 8 to Java 17 with Spring Boot 3.2.2.

### Requirements
- **Java 17** or higher
- Maven 3.6+

### Key Changes
- **Spring Boot**: Upgraded to 3.2.2
- **jakarta.* → jakarta.***: Servlet API, Validation API, Annotations
- **SpringFox → SpringDoc**: OpenAPI 2.3.0 for Swagger
- **Dependencies Updated**: Hibernate Validator 8.0.1.Final, JSoup 1.17.2, Lombok 1.18.32
- **Circular Dependency**: Fixed between EwasteService and EwasteRequestValidator

### Build & Run
```bash
# Ensure Java 17
java -version

# Build
mvn clean install

# Run
java -jar target/ewaste-services-2.0.0-SNAPSHOT.jar
```

=======
>>>>>>> master-LTS
---

## Service Dependencies

- egov-user
- egov-localization
- egov-idgen
- egov-workflow-v2
- egov-mdms-service / mdms-v2
- egov-location
- user-otp
- egov-accesscontrol
- egov-enc-service
- egov-url-shortening
- egov-filestore
- egov-otp

---

## API Documentation

- [Swagger API Contract](http://localhost:8080/sv-services/swagger-ui.html#/street-vending-controller)

> 🛠️ **Local Access:**
>
> If you're running the service locally (e.g., via IntelliJ or Spring Boot), you can directly open the Swagger UI using the link above.

> 🚀 **Access via Kubernetes (using `kubectl`):**
>
> To access the API from a Kubernetes pod, use the following port-forward command:
>
> ```bash
> kubectl port-forward ew-services-9c47c6c87 -n egov 8080:8080
> ```
>
> After that, open: [http://localhost:8080/ewaste-services/swagger-ui.html#!/ewaste-controller/ewasteRegistrationSearchUsingPOST](http://localhost:8080/ewaste-services/swagger-ui.html#!/ewaste-controller/ewasteRegistrationSearchUsingPOST)

---

## Kafka Topics

### Producer Topics

| Topic                                  | Purpose                                  |
|----------------------------------------|------------------------------------------|
| save-e-waste-request                   | Save initial request                     |
| update-e-waste-request                 | Update request details                   |

### Notification Topics

| Topic | Type |
|----------------------------|----------------|
| egov.core.notification.sms | SMS |
| egov.core.notification.email | Email |
| persist-user-events-async | User event |


### Consumer Topics

| Topic                          | Consumed By | Purpose         |
|--------------------------------|------------------------|-----------------|
| save-e-waste-requst            | Persister | Create request  |
| update-e-waste-request         | Persister | Update request  |

---

## API Endpoints

### Base Path: `/ewaste-services/ewaste-request`

| Action         | Endpoint | Method | Description                            |
|----------------|----------|--------|----------------------------------------|
| Create Request | `/_create` | POST | Submit a new request application       |
| Update Request | `/_update` | POST | Update existing request                |
| Search Request | `/_search` | POST | Filter requests by multiple parameters |

---

## ID Generation Format

| Key        | Format                                          |
|------------|-------------------------------------------------|
| Request ID | `EW-[CITY.CODE]-[seq_ew_request_id]`            |
---

## Environment Configuration

| Property | Example                                      |
|----------|----------------------------------------------|
| `server.context-path` | `/ew-services`                               |
| `server.port` | `8080`                                       |
| `app.timezone` | `UTC`                                        |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/localhost` |
| `spring.datasource.username` | `postgres`                                   |
| `spring.datasource.password` | `postgres`                                   |
| `spring.flyway.enabled` | `false`                                      |
| `spring.application.name` | `ew-services`                                |

---

## Localization

| Key | Value |
|-----|-------|
| egov.localization.host | http://localhost:9595 |
| egov.localization.context.path | /localization/messages/v1 |
| egov.localization.search.endpoint | /_search |
| egov.localization.statelevel | true |
| egov.localization.fallback.locale | en_IN |

---

## Workflow Integration

| Key | Value |
|-----|-------|
| workflow.host | http://localhost:8280 |
| workflow.transition.path | /egov-workflow-v2/egov-wf/process/_transition |
| workflow.businessservice.search.path | /egov-workflow-v2/egov-wf/businessservice/_search |
| workflow.processinstance.search.path | /egov-workflow-v2/egov-wf/process/_search |


For further information, [click this link](https://docs.google.com/document/d/13n0NbQaufo6zca5Nry9xebDRflERSWqGl93thgvsV7Y/edit?tab=t.0).

---