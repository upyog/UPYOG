# Changelog — @upyog/digit-ui-module-engagement

All notable changes to this module are documented here.

---

## [1.8.0] — New Module, Vite Setup & Dependency Upgrades

### Version
- Initial release of `@upyog/digit-ui-module-engagement` at version `1.8.0`

### Build Tool
- Built with `vite ^6.0.0` and `@vitejs/plugin-react ^4.3.4` from the start
- Scripts: `vite build --watch` (start) and `vite build` (build)
- Build outputs two formats: `index.modern.js` (ES) and `index.js` (CommonJS)
- React plugin configured with `include: /\.(jsx|js)$/` to handle both file types
- ESBuild `logOverride` added to silence known `duplicate-object-key` and `duplicate-case` warnings
- Output `globals` defined for browser compatibility: `react → React`, `react-dom → ReactDOM`, `react-router-dom → ReactRouterDOM`

### React
- Uses `react 19.0.0` and `react-dom 19.0.0` (both in dependencies and peerDependencies)

### Dependencies
| Package | Version |
|---|---|
| @nudmcdgnpm/upyog-ui-react-components-lts | 1.0.0 |
| react | 19.0.0 |
| react-dom | 19.0.0 |
| react-hook-form | ^7.51.0 |
| react-i18next | ^14.0.0 |
| @tanstack/react-query | ^5.0.0 |
| react-router-dom | 6.28.0 |
| react-time-picker | 4.2.1 |
| recharts | ^2.10.0 |

### Module Setup
- Registered `initEngagementComponents` in `web/src/App.jsx`
- Added `Engagement` to `enabledModules` list in the main app
- Added `/egov-user-event` proxy route to `web/vite.config.js`
- Refactored `src/Module.js` to remove redundant route definitions
- Enhanced `EventsListOnGround/index.js` event card rendering
- Improved `NotificationsAndWhatsNew.js` layout and data handling
- Removed unused imports across module files

---

## [3.10.0] — Previous UPYOG Base Version

- Engagement features existed as part of the monolithic UPYOG module
- Not published as a standalone package in the old structure
