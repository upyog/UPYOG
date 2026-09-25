# egov-telemetry-batch-process

<!-- Built/run on Java 17; Spark 3.4 + start.sh --add-opens for JDK module system. -->

Batch pipeline (Apache Spark) that processes telemetry into sessions/paths and integrates with Kafka output and Elasticsearch indices.

**Main class:** `org.egov.batchtelemetry.Main`

## Requirements

- **JDK 17+** (enforced by this module’s `pom.xml` via the Maven Enforcer Plugin).
- **Apache Maven 3.6+**.

## Build

```bash
mvn clean package
```

Produces a **jar-with-dependencies** under `target/` (for example `egov-telemetry-batch-process-0.0.1-SNAPSHOT-jar-with-dependencies.jar`) for use with `start.sh` and Docker.

## Configuration

Edit `src/main/resources/application.properties` (session timeout, Kafka bootstrap and output topic, Elasticsearch URL and index names, timezone, etc.) for your environment.

## Run

- **Local / VM:** use `start.sh` after building; the Docker layout expects the fat JAR and `datefile` as in `Dockerfile`.
- **Docker:** runtime image `eclipse-temurin:17-jre-alpine`. Build the JAR first, then build the image so `COPY target/...` succeeds. See `Dockerfile` in this folder.

## Changelog

See [CHANGELOG.md](CHANGELOG.md) in this directory.
