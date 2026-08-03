# Changelog

All notable changes to the GIS Service will be documented in this file.

## [1.0.0] - 2024-12-18

### Added
- Initial GIS Service implementation for municipal services
- Support for point and polygon geometry extraction
- Generic adapter pattern for multiple municipal services (ASSET, PT, TL, etc.)
- RESTful APIs for spatial data operations
- Swagger/OpenAPI 3 documentation

### Changed
- **BREAKING**: Migrated from Spring Boot 2.x to Spring Boot 3.2.2
- **BREAKING**: Upgraded to Java 17 (minimum requirement)
- **BREAKING**: Replaced Springfox Swagger 2 with SpringDoc OpenAPI 3
- **BREAKING**: Updated Swagger UI URL from `/swagger-ui.html` to `/swagger-ui/index.html`
- Updated Maven compiler plugin to version 3.11.0
- Updated Lombok to version 1.18.32

### Technical Details
- Spring Boot: 3.2.2
- Java: 17
- SpringDoc OpenAPI: 2.3.0
- Maven Compiler Plugin: 3.11.0
- Lombok: 1.18.32

### Migration Notes
- Update Swagger URL bookmarks to use new path
- Ensure Java 17+ is installed before deployment
- No database schema changes required
- Configuration properties remain compatible

### API Endpoints
- `GET /gis/v1/business-services` - Get available business services
- `POST /gis/v1/{businessService}/points/_search` - Search entities with point geometry
- `POST /gis/v1/{businessService}/polygons/_search` - Search entities with polygon geometry

### Documentation
- Swagger UI: `http://localhost:8292/gis-service/swagger-ui/index.html`
- API Docs: `http://localhost:8292/gis-service/v3/api-docs`