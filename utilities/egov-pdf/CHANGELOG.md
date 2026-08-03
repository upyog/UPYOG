# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2026-05-08

### Upgraded
- Upgraded to Node.js 22
- Replaced `kafka-node` with `kafkajs` v2.2.4 — kafka-node was unmaintained and incompatible with Node 22
- Replaced `jade` with `pug` v3.0.2 — jade was renamed to pug, unmaintained and had security vulnerabilities
- Upgraded `axios` from 0.19.2 to 1.7.0
- Upgraded `uuid` from 3.3.3 to 9.0.0 — updated import style from `require('uuid/v4')` to `const { v4: uuidv4 } = require('uuid')`
- Upgraded `express` from 4.16.1 to 4.18.2
- Upgraded `http-errors` from 1.6.3 to 2.0.0
- Upgraded `debug` from 2.6.9 to 4.3.4
- Upgraded `morgan` from 1.9.1 to 1.10.0
- Upgraded `pg` from 8.7.1 to 8.11.3
- Upgraded `winston` from 3.2.1 to 3.11.0
- Upgraded `cookie-parser` from 1.4.4 to 1.4.7

### Added
- `dotenv` v16.4.5 — environment variable management via `.env` file
- `express-async-handler` v1.2.0 — async error propagation in Express routes
- `nodemon` v3.0.2 (devDependency) — auto-restart during development
- `eslint` v8.56.0 (devDependency) — static code analysis
- `engines` field in package.json enforcing Node >=22

## 1.2.1 - 2023-09-13

- Central Instance Library Integration

## 1.2.0 - 2023-02-02

- Transition from 1.2.0-beta version to 1.2.0 version

## 1.2.0-beta - 2022-04-20
- Added bulk bill feature for w&s

## 1.1.3 - 2022-04-28

- Added new api for birth and death download certificate

## 1.1.3 - 2022-04-28

- Added new api for birth and death download certificate

## 1.1.2 - 2021-12-14

- Added support for download receipt by receipt number 

## 1.1.1 - 2021-07-26

- Added fetch bill api for expired bill
- Added new API for wns group bills


## 1.1.0 - 2021-05-12

- Added rotes/middleware for amendment certificate, consolidate bill, TL bill and W&S bill and receipt PDF.
- Fixed security issue.

## 1.0.1 - 2021-01-12
- Added changes due to collection service dependency

## 1.0.0 - 2020-06-16
- Added this service with support for following PDFs
  - PT mutation cetificate
  - PT bill
  - PT receipt
  - TL receipt
  - TL certificate
  - TL renewal certificate
  - Consolidated receipt         
- Current Express version `4.16.1`
