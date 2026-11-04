#!/usr/bin/env node
/**
 * Safe batch: useHistory + history.push/goBack/go (not replace — needs manual state/replace flags).
 */
const fs = require("fs");
const path = require("path");

function walk(dir, acc = []) {
  for (const name of fs.readdirSync(dir)) {
    const p = path.join(dir, name);
    const st = fs.statSync(p);
    if (st.isDirectory()) walk(p, acc);
    else if (name.endsWith(".js")) acc.push(p);
  }
  return acc;
}

const root = path.join(__dirname, "..");
const files = walk(path.join(root, "packages")).filter((f) => !f.includes("node_modules") && !f.includes("dist"));

for (const file of files) {
  let s = fs.readFileSync(file, "utf8");
  if (!s.includes("useHistory")) continue;
  if (file.includes("RouterCompat")) continue;

  let next = s;

  if (/useHistory/.test(next) && !/useNavigate/.test(next)) {
    next = next.replace(
      /import\s*\{([^}]*)\}\s*from\s*["']react-router-dom["']/g,
      (m, inner) => {
        if (!inner.includes("useHistory")) return m;
        const parts = inner
          .split(",")
          .map((x) => x.trim())
          .filter(Boolean);
        const filtered = parts.filter((p) => !/^useHistory$/.test(p.split(/\s+as\s+/)[0].trim()));
        if (!filtered.some((p) => p.includes("useNavigate"))) filtered.push("useNavigate");
        return `import { ${filtered.join(", ")} } from "react-router-dom"`;
      }
    );
  }

  next = next.replace(/\bconst\s+history\s*=\s*useHistory\s*\(\s*\)/g, "const navigate = useNavigate()");
  next = next.replace(/\blet\s+history\s*=\s*useHistory\s*\(\s*\)/g, "let navigate = useNavigate()");

  next = next.replace(/\bhistory\.push\(/g, "navigate(");
  next = next.replace(/\bhistory\.goBack\(\s*\)/g, "navigate(-1)");
  next = next.replace(/\bhistory\.goForward\(\s*\)/g, "navigate(1)");
  next = next.replace(/\bhistory\.go\((-?\d+)\)/g, "navigate($1)");

  if (next !== s) {
    fs.writeFileSync(file, next);
    console.log("updated", path.relative(root, file));
  }
}
