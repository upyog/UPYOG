# Changelog

**Author**: Shivank - NUDM

All notable changes to UPYOG UI will be documented in this file.

## [LTS-2024] - 2024-12-19

### 🚀 Major LTS Stack Upgrade

Complete modernization of the UPYOG UI frontend to latest LTS technologies.

#### ⬆️ Core Dependencies Upgraded

- **Node.js**: 14 → 22 (LTS)
- **React**: 17 → 19.2.0
- **React DOM**: 17 → 19.2.0
- **React Router**: v5 → v6.23+
- **React Hook Form**: v6.15.8 → v7.51+
- **React Query**: v3 → @tanstack/react-query v5
- **React Redux**: 7.2.8 → 9.0.0
- **Redux**: 4.1.2 → 4.2.1
- **Redux Thunk**: 2.4.1 → 3.1.0

#### 🔧 Build System Overhaul

- **Build Tool**: Webpack/CRA → Vite 6
- **Development Server**: Significantly faster hot reload
- **Bundle Size**: Optimized production builds
- **Monorepo**: Enhanced Yarn Workspaces integration

#### 🏗️ Architecture Improvements

- **Module Publish System**: Centralized package building with `package-builder.mjs`
- **Routing**: Migrated to React Router v6 with relative paths
- **Forms**: Updated to react-hook-form v7 for React 19 compatibility
- **State Management**: Enhanced Redux integration
- **API Layer**: Improved React Query v5 implementation

#### 🐛 Critical Fixes

- **React 19 Compatibility**: Fixed hook call issues and DOM recycling
- **Leaflet Maps**: Resolved container reinitialization problems
- **Form Validation**: Fixed register ref patterns for React 19
- **Route Transitions**: Eliminated blank screen issues
- **Bundle Deduplication**: Resolved multiple React instance conflicts



#### 🔄 Migration Benefits

- **Performance**: 3x faster development builds
- **Developer Experience**: Instant hot reload, better error messages
- **Maintainability**: Modern React patterns, improved type safety
- **Security**: Latest LTS versions with security patches
- **Future-Ready**: Prepared for upcoming React features



### 🔧 Migration Guide

See [README.md](./README.md) for complete setup and migration instructions.

---

## Previous Versions

### [Legacy] - Pre-2024

- Original UPYOG UI implementation
- Node.js 14, React 17, Webpack build system
- Individual module build configurations