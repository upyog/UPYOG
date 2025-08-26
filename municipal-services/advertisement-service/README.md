# Advertisment Module Service (egov-advertisment)

## Overview 
 The Advertisement Module enables users to book advertisement holdings in real-time, offering features such as real-time slot availability, integrated payment flow, and support for multiple locations and advertisement mediums.

## DB UML Diagram
 **Not Applicable (NA)**


## Service Dependencies
- **egov-user** (User Service)
- **egov-mdms** (Master Data Management Service)
- **egov-idgen** (ID Generation Service)
- **egov-filestore** (File Store Service)
- **egov-location** (Location Service)
- **egov-localization** (Localization Service)
- **egov-boundary** (Optional Boundary Service)


 
## API Documentation

- [Swagger API Contract](http://localhost:8080/adv-services/swagger-ui.html#)

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
> After that, open: [http://localhost:8080/adv-services/swagger-ui.html](http://localhost:8080/adv-services/swagger-ui.html)
---
## Entities
## Configurable Properties

| Environment Variable                            | Description                                                                 | Example Value                                      
|-------------------------------------------------|-----------------------------------------------------------------------------|---------------------------------------------------|
| `egov.adv.default.limit`                        | Default number of records per page for pagination                           | `10`                                              |
| `egov.idgen.advertisement.booking.id.name`      | IDGen key name used for generating advertisement booking IDs                | `advertisement.booking.id`                        |
| `egov.idgen.advertisement.booking.id.format`    | Format to generate advertisement booking IDs                                | `ADV-[CITY.CODE]-[seq_adv_booking_id]`            |
| `notif.sms.enabled`                             | Enables or disables SMS notifications                                       | `true`                                            |
| `notif.email.enabled`                           | Enables or disables Email notifications                                     | `true`                                            |
| `egov.enc.host`                                 | Encryption service base URL                                                 | `http://localhost:8083`                           |
| `egov.enc.encrypt.endpoint`                     | API endpoint for data encryption                                            | `/egov-enc-service/crypto/v1/_encrypt`            |
| `egov.enc.decypt.endpoint`                      | API endpoint for data decryption                                            | `/egov-enc-service/crypto/v1/_decrypt`            |
| `adv.decryption.abac.enabled`                   | Toggle for enabling attribute-based access control decryption               | `false`                                           |



## API Details  
**BasePath:** `/adv-services/booking/v1`
---

### a) Create  
- **POST** `/adv-services/booking/v1/_create`  
- Creates a new advertisement booking with all the required details.

---

### b) Update  
- **POST** `/adv-services/booking/v1/_update`  
- Updates an existing advertisement booking with modified information.

---

### c) Search  
- **POST** `/adv-services/booking/v1/_search`  
- Searches for bookings based on parameters like application number, status, booking dates, and applicant details.

---

### d) Slot Search  
- **POST** `/adv-services/booking/v1/_slot-search`  
- Returns available slots for selected holdings based on location, date, and duration.

---

### e) Estimate Create  
- **POST** `/adv-services/booking/v1/_estimate`  
- Generates a cost estimate for the selected advertisement slot before booking.


## ID Generation Formats
| Purpose               |                    Format                       |
|-----------------------|-------------------------------------------------|
| Application ID | `ADV-[CITY.CODE]-[seq_adv_booking_id]]`                |
| Receipt ID | `ADV/[CITY.CODE]/[fy:yyyy-yy]/[SEQ_EGOV_COMMON]`           |

## Localization
| Property | Value                                                |
|----------|------------------------------------------------------|
| `egov.localization.host` | `http://localhost:1234`              |
| `egov.localization.context.path` | `/localization/messages/v1`  |
| `egov.localization.search.endpoint` | `/_search`                |


## Billing Integration
| Action            |                 Endpoint              |
|-------------------|---------------------------------------|
| Fetch Tax Heads   | `/billing-service/taxheads/_search`   |
| Create Demand     | `/billing-service/demand/_create`     |
| Fetch Bill        | `/billing-service/bill/v2/_fetchbill` |

### Producer Topics
| Topic Name                   | Purpose                                     |
|------------------------------|---------------------------------------------|
| `save-sv-vendor`             | Publishes vendor creation events            |
| `update-sv-vendor`           | Publishes vendor update events              |
| `egov.core.notification.sms` | Sends SMS notifications                     |

### Consumer Topics
- **None**


## Notification
- SMS notifications triggered on:
  - Successful registration
  - Application approval or rejection
- Templates configured via **egov-localization** service.

## Security & Encryption
- Optional support for encrypting sensitive fields.
- Use:
  - `/egov-enc-service/crypto/v1/_encrypt`
  - `/egov-enc-service/crypto/v1/_decrypt`