# Employee Dashboard Service

UPYOG Employee Dashboard - Spring Boot Application

## Overview
Employee Dashboard service provides comprehensive analytics and reporting for various UPYOG modules. This service aggregates data from multiple modules and presents it through REST APIs for dashboard consumption.

## Technology Stack
- **Java**: 17
- **Spring Boot**: 3.4.4
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI 3.0
- **Migration**: Flyway
- **Message Queue**: Apache Kafka

## Features
- Module-wise dashboard data aggregation
- Real-time data processing via Kafka
- RESTful API endpoints
- Database migration support
- Comprehensive error handling
- OpenAPI 3.0 documentation with custom configuration

## Supported Modules
OBPAS, ASSET, FSM, PGR, CHB, PETSERVICES, TL, EWASTE, WATER, PT, SEWERAGE, ALL

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka (optional)

### Configuration
Update `src/main/resources/application.properties`:
```properties
server.port=8089
server.contextPath=/employee-dashboard

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Running the Application
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### API Documentation
Access the API documentation at:
- **Swagger UI**: http://localhost:8089/employee-dashboard/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8089/employee-dashboard/v3/api-docs
- **API Title**: Employee Dashboard API v1.0

> **Note**: The application uses custom OpenAPI 3.0 configuration with server context path setup.

## Project Structure
```
src/main/java/org/upyog/employee/dasboard/
├── config/          # Configuration classes
├── kafka/           # Kafka producers and consumers
├── query/           # Database queries and constants
├── repository/      # Data access layer
├── service/         # Business logic
├── util/            # Utility classes
├── web/             # REST controllers and models
└── Main.java        # Application entry point
```

## Database Migration
Flyway migrations are located in `src/main/resources/db/migration/main/`

## API Configuration
- **Base Path**: `/employee-dashboard`
- **API Version**: 1.0
- **Documentation**: Custom OpenAPI 3.0 setup
- **Server URL**: Configured with context path

## Dependencies
- **Spring Boot**: Web, JDBC, Kafka starters
- **Database**: PostgreSQL 42.7.5, Flyway 11.7.1
- **Documentation**: SpringDoc OpenAPI 2.5.0
- **eGov Framework**: Tracer, DIGIT Models, MDMS Client
- **Utilities**: Lombok 1.18.38, Jackson JSR310
- **Testing**: JUnit 5, Mockito, Spring Boot Test