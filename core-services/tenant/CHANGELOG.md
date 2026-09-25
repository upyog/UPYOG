# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2025-04-22

### Changed

#### Java & Spring Boot
- Upgraded Java from `1.8` to `17`
- Upgraded `spring-boot-starter-parent` from `1.5.22.RELEASE` to `3.4.4`
- Upgraded `spring-boot-starter-web` from `1.5.22.RELEASE` to `3.4.4` (managed by parent)
- Upgraded `spring-boot-starter-jdbc` from `1.5.22.RELEASE` to `3.4.4` (managed by parent)
- Upgraded `spring-boot-starter-test` from `1.5.22.RELEASE` to `3.4.4` (managed by parent)

#### Dependencies
- Upgraded `log4j2` from `2.17.1` to `2.21.1`
- Upgraded `lombok` from `1.18.8` to `1.18.32`
- Upgraded `joda-time` from `2.9.6` to `2.12.5`
- Upgraded `commons-io` from `2.5` to `2.15.1`
- Upgraded `postgresql` from `9.4.1212` to `42.7.1`
- Upgraded `commons-lang3` from `3.0` to version managed by Spring Boot parent (version tag removed)
- Upgraded `tracer` from `1.1.3-SNAPSHOT` to `2.9.0-SNAPSHOT`
- Upgraded `jackson-datatype-jsr310` from `2.8.6` to `2.19.1`
- Upgraded `flyway-core` from `4.1.0` to `9.22.3`

#### Build Plugins
- Upgraded `maven-pmd-plugin` from `3.7` to `3.21.2`
- Upgraded `jacoco-maven-plugin` from `0.7.9` to `0.8.11`

### Added
- Added `net.minidev:json-smart` dependency to resolve `net.minidev.json.JSONArray` used in `MdmsResponse`, `MdmsRepository` and `TenantService`
- Added `junit-vintage-engine` (test scope) to support existing JUnit 4 tests on JUnit 5 platform

### Removed
- Removed `services-common:0.4.0` — deprecated, replaced by `tracer`

### Fixed

#### Source Code
- `EgovTenantApplication.java` — Replaced removed `WebMvcConfigurerAdapter` with `WebMvcConfigurer` interface (removed in Spring 6)
- `EgovTenantApplication.java` — Replaced removed `MediaType.APPLICATION_JSON_UTF8` with `MediaType.APPLICATION_JSON` (removed in Spring 6)
- `MdmsRepository.java` — Fixed `requestInfo.getTs().getTime()` to `requestInfo.getTs()` since `ts` field type changed from `Date` to `Long` in updated tracer contract
- `ResponseInfoFactory.java` — Fixed `ts` variable type from `String` to `Long` to match updated `ResponseInfo` constructor signature

#### Test Code
- All test files — Replaced `org.mockito.runners.MockitoJUnitRunner` with `org.mockito.junit.MockitoJUnitRunner` (package moved in Mockito 2+)
- `TenantControllerTest.java` — Replaced `org.mockito.Matchers` with `org.mockito.ArgumentMatchers` (removed in Mockito 4)

#### Configuration
- `application.properties` — Migrated Flyway properties from `flyway.*` to `spring.flyway.*` (Spring Boot 3 prefix change)
- `application.properties` — Migrated `server.context-path` to `server.servlet.context-path` (Spring Boot 3 change)

## 1.3.5 - 2023-02-01

- Transition from 1.3.5-beta version to 1.3.5 version
