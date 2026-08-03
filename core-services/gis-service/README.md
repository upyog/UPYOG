# GIS Service

A Spring Boot microservice for Geographic Information System (GIS) operations on municipal services data. This service provides spatial data extraction and normalization capabilities for various municipal services like Property Tax (PT), Assets, Trade License (TL), and more.

## Features

- **Multi-Service Support**: Generic adapter pattern supporting multiple municipal services
- **Spatial Data Operations**: Extract point and polygon geometries from municipal service data
- **RESTful APIs**: Clean REST endpoints for spatial data operations
- **Swagger Documentation**: Interactive API documentation with OpenAPI 3
- **Configurable**: Easy configuration for different municipal service endpoints

## Technology Stack

- **Java**: 17+
- **Spring Boot**: 3.2.2
- **SpringDoc OpenAPI**: 2.3.0 (Swagger UI)
- **Maven**: 3.6+
- **Lombok**: 1.18.32

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Access to municipal service endpoints (Property Service, Asset Service, etc.)

## Quick Start

### 1. Clone and Build
```bash
git clone <repository-url>
cd gis-service
mvn clean install
```

### 2. Configuration
Update `src/main/resources/application.properties`:
```properties
server.port=8292
server.servlet.context-path=/gis-service

# Property Service Configuration
egov.property.host=http://localhost:8280
egov.property.search.endpoint=/property-services/property/_search

# Asset Service Configuration
egov.asset.host=http://localhost:8281
egov.asset.search.endpoint=/asset-services/v1/assets/_search
```

### 3. Run the Service
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8292/gis-service`

## API Documentation

### Swagger UI
Access interactive API documentation at:
```
http://localhost:8292/gis-service/swagger-ui/index.html
```

### API Endpoints

#### Get Available Business Services
```http
GET /gis/v1/business-services
```

#### Search Entities with Point Geometry
```http
POST /gis/v1/{businessService}/points/_search
Content-Type: application/json

{
  "tenantId": "pg.citya",
  "businessService": "ASSET",
  "requestInfo": {
    "apiId": "gis-service",
    "ver": "1.0",
    "ts": 1734516543000,
    "action": "search",
    "userInfo": {
      "type": "EMPLOYEE",
      "roles": [{"code": "SUPERUSER", "tenantId": "pg.citya"}]
    }
  },
  "searchCriteria": {}
}
```

#### Search Entities with Polygon Geometry
```http
POST /gis/v1/{businessService}/polygons/_search
```
Same request format as points search.

### Supported Business Services
- **ASSET**: Asset Management Service
- **PT**: Property Tax Service
- **TL**: Trade License Service
- More services can be added via adapter pattern

## Configuration

### Service Endpoints
Configure external service endpoints in `application.properties`:
```properties
# Property Service
egov.property.host=http://localhost:8280
egov.property.search.endpoint=/property-services/property/_search

# Asset Service
egov.asset.host=http://localhost:8281
egov.asset.search.endpoint=/asset-services/v1/assets/_search
```

### Logging
```properties
logging.level.org.egov.gis=INFO
logging.level.org.springframework.web=DEBUG
```

## Architecture

### Adapter Pattern
The service uses an adapter pattern to support multiple municipal services:

```
GisController → GisService → ServiceRegistry → MunicipalServiceAdapter
                                            ├── AssetServiceAdapter
                                            ├── PropertyServiceAdapter
                                            └── [Other Service Adapters]
```

### Key Components
- **GisController**: REST API endpoints
- **GisService**: Business logic for spatial operations
- **ServiceRegistry**: Manages service adapters
- **MunicipalServiceAdapter**: Interface for service-specific implementations
- **AssetServiceAdapter**: Asset service implementation
- **PropertyServiceAdapter**: Property service implementation

## Development

### Adding New Service Support
1. Create a new adapter implementing `MunicipalServiceAdapter`
2. Register the adapter in `ServiceRegistry`
3. Add service configuration properties

### Testing
```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Deployment

### Docker
```dockerfile
FROM openjdk:17-jre-slim
COPY target/gis-service-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8292
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables
- `SERVER_PORT`: Service port (default: 8292)
- `EGOV_PROPERTY_HOST`: Property service host
- `EGOV_ASSET_HOST`: Asset service host

## Migration from Previous Versions

### Swagger URL Change
- **Old**: `http://localhost:8292/gis-service/swagger-ui.html`
- **New**: `http://localhost:8292/gis-service/swagger-ui/index.html`

### Java Version
- Minimum Java version upgraded from 8/11 to 17

## Troubleshooting

### Common Issues
1. **Service not starting**: Check Java 17+ is installed
2. **Swagger UI not accessible**: Use new URL path `/swagger-ui/index.html`
3. **External service errors**: Verify service endpoints and authentication

### Logs
Check application logs for detailed error information:
```bash
tail -f logs/gis-service.log
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions, please contact the development team or create an issue in the repository.