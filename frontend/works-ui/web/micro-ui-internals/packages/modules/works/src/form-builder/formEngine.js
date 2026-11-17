/* ───────── localisation ───────── */
export const FB_MESSAGES = {
    FB_SUBMIT: "Submit", FB_SUBMITTING: "Submitting…", FB_EXPAND_ALL: "Expand all", FB_COLLAPSE_ALL: "Collapse all",
    FB_REMOVE: "Remove", FB_ADD: "Add {item}", FB_ITEM: "Item", FB_YES: "Yes", FB_NO: "No",
    FB_SELECT: "Select", FB_LOADING: "Loading…", FB_SELECT_PARENT: "Select the previous field first",
    FB_FROM: "From", FB_TO: "to",
    FB_FILE_HINT: "{types} · max {size} MB", FB_FILES_COUNT: "{count}/{max} files · max {size} MB each",
    FB_COL_NAME: "Name", FB_COL_TYPE: "Type", FB_COL_UPLOADED: "Uploaded",
    FB_UNKNOWN_TYPE: "Unknown field type: {type}", FB_ERRORS_COUNT: "{count} errors",
    FB_CONFIRM_REMOVE: "Remove this {item} and everything inside it?",
    FB_UNSAVED: "You have unsaved changes. Leave this page?",
    FB_ERR_VALIDATE_FAILED: "Could not validate. Please try again.",
    FB_ERR_SUBMIT_FAILED: "Submission failed. Please try again.",
    FB_ERR_REQUIRED: "{label} is required", FB_ERR_CONFIRM: "This confirmation is required",
    FB_ERR_REPEAT_MIN: "Add at least {min} {item}", FB_ERR_REPEAT_MAX: "At most {max} {item} allowed",
    FB_ERR_INTEGER: "Enter a whole number", FB_ERR_AMOUNT: "Enter a valid amount (up to 2 decimals)",
    FB_ERR_PATTERN: "{label} is invalid", FB_ERR_MIN_LENGTH: "{label} must be at least {min} characters",
    FB_ERR_MIN: "{label} must be at least {min}", FB_ERR_MAX: "{label} must be at most {max}",
    FB_ERR_FUTURE_DATE: "{label} cannot be in the future", FB_ERR_MAX_DATE: "{label} must be on or before {date}",
    FB_ERR_FILE_TYPE: "{file}: only {types} files are allowed", FB_ERR_FILE_SIZE: "{file}: must be {size} MB or smaller",
    FB_ERR_MAX_FILES: "A maximum of {max} files is allowed",
};

/* t: your translate function. fallback: key → English, used when t returns the key unchanged. */
export const makeTr = (t = (k) => k, fallback = {}) => (key, params = {}) => {
    if (key == null || key === "") return "";
    let s = t(key);
    if ((!s || s === key) && fallback[key]) s = fallback[key];
    if (!s) s = key;
    // unknown {x} placeholders stay in place, so itemTitle can fill them later
    return String(s).replace(/\{(\w+)\}/g, (m, k) => (k in params ? params[k] : m));
};

/* ───────── paths ───────── */
export const join = (base, name) => (base === "" || base == null ? String(name) : `${base}.${name}`);
export const getIn = (obj, path) => String(path).split(".").reduce((o, k) => (o == null ? undefined : o[k]), obj);

export function setIn(obj, path, value) {
    const [head, ...rest] = String(path).split(".");
    const copy = Array.isArray(obj) ? [...obj] : { ...(obj || {}) };
    copy[head] = rest.length
        ? setIn(obj?.[head] ?? (/^\d+$/.test(rest[0]) ? [] : {}), rest.join("."), value)
        : value;
    return copy;
}

/* Look in the current repeat item first, then walk up through parent items, then the top level. */
export function lookup(values, scope, name) {
    let s = scope;
    while (s) {
        const v = getIn(values, `${s}.${name}`);
        if (v !== undefined) return v;
        s = s.split(".").slice(0, -2).join("."); // projects.0.components.1 → projects.0 → ""
    }
    return getIn(values, name);
}

/* ───────── conditions ───────── */
export function check(cond, values, scope) {
    if (!cond) return true;
    if (cond.all) return cond.all.every((c) => check(c, values, scope));
    if (cond.any) return cond.any.some((c) => check(c, values, scope));
    const v = lookup(values, scope, cond.field);
    if ("equals" in cond) return v === cond.equals;
    if ("notEquals" in cond) return v !== cond.notEquals;
    if ("in" in cond) return cond.in.includes(v);
    if ("truthy" in cond) return !!v === cond.truthy;
    return true;
}
export const isVisible = (f, values, scope) => check(f.visibleWhen, values, scope);
export const isRequired = (f, values, scope) =>
    !!f.required || (!!f.requiredWhen && check(f.requiredWhen, values, scope));

