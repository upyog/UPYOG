# egov-telemetry-kafka-streams

Kafka Streams application that validates, deduplicates, unbundles, enriches, and routes telemetry events to downstream topics (including paths toward Elasticsearch-oriented output).

**Main class:** `org.egov.telemetry.Main`

## Requirements

- **JDK 17+** (enforced by this module’s `pom.xml` via the Maven Enforcer Plugin).
- **Apache Maven 3.6+** (the provided `Dockerfile` uses Maven 3.9 on Temurin 17 for image builds).

## Build

```bash
mvn clean package
```

Produces a **jar-with-dependencies** under `target/` for use with `start.sh` and Docker.

## Configuration

Edit `src/main/resources/application.properties` (Kafka bootstrap, topic names, stream names, deduplication window, etc.) for your environment.

## Run

- **Local / VM:** use `start.sh` after building, or run the fat JAR with the same classpath and working directory expectations as `start.sh`.
- **Docker:** multi-stage image — build stage `maven:3.9-eclipse-temurin-17`, runtime `eclipse-temurin:17-jre-alpine`. See `Dockerfile` in this folder.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) in this directory.
