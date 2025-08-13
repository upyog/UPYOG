
# digit-ui-module-ptr

## Install

```bash
npm install --save @upyog/digit-ui-module-ptr
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
"@upyog/digit-ui-module-ptr":"^1.5.0",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```


```jsx
/** add this import **/

import { initPTRComponents } from "@upyog/digit-ui-module-ptr";

/** inside enabledModules add this new module key **/

const enabledModules = ["PTR"];

/** inside init Function call this function **/

const initDigitUI = () => {
  initPTRComponents();
};
```




### Changelog

```bash
1.7.1 UPYOG Base version
```


## Documentation

Documentation Site (https://core.digit.org/guides/developer-guide/ui-developer-guide/digit-ui)



### Published from DIGIT Frontend 
DIGIT Frontend Repo (https://github.com/upyog/UPYOG/tree/develop)


![Logo](https://s3.ap-south-1.amazonaws.com/works-dev-asset/mseva-white-logo.png)