/* ───────── templates and dropdown dependencies ───────── */
export const fill = (tpl, values, scope, extra = {}) =>
    String(tpl).replace(/\{(\w+)\}/g, (_, k) => (k in extra ? extra[k] : lookup(values, scope, k) ?? ""));

/* Names of the fields a dropdown depends on: `dependsOn`, plus any {placeholder} in a source spec */
export const depsOf = (field) => {
    const spec = field.options && !Array.isArray(field.options) ? field.options : null;
    const fromSpec = spec ? [...JSON.stringify(spec).matchAll(/\{(\w+)\}/g)].map((m) => m[1]) : [];
    return [...new Set([...(field.dependsOn ? [field.dependsOn] : []), ...fromSpec])];
};

/* Sibling fields that depend (directly or through a chain) on `name`; cleared when it changes */
export function dependents(siblings, name, found = new Set()) {
    siblings.forEach((s) => {
        if (!found.has(s.name) && depsOf(s).includes(name)) {
            found.add(s.name);
            dependents(siblings, s.name, found);
        }
    });
    return found;
}

export function itemTitle(field, values, itemPath, i, tr) {
    const base = `${tr(field.itemLabel || "FB_ITEM")} ${i + 1}`;
    if (!field.itemTitle) return base;
    return fill(tr(field.itemTitle), values, itemPath, { n: i + 1 }).replace(/[\s:–-]+$/, "").trim() || base;
}

/* ───────── money (integer paise, so sums never drift) ───────── */
export const toPaise = (v) => {
    const s = String(v ?? "").trim();
    if (!/^\d+(\.\d{1,2})?$/.test(s)) return NaN;
    const [r, p = ""] = s.split(".");
    return Number(r) * 100 + Number(p.padEnd(2, "0"));
};
export const fromPaise = (p) =>
    (p / 100).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 });
export const formatINR = (p) => `₹${fromPaise(p)}`;
export const todayInIndia = () => new Intl.DateTimeFormat("en-CA", { timeZone: "Asia/Kolkata" }).format(new Date());
export const normalizeName = (s) => String(s ?? "").toLowerCase().replace(/[^a-z0-9]/g, "");

/* ───────── initial data, add / remove ───────── */
export function newItem(field) {
    const item = { ...(field.defaults || {}) };
    field.fields.forEach((f) => {
        if (f.type === "repeat") item[f.name] = Array.from({ length: f.min || 0 }, () => newItem(f));
        else if (f.default !== undefined) item[f.name] = f.default;
    });
    return item;
}
export function initialValues(config) {
    const out = {};
    config.sections.forEach((s) =>
        s.fields.forEach((f) => {
            if (f.type === "repeat") out[f.name] = Array.from({ length: f.min || 0 }, () => newItem(f));
            else if (f.default !== undefined) out[f.name] = f.default;
        })
    );
    return out;
}

/* ───────── clean payload: drop hidden and display-only fields ───────── */
const DISPLAY_ONLY = ["computed", "summaryTable"];

function cleanFields(fields, data, root, scope) {
    const out = {};
    fields.forEach((f) => {
        if (DISPLAY_ONLY.includes(f.type) || !isVisible(f, root, scope)) return;
        const v = data?.[f.name];
        if (f.type === "repeat") {
            out[f.name] = (v || []).map((item, i) => cleanFields(f.fields, item, root, `${join(scope, f.name)}.${i}`));
        } else if (v !== undefined) out[f.name] = v;
    });
    return out;
}
export const cleanValues = (config, values) =>
    cleanFields(config.sections.flatMap((s) => s.fields), values, values, "");

/* ───────── field-level validation (everything the JSON declares) ───────── */
const isEmpty = (f, v) =>
    f.type === "dateRange" ? !(v?.from && v?.to) : v == null || v === "" || (Array.isArray(v) && v.length === 0);

function checkFile(f, file, fail, tr) {
    const ext = (file.name.split(".").pop() || "").toLowerCase();
    if (f.fileTypes && !f.fileTypes.includes(ext))
        fail(tr("FB_ERR_FILE_TYPE", { file: file.name, types: f.fileTypes.join(", ").toUpperCase() }));
    if (f.maxSizeMB && file.size > f.maxSizeMB * 1024 * 1024)
        fail(tr("FB_ERR_FILE_SIZE", { file: file.name, size: f.maxSizeMB }));
}

