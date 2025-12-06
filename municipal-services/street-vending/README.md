# Street Vending (street-vending)

## Overview
The **Street Vending (SV)** module enables urban local bodies (ULBs) to manage and regulate street vendors as per the Street Vendors Act, 2014. It handles the full lifecycle from vendor registration to issuing certificates, renewals, and expiries using workflow-based processes.

<<<<<<< HEAD
**Version:** 2.0.0
**Java Version:** 17
**Spring Boot Version:** 3.2.2
**Package:** `org.upyog.sv`

---

## Technical Stack

| Technology | Version |
|------------|---------|
| Java | 17 |
| Spring Boot | 3.2.2 |
| PostgreSQL | 42.7.1 |
| SpringDoc OpenAPI | 2.3.0 |
| ShedLock | 4.44.0 |
| OWASP HTML Sanitizer | 20240325.1 |


## Service Dependencies

- egov-user
- egov-mdms
- egov-mdms-v2
- egov-idgen
- egov-workflow-v2
- egov-filestore
- egov-localization
- egov-url-shortening
- egov-enc-service
- egov-demand
=======
---

## DB UML Diagram
- **Not Available**

---

## Service Dependencies

- egov-user  
- egov-mdms  
- egov-mdms-v2
- egov-idgen  
- egov-workflow-v2  
- egov-filestore  
- egov-localization  
- egov-url-shortening  
- egov-enc-service  
- egov-demand  
>>>>>>> master-LTS
- egov-billing-service
- egov-notification-sms
- egov-user-event
- pdf-service

---

## API Documentation

