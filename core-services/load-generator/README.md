# eGov Load Generator

## Overview

The eGov Load Generator is a generic framework designed to perform concurrent API load testing across eGov modules. It enables configurable load generation, workflow execution, and performance monitoring without requiring module-specific changes to the core engine.

The framework currently supports the Property Tax (PT) workflow and is designed to be easily extensible for additional modules.

---

## Features

- Generic module-based architecture
- Concurrent request execution using Spring WebFlux
- Configurable thread pool and request timeout
- Retry mechanism for transient failures
- Job-based execution with status tracking
- Throughput and execution metrics
- Reusable fake data generation utilities
- Configurable service endpoints
- Extensible design for supporting additional eGov modules

---

## Supported Workflow

Currently supported workflow for **Property Tax (PT)**:

```
CREATE
   ↓
SEARCH
   ↓
VERIFY
   ↓
SEARCH
   ↓
FORWARD
   ↓
SEARCH
   ↓
APPROVE
```

---

## Configuration

Configure the target service endpoints in `application.properties`.

Example:

```properties
egov.pt.host=http://pt:8080
load.generator.thread.pool.size=10
load.generator.webclient.timeout.seconds=120
```

---

## REST APIs

### Start Load Generation

**POST**

```
/load-generator/create
```

Sample Request

```json
{
  "module": "PT",
  "tenantId": "pb.amritsar",
  "count": 100
}
```

Sample Response

```json
{
  "jobId": "8b2c2b54-f0cb-4af0-8c1d-xxxxxxxxxxxx",
  "status": "IN_PROGRESS"
}
```

---

### Check Job Status

**GET**

```
/load-generator/status/{jobId}
```

Sample Response

```json
{
  "jobId": "8b2c2b54-f0cb-4af0-8c1d-xxxxxxxxxxxx",
  "status": "COMPLETED",
  "totalRequests": 100,
  "successCount": 100,
  "failureCount": 0,
  "averageResponseTime": 325
}
```

---

## Architecture

```
Controller
      │
      ▼
LoadGeneratorService
      │
      ▼
LoadGeneratorWorker (@Async)
      │
      ▼
LoadExecutor
      │
      ▼
ModuleGenerator (PT)
      │
      ▼
eGov Services
```

---

## Extending the Framework

To support a new module:

1. Implement the `ModuleGenerator` interface.
2. Register the implementation as a Spring bean.
3. Configure the target service endpoint.
4. Invoke the API with the corresponding module name.

No changes are required in the core execution engine.

---

## Technology Stack

- Java 17
- Spring Boot
- Spring WebFlux
- Reactor
- Maven
- Java Faker

---

## Future Enhancements

- Support for additional eGov modules
- Dashboard for live job monitoring
- CSV/Excel report generation
- Distributed load generation
- Configurable workflows
