# Tree Pruning Service (tp-services)

A Spring Boot microservice for managing tree pruning booking applications in the UPYOG municipal services platform.

## Overview

The Tree Pruning Service enables citizens to book tree pruning services through a digital platform. It provides APIs for creating, updating, and searching tree pruning booking applications with integrated workflow management, billing, and notification capabilities.

## Features

- **Application Management**: Create, update, and search tree pruning booking applications
- **Workflow Integration**: Built-in workflow support for application processing
- **Billing Integration**: Automatic demand generation and billing
- **Notification System**: SMS and email notifications for application updates
- **User Management**: Citizen and employee user management
- **Document Support**: File upload and document management
- **Audit Trail**: Complete audit logging for all operations

## Technology Stack

- **Java**: 17
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL
- **Message Queue**: Apache Kafka
- **API Documentation**: SpringDoc OpenAPI 3
- **Build Tool**: Maven
- **Migration**: Flyway

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka
- Redis (for caching)


## API Endpoints

### Base URL: `http://localhost:8081/tp-services`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tree-pruning/v1/_create` | Create new tree pruning booking |
| POST | `/tree-pruning/v1/_search` | Search tree pruning bookings |
| POST | `/tree-pruning/v1/_update` | Update existing booking |

### API Documentation

Access Swagger UI at: `http://localhost:8081/tp-services/swagger-ui.html`

## Request/Response Examples

### Create Booking Request

```json
{
  "requestInfo": {
    "apiId": "tp-services",
    "ver": "1.0",
    "ts": 1642678200000,
    "action": "create",
    "did": "1",
    "key": "",
    "msgId": "search with from and to values",
    "authToken": "{{authToken}}"
  },
  "treePruningBookingDetail": {
    "tenantId": "pb.amritsar",
    "applicantDetail": {
      "name": "John Doe",
      "mobileNumber": "9876543210",
      "emailId": "john@example.com"
    },
    "address": {
      "locality": "Sector 1",
      "city": "Amritsar",
      "pincode": "143001"
    },
    "treeDetails": {
      "numberOfTrees": 5,
      "treeType": "Neem",
      "reason": "Overgrown branches"
    }
  }
}
```

### Search Request

```json
{
  "requestInfo": {
    "apiId": "tp-services",
    "ver": "1.0",
    "ts": 1642678200000,
    "action": "search"
  }
}
```

Query Parameters:
- `bookingNo`: Booking number
- `mobileNumber`: Applicant mobile number
- `status`: Application status
- `offset`: Pagination offset (default: 0)
- `limit`: Results limit (default: 10, max: 50)

## Configuration

### Key Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `server.port` | Service port | 8081 |
| `server.servlet.context-path` | Context path | /tp-services |
| `upyog.request.service.default.limit` | Default search limit | 10 |
| `upyog.request.service.max.limit` | Maximum search limit | 50 |
| `is.workflow.enabled` | Enable workflow | true |
| `notif.sms.enabled` | Enable SMS notifications | true |
| `notif.email.enabled` | Enable email notifications | true |

### Kafka Topics

| Topic | Purpose |
|-------|---------|
| `create-tree-pruning-booking` | New booking creation |
| `update-tree-pruning-booking` | Booking updates |
| `egov.core.notification.sms` | SMS notifications |
| `egov.core.notification.email` | Email notifications |

## Database Schema

The service uses the following main tables:
- `tp_booking_detail` - Main booking information
- `tp_applicant_detail` - Applicant information
- `tp_address` - Address details
- `tp_document` - Document attachments

## Workflow

The service integrates with DIGIT workflow engine supporting:
- **INITIATED** - Application submitted
- **PENDING_APPROVAL** - Under review
- **APPROVED** - Approved for service
- **REJECTED** - Application rejected
- **COMPLETED** - Service completed

## Error Handling

The service provides standardized error responses:

```json
{
  "responseInfo": {
    "apiId": "tp-services",
    "ver": "1.0",
    "ts": 1642678200000,
    "status": "FAILED"
  },
  "errors": [
    {
      "code": "INVALID_REQUEST",
      "message": "Invalid request parameters",
      "description": "Mobile number is required"
    }
  ]
}
```

## Monitoring and Logging

- **Health Check**: `GET /tp-services/actuator/health`
- **Metrics**: `GET /tp-services/actuator/metrics`
- **Logs**: Structured logging with correlation IDs

## Development

### Project Structure

```
src/main/java/org/upyog/tp/
├── config/          # Configuration classes
├── constant/        # Constants
├── enums/          # Enumerations
├── kafka/          # Kafka producers/consumers
├── repository/     # Data access layer
├── service/        # Business logic
├── util/           # Utility classes
├── validator/      # Request validation
└── web/           # Controllers and models
```

### Kubernetes

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tp-services
spec:
  replicas: 2
  selector:
    matchLabels:
      app: tp-services
  template:
    metadata:
      labels:
        app: tp-services
    spec:
      containers:
      - name: tp-services
        image: tp-services:2.0.0
        ports:
        - containerPort: 8081
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## Support

For issues and questions:
- Create an issue in the repository
- Contact the development team
- Check the UPYOG documentation

## License

This project is licensed under the MIT License - see the LICENSE file for details.

---

**Version**: 2.0.0
**Last Updated**: January 2025