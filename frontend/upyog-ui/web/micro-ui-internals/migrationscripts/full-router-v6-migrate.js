#!/usr/bin/env node
/**
 * Batch migrate remaining RR v5 patterns in .js files:
 * - Switch -> Routes (imports + JSX tags)
 * - Redirect -> Navigate (imports; JSX needs replace prop — applied separately)
 * - PrivateRoute path= component= -> Route path= element={<PrivateRoute>...</PrivateRoute>}
 *
 * Skips: RouterCompat.js, dss Switch UI component file, node_modules, dist
 */
const fs = require("fs");
const path = require("path");

const root = path.join(__dirname, "..", "packages");

function walk(dir, acc = []) {
  if (!fs.existsSync(dir)) return acc;
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const st = fs.statSync(p);
    if (st.isDirectory()) {
      if (name === "node_modules" || name === "dist") continue;
      walk(p, acc);
    } else if (name.endsWith(".js")) acc.push(p);
  }
  return acc;
}

/** Extract JSX expression for PrivateRoute component prop value (balanced braces). */
function extractComponentProp(line, startIdx) {
  const m = line.slice(startIdx).match(/component=\{/);
  if (!m) return null;
  let i = startIdx + line.slice(startIdx).indexOf("component={") + "component={".length;
  let depth = 1;
  const start = i;
  while (i < line.length && depth > 0) {
    const c = line[i];
    if (c === "{") depth++;
    else if (c === "}") depth--;
    i++;
  }
  if (depth !== 0) return null;
  return { full: line.slice(startIdx, i), inner: line.slice(start, i - 1) };
}

/** Convert component prop inner to element child JSX */
function componentInnerToElement(inner) {
  inner = inner.trim();
  // () => <Foo ... />
  const arrow = inner.match(/^\(\s*\)\s*=>\s*(.+)$/s);
  if (arrow) {
    return arrow[1].trim();
  }
  // (props) => <Foo ... />
  const arrowProps = inner.match(/^\(\s*props\s*\)\s*=>\s*(.+)$/s);
  if (arrowProps) {
    return arrowProps[1].trim().replace(/\{\.\.\.props\}/g, "");
  }
  // Identifier Component
  if (/^[A-Za-z_$][A-Za-z0-9_$]*$/.test(inner)) {
    return `<${inner} />`;
  }
  // Fallback: wrap as expression
  return inner;
}

function migrateFile(filePath) {
  let s = fs.readFileSync(filePath, "utf8");
  if (!s.includes("react-router-dom") && !s.includes("PrivateRoute")) return false;
  if (filePath.includes("RouterCompat.js")) return false;
  if (filePath.endsWith("dss/src/components/Switch.js")) return false;

  let next = s;
  const original = next;

  // Imports: add Route if Switch or PrivateRoute path+component pattern present
  const needsRoute =
    /\bSwitch\b/.test(next) ||
    (/<PrivateRoute\s+path=/.test(next) && /component=\{/.test(next));

  next = next.replace(
    /import\s*\{([^}]*)\}\s*from\s*["']react-router-dom["']/g,
    (full, inner) => {
      let parts = inner
        .split(",")
        .map((x) => x.trim())
        .filter(Boolean);
      const names = new Set(
        parts.map((p) => p.split(/\s+as\s+/)[0].trim())
      );
      if (names.has("Switch")) {
        parts = parts.filter((p) => !/^Switch\b/.test(p.split(/\s+as\s+/)[0].trim()));
        if (!names.has("Routes")) parts.push("Routes");
      }
      if (names.has("Redirect")) {
        parts = parts.filter((p) => !/^Redirect\b/.test(p.split(/\s+as\s+/)[0].trim()));
        if (!names.has("Navigate")) parts.push("Navigate");
      }
      if (needsRoute && !names.has("Route") && !parts.some((p) => p.includes("Route"))) {
        parts.push("Route");
      }
      return `import { ${parts.join(", ")} } from "react-router-dom"`;
    }
  );

  // JSX: Switch -> Routes
  next = next.replace(/<Switch\b/g, "<Routes");
  next = next.replace(/<\/Switch>/g, "</Routes>");

  // Redirect -> Navigate with replace (common default for Redirect)
  next = next.replace(/<Redirect\s+to=\{([^}]+)\}\s*\/>/g, "<Navigate to={$1} replace />");
  next = next.replace(/<Redirect\s+to=\{([^}]+)\}\s*\/\s*>/g, "<Navigate to={$1} replace />");
  next = next.replace(
    /<Redirect\s+to=\{\s*\{\s*pathname:\s*([^,}]+),\s*state:\s*([^}]+)\s*\}\s*\}\s*\/>/g,
    "<Navigate to={{ pathname: $1, state: $2 }} replace />"
  );

  // PrivateRoute path= component= />  (single line)
  next = next.replace(
    /<PrivateRoute\s+path=\{(`\$\{path\}[^`]*`)\}\s+component=\{([A-Za-z_$][A-Za-z0-9_$]*)\}\s*\/>/g,
    "<Route path={$1} element={<PrivateRoute><$2 /></PrivateRoute>} />"
  );

  // Multi-line PrivateRoute — process line by line for component={...}
  const lines = next.split("\n");
  const out = [];
  for (let li = 0; li < lines.length; li++) {
    let line = lines[li];
    if (line.includes("<PrivateRoute") && line.includes("path=") && line.includes("component={")) {
      const pathMatch = line.match(/path=\{(`[^`]+`)\}/);
      const compExtract = extractComponentProp(line, 0);
      if (pathMatch && compExtract) {
        const pathExpr = pathMatch[1];
        const childJsx = componentInnerToElement(compExtract.inner);
        line = `<Route path={${pathExpr}} element={<PrivateRoute>${childJsx}</PrivateRoute>} />`;
      }
    }
    out.push(line);
  }
  next = out.join("\n");

  if (next !== original) {
    fs.writeFileSync(filePath, next);
    return true;
  }
  return false;
}

const files = walk(root);
let n = 0;
for (const f of files) {
  try {
    if (migrateFile(f)) {
      console.log("migrated", path.relative(root, f));
      n++;
    }
  } catch (e) {
    console.error("FAIL", f, e.message);
  }
}
console.log("done,", n, "files");
