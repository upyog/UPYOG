/**
 * ========================= VITE CONFIG EXPLANATION =========================
 *
 * This configuration is designed for a monorepo setup where multiple internal
 * packages (modules, libraries, react-components) are built first using:
 *
 *      yarn build
 *
 * and then served using:
 *
 *      yarn start
 *
 * IMPORTANT:
 * - This setup ensures proper aliasing of internal workspace packages and avoids
 *   dependency duplication issues (like multiple React instances).
 *
 * ---------------------------------------------------------------------------
 * 1. IMPORTS & SETUP
 * ---------------------------------------------------------------------------
 * - defineConfig: Helps with better typing and IntelliSense.
 * - loadEnv: Loads environment variables based on mode.
 * - react plugin: Enables React Fast Refresh + JSX support.
 * - path/fs: Used for resolving file paths and reading package.json files.
 * - fileURLToPath: Required because Vite config runs in ES module mode.
 *
 * __dirname is manually recreated because ES modules don't provide it by default.
 *
 * ---------------------------------------------------------------------------
 * 2. ENVIRONMENT VARIABLES
 * ---------------------------------------------------------------------------
 * - Loads all env variables using loadEnv().
 * - Supports REACT_APP_* variables (kept for compatibility with CRA-style apps).
 *
 * proxyTarget:
 *   - API base URL (default: NIUA staging server).
 *
 * assetsTarget:
 *   - Used specifically for asset endpoints (can differ from API).
 *
 * ---------------------------------------------------------------------------
 * 3. API PROXY CONFIGURATION
 * ---------------------------------------------------------------------------
 * - apiPaths array contains all backend service routes.
 * - Each path is proxied to the backend server to avoid CORS issues.
 *
 * Example:
 *   /user → https://niuatt.niua.in/user
 *
 * - `/pb-egov-assets` is separately proxied to assetsTarget.
 *
 * ---------------------------------------------------------------------------
 * 4. MONOREPO PACKAGE ALIASING (CRITICAL PART)
 * ---------------------------------------------------------------------------
 * packagesRoot → points to ../packages directory.
 *
 * getAliases():
 *   - Dynamically scans all workspace packages.
 *   - Reads each package.json.
 *   - Maps package name → entry file (main or src/index.js).
 *
 * Why this matters:
 * - Allows importing internal packages like:
 *      import Something from "@upyog/digit-ui-react-components";
 * - Prevents Vite from resolving them from node_modules.
 * - Ensures local development uses latest source/build.
 *
 * It scans:
 *   - packages/modules/* (multiple packages)
 *   - packages/libraries (single package)
 *   - packages/react-components (single package)
 *
 * ---------------------------------------------------------------------------
 * 5. BASE CONFIGURATION
 * ---------------------------------------------------------------------------
 * base:
 *   - Production: "/upyog-ui/" (important for deployment path)
 *   - Dev: "/" (local server root)
 *
 * define:
 *   - Injects process.env variables so existing code doesn't break.
 *
 * ---------------------------------------------------------------------------
 * 6. MODULE RESOLUTION
 * ---------------------------------------------------------------------------
 * resolve.alias:
 *   - Uses dynamically generated aliases for workspace packages.
 *
 * dedupe:
 *   - Ensures only ONE instance of React & React DOM is used.
 *   - Prevents errors like:
 *       "Invalid hook call"
 *
 * ---------------------------------------------------------------------------
 * 7. ESBUILD CONFIGURATION
 * ---------------------------------------------------------------------------
 * - Treats ALL `.js` files as JSX.
 *
 * Why:
 *   - Some workspace packages use JSX inside `.js` files.
 *   - Prevents syntax errors during build.
 *
 * ---------------------------------------------------------------------------
 * 8. DEV SERVER CONFIGURATION
 * ---------------------------------------------------------------------------
 * port: 3000
 *
 * proxy:
 *   - Applies API proxy rules defined earlier.
 *
 * fs.allow:
 *   - Allows Vite to access files outside current directory (monorepo support).
 *
 * watch:
 *   - Enables file watching across workspace packages.
 *   - Uses polling (important for Linux/WSL/Docker environments).
 *
 * hmr:
 *   - Enables Hot Module Replacement.
 *
 * ---------------------------------------------------------------------------
 * 9. BUILD CONFIGURATION
 * ---------------------------------------------------------------------------
 * outDir:
 *   - Output directory is "build" (instead of default "dist").
 *
 * sourcemap:
 *   - Disabled for production optimization.
 *
 * rollupOptions.manualChunks:
 *   - Separates vendor libraries into a separate chunk.
 *
 * Benefit:
 *   - Better caching
 *   - Faster load times
 *
 * ---------------------------------------------------------------------------
 * 10. DEPENDENCY OPTIMIZATION (VERY IMPORTANT)
 * ---------------------------------------------------------------------------
 * optimizeDeps.include:
 *   - Forces Vite to pre-bundle core dependencies.
 *
 * optimizeDeps.exclude:
 *   - EXCLUDES all workspace packages (moduleAliases).
 *
 * WHY THIS IS CRITICAL:
 *   - Prevents Vite from pre-bundling internal packages.
 *   - Avoids stale builds and duplication issues.
 *   - Ensures live changes in monorepo are reflected immediately.
 *
 * ---------------------------------------------------------------------------
 * SUMMARY
 * ---------------------------------------------------------------------------
 * This config:
 * ✔ Supports monorepo architecture
 * ✔ Dynamically resolves internal packages
 * ✔ Prevents duplicate React issues
 * ✔ Enables smooth HMR across packages
 * ✔ Uses proxy to handle backend APIs
 * ✔ Maintains CRA compatibility via process.env
 *
 * ---------------------------------------------------------------------------
 */

