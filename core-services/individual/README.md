# Individual

### Individual Service
Individual registry is a Health Campaign Service that facilitates maintenance of an Individual registry on the DIGIT platform. The functionality is exposed via REST API.

### DB UML Diagram

<img width="668" alt="Screenshot 2023-03-29 at 2 41 00 PM" src="https://user-images.githubusercontent.com/123379163/228485868-e8b34236-8188-42ae-a24f-b97ec195a3aa.png">


### Service Dependencies
- Idgen Service

### Swagger API Contract
Link to the swagger API contract yaml and editor link like below

https://editor.swagger.io/?url=https://raw.githubusercontent.com/egovernments/health-campaign-services/v1.0.0/docs/health-api-specs/contracts/registries/individual.yml

### Service Details

#### API Details
BasePath `/individual/v1`

Individual service APIs - contains create, update, delete and search end point

a) POST `/individual/v1/_create` - Create Individual, This API is internally call from individual controller to create/add a new individual.

b) POST `/individual/v1/bulk/_create` - Create bulk Individual, This API is internally call from individual controller to create/add new individual in bulk.

c) POST `/individual/v1/_update` - Update Individual, This API is internally call from individual controller to update the details of an existing individual.

d) POST `/individual/v1/bulk/_update` - Update bulk Individual, This API is internally call from individual controller to update the details of existing individual in bulk.

e) POST `/individual/v1/_delete` - Delete Individual, This API is internally call from individual controller to soft delete details of an existing individual.

f) POST `/individual/v1/bulk/_delete` - Delete bulk Individual, This API is internally call from individual controller to soft delete details of an existing individual in bulk.

g) POST `/individual/v1/_search` - Search Individual, This API is internally call from individual controller to search existing individual.


### Kafka Consumers

- individual-consumer-bulk-create-topic
- individual-consumer-bulk-update-topic
- individual-consumer-bulk-delete-topic

### Kafka Producers

- save-individual-topic
- update-individual-topic
- delete-individual-topic

## Pre commit script

[commit-msg](https://gist.github.com/jayantp-egov/14f55deb344f1648503c6be7e580fa12)