function checkFields(fields, data, root, scope, errors, tr) {
    fields.forEach((f) => {
        if (DISPLAY_ONLY.includes(f.type) || !isVisible(f, root, scope)) return;
        const path = join(scope, f.name);
        const v = data?.[f.name];
        const label = tr(f.label || f.itemLabel || f.name);
        const item = tr(f.itemLabel || "FB_ITEM");
        const fail = (m) => {
            if (!(path in errors)) errors[path] = m;
        };

        if (f.type === "repeat") {
            const items = v || [];
            if (f.min && items.length < f.min) fail(tr("FB_ERR_REPEAT_MIN", { min: f.min, item }));
            if (f.max && items.length > f.max) fail(tr("FB_ERR_REPEAT_MAX", { max: f.max, item }));
            items.forEach((it, i) => checkFields(f.fields, it, root, `${path}.${i}`, errors, tr));
            return;
        }
        if (f.mustBeTrue) {
            if (v !== true) fail(tr("FB_ERR_CONFIRM"));
            return;
        }
        if (isEmpty(f, v)) {
            if (isRequired(f, root, scope)) fail(tr("FB_ERR_REQUIRED", { label }));
            return;
        }

        const s = String(v);
        const numeric = ["integer", "amount"].includes(f.type);
        if (f.type === "integer" && !/^\d+$/.test(s)) fail(tr("FB_ERR_INTEGER"));
        if (f.type === "amount" && Number.isNaN(toPaise(s))) fail(tr("FB_ERR_AMOUNT"));
        if (f.pattern && !new RegExp(f.pattern).test(s)) fail(f.patternMessage ? tr(f.patternMessage) : tr("FB_ERR_PATTERN", { label }));
        if (f.minLength && s.length < f.minLength) fail(tr("FB_ERR_MIN_LENGTH", { label, min: f.minLength }));
        if (f.min !== undefined && numeric && Number(s) < f.min) fail(tr("FB_ERR_MIN", { label, min: f.min }));
        if (f.max !== undefined && numeric && Number(s) > f.max) fail(tr("FB_ERR_MAX", { label, max: f.max }));
        if (f.maxDate) {
            const limit = f.maxDate === "today" ? todayInIndia() : f.maxDate;
            if (s > limit) fail(f.maxDate === "today" ? tr("FB_ERR_FUTURE_DATE", { label }) : tr("FB_ERR_MAX_DATE", { label, date: limit }));
        }
        if (f.type === "file") checkFile(f, v, fail, tr);
        if (f.type === "fileList") {
            if (f.maxFiles && v.length > f.maxFiles) fail(tr("FB_ERR_MAX_FILES", { max: f.maxFiles }));
            v.forEach((file) => checkFile(f, file, fail, tr));
        }
    });
}
export function validateFields(config, values, tr) {
    const errors = {};
    checkFields(config.sections.flatMap((s) => s.fields), values, values, "", errors, tr);
    return errors;
}

/* ───────── computed fields ───────── */
export function collect(values, expr) {
    // "projects[].components[].amt" → all amt values
    let nodes = [values];
    expr.split(".").forEach((tok) => {
        const arr = tok.endsWith("[]");
        const key = arr ? tok.slice(0, -2) : tok;
        nodes = nodes.flatMap((n) => {
            const v = n?.[key];
            return arr ? (Array.isArray(v) ? v : []) : [v];
        });
    });
    return nodes;
}
export function computeAll(config, clean) {
    const out = {};
    config.sections.forEach((s) =>
        s.fields.forEach((f) => {
            if (f.type !== "computed") return;
            if (f.sum) out[f.name] = collect(clean, f.sum).reduce((t, v) => t + (toPaise(v) || 0), 0);
            else if (f.diff) {
                const val = (k) => (k in out ? out[k] : toPaise(clean[k]) || 0);
                out[f.name] = val(f.diff[0]) - val(f.diff[1]);
            }
        })
    );
    return out;
}

/* ───────── error path helpers ───────── */
export const topName = (path) => path.split(".")[0];
export function ancestorItems(path) {
    // "projects.1.components.0.x" → ["projects.1", "projects.1.components.0"]
    const seg = path.split(".");
    return seg.reduce((out, s, i) => (/^\d+$/.test(s) ? [...out, seg.slice(0, i + 1).join(".")] : out), []);
}