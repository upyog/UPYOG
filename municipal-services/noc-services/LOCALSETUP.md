# Local Setup

This document will walk you through the dependencies of this service and how to set it up locally

- To setup the NOC Service (noc-services) in your local system, clone the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the municipal-services folder.

## Dependencies

### Infra Dependency

- [X] Postgres DB
- [ ] Redis
- [ ] Elasticsearch
- [X] Kafka
  - [X] Consumer
  - [X] Producer

## Running Locally

To run the NOC Service (noc-services) locally, update below listed properties in `application.properties` before running the project:

```ini
If any host value of the server for any external service pointing to local port can be changed to https://egov-micro-dev.egovernments.org
```
