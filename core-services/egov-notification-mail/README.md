
# Mail Notification Service (egov-notification-mail)

Mail service enables the email notification to the user. The functionality is exposed via REST API.

### DB UML Diagram



### Service Dependencies

- User Service (`egov-user`)
- Localization Service (`egov-localization`)

### Swagger API Contract

NA

## Service Details

Mail Notification Service (egov-notification-mail) is a consumer which listens to the egov.core.notification.email topic, reads the message and generates email using SMTP Protocol.
The services needs the the senders email configured. On the other hand, if senders email is not configured, the services gets the email id by internally calling 
User Service (egov-user) to fetch email id. Once the email is generated, the content is localized by Localization Service (egov-localization) after which its been notified to the email id.

### API Details

NA

### Kafka Consumers

- egov.core.notification.email : egov-notification-mail listens to this topic to listen for the updates on emails and then to send notifications to user.

### Kafka Producers

NA
