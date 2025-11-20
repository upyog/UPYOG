# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2025-09-12 (LTS)

### Major Upgrades
- **Runtime**: Upgraded to Java 17 and Spring Boot 3.2.2
- **API Migration**: Migrated from javax.* to Jakarta (jakarta.*) APIs

### Dependencies Updated
- hibernate-validator: 8.0.1.Final
- spring-boot-starter-validation (Jakarta)
- tracer: 2.9.0-SNAPSHOT
- mdms-client: 2.9.0-SNAPSHOT
- json-smart: 2.4.11
- OWASP Java HTML Sanitizer: 20240325.1
- Spring Boot Actuator: enabled

### API Documentation
- **Swagger/OpenAPI**: Migrated to springdoc-openapi 2.3.0 (replacing springfox)
- **API Docs**: Available at `/v3/api-docs`
- **Swagger UI**: Available at `/swagger-ui.html`

### Breaking Changes
- **JDK Requirement**: JDK 17+ required
- **Package Migration**: All javax.* packages replaced by jakarta.*

## 1.3.0 - 2023-03-31

 - Introduced seperate tab for vehicle.
 - Seperated the Apis -Create, update and search.
 - vehicle linking and delinking with vendor introduced seperatly. 
  
## 1.2.0 - 2022-08-04

 - Vehicle logging at FSTP decoupled from FSM module 
  
## 1.0.3 - 2022-01-13

- Updated to log4j2 version 2.17.1

## 1.0.1

- Code changes related to plant mapping.
- Simple change in vehicle tank capacity.
- Added plain search service.

## 1.0.1

- Fixed security issue of untrusted data pass as user input.


## 1.0.0

- base version
