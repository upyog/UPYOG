/**
 * package-builder.mjs
 *
 * Centralized Vite library build script for all UPYOG UI workspace modules.
 *
 * WHY THIS EXISTS:
 * After migrating from CRA/Webpack to Vite, individual module build scripts
 * (microbundle-crl) were removed from every package.json because Vite resolves
 * workspace packages directly from src/ — no dist/ needed locally.
 * However, when publishing to NPM, consumers need compiled dist/ output.
 * This script handles that build centrally without touching any module's package.json.
 *
 * HOW IT WORKS:
 * - Reads workspaces[] from root package.json to discover all modules
 * - Sorts packages in topological order (dependencies build before dependents)
 * - Builds each module using Vite lib mode, outputting CJS + ESM formats to dist/
 * - Treats .js files as JSX (required since modules use JSX inside .js files)
 * - Externalizes all peerDependencies and internal @upyog/* @nudmcdgnpm/* packages
 * - Never modifies any module's package.json
 *
 * USAGE:
 *   yarn packages:build              → build all modules
 *   yarn packages:build ads          → build only ads module
 *   yarn packages:build ads chb pt   → build multiple modules
 */


import { build } from 'vite';
import react from '@vitejs/plugin-react';
import { readFileSync, existsSync } from 'fs';
import { join } from 'path';

const ROOT = process.cwd();
const rootPkg = JSON.parse(readFileSync(join(ROOT, 'package.json'), 'utf8'));

// Grab short names passed as args: yarn packages:build ads chb pt
// Filters out anything starting with -- 
const filterNames = process.argv.slice(2).filter(a => !a.startsWith('--'));


/**
 * Resolves workspace glob entries from root package.json into absolute paths.
 * Filters out paths that don't exist on disk.
 */
function resolveWorkspaces(workspaces) {
  return workspaces
    .map(ws => join(ROOT, ws))
    .filter(p => existsSync(p));
}

/**
 * Returns true if a package matches any of the short names provided as CLI args.
 * Matches against the end of the package name or the end of the folder path.
 * Example: "ads" matches "@upyog/upyog-ui-module-ads" and path ending in "/ads"
 */
function matchesFilter(pkg, path) {
  if (filterNames.length === 0) return true; // no filter = build all

  return filterNames.some(name => {
    // Match by short name: "ads" matches "@upyog/upyog-ui-module-ads" or path ending in /ads
    const lowerName = name.toLowerCase();
    return (
      pkg.name.toLowerCase().endsWith(`-${lowerName}`) ||
      pkg.name.toLowerCase().endsWith(`/${lowerName}`) ||
      path.toLowerCase().endsWith(`/${lowerName}`)
    );
  });
}


/**
 * Sorts packages so that dependencies always build before the packages that use them.
 * Example: digit-ui-libraries builds before digit-ui-react-components,
 * which builds before any feature module like ads or chb.
 */
function topoSort(packages) {
  const visited = new Set();
  const result = [];

  function visit(name) {
    if (visited.has(name)) return;
    visited.add(name);
    const entry = packages.find(p => p.pkg.name === name);
    if (!entry) return;
    const allDeps = {
      ...entry.pkg.dependencies,
      ...entry.pkg.peerDependencies,
    };
    for (const dep of Object.keys(allDeps)) {
      if (packages.some(p => p.pkg.name === dep)) visit(dep);
    }
    result.push(entry);
  }

  for (const { pkg } of packages) visit(pkg.name);
  return result;
}


/**
 * Builds a single package using Vite lib mode.
 * Outputs CJS (index.js) and ESM (index.modern.js) formats into dist/.
 *
 * Key behaviours:
 * - Entry point resolved from "source" field in package.json (e.g. src/Module.js)
 * - .js files treated as JSX because modules contain JSX in .js files
 * - All peerDependencies externalized — not bundled into output
 * - All @upyog/* and @nudmcdgnpm/* packages externalized (internal monorepo deps)
 */
async function buildPackage({ path, pkg }) {
  const entryField = pkg.source || 'src/index.js';
  const entryPath = join(path, entryField);

  if (!existsSync(entryPath)) {
    console.warn(`⚠️  Skipping ${pkg.name} — entry not found: ${entryPath}`);
    return;
  }

  console.log(`\n🔨 Building ${pkg.name}...`);

  await build({
    root: path,
    configFile: false,
    plugins: [
      react({
        include: ['**/*.js', '**/*.jsx', '**/*.ts', '**/*.tsx'],
      }),
    ],
    esbuild: {
      loader: 'jsx',
      include: /.*\.js$/,
      exclude: [],
    },
    optimizeDeps: {
      esbuildOptions: {
        loader: { '.js': 'jsx' },
      },
    },
    build: {
      lib: {
        entry: entryPath,
        formats: ['cjs', 'es'],
        fileName: (format) => format === 'cjs' ? 'index.js' : 'index.modern.js',
      },
      outDir: join(path, 'dist'),
      emptyOutDir: true,
      sourcemap: false,
      rollupOptions: {
        external: (id) => {
          const peers = Object.keys(pkg.peerDependencies || {});
          return (
            peers.some(p => id === p || id.startsWith(p + '/')) ||
            id.startsWith('@upyog/') ||
            id.startsWith('@nudmcdgnpm/')
          );
        },
      },
    },
    logLevel: 'warn',
  });

  console.log(`✅ ${pkg.name} built`);
}

async function main() {
  const paths = resolveWorkspaces(rootPkg.workspaces);

  let packages = paths
    .map(path => {
      const pkgFile = join(path, 'package.json');
      if (!existsSync(pkgFile)) return null;
      const pkg = JSON.parse(readFileSync(pkgFile, 'utf8'));
      return { path, pkg };
    })
    .filter(Boolean);

  // Apply short name filter
  if (filterNames.length > 0) {
    packages = packages.filter(({ path, pkg }) => matchesFilter(pkg, path));
    if (packages.length === 0) {
      console.error(`❌ No packages found matching: ${filterNames.join(', ')}`);
      console.error(`   Available modules: ${paths.map(p => p.split('/').pop()).join(', ')}`);
      process.exit(1);
    }
    console.log(`\n🎯 Matched: ${packages.map(p => p.pkg.name).join(', ')}`);
  }

  const sorted = topoSort(packages);
  console.log(`\n📦 Building ${sorted.length} package(s) in dependency order...\n`);

  for (const pkg of sorted) {
    await buildPackage(pkg);
  }

  console.log('\n🎉 Done!');
}

main().catch(err => {
  console.error(err);
  process.exit(1);
});