# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2025-05-05
- Upgraded Node.js from Node 10 to v22 LTS
- Removed Babel transpilation layer — service now runs natively on Node 22 with ES Modules
- Replaced `kafka-node` (abandoned) with `kafkajs ^2.2.4`
- Removed deprecated `request` library — all HTTP calls use `axios`
- Upgraded `axios` from `0.18.0` to `1.7.0` — security fixes
- Upgraded `uuid` from `v3` to `v9` — ESM compatibility
- Upgraded `express` from `4.13.3` to `4.18.2` — security patches
- Upgraded `pdfmake` from `0.2.4` to `0.2.10`
- Upgraded `pdf-merger-js` from `3.2.1` to `5.1.1` — `merger.add()` is now async
- Upgraded Docker base image from `egovio/alpine-node-builder-10` to `node:22-alpine`
- Removed unused boilerplate folders: `src/api/`, `src/middleware/`, `src/lib/`, `src/models/`
- Removed `.babelrc` configuration file
- Added `"type": "module"` to `package.json` for native ESM support
- All `require()` calls replaced with ESM `import` statements
- Fixed `__dirname` for ESM using `import.meta.url`
- Fixed `pdfmake` vfs API — `pdfFonts.pdfMake.vfs` → `pdfFonts.vfs`

## 1.2.1 - 2023-02-06
- Transition from 1.2.1-beta version to 1.2.1 version

## 1.2.1-beta - 2022-08-09
- Fixed localisation cache issue.

## 1.2.0 - 2022-04-20
- Enhance pdf service to support bulk pdf creation.

## 1.1.6 - 2022-01-13
- Updated to log4j2 version 2.17.1

## 1.1.6 - 2021-12-03
- Fixed Language switch isue.

## 1.1.5 - 2021-09-24
- Added support for punjabi and odiya language.

## 1.1.4 - 2021-05-11
- Fixed security issue.

## 1.1.3 - 2021-03-17
- Optimisied the service by reducing the localisation service call

## 1.1.2 - 2021-02-26
- Updated domain name in application.properties

## 1.1.1 -2021-01-12
- Handled special character as input in PDF

## 1.1.0 - 2020-19-06
- Added PDF service to generate PDFs at server based on configs     
- Current Express version `4.13.3`
- Current pdfmake version `0.1.56`
- Current Kafka-node version `4.1.3`
