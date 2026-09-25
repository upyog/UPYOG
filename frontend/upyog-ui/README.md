# UPYOG UI — LTS Upgrade

UPYOG (Urban Platform for deliverY of Online Governance) is India's largest open-source platform for urban governance services, maintained by NUDM ( National Urban Digital Mission - Internal Tech Team).

This repository contains the complete frontend source code for UPYOG UI — upgraded to the latest LTS stack.

---

## Tech Stack Upgrade Summary

| Concern | Version |
|---|---|
| Node.js | 22 (LTS) |
| Build Tool | Vite 6 |
| React | 19.2.0 |
| React DOM | 19.2.0 |
| React Router | v6.23+ |
| React Hook Form | v7.51+ |
| React Query | @tanstack/react-query v5 |
| React Redux | 9.0.0 |
| Redux Thunk | 3.1.0 |
| Redux | 4.2.1 |

---

## Repository Structure

```
upyog-ui/
└── web/
    ├── micro-ui-internals/          ← Monorepo (Yarn Workspaces)
    │   ├── example/                 ← Dev server entry point (Vite)
    │   │   ├── src/
    │   │   │   └── index.js         ← App bootstrap, module registration
    │   │   ├── vite.config.js       ← Vite config with proxy + monorepo aliases
    │   │   └── .env                 ← Environment variables
    │   ├── packages/
    │   │   ├── libraries/           ← @upyog/digit-ui-libraries
    │   │   │                           Hooks, services, API calls, utilities
    │   │   ├── react-components/    ← @nudmcdgnpm/digit-ui-react-components
    │   │   │                           Shared UI component library
    │   │   └── modules/
    │   │       ├── core/            ← @upyog/digit-ui-module-core
    │   │       ├── pgr/             ← Complaints
    │   │       ├── pt/              ← Property Tax
    │   │       ├── tl/              ← Trade License
    │   │       ├── ws/              ← Water & Sewerage
    │   │       ├── fsm/             ← Faecal Sludge Management
    │   │       ├── hrms/            ← HR Management
    │   │       ├── obps/            ← Building Plan Scrutiny
    │   │       ├── dss/             ← Dashboard & Analytics
    │   │       ├── bills/           ← Bills
    │   │       ├── receipts/        ← Receipts
    │   │       ├── engagement/      ← Events, Surveys, Documents
    │   │       ├── noc/             ← No Objection Certificate
    │   │       ├── mCollect/        ← Miscellaneous Collections
    │   │       ├── commonPt/        ← Common Property Tax
    │   │       ├── common/          ← Payments
    │   │       ├── ptr/             ← Pet Registration
    │   │       ├── asset/           ← Asset Management
    │   │       ├── assetv2/         ← Asset Management V2
    │   │       ├── ads/             ← Advertisement Management
    │   │       ├── ew/              ← E-Waste Management
    │   │       ├── sv/              ← Street Vending
    │   │       ├── wt/              ← Water Tanker / Work Tanker
    │   │       ├── vendor/          ← Vendor Management
    │   │       ├── chb/             ← Community Hall Booking
    │   │       ├── gis/             ← GIS Map View
    │   │       ├── pgrai/           ← PGR AI
    │   │       └── reports/         ← Reports
    │   └── package.json             ← Root workspace config
    └── public/
```

---

## Prerequisites

Make sure the following are installed before running the project:

