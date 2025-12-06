<<<<<<< HEAD
# Request Service

A Spring Boot microservice for managing municipal service requests including water tanker and mobile toilet bookings within the UPYOG platform.

## Overview

The Request Service is a core component of the UPYOG (Urban Platform for Your Organization and Governance) ecosystem that enables citizens to book and manage essential municipal services. It provides comprehensive REST APIs for creating, updating, and searching service requests with built-in validation, workflow management, and notification capabilities.

### Key Features

- **Water Tanker Booking**: Complete lifecycle management for water tanker requests with scheduling and delivery tracking
- **Mobile Toilet Booking**: Full service management for mobile toilet reservations and maintenance
- **Search & Filter**: Advanced search capabilities with multiple criteria and pagination
- **Workflow Integration**: Built-in workflow engine for approval processes
- **Validation**: Comprehensive request validation and error handling
- **Notification System**: SMS and email notifications for booking updates
- **Payment Integration**: Integrated billing and payment processing
- **User Management**: Citizen and employee profile management
- **OpenAPI Documentation**: Interactive API documentation with Swagger UI

## Technology Stack

- **Java 17** - Programming language
- **Spring Boot 3.2.2** - Application framework
- **PostgreSQL** - Primary database
- **Apache Kafka** - Message streaming platform
- **Flyway** - Database migration tool
- **SpringDoc OpenAPI** - API documentation
- **Maven** - Build and dependency management
- **Docker** - Containerization support

## Architecture

The service follows a layered architecture pattern:

```
┌─────────────────────────────────────────┐
│              REST Controllers           │
├─────────────────────────────────────────┤
│              Service Layer              │
├─────────────────────────────────────────┤
│             Validator Layer             │
├─────────────────────────────────────────┤
│              Repository Layer           │
├─────────────────────────────────────────┤
│              Database (PostgreSQL)      │
└─────────────────────────────────────────┘
```

### External Integrations

- **Workflow Service**: For approval workflows
- **Billing Service**: For payment processing
- **User Service**: For citizen/employee management
- **Notification Service**: For SMS/email alerts
- **MDMS Service**: For master data management
- **ID Generation Service**: For unique booking IDs

## API Endpoints

### Water Tanker Services

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/request-service/water-tanker/v1/_create` | Create new water tanker booking | Required |
| POST | `/request-service/water-tanker/v1/_search` | Search water tanker bookings | Required |
| POST | `/request-service/water-tanker/v1/_update` | Update existing water tanker booking | Required |

### Mobile Toilet Services

| Method | Endpoint | Description | Authentication |
|--------|----------|-------------|----------------|
| POST | `/request-service/mobile-toilet/v1/_create` | Create new mobile toilet booking | Required |
| POST | `/request-service/mobile-toilet/v1/_search` | Search mobile toilet bookings | Required |
| POST | `/request-service/mobile-toilet/v1/_update` | Update existing mobile toilet booking | Required |

## Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **PostgreSQL 12+**
- **Apache Kafka** (for message processing)
- **Docker** (optional, for containerized deployment)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd request-service
   ```

2. **Configure database**
   Update `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/your_database
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   # Flyway Configuration
   spring.flyway.url=jdbc:postgresql://localhost:5432/your_database
   spring.flyway.user=your_username
   spring.flyway.password=your_password
   ```

3. **Configure Kafka**
   ```properties
   # Kafka Configuration
   kafka.config.bootstrap_server_config=localhost:9092
   spring.kafka.consumer.group-id=citizen-request-service
   ```

4. **Build the application**
   ```bash
   mvn clean install
   ```

5. **Run database migrations**
   ```bash
   mvn flyway:migrate
   ```

6. **Start the application**
   ```bash
   mvn spring-boot:run
   ```

   Or run the JAR file:
   ```bash
   java -jar target/citizen-request-service-2.0.0.jar
   ```

### Docker Deployment

1. **Build Docker image**
   ```bash
   docker build -t request-service:2.0.0 .
   ```

2. **Run with Docker Compose**
   ```bash
   docker-compose up -d
   ```

## Configuration

### Application Properties

Key configuration parameters:

