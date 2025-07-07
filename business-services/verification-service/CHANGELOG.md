# Changelog - Verification Service

All notable changes to the verification-service module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-07-01


### Changed
- Server context path set to `/verification-service`
- Default server port configured to 8080
- Application timezone set to UTC

### Technical Details
- Spring Boot with Jakarta EE annotations
- PostgreSQL driver for database connectivity
- Integration with multiple UPYOG modules via HTTP endpoints
- MDMS (Master Data Management Service) integration
- User management service integration
- JsonPath for JSON response parsing
- Custom exception handling with egov-tracer