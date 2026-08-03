# Changelog

All notable changes to the Apply Workflow Utility will be documented in this file.

The format is based on Keep a Changelog
and this project adheres to Semantic Versioning.

---

## [1.0.0] - 2026-05-07

### Added
- Initial implementation of Apply Workflow Utility
- Workflow Create functionality
- Workflow Update functionality
- MDMS integration for workflow configuration retrieval
- Workflow search integration with workflow-v2
- Workflow merge utility for states and actions
- REST API endpoint for workflow processing
- PostgreSQL integration using Spring Data JPA
- Swagger/OpenAPI documentation support
- Flyway database migration support

### Changed
- Upgraded project from Java 8 to Java 17
- Upgraded Spring Boot from 2.x to 3.4.4
- Migrated javax packages to jakarta packages
- Replaced Swagger 2 annotations with OpenAPI 3 annotations
- Upgraded MapStruct compatibility for Java 17
- Updated Maven Compiler Plugin to support Java 17
- Updated PostgreSQL driver version
- Updated Lombok version

### Fixed
- Fixed MapStruct implementation duplication issue
- Fixed WorkflowMapper generation conflict
- Fixed Spring Boot 3 compatibility issues
- Fixed Jakarta validation imports
- Fixed OpenAPI annotation compatibility
- Fixed compilation issues with Java 17

### Removed
- Removed manual WorkflowMapperImpl implementation
- Removed deprecated Swagger 2 dependencies
- Removed incompatible Java 8 configurations