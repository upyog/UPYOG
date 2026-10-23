# Operations Guide

## Runtime Dependencies

| Dependency | Used by | Operational checks |
|---|---|---|
| PostgreSQL | Most backend services, persister, legacy finance/eDCR | connection count, slow queries, locks, replication/backups, Flyway state |
| Kafka | Persister, indexer, notifications, dashboards, payments, audit | broker health, topic partitions, consumer lag, DLQ volume |
| Redis | Gateway/Zuul rate limiting, user/localization/url-shortening caches | memory, evictions, latency, key growth |
| Elasticsearch | Indexer/search/dashboard/search use cases | cluster health, shard allocation, indexing lag, query latency |
| Object storage | Filestore, document upload, generated PDFs | bucket permissions, retention, object lifecycle, access credentials |
| External providers | SMS, email, payment, DigiLocker, eSign, GIS | credential validity, latency, error rates, callback availability |

## Health and Monitoring

- Expose readiness/liveness probes per service; default to actuator `/health` where configured.
- Scrape JVM metrics: heap, GC, threads, CPU, HTTP latency, DB pool usage.
- Scrape Kafka consumer lag for persister/indexer/notification/dashboard/voucher consumers.
- Monitor gateway request rates, 4xx/5xx, RBAC denials, Redis rate-limit behavior.
- Monitor Flyway job success before rolling application pods.

## Logging and Tracing

- Preserve correlation IDs from gateway through service-to-service calls and Kafka events.
- Centralize logs and mask sensitive headers/properties.
- Use structured fields for tenant, module, business identifier, user UUID, correlation ID, and Kafka topic/partition/offset.

## Backup and Recovery

- Back up PostgreSQL databases and verify restore drills.
- Preserve object storage buckets with lifecycle policies.
- Retain Kafka topics long enough for replay/recovery where business requires it.
- Store deployment configuration/secrets in versioned, recoverable infrastructure systems.

## Release Checklist

1. Build images through CI.
2. Run unit/integration tests.
3. Run Flyway migrations in staging.
4. Validate gateway routes and RBAC action mappings.
5. Validate Kafka topics and consumer groups.
6. Validate secret/config injection.
7. Smoke-test critical APIs through gateway.
8. Monitor logs, health, Kafka lag, and DB load during rollout.
9. Keep rollback image tags and migration rollback plan where possible.

## Incident Playbooks

### Persister lag or failures

- Check Kafka broker health and `egov-persister` consumer lag.
- Inspect dead-letter topic volume.
- Identify poison message by topic/partition/offset.
- Pause/replay only after idempotency review.

### Gateway outage

- Check Redis, user service, accesscontrol service, and gateway pod health.
- Temporarily scale gateway replicas if CPU/request pressure is high.
- Verify route config and ingress/service DNS.

### Database saturation

- Identify slow queries and lock waits.
- Scale read replicas only for read-heavy paths that support them.
- Add indexes on tenant/business/workflow/payment fields.
- Reduce page sizes and long-running dashboard queries.

### Notification provider failure

- Monitor notification service retries and provider response codes.
- Fail gracefully and queue messages for retry.
- Rotate credentials if provider auth fails.