import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import fs from "fs";
import { fileURLToPath } from "url";
import { apiPaths } from "./setupProxy";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);


export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const isProd = mode === "production";

  const proxyTarget = env.REACT_APP_PROXY_API || "https://niuatt.niua.in";
  const assetsTarget = env.REACT_APP_PROXY_ASSETS || proxyTarget;


  const packagesRoot = path.resolve(__dirname, "micro-ui-internals/packages");


  function getAliases() {
  const aliases = {};

  const rootPackageJsonPath = path.join(__dirname, "package.json");
  const rootPackageJson = JSON.parse(fs.readFileSync(rootPackageJsonPath, "utf-8"));
  const workspaces = rootPackageJson.workspaces || [];

  /**
   * Build a set of active workspace package names from the root package.json.
   * Only packages listed here will be aliased to local source.
   * Packages absent from this list resolve from node_modules (NPM).
   *
   * Example:
   *   "micro-ui-internals/packages/modules/ads" → "ads"
   *   "micro-ui-internals/packages/libraries"   → "libraries"
   */
  const activeWorkspaces = new Set(
    workspaces.map(ws => ws.split("/").pop())
  );

  /**
   * Registers a Vite alias for a package if and only if it is listed
   * in the root workspaces[]. This ensures:
   *   - In workspace  → Vite resolves from local src/ (live changes, HMR)
   *   - Not in workspace → No alias registered → Vite resolves from node_modules (NPM)
   *
   * Entry point priority: package.json "main" field → fallback to src/index.js
   */
  function register(pkgDir) {
    const pkgJsonPath = path.join(pkgDir, "package.json");
    if (!fs.existsSync(pkgJsonPath)) return;

    const { name, main } = JSON.parse(fs.readFileSync(pkgJsonPath, "utf-8"));
    if (!name) return;

    const packageName = pkgDir.split("/").pop();
    if (!activeWorkspaces.has(packageName)) return;

    const entry = main
      ? path.join(pkgDir, main)
      : path.join(pkgDir, "src", "index.js");

    if (fs.existsSync(entry)) {
      aliases[name] = entry;
    }
  }

  /**
   * Scan all feature modules under packages/modules/*.
   * Each subdirectory is treated as a potential workspace package.
   */
  const modulesDir = path.join(packagesRoot, "modules");
  if (fs.existsSync(modulesDir)) {
    fs.readdirSync(modulesDir)
      .map(pkg => path.join(modulesDir, pkg))
      .filter(pkgDir => fs.statSync(pkgDir).isDirectory())
      .forEach(register);
  }

  /**
   * Shared infrastructure packages — libraries and react-components.
   * These follow the exact same workspace rule as feature modules.
   * Add to workspaces[] to develop locally, remove to consume from NPM.
   */
  const sharedPackages = [
    path.join(packagesRoot, "libraries"),
    path.join(packagesRoot, "react-components"),
    path.join(packagesRoot, "css"),
  ];

  sharedPackages.filter(pkgDir => fs.existsSync(pkgDir)).forEach(register);

  return aliases;
}


  const moduleAliases = getAliases();

  const proxy = {};
  apiPaths.forEach((path) => {
    proxy[path] = { target: proxyTarget, changeOrigin: true };
  });
  proxy["/pb-egov-assets"] = { target: assetsTarget, changeOrigin: true };

  return {
    plugins: [
      react()
    ],

    root: __dirname,

    cacheDir: path.resolve(__dirname, "node_modules/.vite"),

    base: isProd ? "/upyog-ui/" : "/",

    define: {
      "process.env": JSON.stringify({
        ...env,
        NODE_ENV: mode,
      }),
    },

    resolve: {
      alias: moduleAliases,
      preserveSymlinks: true,
      dedupe: ["react", "react-dom", "@tanstack/react-query", "react-router-dom", "i18next", "react-i18next", "@nudmcdgnpm/digit-ui-react-components"],
    },

    esbuild: {
      loader: "jsx",
      include: /.*\.js$/,
      exclude: /node_modules/,
    },

    server: {
      port: 3000,
      proxy,
      fs: {
        allow: [".."],
      },
      watch: {
        usePolling: true,
        interval: 300,
        include: [
          path.resolve(__dirname, "micro-ui-internals/packages/**"),
          path.resolve(__dirname, "src/**"),
        ],
        awaitWriteFinish: {
          stabilityThreshold: 100,
          pollInterval: 100,
        },
      },
      hmr: true,
    },

    build: {
      outDir: "build",
      sourcemap: false,
      rollupOptions: {
        output: {
          manualChunks: {
            vendor: ["react", "react-dom", "react-router-dom"],
          },
        },
      },
    },


    optimizeDeps: {
      include: ["react", "react-dom", "react-router-dom", "@tanstack/react-query", "i18next", "react-i18next"],
      exclude: Object.keys(moduleAliases), // prevents double-bundling of local workspace packages
      esbuildOptions: {
        loader: { ".js": "jsx" },
      },
    },
  };
});
