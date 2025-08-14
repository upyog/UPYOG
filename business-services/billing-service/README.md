# Billing Service

A comprehensive billing and demand management service for UPYOG platform that handles bill generation, demand management, and payment processing for various revenue services.

## Overview

The Billing Service is a Spring Boot 3.2.2 application that manages the complete billing lifecycle including demand creation, bill generation, payment processing, and amendments. It serves as the central billing engine for multiple municipal services.

## Features

- **Demand Management**: Create, update, and search demands for various services
- **Bill Generation**: Generate bills based on existing demands with configurable formats
- **Payment Integration**: Process payments and update demand status via Kafka
- **Amendment Support**: Handle bill amendments with workflow integration
- **Multi-tenant Support**: Tenant-level bill number generation and management
- **Bulk Operations**: Support for bulk bill generation and processing
- **Audit Trail**: Complete audit logging for demands and bills

## Technology Stack

- **Framework**: Spring Boot 3.2.2
- **Language**: Java 17
- **Database**: PostgreSQL 42.7.1
- **Message Queue**: Apache Kafka
- **Build Tool**: Maven 3.3.9
- **Documentation**: SpringDoc OpenAPI 2.3.0

## Service Dependencies

### Core Dependencies
- **egov-user**: User management and authentication
- **egov-idgen**: ID generation service
- **egov-localization**: Localization and translation
- **egov-apportion**: Demand apportionment service
- **egov-mdms**: Master data management
- **egov-workflow-v2**: Amendment workflow management

### Infrastructure Dependencies
- PostgreSQL Database
- Apache Kafka
- Redis (optional)

## API Endpoints

### Demand APIs
- `POST /demand/_create` - Create new demands
- `POST /demand/_update` - Update existing demands
- `POST /demand/_search` - Search demands with criteria

### Bill APIs
- `POST /bill/v2/_generate` - Generate bills from demands
- `POST /bill/v2/_fetchbill` - Fetch or generate bills
- `POST /bill/v2/_search` - Search bills
- `POST /bill/v2/_cancel` - Cancel bills

### Amendment APIs
- `POST /amendment/_create` - Create bill amendments
- `POST /amendment/_update` - Update amendments
- `POST /amendment/_search` - Search amendments

### Master Data APIs
- `POST /taxhead/_create` - Create tax heads
- `POST /taxperiod/_create` - Create tax periods
- `POST /businessservice/_create` - Create business service details


## Kafka Integration

### Consumers
- **Payment Updates**: `egov.collection.payment-create`
- **Payment Cancellation**: `egov.collection.payment-cancel`
- **Receipt Processing**: `egov.collection.receipt-create`

### Producers
- **Bill Generation**: `save-bill-db`, `update-bill-db`
- **Demand Updates**: `save-demand`, `update-demand`
- **Notifications**: `billing-billgen-topic-name`
- **Bulk Operations**: `bulk-bill-generator`

## Local Development

### Prerequisites
- Java 17
- Maven 3.3.9+
- PostgreSQL
- Apache Kafka

### Setup
1. Clone the repository
2. Configure database connection in `application.properties`
3. Start required services (PostgreSQL, Kafka)
4. Port forward dependent services:
   ```bash
   kubectl port-forward -n egov $(kubectl get pods -n egov --selector=app=egov-user --no-headers=true | head -n1 | awk '{print $1}') 8085:8080
   kubectl port-forward -n egov $(kubectl get pods -n egov --selector=app=egov-idgen --no-headers=true | head -n1 | awk '{print $1}') 8086:8080
   ```
5. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Testing
Access Swagger UI at: `http://localhost:8081/billing-service/swagger-ui/index.html`

## Build and Deployment

```bash
# Build the application
mvn clean compile

# Run tests
mvn test

# Create JAR
mvn clean package

# Skip tests during build
mvn clean package -DskipTests
```

## Version History

- **v2.0.0** (2024-06-30): Spring Boot 3.x upgrade, OpenAPI documentation
- **v1.3.5** (2023-02-01): Stable release with amendment support
- **v1.3.4** (2022-01-04): Security updates (log4j2 2.17.1)
- **v1.3.3** (2021-07-26): Bill cancellation and state-level features

For detailed changelog, see [CHANGELOG.md](CHANGELOG.md)

## Documentation

- [Local Setup Guide](LOCALSETUP.md)
- [API Documentation](http://localhost:8081/billing-service/swagger-ui/index.html)
- [Postman Collection](src/main/resources/postman-collection/)

## Support

For issues and support, please refer to the UPYOG community documentation or raise issues in the project repository.