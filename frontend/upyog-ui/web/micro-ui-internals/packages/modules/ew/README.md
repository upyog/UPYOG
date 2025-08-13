# upyog-ui-module-ew

## E-Waste Module

The E-Waste module is a part of the UPYOG platform designed to manage and streamline e-waste collection and disposal processes. This module provides tools for efficient tracking, reporting, and management of e-waste operations.

## Features

- **E-Waste Collection Requests**: Users can raise requests for e-waste collection.
- **Tracking**: Monitor the status of e-waste collection requests in real-time.
- **Reporting**: Generate reports for e-waste collection and disposal activities.


## Installation

```bash
npm install --save @nudmcdgnpm/upyog-ui-module-ew
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/micro-ui/web/package.json
```

```json
"@nudmcdgnpm/upyog-ui-module-ew": "^1.2.6",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```
and index.js
```bash
 frontend/micro-ui/web/micro-ui-internals/example/src/index.js
```

```jsx

/** add this import **/
import { EWModule, EWLinks, EWComponents } from "@nudmcdgnpm/upyog-ui-module-ew";

/** inside enabledModules add this new module key **/
const enabledModules = ["EW"];

/** inside init Function call this function in setup registry add these **/
window.Digit.ComponentRegistryService.setupRegistry({
  EWModule,
  EWLinks,
  ...EWComponents,
});

```



## Contributors

[khalid-rashid] [sourabh]

## License

This project is licensed under the MIT License. See the [LICENSE](./LICENSE) file for details.
