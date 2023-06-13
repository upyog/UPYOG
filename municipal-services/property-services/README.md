# Property Service (property-services)

Property service creates property and stores in registry on top of which municipal activities like assessment, mutation and amalgamation can be performed. keeps tracks of the properties and the taxes paid for them.

### DB UML Diagram

NA

### Service Dependencies

- User Service (user)
- ID Gen. Service (ID-GEN)
- Property Tax Calculator Service (pt-calculator)
- MDM Service (MDMS)
- Location Service (Location)
- Localisation Service (localisation)

### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/property-services/property-services.yml#!/

## Service Details

Creates property, Assessment and mutation on existing proeprties.

### API Details

- property - The property set of API's can be used to create and update, mutate properties.
- assessment - The Assessment APIs can be used to assess a property and pay tax for them.

### Kafka Consumers

persister.save.property.topic=save-property-registry
persister.update.property.topic=update-property-registry

egov.pt.assessment.create.topic=save-pt-assessment
egov.pt.assessment.update.topic=update-pt-assessment

### Kafka Producers

persister.save.property.topic=save-property-registry
persister.update.property.topic=update-property-registry

egov.pt.assessment.create.topic=save-pt-assessment
egov.pt.assessment.update.topic=update-pt-assessment

