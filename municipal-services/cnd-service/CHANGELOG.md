# Changelog

All notable changes to the CND (Construction and Demolition) Service project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-02-12

### Added by @Shivank-NUDM
- **Complete CND Service Implementation**: Built comprehensive Construction and Demolition waste management system
- **Dual Application Types**:
    - `REQUEST_FOR_PICKUP`: Citizens can request waste pickup from construction sites
    - `DEPOSIT_TO_THE_CENTER`: Direct deposit of waste to designated centers
- **Multi-stage Workflow System**: Implemented complete workflow with role-based actions
    - Application submission by citizens/employees
    - Field inspector assignment and verification
    - Approval and payment processing
    - Vendor and vehicle assignment
    - Waste pickup execution and completion
- **Database Schema**: Created comprehensive database structure
    - `ug_cnd_application_details`: Main application table
    - `ug_cnd_waste_detail`: Waste type and quantity tracking
    - `ug_cnd_document_detail`: Document management
    - `ug_cnd_disposal_deposit_centre_detail`: Facility center information
    - Complete audit trail tables for all entities
- **API Endpoints**: Implemented RESTful APIs
    - `POST /v1/_create`: Create new CND applications
    - `POST /v1/_search`: Advanced search with multiple criteria
    - `POST /v1/_update`: Update existing applications
- **Integration Services**: Connected with UPYOG ecosystem
    - MDMS integration for master data management
    - User service integration for authentication
    - Workflow service for business process management
    - Billing service for payment processing
    - Notification service for SMS/Email alerts
- **Kafka Integration**: Event-driven architecture
    - Notification consumers for real-time messaging
    - Payment update consumers for transaction tracking
    - Async processing for better performance
- **Security Features**:
    - Input validation with Jakarta Bean Validation
    - HTML sanitization using OWASP sanitizer
    - SQL injection prevention
- **Documentation**:
    - OpenAPI 3.x specification with SpringDoc
    - Comprehensive API documentation
    - Postman collection for testing

### Enhancement

#### [1.2.0] - 2025-08-01
- **Applicant & Address Management**: Added dedicated tables for better data organization
    - `ug_cnd_applicant_details`: Separate applicant information storage
    - `ug_cnd_address_details`: Dedicated address management
    - Enhanced data integrity and normalization

#### [1.1.0] - 2025-08-01
- **User Type Tracking**: Added user type tracking for better audit trail
    - `created_by_usertype` column in main and audit tables
    - Enhanced tracking of who created/modified applications
    - Better role-based analytics and reporting

### Technical Improvements
- **Spring Boot 3.2.2**: Upgraded to latest Spring Boot version
- **Java 17**: Modern Java features and performance improvements
- **PostgreSQL 42.7.1**: Latest database driver with security patches
- **Flyway Migrations**: Automated database schema management
- **Maven Build System**: Standardized build and dependency management
- **Docker Support**: Containerization for easy deployment
- **Health Checks**: Spring Boot Actuator for monitoring
- **Structured Logging**: SLF4J with correlation IDs
- **Connection Pooling**: Optimized database performance
- **Pagination Support**: Efficient data retrieval for large datasets

### Configuration
- **Environment-specific Properties**: Configurable for different environments
- **Kafka Configuration**: Event-driven messaging setup
- **Service Integration URLs**: Configurable service endpoints
- **ID Generation**: Custom ID format `CND-[CITY.CODE]-[seq_cnd_application_id]`
- **Workflow Configuration**: Business service setup for `cnd`
- **Notification Settings**: SMS/Email notification configuration
- **URL Shortening**: Integration with URL shortener service
- **Security Settings**: Input validation and sanitization rules



### Documentation
- **Comprehensive README**: Complete project documentation
- **Database Schema**: ERD and table documentation
- **Workflow Documentation**: Business process flows
- **Postman Collection**: API testing collection

## [1.0.0] - 2024-02-18

### Initial Release
- **Project Bootstrap**: Initial Spring Boot project setup
- **Basic Database Schema**: Core tables for CND applications
- **Swagger Integration**: API documentation framework
- **Maven Configuration**: Build system setup
- **Basic Controller Structure**: REST API foundation

---

## Upcoming Features

### Planned Enhancements
- [ ] Mobile application support
- [ ] Real-time tracking dashboard
- [ ] Advanced analytics and reporting
- [ ] Integration with GIS systems
- [ ] Multi-language support
- [ ] Bulk operations support
- [ ] Advanced search filters
- [ ] Export functionality (PDF, Excel)
- [ ] Rate limiting and throttling
- [ ] Enhanced security features

### Technical Debt
- [ ] Convert application status to enum
- [ ] Implement caching layer
- [ ] Add more comprehensive validation
- [ ] Performance optimization
- [ ] Code coverage improvement
- [ ] Documentation updates

---

**Maintainer**: Shivank
**Project**: CND Service - UPYOG Platform
**Repository**: UPYOG Municipal Services
**License**: As per UPYOG platform licensing
