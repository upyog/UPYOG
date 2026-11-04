# SV-UI — Street Vending Module (UPYOG)

UPYOG (Urban Platform for deliverY of Online Governance) is India's largest platform for governance services.
Visit the [UPYOG documentation portal](https://upyog-docs.gitbook.io/upyog-v-1.0/) for more details.

This repository contains the web implementation of the **Street Vending (SV)** UI module built on the UPYOG micro-frontend architecture.

> This project was migrated from Create React App (CRA) to **Vite** for faster builds and HMR.
> See [CHANGELOG.md](./CHANGELOG.md) for the full list of changes.

---

## Prerequisites

- Node.js `>= 22`
- Yarn

---

## Getting Started

### 1. Install dependencies

```bash
cd web
yarn install
```

The `postinstall` script runs automatically after install and patches incompatible packages in `node_modules`:
- Removes broken `module` fields from package.json files that cause Vite resolution errors
- Creates shims for renamed Babel plugins (`@babel/plugin-proposal-*` → `@babel/plugin-transform-*`)

### 2. Configure environment (optional)

Create a `.env` file inside `web/` to override the default proxy target:

```env
VITE_PROXY_API=https://your-backend-server.example.com
```

If not set, the dev server proxies API requests to `https://niuatt.niua.in` by default.

> All client-side env vars must use the `VITE_` prefix (replaces CRA's `REACT_APP_` prefix).

### 3. Start the development server

```bash
yarn start
```

App runs at `http://localhost:3000/sv-ui/`

---

## Available Scripts

| Script | Description |
|---|---|
| `yarn start` | Start Vite dev server on port 3000 |
| `yarn build` | Production build — output to `web/build/` |
| `yarn preview` | Preview the production build locally |

---

## Project Structure

```
sv-ui/
├── web/
│   ├── src/
│   │   ├── App.jsx                  # Root component — module registration and shell mount
│   │   ├── index.jsx                # Entry point — session restore and ReactDOM.createRoot
│   │   ├── ComponentRegistry.js     # Lightweight component registry utility
│   │   └── index.css                # Global styles
│   ├── micro-ui-internals/
│   │   ├── example/                 # Local dev app with workspace aliases for live editing
│   │   │   ├── src/index.jsx        # Example app entry point
│   │   │   ├── index.html           # Vite HTML entry for example app
│   │   │   └── vite.config.js       # Dev server config with proxy and workspace aliases
│   │   ├── packages/
│   │   │   ├── libraries/           # Shared hooks, services, and utilities
│   │   │   ├── react-components/    # Shared UI component library (LTS)
│   │   │   ├── css/                 # SCSS/Tailwind styles — built with Vite
│   │   │   └── modules/
│   │   │       ├── core/            # Core shell — routing, auth, layout
│   │   │       ├── common/          # Shared module — payment, common flows
│   │   │       ├── sv/              # Street Vending module
│   │   │       └── engagement/      # Engagement module — events, notifications, surveys
│   │   └── workspace-aliases.js     # Maps local package names to source for HMR
│   ├── docker/
│   │   ├── Dockerfile               # Multi-stage Docker build (Node 22 + Nginx)
│   │   └── nginx.conf               # Nginx config for serving the built app
│   ├── index.html                   # Vite HTML entry (replaces CRA's public/index.html)
│   ├── vite.config.js               # Main Vite config — proxy, esbuild, optimizeDeps
│   ├── postinstall.js               # Post-install compatibility patches for node_modules
│   └── package.json
├── CHANGELOG.md
└── README.md
```

---

## CRA → Vite Migration Summary

| Area | Before (CRA) | After (Vite) |
|---|---|---|
| Build tool | `react-scripts` | `vite` + `@vitejs/plugin-react` |
| Dev server | `react-scripts start` | `vite` |
| Production build | `react-scripts build` | `vite build` |
| HTML entry | `public/index.html` | `index.html` (root) |
| Env var prefix | `REACT_APP_` | `VITE_` |
| Proxy config | `src/setupProxy.js` | `vite.config.js` server.proxy |
| React render API | `ReactDOM.render` | `ReactDOM.createRoot` |
| Package builds | gulp / webpack | Vite library mode |
| CSS build | gulp | Vite + PostCSS |

---

## Docker

The app is containerized using a multi-stage Dockerfile:

1. **Build stage** — Node 22 Alpine: installs dependencies and runs `vite build`
2. **Production stage** — Nginx Alpine: serves the built files from `web/build/`

```bash
docker build --build-arg WORK_DIR=frontend/sv-ui -t sv-ui .
```

---

## Key Dependencies

| Package | Version | Purpose |
|---|---|---|
| react | ^19.0.0 | UI framework |
| react-dom | ^19.0.0 | DOM renderer |
| react-router-dom | 6.28.0 | Client-side routing |
| @upyog/digit-ui-module-core | 1.8.2 | Core shell and routing |
| @upyog/digit-ui-module-common | 1.8.12 | Payment and common flows |
| @upyog/digit-ui-module-engagement | 1.8.0 | Events, notifications, surveys |
| @nudmcdgnpm/upyog-ui-module-sv | 1.3.0 | Street Vending module |
| @nudmcdgnpm/digit-ui-libraries | 1.2.1 | Shared hooks and services |
| vite *(dev)* | ^6.0.0 | Build tool |
| @vitejs/plugin-react *(dev)* | ^4.3.4 | JSX transform and Fast Refresh |

---

## License

UPYOG Source Code is open source under the [UPYOG Code, Copyright and Contribution License](https://upyog.niua.org/employee/Upyog%20Code%20and%20Copyright%20License_v1.pdf).
