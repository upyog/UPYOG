

# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 2025-07-29

## Added
- Added Swagger/OpenAPI documentation using springdoc-openapi-starter-webmvc-ui
- Added Postman collection for API testing and documentation

## Changed

- Upgraded Spring Boot version to 3.2.2
- Upgraded Java version to 17
- Upgraded PostgreSQL driver to version 42.7.1
- Upgraded Flyway to version 9.22.3
- Upgraded Jakarta Validation API (version managed by Spring Boot 3.2.2)
- Changed `javax` to `jakarta`
- Upgraded Lombok to version 1.18.32
- Upgraded to `spring-beans:6.1.4` (managed by Spring Boot)
- Upgraded to `spring-boot-starter-jdbc:3.2.2`
- Upgraded to `mdms-client:2.9.0-SNAPSHOT`
- Upgraded to `tracer:2.9.0-SNAPSHOT`
- Added TypeScript definition generation plugin version 2.22.595



## Fixed
- Resolved compatibility issues with Spring Boot 3.x
- Resolved dependency conflicts and version mismatches


## 1.1.5 - 2023-02-02

- Transition from 1.1.5-beta version to 1.1.5 version

## 1.1.5-beta - 2022-01-13
- Updated to log4j2 version 2.17.1

## 1.1.4 2021-02-26

- Updated domain name in application.properties

## 1.1.3 2021-01-12

- Apportion and backupdate to demands enabled for zero payments, which was blocked till this point of time

## 1.1.3 

## 1.1.2 2020-08-24
- Fixed Bug RAIN-1432: Added audit for demand apportion

## 1.1.1 - 2020-06-17

- Fixed Bug WOR-415: Regarding adjustedAmount during multiple advance adjustment

## 1.1.0 - 2020-06-25
- Added typescript definition generation plugin
- Upgraded to `tracer:2.0.0-SNAPSHOT`
- Upgraded to spring boot `2.2.6-RELEASE`
- Deleted `Dockerfile` and `start.sh` as it is no longer in use

## 1.0.0

- Base version
