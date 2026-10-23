# Tenant Service

## Overview
Tenant Service manages tenant data for the UPYOG platform. Tenant data has been migrated to MDMS — there is no change in endpoints or functionality.

## Tech Stack
- Java 17
- Spring Boot 3.4.4
- PostgreSQL 42.7.1
- Flyway 9.22.3
- Lombok 1.18.32

## Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL
- Running MDMS service

## Local Setup

### 1. Clone the repository
```bash
git clone https://github.com/upyog/UPYOG-NIUA.git
cd core-services/tenant
```

### 2. Configure application.properties
Update the following properties as per your local environment:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/devdb
spring.datasource.username=postgres
spring.datasource.password=postgres

egov.services.egov_mdms.hostname=http://localhost:8093/
egov.services.egov_mdms.searchpath=egov-mdms-service/v1/_search
```

### 3. Build
```bash
export JAVA_HOME=/path/to/java17
mvn clean install -DskipTests
```

### 4. Run
```bash
mvn spring-boot:run
```
Service starts on port `8092` with context path `/tenant`.

## Tenant Data
Tenant data is managed via MDMS. To create or update a tenant, modify the following file and check in:

**Location:** https://github.com/upyog/upyog-mdms-data/blob/master/data/pg/tenant/tenants.json

## API Endpoints

### Search Tenant
```
POST /tenant/v1/tenant/_search?code={tenantCode}
```
**Example:**
```
http://localhost:8092/tenant/v1/tenant/_search?code=panavel
```

### Create Tenant
```
POST /tenant/v1/tenant/_create
```

### Update Tenant
```
POST /tenant/v1/tenant/_update
```

## Upgrade Notes (v2.0.0)
This service has been upgraded from Spring Boot 1.5.x / Java 8 to Spring Boot 3.4.4 / Java 17.

Key changes:
- `javax` → `jakarta` namespace (Spring Boot 3)
- `WebMvcConfigurerAdapter` replaced with `WebMvcConfigurer`
- Flyway properties migrated from `flyway.*` to `spring.flyway.*`
- `server.context-path` migrated to `server.servlet.context-path`
- `tracer` upgraded to `2.9.0-SNAPSHOT`
- `services-common` removed (replaced by `tracer`)

Refer to [CHANGELOG.md](./CHANGELOG.md) for full details.
