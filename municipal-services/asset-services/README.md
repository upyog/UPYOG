# Asset Service

The **Asset Service** is part of the UPYOG application, which provides a digital interface for employees to register and manage assets for Urban Local Bodies (ULBs). The service allows employees to register, update, and search for assets. Authorized employees, as per the configured workflow, can verify and approve assets.

---

## Introduction

The **UPYOG Asset Management Module** empowers employees to manage asset-related data efficiently. The module provides the following functionalities:

- **Asset Registration**: Employees can add and update assets using a detailed registration form.
- **Asset Search**: Employees can search and view the details of assets they registered.
- **Workflow Integration**: Authorized employees can verify and approve assets as per the configured workflows.

- **UPYOG Services used**:
        - eGov-mdms
        - eGov-persister
        - eGov-idgen
        - eGov-user
        - eGov-localization
        - eGov-workflow-service

---

## Features

- **Asset Management**:
    - Register new assets.
    - Update and manage existing assets.
    - Assign assets to users or entities.

- **Workflow Integration**:
    - Supports workflows for asset approvals and transitions.

- **Localization**:
    - Integrated localization support for state-level and tenant-level messages.

- **Dynamic Configuration**:
    - Integrated with MDMS for fetching dynamic configurations.

---

## API Details

- **Asset Details Management**:
    - **`/asset-services/api/v1/_create`**: API to create new asset details.
    - **`/asset-services/api/v1/_update`**: API to update existing asset details.
    - **`/asset-services/api/v1/_search`**: API to search for asset details based on filters.

- **Asset Assignment Management**:
    - **`/asset-assignment/api/v1/_create`**: API to assign assets to entities or users.
    - **`/asset-assignment/api/v1/_update`**: API to update asset assignments.

---

## Service Configuration

### **Server Configurations**
```properties
server.context-path=/asset-services
server.servlet.context-path=/asset-services
server.port=8098
app.timezone=UTC
