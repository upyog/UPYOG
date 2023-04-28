# User Event Service (egov-user-event)

The objective of this service is to create a common point to manage all the events generated for the user in the system. 
Events include updates from multiple applications like Property Tax, PGR, Trade License etc, events created by the employee addressing the citizen etc. 
This service provides APIs to create , update and search such events for the user.

### DB UML Diagram

- TBD

### Service Dependencies

- MDM Service (egov-mdms-service)
- Localization Service (egov-localization)

### Swagger API Contract

Link to the swagger API contract yaml and editor link like below

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/user-events.yml#!/
https://github.com/upyog/UPYOG/blob/dev/municipal-services/docs/user-events.yml


## Service Details

This service manages user events on the UPYOG platform, which means all the events about which the user (essentially citizen) has to be notified are stored and retrieved through this service. 
Events can be created either by an API call or through pushing records to the Kafka Queue.

**Configurable Properties:**

Following are the properties in application.properties file in egov-user-events service which are configurable.

| Property                     | Value    | Remarks                    | 
| -----------------------------| ---------| ---------------------------|
| `mseva.notif.search.offset`  | 0        | Default pagination offset. |
| `mseva.notif.search.limit`   | 200      | Default pagination limit.  |


### API Details

`/_create` : API to create events in the system.

`/_update` : API to update events in the system.

`/_search` : API to search events in the system.

`/notification/_count` : API to fetch the count of total, unread, read notifications.

`/lat/_update` : API to update the last-login-time of the user. We store last-login-time of the user through this API thereby deciding which notifications have been read.


### Reference Document

All the details and configurations on the services are explained in the document [User Events Services](https://upyog-docs.gitbook.io/upyog-v-1.0/reference-applications/products-and-modules/events-and-notifications/user-events-services)

### Kafka Consumers

`persist-user-events-async` : Topic to which the user-events consumer is subscribed. Producers willing to create events must push records to this topic.
`update-user-events-async` : Topic to which the user-events consumer is subscribed. Producers willing to update events must push records to this topic.

### Kafka Producers

`kafka.topics.persister.save.events` : This is the persister topic onto which user-events pushes records for persistence. This is for creating events. 
`kafka.topics.persister.update.events` : This is the persister topic onto which user-events pushes records for persistence. This is for updating events.
`kafka.topics.lat.details` : This is the persister topic onto which user-events pushes records for persistence. This is for storing last-access-time / last-login-time of the user.
