
# NDC Service (ndc-services)

Module is used to apply NDC application for approval of respective NDC users.


### DB UML Diagram



### Service Dependencies

- egov-user  ( Manage user )

- egov-idgen ( To generate the application No)

- egov-localization ( To use the localized messages )

- egov-location ( To store the address locality )

- egov-mdms ( Configurations/master data used in the application is served by MDMS )

- egov-notification-sms ( Service to send SMS to the users involved in the application )

- egov-persister ( Helps to persist the data  )

- egov-workflow-v2 ( Workflow configuration for different BPA application is configured )

### Swagger API Contract

 - [Swagger API](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/ndc-v-1.0.0.yaml#!/)


## Service Details

For every building plan service applied there is a need to get the No objection certificate from concerned departments. Based on the  configuration we have for the NDCs, for every application there will be a set of NDCs required. There should be a provision to allow the NDC department user to login to our system and upload the required NDC. We are providing a user to one NDC department. Based on the workflow mode (online/offline) of each NDC type, the NDC department user can perform action. 

Online mode – NDC department user can login to system and approve/reject the application.

Offline mode – NDC application will be auto approved.
Offline mode – NDC application will be auto approved.

### API Details
- Create : NDC application with ndc-services/v1/ndc/_create api.
The response contains the NDC object with its assigned application number .
- Update : On created NDC multiple assessments can be done by calling the ndc-services/v1/ndc/_update api. Validations are carried out to verify the authenticity of the request and generate application fee which will be paid by the architect and gets approval number generated on approval .
- Search : NDC can be searched based on several search parameters by calling ndc-services/v1/ndc/_search.

### Kafka Consumers
- persister.save.ndc.topic=save-ndc-application
- persister.update.ndc.topic=update-ndc-application
- persister.update.ndc.workflow.topic=update-ndc-workflow


### Kafka Producers
- persister.save.ndc.topic=save-ndc-application
- persister.update.ndc.topic=update-ndc-application
- persister.update.ndc.workflow.topic=update-ndc-workflow

