# Data Extractor Service

The `data-extractor` service queries UPYOG databases daily to gather transaction and workflow metrics, structures them, and runs them through `adapter-service` to ingest into the National Dashboard.

## Key Features

1. **Daily Scheduler**:
   * Runs daily at a configured cron time (typically 1 AM) to extract the previous day's metrics and trigger the ingestion pipeline.
2. **SQL Query Performance**:
   * Uses combined queries in `PTQueryRegistry.java` to minimize database round-trips:
     * **Combined Scalar Metrics Query**: Fetches 8 distinct transaction/application/SLA metrics in 1 database round-trip.
     * **Combined Collection Breakdown Query**: Fetches all tax heads (`PT_TAX`, `PT_FIRE_CESS`, `PT_CANCER_CESS`, penalty, interest, rebate) and payment modes for today's collection, aggregating them in memory.
3. **Flexible Date Handling**:
   * Extraction logic supports querying any target `LocalDate` to allow manual historical backfills.

## Local Testing

To test ingestion for active test records locally:
1. Run the test endpoint `/api/v1/test` or trigger the service directly.
2. The current temporary configuration queries target date `30-06-2026` (the most active date in the test database).
3. The real date calculation logic is commented out in `DailyIngestionService.fetchPTDataFromDatabase()` for easy test validation.
