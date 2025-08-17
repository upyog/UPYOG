#Changelog

All notable changes to this module will be documented in this file.

## 2.0.0 - 2025-06-30

### Added
- Added Swagger/OpenAPI documentation using springdoc-openapi-starter-webmvc-ui
- Added Spring Retry dependency for improved resilience
- Added Jakarta servlet API support for Spring Boot 3.x compatibility

### Changed
- Upgraded Spring Boot version to 3.2.2
- Upgraded Java version to 17
- Upgraded Cache2k Spring to version 2.6.1.Final
- Upgraded Jackson Dataformat XML to version 2.16.0
- Upgraded Lombok to version 1.18.38
- Upgraded Apache POI OOXML to version 5.4.1
- Upgraded Jakarta WS-RS API to version 3.1.0
- Upgraded Commons Lang3 to version 3.13.0
- Upgraded Commons IO to version 2.15.1
- Upgraded Log4j2 to version 2.21.1
- Upgraded org.json to version 20231013
- Upgraded AWS Java SDK to version 1.12.600
- Migrated from javax.* to jakarta.* packages
- Updated WebMvcConfigurerAdapter to WebMvcConfigurer interface
- Updated tracer module to version 2.9.0-SNAPSHOT with Java 17 compatibility

### Fixed
- Resolved compatibility issues with Spring Boot 3.x
- Fixed Jakarta EE migration issues (javax.servlet → jakarta.servlet)
- Fixed javax.annotation → jakarta.annotation migration
- Resolved dependency conflicts and version mismatches
- Fixed Maven compiler configuration for Java 17
- Improved MDMS service error handling for graceful startup
- Resolved OpenTelemetry version conflicts
- Fixed deprecated Double constructor warnings

### Removed
- Removed incompatible OpenTelemetry dependencies causing version conflicts

## 1.1.8 - 2023-02-02

- Transition from 1.1.8-beta version to 1.1.8 version

## 1.1.8-beta - 2022-11-04

- caching added to search API for performance improvement

## 1.1.7 - 2022-03-02
- LineChart ResponseHandler modified to consider the empty value for particular interval 
- TodaysCollection property added to the Metric chart, when this property is true query response is expected to have todaysDate and lastUdatedTime aggreagations which would be returned as the plots
- Performance Chart response handler changes to consider only the value of the aggregations which does not have buckets
- AdvanceTable Response handler changes to consider valueType of the chart when pathTypeDataMapping is not configured
- preActionTheory property added to metric chart type, which help to run the computeHelper on the aggregation path before applying action of the chart
	Ex: preActionTheory:{"ActualCollection":"repsonseToDifferenceOfDates"}




## 1.1.6 - 2022-01-13
- Updated to log4j2 version 2.17.1


## 1.1.5 - 2021-07-23
- Code changes related to new properties.
- Here are the properties which are added 
  - isRoundOff from configuration will round off the specific number value.
  - chartSpecificProperty, XtableColumnOrder to give the xtable column order as we mention in this configuration

## 1.1.4 - 2021-05-11
- security fixes

## 1.1.3 - 2021-02-26
- Updated egov mdms host name in application.properties

## 1.1.2 - 2020-11-18
- Removed default DDR hard coding 


## 1.1.1 - 2020-09-01

- Added LOCALSETUP.md and README.md
- updated Plot object to support String dataType

## 1.1.0 - 2020-06-24

- Added typescript definition generation plugin
- Upgraded to `tracer:2.0.0-SNAPSHOT`
- Upgraded to spring boot `2.2.6-RELEASE`

## 1.0.0

- Base version