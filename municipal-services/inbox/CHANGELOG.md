# Changelog
All notable changes to this module will be documented in this file.

---

# 1.3.0 - 2026-05-20

## Added

* Added handler-based inbox architecture
* Added `ModuleInboxHandler`
* Added `ModuleHandlerRegistry`
* Added `InboxAssembler`
* Added `InboxContext`
* Added module-specific handler implementations
* Added module-specific workflow customization
* Added module-specific business object mapping
* Added configurable workflow total count handling
* Added configurable nearing SLA count handling
* Added ElasticSearch integration handling

---

## Refactored

### Previous Architecture

Previously all module-specific logic was tightly coupled inside:

```
InboxService
```

The service contained:

* Module-specific conditions
* Workflow customizations
* SLA customizations
* Status mapping logic
* Application id fetching logic
* Business object mapping logic

Example:

```
if(module.equals("PT")) {
   ...
}
else if(module.equals("FSM")) {
   ...
}
```

Problems:

* Large monolithic service
* Difficult maintenance
* Difficult onboarding of new modules
* High coupling
* Repeated conditional logic
* Difficult testing

---

### New Architecture

Module-specific logic has been moved into dedicated handlers.

New flow:

```
InboxService
    ->
ModuleHandlerRegistry
    ->
ModuleInboxHandler
    ->
InboxAssembler


* `InboxService` handles the overall inbox processing flow and workflow integration.
* `ModuleHandlerRegistry` stores and finds the appropriate module handler dynamically based on module name.
* `ModuleInboxHandler` handles module-specific logic like application fetching, counts, SLA handling, and business mapping.
* `InboxAssembler` combines workflow and business object data to prepare the final inbox response.

```

Benefits:

* Modular architecture
* Easy module onboarding
* Cleaner code separation
* Better maintainability
* Better extensibility
* Reduced conditional logic
* Improved readability
* Improved testability

---

## Added Module Handlers

* `ASSETModuleHandler` handles inbox processing for Asset module applications.
* `BPAModuleHandler` handles inbox processing for BPA module applications.
* `ChallanModuleHandler` handles inbox processing for Challan module applications.
* `CHBModuleHandler` handles inbox processing for Community Hall Booking applications.
* `CNDModuleHandler` handles inbox processing for Construction and Demolition applications.
* `EWasteModuleHandler` handles inbox processing for E-Waste applications.
* `FSMModuleHandler` handles inbox processing for FSM applications.
* `MTModuleHandler` handles inbox processing for Mobile Toilet service applications.
* `NDCModuleHandler` handles inbox processing for NDC applications.
* `NOCModuleHandler` handles inbox processing for NOC applications.
* `PGRAiModuleHandler` handles inbox processing for PGRAI grievance applications.
* `PTModuleHandler` handles inbox processing for Property Tax applications.
* `PTRModuleHandler` handles inbox processing for Pet Registration applications.
* `SVModuleHandler` handles inbox processing for Street Vending applications.
* `TLModuleHandler` handles inbox processing for Trade License applications.
* `TPModuleHandler` handles inbox processing for Tree Pruning service applications.
* `WSModuleHandler` handles inbox processing for Water and Sewerage applications using ElasticSearch flow.
* `WTModuleHandler` handles inbox processing for Water Tanker service applications.
---

## Moved Logic From InboxService To Handlers

Moved:

* Application id fetching
* Module-specific counts
* Status enrichment
* Business object mapping
* Workflow customization
* SLA handling flags
* Search parameter cleanup
* ElasticSearch integration handling

---

## 1.2.2 - 2023-01-31

- Changed 1.2.2-beta version to 1.2.2

## 1.2.2-beta - 2022-09-10

- Added elastic search call for privacy audit report.

## 1.2.1 - 2022-08-29

- Added serviceSLA for W&S inbox objects

## 1.2.0 - 2022-08-18

- Support Inbox for  WnS service

## 1.1.0 - 2022-01-13

- Updated to log4j2 version 2.17.1

## 1.1.0

- Support new Inbox for for OBPS service, stakeholder registration service, and NOC service

## 1.0.0

- base version