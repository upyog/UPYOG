# sv-ui-module-sv (Street Vending)

This module contains all Street Vending features — vendor applications, approvals, certificate generation, and vendor management.

## Install

```bash
npm install --save @nudmcdgnpm/upyog-ui-module-sv
```

## Add as a dependency

In your `frontend/sv-ui/web/package.json`, add:

```json
"@nudmcdgnpm/upyog-ui-module-sv": "^1.3.0"
```

## Usage

Navigate to your `App.js`:

```bash
frontend/sv-ui/web/src/App.js
```

```jsx
import { SVModule, initSVComponents } from "@nudmcdgnpm/upyog-ui-module-sv";

// Call this during app initialization to register all SV components
initSVComponents();

// Add "SV" to your enabled modules list
const enabledModules = ["SV"];
```

## Key Dependencies

| Package | Version |
|---|---|
| react-router-dom | 6.28.0 |
| react-i18next | ^14.0.0 |
| @tanstack/react-query | ^5.0.0 |
| react-hook-form | ^7.51.0 |
| jspdf | ^2.5.1 |
| jspdf-autotable | ^3.8.2 |
| qrcode | ^1.5.3 |
| xlsx | ^0.18.5 |

> **Note:** `react-redux` and `redux` are bundled directly into this module (not treated as external), so this module manages its own Redux store independently.

## Changelog

See [CHANGELOG.md](./CHANGELOG.md)

## Published from UPYOG Frontend

[UPYOG Frontend Repo](https://github.com/upyog/UPYOG/tree/develop)
