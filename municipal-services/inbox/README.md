# Inbox Service (inbox)

The inbox service is a aggregation service which aggregates data of municipal services and workflow based on given complex search criteria
and returns applications and workflow data in paginated manner. The service also returns the total count matching the search criteria.



### Service Dependencies


- Workflow Service (workflow-v2)
- User Service (user-service)
- Searcher Service (egov-searcher)
- The Municipal service for which inbox configuration is added


### Swagger API Contract

Link to the swagger API contract [YAML](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/municipal-services/docs/inbox.yml#!/) and editor link like below


### Postman Collection



## Service Details

The service aggregates data from workflow and configured municipal services, to provide data required for displaying on inbox screen


### API Details


`_search` : This API is used to search inbox application data based on the criteria provided

---

# Architecture

The Inbox service follows a handler-based modular architecture.

---

# Architecture Flow

```
Client Request
      |
      v
InboxController
      |
      v
InboxService
      |
      +------------------------------------------------------------------+
      |                                                                  |
      v                                                                  v
WorkflowService                                              ModuleHandlerRegistry
      |                                                                  |
      |                                                                  v
      |                                                       ModuleInboxHandler
      |                                                                  |
      |     +------------+------------+------------+------------+------------+------------+
      |     |            |            |            |            |            |            |
      |     v            v            v            v            v            v            v
      | ASSETModule  BPAModule   ChallanModule  CHBModule   CNDModule   EWasteModule
      |   Handler      Handler      Handler       Handler      Handler      Handler
      |     |            |            |            |            |            |
      |     |            |            |            |            |            |
      |     v            v            v            v            v            v
      | PostgreSQL  PostgreSQL  PostgreSQL   PostgreSQL  PostgreSQL  PostgreSQL
      |  /Searcher   /Searcher   /Searcher    /Searcher   /Searcher   /Searcher
      |
      |     +------------+------------+------------+------------+------------+
      |     |            |            |            |            |
      |     v            v            v            v            v
      | FSMModule    MTModule     NDCModule    NOCModule    PGRAiModule
      |  Handler      Handler      Handler      Handler       Handler
      |     |            |            |            |            |
      |     |            |            |            |            |
      |     v            v            v            v            v
      | PostgreSQL  PostgreSQL  PostgreSQL   PostgreSQL  PostgreSQL
      |  /Searcher   /Searcher   /Searcher    /Searcher   /Searcher
      |
      |     +------------+------------+------------+------------+------------+
      |     |            |            |            |            |
      |     v            v            v            v            v
      | PTModule     PTRModule    SVModule      TLModule      TPModule
      |  Handler      Handler      Handler       Handler       Handler
      |     |            |            |            |            |
      |     |            |            |            |            |
      |     v            v            v            v            v
      | PostgreSQL  PostgreSQL  PostgreSQL   PostgreSQL  PostgreSQL
      |  /Searcher   /Searcher   /Searcher    /Searcher   /Searcher
      |
      |                      +-------------------+-------------------+
      |                      |                                       |
      |                      v                                       v
      |                 WSModuleHandler                        WTModuleHandler
      |                      |                                       |
      |                      |                                       |
      |                      v                                       v
      |               ElasticSearch                         PostgreSQL/Searcher
      |
      +------------------------------------------------------------------+
      |
      v
InboxAssembler
      |
      +----------------------------+-----------------------------+
      |                            |                             |
      v                            v                             v
Workflow Data              Business Object Data          SLA Enrichment
      |
      v
Final Inbox Response
      |
      v
Client Response
```

---

# Core Components

## InboxService

Location:

```
org.egov.inbox.service.InboxService
```

InboxService is the main orchestration layer of the inbox module. It handles workflow integration, finds the correct module handler, prepares inbox context, fetches workflow counts, and coordinates the complete inbox response generation flow.

---

## ModuleInboxHandler

Location:

```
org.egov.inbox.service.handler.ModuleInboxHandler
```

ModuleInboxHandler defines the contract for all module-specific inbox implementations. Each module handler is responsible for fetching application ids, handling module-specific counts, removing unwanted search parameters, and customizing inbox behavior for that module.

---

## ModuleHandlerRegistry

Location:

```
org.egov.inbox.service.handler.ModuleHandlerRegistry
```

ModuleHandlerRegistry maintains all registered module handler beans and finds the appropriate handler dynamically based on the module name. It acts as the central registry for module-specific inbox processing.

---

## InboxAssembler

Location:

```
org.egov.inbox.service.handler.InboxAssembler
```

InboxAssembler combines workflow data and business object data to prepare the final inbox response. It also handles ElasticSearch-based flow, SLA enrichment, workflow mapping, and inbox object assembly.

---

## InboxContext

Location:

```
org.egov.inbox.service.handler.InboxContext
```

InboxContext is a shared processing object used across the inbox flow. It stores request information, search criteria, workflow mappings, business keys, status mappings, and intermediate processing data required during inbox execution.

---

# Current Module Handlers

| Module  | Handler              | Data Source         |
| ------- | -------------------- | ------------------- |
| ASSET   | ASSETModuleHandler   | PostgreSQL/Searcher |
| BPA     | BPAModuleHandler     | PostgreSQL/Searcher |
| Challan | ChallanModuleHandler | PostgreSQL/Searcher |
| CHB     | CHBModuleHandler     | PostgreSQL/Searcher |
| CND     | CNDModuleHandler     | PostgreSQL/Searcher |
| EWaste  | EWasteModuleHandler  | PostgreSQL/Searcher |
| FSM     | FSMModuleHandler     | PostgreSQL/Searcher |
| MT      | MTModuleHandler      | PostgreSQL/Searcher |
| NDC     | NDCModuleHandler     | PostgreSQL/Searcher |
| NOC     | NOCModuleHandler     | PostgreSQL/Searcher |
| PGRAI   | PGRAiModuleHandler   | PostgreSQL/Searcher |
| PT      | PTModuleHandler      | PostgreSQL/Searcher |
| PTR     | PTRModuleHandler     | PostgreSQL/Searcher |
| SV      | SVModuleHandler      | PostgreSQL/Searcher |
| TL      | TLModuleHandler      | PostgreSQL/Searcher |
| TP      | TPModuleHandler      | PostgreSQL/Searcher |
| WT      | WTModuleHandler      | PostgreSQL/Searcher |
| WS      | WSModuleHandler      | ElasticSearch       |
| SW      | WSModuleHandler      | ElasticSearch       |

---

# Application Properties Configuration

```
service.search.mapping={
"booking-refund":{
    "searchPath":"http://localhost:8085/chb-services/booking/v1/_search",
    "dataRoot":"hallsBookingApplication",
    "applNosParam":"bookingNo",
    "businessIdProperty":"bookingNo",
    "applsStatusParam":"status"
}
}
```

---

# How To Add New Module

## Step 1

Create constants file.

Example:

```
org.egov.inbox.util.NewModuleConstants
```

---

## Step 2

Create Inbox Filter Service.

Example:

```
NewModuleInboxFilterService
```

Methods:

```
fetchApplicationNumbersFromSearcher()

fetchApplicationCountFromSearcher()
```

---

## Step 3

Create Module Handler.

Location:

```
org.egov.inbox.service.handler.impl
```

Example:

```
@Service
public class NewModuleHandler implements ModuleInboxHandler
```

Implement:

```
supports()

fetchApplicationIds()

fetchCount()

paramsToRemove()

buildBusinessMap()
```

---

## Step 4

Add service mapping configuration.

```
service.search.mapping={
"NEW_MODULE":{
    "searchPath":"http://localhost:8080/new-module/v1/_search",
    "dataRoot":"applications",
    "applNosParam":"applicationNo",
    "businessIdProperty":"applicationNo",
    "applsStatusParam":"status"
}
}
```

---

## Step 5

Add searcher query in:

```
egov-searcher
```

---

# How To Connect Through ElasticSearch

If after adding a new module you want inbox data to be fetched from ElasticSearch instead of Searcher, follow the below steps.

---

## Step 1

Update:

```
isBillingModule()
```

Example:

```
return BS_WS_MODULENAME.equalsIgnoreCase(moduleName)
        || BS_SW_MODULENAME.equalsIgnoreCase(moduleName)
        || NEW_MODULE.equalsIgnoreCase(moduleName);
```

---

## Step 2

Update:

```
InboxAssembler
```

---

## Step 3

Add ElasticSearch query logic in:

```
ElasticSearchRepository
```

---

## Step 4

Configure:

* ElasticSearch Index
* Index Mapping
* Data Sync

---

## Step 5

Update:

```
getApplicationServiceSla()
```

---

## Step 6

Add module-specific object mapping if required.

Example:

```
buildBusinessMap()
```

---

# Workflow Count Handling

```
@Override
public boolean isWorkflowTotalCountRequired() {
    return false;
}
```

---

# Nearing SLA Count Handling

```
@Override
public boolean isWorkflowNearingSlaCountRequired() {
    return false;
}
```

---

# Reference Document

TBD
