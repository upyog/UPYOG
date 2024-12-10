# Local Setup

To setup the Asset Service (Asset-Service) in your local system, clone
the [UPYOG Repository](https://github.com/upyog/UPYOG) and navigate to the municipal-services.

## Dependencies

### Infra Dependency

- [x] Postgres DB
- [ ] Redis
- [ ] Elasticsearch
- [x] Kafka
    - [ ] Consumer
    - [x] Producer

## Running Locally

To run the Asset Service (asset-service) locally, you need to run the below command to port forward below services

```bash
 function kgpt(){kubectl get pods -n egov --selector=app=$1 --no-headers=true | head -n1 | awk '{print $1}'}

 kubectl port-forward -n egov $(kgpt asset-service) 8084:8080 &
 kubectl port-forward -n egov $(kgpt egov-mdms-service) 8085:8080 &
 kubectl port-forward -n egov $(kgpt persister-service) 8086:8080
``` 

