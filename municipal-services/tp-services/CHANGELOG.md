# Changelog

All notable changes to the Tree Pruning Service (tp-services) project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-01-16

### Added

#### Core Service Features
- **Tree Pruning Booking Service**: Complete microservice for managing tree pruning applications
- **RESTful API Endpoints**:
  - `POST /tree-pruning/v1/_create` - Create new tree pruning booking
  - `POST /tree-pruning/v1/_search` - Search tree pruning bookings with pagination
  - `POST /tree-pruning/v1/_update` - Update existing booking applications
- **Swagger API Documentation**: SpringDoc OpenAPI 3 integration for interactive API documentation

#### Database Schema
- **Core Tables**:
  - `upyog_rs_tree_pruning_booking_detail` - Main booking information with geolocation support
  - `upyog_rs_tree_pruning_booking_detail_audit` - Audit trail for all booking changes
  - `upyog_rs_tree_pruning_document_detail` - Document attachment management
  - `upyog_rs_tree_pruning_applicant_details` - Applicant personal information
  - `upyog_rs_tree_pruning_address_details` - Detailed address information
- **Database Indexes**: Optimized indexes for booking number, tenant ID, and audit queries
- **Sequence Generator**: `seq_tree_pruning_booking_id` for unique booking ID generation

#### Workflow Integration
- **Complete Workflow States**:
  - `PENDING_FOR_APPROVAL` - Initial application review
  - `PAYMENT_PENDING` - Awaiting payment completion
  - `TEAM_ASSIGNMENT_FOR_VERIFICATION` - Field verification stage
  - `TEAM_ASSIGNMENT_FOR_EXECUTION` - Service execution stage
  - `TREE_PRUNING_SERVICE_COMPLETED` - Service completion
  - `REJECTED` - Application rejection
- **Role-Based Actions**: Support for CITIZEN, TP_CEMP, TP_VERIFIER, and TP_EXECUTION roles
- **State Transitions**: Configurable workflow with proper state management

#### Business Logic Services
- **TreePruningService**: Core business logic for CRUD operations
- **EnrichmentService**: Data enrichment and validation
- **WorkflowService**: Workflow state management and transitions
- **NotificationService**: SMS and email notification handling
- **UserService**: User management and profile integration
- **DemandService**: Billing and payment demand generation
- **CalculationService**: Fee calculation logic
- **PaymentService**: Payment processing integration

#### Integration Components
- **Kafka Integration**:
  - Producer for publishing booking events
  - Consumer for notification and payment updates
  - Topics: `create-tree-pruning-booking`, `update-tree-pruning-booking`
- **MDMS Integration**: Master data management service connectivity
- **ID Generation**: Automatic booking number generation with configurable format
- **Billing Service**: Integration with UPYOG billing system
- **User Service**: Integration with UPYOG user management
- **Notification System**: SMS and email notification support

#### Validation & Security
- **Request Validation**: Comprehensive input validation using Jakarta Validation
- **HTML Sanitization**: OWASP HTML sanitizer for security
- **Audit Logging**: Complete audit trail for all operations
- **Error Handling**: Standardized error responses with proper HTTP status codes

#### Configuration & Deployment
- **Spring Boot 3.2.2**: Latest Spring Boot framework
- **Java 17**: Modern Java runtime support
- **PostgreSQL**: Database connectivity with connection pooling
- **Flyway**: Database migration management
- **Maven Build**: Complete Maven configuration with dependencies
- **Docker Support**: Containerization ready configuration

#### Development Tools
- **Lombok**: Reduced boilerplate code with annotations
- **Jackson**: JSON serialization/deserialization
- **SpringDoc**: API documentation generation
- **Tracer**: Request tracing and monitoring support

#### Repository Layer
- **TreePruningRepository**: Data access abstraction
- **Query Builder**: Dynamic SQL query construction
- **Row Mappers**: Database result mapping to domain objects
- **Generic Repository**: Reusable repository patterns

#### Utility Components
- **TreePruningUtil**: Common utility functions
- **NotificationUtil**: Notification formatting and processing
- **UserUtil**: User-related utility functions
- **IdgenUtil**: ID generation utilities
- **MdmsUtil**: Master data utilities
- **UrlShortenerUtil**: URL shortening integration

#### Models & DTOs
- **Request/Response Models**: Complete API contract definitions
- **Domain Models**: Business entity representations
- **Search Criteria**: Flexible search parameter handling
- **Workflow Models**: Workflow state and action definitions
- **Notification Models**: SMS and email templates
- **Billing Models**: Demand and payment structures

### Technical Specifications
- **Framework**: Spring Boot 3.2.2
- **Java Version**: 17
- **Database**: PostgreSQL with Flyway migrations
- **Message Queue**: Apache Kafka
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven
- **Security**: OWASP HTML Sanitizer
- **Monitoring**: Actuator endpoints for health checks

### Configuration
- **Default Pagination**: 10 records per page, maximum 50
- **Workflow**: Enabled by default with configurable business service
- **Notifications**: SMS and email notifications enabled
- **ID Format**: `TP-[CITY.CODE]-[seq_tree_pruning_booking_id]`
- **Context Path**: `/tp-services`
- **Server Port**: 8081

### Database Migrations
- **V20250610050854**: Initial table creation for booking, audit, and document management
- **V20250710050857**: Added applicant and address detail tables with foreign key relationships

---

**Release Date**: January 16, 2025
**Version**: 2.0.0
**Build**: tree-pruning-services-2.0.0.jar