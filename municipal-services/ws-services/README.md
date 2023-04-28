# Water Service (WS-Services)

This module created to manage Water Service connections against a Property in the system.

### DB UML Diagram

- NA

### Service Dependencies
- MDMS Service (egov-mdms service)
- Property Service (property-service)
- ID Gen Service (egov-idgen)
- Persister Service (egov-persister)
- Water Service Calculator (ws-calculator)
- Filestore Service (egov-filestore)
- PDF Service (pdf-service)

### Swagger API Contract

- Please refer to the [Swagger API Contract](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/water-sewerage-services.yaml#!/) for ws-services to understand the structure of APIs and to have visualization of all internal APIs.

## Service Details

**Functionality:**
- Apply for water connection.
- Searching for water connections.
- Can take different action based on state (Workflow) 
- Notification based on the application state.```

### API Details

`BasePath` /ws-services/wc/[API endpoint]

##### Method

a) `_create`

   - WaterService is created by calling ws-services/wc/_create api.
   
   - The response contains the WaterConnection object with its assigned ApplicationId of that WaterService Connection.

b) `_update`

   -  Created WaterService application needs to be approved and activated, and these are done by calling the ws-services/wc/_update api.
   
   - Once the application activated new Water Connection Number will be generated, and the same would be updated in the WaterConnection Object.

c) `_search`

   -  WaterService Application/Connection  can be searched based on several search parameters as detailed in the swagger yaml [[Water Sewerage API](https://github.com/upyog/UPYOG/blob/master/municipal-services/docs/water-sewerage-services.yaml)]

### Kafka Consumers

- Following are the Consumer topic.
    - **save-ws-connection**, **update-ws-connection**, **update-ws-workflow** and **egov.collection.payment-create** this topic are use to create notification to send to water connection owner.
    - **create-meter-reading** :- This topic is use to save intital meter reading of water connection.
### Kafka Producers
- Following are the Producer topic.
    - **save-ws-connection** :- This topic is used to create new water connection application in the system.
    - **update-ws-connection** :- This topic is used to update the existing water connection application in the systen.
    - **update-ws-workflow** :- This topic is used to update the process instance of the water connection application.
    - **egov.core.notification.sms** :- This topic is used to send noification to the phone number of the water connection owner.
