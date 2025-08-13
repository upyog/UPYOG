
# Upyog-ui-module-chb

## Install

```bash
npm install --save @nudmcdgnpm/digit-ui-module-chb
```

## Limitation

```bash
This Package is more specific to DIGIT-UI's can be used across mission's
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/micro-ui/web/package.json
```

```json
"@nudmcdgnpm/digit-ui-module-chb":"^1.2.7",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```


```jsx
/** add this import **/

import { initCHBComponents } from "@upyog/digit-ui-module-chb";

/** inside enabledModules add this new module key **/

const enabledModules = ["CHB"];

/** inside init Function call this function **/

const initDigitUI = () => {
  initCHBComponents();
};
```
