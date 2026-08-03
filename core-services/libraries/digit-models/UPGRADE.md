# digit-models — Java 8 → 17 Upgrade

## Java Version

| Component | Current | Upgraded |
|-----------|---------|----------|
| Java | 1.8 | 17 |

---

## Dependency Versions

| Dependency | Current Version | Upgraded Version |
|---|---|---|
| spring-boot-starter-parent | 2.4.1 | 3.4.4 |
| spring-boot-starter-web | (inherited) | 3.4.4 |
| spring-boot-starter-jdbc | (inherited) | 3.4.4 |
| spring-boot-starter-test | (inherited) | 3.4.4 |
| spring-boot-starter-actuator | (inherited) | 3.4.4 |
| hibernate-validator | 6.0.16.Final | 8.0.2.Final |
| jsoup | 1.10.2 | 1.19.1 |
| swagger-core | 1.5.18 | 2.2.30 |
| flyway-core | (inherited) | 11.7.1 |
| lombok | 1.18.8 | 1.18.38 |
| javers-core | 3.1.0 | 7.8.0 |
| jackson-datatype-jsr310 | (inherited) | 2.19.0-rc2 |
| jackson-annotations | 2.8.7 | 2.19.0-rc2 |
| jackson-databind | 2.8.7 | 2.19.0-rc2 |
| jakarta.validation-api | 2.0.2 | 3.0.2 |
| spring-core | 5.2.5.RELEASE | 6.2.5 |
| postgresql | (inherited) | 42.7.5 |
| tracer | 2.0.0-SNAPSHOT | 2.0.0-SNAPSHOT |
| services-common | 1.0.1-SNAPSHOT | 1.0.1-SNAPSHOT |
| mdms-client | 0.0.2-SNAPSHOT | — |

---

## Key Breaking Changes

### javax → jakarta namespace
Spring Boot 3.x and Jakarta EE 9+ replaced `javax.*` with `jakarta.*`. Update all imports:
- `javax.validation.*` → `jakarta.validation.*`
- `javax.persistence.*` → `jakarta.persistence.*`
- `javax.servlet.*` → `jakarta.servlet.*`

### Swagger / OpenAPI
`swagger-core` 2.x uses `io.swagger.v3.oas.annotations.*` instead of `io.swagger.annotations.*`.

### Flyway
Flyway 10+ dropped support for older databases and changed some configuration properties. Verify `spring.flyway.*` properties in `application.properties`.

### Hibernate Validator
`hibernate-validator` 8.x aligns with Jakarta Bean Validation 3.0. Ensure constraint annotations use `jakarta.validation` package.

### JaVers 7.x
JaVers 7 requires Java 11+ and has API changes in `JaversBuilder`. Review any custom `JaversBuilder` configuration.

### maven-compiler-plugin
Duplicate plugin declaration removed. Single declaration now uses `<source>17</source>`, `<target>17</target>`, and `<release>17</release>`.
