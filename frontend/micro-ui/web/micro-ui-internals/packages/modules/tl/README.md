
# digit-ui-module-tl

## Install

```bash
npm install --save @upyog/digit-ui-module-tl
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
"@upyog/digit-ui-module-tl":"^1.5.0",
```

then navigate to App.js

```bash
 frontend/micro-ui/web/src/App.js
```


```jsx
/** add this import **/

import { inittlComponents } from "@upyog/digit-ui-module-tl";

/** inside enabledModules add this new module key **/

const enabledModules = ["tl"];

/** inside init Function call this function **/

const initDigitUI = () => {
  inittlComponents();
};
```




### Changelog

```bash
1.7.1 UPYOG Base version
```

### Contributors

[jagankumar-egov] [Tulika-eGov]  [vamshikrishnakole-wtt-egov] 

## Documentation

Documentation Site (https://core.digit.org/guides/developer-guide/ui-developer-guide/digit-ui)

## Maintainer

- [jagankumar-egov](https://www.github.com/jagankumar-egov)


### Published from DIGIT Frontend 
DIGIT Frontend Repo (https://github.com/upyog/UPYOG/tree/develop)


![Logo](https://s3.ap-south-1.amazonaws.com/works-dev-asset/mseva-white-logo.png)