# Changelog

All notable changes to the GIS DX Service will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0-SNAPSHOT] - 2024-12-18

### Added
- Initial release of GIS DX Service
- GeoJSON data wrapper functionality for municipal services
- Support for both point and polygon geometries
- RESTful API endpoint `/gis-dx/v1/_search`
- Integration with eGov GIS Service
- Integration with eGov Collection Service
- Request correlation tracking with eGov tracer
- OpenAPI documentation with SpringDoc
- Configurable service endpoints

### Changed
- Upgraded from Java 8 to Java 17
- Upgraded to Spring Boot 3.2.2
- Updated to use Jakarta validation instead of javax
- Modernized dependency injection using Lombok @RequiredArgsConstructor
- Implemented Java 17 features (var keyword, modern syntax)

### Technical Details
- **Java Version**: 17
- **Spring Boot Version**: 3.2.2
- **Maven Compiler Plugin**: 3.11.0
- **Lombok Version**: 1.18.38
- **SpringDoc OpenAPI**: 2.3.0

### Dependencies
- `spring-boot-starter-web`: Web framework
- `tracer`: eGov request tracing (2.9.0-SNAPSHOT)
- `lombok`: Code generation (1.18.38)
- `jackson-databind`: JSON processing
- `springdoc-openapi-starter-webmvc-ui`: API documentation (2.3.0)

### Configuration
- Server port: 8290
- Context path: `/gis-dx-service`
- Timezone: Asia/Kolkata
- Logging level: INFO for org.egov.dx.gis

### API Features
- Generate GeoJSON FeatureCollection for any business service
- Support for ASSET, and other municipal services
- Tenant-based data filtering
- Geometry type selection (point/polygon)
- Comprehensive error handling
- Request validation using Jakarta validation

### Infrastructure
- Maven-based build system
- eGov Maven repositories integration
- Docker-ready application structure
- Comprehensive logging with correlation IDs

## [Unreleased]

### Planned
- Unit test coverage
- Integration tests
- Performance optimizations
- Additional geometry type support
- Enhanced error handling with custom exceptions
- Metrics and monitoring endpoints