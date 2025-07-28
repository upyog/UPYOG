# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2024-07-08

### Added
- Added SpringDoc OpenAPI starter for Spring Boot 3.x (version 2.3.0)
- Added Postman Collection for API testing and documentation
- Added OWASP HTML Sanitizer for Java 17 (version 20240325.1)

### Changed
- Changed `javax` to `jakarta`
- Upgraded to `Spring Boot 3.2.2`
- Upgraded to `Java 17`
- Updated to `log4j2 version 2.21.1`
- Upgraded to `tracer:2.9.0-SNAPSHOT`
- Upgraded to `mdms-client:2.9.0-SNAPSHOT`
- Updated hibernate-validator to 8.0.1.Final
- Updated jsoup to 1.17.2
- Updated lombok to 1.18.32
- Updated gson to 2.10.1
- Updated typescript-generator-maven-plugin to 2.22.595

### Fixed
- Resolved compatibility issues with Spring Boot 3.x
- Resolved dependency conflicts and version mismatches

## 1.2.5 - 2023-02-02

- Transition from 1.2.5-beta version to 1.2.5 version

## 1.2.5-beta - 2022-03-02
- Added security fix for restricting employee search from citizen role

## 1.2.4 - 2022-01-13
- Updated to log4j2 version 2.17.1

## 1.2.3 - 2021-07-26
 - Fixed RAIN-3056: Able to re-activate employee by selecting the previous date

## 1.2.2 - 2021-05-11
 - VUL-WEB-L008
 - Added @SanitizeHtml annotaion on string fields
 - Updated POM to add safeHtml validator libraries

## 1.2.1 - 2021-02-26
- Updated domain name in application.properties

## 1.2.0 - 2021-01-12
- Added employee reactivation feature

## 1.1.0 - 2020-05-27

- Upgraded to `tracer:2.0.0-SNAPSHOT`
- Upgraded to `Spring boot 2.2.6`
- Renamed `ReferenceType` enum to `EmployeeDocumentReferenceType`
- Added typescript interface generation plugin

## 1.0.0

- Base version
