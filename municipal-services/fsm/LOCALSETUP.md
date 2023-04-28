# Local Setup

To setup the Faecal sludge management Service (fsm) in your local system, clone the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the municipal-services.

## Dependencies

### Infra Dependency

- [X] Postgres DB
- [ ] Redis
- [X] Elastic search
- [X] Kafka
  - [X] Consumer
  - [X] Producer

## Running Locally

To run the Faecal sludge management Service (fsm) localy, you need to port forward below services locally

```bash
billing-service
mdms-service
workflow-v2
boundary-service
user-service
idgen-service
user-events
collection-service
notification-service
vendor
vehicle
fsm-calculator
egov-url-shortener
collection-service
pdf-service
```

To run the Faecal sludge management Service (fsm) locally, update below listed properties in `application.properties` prior to running the project:

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

