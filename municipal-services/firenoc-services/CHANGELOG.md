# Changelog
All notable changes to this module will be documented in this file.

## 2.0.0 - 2026-07-15

### Node.js 22 Upgrade

#### Why This Upgrade Was Needed
- Node.js 14/16 reached End-of-Life and no longer receives security patches
- Node.js 22 is the current LTS release with long-term support until 2027
- Several dependencies were incompatible or unmaintained on older Node versions
- Aligns the service with the Dockerfile which was already using `node:22.11.0-alpine`

---

### Build Toolchain — Babel 6 → Babel 7

**What changed:**
- Removed `babel-cli@6`, `babel-core@6`, `babel-preset-es2015`, `babel-preset-stage-0`, `babel-plugin-transform-runtime@6`, `babel-polyfill@6`, `babel-loader@8`

- Installed `@babel/core@7`, `@babel/cli@7`, `@babel/node@7`, `@babel/preset-env@7`, `@babel/plugin-transform-runtime@7`, `@babel/runtime@7`

- Rewrote `.babelrc` to Babel 7 format targeting Node 22

- Updated `package.json` build and dev scripts to use `@babel/node` and `@babel/cli`

- Removed `require("babel-core/register")` and `require("babel-polyfill")` from `src/index.js`

**Why it was needed:**
- Babel 6 was released in 2015 and has been fully unsupported since 2018

- `babel-preset-es2015` and `babel-preset-stage-0` are deprecated and do not compile correctly on Node 22

- Babel 7 with `@babel/preset-env` targeting `{ node: "22" }` produces leaner, more accurate output since Node 22 natively supports most ES2022+ features without transpilation

---

### Kafka Client — kafka-node → kafkajs

**What changed:**
- Removed `kafka-node@5` from dependencies
- Upgraded `kafkajs` from `1.16.0` → `^2.2.4`
- Fully rewrote `src/kafka/producer.js` using the kafkajs API
- Added a backward-compatible `send(payloads, callback)` adapter so `create.js`, `update.js`, `notificationUtil.js` and `consumer.js` required zero changes
- Fixed KafkaJS v2 partitioner warning by using `Partitioners.LegacyPartitioner`

**Why it was needed:**
- `kafka-node` last published in 2020, has known memory leaks and is incompatible with Node 22
- `kafkajs` is the actively maintained community standard for Kafka in Node.js
- `kafkajs` uses native async/await vs `kafka-node`'s callback-only event-based API
- Consumer was already using `kafkajs` — producer was the only remaining `kafka-node` usage, creating an inconsistency

---

### UUID — uuid@3 → uuid@9

**What changed:**
- Upgraded `uuid` from `^3.3.2` → `^9.0.0`
- Fixed `require("uuid/v4")` subpath import in `src/utils/index.js` → `import { v4 as uuidv4 } from "uuid"`
- Fixed `require("uuid/v4")` subpath import in `src/middleware/tracer.js` → `import { v4 as uuidv4 } from "uuid"`

**Why it was needed:**
- `uuid@3` used deep subpath requires (`uuid/v4`) which are not supported in uuid@7+
- Node 22 enforces stricter ESM/CJS subpath resolution — the old import pattern throws at runtime

---

### Dependency Upgrades

| Package | From | To | Reason |
|---|---|---|---|
| `axios` | `^0.18.0` | `^1.6.0` | v0.x is EOL, v1.x has security fixes and better error handling |
| `cross-env` | `^5.2.0` | `^7.0.0` | v5 has known security vulnerabilities |
| `nodemon` | `^1.9.2` | `^3.0.0` | v1 incompatible with Node 22 file watching APIs |
| `eslint` | `^5.15.2` | `^8.0.0` | v5 does not support ES2022 syntax parsing |
| `kafkajs` | `1.16.0` | `^2.2.4` | v2 is the current stable release with Node 22 support |

---

### Unused Dependencies Removed

The following packages were identified as never imported anywhere in the source code and were removed to reduce attack surface and install size:

| Package | Why Removed |
|---|---|
| `kafka-node` | Replaced by kafkajs for producer |
| `compression` | Imported in zero source files |
| `resource-router-middleware` | Imported in zero source files |
| `swagger-express-validator` | Commented out in `src/index.js`, never used |
| `swagger-model-validator` | Imported in zero source files |
| `util` | Node 22 has this built-in, npm shim never imported |
| `random-number` | Imported in zero source files |
| `babel-loader` | Only needed for webpack — no webpack config exists in this project |

---

## 1.3.3 - 2023-08-10

- Central Instance Library Integration
- 
## 1.3.2 - 2023-02-01

- Transition from 1.3.2-beta version to 1.3.2 version
- Modify citizen search to incorporate a bypass for searching with application uuid instead of user uuid

## 1.3.2-beta - 2022-01-13

- Updated to log4j2 version 2.17.1

## 1.3.2 - 2021-12-15

- Updated search query for citizen search
- Added validation for system payment

## 1.3.1 - 2021-07-26

- Added missing state in workflow config of firenoc service
- Added OTHERS in gender validation 

## 1.3.0 - 2021-05-11

- Fixed security issue of untrusted data pass as user input.
- Fixed issue of workflow approval without payment

## 1.2.0 - 2021-03-19

- Added sendback to citizen feature in product.

## 1.1.2 - 2021-02-26

- Updated domain name in application.properties.

## 1.1.1 - 2021-01-12

- Added validation for workflow call response.

## 1.1.0 - 2020-10-1

- Added Index to achieve performance benefits.
- Update the workflow service call.

## 0.0.2

- Base version
