<<<<<<< HEAD
#Changelog

All notable changes to this module will be documented in this file.

## 2.0.0 - 2025-07-07

### Added
- Added Swagger/OpenAPI documentation using springdoc-openapi-starter-webmvc-ui
- Added Hibernate Validator for Spring Boot 3.x compatibility
- Added Jakarta servlet API support for Spring Boot 3.x compatibility

### Changed
- Upgraded Spring Boot version to 3.2.2
- Upgraded Java version to 17
- Upgraded Hibernate Validator to version 8.0.1.Final
- Upgraded JSoup to version 1.17.2
- Upgraded Lombok to version 1.18.32
- Upgraded PostgreSQL driver to version 42.7.1
- Migrated from jakarta.* to jakarta.* packages
- Migrated jakarta.validation to jakarta.validation
- Migrated jakarta.annotation to jakarta.annotation
- Updated SpringFox to SpringDoc OpenAPI for Spring Boot 3.x compatibility

### Fixed
- Resolved circular dependency between EwasteService and EwasteRequestValidator
- Fixed Jakarta EE migration issues (jakarta.servlet → jakarta.servlet)
- Fixed jakarta.annotation → jakarta.annotation migration
- Resolved dependency conflicts and version mismatches
- Fixed Maven compiler configuration for Java 17
- Removed SafeHtml annotations for compilation compatibility
- Fixed SpringFox to SpringDoc migration for API documentation

### Removed
- Removed SafeHtml annotations (not critical for functionality)
- Removed SpringFox dependencies (replaced with SpringDoc)

## 1.0.0 - Previous

- Base version with Java 8 and Spring Boot 2.x
=======

All notable changes to this module will be documented in this file.

## 1.0.0-beta - 2024-01-31

- Created ewaste service apis for creating, updating and searching application. This also consist of certificate that can downloaded by user.
>>>>>>> master-LTS