```properties
# Server Configuration
server.port=8081
server.servlet.context-path=/request-service

# Database Configuration
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres

# Kafka Configuration
kafka.config.bootstrap_server_config=localhost:9092

# Service Limits
upyog.request.service.default.offset=0
upyog.request.service.default.limit=10
upyog.request.service.max.limit=50

# Workflow Configuration
is.workflow.enabled=true
egov.workflow.host=http://localhost:8280

# Notification Configuration
notif.sms.enabled=true
notif.email.enabled=true
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Database host | localhost |
| `DB_PORT` | Database port | 5432 |
| `DB_NAME` | Database name | postgres |
| `KAFKA_BROKERS` | Kafka broker list | localhost:9092 |
| `WORKFLOW_HOST` | Workflow service URL | http://localhost:8280 |

## API Documentation

Once the application is running, access the interactive API documentation:

- **Swagger UI**: http://localhost:8081/request-service/swagger-ui/index.html#/


## Project Structure

```
request-service/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/upyog/rs/
│   │   │       ├── web/
│   │   │       │   └── controllers/     # REST Controllers
│   │   │       ├── service/             # Business Logic Layer
│   │   │       ├── validator/           # Request Validators
│   │   │       ├── repository/          # Data Access Layer
│   │   │       ├── util/                # Utility Classes
│   │   │       ├── constant/            # Application Constants
│   │   │       ├── config/              # Configuration Classes
│   │   │       └── web/models/          # Request/Response Models
│   │   └── resources/
│   │       ├── application.properties   # Configuration
│   │       ├── db/migration/           # Database Migration Scripts
│   │       ├── workflows/              # Workflow Definitions
│   │       └── Postman-Collection/     # API Testing Collection
│   └── test/
│       └── java/                       # Unit and Integration Tests
├── target/                             # Build Output
├── pom.xml                            # Maven Configuration
├── Dockerfile                         # Docker Configuration
├── docker-compose.yml                 # Docker Compose Setup
└── README.md                          # This file
```

## Development

### Building

```bash
# Compile source code
mvn clean compile

# Package application
mvn package

# Skip tests during build
mvn clean install -DskipTests
```

### Database Management

```bash
# Run migrations
mvn flyway:migrate

# Check migration status
mvn flyway:info

# Clean database (development only)
mvn flyway:clean
```

### API Testing

Use the provided Postman collection:
- Import `src/main/resources/Postman-Collection/Request-Service_MT_WT.json`
- Configure environment variables
- Run the collection



# Database Connection Pool
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000


## Dependencies

### Core Dependencies

- **Spring Boot Starter Web**: REST API framework
- **Spring Boot Starter JDBC**: Database connectivity
- **PostgreSQL Driver**: Database driver
- **Flyway Core**: Database migration
- **SpringDoc OpenAPI**: API documentation
- **Spring Kafka**: Message streaming
- **Spring Boot Starter Validation**: Input validation

### UPYOG Dependencies

- **DIGIT Models**: UPYOG platform models
- **Tracer**: Logging and tracing utilities
- **MDMS Client**: Master data management

### Utility Dependencies

- **Lombok**: Code generation
- **Jackson**: JSON processing
- **OWASP HTML Sanitizer**: XSS protection

### Code Style

- Follow Java coding conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Maintain test coverage above 80%

## Support

### Documentation

- **API Documentation**: Available at `/swagger-ui.html` when running
- **UPYOG Documentation**: https://upyog-docs.gitbook.io/upyog-v-2.0/reference-applications/products-and-modules
- **Spring Boot Reference**: [https://docs.spring.io/spring-boot/docs/current/reference/html/](https://docs.spring.io/spring-boot/docs/current/reference/html/)

### Getting Help

For issues and questions:
- Check the API documentation at `/swagger-ui.html`
- Verify database and Kafka connectivity
- Check UPYOG community forums
- Create GitHub issues for bugs or feature requests

### Contact

- **Project Team**: UPYOG Development Team
- **Email**: cdg-contact@niua.org
- **Documentation**: https://upyog-docs.gitbook.io/upyog-v-2.0/reference-applications/products-and-modules

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Version History

- **2.0.0** - Current version with Spring Boot 3.2.2 and Java 17 support
- **1.x.x** - Legacy versions with Spring Boot 2.x

---

**Note**: This service is part of the UPYOG platform. For complete setup and integration, refer to the main UPYOG documentation.
=======
# Swagger generated server

Spring Boot Server 


## Overview  
This server was generated by the [swagger-codegen](https://github.com/swagger-api/swagger-codegen) project.  
By using the [OpenAPI-Spec](https://github.com/swagger-api/swagger-core), you can easily generate a server stub.  
This is an example of building a swagger-enabled server in Java using the SpringBoot framework.  

The underlying library integrating swagger to SpringBoot is [springfox](https://github.com/springfox/springfox)  

Start your server as an simple java application  

You can view the api documentation in swagger-ui by pointing to  
http://localhost:8080/  

Change default port value in application.properties

This service facilitates the booking of water tankers, allowing users to request and manage their water tanker reservations efficiently. It supports features such as booking creation, scheduling delivery times, and managing associated details such as tanker type and quantity.

>>>>>>> master-LTS
