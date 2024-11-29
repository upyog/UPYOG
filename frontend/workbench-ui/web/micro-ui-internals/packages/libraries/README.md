<!-- TODO: update this -->

# digit-ui-svg-components

## Install

```bash
npm install --save @egovernments/digit-ui-libraries
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
"@egovernments/digit-ui-libraries":"1.8.0",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```


## Usage

```jsx
import React from "react";
import initLibraries from "@egovernments/digit-ui-libraries";

import defaultConfig from "./config";

const App = ({ deltaConfig, stateCode, cityCode, moduleCode }) => {
  initLibraries();

  const store = eGov.Services.useStore(defaultConfig, { deltaConfig, stateCode, cityCode, moduleCode });

  return <p>Create React Library Example 😄</p>;
};

export default App;
```

### Changelog

```bash
1.8.1 Added logoutRedirectURL for mgramseva workbench
1.8.0 Released as part of workbench v1.0
```

### Published from DIGIT Frontend 
DIGIT Frontend Repo (https://github.com/egovernments/Digit-Frontend/tree/master)

### Contributors

[jagankumar-egov] [nipunarora-eGov] [Tulika-eGov] [Ramkrishna-egov] [nabeelmd-eGov] [anil-egov] [vamshikrishnakole-wtt-egov] 

## License

MIT © [jagankumar-egov](https://github.com/jagankumar-egov)


![Logo](https://s3.ap-south-1.amazonaws.com/works-dev-asset/mseva-white-logo.png)
