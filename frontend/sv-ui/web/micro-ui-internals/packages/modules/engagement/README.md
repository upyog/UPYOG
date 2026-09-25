# sv-ui-module-engagement

This module handles citizen engagement features in the SV-UI project — including surveys, events, notifications, and charts.

## Install

```bash
npm install --save @upyog/digit-ui-module-engagement
```

## Add as a dependency

In your `frontend/sv-ui/web/package.json`, add:

```json
"@upyog/digit-ui-module-engagement": "^1.8.0"
```

## Usage

Navigate to your `App.js`:

```bash
frontend/sv-ui/web/src/App.js
```

```jsx
import { EngagementModule, initEngagementComponents } from "@upyog/digit-ui-module-engagement";

// Call this during app initialization to register all engagement components
initEngagementComponents();

// Add "Engagement" to your enabled modules list
const enabledModules = ["Engagement"];
```

## Key Dependencies

| Package | Version |
|---|---|
| react | 19.0.0 |
| react-dom | 19.0.0 |
| react-router-dom | 6.28.0 |
| react-i18next | ^14.0.0 |
| @tanstack/react-query | ^5.0.0 |
| react-hook-form | ^7.51.0 |
| recharts | ^2.10.0 |
| react-time-picker | 4.2.1 |

## Changelog

See [CHANGELOG.md](./CHANGELOG.md)

## Published from UPYOG Frontend

[UPYOG Frontend Repo](https://github.com/upyog/UPYOG/tree/develop)
