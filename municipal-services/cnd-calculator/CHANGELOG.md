# Changelog

All notable changes to the CND Calculator service will be documented in this file.

## [2.0.0] - 2024-01-15

### 🚀 Major Upgrades
- **Java 17**: Upgraded from Java 8/11 to Java 17 for improved performance and security
- **Spring Boot 3.2.2**: Major framework upgrade from Spring Boot 2.x to 3.2.2
- **PostgreSQL 42.7.1**: Updated PostgreSQL driver for better compatibility
- **SpringDoc OpenAPI 3.0**: Migrated from Springfox to SpringDoc OpenAPI 2.3.0

### ✨ New Features
- **Enhanced API Documentation**: Swagger UI now available at `/cnd-calculator/swagger-ui/index.html`
- **Improved Fee Calculation**: Enhanced calculation logic for CND waste management fees
- **MDMS Integration**: Better integration with Master Data Management System
- **Kafka Event Processing**: Enhanced event-driven architecture support

### 🔧 Technical Improvements
- **Package Structure**: Reorganized to `org.upyog.cdwm.calculator` namespace
- **Configuration Management**: Improved application properties structure
- **Database Migrations**: Flyway integration for database schema management
- **Test Coverage**: Added comprehensive unit tests with Spring Boot Test

### 🛠️ Infrastructure Changes
- **Build System**: Maven configuration optimized for Java 17
- **Docker Support**: Enhanced containerization capabilities
- **Dependency Management**: Updated all dependencies to latest stable versions

### 📋 API Changes
- **Endpoint**: Main calculation endpoint remains `/cnd-calculator/v1/_calculate`
- **Request/Response**: Enhanced models with better validation
- **Error Handling**: Improved error responses and logging

### 🔒 Security Enhancements
- **Dependency Updates**: All security vulnerabilities addressed
- **Input Validation**: Enhanced request validation using Spring Boot Validation
- **Database Security**: Improved connection security and configuration

### 🐛 Bug Fixes
- Fixed calculation accuracy issues
- Resolved database connection pooling problems
- Corrected Kafka consumer configuration
- Fixed API documentation generation

### 📚 Documentation
- **README**: Comprehensive documentation with quick start guide
- **API Docs**: Auto-generated OpenAPI 3.0 specification
- **Configuration**: Detailed configuration examples

### 🔄 Migration Notes
- **Java 17 Required**: Minimum Java version is now 17
- **Spring Boot 3.x**: Applications integrating with this service may need updates
- **Database**: Run Flyway migrations for schema updates
- **Configuration**: Update application properties as per new format

### 🏗️ Development
- **IDE Support**: Enhanced IDE integration with better autocomplete
- **Build Performance**: Faster build times with optimized Maven configuration
- **Testing**: Improved test execution with Spring Boot 3.x test framework

---

## Previous Versions

### [1.x.x] - Legacy
- Initial implementation with Spring Boot 2.x
- Basic CND fee calculation functionality
- PostgreSQL integration
- Kafka messaging support

## Author
- Shivank-NUDM