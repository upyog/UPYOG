# Asset Calculator Service

The **Asset Calculator Service** is designed to manage asset depreciation, re-evaluations etc based on master data  and calculate charges for assets within the ecosystem. This document provides details on how to set up and use the Asset Calculator service, along with its key functionalities and dependencies.

---

## Features
2. **Depreciation Calculation**:
    - Calculate and generate Depreciation based on configured slabs.
---

## Service Dependencies

The Asset Calculator service interacts with the following dependent services:
- **mdms-service**
- **workflow-v2**
- **user-service**
- **vendor**
- **vehicle**

---

## Database Schema

The database schema is designed to store billing slab details and related configurations. Refer to the UML diagram in `asset-calculator.png` for detailed database relationships.

---

## API Details

| **Endpoint**            | **Description**      |
|--------------------------|----------------------|
| `_calculate`             | Calculates           |

---

## Kafka Topics

### Producers

- **`xyz`**: Xyz.

---

## Configuration

### Server Settings
```properties
server.context-path=/asset-calculator

```


### Persister Topics
```properties
persister.save.billing.slab.topic=save-asset-depreciation-slab
```

### MDMS Service
```properties
egov.mdms.host=http://localhost:8094
egov.mdms.search.endpoint=/egov-mdms-service/v1/_search
```


### Asset Registry
```properties
egov.asset.create.endpoint=/_create
egov.asset.update.endpoint=/_update
egov.asset.search.endpoint=/_search
```

---

## Swagger API Contract

Access the Swagger API contract via the following links:
- [YAML File](https://raw.githubusercontent.com/egovernments/municipal-services/master/docs/fsm/Fsm_Apply_Contract.yaml)
- [Swagger Editor](https://editor.swagger.io/)

---

## Postman Collection

The Postman collection is available for testing the APIs:
- [Postman Collection](https://www.getpostman.com/collections/8b9eb951a810486f41a4)

---

## Future Enhancements

- Detailed documentation for logic (TBD).
- Advanced search and reporting capabilities for slabs.

---

For additional details or contributions, visit the [GitHub Repository](https://github.com/egovernments).
