# GIS DX Service

A Spring Boot microservice that provides GeoJSON data wrapper functionality for municipal services in the UPYOG platform.

## Overview

The GIS DX Service acts as a data transformation layer that converts municipal service data into GeoJSON format for mapping and visualization purposes. It integrates with the core GIS service and collection services to provide comprehensive geospatial data.

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.2
- **Maven**: 3.x
- **Lombok**: 1.18.38
- **Jackson**: For JSON processing
- **SpringDoc OpenAPI**: 2.3.0 (API documentation)

## Features

- Generate GeoJSON data for any municipal service
- Support for both point and polygon geometries
- Integration with eGov tracer for request tracking
- RESTful API with OpenAPI documentation
- Configurable service endpoints

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Access to eGov GIS Service
- Access to eGov Collection Service (optional)

## Configuration

### Application Properties

```properties
# Server Configuration
server.port=8290
server.servlet.context-path=/gis-dx-service
app.timezone=Asia/Kolkata

# GIS Service Configuration
egov.gis.host=http://localhost:8291
egov.gis.points.search.endpoint=/gis-service/gis/v1/{businessService}/points/_search
egov.gis.polygons.search.endpoint=/gis-service/gis/v1/{businessService}/polygons/_search

# Collection Service Configuration  
egov.collection.host=http://localhost:8095
egov.collection.payment.search.endpoint=/collection-services/payments/{moduleName}/_search

# Logging
logging.level.org.egov.dx.gis=INFO
```

## API Endpoints

### Search GeoJSON Data

**POST** `/gis-dx/v1/_search`

Generates GeoJSON data for a specified municipal service.

#### Request Body

```json
{
  "businessService": "ASSET",
  "tenantId": "pg.citya",
  "geometryType": "polygon",
  "searchCriteria": {},
  "requestInfo": {
    "apiId": "Rainmaker",
    "msgId": "1765952490964|en_IN",
    "authToken": "your-auth-token",
    "userInfo": {
      "id": 4928,
      "userName": "GIS_USER",
      "tenantId": "pg.citya",
      "roles": [...]
    }
  }
}
```

#### Response

```json
{
  "type": "FeatureCollection",
  "features": [
    {
      "type": "Feature",
      "geometry": {
        "type": "Polygon",
        "coordinates": [...]
      },
      "properties": {
        "id": "asset-id",
        "businessService": "ASSET",
        "tenantId": "pg.citya"
      }
    }
  ]
}
```

## Building and Running

### Build the Application

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Package the Application

```bash
mvn clean package
```

### Run the Application

```bash
java -jar target/gis-dx-service-1.0.0-SNAPSHOT.jar
```

Or using Maven:

```bash
mvn spring-boot:run
```

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── org/egov/dx/gis/
│   │       ├── config/          # Configuration classes
│   │       ├── models/          # Data models
│   │       ├── repository/      # Data access layer
│   │       ├── service/         # Business logic
│   │       └── web/            # REST controllers
│   └── resources/
│       └── application.properties
```

### Key Components

- **GisDxController**: Main REST controller handling GeoJSON requests
- **GisDxService**: Core business logic for data transformation
- **GeoJsonRequest/Response**: Request and response models
- **SwaggerConfiguration**: API documentation configuration

### Code Quality

The project follows Spring Boot 3.x best practices:
- Constructor-based dependency injection with Lombok
- Jakarta validation for request validation
- Proper error handling and logging
- Modern Java 17 features (var, text blocks)

## API Documentation

Once the application is running, access the interactive API documentation at:

```
http://localhost:8290/gis-dx-service/swagger-ui.html
```

## Monitoring and Logging

The service integrates with eGov tracer for:
- Request correlation tracking
- Tenant-based logging
- Performance monitoring

Log format includes:
- Timestamp
- Log level
- Tenant ID
- Correlation ID
- Message

## Dependencies

### Core Dependencies

- `spring-boot-starter-web`: Web framework
- `tracer`: eGov request tracing
- `lombok`: Code generation
- `jackson-databind`: JSON processing
- `springdoc-openapi-starter-webmvc-ui`: API documentation

### Repositories

The project uses eGov Maven repositories:
- Releases: https://nexus-repo.egovernments.org/nexus/content/repositories/releases/
- Snapshots: https://nexus-repo.egovernments.org/nexus/content/repositories/snapshots/

## Contributing

1. Follow Java 17 coding standards
2. Use Lombok annotations for boilerplate code
3. Maintain proper logging with correlation IDs
4. Update API documentation for new endpoints
5. Write unit tests for new functionality

## Version History

- **1.0.0-SNAPSHOT**: Initial release with Java 17 and Spring Boot 3.2.2 support

## Support

For issues and questions, please refer to the UPYOG platform documentation or contact the development team.