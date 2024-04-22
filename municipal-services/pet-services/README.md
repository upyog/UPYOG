# Pet Service

Pet service creates pet application for user which can be acted upon by ulb officals to verify and approve the application, and further provide the user with a pet certificate.
### DB UML Diagram

NA

### Service Dependencies

- User Service (user)
- ID Gen. Service (ID-GEN)
- MDM Service (MDMS)
- Location Service (Location)
- Localisation Service (localisation)


## Service Details

Creates and search pet application. Update applicaion's workflow by ulb officals

### API Details

- pet application - The pet application set of API's can be used to create and search application. Update application workflow.

### Kafka Consumers

ptr.kafka.create.topic=save-ptr-application
ptr.kafka.update.topic=update-ptr-application

### Kafka Producers

ptr.kafka.create.topic=save-ptr-application
ptr.kafka.update.topic=update-ptr-application
