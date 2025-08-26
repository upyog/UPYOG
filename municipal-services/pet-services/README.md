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

### c) Certificate Generation
- Certificates issued after approval.
- Certificate numbers are generated via IDGen.


## Service Details

**a) Application Submission & Status Tracking:** Citizens or counter employees can submit a pet registration application by providing necessary details and uploading required documents. Citizens can track their application status under the “My Applications” section.

**b) Ulb Employee Processing & Approval Flow:** ULB employees can filter, view, and validate submitted applications. After verification, they can forward the application to higher authorities for approval.

**c) Payment, Receipt & Certificate Download:** Once approved, the citizen or counter employee can make the payment. After payment, the citizen can download the payment receipt and the final Pet Certificate from their login.

### Swagger API Contract
- Please refer to the [Swagger API Contract](http://localhost:8080/pet-services/swagger-ui.html#/pet-controller/petRegistrationCreateUsingPOST) for Pet service to understand the structure of APIs and to have visualization of all internal APIs.

### Configurable properties

| Environment Variables                    | Description                                                                                        | Value                                            |
| ---------------------------------------- |----------------------------------------------------------------------------------------------------|--------------------------------------------------|
| `https://niuatt.niua.in`             | This is the link to the UPYOG Portal, which differs based on the environment.                      | https://upyog.niua.org/digit-ui/employee/user/login |
| `egov.mdms.search.endpoint`     | This is the mdms (Master Data Management System) endpoint to which system makes the for mdms data. |                     /egov-mdms-service/v1/_search                             |
| `egov.usr.events.create.topic`           | Persister create topic configured in application.properties file.                                  | persist-user-events-async                                                 |
| `egov.billingservice.host`              | This is the billing service endpoint for any bill, demand call.                                    | http://billing-service:8080                      |



## Billing Integration

| Action | Endpoint |
|--------|----------|
| Fetch Tax Heads | `/billing-service/taxheads/_search` |
| Create Demand | `/billing-service/demand/_create` |
| Fetch Bill | `/billing-service/bill/v2/_fetchbill` |

## Notification System

- Notifications via SMS/Email are enabled:
  - `notification.sms.enabled=true`
  - `notification.email.enabled=true`
- Templates are managed using **Localization Service**

## Scheduler

| Property | Description | Value |
|----------|-------------|-------|
| `scheduler.sv.expiry.enabled` | Enables scheduled expiry job | `true` |

### API Details

- pet application - The pet application set of API's can be used to create and search application. Update application workflow.

##### Method
- **a) Create API `pet-registration/_create` :** This Api is used to create new Pet Application.
- **a) update API `pet-registration/_update` :** This Api is used to update the Created Pet Application
- **a) Search API  `pet-registration/_search` :** This Api is used to search the Pet Application

### Kafka Consumers
- Following are the Producer topic.

  ptr.kafka.create.topic=save-ptr-application

  ptr.kafka.update.topic=update-ptr-application

### Kafka Producers

- Following are the Producer topic.

  ptr.kafka.create.topic=save-ptr-application 

  ptr.kafka.update.topic=update-ptr-application
