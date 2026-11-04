# Dashboard Data Engine Library

This library contains core logic, transformers, validators, dataset file generators, and API transport clients for the UPYOG National Dashboard pipeline. It is packaged as a standard Maven JAR dependency consumed by sibling modules (such as `dashboard-data-extractor`).

## Core Responsibilities
- **Transformation & Registry**: Maps raw DTO payloads to normalized `DashboardData` metrics objects via `TransformerRegistry` (`PTTransformer`, `PGRTransformer`, `CHBTransformer`, etc.).
- **Validation & Registry**: Enforces metric structure, values, and completeness rules prior to ingestion via `ValidatorRegistry`.
- **Dataset File Generation**:
  - **SXSSF Excel Streaming**: Generates memory-safe Apache POI `.xlsx` workbooks (`SXSSFExcelGeneratorService`) with strict cell size checks (`EXCEL_MAX_CELL_CHAR_LIMIT`).
  - **Flat Delimited Dataset Streaming**: Generates generic flat dataset files (`DelimitedFileGeneratorService`) with customizable delimiters (`|`, `,`, `\t`) and file extensions (`.psv`, `.csv`, `.txt`). Automatically flattens complex nested metric objects into dot-separated header keys (e.g. `cess.usageCategory.RESIDENTIAL`).
- **Storage & Transport**:
  - Stage files to AWS S3 storage using `S3UploadClient`.
  - Dispatches bulk ingestion initialization notifications to downstream core services via `BulkIngestionInitService` carrying state code and delimiter metadata.
  - Direct HTTP multipart POST ingestion via `DashboardIngestionClient`.

## Configuration Options
| Property | Default | Description |
| :--- | :--- | :--- |
| `dashboard-data.delimited-file.enabled` | `false` | Enables parallel flat delimited file generation during extraction. |
| `dashboard-data.delimited-file.delimiter` | `|` | Custom field delimiter character (e.g. `|`, `,`, `\t`). |
| `dashboard-data.delimited-file.file-extension` | `.psv` | Custom file extension suffix for flat dataset files (e.g. `.psv`, `.csv`, `.txt`). |
| `dashboard-data.delimited-file.keep-file` | `false` | Retains local temporary delimited files on disk after upload. |

## Directory Structure
```
dashboard-data-engine
 ├── src/main/java/org/upyog/dashboard/
 │    ├── api/           # Unified clients (DashboardIngestionClient, S3UploadClient)
 │    ├── client/        # Feign client interfaces (DashboardFeignClient)
 │    ├── common/        # Shared constants and module definitions
 │    ├── config/        # DashboardProperties configuration component
 │    ├── exception/     # Custom ValidationException and error handlers
 │    ├── model/         # DashboardData, BulkIngestRequest, BulkIngestDetails, IngestionResult
 │    ├── registry/      # Extensible registries (TransformerRegistry, ValidatorRegistry)
 │    ├── service/       # File generators (SXSSFExcelGeneratorService, DelimitedFileGeneratorService, BulkIngestionInitService)
 │    ├── transformer/   # Module-specific transformers (PT, PGR, CHB)
 │    ├── util/          # CommonUtils (UUID generation, S3 key path builder)
 │    └── validator/     # Module-specific validators
 └── pom.xml
```

## Documentation & Architecture Guide
For the comprehensive pipeline architecture, database schemas, S3 storage layout, and integration guide, refer to the [State Manual](STATE_MANUAL.md).

## How to Build & Install
To build and install the library to your local Maven repository (`~/.m2`):
```bash
mvn clean install
```

