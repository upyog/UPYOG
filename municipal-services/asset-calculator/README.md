# Asset Calculator Service

<<<<<<< HEAD
The **Asset Calculator Service** is a Spring Boot microservice designed to manage asset depreciation calculations and re-evaluations based on master data within the UPYOG ecosystem. This service provides automated depreciation processing, detailed asset depreciation tracking, and integration with other municipal services.

## Features

- **Depreciation Calculation**: Calculate and generate depreciation based on configured slabs and rates
- **Asset Depreciation Tracking**: Maintain detailed depreciation history for assets
- **Scheduled Processing**: Automated depreciation calculation through configurable schedulers
- **RESTful APIs**: Comprehensive API endpoints for depreciation operations
- **Database Integration**: PostgreSQL database with Flyway migrations
- **Kafka Integration**: Event-driven architecture with Kafka producers

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL
- **Migration**: Flyway
- **Message Queue**: Apache Kafka
- **Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- PostgreSQL database
- Apache Kafka
- Maven 3.6+

## Service Dependencies

The Asset Calculator service integrates with:
- **mdms-service**: Master data management
- **asset-service**: Asset registry operations
- **workflow-v2**: Workflow management
- **user-service**: User management

## API Endpoints

| **Endpoint** | **Method** | **Description** |
|--------------|------------|-----------------|
| `/v1/depreciation/_calculate` | POST | Calculate depreciation for assets |
| `/v1/depreciation/{assetId}/details` | GET | Get depreciation details for specific asset |


### Kafka Topics

#### Producers
- **`save-depreciation`**: Save depreciation records
- **`update-depreciation`**: Update depreciation records

## Database Schema

The service uses PostgreSQL with Flyway migrations located in:
- `src/main/resources/db/migration/main/db/`

Key tables:
- Asset depreciation tracking tables
- Configuration and master data tables

## Build and Run

### Build the Application
```bash
mvn clean compile
mvn package
```

### Run the Application
```bash
java -jar target/asset-calculator-2.0.0.jar
```

## API Documentation

Once the application is running, access the API documentation at:
- **Swagger UI**: `http://localhost:9093/asset-calculator/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:9093/asset-calculator/v3/api-docs`

## Development

### Project Structure
```
src/main/java/org/egov/asset/calculator/
├── config/              # Configuration classes
├── kafka/               # Kafka producers and consumers
├── repository/          # Data access layer
├── services/            # Business logic
├── utils/               # Utility classes
├── validator/           # Validation logic
└── web/                 # REST controllers and models
```

### Key Components
- **CalculatorController**: Main REST controller
- **ProcessDepreciation**: Depreciation processing service
- **CalculationService**: Core calculation logic
- **DepreciationScheduler**: Scheduled depreciation processing



## Monitoring and Health

The service includes Spring Boot Actuator endpoints for monitoring:
- Health check: `/asset-calculator/actuator/health`
- Metrics: `/asset-calculator/actuator/metrics`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Submit a pull request

## License

This project is part of the UPYOG platform and follows the same licensing terms.

## Support

For issues and support, please refer to the [UPYOG GitHub Repository](https://github.com/upyog/UPYOG).
=======
The **Asset Calculator Service** is designed to manage asset depreciation, re-evaluations etc based on master data  and calculate charges for assets within the ecosystem. This document provides details on how to set up and use the Asset Calculator service, along with its key functionalities and dependencies.

---

## Features
2. **Depreciation Calculation**:
    - Calculate and generate Depreciation based on configured slabs.
---

## Service Dependencies

The Asset Calculator service interacts with the following dependent services:
- **mdms-service**
- **workflow-v2**
- **user-service**
- **vendor**
- **vehicle**

---

## Database Schema

The database schema is designed to store billing slab details and related configurations. Refer to the UML diagram in `asset-calculator.png` for detailed database relationships.

---

## API Details

| **Endpoint**            | **Description**      |
|--------------------------|----------------------|
| `_calculate`             | Calculates           |

---

## Kafka Topics

### Producers

- **`xyz`**: Xyz.

---

## Configuration

### Server Settings
```properties
server.context-path=/asset-calculator

```


### Persister Topics
```properties
persister.save.billing.slab.topic=save-asset-depreciation-slab
```

### MDMS Service
```properties
egov.mdms.host=http://localhost:8094
egov.mdms.search.endpoint=/egov-mdms-service/v1/_search
```


### Asset Registry
```properties
egov.asset.create.endpoint=/_create
egov.asset.update.endpoint=/_update
egov.asset.search.endpoint=/_search
```

---

## Swagger API Contract

Access the Swagger API contract via the following links:
- [YAML File](https://raw.githubusercontent.com/egovernments/municipal-services/master/docs/fsm/Fsm_Apply_Contract.yaml)
- [Swagger Editor](https://editor.swagger.io/)

---

## Postman Collection

The Postman collection is available for testing the APIs:
- [Postman Collection](https://www.getpostman.com/collections/8b9eb951a810486f41a4)

---

## Future Enhancements

- Detailed documentation for logic (TBD).
- Advanced search and reporting capabilities for slabs.

---

For additional details or contributions, visit the [GitHub Repository](https://github.com/egovernments).
>>>>>>> master-LTS
