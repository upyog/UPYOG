# Changelog

<!-- Primary release note for the Java 8 → 17 + Spark/Kafka upgrades in this module. -->

All notable changes to **egov-telemetry-batch-process** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/).

## [Unreleased]

## [Latest] - 2026-04-21

### Changed

- **Java runtime and build**: Migrated from Java 8 to **Java 17** (`maven.compiler.release` **17**; Enforcer `requireJavaVersion` **`[17,)`**).

#### Dependency versions (previous → latest)

| Artifact | Previous | Latest |
|----------|----------|--------|
| **Java (compile)** | 8 (`source` / `target`) | **17** (`release`) |
| `org.apache.spark:spark-core` | `spark-core_2.11` **2.4.0** | `spark-core_2.12` **3.4.4** |
| `org.apache.spark:spark-sql` | `spark-sql_2.11` **2.4.0** | `spark-sql_2.12` **3.4.4** |
| `org.apache.kafka:kafka` | `kafka_2.12` **1.1.0** | _removed_ (not required for current build) |
| `org.apache.kafka:connect-json` | 1.1.0 | **3.6.2** |
| `org.apache.kafka:kafka-clients` | 1.1.0 | **3.6.2** |
| `org.elasticsearch:elasticsearch-spark` | `elasticsearch-spark-20_2.11` **6.5.1** | `elasticsearch-spark-30_2.12` **7.17.15** |
| `joda-time:joda-time` | _(added **2.12.5** during upgrade)_ | **removed** — `Main.java` now uses **`java.time`** only (no Joda) |
| `org.projectlombok:lombok` | 1.18.4 | **1.18.30** |

_Unchanged this release (same version as before):_ `org.json:json` 20180813, `com.jayway.jsonpath:json-path` 2.4.0.

#### Maven plugins (previous → latest)

| Plugin | Previous | Latest |
|--------|----------|--------|
| `maven-compiler-plugin` | (explicit `source`+`target` **8**) | **3.11.0** with `<release>17</release>` |
| `maven-enforcer-plugin` | _(not present)_ | **3.5.0** (`requireJavaVersion` `[17,)`) |

#### Docker base image (previous → latest)

| Stage | Previous | Latest |
|-------|----------|--------|
| Runtime | `openjdk:8` | **`eclipse-temurin:17-jre-alpine`** |

### Notes for operators

- Build and run this service with **JDK / JRE 17** (or newer, per Enforcer rules).
- **Spark** upgraded **2.4.x → 3.4.x** and **Scala 2.11 → 2.12**; **Elasticsearch-Hadoop** artifact moved to **`elasticsearch-spark-30`** aligned with Spark 3.x — validate jobs and ES cluster compatibility in non-production first.
- **Kafka** client stack moved from **1.1.x** to **3.6.x** — confirm broker compatibility.
