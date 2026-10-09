# National Dashboard Ingest Service

National dashboard ingest will be used by ULB employee for - 

1. To provide a one-stop framework for ingesting data regardless of data-source based on configuration.

2. To create provision for ingest based on module-wise requirement which directly or indirectly require aggregated data ingestion functionality.

### DB UML Diagram

- NA

### Service Dependencies

- NA

### Swagger API Contract

Please refer to the [Swagget API Contract](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/core-services/docs/national-dashboard-ingest.yml) for Document Uploader Service (egov-document-uploader) to understand the structure of APIs and to have visualization of all internal APIs.


### Functionalities

1. When national dashboard ingest metrics API is hit, all the data payload lookup keys are first checked against the db to determine whether they already exist or not. The db table currently being used for storing lookup keys is nss-ingest-data. 

2. If the record for a given date and area details is not present, the payload is then flattened and pushed to persist-national-records topic.

3. National dashboard ingest kafka pipeline consumer listens on persist-national-records topic and according to the module to which the data belongs to, pushes it to the respective topic defined in the module.index.mapping key.

4. Once the national dashboard ingest kafka pipeline pushes data to the respective topic, a kafka-connector then takes the flattened records from that topic and ingests to ElasticSearch.

5. **Bulk Data Ingestion (Asynchronous S3 Workflow)**:
   - Provides an asynchronous framework for ingesting large spreadsheet (`.xlsx`) datasets uploaded to Amazon S3.
   - API `/national-dashboard/bulk/v1/_init` validates file metadata and pushes the task to the `bulk-ingest-init` Kafka topic.
   - Kafka listener (`BulkIngestListener`) consumes the event and checks PostgreSQL table `ug_bulk_ingest_job` for idempotency to avoid re-processing completed or in-progress files.
   - Downloads the file from S3 to temporary storage (`/tmp`) and processes rows in streaming batches (default 100 rows) using Apache POI to prevent OutOfMemory errors.
   - Supports both cell-embedded JSON payloads (`payload_json`) and tabular column data, propagating caller authentication context (`RequestInfo`).
   - Individual row failures are isolated and captured without aborting the entire batch.
   - Captures granular inbound API audit logs in `ug_external_api_audit_request` and `ug_external_api_audit_response`.
   - Tracks job completion status (`COMPLETED` or `FAILED`) and row counts via `egov-persister`.



### API Details

1. /national-dashboard/metric/_ingest - Takes RequestInfo and Data in request body. Data has all the parameters related to the record being inserted.

2. /national-dashboard/masterdata/_ingest - Takes RequestInfo and Data in request body. Data has all the parameters related to the record being inserted.

3. /national-dashboard/bulk/v1/_init - Initializes asynchronous bulk data ingestion for an S3 spreadsheet file.
   - **Method**: `POST`
   - **Request Body**:
     ```json
     {
       "RequestInfo": {
         "apiId": "national-dashboard-ingest",
         "ver": "1.0",
         "ts": 1710500000000,
         "action": "_init",
         "did": "1",
         "key": "",
         "msgId": "20260916-0001",
         "requesterId": "",
         "authToken": "d8e3b3e8-5b4d-4b8f-8b2d-1a8c4f9a0e1b",
         "userInfo": {
           "id": 1,
           "uuid": "b3177c56-a9f2-4b59-a00c-9e9c38b5f28f",
           "userName": "SYSTEM",
           "roles": [
             {
               "name": "NDA_SYSTEM",
               "code": "NDA_SYSTEM"
             }
           ],
           "tenantId": "pb"
         }
       },
       "details": {
         "fileName": "UPYOG/pb/PGR/pb_PGR_data_2026-09-16.xlsx",
         "stateCode": "pb"
       }
     }
     ```
   - **Response Body**:
     ```json
     {
       "ResponseInfo": {
         "apiId": "national-dashboard-ingest",
         "ver": "1.0",
         "ts": 1710500000000,
         "resMsgId": "uief87324",
         "msgId": "20260916-0001",
         "status": "SUCCESSFUL"
       },
       "status": "SUCCESS",
       "statusCode": 1,
       "message": "Bulk ingest init request pushed to queue successfully",
       "details": {
         "fileName": "UPYOG/pb/PGR/pb_PGR_data_2026-09-16.xlsx",
         "stateCode": "pb"
       }
     }
     ```
   - **Status Codes**:
     - `1` (`SUCCESS` / HTTP 200): Bulk ingest init request pushed to queue successfully.
     - `2` (`ALREADY_PRESENT` / HTTP 200): File is already present in Kafka queue.
     - `3` (`FAILED` / HTTP 500): Failed to initialize bulk ingest request.


