# Vendor

Vehicle Registry is a system that enables ULB Employees to create and search Vendor i.e Desluding Operator (DSO) and driver entities with appropriate vehicle Entities  for FSM Application. This document contains the details about how to setup the Vendor and describe the functionalities provided.

### DB UML Diagram

![plot](./vendor.png)

### Service Dependencies

- MDMS Service (egov-mdms-service)
- User Service (egov-user-service)
- Boundary Service (boundary-service)
- Vechile Service (vehicle)



### Swagger API Contract

Link to the swagger API contract [YAML](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/fsm/Vendor_Registration_Contract.yaml#!/) and editor link


### Postman Collection
Link to the postman collection [here](https://api.postman.com/collections/23419225-17999027-0328-410c-8d3e-a4bcd034b082?access_key=PMAT-01GW90PMEGCF8KZDNYNKPG22ER)


## Service Details

**Vendor Registry**

- Contains the API's to create,  search Vendor i.e DSO in FSM Case
  

### API Details

`v1/_create` 		: The create api to create Vendor in the system

`v1/_search`		: The search api to fetch the Vendors in the system based on the search criteria



### Reference Document
TBD


### Kafka Consumers
NA

### Kafka Producers


- **save-vendor-application** 			: service sends data to this topic to create new Vendor.
