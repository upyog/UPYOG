# ID Generation Service (egov-idgen)

The ID Gen service generates new id based on the id formats passed. The application exposes a Rest API to take in requests and provide the ids in response in the requested format. 

### DB UML Diagram



### Service Dependencies

- MDMS Service (egov-mdms-service)

### Swagger API Contract

Link to the swagger API contract yaml and editor link like below

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/core-services/docs/idgen-contract.yml#!/


## Service Details

The application can be run as any other spring boot application but needs lombok extension added in your ide to load it. Once the application is up and running API requests can be posted to the url and ids can be generated.
In case of intellij the plugin can be installed directly, for eclipse the lombok jar location has to be added in eclipse.ini file in this format -javaagent:lombok.jar.


### API Details

- id/v1/_genearte

## Reference document

Details on every parameters and its significance are mentioned in the document - [ID Generation Service](https://upyog-docs.gitbook.io/upyog-v-1.0/upyog-1/platform/configure-upyog/configuring-services/id-generation-service)

### Kafka Consumers

- NA

### Kafka Producers

- NA
