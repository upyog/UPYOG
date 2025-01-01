
# digit-ui-module-pgr

## Install

```bash
npm install --save @egovernments/digit-ui-module-pgr
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
"@egovernments/digit-ui-module-pgr":"1.7.6",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```


```jsx
/** add this import **/

import { initPGRComponents } from "@egovernments/digit-ui-module-pgr";

/** inside enabledModules add this new module key **/

const enabledModules = ["PGR"];

/** inside init Function call this function **/

const initDigitUI = () => {
  initPGRComponents();
};
```

### Changelog

```bash
1.8.0 base urban version released
1.7.8 added some null checks to improve stability 
1.7.7 used new color constants
1.7.6 fixed routing issue
1.7.5 base urban version
```

### Contributors

[jagankumar-egov] [Tulika-eGov]

## Documentation

Documentation Site (https://core.digit.org/guides/developer-guide/ui-developer-guide/digit-ui)

## Maintainer

- [jagankumar-egov](https://www.github.com/jagankumar-egov)


### Published from DIGIT Frontend 
DIGIT Frontend Repo (https://github.com/egovernments/Digit-Frontend/tree/master)

![Logo](https://s3.ap-south-1.amazonaws.com/works-dev-asset/mseva-white-logo.png)
