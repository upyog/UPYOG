# Estate Management Service

The **Estate Management Service** is a Spring Boot municipal service module in UPYOG. It enables urban local bodies (ULBs) to manage municipal assets, handle rent allotments, auto-generate billing demands periodically, and process monthly, quarterly, and yearly rent payments.

---

## 🛠️ Technology Stack
- **Core**: Java 17, Spring Boot 3.2.2
- **Database**: PostgreSQL with Flyway DB Migrations
- **Queue**: Apache Kafka for asynchronous persistence and event-driven updates (e.g. payment collection)
- **Build Tool**: Maven

---

## 🏗️ Core Workflow

1. **Asset Creation**: Municipal assets (shops, community halls, lands) are registered and assigned unique estate numbers (`estateNo`).
2. **Allotment Registration**: Assets are leased/allotted to citizens. This generates a unique `allotmentNo` via IDGen, assigns the initial lease `dueDate`, sets default status to `PENDING_FOR_PAYMENT`, and updates the asset status to `ALLOTTED`.
3. **Periodic Billing (Scheduler)**: Scheduled jobs periodically run to generate demand bills for leased allotments and update their statuses to `PENDING_FOR_PAYMENT`. 
   - Supports **monthly**, **quarterly**, and **yearly** cycles.
   - Can be triggered asynchronously via the **`trigger-estate-demand`** Kafka topic.
4. **Payment Processing**: Receipt events from `collection-services` are consumed via Kafka (or REST trigger), which updates the allotment status to `PAID` and saves payment details for all billing cycles.

---

## 🔌 API Documentation

### Asset Endpoints
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/estate/asset/v1/_create` | Creates a new asset and generates an `estateNo`. |
| `POST` | `/estate/asset/v1/_update` | Updates asset details (area, category, structure, etc.). |
| `POST` | `/estate/asset/v1/_search` | Searches assets with pagination (`limit`, `offset`) and sorts by creation time (latest first). |

### Allotment & Payment Endpoints
| HTTP Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/estate/allotment/v1/_create` | Creates an allotment, validates asset existence, creates initial demand, and sets status to `PENDING_FOR_PAYMENT`. |
| `POST` | `/estate/allotment/v1/_search` | Searches allotments with pagination (`limit`, `offset`) and filters (allotmentNo, mobileNumber, etc.). |
| `POST` | `/estate/payment/v1/_update` | REST endpoint to manually trigger payment update processing locally (useful for local testing without Kafka). |
| `POST` | `/estate/scheduler/v1/_trigger` | Manually triggers the periodic billing cycle demand generator. |

---

## 🗃️ Database Tables
- **`ug_em_asset_details`**: Stores asset structural profiles, classification, types, and allotment states.
- **`ug_em_allotment_details`**: Stores lease agreements, allottee contact details, rental rates, allotment numbers, and statuses.
- **`ug_em_monthly_rent_payment`**: Stores billing history, due dates, paid amounts, and payment statuses for monthly, quarterly, and yearly rent payments.
- **`eg_est_scheduler_log`**: Logs database of scheduler actions, billing dates, time spans, and generation outcomes.

---

## 📡 Kafka Topics & Consumers

### Listeners
- **`egov.collection.payment-create`** (Property: `kafka.topics.receipt.create`): Listens for successful billing transaction events from `collection-services` to mark the corresponding allotment as `PAID`.
- **`trigger-estate-demand`** (Property: `trigger-estate-demand-topic`): Listens for requests to execute monthly, quarterly, and yearly billing demand generation asynchronously.

### Publishers
- **`save-asset-details`** / **`update-asset-details`**: Saves or updates asset entries.
- **`save-allotment-details`** / **`update-allotment-details`**: Saves or updates lease allotment entries.
- **`save-monthly-rent-payment`**: Saves payment collection logs for all monthly, quarterly, and yearly billing cycles.
- **`save-est-scheduler-log`** (Property: `save-scheduler-log-topic`): Saves demand generation scheduler execution logs.

---

## 🚀 Running Locally

### Prerequisites
- JDK 17
- Apache Maven 3.6+
- PostgreSQL instance running locally

### Execution Command
Run the Spring Boot application from the `estate-management` directory:
```bash
mvn spring-boot:run
```
