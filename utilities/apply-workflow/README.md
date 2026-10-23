# Apply Workflow Utility

The **Apply Workflow Utility** is designed to process workflow operations such as **Create** and **Update** for workflow configurations.  
This service interacts with workflow APIs and MDMS data to dynamically create or update workflow definitions based on the provided payload.

---

## Features

- Create workflow configurations dynamically
- Update existing workflow configurations
- Fetch workflow definitions from MDMS
- Merge workflow states and actions
- Validate workflow existence before create/update
- REST API based workflow processing

---

## Service Dependencies

The Apply Workflow Utility interacts with the following services:

- **mdms-service**
- **workflow-v2**
- **user-service**

---

## Technology Stack

- Java 17
- Spring Boot 3.4.4
- PostgreSQL
- Flyway
- MapStruct
- Lombok
- Spring Data JPA

---

## API Details

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/apply-workflow/api/v1/_process` | POST | Create or Update Workflow |

---

## Request Flow

1. Receive workflow payload
2. Fetch workflow configuration from MDMS
3. Check existing workflow in workflow-v2
4. Create workflow if not present
5. Update workflow if already present
6. Return workflow response

---

## Configuration

### Server Configuration

```properties
server.context-path=/apply-workflow
server.port=8080
```

### Workflow Service Configuration

```properties
workflow.context.path=http://localhost:8080
workflow.search.path=/egov-workflow-v2/egov-wf/businessservice/_search
workflow.create.path=/egov-workflow-v2/egov-wf/businessservice/_create
workflow.update.path=/egov-workflow-v2/egov-wf/businessservice/_update
```

### MDMS Configuration

```properties
egov.mdms.host=http://localhost:8094
egov.mdms.search.endpoint=/egov-mdms-service/v1/_search
```

### Database Configuration

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/egov_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=none
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

---

## Build Command

```bash
mvn clean install
```

---

## Run Application

```bash
mvn spring-boot:run
```

---

## Sample Request

```json
{
  "RequestInfo": {},
  "BusinessService": {
    "tenantId": "pg.citya",
    "uniqueIdentifier": "TRADE_LICENSE",
    "applyType": "CREATE"
  }
}
```

---

## Apply Types

| Apply Type | Description |
|------------|-------------|
| CREATE | Creates a new workflow |
| UPDATE | Updates an existing workflow |

---

## Database Table

### eg_mdms_data

Stores MDMS workflow configuration data.

| Column Name | Description |
|-------------|-------------|
| id | Primary Key |
| tenantid | Tenant Identifier |
| uniqueidentifier | Unique Workflow Identifier |
| schemacode | MDMS Schema Code |
| data | Workflow JSON Configuration |
| isactive | Active Status |

---

## Swagger/OpenAPI

Swagger UI:

```text
http://localhost:8080/apply-workflow/swagger-ui/index.html
```

OpenAPI Docs:

```text
http://localhost:8080/apply-workflow/v3/api-docs
```

---

## Postman Collection

The Postman collection is available for testing the APIs:
- [Postman Collection]()

---

## Project Structure

```text
src/main/java/org/egov/applyworkflow
├── config
├── repository
├── service
├── util
├── web/controller
├── web/model
└── web/model/workflow
```

---

## Error Handling

The service handles:

- Invalid apply type
- Missing workflow configuration
- Duplicate workflow creation
- Workflow update without existing workflow
- External service failures

---

## Future Enhancements

- Bulk workflow processing
- Workflow validation engine
- Version-based workflow management
- Audit history support

---

## Repository

For additional details or contributions, visit the [GitHub Repository](https://github.com/upyog/UPYOG).
