# CND Service - Construction and Demolition Waste Management

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.1-blue.svg)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

## Overview

The CND (Construction and Demolition) Service is a comprehensive waste management system designed for municipal services under the UPYOG platform. It facilitates the management of construction and demolition waste through digital applications, workflow management, and integrated payment systems.

## Key Features

### 🏗️ Application Management
- **Dual Application Types**:
  - `REQUEST_FOR_PICKUP`: Citizens request waste pickup from construction sites
  - `DEPOSIT_TO_THE_CENTER`: Direct deposit of waste to designated centers
- **Comprehensive Application Details**: Property type, construction dates, waste quantities, vehicle assignments
- **Document Management**: Upload and manage supporting documents with file store integration
- **Address & Applicant Management**: Detailed applicant and address information handling

### 🔄 Workflow Management
- **Multi-stage Approval Process**:
  - Application submission by citizens/employees
  - Field inspector assignment and verification
  - Approval and payment processing
  - Vendor and vehicle assignment
  - Waste pickup execution
  - Application completion
- **Role-based Actions**: Different actions available for Citizens, Employees, Field Inspectors, Vendors
- **Status Tracking**: Real-time application status updates throughout the lifecycle

### 💰 Payment Integration
- **Billing Service Integration**: Automated demand creation and bill generation
- **Tax Calculation**: Integration with CND calculator service for fee computation
- **Payment Gateway**: Seamless payment processing with receipt generation

### 📊 Waste Type Management
- **Multiple Waste Types**: Support for various construction and demolition waste categories
- **Quantity Tracking**: Precise measurement and metrics for waste quantities
- **Facility Center Details**: Integration with disposal and processing centers

### 🔔 Notification System
- **Multi-channel Notifications**: SMS and Email notifications
- **Event-driven Messaging**: Kafka-based notification system
- **User Events**: Application status updates and payment reminders
- **URL Shortening**: Integrated link shortening for better user experience

### 🔍 Search & Reporting
- **Advanced Search**: Multi-criteria search functionality
- **Pagination Support**: Efficient data retrieval with configurable limits
- **Audit Trail**: Complete audit logging for all operations
- **Data Export**: Support for various reporting formats

## Technical Architecture

### Technology Stack
- **Backend**: Spring Boot 3.2.2 with Java 17
- **Database**: PostgreSQL with Flyway migrations
- **Message Queue**: Apache Kafka for event-driven architecture
- **API Documentation**: SpringDoc OpenAPI 3.x
- **Security**: Input validation and HTML sanitization
- **Build Tool**: Maven

### Database Schema
- `ug_cnd_application_details`: Main application table
- `ug_cnd_waste_detail`: Waste type and quantity details
- `ug_cnd_document_detail`: Document management
- `ug_cnd_disposal_deposit_centre_detail`: Facility center information
- Audit tables for complete transaction history

### Integration Points
- **MDMS (Master Data Management)**: Configuration and master data
- **User Service**: User management and authentication
- **Workflow Service**: Business process management
- **Billing Service**: Payment and demand management
- **Notification Service**: SMS/Email delivery
- **File Store**: Document storage and retrieval
- **ID Generation**: Unique identifier generation
- **URL Shortener**: Link management

## API Endpoints

### Core Operations
- `POST /cnd-service/v1/_create` - Create new CND application
- `POST /cnd-service/v1/_search` - Search applications with criteria
- `POST /cnd-service/v1/_update` - Update existing applications

### Request/Response Models
- **CNDApplicationRequest**: Complete application submission
- **CNDApplicationResponse**: Application details with metadata
- **CNDServiceSearchCriteria**: Multi-parameter search options
- **CNDServiceSearchResponse**: Paginated search results

## Configuration

### Environment Setup
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

# Kafka Configuration
kafka.config.bootstrap_server_config=localhost:9092

# Services you have to port-forward while running this service locally check the port from application.properties
egov.mdms.host=http://localhost:8094
egov.user.host=http://localhost:6161
egov.workflow.host=http://localhost:8280
egov.billingservice.host=http://localhost:8077
```

### Application Properties
- **Server Context**: `/cnd-service`
- **Default Port**: `8080`
- **Pagination**: Default limit 10, max limit 50
- **Workflow**: Enabled with business service `cnd`
- **ID Format**: `CND-[CITY.CODE]-[seq_cnd_application_id]`

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL 12+
- Apache Kafka
- Maven 3.6+

### Installation

1. **Clone the repository**
   ```bash
   git clone <https://github.com/upyog/UPYOG>
   cd municiple-services/cnd-service
   ```

2. **Database Setup**
   ```bash
   # Create PostgreSQL database
   createdb postgres

   # Flyway will automatically run migrations on startup
   ```

3. **Build the application**
   ```bash
   mvn clean compile
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access API Documentation**
   - Swagger UI: `http://localhost:8085/cnd-service/swagger-ui/index.html#/`


## Development

### Project Structure
```
src/main/java/org/upyog/cdwm/
├── config/           # Configuration classes
├── constants/        # Application constants
├── enums/           # Enumeration definitions
├── kafka/           # Kafka producers/consumers
├── notification/    # Notification services
├── repository/      # Data access layer
├── service/         # Business logic
├── util/           # Utility classes
├── validation/     # Input validation
└── web/            # REST controllers and models
```

### Code Quality
- **Lombok**: Reduces boilerplate code
- **Validation**: Jakarta Bean Validation
- **Security**: OWASP HTML Sanitizer
- **Logging**: SLF4J with Logback

## Monitoring & Operations

### Health Checks
- Spring Boot Actuator endpoints
- Database connectivity monitoring
- Kafka connectivity status

### Logging
- Structured logging with correlation IDs
- Request/response logging
- Error tracking and alerting

### Performance
- Connection pooling for database
- Async processing with Kafka
- Pagination for large datasets
- Caching for frequently accessed data

## Security

### Input Validation
- Bean validation annotations
- HTML sanitization for user inputs
- SQL injection prevention

### Authentication & Authorization
- Integration with UPYOG user service
- Role-based access control
- JWT token validation

## Deployment

### Production Checklist
- [ ] Database migrations tested
- [ ] Environment variables configured
- [ ] Kafka topics created
- [ ] SSL certificates installed
- [ ] Monitoring dashboards setup
- [ ] Backup procedures verified

### Environment Variables
```bash
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:postgresql://prod-db:5432/upyog
KAFKA_BOOTSTRAP_SERVERS=kafka-cluster:9092
REDIS_HOST=redis-cluster
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## Support

For technical support and documentation:
- **UPYOG Documentation**: [https://upyog.niua.org](https://upyog.niua.org)
- **Issue Tracker**: Create issues in the repository
- **Community**: Join UPYOG developer community

## License

This project is part of the UPYOG platform and follows the same licensing terms.

---

**Version**: 2.0.0
**Last Updated**: February 2025
**Maintainer**: NIUA UPYOG Team
