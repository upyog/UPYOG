# @upyog/digit-ui-module-noc

This module provides the **Fire No Objection Certificate (Fire NOC)** frontend interface for the UPYOG platform. It handles the complete lifecycle of Fire NOC applications, offering a structured, multi-step application wizard for citizens and a robust workflow inbox and review system for municipal employees.

## Features

* **Citizen Fire NOC Application Wizard:** A guided multi-step process for filing provisional or new certificate applications.
* **Employee Workflow Management:** Dedicated officer dashboard for tracking, verification, and approval/rejection.
* **Property Search Integration:** Pre-fills location details by calling the Property Tax (PT) service via Property ID.
* **GIS & Location Support:** Integrated map picker for coordinates (Latitude/Longitude) and Mohalla boundary matching.
* **Document Upload & Verification:** Direct integration with FileStore for uploading identity, address, and building plans.
* **Review & Fee Estimation:** Automatic bill calculation displaying tax head estimates and totals prior to submission.
* **Payment Integration:** Redirection to payment gateways for online fee collection.
* **Workflow-based Application Lifecycle:** Powered by state-machine workflow configurations on the backend.
* **MDMS-driven Dropdowns:** Dropdowns (NBC occupancy sub-types, ownership categories, relationship options) are fully populated by Master Data Management Services (MDMS).
* **Search & Inbox:** Comprehensive filtering for applications based on status, applicant mobile, or certificate ID.

## Architecture

The Fire NOC module is developed as an independent **Micro Frontend (Micro UI)** module, migrated from the legacy monolithic repository. It operates autonomously in terms of state, routing, and view layers while preserving existing municipal business logic and maintaining complete backward compatibility with backend APIs.

It is registered dynamically with the host shell via the global **Component Registry** (`Digit.ComponentRegistryService`). This allows page routes and wizard screens to be loaded on-demand.

The module is built entirely using standard React patterns. It utilizes a **config-driven wizard** structure to map steps, fetches and caches API data using **React Query Hooks**, and renders layouts with unified **Shared Digit Components** (such as `FormStep`, `StatusTable`, `Row`, and `Card`).

## Folder Structure

* `src/Module.js` :-  Core module entrypoint registering Citizen and Employee components.
* `src/config/config.js` : Form wizard step routing, step order, and field configuration.
* `src/pageComponents/` : Individual step forms (NOC Type, property specifications, applicant details, document uploads).
* `src/pages/citizen/` : Citizen-facing landing pages, wizard routes, and summary review screens.
* `src/pages/employee/` : Employee landing pages, workflow inboxes, and officer check sheets.
* `src/utils/` : Data serializers transforming form state to backend payloads.

## Code Structure & Implementation Details

The codebase is organized modularly to split the presentation, configuration, routing, and data serialization layers:

### 1. Component Registry & Initialization (`src/Module.js`)
* **Purpose:** Serves as the micro-frontend entrypoint.
* **Mechanism:** Exposes the `initNOCComponents()` function. This maps internal react components to a standardized registry (`componentsToRegister`), enabling the host application shell to dynamically load the module components without compile-time coupling.

### 2. Config-Driven Step Controller (`src/config/config.js`)
* **Purpose:** Defines the citizen wizard steps and component mappings.
* **Mechanism:** Declares step paths, labels, condition-based skip options, and step component bindings. Steps (e.g. `noc-type`, `property-details`, `owner-details`, `document-details`) are loaded iteratively.

### 3. Wizard State & Lifecycle (`src/pages/citizen/Create/index.js`)
* **Purpose:** Controls wizard step routing and data synchronizations.
* **Mechanism:** Implements state management for intermediate wizard steps. Syncs ongoing application states to the browser's session storage (`Digit.SessionStorage.set`), ensuring the user's progress is saved if they refresh the tab.

### 4. Data Serialization & Parity (`src/utils/index.js`)
* **Purpose:** Acts as the data translation layer.
* **Mechanism:** Translates the client-side form configurations to standard Fire NOC JSON payloads required by backend REST APIs. Also translates backend search query results into the structured form model when editing or pre-filling fields.

---

## Citizen Workflow

```
NOC Type 
  → Property Details 
  → Applicant Details 
  → Document Upload 
  → Review & Fee Estimate 
  → Acknowledgement
```
## Integrations

* **MDMS:** Retreiving NBC code definitions, document checklists, and local tenant settings.
* **Property:** Prefilling address data by searching Property IDs.
* **Workflow:** State transitions and approval chain routing.
* **FileStore:** Uploading and retrieving application documents.
* **Payment:** Generating bill receipts and tracking payment success.
* **User:** Managing citizen profiles and mobile auth details.
* **Location:** Fetching municipal boundaries, cities, and mohallas.

## Enhancements Introduced During Micro UI Migration

1. **Summary Declaration Checkbox:** Added a mandatory checkbox on the `CheckPage` review screen. Users must agree to the declarations regarding document authenticity before the Submit button becomes clickable.
2. **Acknowledgement Route Protection:** Once submission succeeds, the stepper state is cleared. Stepper routes are protected, and any navigation actions from the `NOCAcknowledgement` page direct users to the Citizen Home page.
3. **Floating Toast Validation:** Provisional NOC and Property ID search inputs are validated for empty states. Triggering search with empty inputs displays a floating red warning toast that auto-dismisses after a 3-second timeout.
4. **Mandatory Field Step-Navigation Toast Alerts:** Attempting to proceed to the next step in the wizard without completing required fields or uploading mandatory documents triggers a floating red warning toast. These toast alerts automatically dismiss after a 3-second timeout to keep the UI clean.

---

## Installation

```bash
npm install --save @upyog/digit-ui-module-noc
```

## Limitation

```bash
This Package is more specific to DIGIT-UI's can be used across mission's
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/upyog-ui/web/package.json
```

```json
"@upyog/digit-ui-module-noc": "3.10.0"
```

then navigate to App.js

```bash
 frontend/upyog-ui/web/src/App.js
```


```jsx
/** add this import **/

import { initNOCComponents } from "@upyog/digit-ui-module-noc";

/** inside enabledModules add this new module key **/

const enabledModules = ["NOC"];

/** inside init Function call this function **/

const initDigitUI = () => {
  initNOCComponents();
};
```
