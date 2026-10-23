# sv-ui-module-core

This module is the foundation of the SV-UI application. It handles app startup, routing setup, global providers, and renders the root React component.

## Install

```bash
npm install --save @upyog/digit-ui-module-core
```

## Add as a dependency

In your `frontend/sv-ui/web/package.json`, add:

```json
"@upyog/digit-ui-module-core": "^1.8.2"
```

## Usage

Navigate to your `App.js`:

```bash
frontend/sv-ui/web/src/App.js
```

```jsx
import { StreetVendingUI } from "@upyog/digit-ui-module-core";

// Render the root app component with required props
ReactDOM.render(
  <StreetVendingUI
    stateCode={stateCode}
    enabledModules={enabledModules}
    moduleReducers={moduleReducers}
  />,
  document.getElementById("root")
);
```

## Key Dependencies

| Package | Version |
|---|---|
| react-router-dom | 6.28.0 |
| react-redux | 7.2.8 |
| redux | 4.1.2 |
| react-i18next | ^14.0.0 |
| @tanstack/react-query | ^5.0.0 |
| react-google-recaptcha | ^3.1.0 |
| react-tooltip | ^5.26.0 |

## Changelog

See [CHANGELOG.md](./CHANGELOG.md)

## Published from UPYOG Frontend

[UPYOG Frontend Repo](https://github.com/upyog/UPYOG/tree/develop)
