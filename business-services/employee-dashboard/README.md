# Employee Dashboard Service

## Overview
Employee Dashboard service provides analytics and reporting for UPYOG modules through REST APIs.

## Features
- **Module-wise Analytics**: Fetch data for specific modules
- **Application Metrics**: Applications received, pending, approved count
- **Financial Summary**: Total amount tracking
- **Multi-tenant Support**: Tenant-based data segregation
- **12 Module Support**: Including aggregated ALL view


## Technology Stack
- **Java**: 17
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL 42.7.1
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI 2.5.0
- **Database Migration**: Flyway 9.22.3
- **Validation**: Jakarta Validation API 3.1.1
- **JSON Processing**: Jackson JSR310 2.19.1
- **Code Generation**: Lombok 1.18.32


### Prerequisites
- Java 17+
- Maven 3.9+
- PostgreSQL 12+

### Configuration
Update `src/main/resources/application.properties`

#### Running the Application
```bash
mvn clean install
mvn spring-boot:run


### API Documentation
- **Swagger UI**: http://localhost:8089/employee-dashboard/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8089/employee-dashboard/v3/api-docs
