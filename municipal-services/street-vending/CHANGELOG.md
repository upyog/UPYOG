# Changelog

All notable changes to the Street Vending service will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2024-12-19

### Added
- **Complete Street Vending Management System** - Full implementation for managing street vendors as per Street Vendors Act, 2014
- **Vendor Registration Module** - Create/update vendor profiles with Aadhaar, phone, address, vending type
- **Document Management** - Attach supporting documents with HTML sanitization
- **Draft Management** - Save, update, and delete draft applications
- **Workflow Integration** - Complete workflow support with status tracking (APPLIED, VERIFIED, APPROVED, REJECTED)
- **Certificate Generation** - Automated certificate issuance after approval with unique certificate numbers
- **Renewal System** - Certificate renewal with multiple scenarios and expiry tracking
- **Payment Integration** - Demand creation and billing service integration
- **Notification System** - SMS, email, and user event notifications
- **Distributed Scheduler** - ShedLock-based expiry checking with database locking
- **Security Features** - HTML sanitization, field encryption, input validation
- **API Documentation** - SpringDoc OpenAPI integration

### Technical Enhancements
- **Spring Boot 3.2.2** - Upgraded to latest Spring Boot version with Jakarta EE namespace
- **Java 17** - Modern Java runtime with LTS support
- **ShedLock 4.44.0** - Distributed lock management for scheduler coordination
- **OWASP HTML Sanitizer 20240325.1** - Input sanitization for security
- **SpringDoc OpenAPI 2.3.0** - Modern API documentation (replaces Springfox)
- **PostgreSQL 42.7.1** - Database connectivity
- **Package Structure** - Updated to `org.upyog.sv` namespace

### Infrastructure Changes
- **Port Configuration** - Service now runs on port 8085 (changed from 8080)
- **Context Path** - Updated to `/sv-services`
- **Database Integration** - PostgreSQL with optional Flyway migrations
- **Timezone Support** - Configurable application timezone (UTC default)

### API Endpoints
- `POST /sv-services/street-vending/_create` - Create new vendor application
- `POST /sv-services/street-vending/_update` - Update existing application
- `POST /sv-services/street-vending/_search` - Search vendors with filters
- `POST /sv-services/street-vending/_deletedraft` - Delete saved drafts
- `POST /sv-services/street-vending/_createdemand` - Create renewal demands
- `GET /sv-services/street-vending/trigger-expire-streetvendingapplications` - Manual expiry trigger

### Kafka Integration
#### Producer Topics
- `create-street-vending` - Create vendor
- `update-street-vending` - Update vendor
- `create-draft-street-vending` - Save draft
- `update-draft-street-vending` - Update draft
- `delete-draft-street-vending` - Delete draft
- `create-payment-schedule` - Create payment schedule
- `update-payment-schedule` - Update payment schedule

#### Consumer Topics
- Notification processing on create/update operations
- Payment update event handling via `PaymentUpdateConsumer`

### ID Generation System
- **Application Number**: `SV-[CITY.CODE]-[seq_street_vending_application_id]`
- **Certificate Number**: `SV-CT-[seq_street_vending_certificate_no]`
- **Receipt Number**: `SV/[CITY.CODE]/[fy:yyyy-yy]/[SEQ_EGOV_COMMON]`

### Service Dependencies
- egov-user, egov-mdms, egov-mdms-v2
- egov-idgen, egov-workflow-v2, egov-filestore
- egov-localization, egov-url-shortening
- egov-enc-service, egov-demand, egov-billing-service
- egov-notification-sms, egov-user-event, pdf-service

### Security Enhancements
- **HTML Sanitization** - All user inputs sanitized using OWASP Java HTML Sanitizer
- **Custom Validation** - `@SanitizeHtml` annotation for automatic input cleaning
- **Field Encryption** - Optional encryption for sensitive data fields via egov-enc-service
- **Input Validation** - Spring Boot Validation framework integration

