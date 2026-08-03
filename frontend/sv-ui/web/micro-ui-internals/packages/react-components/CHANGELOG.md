# Changelog — @nudmcdgnpm/upyog-ui-react-components-lts

All notable changes to this package are documented here.

---

## [1.0.0] — LTS Fork, Vite Migration & Dependency Upgrades

### Version
- Initial release of `@nudmcdgnpm/upyog-ui-react-components-lts` at version `1.0.0`

### Build Tool
- Replaced `microbundle-crl` with `vite ^6.0.0` and `@vitejs/plugin-react ^4.3.4`
- Old scripts: `microbundle-crl watch` (start) and `microbundle-crl --compress` (build)
- New scripts: `vite build --watch` (start) and `vite build` (build)
- Removed `prepublish` script (no longer needed)
- Build outputs two formats: `index.modern.js` (ES) and `index.js` (CommonJS)

### React
- Moved `react 17.0.2` and `react-dom 17.0.2` out of dependencies

### Dependencies Upgraded
| Package | Before | After |
|---|---|---|
| react-hook-form | 6.15.8 | ^7.51.0 |
| react-i18next | 11.16.2 | ^14.0.0 |
| date-fns | ^2.x | ^2.30.0 |

### Packages Renamed / Replaced
| Before | After |
|---|---|
| react-query 3.6.1 | removed (not needed in components layer) |

### Added
- `@googlemaps/js-api-loader 1.13.10` — Google Maps integration for location-based components
- `react-date-range 1.3.0` — date range picker component

### Removed
- `microbundle-crl` — replaced by Vite
- `react` and `react-dom` from dependencies (now peerDependencies)
- `react-query` — data fetching moved to libraries package
- `.babelrc` and `brunch-config.js` — not needed with Vite

---

## [3.10.0] — Previous UPYOG Base Version

- Built with `microbundle-crl`
- React 17, react-hook-form v6, react-i18next v11
- Published under `@upyog/digit-ui-react-components`
