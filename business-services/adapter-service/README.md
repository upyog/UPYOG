# Adapter Service

This service transforms property-tax data from upstream sources into the payload structure expected by the national dashboard ingest endpoint.

## What it does
- Extracts property, assessment, and unit data for a tenant and reporting date.
- Applies schema-based transformation and normalization rules.
- Produces module payloads for downstream ingestion.
- Authenticates outbound calls using OAuth and user-context lookups.

## Main components
- `src/main/java/org/upyog/as/core/extractor/impl/GenericExtractor.java` executes YAML-defined datasets.
- `src/main/java/org/upyog/as/core/transformer/impl/PTTransformer.java` builds the module payload.
- `src/main/java/org/upyog/as/common/CommonLoader.java` sends the payload to the ingest endpoint.
- `src/main/java/org/upyog/as/service/OAuthTokenService.java` manages authentication.

## Build and run
```bash
./mvnw clean package
./mvnw spring-boot:run
```

## Configuration
Set the required properties in the application configuration before running the service, including the datasource, OAuth endpoints, and national dashboard ingest URL.
