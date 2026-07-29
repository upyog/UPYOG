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
