# Local Setup

To setup the Inbox Service (inbox) in your local system, clone the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the municipal-services.

## Dependencies

### Infra Dependency

- [X] Postgres DB
- [ ] Redis
- [ ] Kafka
  - [ ] Consumer
  - [ ] Producer

## Running Locally

To run the Inbox Service (inbox) locally, you need to port forward below services locally

```bash
egov-workflow-v2
user-service
egov-searcher
Municipal service for which inbox config is defined
```

To run the Inbox Service (inbox) locally, update below listed properties in `application.properties` prior to running the project:

```ini
service.search.mapping=
```