<<<<<<< HEAD
- **SpringDoc OpenAPI**: [http://localhost:8085/sv-services/swagger-ui.html](http://localhost:8085/sv-services/swagger-ui.html)

> 🛠️ **Local Access:**
>
> The service runs on port **8085** by default. Access Swagger UI directly using the link above.

> 🚀 **Access via Kubernetes (using `kubectl`):**
>
> ```bash
> kubectl port-forward sv-services-pod -n egov 8085:8085
> ```
>
> After that, open: [http://localhost:8085/sv-services/swagger-ui.html](http://localhost:8085/sv-services/swagger-ui.html)

---

=======
- [Swagger API Contract](http://localhost:8080/sv-services/swagger-ui.html#/street-vending-controller)

> 🛠️ **Local Access:**
>
> If you're running the service locally (e.g., via IntelliJ or Spring Boot), you can directly open the Swagger UI using the link above.

> 🚀 **Access via Kubernetes (using `kubectl`):**
>
> To access the API from a Kubernetes pod, use the following port-forward command:
>
> ```bash
> kubectl port-forward sv-services-9c47c6c87 -n egov 8080:8080
> ```
>
> After that, open: [http://localhost:8080/sv-services/swagger-ui.html](http://localhost:8080/sv-services/swagger-ui.html)

---


>>>>>>> master-LTS
## Key Modules / Features

### a) Vendor Registration
- Create/update vendor profiles with Aadhaar, phone, address, vending type, etc.
<<<<<<< HEAD
- Attach supporting documents with HTML sanitization
- Generate unique Vendor Code using IDGen
- Draft management with save/update/delete operations

### b) Workflow Integration
- Tracks application status: `APPLIED`, `VERIFIED`, `APPROVED`, `REJECTED`
- Workflow transitions based on roles and configuration
- Business service integration with egov-workflow-v2

### c) Certificate Generation
- Certificates issued after approval
- Certificate numbers generated via IDGen
- PDF generation support

### d) Renewal and Expiry
- Vendors can renew certificates
- **Distributed Scheduler** with ShedLock for expiry checking
- Payment schedule management
- Multiple renewal scenarios support

### e) Security Features
- **HTML Sanitization** using OWASP Java HTML Sanitizer
- **Field Encryption** support via egov-enc-service
- **Input Validation** with Spring Boot Validation

### f) Distributed Processing
- **ShedLock Integration** for distributed lock management
- **Database-based Locking** to prevent concurrent scheduler execution
- **Timezone Support** with configurable app timezone
=======
- Attach supporting documents.
- Generate unique Vendor Code using IDGen.

### b) Workflow Integration
- Tracks application status: `APPLIED`, `VERIFIED`, `APPROVED`, `REJECTED`.
- Workflow transitions based on roles and configuration.

### c) Certificate Generation
- Certificates issued after approval.
- Certificate numbers are generated via IDGen.

### d) Renewal and Expiry
- Vendors can renew certificates.
- Scheduler checks for expiry if `scheduler.sv.expiry.enabled=true`.
>>>>>>> master-LTS

---

## Environment Configuration

<<<<<<< HEAD
| Property | Description | Default/Example |
|---------|-------------|-----------------|
| `server.context-path` | Application base context path | `/sv-services` |
| `server.port` | Application port | `8085` |
=======
| Property | Description | Example |
|---------|-------------|---------|
| `server.context-path` | Application base context path | `/sv-services` |
| `server.port` | Application port | `8080` |
>>>>>>> master-LTS
| `app.timezone` | App timezone | `UTC` |
| `spring.datasource.url` | DB connection | `jdbc:postgresql://localhost:5432/postgres` |
| `spring.flyway.enabled` | Enable Flyway DB migration | `false` |
| `egov.idgen.street-vending.application.id.name` | IDGen key for application ID | `street-vending.application.id` |
| `egov.workflow.host` | Workflow service base URL | `http://localhost:8280` |
| `egov.sms.notification.topic` | Kafka topic for SMS | `egov.core.notification.sms` |
<<<<<<< HEAD
| `scheduler.sv.expiry.enabled` | Enable distributed scheduler | `true` |
| `sv.decryption.abac.enabled` | Enable field encryption | `false` |
| `upyog.mdms.v2.enabled` | Enable MDMS v2 | `true` |
=======
>>>>>>> master-LTS

> 🔐 Encryption/decryption can be enabled using `sv.decryption.abac.enabled=true`.

---

## Kafka Topics

### Producer Topics

| Topic | Purpose |
|-------|---------|
| `create-street-vending` | Create vendor |
| `update-street-vending` | Update vendor |
<<<<<<< HEAD
| `create-draft-street-vending` | Save draft |
| `update-draft-street-vending` | Update draft |
| `delete-draft-street-vending` | Delete draft |
| `create-payment-schedule` | Create payment schedule |
| `update-payment-schedule` | Update payment schedule |
=======
| `renew-street-vending` | Renew certificate |
| `create-draft-street-vending` | Save draft |
| `update-draft-street-vending` | Update draft |
| `delete-draft-street-vending` | Delete draft |
>>>>>>> master-LTS

### Notification Topics

| Topic | Type |
|-------|------|
| `egov.core.notification.sms` | SMS |
| `egov.core.notification.email` | Email |
| `persist-user-events-async` | User event |

<<<<<<< HEAD
### Consumer Topics

| **Topic** | **Consumed By** | **Purpose** |
|-----------|-----------------|-------------|
| `${persister.create.street-vending.topic}` | `NotificationConsumer` | Process notifications on creation |
| `${persister.update.street-vending.topic}` | `NotificationConsumer` | Process notifications on update |
| `${kafka.topics.receipt.create}` | `PaymentUpdateConsumer` | Handle payment update events |
=======
### 🔁 **Consumer Topics**

| **Topic**                                  | **Consumed By**            | **Purpose**                          |
|--------------------------------------------|-----------------------------|--------------------------------------|
| `${persister.create.street-vending.topic}` | `NotificationConsumer`      | Process notifications on creation   |
| `${persister.update.street-vending.topic}` | `NotificationConsumer`      | Process notifications on update     |
| `${kafka.topics.receipt.create}`           | `PaymentUpdateConsumer`     | Handle payment update events         |

>>>>>>> master-LTS

---

## API Endpoints

### BasePath: `/sv-services/street-vending`

| Action | Endpoint | Method | Description |
|--------|----------|--------|-------------|
| Create Vendor | `/_create` | POST | Creates a new street vending application. If marked as draft, creates a draft instead. |
| Update Vendor | `/_update` | POST | Updates an existing application. Handles both draft and submitted applications. |
<<<<<<< HEAD
| Search Vendors | `/_search` | POST | Searches vendors using various filters with HTML sanitization. |
| Delete Draft | `/_deletedraft` | POST | Deletes a saved draft using draft ID. |
| Create Demand | `/_createdemand` | POST | Creates demand for renewal. |
| Expiry Scheduler | `/trigger-expire-streetvendingapplications` | GET | Triggers expiry workflow manually (with ShedLock protection). |
=======
| Search Vendors | `/_search` | POST | Searches vendors using various filters. |
| Delete Draft | `/_deletedraft` | POST | Deletes a saved draft using draft ID. |
| Create Demand | `/_createdemand` | POST | Creates demand for renewal. |
| Expiry Scheduler | `/trigger-expire-streetvendingapplications` | GET | Triggers expiry workflow manually. |
>>>>>>> master-LTS

---

## ID Generation Formats

| Purpose | Format |
<<<<<<< HEAD
|---------|--------|
=======
|--------|--------|
>>>>>>> master-LTS
| Application Number | `SV-[CITY.CODE]-[seq_street_vending_application_id]` |
| Certificate Number | `SV-CT-[seq_street_vending_certificate_no]` |
| Receipt Number | `SV/[CITY.CODE]/[fy:yyyy-yy]/[SEQ_EGOV_COMMON]` |

---

<<<<<<< HEAD
## New Features in v2.0.0

### Distributed Scheduler
- **ShedLock Integration**: Prevents multiple instances from running the same scheduled task
- **Database-based Locking**: Uses shared database for distributed coordination
- **Configurable Lock Duration**: Default lock timeout of 30 minutes (`PT30M`)

### Enhanced Security
- **HTML Sanitization**: All user inputs are sanitized using OWASP Java HTML Sanitizer
- **Custom Validation Annotations**: `@SanitizeHtml` for automatic input cleaning
- **Field-level Encryption**: Optional encryption for sensitive data

### Modern Spring Boot 3.x
- **Java 17 Support**: Latest LTS Java version
- **SpringDoc OpenAPI**: Modern API documentation (replaces Springfox)
- **Jakarta EE Namespace**: Updated for Spring Boot 3.x compatibility

### MDMS v2 Support
- **Dual MDMS Support**: Both v1 and v2 MDMS integration
- **Configurable MDMS Version**: Switch between MDMS versions via configuration

---

=======
>>>>>>> master-LTS
## Localization

| Property | Value |
|----------|-------|
| `egov.localization.host` | `http://localhost:1234` |
| `egov.localization.context.path` | `/localization/messages/v1` |
| `egov.localization.search.endpoint` | `/_search` |
<<<<<<< HEAD
| `egov.localization.statelevel` | `true` |
=======
>>>>>>> master-LTS

---

## Billing Integration

| Action | Endpoint |
|--------|----------|
| Fetch Tax Heads | `/billing-service/taxheads/_search` |
| Create Demand | `/billing-service/demand/_create` |
<<<<<<< HEAD
| Update Demand | `/billing-service/demand/_update` |
| Search Demand | `/billing-service/demand/_search` |
=======
>>>>>>> master-LTS
| Fetch Bill | `/billing-service/bill/v2/_fetchbill` |

---

<<<<<<< HEAD
## Scheduler Configuration
=======
## Miscellaneous

| Key | Description |
|-----|-------------|
| `sv.module.name` | Module identifier | `sv-services` |
| `sv.business.service.name` | Business service key | `street-vending` |
| `state.level.tenant.id` | Top-level tenant | `pg` |
| `spring.flyway.enabled` | Enable/disable Flyway DB migration | `false` |

---

## Notification System

- Notifications via SMS/Email are enabled:
  - `notification.sms.enabled=true`
  - `notification.email.enabled=true`
- Templates are managed using **Localization Service**

---

## Scheduler
>>>>>>> master-LTS

| Property | Description | Value |
|----------|-------------|-------|
| `scheduler.sv.expiry.enabled` | Enables scheduled expiry job | `true` |
<<<<<<< HEAD
| **ShedLock Configuration** | | |
| `@EnableSchedulerLock` | Enables distributed locking | `defaultLockAtMostFor = "PT30M"` |
| Lock Provider | Database-based lock provider | JDBC Template |

---

## Renewal Scenarios

| Scenario | Application Status | Renewal Status | Expire Flag | Description |
|----------|-------------------|----------------|-------------|-------------|
| Initial Application | REGISTRATIONCOMPLETED | null | false | When a new application is created and completed |
| Scheduler Marks for Renewal | REGISTRATIONCOMPLETED | ELIGIBLE_TO_RENEW | false | Scheduler marks applications nearing expiry (2 months before) |
| Direct Renewal (Payment) | REGISTRATIONCOMPLETED | RENEWED | false | When user directly pays for renewal without editing |
| Renewal with Edit | REGISTRATIONCOMPLETED | RENEW_APPLICATION_CREATED | false | When user creates a new application for renewal |
| Expired Application | EXPIRED | ELIGIBLE_TO_RENEW | true | When application validity date has passed |
| Renewal In Progress | REGISTRATIONCOMPLETED | RENEW_IN_PROGRESS | false | When user initiates renewal process |

---

## Security & Encryption

- **Optional Field Encryption** using egov-enc-service
- **HTML Sanitization** for all user inputs
- **Input Validation** with Spring Boot Validation
- **Secure Configuration** with externalized properties

### Encryption Endpoints:
- `/egov-enc-service/crypto/v1/_encrypt`
- `/egov-enc-service/crypto/v1/_decrypt`

---

## Development Setup

### Prerequisites
- Java 17
- PostgreSQL 12+
- Kafka
- Maven 3.6+



## Key Changes Made:

1. **Updated Technical Stack**: Added Java 17, Spring Boot 3.2.2, and new dependencies
2. **Port Change**: Updated from 8080 to 8085
3. **Package Structure**: Updated to `org.upyog.sv`
4. **New Features Section**: Added ShedLock, HTML sanitization, MDMS v2 support
5. **Enhanced Security**: Added security features and validation details
6. **Scheduler Updates**: Added distributed scheduler configuration
7. **API Documentation**: Updated to SpringDoc OpenAPI
8. **Development Setup**: Added setup instructions
9. **Additional Kafka Topics**: Added payment schedule topics
10. **Configuration Updates**: Added new properties and their descriptions

The README now accurately reflects the current state of your codebase with all the modern Spring Boot 3.x features and security enhancements you've implemented.
=======

---
## Renewal Scenarios
| Scenario                                       | Application Status      | Renewal Status          | Expire Flag | Description                                                     |
|------------------------------------------------|-------------------------|-------------------------|-------------|-----------------------------------------------------------------|
| Initial Application                            | REGISTRATIONCOMPLETED   | null                    | false       | When a new application is created and completed                 |
| Scheduler Marks for Renewal(2 months before expiry) | REGISTRATIONCOMPLETED   | ELIGIBLE_TO_RENEW       | false       | Scheduler marks applications nearing expiry                     |
| Direct Renewal (Payment)                       | REGISTRATIONCOMPLETED   | RENEWED                 | false       | When user directly pays for renewal without editing             |
| Renewal with Edit                              | REGISTRATIONCOMPLETED   | RENEW_APPLICATION_CREATED | false       | When user creates a new application for renewal                 |
| Expired Application                            | EXPIRED                 | ELIGIBLE_TO_RENEW       | true        | When application validity date has passed                      |
| Renewal In Progress                            | REGISTRATIONCOMPLETED   | RENEW_IN_PROGRESS       | false       | When user initiates renewal process                               |


## Security & Encryption

- Optional support for encrypting sensitive fields.
- Use:
  - `/egov-enc-service/crypto/v1/_encrypt`
  - `/egov-enc-service/crypto/v1/_decrypt`


For further information, [click this link](https://docs.google.com/document/d/1AY0mLcFORtN4bKyJ5zLnMAPsWdB4K4t5ootRYjXcwSU/edit?tab=t.0).
>>>>>>> master-LTS

