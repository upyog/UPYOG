
# cnd-ui-module-core

## Install

```bash
npm install --save @upyog/digit-ui-module-core
```

## Limitation

```bash
This Package is more specific to CND's can be used across mission's
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/cnd-ui/web/package.json
```

```json
"@upyog/digit-ui-module-core":"^1.5.0",
```

then navigate to App.js

```bash
 frontend/cnd-ui/web/src/App.js
```

```jsx
/** add this import **/

import { CndUI } from "@upyog/digit-ui-module-core";


/** inside render Function add  the import for the component **/

  ReactDOM.render(<CndUI stateCode={stateCode} enabledModules={enabledModules} moduleReducers={moduleReducers} />, document.getElementById("root"));

```



### Changelog

```bash
1.7.1 UPYOG Base version
```

## Maintainer

- [Shivank-NIUA](https://github.com/ShivankShuklaa)


### Published from UPYOG Frontend 
UPYOG Frontend Repo (https://github.com/upyog/UPYOG/tree/develop)