**`Postman collection`** :-

You can access from [here](https://api.postman.com/collections/23419225-ba51e32e-77a3-4455-a0c5-3bb1dfdfd291?access_key=PMAT-01GWVA47JJXA82AD1MB78SZNX1)



### Kafka Consumers

- NA
- **bulk-ingest-init** :- Consumes bulk ingestion initialization messages to trigger S3 file download and streaming batch ingestion.

### Kafka Producers

- Following are the Producer topic.
    - **persist-national-records** :- This topic is used to push data to national-dasboard-kafka-pipeline.
    - **bulk-ingest-init** :- Used to enqueue bulk ingest file details from the `_init` API.
    - **save-bulk-ingest-job** :- Used to push initial job execution state (`IN_PROGRESS`) to `egov-persister` for saving in `ug_bulk_ingest_job`.
    - **update-bulk-ingest-job** :- Used to push final job execution state (`COMPLETED`/`FAILED`) and processed row counts to `egov-persister`.
    - **external-api-request-initiated** :- Used to push inbound API request audit events to `egov-persister` (`ug_external_api_audit_request`).
    - **external-api-response-received** :- Used to push inbound API response audit events to `egov-persister` (`ug_external_api_audit_response`).
    - **nss-ingest-keydata** :- Used to persist lookup key data in `nss-ingest-data`.
    - **egov.core.notification.email** :- Used to push email notification alerts on ingestion failures.

### Database Schema (Bulk Ingest Tracking)

Table created via Flyway migration script `V20260916130000__bulk_ingest_job_table.sql`:

```sql
CREATE TABLE IF NOT EXISTS ug_bulk_ingest_job (
    id VARCHAR(128) PRIMARY KEY,
    file_name VARCHAR(512) NOT NULL,
    state_code VARCHAR(64) NOT NULL,
    status VARCHAR(32) NOT NULL,
    total_rows_processed INTEGER DEFAULT 0,
    failed_rows_count INTEGER DEFAULT 0,
    created_time BIGINT NOT NULL,
    last_modified_time BIGINT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_ug_bulk_ingest_job_file_name ON ug_bulk_ingest_job (file_name);
CREATE INDEX IF NOT EXISTS idx_ug_bulk_ingest_job_status ON ug_bulk_ingest_job (status);
```

### Bulk Ingest Configuration Properties

| Property | Default / Example Value | Description |
| :--- | :--- | :--- |
| `bulk.ingest.topic` | `bulk-ingest-init` | Kafka topic for initiating bulk file ingestion. |
| `save.bulk.ingest.job.topic` | `save-bulk-ingest-job` | Kafka persister topic to insert new bulk ingest job record. |
| `update.bulk.ingest.job.topic` | `update-bulk-ingest-job` | Kafka persister topic to update bulk ingest job record with status and counts. |
| `bulk.ingest.batch.size` | `100` | Number of spreadsheet rows processed per streaming batch. |
| `bulk.ingest.temp.dir` | `/tmp` | Local temporary directory for downloading files from S3. |
| `aws.s3.access-key` | `${AWS_S3_ACCESS_KEY}` | AWS IAM access key ID. |
| `aws.s3.secret-key` | `${AWS_S3_SECRET_KEY}` | AWS IAM secret access key. |
| `aws.s3.region` | `ap-south-1` | AWS S3 region. |
| `aws.s3.bucket` | `${AWS_S3_BUCKET}` | Target S3 bucket name. |
| `aws.s3.folder` | `${AWS_S3_FOLDER}` | S3 folder path prefix. |
| `external.api.audit.request.initiated.topic` | `external-api-request-initiated` | Kafka topic for inbound API request audit events. |
| `external.api.audit.response.received.topic` | `external-api-response-received` | Kafka topic for inbound API response audit events. |
