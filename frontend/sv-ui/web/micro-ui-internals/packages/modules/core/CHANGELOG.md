# Changelog — @upyog/digit-ui-module-core

All notable changes to this module are documented here.

---

## [1.8.2] — LTS Fork, Vite Migration & Dependency Upgrades

### Version
- Initial release of `@upyog/digit-ui-module-core` at version `1.8.2`

### Build Tool
- Replaced `microbundle-crl` with `vite ^6.0.0` and `@vitejs/plugin-react ^4.3.4`
- Old scripts: `microbundle-crl watch` (start) and `microbundle-crl --compress` (build)
- New scripts: `vite build --watch` (start) and `vite build` (build)
- Removed `prepublish` script (no longer needed)
- Build now outputs two formats: `index.modern.js` (ES) and `index.js` (CommonJS)

### React
- Added `react >=19` and `react-dom >=19` as peerDependencies (host app must provide React)

### Dependencies Upgraded
| Package | Before | After |
|---|---|---|
| react-i18next | 11.16.2 | ^14.0.0 |
| react-router-dom | 5.3.0 | 6.28.0 |
| react-query | 3.6.1 (react-query) | ^5.0.0 (@tanstack/react-query) |

### Packages Renamed
| Before | After |
|---|---|
| @upyog/digit-ui-react-components 3.10.0 | @nudmcdgnpm/upyog-ui-react-components-lts 1.0.0 |

### Added
- `react-tooltip ^5.26.0` — tooltip support for UI components
- `react-google-recaptcha ^3.1.0` — reCAPTCHA support for login/forms
- `keywords` field added: `upyog`, `nudm`, `dpg`, `sv-ui`, `core`, `niua`

### Removed
- `microbundle-crl` — replaced by Vite
- `rooks` — no longer used
- `react` and `react-dom` from dependencies (now peerDependencies)
- `.babelrc` and `brunch-config.js` — not needed with Vite

---

## [3.10.0] — Previous UPYOG Base Version

- Built with `microbundle-crl`
- React 17, react-router-dom v5, react-query v3
- Published under `@upyog/digit-ui-module-core`
