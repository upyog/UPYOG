# Changelog

All notable changes to the Request Service project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2024-01-15

### Added
- **Spring Boot 3.2.2 Migration**: Upgraded from Spring Boot 2.x to 3.2.2
- **Java 17 Support**: Updated to Java 17 as the minimum required version
- **SpringDoc OpenAPI Integration**: Added interactive API documentation with Swagger UI
- **Enhanced Security**: Integrated OWASP HTML Sanitizer for XSS protection
- **Validation Framework**: Added comprehensive request validation using Spring Boot Starter Validation
- **Database Migration**: Implemented Flyway for database schema management
- **Configuration Management**: Enhanced configuration classes for better modularity
- **Postman Collection**: Added API testing collection for development and testing

### Changed
- **Architecture Modernization**: Updated to modern Spring Boot 3.x architecture patterns
- **Dependency Updates**:
  - PostgreSQL driver updated to 42.7.1
  - SpringDoc OpenAPI 2.3.0 for Spring Boot 3.x compatibility
  - Jackson datatype JSR310 for improved date/time handling
- **Package Structure**: Reorganized packages for better separation of concerns
- **Model Classes**: Enhanced model classes with proper validation annotations
- **Controller Layer**: Improved REST controllers with better error handling
- **Service Layer**: Refactored business logic for better maintainability

### Enhanced
- **Water Tanker Booking**: Complete lifecycle management with scheduling and delivery tracking
- **Mobile Toilet Booking**: Full service management for reservations and maintenance
- **Search Capabilities**: Advanced search with multiple criteria and pagination
- **Workflow Integration**: Built-in workflow engine for approval processes
- **Notification System**: SMS and email notifications for booking updates
- **Payment Integration**: Integrated billing and payment processing
- **User Management**: Enhanced citizen and employee profile management

### Technical Improvements
- **Database Connection Pooling**: Optimized HikariCP configuration
- **Error Handling**: Improved error responses and exception handling
- **Logging**: Enhanced logging with tracer utilities
- **Performance**: Optimized database queries and service calls
- **Code Quality**: Added Lombok for reduced boilerplate code

### Configuration
- **Application Properties**: Comprehensive configuration management
- **Environment Variables**: Support for containerized deployments
- **Docker Support**: Added Dockerfile and docker-compose configuration
- **Kafka Integration**: Message streaming for asynchronous processing

### API Endpoints
- **Water Tanker Services**:
  - `POST /request-service/water-tanker/v1/_create`
  - `POST /request-service/water-tanker/v1/_search`
  - `POST /request-service/water-tanker/v1/_update`
- **Mobile Toilet Services**:
  - `POST /request-service/mobile-toilet/v1/_create`
  - `POST /request-service/mobile-toilet/v1/_search`
  - `POST /request-service/mobile-toilet/v1/_update`

### Dependencies
- **Core Dependencies**:
  - Spring Boot Starter Web
  - Spring Boot Starter JDBC
  - PostgreSQL Driver
  - Flyway Core
  - SpringDoc OpenAPI
  - Spring Boot Starter Validation
- **UPYOG Dependencies**:
  - DIGIT Models (1.0.0-SNAPSHOT)
  - Tracer (2.9.0-SNAPSHOT)
  - MDMS Client (2.9.0-SNAPSHOT)
- **Utility Dependencies**:
  - Lombok
  - Jackson JSR310
  - OWASP HTML Sanitizer

### Documentation
- **README.md**: Comprehensive project documentation
- **API Documentation**: Interactive Swagger UI at `/swagger-ui.html`
- **Postman Collection**: Complete API testing suite
- **Configuration Guide**: Detailed setup and configuration instructions

### Testing
- **Unit Tests**: Comprehensive test coverage for controllers and services
- **Integration Tests**: End-to-end testing for API endpoints
- **Test Configuration**: Separate test configurations and profiles

### Deployment
- **Docker Support**: Containerization with Dockerfile and docker-compose
- **Maven Build**: Optimized build configuration with Spring Boot Maven plugin
- **Environment Configuration**: Support for multiple deployment environments

## [1.x.x] - Legacy Versions

### Legacy Features
- Basic water tanker and mobile toilet booking functionality
- Spring Boot 2.x based architecture
- Java 8/11 compatibility
- Basic REST API endpoints
- PostgreSQL database integration

---

## Migration Guide

### From 1.x.x to 2.0.0

#### Prerequisites
- Upgrade Java to version 17 or higher
- Update Maven to 3.6+
- Ensure PostgreSQL 12+ compatibility

#### Breaking Changes
- **Java Version**: Minimum Java 17 required
- **Spring Boot**: Upgraded to 3.2.2 (breaking changes from 2.x)
- **API Documentation**: Moved from Springfox to SpringDoc OpenAPI
- **Configuration**: Some configuration properties have changed

#### Migration Steps
1. Update Java runtime to 17+
2. Update application.properties for Spring Boot 3.x compatibility
3. Review and update custom configurations
4. Run database migrations with Flyway
5. Update API documentation references
6. Test all endpoints with provided Postman collection

---

## Support

For questions and support:
- **Documentation**: Check `/swagger-ui.html` for API documentation
- **Issues**: Create GitHub issues for bugs or feature requests
- **Community**: UPYOG community forums
- **Contact**: cdg-contact@niua.org
- **Author**: Shivank-NUDM

---

**Note**: This changelog covers the major version 2.0.0 release. For detailed commit history, please refer to the Git repository.