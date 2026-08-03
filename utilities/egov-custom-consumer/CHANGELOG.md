# Changelog

All notable changes to this module will be documented in this file.

## 1.1.2 - 2026-05-12

- Upgraded Java from 8 to 17
- Upgraded Spring Boot to 3.2.2
- Updated Dockerfile base image to `eclipse-temurin:17-jre-alpine`
- Removed manual `spring-beans` dependency to avoid Spring version conflicts
- Upgraded `tracer` dependency to `2.9.0-SNAPSHOT`
- Replaced legacy `commons-lang:commons-lang` with `org.apache.commons:commons-lang3:3.14.0`
- Updated `org.json` from `20160810` to `20240303`
- Updated `log4j2` to 2.24.3
- Updated `lombok` to 1.18.38
- Fixed Dockerfile jar filename to match current version `1.1.1-SNAPSHOT`

## 1.1.1 - 2023-02-02

- Transition from 1.1.1-beta version to 1.1.1 version

## 1.1.1-beta - 2022-01-13
- Updated to log4j2 version 2.17.1

## 1.1.0 - 2020-06-22

- Upgraded to `tracer:2.0.0-SNAPSHOT`
- Upgraded to `Spring boot 2.2.6`

## 1.0.0

- Base version
