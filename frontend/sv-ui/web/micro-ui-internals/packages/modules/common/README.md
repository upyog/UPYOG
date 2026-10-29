# sv-ui-module-common

This module contains shared components, hooks, and utilities used across all other modules in the SV-UI project.

## Install

```bash
npm install --save @upyog/digit-ui-module-common
```

## Add as a dependency

In your `frontend/sv-ui/web/package.json`, add:

```json
"@upyog/digit-ui-module-common": "^1.8.12"
```

## Usage

Navigate to your `App.js`:

```bash
frontend/sv-ui/web/src/App.js
```

```jsx
import { initcommonComponents } from "@upyog/digit-ui-module-common";

// Call this during app initialization to register all common components
const initSVUI = () => {
  initcommonComponents();
};
```

## Key Dependencies

| Package | Version |
|---|---|
| react-router-dom | 6.28.0 |
| react-redux | 7.2.8 |
| redux | 4.1.2 |
| react-i18next | ^14.0.0 |
| @tanstack/react-query | ^5.0.0 |
| react-hook-form | ^7.51.0 |

## Changelog

See [CHANGELOG.md](./CHANGELOG.md)

## Published from UPYOG Frontend

[UPYOG Frontend Repo](https://github.com/upyog/UPYOG/tree/develop)
