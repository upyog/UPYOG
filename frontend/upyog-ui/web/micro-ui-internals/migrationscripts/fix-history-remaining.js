#!/usr/bin/env node
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
const files = walk(path.join(root, "packages")).filter((f) => !f.includes("node_modules"));

function fix(content) {
  let c = content;

  // redirectWithHistory = history.push / history.replace (after first migrate left broken refs)
  c = c.replace(
    /let redirectWithHistory = history\.push;\s*if \(skipStep\) \{\s*redirectWithHistory = history\.replace;\s*\}/g,
    `let redirectWithHistory = (to, state) => navigate(to, state != null ? { state } : undefined);
    if (skipStep) {
      redirectWithHistory = (to, state) => navigate(to, state != null ? { replace: true, state } : { replace: true });
    }`
  );

  // history.replace(url-only) — template or string literal single argument
  c = c.replace(/history\.replace\(\s*(`[^`]*`|'[^']*'|"[^"]*")\s*\)/g, "navigate($1, { replace: true })");

  // history.replace(url, { state object }) — non-nested braces (common case)
  c = c.replace(/history\.replace\(\s*([^,()]+)\s*,\s*(\{(?:[^{}]|\{[^{}]*\})*\})\s*\)/g, "navigate($1, { replace: true, state: $2 })");

  // history.replace({ pathname, state }) — match balanced braces roughly
  c = c.replace(/history\.replace\(\s*\{\s*pathname:\s*([^,]+),\s*state:\s*(\{[\s\S]*?\})\s*\}\s*\)/g, (_, pathname, state) => {
    return `navigate(${pathname.trim()}, { replace: true, state: ${state} })`;
  });

  // history.location
  c = c.replace(/\bhistory\.location\b/g, "location");

  // goToNext = skipStep ? history.replace : history.push
  c = c.replace(
    /const goToNext = skipStep \? history\.replace : history\.push/g,
    "const goToNext = skipStep ? ((u, s) => navigate(u, s != null ? { replace: true, state: s } : { replace: true })) : navigate"
  );

  return c;
}

for (const file of files) {
  let s = fs.readFileSync(file, "utf8");
  if (!s.includes("history.")) continue;
  const next = fix(s);
  if (next !== s) {
    fs.writeFileSync(file, next);
    console.log("fixed", path.relative(root, file));
  }
}
