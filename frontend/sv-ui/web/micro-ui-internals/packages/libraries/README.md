# sv-ui-libraries

This package contains shared hooks, API services, Redux store utilities, and data-fetching helpers used across all SV-UI modules.

## Install

```bash
npm install --save @nudmcdgnpm/digit-ui-libraries
```

## Add as a dependency

In your `frontend/sv-ui/web/package.json`, add:

```json
"@nudmcdgnpm/digit-ui-libraries": "^1.2.1"
```

## Usage

```jsx
import { Digit } from "@nudmcdgnpm/digit-ui-libraries";

// Access shared services
const userInfo = Digit.UserService.getUser();
const { data } = Digit.Hooks.useCommonMDMS(tenantId, "common-masters", ["StateInfo"]);
```

## Custom Hooks

### useCustomNavigate

A custom navigation hook that wraps `react-router-dom`'s `useNavigate`.
Use this everywhere in the project instead of importing `useNavigate` directly from `react-router-dom`.
This centralizes navigation logic so future upgrades only need a single change.

```jsx
const navigate = Digit.Hooks.useCustomNavigate();

// Navigate to a path
navigate("/path");

// Navigate with state
navigate("/path", { state: { data } });

// Go back
navigate(-1);
```

> If navigation fails for any reason, it automatically falls back to `window.location.href` so the user is never stuck.

## Key Dependencies

| Package | Version | Purpose |
|---|---|---|
| axios | ^1.6.0 | HTTP requests |
| @tanstack/react-query | ^5.0.0 | Server state and data fetching |
| react-redux / redux | 7.2.8 / 4.1.2 | State management |
| react-router-dom | 6.28.0 | Routing utilities |
| react-i18next / i18next | ^14.0.0 / ^23.7.0 | Translations |
| date-fns | ^3.6.0 | Date formatting utilities |
| jspdf | 2.5.1 | PDF generation |
| pdfmake | 0.1.72 | PDF document builder |
| xlsx | 0.17.5 | Excel file handling |
| html2canvas | 1.4.1 | Screenshot/canvas utility |
| dom-to-image | 2.6.0 | DOM to image conversion |

## Changelog

See [CHANGELOG.md](./CHANGELOG.md)

## License

MIT

## Published from UPYOG Frontend

[UPYOG Frontend Repo](https://github.com/upyog/UPYOG/tree/develop)

![Logo](https://in-egov-assets.s3.ap-south-1.amazonaws.com/images/Upyog-logo.png)
