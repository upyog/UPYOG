# Bulding Plan Approval

Module is used to apply for Buildin Plan Approval as well as Bulding Plan Occupancy Certificate.

### Building Plan Approval Flow
- Create
   - Bulidling Plan Approval application with bpa-services/v1/bpa/_create api.
   - The response contains the BPA object with its assigned applicationNumber and application Fee Generated.
- Update
   -  On created BPA multiple assessments can be done by calling the bpa-servcies/v1bpa//_update api.
    - Validations are carried out to verify the authenticity of the request and genrate applciation fee which will be paid by the architect and gets approval number genrated on approval .
- Search
   -  BPA can be searched based on several search parameters as detailed in the swagger yaml [[ Resources ](#resources)] .


### Project Structure 
*Packages*
 - config - Contains all the configuration properties related to module
 - service - Consists of all services containing the business logic.
 - util - Contains utility functions and constatns.
 - validator - Contains all validation code
 - repository - Fetch data from dependent micro services
 - repository/rowmapper - Rowmappers to convert db query results to object
 - repository/builder - Contains query builder for search
 - web/controllers - Controllers for the app.
 - web/models - POJO for the module.
 - consumer - Contains all the kafka consumers
 - producer - Contains kafka producer


### Resources
- Granular details about the API's can be found in the [Swagger API definition](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/bpa/bpa-service.yaml#!/)
- 


## Build & Run


    mvn clean install
    java -jar target/bpa-services-1.1.0-SNAPSHOT.jar


## Dependencies


- Postgres database to store property data.

- Location service to validate locality and set area code.

- ID Gen Module to generate unique PropertyId and assessmentNumber.

- Persister module for persistence.

- BPA calculator module to calculate tax for the given property.

- MDM service to verify master data

- User Service to create users of the property owners

- SMS Notification Service to send notifications related to registration and payment

- ECCR Service
