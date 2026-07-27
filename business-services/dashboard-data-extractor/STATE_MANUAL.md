# State Manual for Ingestion Pipeline

## Architecture Overview

The Ingestion Pipeline is responsible for extracting, transforming, validating, and loading metrics from local state services into the National Dashboard. It is split into two primary components:

1. **dashboard-data-extractor**: Contains module-specific extractor logic that fetches metrics data from database/APIs and passes it to the generic transformation layer. Includes `DailyIngestionScheduler` and `LegacyIngestionScheduler`.
2. **dashboard-data-engine**: Contains module-agnostic transformation and loading abstractions. Uses standard transformers (`PTTransformer`, `PGRTransformer`) and validators (`PTValidator`, `PGRValidator`) to massage raw metric structures into `NationalDashboardIngestRequest` and push to the ingest endpoint using `HttpLoader`.

The system persists logs to a database via `egov-persister`, making use of Kafka topics (`save-adapter-ingestion-detail`, `save-adapter-module-ingestion-detail`, `save-adapter-error-log`).

## Onboarding a New State / Module

### 1. Extractors
- Create a new Extractor class implementing `ModuleExtractor<T>` inside `dashboard-data-extractor` (`org.upyog.adapter.extractor.impl`).
- Register it using the `@Component` annotation so that `ExtractorRegistry` automatically picks it up based on its implemented generic module type.

### 2. Transformers
- Implement `ModuleTransformer<T>` inside `dashboard-data-engine` (`org.upyog.adapter.transformer.impl`).
- The `TransformerRegistry` automatically maps the Module ENUM to this transformer.

### 3. Validators
- Implement `ModuleValidator<T>` inside `dashboard-data-engine` (`org.upyog.adapter.validator.impl`).
- The `ValidatorRegistry` automatically handles execution before transformation.

### 4. Application Properties
Enable the new module in `application.properties`:
```properties
extractor.enabled-modules=PT,PGR,NEW_MODULE
```

### 5. DB Migration (Optional)
If your state requires new tracking tables, place your migration scripts in `dashboard-data-extractor/src/main/resources/db/migration/main/`. Ensure the new tables are tied to the Kafka producer config within `adapter-service-persister.yml`.

### 6. Legacy Ingestion
You can ingest historical data using the newly built API:
`POST /api/v1/legacy/ingest?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD&module=NEW_MODULE`
Check the job statuses using:
`GET /api/v1/legacy/jobs/status?tenantId=pg&moduleName=NEW_MODULE`
