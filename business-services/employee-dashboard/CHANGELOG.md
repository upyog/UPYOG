# Changelog

All notable changes to the Employee Dashboard Service will be documented in this file.
## [2.0.0] - 2025-07-01 updated

### Added
- Custom OpenAPI 3.0 configuration with SpringDoc

### Updated
- **Java Version**: Upgraded to Java 17
- **Spring Boot**: Upgraded to version 3.2.2
- **PostgreSQL Driver**: Updated to version 42.7.1
- **Flyway**: Updated to version 9.22.3
- **SpringDoc OpenAPI**: Updated to version 2.5.0
- **Lombok**: Updated to version 1.18.32
- **Jackson JSR310**: Updated to version 2.19.1

### Changed
- **API Documentation**: Migrated from Springfox Swagger 2 to SpringDoc OpenAPI 3.0

### Dependencies Removed
- Legacy Swagger 2 dependencies (replaced with OpenAPI 3)
- Older Spring Boot starters (upgraded to 3.x)
- mdms-client dependency removed
- junit and mockito dependencies removed 

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
- **Spring Boot 2.x → 3.2.2**: Major framework upgrade with breaking changes
- **Swagger 2 → OpenAPI 3**: Complete API documentation migration

---

## [1.0.0] - 2024-12-19 created
- Initial release of Employee Dashboard Service
- Employee Dashboard API with comprehensive module support
- Database integration with PostgreSQL
- Kafka integration for real-time data processing
- Flyway database migration support