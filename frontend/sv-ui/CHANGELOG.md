# Changelog

All notable changes to the sv-ui project are documented in this file.
Changes are based on commits in [PR #1370](https://github.com/niua-org/UPYOG-NIUA/pull/1370).

---

## [1.8.x] — CRA to Vite Migration

### Commits

#### Migrate project from React Scripts to Vite
- Replaced `react-scripts` with `vite` ^6.0.0 and `@vitejs/plugin-react`
- Added `vite.config.js` at `web/` root with proxy, esbuild JSX loader, optimizeDeps, and envPrefix config
- Moved `public/index.html` to root-level `index.html` with `<script type="module">` entry point
- Removed `src/setupProxy.js` and `webpack.config.js` (no longer needed with Vite)
- Renamed `src/App.js` → `src/App.jsx` and `src/index.js` → `src/index.jsx`
- Renamed env var prefix from `REACT_APP_` to `VITE_`
- Updated npm scripts: `start` → `vite`, `build` → `vite build`, added `preview`

#### Refactor HTML structure and enhance error handling
- Refactored root `index.html` with improved script error handling for CDN-loaded globals
- Updated `micro-ui-internals/example/index.html` to use Vite module script entry
- Removed `micro-ui-internals/example/public/index.html` (replaced by root-level index.html)
- Renamed `example/src/index.js` → `example/src/index.jsx`
- Enhanced `example/vite.config.js` with workspace aliases, SCSS config, and optimizeDeps

#### Add postinstall script
- Added `web/postinstall.js` to fix broken `module` fields in node_modules package.json files
- Added shim for renamed Babel plugin: `@babel/plugin-proposal-unicode-property-regex` → `@babel/plugin-transform-unicode-property-regex`

#### Enhance postinstall script
- Extended module field fix to scan up to 4 levels of node_modules nesting
- Improved shim logic to check both `web/node_modules` and `micro-ui-internals/node_modules`

#### Update Dockerfile
- Pinned Node.js base image to `node:22.0.0-alpine` for reproducible builds
- Added `git` as an Alpine dependency (required by some UPYOG install scripts)
- Removed unnecessary intermediate `yarn install` steps from build stage
- Adjusted `NODE_OPTIONS` to `--max-old-space-size=2560`

#### Update package configurations to use Vite for builds
- Added `vite.config.js` to all internal packages: `libraries`, `common`, `core`, `sv`, `engagement`, `react-components`
- Removed `.babelrc`, `brunch-config.js`, `envs.js`, and `CODEOWNERS` files no longer needed
- Removed `router_Comp_With_v6.js` (obsolete routing reference file)
- Removed `public/robots.txt` and moved `browser-icon.png` to `web/public/`
- Removed console.log statements from SV page components (`SVAdrressDetails`, `SVApplicantDetails`, `SVBankDetails`, `SVBusinessDetails`, `SVDocumentsDetail`, `SVSpecialCategory`)
- Removed obsolete Babel resolutions from all package.json files
- Updated `sv/src/utils/index.js` to remove CRA-specific utility references
- Renamed package `@upyog/digit-ui-react-components` → `@nudmcdgnpm/upyog-ui-react-components-lts`

#### Refactor package.json scripts and CSS build
- Replaced gulp-based CSS build with Vite in `packages/css/vite.config.js`
- Updated `micro-ui-internals/package.json` build scripts to use Vite
- Cleaned up exports configuration across packages

#### Add react-i18next and i18next as external dependencies
- Added `react-i18next` and `i18next` to the `external` list in vite.config.js for: `libraries`, `common`, `core`, `sv`, `engagement`, `react-components`
- Prevents i18n libraries from being bundled into each package output

#### Add engagement module
- Registered `initEngagementComponents` in `web/src/App.jsx`
- Added `Engagement` to `enabledModules` list
- Added engagement module to `micro-ui-internals/example/src/index.jsx`
- Added `@upyog/digit-ui-module-engagement` to `web/package.json` dependencies
- Added `/egov-user-event` proxy route to `web/vite.config.js`

#### Update cssnano and peer dependencies
- Updated `cssnano` to `6.0.1` in `packages/css/package.json`
- Added `react-dom` as a peer dependency in `packages/libraries/package.json`

#### Update engagement module and optimize routes
- Upgraded `vite` version in `packages/modules/engagement/package.json`
- Refactored `engagement/src/Module.js` to remove redundant route definitions
- Enhanced `EventsListOnGround/index.js` event card rendering
- Improved `NotificationsAndWhatsNew.js` layout and data handling

#### Update Vite configuration: add output settings and refactor build scripts
- Added `output.manualChunks` and `inlineDynamicImports` to `libraries` and `common` vite configs
- Added `workspace-aliases.js` to `micro-ui-internals/` for local package source aliasing in example app
- Refactored `micro-ui-internals/package.json` build scripts
- Removed unused import links from engagement module files
- Simplified `web/vite.config.js` by removing redundant options

---

### Files Removed (CRA artifacts)
- `web/src/setupProxy.js`
- `web/webpack.config.js`
- `web/.babelrc`
- `web/.env.sample`
- `web/brunch-config.js`
- `web/envs.js`
- `web/router_Comp_With_v6.js`
- `web/public/robots.txt`
- `web/micro-ui-internals/example/public/index.html`
- `web/micro-ui-internals/example/src/setupProxy.js`
- `frontend/sv-ui/CODEOWNERS`
- `web/micro-ui-internals/CODEOWNERS`

### Files Added (Vite setup)
- `web/vite.config.js`
- `web/index.html`
- `web/postinstall.js`
- `web/micro-ui-internals/example/vite.config.js`
- `web/micro-ui-internals/example/index.html`
- `web/micro-ui-internals/workspace-aliases.js`
- `web/micro-ui-internals/packages/libraries/vite.config.js`
- `web/micro-ui-internals/packages/modules/common/vite.config.js`
- `web/micro-ui-internals/packages/modules/core/vite.config.js`
- `web/micro-ui-internals/packages/modules/sv/vite.config.js`
- `web/micro-ui-internals/packages/modules/engagement/vite.config.js`
- `web/micro-ui-internals/packages/react-components/vite.config.js`
- `web/micro-ui-internals/packages/css/vite.config.js`
