# Local Setup

To setup the Faecal Sludge Management (FSM) in your local system, clone the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the municipal-services.

## Dependencies

### Infra Dependency

- [X] Postgres DB
- [ ] Redis
- [X] Elastic search
- [X] Kafka
  - [X] Consumer
  - [X] Producer

## Running Locally

To run the Vendor Service locally, you need to port forward below services locally

```bash
boundary-service
user-service
vehicle
mdms-service
```

To run the Vendor Service locally, update below listed properties in `application.properties` prior to running the project:

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
