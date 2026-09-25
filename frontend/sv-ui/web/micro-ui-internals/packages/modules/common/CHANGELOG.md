# Changelog — @upyog/digit-ui-module-common

All notable changes to this module are documented here.

---

## [1.8.12] — LTS Fork, Vite Migration & Dependency Upgrades

### Version
- Initial release of `@upyog/digit-ui-module-common` at version `1.8.12`

### Build Tool
- Replaced `microbundle-crl` with `vite ^6.0.0` and `@vitejs/plugin-react ^4.3.4`
- Old scripts: `microbundle-crl watch` (start) and `microbundle-crl --compress` (build)
- New scripts: `vite build --watch` (start) and `vite build` (build)
- Removed `prepublish` script (no longer needed)
- Build now outputs two formats: `index.modern.js` (ES) and `index.js` (CommonJS)
- Added `output.manualChunks: undefined` and `inlineDynamicImports: true` — everything packed into a single output file

### React
- Added `react >=19` and `react-dom >=19` as peerDependencies (host app must provide React)

### Dependencies Upgraded
| Package | Before | After |
|---|---|---|
| react-hook-form | 6.15.8 | ^7.51.0 |
| react-i18next | 11.16.2 | ^14.0.0 |
| react-router-dom | 5.3.0 | 6.28.0 |
| react-query | 3.6.1 (react-query) | ^5.0.0 (@tanstack/react-query) |

### Removed
- `microbundle-crl` — replaced by Vite
- `rooks 4.11.2` — no longer used
- `react` and `react-dom` from dependencies (now peerDependencies)
- `.babelrc` and `brunch-config.js` — not needed with Vite

---

## [3.10.0] — Previous UPYOG Base Version

- Built with `microbundle-crl`
- React 17, react-router-dom v5, react-query v3
- Published under `@upyog/digit-ui-module-common`
