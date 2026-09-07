# Adapter Engine Library

This library contains the core logic for translating, validating, and loading metrics to the National Dashboard. It is packaged as a standard Maven JAR dependency to be consumed by other service modules (such as `dashboard-data-extractor`).

## Responsibilities
- **Transformation**: Maps raw DTO payloads to specific target metrics classes (e.g. `PTMetric`).
- **Validation**: Enforces data shape and value rules on metrics prior to ingestion.
- **Loading**: Transports data over HTTP to the National Dashboard endpoint and pushes ingestion audits via Kafka.

## Directory Structure
```
dashboard-data-engine
 ├── src/main/java/org/upyog/adapter/
 │    ├── api/           # Client executables
 │    ├── entity/        # Ingestion audit POJOs
 │    ├── loader/        # Loaders (HTTP, Kafka)
 │    ├── model/         # General dashboard models
 │    ├── pt/            # Property Tax specific DTOs, metrics models, and transformers
 │    ├── service/       # OAuth token fetchers
 │    └── validator/     # Validations (Common & module specific)
 └── pom.xml
```

## How to Build & Install
Since this is a library, you need to compile and install it to your local Maven repository (`~/.m2`) so sibling projects can import it:
```bash
mvn clean install
```
