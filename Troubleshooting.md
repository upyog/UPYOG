# Troubleshooting Guide

## Startup Failures

| Symptom | Likely cause | Resolution |
|---|---|---|
| Service exits before binding port | Wrong Java version, missing property, failed bean creation | Check container image Java version, `application.properties`, and Spring stack trace |
| `BeanCreationException` for datasource | DB URL/user/password missing or wrong driver | Verify PostgreSQL connectivity and secret injection |
| Flyway validation/migration failure | Out-of-order migration, schema history mismatch, duplicate object | Inspect Flyway table, migration checksum, and service migration folder |
| Kafka consumer fails repeatedly | Topic missing, bad bootstrap server, poison message | Create topic, validate bootstrap DNS, inspect consumer logs/DLQ |
| Redis connection refused | Redis host/port wrong or unavailable | Check `spring.redis.*` / `spring.data.redis.*` properties and network policy |
| 401/403 from gateway | Token invalid, action not mapped to role, whitelist mismatch | Check `egov-user`, `egov-accesscontrol`, and gateway route/whitelist properties |
| 404 at gateway | Route missing or service DNS/port mismatch | Check Zuul `routes.properties`, Spring Gateway route config, and Kubernetes Service |
| Notification not delivered | Kafka topic mismatch, provider credentials, template/localization missing | Check notification service consumer lag, SMS/email provider config, localization keys |
| Search results missing | Indexer lag, Elasticsearch mapping issue, persister failure | Check indexer config, ES health, Kafka offsets, and service indexer YAML |
| Payment callback not reflected | PG service callback failure or collection topic lag | Check `egov-pg-service`, collection-services, Kafka payment topics, and idempotency keys |

## Database Issues

- Ensure every service points to the correct tenant/schema/database.
- Confirm Flyway migrations ran before service pods start.
- Check indexes for tenantId, business identifiers, workflow state, payment IDs, and audit timestamps.
- Avoid direct cross-service table updates; use APIs/events.

## Kafka Issues

- Validate `kafka.config.bootstrap_server_config` or `spring.kafka.bootstrap-servers`.
- Confirm topic names match service properties and persister/indexer YAML.
- Monitor consumer group lag for persister, indexer, notification, dashboard, and voucher consumer.
- Use DLQs for poison messages and replay only idempotent events.

## Redis Issues

- Gateway rate limiting and selected services depend on Redis availability.
- Check connection pool exhaustion and TTL/key growth.
- Treat Redis as cache/session/rate-limit infrastructure, not authoritative storage unless explicitly designed.

## Configuration Issues

- Most services use `application.properties`; profiles are not widely used.
- Deployment overlays should override checked-in defaults.
- Secrets must come from Kubernetes secrets or external config, not source files.
- Confirm context path and gateway route path align.

## Java 17 Migration Issues

- Boot 3 services require Java 17; legacy Boot 1/2 and WildFly modules may require Java 8.
- Watch for `javax.*` to `jakarta.*` incompatibilities, dependency conflicts, reflection restrictions, and older Maven plugin behavior.

## WildFly Deployment Issues

- Finance and eDCR are EAR/WildFly deployments, not Boot JARs.
- Validate module descriptors, datasource/JNDI names, external property files, and Java 8 base images.
- Check deployment marker files and WildFly server logs for classloading conflicts.

## Spring Boot Issues

- Boot 3 services may fail if old libraries pull `javax.servlet` APIs.
- Bean creation commonly fails when MDMS/user/workflow/billing host properties are absent.
- Actuator endpoints can move based on `management.endpoints.web.base-path`; many services expose health at `/health`.
