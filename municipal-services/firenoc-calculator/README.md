# Firenoc Calculator (firenoc-calculator)

The main objective of the Firenoc calculator module is to create, update, search billing slabs. Calculate and generate FireNOC charges.

### DB UML Diagram
- To Do

### Service Dependencies
- Billing Service (billing-service)
- MDM Service (egov-mdms)
- FireNOC Service (firenoc-service)

### Swagger API Contract

Please refer to the [Swagger API Contract](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/fire_noc_calculation_service.yaml#!/) for firenoc calculator to understand the structure of APIs and to have visualization of all internal APIs.


## Service Details

**MDMS COnfiguration**

Firenoc Calculator makes calls to MDM Service (egov-mdms-service) to fetch required masters. These are significant in validations of application.


   | Fire-NOC masters                                                                                                                           | Description                                                                                                    |  
   | ------------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------|
   | [Building Type](https://github.com/upyog/upyog-mdms-data/blob/master/data/pg/firenoc/BuildingType.json)                             | This master contains the details about which unit of measurement is use for a particular building type.        | 
   | [FireNocStateConstats](https://github.com/upyog/upyog-mdms-data/blob/master/data/pg/firenoc/FireNocStateConstats.json)              | This master contains state level constants and their values.                                                   |
   | [UOMs](https://github.com/upyog/upyog-mdms-data/blob/master/data/pg/firenoc/UOMs.json)                                             | This master contains list of Unit of measurements for firenoc                                                  |
   | [FireNocULBConstats](https://github.com/upyog/upyog-mdms-data/blob/master/data/pg/citya/FireNoc/FireNocULBConstats.json)        | This master contains the list of state level constants and their values..                                      |


### API DetailsThis master has state level constants and their values.

`BasePath` /firenoc-calculator/[API endpoint]

##### Method
a) `/v1/_calculate`

Calculates Fire NOC fee and generates the respective demands in the system.
    
b) `/v1/_getbill`

This API updates demand with time based penalty if applicable and Generates bill for the given criteria.

c) `/billingslab/_create`

Create new billing slabs for Fire NOC calculation.

d) `/billingslab/_update`

Updates an existing billing slab with new properties

e) `/billingslab/_search`

Get the list of bill slabs defined in the system for Fire NOC calculation.


**Postman collection** :-  [Postman Collection](https://api.postman.com/collections/23419225-f663f199-a7b6-485e-9728-afb5794c302b?access_key=PMAT-01GW47RK3F2MVAC72JKBJPK2ZZ)





### Kafka Consumers

- NA

### Kafka Producers

- Following are the Producer topic.
    - **save-firenoc-calculator-billingslab** :- This topic is use to save billing slabs for firenoc calculation in system.
    - **update-firenoc-calculator-billingslab** :- This topic is use to update the existing billing slabs in system.
