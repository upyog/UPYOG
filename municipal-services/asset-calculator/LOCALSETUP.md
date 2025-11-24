# Local Setup

This document will walk you through the dependencies of this service and how to set it up locally

- To setup the FSM Calculator Service (asset-calculator) in your local system, clone the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the municipal-services.

## Dependencies

### Infra Dependency

- [ ] Postgres DB
- [ ] Redis
- [ ] Elasticsearch
- [X] Kafka
  - [ ] Consumer
  - [X] Producer

## Running Locally


To run the fsm locally, you need to port forward below services locally

```bash
billing-service
mdms-service
asset
```

To run the FSM Calculator Service (fsm-calculator) locally, update below listed properties in `application.properties` before running the project:

```ini
server.port
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.platform=

spring.flyway.url=
spring.flyway.user=
spring.flyway.password=
spring.flyway.table=
```
