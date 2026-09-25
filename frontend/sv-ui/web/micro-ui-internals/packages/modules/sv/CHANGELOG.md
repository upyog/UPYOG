# Changelog — @nudmcdgnpm/upyog-ui-module-sv

All notable changes to this module are documented here.

---

## [1.2.10] — LTS Fork, Vite Migration & Dependency Upgrades

### Version
- Initial release of `@nudmcdgnpm/upyog-ui-module-sv` at version `1.3.0`

### Build Tool
- Replaced `microbundle-crl` with `vite ^6.0.0` and `@vitejs/plugin-react ^4.3.4`
- Old scripts: `microbundle-crl watch` (start) and `microbundle-crl --compress` (build)
- New scripts: `vite build --watch` (start) and `vite build` (build)
- Removed `prepublish` script (no longer needed)
- Build now outputs two formats: `index.modern.js` (ES) and `index.js` (CommonJS)
- Note: `react-redux` and `redux` are NOT external — they are bundled into this module's output

### React
- Moved `react 17.0.2` and `react-dom 17.0.2` out of dependencies
- Added `react >=19`, `react-dom >=19`, and `react-router-dom >=6` as peerDependencies

### Dependencies Upgraded
| Package | Before | After |
|---|---|---|
| react-hook-form | 6.15.8 | ^7.51.0 |
| react-i18next | 11.16.2 | ^14.0.0 |
| react-router-dom | 5.3.0 | 6.28.0 |
| react-query | 3.6.1 (react-query) | ^5.0.0 (@tanstack/react-query) |

### Packages Renamed
| Before | After |
|---|---|
| @upyog/digit-ui-libraries 3.10.0 | @nudmcdgnpm/digit-ui-libraries 1.2.1 |
| @upyog/digit-ui-react-components 3.10.0 | @nudmcdgnpm/upyog-ui-react-components-lts 1.0.0 |

### Added
- `exif-js ^2.3.0` — reads EXIF data from uploaded images
- `jspdf ^2.5.1` — PDF generation for certificates
- `jspdf-autotable ^3.8.2` — table support inside generated PDFs
- `lodash.merge ^4.6.2` — deep object merging utility
- `qrcode ^1.5.3` — QR code generation for vendor certificates
- `xlsx ^0.18.5` — Excel file export support

### Removed
- `microbundle-crl` — replaced by Vite
- `rooks` — no longer used
- `react` and `react-dom` from dependencies (now peerDependencies)
- `.babelrc` and `brunch-config.js` — not needed with Vite
- `console.log` statements removed from: `SVAddressDetails`, `SVApplicantDetails`, `SVBankDetails`, `SVBusinessDetails`, `SVDocumentsDetail`, `SVSpecialCategory`
- CRA-specific utility references removed from `src/utils/index.js`

---

## [3.10.0] — Previous UPYOG Base Version

- Built with `microbundle-crl`
- React 17, react-router-dom v5, react-query v3
- Published under `@upyog/digit-ui-module-sv`
