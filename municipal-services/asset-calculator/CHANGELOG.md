# Changelog

<<<<<<< HEAD
All notable changes to the Asset Calculator Service will be documented in this file.

## [2.0.0] - 2025-09-16

**Author:** Shivank-NUDM

### 🚀 Major Upgrades

#### Framework & Dependencies
- **Spring Boot Upgrade**: Migrated from Spring Boot 2.x to **3.2.2**
  - *Why*: Enhanced performance, security improvements, and better Java 17 support
- **Java Version**: Upgraded to **Java 17** (LTS)
  - *Why*: Modern language features, improved performance, and long-term support
- **Jakarta EE Migration**: Transitioned from `javax.*` to `jakarta.*` packages
  - *Why*: Compliance with Jakarta EE standards and future-proofing

#### API Documentation
- **OpenAPI 3**: Replaced Swagger 2 with **SpringDoc OpenAPI 3** (v2.3.0)
  - *Why*: Better Spring Boot 3 integration and modern API documentation standards
- **Enhanced API Annotations**: Added comprehensive `@Operation` and `@Tag` annotations
  - *Why*: Improved API documentation and developer experience

#### Database & Persistence
- **JPA Enhancement**: Upgraded to latest Hibernate with Spring Boot 3 compatibility
- **PostgreSQL Driver**: Updated to version **42.7.1** for security patches
- **Flyway Integration**: Enhanced database migration management
  - *Why*: Better version control of database schema changes

#### Architecture Improvements
- **Depreciation Processing V2**: Introduced `ProcessDepreciationV2` service
  - *Why*: Enhanced depreciation calculation logic with better performance and accuracy
- **Dual Processing Support**: Maintains backward compatibility with V1 while offering V2 enhancements
- **Enhanced Scheduling**: Improved `@EnableScheduling` with configurable cron expressions
  - *Why*: Better control over automated depreciation calculations

#### Security & Validation
- **Hibernate Validator**: Updated to **8.0.1.Final**
  - *Why*: Enhanced validation capabilities and security improvements
- **Input Validation**: Strengthened `@Valid` annotations across all endpoints
  - *Why*: Better data integrity and security

#### Performance Optimizations
- **Connection Pooling**: Enhanced HikariCP configuration
  - *Why*: Better database connection management and performance
- **Batch Processing**: Improved batch size configuration for large-scale operations
  - *Why*: Efficient processing of bulk depreciation calculations

#### Developer Experience
- **Lombok Integration**: Enhanced with latest version for cleaner code
- **Comprehensive Logging**: Improved SLF4J logging with structured messages
  - *Why*: Better debugging and monitoring capabilities
- **TypeScript Generation**: Added Maven plugin for TypeScript type definitions
  - *Why*: Better frontend integration and type safety

### 🔧 Technical Changes

#### Configuration Updates
- Updated `application.properties` with Spring Boot 3 compatible settings
- Enhanced Kafka configuration for better message handling
- Improved database connection settings with timeout configurations

#### API Enhancements
- **RESTful Endpoints**: Maintained backward compatibility while adding new features
- **Response Handling**: Enhanced `ResponseInfoFactory` for consistent API responses
- **Error Handling**: Improved exception handling and error responses

#### Database Schema
- **Migration Scripts**: Added new Flyway migrations for schema updates
- **Entity Mapping**: Updated JPA entities for Jakarta EE compatibility

### 🐛 Bug Fixes
- Fixed depreciation calculation edge cases in legacy data processing
- Resolved timezone handling issues in date calculations
- Improved error handling for missing master data

### 📚 Documentation
- **Comprehensive README**: Added detailed setup and usage instructions
- **API Documentation**: Enhanced Swagger/OpenAPI documentation
- **Code Comments**: Improved inline documentation for better maintainability

### 🔄 Migration Notes
- **Breaking Changes**: Minimal breaking changes due to careful backward compatibility design
- **Configuration**: Update application properties for Spring Boot 3
- **Dependencies**: All dependencies updated to compatible versions

---

## [1.0.0] - 2023-XX-XX

### Initial Release
- Base version with core depreciation calculation functionality
- Spring Boot 2.x framework and Java 8
- Basic REST API endpoints
- PostgreSQL database integration
- Kafka message processing
=======

## 1.0.0

- Base version
>>>>>>> master-LTS
