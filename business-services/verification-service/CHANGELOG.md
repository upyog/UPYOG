# Changelog - Verification Service

All notable changes to the verification-service module will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.1.0] - 2025-07-01


### Added
- Initial verification service implementation
- REST API endpoint for application validity search (`/validity/_search`)
- Support for multiple module verification (advertisement, property, tradelicense, streetvending, pet, ewaste, communityhall, firenoc, bpa, sewerage)
- PostgreSQL database integration with AWS RDS connection
- User service integration for UUID fetching based on mobile numbers
- Common service for retrieving application details across modules
- Request/Response models for verification operations
- Maven-based Spring Boot project structure
- JUnit test suite setup for controller testing

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