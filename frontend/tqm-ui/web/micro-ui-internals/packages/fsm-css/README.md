<!-- TODO: update this -->

# digit-ui-fsm-css

## Install

```bash
npm install --save @egovernments/digit-ui-fsm-css
```

## Limitation

```bash
This Package is more specific to DIGIT-UI's can be used across mission's
It is the base css for Sanitation UI
Parent CSS would be digit-ui-css(https://www.npmjs.com/package/@egovernments/digit-ui-css)
```

## Usage

After adding the dependency make sure you have this dependency in

```bash
frontend/micro-ui/web/package.json
```

```json
"@egovernments/digit-ui-fsm-css":"^0.0.11",
```

then navigate to App.js

```bash
frontend/micro-ui/web/public/index.html
```

```jsx
/** add this import **/

  <link rel="stylesheet" href="https://unpkg.com/@egovernments/digit-ui-fsm-css@0.0.11/dist/index.css" />

```

# Changelog

```bash
0.0.36 dropdown style fixes
0.0.35 sla css fixes
0.0.34 pop up css fixes
0.0.33 ui ux fixes
0.0.32 added styles for tqm home
0.0.31 added styles for tqm view test screen
0.0.30 added styles for tqm test wf actions and pop up screen
0.0.29 added styles for tqm test and response screen
0.0.28 Added Home screen card styles for plant operator home screen
0.0.27 Added TQM Home Card and Notification component.  
0.0.26 Added TQM and Summary and enhancements.  
0.0.25 Added support fixes for inbox v2 component.  
0.0.24 Added min-height for mobile reponive  
0.0.23 Dropdown width fixes  
0.0.22 employee dropdown height reduces  
0.0.21 Multiple image ui fixes  
0.0.20 Pop up and dropdown position issue fix    
0.0.19 UI/UX fixes    
0.0.18 No Changes    
0.0.17 Fixed dropdown list view in registry screen    
0.0.16 Removed extra margin of dropdown from filter and added margin for tag  
0.0.15 Removed selected locality and no result found card extra margin  
0.0.14 Fixed word break 
0.0.13 Latest version with no changes
0.0.12 No changes
0.0.11 Fixed navbar header fsm citizen
0.0.10 Updated radio button,checkbox,label,card,header,erorr field for all fsm citizen
0.0.9 Rating issue fixed for fsm
0.0.8 homescreen card issue fixed for fsm
0.0.7 added the readme file
0.0.6 base version
```

## Published from DIGIT Sanitation

Digit Dev Repo (<https://github.com/egovernments/SANITATION>)

## License

MIT © [jagankumar-egov](https://github.com/jagankumar-egov)
