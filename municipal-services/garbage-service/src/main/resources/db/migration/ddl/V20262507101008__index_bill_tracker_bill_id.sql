-- Support the per-bill tracker lookup performed by GB bill searches.
-- Keep this as a single statement so Flyway detects a nontransactional migration.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_grbg_bill_tracker_bill_id
    ON public.eg_grbg_bill_tracker (bill_id);
