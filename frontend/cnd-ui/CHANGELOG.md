**Upgraded by :** Harshita Purohit

**Under The Guidance of :** Shivank - NUDM

## Migration Inventory

### Platform Modernization & Core Upgrades
- Upgraded the React platform core runtime to React 19 (`19.2.0`) and React DOM (`19.2.0`).
- Modernized peer dependency libraries, aligning with React 19 and Vite requirements.

### Build System & Tooling Modernization
- Migrated the application compilation baseline to Vite (`^6.4.1`) from legacy compilation utilities.
- Configured dynamic monorepo workspace package mapping and compilation pipelines.

### Routing & Navigation Migration
- Upgraded routing structure from React Router v5 to v6 (`^6.23.0`).
- Implemented nested sub-route matching, relative paths, and scroll context resets.

### State Management & Query Layer Migration
- Upgraded the data query engine from `react-query` v3 (`3.6.1`) to TanStack Query v5 (`^5.0.0`).
- Standardized hook cache parameter signatures and normalized QueryClient instantiations.

### React Hook Form Migration
- Migrated the forms engine from React Hook Form v6 (`6.15.8`) to v7 (`^7.51.0`).
- Updated registration syntaxes and integrated form controller wrappers.

### Redux Compatibility Updates
- Upgraded `react-redux` to v9 (`^9.0.0`) to ensure runtime compatibility with React 19.

### Citizen Flow Stabilization
- Stabilized the CND application creation lifecycle with mutation APIs and context state sharing.
- Fixed Citizen booking acknowledgement page to survive browser reloads:
  - Appended query parameters (`applicationNumber`, `tenantId`) on successful creation in `Create/index.js`.
  - Implemented backend retrieval via `useCndSearchApplication` fallback in `CndAcknowledgement.js` when history state is null.
  - Modified `useCustomBackNavigation` to preserve query parameters in the address bar during history state manipulation.

### Employee Flow Stabilization
- Modernized inbox search criteria filters and hooks.
- Fixed Employee search input fields and disabled search button by migrating legacy `react-hook-form` controller and ref registration syntax to v7 (`search.js` and `CNDSearchApplication.js`).
- Aligned workflow routing states with React Router v6 navigation context changes.

### Validation Standardization
- Enforced standardized postal pincode validations with character caps and regex utility lookups.
- Standardized mobile number inputs and validation messages.

### Runtime & Stability Improvements
- Patched core reducers to prevent undefined boot crashes in Redux store setup.
- Restructured query conditions and element loading references to avoid runtime failures.

### Technical Debt Reduction
- Deleted obsolete legacy Brunch and custom Webpack build files.
- Removed deprecated routing matchers and redirect components.



---

# Changelog

## Release Summary

This release delivers the Long-Term Support (LTS) modernization of the **Construction & Demolition (CND) UI** platform. The codebase has been migrated from legacy compilation frameworks to a high-performance **Vite** pipeline and upgraded to modern runtime dependencies (**React 19**, **React Router v6**, **TanStack Query v5**, and **React Hook Form v7**). 

These upgrades eliminate technical debt, enhance application responsiveness, ensure long-term framework support, and stabilize core customer workflows.

---

## Technical Changes

### Build & Tooling
- **Vite Migration**: Replaced legacy Brunch and Create React App compilation pipelines with a modernized Vite compilation engine.
- **Monorepo Workspace Consolidation**: Consolidated the monorepo architecture by bringing the bills, reports, and shared CSS packages into the unified workspaces setup.
- **Dependency Isolation**: Deduplicated core dependency packages (including React, React Router, and TanStack Query) to prevent multi-instance runtime collisions.

### React & Framework Upgrades
- **React 19 Platform Upgrade**: Upgraded the core application and package dependencies to React 19 (`19.2.0`) and React DOM (`19.2.0`), adopting the modern React 18+ Root API (`createRoot`).
- **React Router v6 Migration**: Replaced React Router v5 with v6, updating routing structures, relative path definitions, and navigation mechanisms.
- **TanStack Query v5 Migration**: Migrated all asynchronous state query interfaces from `react-query` v3 to TanStack Query v5.
- **React Hook Form v7 Compatibility Migration**: Upgraded the form validation engine from v6 to v7 to ensure form compatibility.
- **React Redux v9 Upgrade**: Upgraded Redux binding definitions to v9 to maintain compatibility with modern React context mechanisms.

### Routing & Navigation
- **Nested Route Path Resolution**: Transitioned legacy route structures to Router v6 `<Routes>` and configured nested route wildcard suffixes.
- **Relative Path Routing**: Migrated sub-page routes to use relative path matching, removing hardcoded match path prefixes.
- **Navigation Context Updates**: Replaced legacy history manipulation with relative navigation hooks.
- **Route Transition Viewport Reset**: Introduced pathname-driven scroll synchronization hooks to reset scroll viewport coordinates on route transitions.