- **Node.js** >= 22 (LTS) — [Download](https://nodejs.org/)
- **Yarn** >= 1.22 — `npm install -g yarn`
- **Git**

Verify:
```bash
node -v    # should be v22.x.x
yarn -v    # should be 1.22.x
```

---

## Running Locally (Development)

### Step 1 — Clone the repository

```bash
git clone <repo-url>
cd upyog-ui/web/
```

### Step 2 — Install dependencies

```bash
yarn install
```

This installs all workspace packages in one go using Yarn Workspaces.

### Step 3 — Configure environment

Create one environment file `web/.env` to point to your backend:

```env
REACT_APP_PROXY_API=https://niuatt.niua.in        # your backend API URL
REACT_APP_PROXY_ASSETS=https://niuatt.niua.in     # your assets URL
REACT_APP_USER_TYPE=CITIZEN
```

Available env variables:

| Variable | Purpose |
|---|---|
| `REACT_APP_PROXY_API` | Backend API base URL (proxied by Vite) |
| `REACT_APP_PROXY_ASSETS` | Assets base URL |
| `REACT_APP_USER_TYPE` | Default user type — `CITIZEN` or `EMPLOYEE` |
| `REACT_APP_EMPLOYEE_TOKEN` | Pre-set employee token for dev |
| `REACT_APP_CITIZEN_TOKEN` | Pre-set citizen token for dev |
| `REACT_APP_GLOBAL` | Global config JS URL |

### Step 4 — Start the dev server

```bash
yarn start
```

This runs Vite from `web/vite.config.js` on **http://localhost:3000**

All API calls are proxied — no CORS issues locally.

---

## Building for Production

```bash
yarn build
```

Output is generated in `web/build/`. The base path is `/upyog-ui/` for production deployment.


---

## How the Monorepo Works

This project uses **Yarn Workspaces**. All packages under `packages/` are linked locally — no need to publish to npm to test changes between packages.

Vite's `vite.config.js` dynamically scans all workspace packages and creates **aliases** so that:

```js
import { Loader } from "@upyog/digit-ui-react-components";
// resolves to → packages/react-components/src/index.js  (local source)
// NOT from node_modules
```

This means any change in `react-components` or `libraries` is immediately reflected in the running dev server without republishing.

---

## Key Architecture Concepts

### Module Registration

Every module exports a `Module`, `Links`, and `Components` object. These are registered in `web/src/App.js`:

```js
window?.Digit.ComponentRegistryService.setupRegistry({
  PTModule,
  PTLinks,
  ...PTComponents,
  // ...
});
```

### Routing

All routing uses **React Router v6**. Key rules after the v5 → v6 migration:

- Nested `<Routes>` must use **relative paths** (not absolute)
- Parent routes that have nested routes inside must use `/*` suffix
- `useHistory` → `useNavigate`
- `useRouteMatch` → `useMatch` or `Digit.Hooks.useModuleBasePath()`
- `<Switch>` → `<Routes>`
- `<Redirect>` → `<Navigate>`

### React Hook Form

All forms use **react-hook-form v7**. Key change from v6:

```js
// v6 (old — broken with React 19):
inputRef={register(input.name, input.validation).ref}

// v7 (correct):
inputRef={register(input.name, input.validation)}
```

### React Query

Uses **@tanstack/react-query v5**:

```js
// Old v3:
import { useQuery } from "react-query";

// New v5:
import { useQuery } from "@tanstack/react-query";
```

`cacheTime` is renamed to `gcTime` in v5.

---

## Common Issues & Fixes

### Issue: `Invalid hook call`
Caused by multiple React instances. Fixed by `dedupe: ["react", "react-dom"]` in `vite.config.js`.

### Issue: Blank screen after route change
Caused by absolute paths in nested `<Routes>`. All nested route paths must be **relative** in React Router v6.

### Issue: `Map container is already initialized`
Caused by React 19 DOM recycling reusing Leaflet's container. Fixed by adding `key` prop to `MapContainer` or calling `map.remove()` on unmount.

### Issue: `register(...).ref is undefined`
Caused by react-hook-form v6 `.ref` pattern broken in React 19. Fixed by upgrading to v7 and using `register()` directly as ref.

### Issue: `Object.keys(linkData)` crash
Caused by `linkData` being `undefined` during route transitions. Fixed by using `Object.keys(linkData || {})`.

---

## Environment-Specific Proxy Targets

| Environment | URL |
|---|---|
| Local Dev (default) | `https://niuatt.niua.in` |

Change `REACT_APP_PROXY_API` in `web/.env` to switch environments.

---

## Local Docker Run

If you want to run your docker locally to see of your jenkins build will get success or not then first 
open your Docker Desktop App and then run below command in **upyog-ui** directory.

docker build \
  --progress=plain \
  --no-cache \
  -f web/docker/Dockerfile \
  --build-arg WORK_DIR=. \
  -t upyog-ui-test .


It will start building your Code with the logs same as jenkins logs

---

## License

UPYOG Source Code is open source under [UPYOG CODE, COPYRIGHT AND CONTRIBUTION LICENSE TERMS](https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf)

© 2024 National Institute of Urban Affairs (NIUA)
