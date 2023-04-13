# Birth Death Services (birth-death-services)

This is the UPYOG application, which helps and provides a digital interface, allowing employees/citizens for downloading the Birth & death Certificates. Employee can register both birth and death applications, update and search where in citizen has an access to download the certificates. There are two processes while downloading the certificates as citizen can download the certificate free for the first time and it will be charged for next downloads so citizen has to pay the amount and download the certificates.

### DB UML Diagram

- NA

### Service Dependencies

- Billing Service (billing-service)
- MDM Service (egov-mdms-service)
- Localization Service (egov-localization)
- ID Generation Service (egov-idgen)
- User Service (egov-user)
- PDF Service (egov-pdf)
- PDF Service (pdf-services)
- Encryption Service (egov-enc-service)

### Swagger API Contract

https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/birth-death/birth-death.yml

## Service Details

Creates birth and death applications, download certificates for first time and charges applicable after that.

### API Details

`savebirthimport` : This API is used to create an application for birth in the system. Whenever an application is created an application can be downloaded from the system.

`savedeathimport` : This API is used to create an application for death in the system. Whenever an application is created an application can be downloaded from the system.

`updatebirthimport` : The updatebirthimport API is used to update the application information.

`updatedeathimport` : The updatedeathimport API is used to update the application information.

`_download` : The _download API is to download birth and death application information.

### Kafka Consumers

persister.save.birth.topic=save-birth-topic
persister.update.birth.topic=update-birth-topic

persister.save.death.topic=save-death-topic
persister.update.death.topic=update-death-topic

### Kafka Producers

persister.save.birth.topic=save-birth-topic
persister.update.birth.topic=update-birth-topic

persister.save.death.topic=save-death-topic
persister.update.death.topic=update-death-topic
