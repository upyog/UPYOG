# Changelog — @nudmcdgnpm/digit-ui-libraries

All notable changes to this package are documented here.

---

## [1.2.1] — LTS Fork, Vite Migration & Dependency Upgrades

### Version
- Initial release of `@nudmcdgnpm/digit-ui-libraries` at version `1.2.1`

### Build Tool
- Replaced `microbundle-crl` with `vite ^6.0.0` and `@vitejs/plugin-react ^4.3.4`
- Old scripts: `microbundle-crl watch` (start) and `microbundle-crl --compress` (build)
- New scripts: `vite build --watch` (start) and `vite build` (build)
- Removed `prepublish` script (no longer needed)
- Build outputs two formats: `index.modern.js` (ES) and `index.js` (CommonJS)
- Added `output.manualChunks: undefined` and `inlineDynamicImports: true` — everything packed into a single output file

### React
- Added `react >=19` and `react-dom >=19` as peerDependencies (host app must provide React)

### Dependencies Upgraded
| Package | Before | After |
|---|---|---|
| axios | ^0.24.0 | ^1.6.0 |
| date-fns | ^2.x | ^3.6.0 |
| react-i18next | 11.16.2 | ^14.0.0 |
| i18next | ^21.x | ^23.7.0 |
| react-router-dom | 5.3.0 | 6.28.0 |
| react-query | 3.6.1 (react-query) | ^5.0.0 (@tanstack/react-query) |

### Added
- `i18next-react-postprocessor ^3.0.7` — post-processing support for i18next translations
- `dom-to-image 2.6.0` — DOM to image conversion utility

### Removed
- `microbundle-crl` — replaced by Vite
- `react` and `react-dom` from dependencies (now peerDependencies)
- `.babelrc` and `brunch-config.js` — not needed with Vite

---

## [3.10.0] — Previous UPYOG Base Version

- Built with `microbundle-crl`
- React 17, react-router-dom v5, react-query v3, axios v0.x
- Published under `@upyog/digit-ui-libraries`
