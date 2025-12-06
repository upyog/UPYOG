# Asset Calculator Utility

The **Apply Workflow Utility ** is designed to process (Create/Update). This document provides details on how to set up and use the Asset Calculator service, along with its key functionalities and dependencies.

---


## Service Dependencies

The Asset Calculator service interacts with the following dependent services:
- **mdms-service**
- **workflow-v2**
- **user-service**

---

## Database Schema

The database schema is designed to store billing slab details and related configurations. Refer to the UML diagram in `asset-calculator.png` for detailed database relationships.

---

## API Details

| **Endpoint**                      | **Description**      |
|-----------------------------------|----------------------|
| `/apply-workflow/api/v2/_process` | Calculates           |

---

## Configuration

### Server Settings
```properties
server.context-path=/apply-workflow

```

### MDMS Service
```properties
egov.mdms.host=http://localhost:8094
egov.mdms.search.endpoint=/egov-mdms-service/v1/_search
```

## Swagger API Contract

Access the Swagger API contract via the following links:
- [YAML File](https://raw.githubusercontent.com/egovernments/municipal-services/master/docs/fsm/Fsm_Apply_Contract.yaml)
- [Swagger Editor](https://editor.swagger.io/)

---

## Postman Collection

The Postman collection is available for testing the APIs:
- [Postman Collection]()

---

For additional details or contributions, visit the [GitHub Repository](https://github.com/upyog/UPYOG).