### State Management & Query Layer
- **Redux Initialization Fixes**: Patched core reducer definitions with default empty state fallbacks to prevent runtime crashes during store setup.
- **Query Cache Parameters**: Standardized cache settings globally by updating query options to conform with modern API signatures.
- **Query Client Persistence**: Wrapped client initialization routines in React hooks to prevent cache drops and redundant query calls on parent re-renders.

### Form Handling & Validation
- **Indian Pincode Validation Standardization**: Standardized pincode inputs with numeric-only rules and system-wide pattern validation.
- **Mobile Number Validation Standardization**: Standardized mobile number inputs to enforce standard validation checks.
- **React Hook Form v7 Controller Integrations**: Standardized forms using Controller components to wrap complex dropdown selectors and inputs.

### Citizen Flow Stabilization
- **Citizen Acknowledgement Flow Stabilization**: Refactored acknowledgement handling to persist submission state across page refreshes, prevent duplicate application creation requests, and improve post-submission navigation reliability.
- **Refresh Persistence Handling**: Introduced session-based persistence for critical application and acknowledgement states to maintain continuity across browser refreshes.
- **Duplicate Submission Prevention**: 
Eliminated repeated application creation requests caused by acknowledgement page refreshes and re-entry scenarios.

### Employee Flow Stabilization
- **Inbox Search Configurations**: Refactored inbox criteria parameters to leverage query caching features.
- **Workflow State Integrations**: Adjusted workflow status updates to align with Router v6 navigation context changes.

### Runtime & Stability Fixes
- **Redux Bootstrap Crash Patch**: Fixed runtime boot crashes caused by undefined default initial states inside common hooks.
- **RHF v7 Runtime Fixes**: Resolved form validation state extraction errors during page loading by replacing legacy registration bindings with hook controller elements.
- **Query Initialization Stability**: Stabilized hook execution by wrapping queries with active `enabled` conditions dependent on valid parent param settings.

### Technical Debt Reduction
- **Legacy Compilation Deletion**: Removed obsolete Brunch and custom Webpack configurations.
- **Deprecated Routing Cleanup**: Cleaned up legacy route wrappers, removing obsolete route matching and redirect logics.

---

## Build Verification

- Dependency installation completed successfully.
- Production build completed successfully.
- Deployable assets generated successfully.

---

## Known Limitations

- **Bundle Size Optimization**: Initial bundle sizes exceed 500 kB. Rollup manual code splitting or dynamic `import()` configurations should be introduced in future cycles to further refine initial load telemetry.

---

## Previous Release Notes (Draft Layout)

### Added

- **Workspace Integration**: Integrated `@upyog/digit-ui-module-bills` and `@nudmcdgnpm/digit-ui-module-reports` into the CND-UI monorepo workspace configurations to allow local development and build consolidation.
- **Vite Build Infrastructure**: Introduced Vite configurations (`vite.config.js`) supporting ESBuild JSX compilation, API proxies, and workspace HMR.

### Changed

- **React Runtime Upgrade**: Migrated the core application and modules from legacy runtimes to React 19 (`19.2.0`) and React DOM (`19.2.0`).
- **React Router Migration**: Upgraded routing architecture from React Router v5 to v6 (`^6.23.0`).
- **TanStack Query Migration**: Upgraded data-fetching layers from `react-query` v3 (`3.6.1`) to TanStack Query v5 (`^5.0.0`).
- **React Hook Form Migration**: Modernized form handling from React Hook Form v6 to v7 (`^7.51.0`).
- **Redux Integration**: Modernized Redux bindings by upgrading `react-redux` to v9 (`^9.0.0`).
- **Internationalization Upgrades**: Upgraded `react-i18next` to v14 (`14.0.0`) and `i18next` to `^23.16.0`.

### Fixed

- **Query Client Multiple Re-creations**: Fixed redundant React QueryClient instantiation by wrapping QueryClient creation in a React `useState` hook.
- **Route Definitions and Fallbacks**: Fixed routing issues by migrating `<Switch>` to v6 `<Routes>` and resolving legacy `<Redirect>` routes using `<Navigate>`.
- **Duplicate Request Loops**: Fixed potential request recursion in custom hook definitions by adopting modern TanStack Query caching signatures and removing manual retry delay functions.
- **Scroll Position Reset**: Fixed navigation behavior by introducing a scroll-to-top component handler that listens directly to router pathname updates.
- **Webpack & Brunch Configurations**: Cleaned up the build system by deleting obsolete `brunch-config.js` and `webpack.config.js` configurations.

### Technical Improvements

- **Abstracted Query Templates**: Created standardized wrapper hooks (`queryTemplate` and `useQueryClient` templates) to decouple components from direct TanStack Query v5 dependencies and ease the v3-to-v5 signature transition.
- **Dynamic Package Aliasing**: Implemented a dynamic workspace scanner inside `vite.config.js` to automatically resolve local packages (like `@upyog/digit-ui-libraries`) from local sources, preventing duplicate instances of React.
- **V5 Query Parameter Alignments**: Migrated configuration options like `cacheTime` to `gcTime` globally across query hooks.
- **Legacy JS-JSX Support**: Configured ESBuild to process `.js` files containing JSX tags without throwing compiler syntax failures.
