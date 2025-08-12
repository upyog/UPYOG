
# cnd-ui-module-common

## Install

```bash
npm install --save @upyog/digit-ui-module-common
```

## Limitation

```bash
This Package is more specific to CND-UI's can be used across mission's
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/micro-ui/web/package.json
```

```json
"@upyog/digit-ui-module-common":"^1.5.0",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```


```jsx
/** add this import **/

import { initcommonComponents } from "@upyog/digit-ui-module-common";

/** inside enabledModules add this new module key **/

const enabledModules = ["Payment"];

/** inside init Function call this function **/

const initDigitUI = () => {
  initcommonComponents();
};
```




### Changelog

```bash
1.7.1 UPYOG Base version
```


## Maintainer

- [Shivank-NIUA](https://github.com/ShivankShuklaa)


### Published from UPYOG Frontend 
UPYOG Frontend Repo (https://github.com/upyog/UPYOG/tree/develop)

