# egf-master service

egf master service is used to search finance masters.

## Version
2.0.0

## Technology Stack
- **Java**: 17
- **Spring Boot**: 3.2.2
- **Database**: PostgreSQL 42.6.0
- **Search Engine**: Elasticsearch 6.2.4
- **Build Tool**: Maven
- **Documentation**: Swagger/OpenAPI 3.0

## Service Dependencies
- PostgreSQL Database
- Elasticsearch Cluster
- Tracer Service (2.9.0-SNAPSHOT)

### API Documentation

- **Swagger UI**: http://localhost:8280/egf-master/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8280/egf-master/v3/api-docs
- Access the interactive API documentation at: `/swagger-ui.html` (when service is running)


### DB UML Diagram

NA

### Service Dependencies

NA

### Prerequisites
- Java 17
- Maven 3.6+
- PostgreSQL 12+
- Elasticsearch 6.2.4



### Swagger API Contract

https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/business-services/Docs/egf-master-v1.0.0%20.yaml#!/

## Service Details

egf master service is used to search finance masters.

### API Details

`/chartofaccounts/_search` : API to search chart of accounts 
`/financialstatuses/_search` : API to search the instrument status 
`/bankaccounts/_search` : API to search bank accounts

### Kafka Consumers

NA

### Kafka Producers

NA
