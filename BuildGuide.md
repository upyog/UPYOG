# Build Guide

## Build System Overview

| Area | Build tool |
|---|---|
| Java microservices | Maven |
| Frontends | npm/yarn workspaces and webpack/build scripts |
| Android apps | Gradle |
| Legacy finance/eDCR | Maven, Make, Ansible, WildFly EAR packaging |
| Containers | Dockerfiles and Jenkins build registry files |

## Java Services

```bash
cd core-services/egov-user
mvn clean test
mvn clean package
```

Use Java 17 for Spring Boot 3.2.x services. Use Java 8 for legacy Boot 1/2 and WildFly finance/eDCR modules.

## Frontend Apps

```bash
cd frontend/micro-ui/web
yarn install
yarn build
```

Check the app-specific README and Dockerfile because the frontend directories contain multiple UI variants and package workspaces.

## Docker Builds

Jenkins uses build registry files such as `build/build-config.yml` and domain-specific `*/build/build-config.yml` to map jobs to work directories, image names, and Dockerfiles.

```bash
# Example pattern; adjust WORK_DIR and Dockerfile from build-config.yml
docker build -t egov-user -f core-services/build/maven/Dockerfile --build-arg WORK_DIR=core-services/egov-user .
```

## Database Migration Images

Many services package migrations separately from application images. Migration containers run Flyway with `DB_URL`, `FLYWAY_USER`, `FLYWAY_PASSWORD`, `FLYWAY_LOCATIONS`, and `SCHEMA_TABLE`.

## CI/CD

- Jenkins is the primary build pipeline and uses `ci-libs`.
- GitHub Actions are present for selected package publishing, GHCR builds, CodeQL, AI review, and Jenkins trigger workflows.
- Build outputs are pushed to external container registries and deployed by external Kubernetes/Helm infrastructure.
