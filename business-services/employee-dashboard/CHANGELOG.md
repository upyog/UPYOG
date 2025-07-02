# Changelog

All notable changes to the Employee Dashboard Service will be documented in this file.

## [1.0.0] - 2024-12-19

### Added
- Initial release of Employee Dashboard Service
- Custom OpenAPI 3.0 configuration with SpringDoc
- Employee Dashboard API with comprehensive module support
- Database integration with PostgreSQL
- Kafka integration for real-time data processing
- Flyway database migration support

### Updated
- **Java Version**: Upgraded to Java 17
- **Spring Boot**: Upgraded to version 3.4.4
- **PostgreSQL Driver**: Updated to version 42.7.5
- **Flyway**: Updated to version 11.7.1
- **SpringDoc OpenAPI**: Updated to version 2.5.0
- **Lombok**: Updated to version 1.18.38
- **Jackson JSR310**: Updated to version 2.19.0-rc2
- **JUnit Jupiter**: Updated to version 5.10.1
- **Mockito**: Updated to version 5.11.0

### Changed
- **API Documentation**: Migrated from Springfox Swagger 2 to SpringDoc OpenAPI 3.0
- **Server Configuration**: Added custom context path `/employee-dashboard`
- **Port Configuration**: Changed default port to 8089
- **Database Configuration**: Updated to use AWS RDS PostgreSQL instance
- **Project Structure**: Reorganized package structure under `org.upyog.employee.dasboard`

### Technical Improvements
- **Swagger Configuration**: Custom OpenAPI bean with server URL configuration
- **Database Migration**: Added V20241017050754__emoloyee.sql migration script
- **Error Handling**: Enhanced error response models
- **Testing**: Upgraded to JUnit 5 with Mockito integration
- **Build Configuration**: Updated Maven compiler plugin for Java 17

### Dependencies Added
- `org.egov.services:tracer:2.0.0-SNAPSHOT`
- `org.egov.services:digit-models:1.0.0-SNAPSHOT`
- `org.egov:mdms-client:0.0.2-SNAPSHOT`
- `jakarta.validation:jakarta.validation-api:3.1.1`

### Dependencies Removed
- Legacy Swagger 2 dependencies (replaced with OpenAPI 3)
- Older Spring Boot starters (upgraded to 3.x)

### Configuration Changes
- **Application Properties**: Updated server context path and port
- **Database URL**: Configured for AWS RDS instance
- **Flyway**: Configured with proper migration path
- **Swagger**: Custom OpenAPI configuration with server context

### Supported Modules
- OBPAS (Online Building Plan Approval System)
- ASSET (Asset Management)
- FSM (Faecal Sludge Management)
- PGR (Public Grievance Redressal)
- CHB (Community Health Block)
- PETSERVICES (Pet Services)
- TL (Trade License)
- EWASTE (E-Waste Management)
- WATER (Water Connection)
- PT (Property Tax)
- SEWERAGE (Sewerage Connection)
- ALL (Aggregated data for all modules)

---

## Version History

### Previous Versions
- **Pre-1.0.0**: Legacy implementation with Swagger 2, Spring Boot 2.x, Java 8
  - Used Springfox Swagger 2 for API documentation
  - Spring Boot version < 3.0
  - Java 8 compatibility
  - Basic PostgreSQL integration
  - Limited module support

### Migration Notes
- **Java 8 → Java 17**: Updated minimum Java version requirement
- **Spring Boot 2.x → 3.4.4**: Major framework upgrade with breaking changes
- **Swagger 2 → OpenAPI 3**: Complete API documentation migration
- **Database**: Enhanced PostgreSQL configuration with AWS RDS support
- **Testing**: Migrated from JUnit 4 to JUnit 5

---

*For more details about specific changes, refer to the commit history and pull requests.*