# Changelog

<!-- Primary release note for the Java 8 → 17 + Kafka Streams upgrades in this module. -->

All notable changes to **egov-telemetry-kafka-streams** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

## [Latest] - 2026-04-21

### Changed

- **Java runtime and build**: Migrated from Java 8 to **Java 17** (`maven.compiler.release` **17**; Enforcer `requireJavaVersion` **`[17,)`**).

#### Dependency versions (previous → latest)

| Artifact | Previous | Latest |
|----------|----------|--------|
| **Java (compile)** | 8 (`source` / `target`) | **17** (`release`) |
| `org.apache.kafka:kafka-streams` | 1.0.2 | **3.6.2** |
| `org.apache.kafka:kafka-clients` | 1.0.2 | **3.6.2** |
| `org.projectlombok:lombok` | 1.18.2 | **1.18.30** |
| `org.slf4j:slf4j-api` | 1.7.25 | **1.7.36** |
| `ch.qos.logback:logback-classic` / `logback-core` | 1.2.0 / 1.1.11 (mismatched) | **1.2.13** (aligned; core explicit in POM) |
| `commons-io:commons-io` | 2.4 | **2.16.1** |
| `commons-codec:commons-codec` | 1.6 | **1.17.1** |

_Unchanged this release:_ `com.github.fge:json-schema-validator` 2.2.6, `com.github.fge:json-schema-core` 1.2.5 (also declared explicitly alongside the validator), `org.json:json` 20180813.

#### Maven plugins (previous → latest)

| Plugin | Previous | Latest |
|--------|----------|--------|
| `maven-compiler-plugin` | (default / explicit `source`+`target` **8**) | **3.11.0** with `<release>17</release>` |
| `maven-enforcer-plugin` | _(not present)_ | **3.5.0** (`requireJavaVersion` `[17,)`) |

#### Docker base images (previous → latest)

| Stage | Previous | Latest |
|-------|----------|--------|
| Build | `openjdk:8` | **`maven:3.9-eclipse-temurin-17`** |
| Runtime | `egovio/8-openjdk-alpine` | **`eclipse-temurin:17-jre-alpine`** |

### Notes for operators

- Build and run this service with **JDK / JRE 17** (or newer, per Enforcer rules).
- **Kafka client / broker compatibility**: Kafka client libraries moved from **1.0.x** to **3.6.x** — confirm broker and cluster protocol compatibility with your Kafka deployment.
