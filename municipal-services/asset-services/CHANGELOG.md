# Changelog

All notable changes to the Asset Services project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2025-01-13

### Added
- LTS upgrade to Spring Boot 3.4.4
- Java 17 support
- Jakarta EE migration (javax to jakarta)
- Asset creation API (`/v1/assets/_create`)
- Asset search API (`/v1/assets/_search`)
- Asset update API (`/v1/assets/_update`)
- Asset assignment APIs (`/assignment/_create`, `/assignment/_update`, `/assignment/_search`)
- Asset disposal APIs (`/disposal/_create`, `/disposal/_update`, `/disposal/_search`)
- Asset maintenance APIs (`/maintenance/v1/_create`, `/maintenance/v1/_update`, `/maintenance/v1/_search`)
- Asset depreciation APIs (`/depreciation/_process`, `/depreciation/list`)
- Enhanced audit details support
- Document management for assets
- Address details integration
- Workflow integration for asset lifecycle
- MDMS integration for master data
- Kafka integration for async processing
- PostgreSQL database support with Flyway migrations

### Changed
- Upgraded from Spring Boot 2.x to 3.4.4
- Updated Java version from 8 to 17
- Migrated from javax to jakarta namespace
- Updated dependency versions for LTS compatibility
- Enhanced error handling and validation
- Improved API response structure

### Fixed
- JsonIgnore annotation issues in Asset and AssetAssignment models
- Audit details serialization/deserialization
- Persister configuration compatibility
- Database constraint handling
- Kafka message processing

### Technical Details
- **Framework**: Spring Boot 3.4.4
- **Java Version**: 17
- **Database**: PostgreSQL
- **Message Queue**: Apache Kafka
- **Build Tool**: Maven
- **API Documentation**: OpenAPI 3.0 (Swagger)

### Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- PostgreSQL Driver
- Flyway Core
- Lombok
- Jackson Databind
- EGOV Tracer
- EGOV MDMS Client
- EGOV Digit Models

### Database Schema
- `eg_asset_assetdetails` - Main asset information
- `eg_asset_document` - Asset documents
- `eg_asset_addressDetails` - Asset address information
- `eg_asset_assignmentdetails` - Asset assignments
- `eg_asset_disposal_details` - Asset disposal records
- `eg_asset_maintenance_details` - Asset maintenance records
- `eg_asset_auditdetails` - Audit trail

### API Endpoints
- **Assets**: `/v1/assets/*`
- **Assignment**: `/v1/assets/assignment/*`
- **Disposal**: `/v1/disposal/*`
- **Maintenance**: `/maintenance/v1/*`
- **Depreciation**: `/v1/assets/depreciation/*`

## [1.0.0] - 2024-04-12

### Added
- Initial release of Asset Services
- Basic asset management functionality
- Spring Boot 2.x implementation
- Java 8 support