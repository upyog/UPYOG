# Community Hall Booking (CHB)

## Overview

The **Community Hall Booking (CHB)** module allows citizens to book community halls managed by the urban local bodies (ULBs). It supports the end-to-end lifecycle of a booking—from application to approval, billing, and payment—with integration into workflows and notifications.

---

## API Documentation

- [Swagger API Contract](http://localhost:8080/chb-services/swagger-ui.html#/community-hall-booking-controller)

> 🛠️ **Use Locally:**  
> Run the service (e.g., via IntelliJ or Spring Boot), then access Swagger UI from your browser.

> 🚀 **Using Kubernetes:**  
> Run the following command to access locally:
> ```bash
> kubectl port-forward chb-services-674d689448-wxtz7 -n egov 8080:8080
> ```
> Then open: [http://localhost:8080/chb-services/swagger-ui.html#/community-hall-booking-controller](http://localhost:8080/chb-services/swagger-ui.html#/community-hall-booking-controller)

---

## Service Dependencies

- egov-user  
- egov-mdms / egov-mdms-v2  
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

## Kafka Topics

### Producer Topics

| Topic | Purpose |
|-------|---------|
| save-community-hall-booking | Save initial booking |
| update-community-hall-booking | Update booking details |
| update-community-hall-workflow-booking | Update booking after workflow transition |

### Notification Topics

| Topic | Type |
|----------------------------|----------------|
| egov.core.notification.sms | SMS |
| egov.core.notification.email | Email |
| persist-user-events-async | User event |

### Consumer Topics

| Topic | Consumed By | Purpose |
|-----------------------------|------------------------|--------------------------|
| save-community-hall-booking | Persister | Create booking |
| update-community-hall-booking | Persister | Update booking |
| egov.collection.payment-create | PaymentUpdateConsumer | Handle payments |

---

## API Endpoints

### Base Path: `/chb-services/booking/v1`

| Action | Endpoint | Method | Description |
|--------|----------|--------|-------------|
| Create Booking | `/_create` | POST | Submit a new booking application |
| Update Booking | `/_update` | POST | Update existing booking |
| Search Bookings | `/_search` | POST | Filter bookings by multiple parameters |
| Estimate Bookings | `/_estimate` | POST | Estimate demand for booking |
| Slot-search Bookings | `/_slot-search` | POST | Check slot availability for halls |

---

## ID Generation Format

| Key | Format |
|-----|--------|
| Booking No | `CHB-[CITY.CODE]-[seq_chb_booking_id]` |
| Receipt No | `CHB/[CITY.CODE]/[fy:yyyy-yy]/[SEQ_EGOV_COMMON]` |

---

## Environment Configuration

| Property | Example |
|----------|---------|
| `server.context-path` | `/chb-services` |
| `server.port` | `8080` |
| `app.timezone` | `UTC` |
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/localhost` |
| `spring.datasource.username` | `keshav` |
| `spring.datasource.password` | `niua@123` |
| `spring.flyway.enabled` | `false` |
| `spring.application.name` | `chb-services` |

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

---

## Billing Integration

| Action | Endpoint |
|--------|----------|
| Create Demand | /billing-service/demand/_create |
| Update Demand | /billing-service/demand/_update |
| Search Demand | /billing-service/demand/_search |
| Fetch Bill | /billing-service/bill/v2/_fetchbill |
| Search Tax Heads | /billing-service/taxheads/_search |

---

## Notification Links

| Description | Template |
|-------------|----------|
| Payment Link | `citizen/otpLogin?mobileNo=$mobile&redirectTo=digit-ui/citizen/payment/my-bills/$businessService/$consumerCode` |
| Download Receipt No | `citizen/otpLogin?mobileNo=$mobile&redirectTo=egov-common/download-receipt?status=success&consumerCode=$consumerCode&tenantId=$tenantId&receiptNumber=$receiptNumber&businessService=$businessService&smsLink=true&mobileNo=$mobile` |

---

## Encryption / Decryption

| Description | Endpoint |
|-------------|----------|
| Encrypt | `/egov-enc-service/crypto/v1/_encrypt` |
| Decrypt | `/egov-enc-service/crypto/v1/_decrypt` |
| Enable Decryption | `chb.decryption.abac.enabled=false` |


For further information, [click this link](https://docs.google.com/document/d/1J8IvWhz87b91tczWhn-qsetEOcEw0YunP9pAcqfgEkM/edit?tab=t.0).


---