# Code Quality Report

## Summary

The repository is a large multi-generation platform. It contains modern Spring Boot 3 services, older Spring Boot services, Java 8 WildFly applications, Node services, React frontends, Android apps, and shared libraries. Quality risks are concentrated in legacy finance/eDCR code, duplicated frontend stacks, dynamic SQL utilities, inconsistent API contract coverage, and build-breaking merge conflict markers in parts of the frontend tree.

## Findings

| Category | Evidence/Risk | Recommendation |
|---|---|---|
| Duplicate UI code | Similar React modules exist across `micro-ui`, `upyog-ui`, `workbench-ui`, `cnd-ui`, `sv-ui`, and `mono-ui` | Establish a canonical UI workspace and migrate common modules/components into shared packages |
| Large legacy classes | Finance contains very large action/service classes mixing UI, reports, persistence, workflow, and integrations | Extract query, report, workflow, and integration services; add characterization tests before refactoring |
| Dynamic SQL | Some query builders/repositories build SQL with concatenated values or identifiers | Parameterize values and allowlist identifiers |
| API contract drift | Swagger YAMLs are fragmented across `core-services/docs`, `municipal-services/docs`, `business-services/Docs`, `utilities/docs`, and `finance/docs` | Create a central API catalog and CI drift checks against controller mappings |
| Runtime version split | Java 17 Boot 3 services coexist with Java 8 Boot/WildFly services | Document service runtime version and isolate build/deploy images |
| Sparse tests in domain modules | Some municipal services have limited integration coverage compared with core services | Add workflow, billing, collection, persister, and notification integration tests for high-risk flows |
| Frontend merge conflicts | Conflict markers were detected in selected frontend files during repository audit | Resolve before production builds and add CI checks for conflict markers |
| Inconsistent local setup docs | Some `LOCALSETUP.md` files reference older Java/runtime assumptions | Refresh setup docs from actual POM/Dockerfile versions |

## Refactoring Priorities

1. Secure and parameterize high-risk SQL utilities.
2. Remove committed secrets and add secret scanning.
3. Normalize gateway/security whitelist ownership.
4. Consolidate common service patterns: validators, query builders, Kafka producer/consumer config, workflow integration, and notification templates.
5. Split legacy god classes behind tested service boundaries.
6. Centralize API specs and generated documentation.
7. Improve module-level tests for municipal services that drive revenue/payment/workflow.

## Maintainability Recommendations

- Create ownership metadata per service: business owner, technical owner, database schema, Kafka topics, and API contract path.
- Add architectural tests for forbidden dependencies and direct cross-schema access.
- Generate service inventory from CI and publish it with docs.
- Add linting for OpenAPI specs, Mermaid docs, and markdown links.
