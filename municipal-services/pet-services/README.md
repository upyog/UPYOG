# Pet Service

Pet service creates pet application for user which can be acted upon by ulb officals to verify and approve the application, and further provide the user with a pet certificate.

## Technology Stack

- **Java**: 17 (LTS)
- **Spring Boot**: 3.2.2
- **Maven**: 3.x
- **Database**: PostgreSQL
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Validation**: Jakarta Validation API
### DB UML Diagram

NA

### Service Dependencies

- User Service (user)
- ID Gen. Service (ID-GEN)
- MDM Service (MDMS)
- Location Service (Location)
- Localisation Service (localisation)

### c) Certificate Generation
- Certificates issued after approval.
- Certificate numbers are generated via IDGen.


## Service Details

**a) Application Submission & Status Tracking:** Citizens or counter employees can submit a pet registration application by providing necessary details and uploading required documents. Citizens can track their application status under the “My Applications” section.

**b) Ulb Employee Processing & Approval Flow:** ULB employees can filter, view, and validate submitted applications. After verification, they can forward the application to higher authorities for approval.

**c) Payment, Receipt & Certificate Download:** Once approved, the citizen or counter employee can make the payment. After payment, the citizen can download the payment receipt and the final Pet Certificate from their login.

### API Documentation

#### Swagger UI
- **Local Development**: [http://localhost:8087/pet-services/swagger-ui/index.html](http://localhost:8087/pet-services/swagger-ui/index.html)
- **OpenAPI Specification**: [http://localhost:8087/pet-services/v3/api-docs](http://localhost:8087/pet-services/v3/api-docs)

#### Postman Collection
- Import the OpenAPI specification directly into Postman using the above URL
- Or use the provided Postman collection in `/postman/` directory

### Configurable properties

| Environment Variables                    | Description                                                                                        | Value                                            |
| ---------------------------------------- |----------------------------------------------------------------------------------------------------|--------------------------------------------------|
| `https://niuatt.niua.in`             | This is the link to the UPYOG Portal, which differs based on the environment.                      | https://upyog.niua.org/digit-ui/employee/user/login |
| `egov.mdms.search.endpoint`     | This is the mdms (Master Data Management System) endpoint to which system makes the for mdms data. |                     /egov-mdms-service/v1/_search                             |
| `egov.usr.events.create.topic`           | Persister create topic configured in application.properties file.                                  | persist-user-events-async                                                 |
| `egov.billingservice.host`              | This is the billing service endpoint for any bill, demand call.                                    | http://billing-service:8080                      |



## Billing Integration

| Action | Endpoint |
|--------|----------|
| Fetch Tax Heads | `/billing-service/taxheads/_search` |
| Create Demand | `/billing-service/demand/_create` |
| Fetch Bill | `/billing-service/bill/v2/_fetchbill` |

## Notification System

- Notifications via SMS/Email are enabled:
  - `notification.sms.enabled=true`
  - `notification.email.enabled=true`
- Templates are managed using **Localization Service**

## Scheduler

| Property | Description | Value |
|----------|-------------|-------|
| `scheduler.sv.expiry.enabled` | Enables scheduled expiry job | `true` |

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Kafka** (for async processing)

## Getting Started

### Local Development

```bash
# Clone the repository
git clone <repository-url>

# Navigate to pet-services directory
cd municipal-services/pet-services

# Build the application
mvn clean compile

# Run the application
mvn spring-boot:run
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/pet-registration/_create` | Create new Pet Application |
| POST | `/pet-registration/_update` | Update existing Pet Application |
| POST | `/pet-registration/_search` | Search Pet Applications |
| POST | `/pet-registration/trigger-expire-petapplications` | Trigger application expiry process |
| POST | `/pet-registration/trigger-advance-notification` | Trigger advance notifications |

## Kafka Integration

### Topics

| Topic | Type | Description |
|-------|------|-------------|
| `save-ptr-application` | Producer/Consumer | Create pet applications |
| `update-ptr-application` | Producer/Consumer | Update pet applications |
| `renew-ptr-application` | Producer/Consumer | Renew pet applications |
| `expire-ptr-application` | Producer | Expire pet applications |

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=8087
server.servlet.context-path=/pet-services

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/petdb
spring.datasource.username=postgres
spring.datasource.password=postgres

# Kafka Configuration
kafka.config.bootstrap_server_config=localhost:9092
```

## Deployment

### Docker

```bash
# Build Docker image
docker build -t pet-services:latest .

# Run container
docker run -p 8087:8087 pet-services:latest
```

## Testing

### Unit Tests

```bash
mvn test
```


## Contributing

1. Follow Java 17 coding standards
2. Use Jakarta EE specifications
3. Ensure all tests pass before committing
4. Update API documentation for any endpoint changes
