
# dashboard-analytics

DSS Analytics Module is used to return aggregated data from elastic search indexes which is displayed on UI to gain meaningful insights from the data

## Requirements

- **Java 17** or higher
- Maven 3.6+
- Elasticsearch 6.2.4+
- PostgreSQL (optional, for MDMS integration)

## Java 17 Migration

This service has been upgraded from Java 8 to Java 17 with Spring Boot 3.2.2. Key changes include:

### Breaking Changes
- **Java Version**: Now requires Java 17 minimum
- **Spring Boot**: Upgraded to 3.2.2 (from 2.x)
- **Servlet API**: Migrated from javax.servlet to jakarta.servlet
- **Annotations**: Migrated from javax.annotation to jakarta.annotation

### Updated Dependencies
- Spring Boot: 3.2.2
- Cache2k: 2.6.1.Final
- Jakarta WS-RS API: 3.1.0
- SpringDoc OpenAPI: 2.3.0
- Spring Retry: Added for resilience

### Compatibility
- API endpoints remain backward compatible
- Configuration files unchanged
- External service integrations preserved

## DSS Analytics 
The analytics service creates/wraps queries based on the configuration provided and executes it on the elastic search to fetch the aggregated data.
This aggregated data is then transformed to AggregateDTO by Response Handlers. AggregateDTO contains list of object called Plots. This plot object are created based on the chart type defined in the configuration. The module also provides functionality to compare the data with previous time period,which can be configured 
by defining insight in the chartAPI configuration for the required chart.

## Project Structure 
*Packages*
 - constant - Contains all the constant values
 - controller - Controllers for the app.
 - dao - DAO layer
 - dto - contains POJO's which are used to manage data returned from queries
 - enums - Enum definitions
 - exception - POJO for the module.
 - handler - Reponse Handlers which converts the aggregated data to AggregationDTO object based on chart type defined
 - helper - Helper classes to do computations on data
 - model - POJO's related to chart config
 - org.service - Consists of all services containing the business logic.
 - query.model - POJO's related to building query
 - repository - Fetches data from elastic search
 - service - Implementations of services which help with fetching data
 - utils - Contains utility functions.

## Build & Run

### Prerequisites
Ensure Java 17 is installed:
```bash
java -version
# Should show Java 17.x.x
```

### Build
```bash
mvn clean install
```

### Run
```bash
java -jar target/analytics-2.9.0-SNAPSHOT.jar
```

### Docker
```dockerfile
FROM openjdk:17-jre-slim
COPY target/analytics-2.9.0-SNAPSHOT.jar app.jar
EXPOSE 8280
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Configuration

### Application Properties
- **Port**: 8280 (default)
- **Context Path**: /dashboard-analytics
- **Database**: PostgreSQL (optional)
- **Elasticsearch**: Required for data aggregation

### Environment Variables
```bash
JAVA_OPTS="-Xmx1g -Xms512m"
SERVER_PORT=8280
ELASTICSEARCH_HOST=localhost:9200
```

## API Documentation

- **Swagger UI**: http://localhost:8280/dashboard-analytics/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8280/dashboard-analytics/v3/api-docs

## Resources
- Granular details about the API's can be found in the [swagger api definition](https://raw.githubusercontent.com/upyog/UPYOG/master/business-services/Docs/dss-dashboard/DSS%20Analytics%20Dashboard%20YAML%20Spec%201.0.0.yaml)
- Postman collection for all the API's can be found in the [postman collection](https://api.postman.com/collections/23419225-27e44c83-6e4b-4308-b231-1fb02ccb57eb?access_key=PMAT-01GTKJBA7MF0KNQJ8MJ4WAF9G1)

## Dependencies

- **Elasticsearch**: Database to fetch data from index
- **MDMS Service**: Master data management (optional)
- **Tracer Service**: For distributed tracing
- **Cache2k**: For performance optimization

## Troubleshooting

### Common Issues
1. **Java Version**: Ensure Java 17+ is installed
2. **Memory**: Increase heap size for large datasets
3. **Elasticsearch**: Verify connection and index availability
4. **MDMS**: Service continues gracefully if MDMS unavailable

### Logs
```bash
# Enable debug logging
logging.level.com.tarento.analytics=DEBUG
```
