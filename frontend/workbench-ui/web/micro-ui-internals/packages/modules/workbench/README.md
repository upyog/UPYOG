# workbench-ui-module-workbench

## Install

```bash
npm install --save workbench-ui-module-workbench
```

## Limitation

```bash
This Package is more specific to UPYOG-UI's can be used across mission's
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/micro-ui/web/package.json
```

```json
"@nudmcdgnpm/workbench-ui-module-workbench":"0.1.0",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```

```jsx
/** add this import **/

import { initWorkbenchComponents } from "@nudmcdgnpm/workbench-ui-module-workbench";

/** inside enabledModules add this new module key **/

const enabledModules = ["workbench"];

/** inside init Function call this function **/

const initDigitUI = () => {
  initWorkbenchComponents();
};

```

In MDMS

_Add this configuration to enable this module [MDMS Enabling Workbench Module](https://github.com/egovernments/works-mdms-data/blob/588d241ba3a9ab30f4d4c2c387a513da811620ca/data/pg/tenant/citymodule.json#L227)_

## List of Screens available in this versions were as follows

1 . Search Master Data
    > -Provides a screen based on Schema and renders the search result if data is present
    > -It also provides a dynamic filter based on which data can be filtered


2 . Add Master Data based on selected schema
    > -Provides a screen to add new master data according to the schema
    > -Provides a Dropdown if it has any referenced master 

3 . Update Master data for selected data.
    > -View the master data from search screen
    > -Disable/Enable the master data if required
    > -Update the master data value except the unique-identifier field mentioned in the schema



4 . Localisation screens
    > -Provides a screen to search the localisation present in the environment
    > -Add new localisation 
    > -Update existing localisation
    > -Bulk Upload of Localisation data

5 . MDMS UI Schema

6 . Data push for any API based on schema

### Mandatory changes to use Workbench module

1 . Assuming core module is already updated with 1.5.38+ and related changes were taken

2 . add the following hook method in micro-ui-internals/packages/libraries/src/hooks/useCustomAPIMutationHook.js

reference:: 
https://github.com/egovernments/DIGIT-Dev/blob/6e711bdc005c226c7debd533209681fc77078a3e/frontend/micro-ui/web/micro-ui-internals/packages/libraries/src/hooks/useCustomAPIMutationHook.js

3 . add the following utility method in micro-ui-internals/packages/libraries/src/utils/index.js
```jsx
didEmployeeHasAtleastOneRole

const didEmployeeHasAtleastOneRole = (roles = []) => {
  return roles.some((role) => didEmployeeHasRole(role));
};

```

4 . stylesheet link has to be added 
```jsx
<link rel="stylesheet" href="https://unpkg.com/@egovernments/digit-ui-css@1.2.114/dist/index.css" />

### Changelog

```bash
1.0.1 Fixes related to the limits
1.0.0 Workbench v1.0 release
1.0.0-beta workbench base version beta release
0.0.3 readme updated
0.0.2 readme updated
0.0.1 base version
```

### Contributors

- [@NUDM TEam]