### Scheduler Features
- **Distributed Locking** - ShedLock prevents concurrent execution across instances
- **Database-based Coordination** - Shared database for lock management using JDBC Template
- **Configurable Lock Duration** - Default 30-minute lock timeout (`PT30M`)
- **Expiry Management** - Automated certificate expiry checking and workflow triggers
- **Manual Trigger** - REST endpoint for manual expiry workflow execution

### MDMS Integration
- **Dual MDMS Support** - Both v1 and v2 MDMS integration
- **Configurable Version** - Switch between MDMS versions via `upyog.mdms.v2.enabled`
- **Cache Management** - MDMS data caching for performance optimization

### Renewal Workflow
- **Initial Application** → REGISTRATIONCOMPLETED
- **Scheduler Marks for Renewal** → ELIGIBLE_TO_RENEW (2 months before expiry)
- **Direct Renewal** → RENEWED (payment without editing)
- **Renewal with Edit** → RENEW_APPLICATION_CREATED
- **Expired Application** → EXPIRED with ELIGIBLE_TO_RENEW
- **Renewal In Progress** → RENEW_IN_PROGRESS

### Configuration Management
- **Environment-specific Properties** - Externalized configuration
- **Database Configuration** - PostgreSQL connection with credentials
- **Kafka Configuration** - Producer/consumer settings
- **Service URLs** - Configurable endpoints for all dependent services
- **Feature Flags** - Toggle encryption, notifications, scheduler

---

## Migration Guide

### Upgrading to 2.0.0
This is the initial production release. For new installations:

#### Prerequisites
- Java 17 or higher
- PostgreSQL 12+
- Kafka cluster
- Maven 3.6+

#### Configuration Steps
1. Update `application.properties` with environment settings
2. Configure database connection (default port 8085)
3. Set up Kafka topics for event processing
4. Configure dependent services (egov-user, egov-workflow-v2, etc.)
5. Run database migrations if Flyway is enabled
6. Ensure ShedLock table exists for distributed locking

#### Breaking Changes
- **Port Change**: Service runs on port 8085 instead of 8080
- **Package Structure**: Updated from `org.egov` to `org.upyog.sv`
- **Spring Boot 3.x**: Jakarta EE namespace changes
- **Java 17**: Minimum Java version requirement

## Key Changes Made

### 1. Framework Modernization
- **Spring Boot 2.x → 3.2.2**: Major framework upgrade
- **Java 8/11 → Java 17**: Latest LTS Java version
- **Springfox → SpringDoc OpenAPI**: Modern API documentation
- **javax → jakarta**: Namespace migration for Jakarta EE

### 2. Architecture Improvements
- **Package Restructuring**: Moved from `org.egov` to `org.upyog.sv`
- **Distributed Processing**: Added ShedLock for multi-instance coordination
- **Security Hardening**: OWASP HTML sanitizer integration
- **Validation Framework**: Custom validation annotations

### 3. Infrastructure Changes
- **Port Configuration**: Changed default port from 8080 to 8085
- **Database Integration**: Enhanced PostgreSQL configuration
- **Kafka Topics**: Added payment schedule management topics
- **Service Discovery**: Updated service endpoint configurations

### 4. Feature Enhancements
- **MDMS v2 Support**: Dual MDMS version compatibility
- **Encryption Service**: Optional field-level encryption
- **Scheduler Service**: Distributed expiry management
- **Payment Workflows**: Enhanced renewal scenarios

### 5. Developer Experience
- **API Documentation**: Interactive Swagger UI
- **Configuration Management**: Externalized properties
- **Error Handling**: Global exception handling
- **Logging**: Enhanced tracing and monitoring

## Support

For technical support, bug reports, or feature requests:
- Review API documentation at `/sv-services/swagger-ui.html`
- Check configuration guide in README.md
- Contact development team for assistance

---

**Note**: This changelog will be updated with each release to track all changes, improvements, and bug fixes.