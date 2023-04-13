# Local Setup

To setup the egov-custom-consumer service in your local system, clone the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the utilities folder.

## Dependencies

### Infra Dependency

- [ ] Postgres DB
- [ ] Redis
- [ ] Elasticsearch
- [X] Kafka
  - [X] Consumer
  - [ ] Producer

## Running Locally

To run the Custom Consumer Service(egov-custom-consumer) locally, update below listed properties in `application.properties` before running the project:

```ini
egov.coexistence.hostname=#Host value of the co-existence finance erp server (ex: https://upyog.niua.org)
```
