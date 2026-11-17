const fs = require("fs");
const path = require("path");
const parser = require("@babel/parser");
const traverse = require("@babel/traverse").default;
const generate = require("@babel/generator").default;
const t = require("@babel/types");

const moduleName = process.argv[2];
if (!moduleName) {
  console.error("Usage: node scripts/convert-module-inline-styles.js <moduleName>");
  process.exit(1);
}

const srcRoot = path.resolve(
  __dirname,
  `../web/micro-ui-internals/packages/modules/${moduleName}/src`
);
const cssFile = path.join(srcRoot, "css", `${moduleName}-inline-auto.css`);

if (!fs.existsSync(srcRoot)) {
  console.error(`Module src path not found: ${srcRoot}`);
  process.exit(1);
}

function walk(dir, out = []) {
  for (const entry of fs.readdirSync(dir, { withFileTypes: true })) {
    const full = path.join(dir, entry.name);
    if (entry.isDirectory()) walk(full, out);
    else if (/\.(js|jsx|ts|tsx)$/.test(entry.name)) out.push(full);
  }
  return out;
}

function toKebab(prop) {
  return prop.replace(/[A-Z]/g, (m) => `-${m.toLowerCase()}`);
}

function valueToCss(valueNode) {
  if (t.isStringLiteral(valueNode)) return valueNode.value;
  if (t.isNumericLiteral(valueNode)) return `${valueNode.value}px`;
  if (t.isBooleanLiteral(valueNode)) return valueNode.value ? "true" : "false";
  if (t.isNullLiteral(valueNode)) return "null";
  if (t.isUnaryExpression(valueNode) && valueNode.operator === "-" && t.isNumericLiteral(valueNode.argument)) {
    return `${-valueNode.argument.value}px`;
  }
  return null;
}

function objectExpressionToCss(obj) {
  const lines = [];
  for (const prop of obj.properties) {
    if (!t.isObjectProperty(prop) || prop.computed) return null;
    const key =
      t.isIdentifier(prop.key) ? prop.key.name : t.isStringLiteral(prop.key) ? prop.key.value : null;
    if (!key) return null;
    if (!t.isExpression(prop.value)) return null;
    const cssVal = valueToCss(prop.value);
    if (cssVal === null) return null;
    lines.push(`  ${toKebab(key)}: ${cssVal};`);
  }
  return lines;
}

function ensureCssImport(ast, relImportPath) {
  const body = ast.program.body;
  const hasImport = body.some((n) => t.isImportDeclaration(n) && n.source.value === relImportPath);
  if (hasImport) return;
  const importDecl = t.importDeclaration([], t.stringLiteral(relImportPath));
  let insertAt = 0;
  while (insertAt < body.length && t.isImportDeclaration(body[insertAt])) insertAt++;
  body.splice(insertAt, 0, importDecl);
}

const files = walk(srcRoot);
let globalIndex = 1;
const cssBlocks = [];
const touched = [];
const skipped = [];

for (const file of files) {
  const src = fs.readFileSync(file, "utf8");
  let ast;
  try {
    ast = parser.parse(src, {
      sourceType: "module",
      plugins: ["jsx", "classProperties", "optionalChaining", "nullishCoalescingOperator"],
    });
  } catch (e) {
    skipped.push({ file, reason: "parse-failed" });
    continue;
  }

  let changed = false;
  let localCount = 0;
  const localCss = [];

  traverse(ast, {
    JSXOpeningElement(pathEl) {
      const attrs = pathEl.node.attributes;
      const styleIndex = attrs.findIndex(
        (a) => t.isJSXAttribute(a) && t.isJSXIdentifier(a.name, { name: "style" })
      );
      if (styleIndex === -1) return;
      const styleAttr = attrs[styleIndex];
      if (!t.isJSXAttribute(styleAttr) || !t.isJSXExpressionContainer(styleAttr.value)) return;
      const expr = styleAttr.value.expression;
      if (!t.isObjectExpression(expr)) return;
      const cssLines = objectExpressionToCss(expr);
      if (!cssLines) return;

      const className = `${moduleName}-auto-${globalIndex++}`;
      localCount++;
      localCss.push(`.${className} {\n${cssLines.join("\n")}\n}`);

      attrs.splice(styleIndex, 1);
      const classIndex = attrs.findIndex(
        (a) => t.isJSXAttribute(a) && t.isJSXIdentifier(a.name, { name: "className" })
      );
      if (classIndex === -1) {
        attrs.push(t.jsxAttribute(t.jsxIdentifier("className"), t.stringLiteral(className)));
      } else {
        const classAttr = attrs[classIndex];
        if (!t.isJSXAttribute(classAttr)) return;
        if (t.isStringLiteral(classAttr.value)) {
          classAttr.value = t.stringLiteral(`${classAttr.value.value} ${className}`.trim());
        } else if (
          t.isJSXExpressionContainer(classAttr.value) &&
          t.isStringLiteral(classAttr.value.expression)
        ) {
          classAttr.value = t.jsxExpressionContainer(
            t.stringLiteral(`${classAttr.value.expression.value} ${className}`.trim())
          );
        } else {
          return;
        }
      }
      changed = true;
    },
  });

  if (!changed) continue;
  const relToSrc = path.relative(path.dirname(file), cssFile).replace(/\\/g, "/");
  const importPath = relToSrc.startsWith(".") ? relToSrc : `./${relToSrc}`;
  ensureCssImport(ast, importPath);
  const out = generate(ast, { jsescOption: { minimal: true } }, src).code;
  fs.writeFileSync(file, `${out}\n`, "utf8");
  cssBlocks.push(`/* ${path.relative(srcRoot, file)} */\n${localCss.join("\n\n")}\n`);
  touched.push({ file, converted: localCount });
}

if (cssBlocks.length) {
  const existing = fs.existsSync(cssFile) ? fs.readFileSync(cssFile, "utf8").trim() : "";
  const next = [existing, ...cssBlocks].filter(Boolean).join("\n\n");
  fs.mkdirSync(path.dirname(cssFile), { recursive: true });
  fs.writeFileSync(cssFile, `${next}\n`, "utf8");
}

console.log(
  JSON.stringify(
    {
      module: moduleName,
      touched: touched.length,
      converted: touched.reduce((a, b) => a + b.converted, 0),
      skipped: skipped.length,
    },
    null,
    2
  )
);
