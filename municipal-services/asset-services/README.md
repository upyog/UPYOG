# Asset Service

[![Version](https://img.shields.io/badge/version-2.0.0-blue.svg)](CHANGELOG.md)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

The **Asset Service** is part of the UPYOG application, providing a comprehensive digital interface for employees to register, manage, assign, dispose, and maintain assets for Urban Local Bodies (ULBs). This service has been upgraded to support LTS (Long Term Support) with modern Java and Spring Boot versions.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL 12+
- Apache Kafka 2.8+



---

## 📋 Introduction

The **UPYOG Asset Management Module** empowers employees to manage comprehensive asset lifecycle efficiently. The module provides the following functionalities:

### Core Features
- **Asset Registration**: Complete asset registration with documents and address details
- **Asset Search & Management**: Advanced search capabilities with multiple filters
- **Asset Assignment**: Assign assets to employees with full audit trail
- **Asset Disposal**: Manage asset disposal process with approval workflows
- **Asset Maintenance**: Track maintenance activities and schedules
- **Depreciation Calculation**: Automated asset depreciation processing
- **Workflow Integration**: Complete workflow support for all asset operations
- **Document Management**: Attach and manage documents for all asset operations

### UPYOG Services Integration
- **eGov-mdms**: Master data management
- **eGov-persister**: Data persistence layer
- **eGov-idgen**: ID generation service
- **eGov-user**: User management
- **eGov-localization**: Multi-language support
- **eGov-workflow-service**: Workflow management
- **eGov-filestore**: Document storage

---

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.2
- **Java Version**: 17 (LTS)
- **Database**: PostgreSQL
- **Message Queue**: Apache Kafka
- **Build Tool**: Maven
- **API Documentation**: OpenAPI 3.0 (Swagger)
- **Validation**: Jakarta Bean Validation

---

## 📚 API Documentation

### Asset Management APIs

#### Core Asset Operations
- **`POST /v1/assets/_create`**: Create new asset
- **`POST /v1/assets/_update`**: Update existing asset
- **`POST /v1/assets/_search`**: Search assets with filters

#### Asset Assignment APIs
- **`POST /v1/assets/assignment/_create`**: Create asset assignment
- **`POST /v1/assets/assignment/_update`**: Update asset assignment
- **`POST /v1/assets/assignment/_search`**: Search asset assignments

#### Asset Disposal APIs
- **`POST /v1/disposal/_create`**: Create asset disposal
- **`POST /v1/disposal/_update`**: Update asset disposal
- **`POST /v1/disposal/_search`**: Search asset disposals

#### Asset Maintenance APIs
- **`POST /maintenance/v1/_create`**: Create maintenance record
- **`POST /maintenance/v1/_update`**: Update maintenance record
- **`POST /maintenance/v1/_search`**: Search maintenance records

#### Depreciation APIs
- **`POST /v1/assets/depreciation/_process`**: Trigger depreciation calculation
- **`POST /v1/assets/depreciation/list`**: Get depreciation history



---

## 🗄️ Database Schema

### Core Tables
- **`eg_asset_assetdetails`**: Main asset information
- **`eg_asset_document`**: Asset documents
- **`eg_asset_addressDetails`**: Asset address information
- **`eg_asset_assignmentdetails`**: Asset assignments
- **`eg_asset_disposal_details`**: Asset disposal records
- **`eg_asset_maintenance_details`**: Asset maintenance records
- **`eg_asset_auditdetails`**: Audit trail

### Database Migration
The service uses Flyway for database migrations. Migration scripts are located in:
```
src/main/resources/db/migration/main/
```

