# Street Vending (street-vending)

## Overview
The **Street Vending (SV)** module enables urban local bodies (ULBs) to manage and regulate street vendors as per the Street Vendors Act, 2014. It handles the full lifecycle from vendor registration to issuing certificates, renewals, and expiries using workflow-based processes.

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
- egov-billing-service
- egov-notification-sms
- egov-user-event
- pdf-service

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
> kubectl port-forward sv-services-9c47c6c87 -n egov 8080:8080
> ```
>
> After that, open: [http://localhost:8080/sv-services/swagger-ui.html](http://localhost:8080/sv-services/swagger-ui.html)

---


## Key Modules / Features

### a) Vendor Registration
- Create/update vendor profiles with Aadhaar, phone, address, vending type, etc.
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

---

## Environment Configuration

| Property | Description | Example |
|---------|-------------|---------|
| `server.context-path` | Application base context path | `/sv-services` |
| `server.port` | Application port | `8080` |
| `app.timezone` | App timezone | `UTC` |
| `spring.datasource.url` | DB connection | `jdbc:postgresql://localhost:5432/postgres` |
| `spring.flyway.enabled` | Enable Flyway DB migration | `false` |
| `egov.idgen.street-vending.application.id.name` | IDGen key for application ID | `street-vending.application.id` |
| `egov.workflow.host` | Workflow service base URL | `http://localhost:8280` |
| `egov.sms.notification.topic` | Kafka topic for SMS | `egov.core.notification.sms` |

> 🔐 Encryption/decryption can be enabled using `sv.decryption.abac.enabled=true`.

---

## Kafka Topics

### Producer Topics

| Topic | Purpose |
|-------|---------|
| `create-street-vending` | Create vendor |
| `update-street-vending` | Update vendor |
| `renew-street-vending` | Renew certificate |
| `create-draft-street-vending` | Save draft |
| `update-draft-street-vending` | Update draft |
| `delete-draft-street-vending` | Delete draft |

### Notification Topics

| Topic | Type |
|-------|------|
| `egov.core.notification.sms` | SMS |
| `egov.core.notification.email` | Email |
| `persist-user-events-async` | User event |

### 🔁 **Consumer Topics**

| **Topic**                                  | **Consumed By**            | **Purpose**                          |
|--------------------------------------------|-----------------------------|--------------------------------------|
| `${persister.create.street-vending.topic}` | `NotificationConsumer`      | Process notifications on creation   |
| `${persister.update.street-vending.topic}` | `NotificationConsumer`      | Process notifications on update     |
| `${kafka.topics.receipt.create}`           | `PaymentUpdateConsumer`     | Handle payment update events         |


---

## API Endpoints

### BasePath: `/sv-services/street-vending`

| Action | Endpoint | Method | Description |
|--------|----------|--------|-------------|
| Create Vendor | `/_create` | POST | Creates a new street vending application. If marked as draft, creates a draft instead. |
| Update Vendor | `/_update` | POST | Updates an existing application. Handles both draft and submitted applications. |
| Search Vendors | `/_search` | POST | Searches vendors using various filters. |
| Delete Draft | `/_deletedraft` | POST | Deletes a saved draft using draft ID. |
| Create Demand | `/_createdemand` | POST | Creates demand for renewal. |
| Expiry Scheduler | `/trigger-expire-streetvendingapplications` | GET | Triggers expiry workflow manually. |

---

## ID Generation Formats

| Purpose | Format |
|--------|--------|
| Application Number | `SV-[CITY.CODE]-[seq_street_vending_application_id]` |
| Certificate Number | `SV-CT-[seq_street_vending_certificate_no]` |
| Receipt Number | `SV/[CITY.CODE]/[fy:yyyy-yy]/[SEQ_EGOV_COMMON]` |

---

## Localization

| Property | Value |
|----------|-------|
| `egov.localization.host` | `http://localhost:1234` |
| `egov.localization.context.path` | `/localization/messages/v1` |
| `egov.localization.search.endpoint` | `/_search` |

---

## Billing Integration

| Action | Endpoint |
|--------|----------|
| Fetch Tax Heads | `/billing-service/taxheads/_search` |
| Create Demand | `/billing-service/demand/_create` |
| Fetch Bill | `/billing-service/bill/v2/_fetchbill` |

---

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

| Property | Description | Value |
|----------|-------------|-------|
| `scheduler.sv.expiry.enabled` | Enables scheduled expiry job | `true` |

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

