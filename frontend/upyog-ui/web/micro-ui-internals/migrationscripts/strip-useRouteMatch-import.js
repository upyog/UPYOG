/**
 * Removes useRouteMatch from react-router-dom import lines in .js files under packages/modules.
 */
const fs = require("fs");
const path = require("path");

const root = path.join(__dirname, "../packages/modules");

function walk(dir, files = []) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    if (fs.statSync(p).isDirectory()) walk(p, files);
    else if (name.endsWith(".js")) files.push(p);
  }
  return files;
}

function fixImportLine(line) {
  const trimmed = line.trim();
  const m = trimmed.match(/^import\s*\{([^}]+)\}\s*from\s*["']react-router-dom["'];?$/);
  if (!m) return line;
  const parts = m[1]
    .split(",")
    .map((s) => s.trim())
    .filter(Boolean)
    .filter((p) => p !== "useRouteMatch");
  if (parts.length === 0) return "";
  return line.replace(/\{[^}]+\}/, `{ ${parts.join(", ")} }`);
}

const files = walk(root);
let changed = 0;
for (const file of files) {
  let raw = fs.readFileSync(file, "utf8");
  if (!raw.includes("useRouteMatch")) continue;
  const lines = raw.split("\n");
  const out = lines.map((line) => {
    if (line.includes("react-router-dom") && line.includes("useRouteMatch") && line.trim().startsWith("import")) {
      const fixed = fixImportLine(line);
      return fixed === "" ? null : fixed;
    }
    return line;
  });
  const next = out.filter((l) => l !== null).join("\n");
  if (next !== raw) {
    fs.writeFileSync(file, next);
    changed++;
  }
}
console.log("Updated files:", changed);
