# Adapter Metrics Extractor Service

This is the main executable Spring Boot service in the metrics ingestion pipeline. It interacts with the database to extract raw application and collection data, maps them to type-safe DTOs, and invokes the `dashboard-data-engine` pipeline to ingest them to the National Dashboard.

## Responsibilities
- **DB Configuration & Schedulers**: Defines database connection configs, schemas, and ShedLock schedulers.
- **Extraction**: Runs SQL queries for configured modules (e.g. Property Tax).
- **Mapping**: Maps SQL results into structured DTOs (e.g., using `PTRowmapper`).
- **REST Endpoints**: Exposes REST interfaces to manually trigger historical or daily data migrations.

## Directory Structure
```
dashboard-data-extractor
 ├── src/main/java/org/upyog/adapter/
 │    ├── pt/            # PT database constants, mappers, and extractors
 │    ├── controller/    # REST endpoints
 │    ├── repository/    # Database query summaries and tracking
 │    ├── service/       # Historical and daily ingestion orchestration services
 │    └── AdapterServiceApplication.java   # Spring Boot Main Class
 └── pom.xml
```

## Prerequisite
Make sure you compile and install the `dashboard-data-engine` library first:
```bash
cd ../dashboard-data-engine
mvn clean install
```

## How to Run & Build
1. Build the executable JAR:
   ```bash
   mvn clean package
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Tenant Hierarchy and Multiple ULBs Support

The extractor service natively handles dot-notation tenant IDs representing the geographical hierarchy (e.g., `state.ulb.region.ward`).
## Configuration Parameters Reference

The service can be configured via `application.properties` (or environment-specific files like `application-local.properties`):

| Configuration Key | Default Value | Description |
| :--- | :--- | :--- |
| `dashboard-data.timeout.enabled` | `true` | Enables or disables custom Feign client HTTP timeouts. |
| `dashboard-data.timeout.connect-ms` | `5000` | Connection timeout in milliseconds when timeout is enabled. |
| `dashboard-data.timeout.read-ms` | `15000` | Read timeout in milliseconds when timeout is enabled. |
| `dashboard-data.metric.ulb` | `""` | Fallback placeholder property for metric ULB initialization. |
| `state.level.tenant.id` | `pg` | State-level tenant ID fallback used for summary tracker records. |
| `dashboard-data.daily.catch-up-limit-days` | `7` | Maximum number of days allowed for daily catch-up before halting. |
| `dashboard-data.ingestion.batch-size` | `10` | Ingestion batch size for HTTP API payloads and database inserts. |

## egov-persister Integration Setup

To persist ingestion audit details and summary tracker dates into PostgreSQL via Kafka, ensure `egov-persister` has `dashboard-data-extractor-persister.yml` configured in its `application.properties`:

```properties
egov.persist.yml.repo.path=classpath:egov-pg-service-persister.yml,classpath:dashboard-data-extractor-persister.yml
```
