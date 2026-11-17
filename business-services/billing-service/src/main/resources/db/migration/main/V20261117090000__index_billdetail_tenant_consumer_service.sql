-- Match the tenant, consumer-code and service filters used by bill/v2/_search.
-- Keep this as a single statement so Flyway detects a nontransactional migration.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_billdetail_tenant_consumer_service
    ON public.egbs_billdetail_v1 (tenantid, consumercode, businessservice);
