# micro-ui-internals

This folder contains all the internal packages that make up the SV-UI frontend — modules, libraries, shared components, and styles.

UPYOG (Urban Platform for deliverY of Online Governance) is India's largest platform for governance services.
Visit the [UPYOG documentation portal](https://upyog-docs.gitbook.io/upyog-v-1.0/) for more details.

---

## Folder Structure

```
micro-ui-internals/
├── packages/
│   ├── modules/
│   │   ├── core/          # App shell — routing, auth, layout
│   │   ├── common/        # Shared flows — payment, common components
│   │   ├── sv/            # Street Vending module
│   │   └── engagement/    # Events, notifications, surveys
│   ├── libraries/         # Shared hooks, services, and API utilities
│   ├── react-components/  # Shared UI component library (LTS)
│   └── css/               # Global styles — built with Vite + PostCSS
├── example/               # Local dev app for testing packages in isolation
└── workspace-aliases.js   # Maps package names to local source for HMR
```

---

## Getting Started

### 1. Install dependencies

```bash
# From the web/ directory
yarn install

# Then from micro-ui-internals/
cd micro-ui-internals
yarn install
```

### 2. Start the local dev server

```bash
cd micro-ui-internals
yarn start
```

This starts the example app with live workspace aliases — changes to any package source are reflected immediately without rebuilding.

### 3. Build all packages

```bash
cd micro-ui-internals
yarn build
```

Each package builds independently using Vite in library mode and outputs to its own `dist/` folder.

---

## Key Dependencies

| Package | Purpose |
|---|---|
| react / react-dom | UI framework |
| react-router-dom | Client-side routing |
| react-redux / redux | State management |
| @tanstack/react-query | Server state and data fetching |
| react-hook-form | Form handling |
| react-i18next / i18next | Translations and localization |
| react-table | Table rendering |
| react-time-picker | Time input component |
| recharts | Charts and data visualization |

---

## License

UPYOG Source Code is open source under the [UPYOG Code, Copyright and Contribution License](https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf).
