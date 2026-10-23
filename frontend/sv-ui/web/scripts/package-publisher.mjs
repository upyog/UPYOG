/**
 * package-publisher.mjs
 *
 * Centralized NPM publish script for all UPYOG UI workspace modules.
 *
 * WHY THIS EXISTS:
 * Modules in this monorepo have "main": "src/Module.js" in their package.json
 * so that Vite can resolve them from source during local development and the
 * main app build. This must never change — it's what keeps yarn start fast.
 *
 * But NPM consumers downloading a published package expect compiled output
 * at "main": "index.js" (inside dist/). Publishing the module as-is would
 * ship raw JSX source to NPM, which breaks for any consumer without Vite.
 *
 * This script solves that by writing a separate publish-ready package.json
 * INSIDE dist/ before publishing. npm publish is then run against the dist/
 * folder as the package root — so NPM receives correct compiled output with
 * correct entry points, while the real package.json on disk is never touched.
 *
 * HOW IT WORKS:
 * 1. Reads workspaces[] from root package.json to discover all modules
 * 2. Filters to requested modules (if args provided)
 * 3. Skips modules already published at the current version (idempotent)
 * 4. Writes a clean publish manifest into dist/package.json
 * 5. Runs: npm publish dist/ --access public
 * 6. dist/package.json is a build artifact — not committed, not the real one
 *
 * USAGE:
 *   yarn packages:publish              → publish all modules (skips already-published)
 *   yarn packages:publish ads          → publish only ads
 *   yarn packages:publish ads chb pt   → publish multiple modules
 *
 * REQUIRES:
 *   dist/ must exist for each module — run yarn packages:build first
 *   npm login must be done before running this script
 */




import { execSync, spawnSync } from 'child_process';
import { readFileSync, writeFileSync, existsSync } from 'fs';
import { join } from 'path';

const ROOT = process.cwd();
const rootPkg = JSON.parse(readFileSync(join(ROOT, 'package.json'), 'utf8'));

// Short names passed as CLI args e.g. "ads", "chb"
const filterNames = process.argv.slice(2).filter(a => !a.startsWith('--'));

function resolveWorkspaces(workspaces) {
  return workspaces
    .map(ws => join(ROOT, ws))
    .filter(p => existsSync(p));
}

function matchesFilter(pkg, path) {
  if (filterNames.length === 0) return true;

  return filterNames.some(name => {
    const lowerName = name.toLowerCase();
    return (
      pkg.name.toLowerCase().endsWith(`-${lowerName}`) ||
      pkg.name.toLowerCase().endsWith(`/${lowerName}`) ||
      path.toLowerCase().endsWith(`/${lowerName}`)
    );
  });
}


/**
 * Checks NPM registry to see if this exact name@version is already published.
 * Returns true if already exists — we skip publishing to avoid 403 errors.
 * This makes the publish command safe to re-run at any time.
 */
function isPublished(name, version) {
  try {
    execSync(`npm view ${name}@${version} version`, { stdio: 'pipe' });
    return true;
  } catch {
    return false;
  }
}

/**
 * Removes internal build tools from the dependencies list before publishing.
 * Consumers don't need microbundle, vite, rollup etc. in their node_modules.
 */
function buildStrippedDeps(pkg) {
  const buildTools = new Set([
    'microbundle', 'microbundle-crl', 'vite', '@vitejs/plugin-react',
    'rollup', 'esbuild', 'webpack', 'babel', '@babel/core',
  ]);
  return Object.fromEntries(
    Object.entries(pkg.dependencies || {}).filter(
      ([name]) => !buildTools.has(name) && !name.startsWith('@babel/')
    )
  );
}

/**
 * Writes a publish-ready package.json into dist/.
 *
 * This is the key function that makes the whole pipeline work without
 * modifying the real package.json. The dist/package.json that gets
 * published to NPM has:
 *   - "main": "index.js"          (relative to dist/, points to compiled CJS)
 *   - "module": "index.modern.js" (relative to dist/, points to compiled ESM)
 *   - No "source" field           (that's internal only, consumers don't need it)
 *   - No build tool dependencies  (stripped out, consumers don't need them)
 *
 * The real package.json at the module root remains:
 *   - "main": "src/Module.js"     (Vite workspace resolution, never changes)
 */
function writePublishManifest(pkgPath, pkg) {
  const publishManifest = {
    name: pkg.name,
    version: pkg.version,
    description: pkg.description || '',
    license: pkg.license || 'MIT',
    main: 'index.js',
    module: 'index.modern.js',
    peerDependencies: pkg.peerDependencies || {},
    dependencies: buildStrippedDeps(pkg),
    ...(pkg.keywords && { keywords: pkg.keywords }),
    ...(pkg.repository && { repository: pkg.repository }),
    ...(pkg.author && { author: pkg.author }),
  };

  writeFileSync(
    join(pkgPath, 'dist', 'package.json'),
    JSON.stringify(publishManifest, null, 2)
  );
}

/**
 * Publishes the dist/ folder as the package root.
 * "npm publish <directory>" treats that directory as the package —
 * so dist/package.json becomes the published package.json on NPM.
 */
function publishFromDist(pkgPath) {
  const result = spawnSync('npm', ['publish', join(pkgPath, 'dist'), '--access', 'public'], {
    cwd: ROOT,
    stdio: 'inherit',
  });
  return result.status === 0;
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

  if (filterNames.length > 0) {
    packages = packages.filter(({ path, pkg }) => matchesFilter(pkg, path));
    if (packages.length === 0) {
      console.error(`❌ No packages found matching: ${filterNames.join(', ')}`);
      process.exit(1);
    }
    console.log(`\n🎯 Matched: ${packages.map(p => p.pkg.name).join(', ')}`);
  }

  let published = 0, skipped = 0, failed = 0;

  for (const { path, pkg } of packages) {
    if (pkg.private) {
      console.log(`⏭️  Skipping private: ${pkg.name}`);
      skipped++;
      continue;
    }

    if (!existsSync(join(path, 'dist'))) {
      console.warn(`⚠️  No dist/ for ${pkg.name} — run packages:build ${filterNames[0] || ''} first`);
      skipped++;
      continue;
    }

    if (isPublished(pkg.name, pkg.version)) {
      console.log(`⏭️  Already published: ${pkg.name}@${pkg.version}`);
      skipped++;
      continue;
    }

    console.log(`\n📤 Publishing ${pkg.name}@${pkg.version}...`);
    writePublishManifest(path, pkg);

    const ok = publishFromDist(path);
    if (ok) {
      console.log(`✅ Published ${pkg.name}@${pkg.version}`);
      published++;
    } else {
      console.error(`❌ Failed: ${pkg.name}`);
      failed++;
    }
  }

  console.log(`\n📊 Done — ${published} published, ${skipped} skipped, ${failed} failed`);
  if (failed > 0) process.exit(1);
}

main().catch(err => {
  console.error(err);
  process.exit(1);
});