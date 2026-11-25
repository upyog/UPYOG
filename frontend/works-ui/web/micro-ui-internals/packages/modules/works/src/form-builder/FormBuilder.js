import React, { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from "react";
import "./formBuilder.css";
import * as E from "./formEngine";
import { useOptions } from "./useOption";

const Ctx = createContext(null);
const useCtx = () => useContext(Ctx);

const formatFileSize = (bytes) => {
    if (!bytes && bytes !== 0) return "";
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
};

/* ───────── accordion panel (used for sections AND repeat items) ───────── */
function Panel({ id, title, open, onToggle, errorCount = 0, order, action, footer, children }) {
    const { tr } = useCtx();
    return (
        <section
            id={id}
            className={`fb-panel ${order ? "fb-panel--section" : "fb-panel--item"} ${open ? "is-open" : ""} ${errorCount ? "has-error" : ""}`}
        >
            <div className="fb-panel__bar">
                <button type="button" className="fb-panel__trigger" aria-expanded={open} onClick={onToggle}>
                    {order && <span className="fb-order" aria-hidden="true">{order}</span>}
                    <span className="fb-panel__title">{title}</span>
                    {errorCount > 0 && (
                        <span className="fb-badge" aria-label={tr("FB_ERRORS_COUNT", { count: errorCount })}>
                            {errorCount} {errorCount === 1 ? "error" : "errors"}
                        </span>
                    )}
                    <span className={`fb-chevron ${open ? "is-open" : ""}`} aria-hidden="true" />
                </button>
                {action && <div className="fb-panel__actions">{action}</div>}
            </div>
            {/* hidden, not unmounted, so collapsing never loses data */}
            <div className="fb-panel__body" hidden={!open}>
                {children}
                {footer && <div className="fb-panel__footer">{footer}</div>}
            </div>
        </section>
    );
}

/* ───────── layout ───────── */
function FieldGrid({ fields, scope, columns = 2 }) {
    const { values } = useCtx();
    return (
        <div className="fb-grid" style={{ "--fb-cols": columns }}>
            {fields.map((f) => E.isVisible(f, values, scope) && <Field key={f.name} field={f} siblings={fields} scope={scope} />)}
        </div>
    );
}

/* ───────── repeat: Add / Remove, nestable ───────── */
function Repeat({ field, scope }) {
    const c = useCtx();
    const path = E.join(scope, field.name);
    const items = E.getIn(c.values, path) || [];
    const canAdd = !c.readOnly && (!field.max || items.length < field.max);
    const canRemove = !c.readOnly && items.length > (field.min || 0);
    const error = c.errors[path];
    const itemLabel = c.tr(field.itemLabel || "FB_ITEM");
    const addText = field.addLabel ? c.tr(field.addLabel) : c.tr("FB_ADD", { item: itemLabel });
    const isNested = Boolean(scope);

    return (
        <div className={`fb-repeat ${isNested ? "fb-repeat--nested" : "fb-repeat--root"}`}>
            <div className="fb-repeat__header">
                <div className="fb-repeat__meta">
                    <span className="fb-repeat__title">{itemLabel}s</span>
                    <span className="fb-repeat__count">{items.length}{field.max ? ` / ${field.max}` : ""}</span>
                </div>
                {canAdd && (
                    <button type="button" className="fb-btn fb-btn--secondary fb-btn--sm" onClick={() => c.addItem(path, field)}>
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                            <line x1="12" y1="5" x2="12" y2="19" />
                            <line x1="5" y1="12" x2="19" y2="12" />
                        </svg>
                        <span>{addText}</span>
                    </button>
                )}
            </div>

            {items.map((item, i) => {
                const itemPath = `${path}.${i}`;
                return (
                    <Panel
                        key={i}
                        title={E.itemTitle(field, c.values, itemPath, i, c.tr)}
                        open={!c.collapsed.has(itemPath)}
                        onToggle={() => c.toggleItem(itemPath)}
                        errorCount={c.countErrors(itemPath)}
                        action={
                            canRemove && (
                                <button
                                    type="button"
                                    className="fb-link-danger"
                                    onClick={() => c.removeItem(path, i, field, item)}
                                    title={c.tr("FB_REMOVE")}
                                >
                                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                                        <polyline points="3 6 5 6 21 6" />
                                        <path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6" />
                                        <path d="M10 11v6" />
                                        <path d="M14 11v6" />
                                    </svg>
                                    <span>{c.tr("FB_REMOVE")}</span>
                                </button>
                            )
                        }
                    >
                        <FieldGrid fields={field.fields} scope={itemPath} columns={field.columns} />
                    </Panel>
                );
            })}

            {error && <p className="fb-error" role="alert" data-error="true" tabIndex={-1}>{error}</p>}

            {canAdd && items.length > 0 && (
                <button type="button" className="fb-btn fb-btn--dashed" onClick={() => c.addItem(path, field)}>
                    <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                        <line x1="12" y1="5" x2="12" y2="19" />
                        <line x1="5" y1="12" x2="19" y2="12" />
                    </svg>
                    <span>{addText}</span>
                </button>
            )}
        </div>
    );
}

/* ───────── one field = label + control + error/warning ───────── */
const NO_FOR = ["radio", "dateRange", "fileList", "computed", "summaryTable"];

function Field({ field, siblings, scope }) {
    const c = useCtx();
    if (field.type === "repeat") return <Repeat field={field} scope={scope} />;

    const path = E.join(scope, field.name);
    const id = `fb-${path.replace(/\./g, "-")}`;
    const error = c.errors[path];
    const warning = c.warnings[path];
    const required = E.isRequired(field, c.values, scope);
    const label = field.label ? c.tr(field.label) : "";

    // Changing a field clears every dropdown that depends on it (State → District → Ward)
    const onChange = (v) =>
        c.setMany([[path, v], ...[...E.dependents(siblings, field.name)].map((n) => [E.join(scope, n), ""])]);

    const fieldClasses = [
        "fb-field",
        `fb-field--type-${field.type}`,
        field.span ? "fb-field--span" : "",
        error ? "has-error" : "",
    ]
        .filter(Boolean)
        .join(" ");

    return (
        <div
            className={fieldClasses}
            data-error={error ? "true" : undefined}
            style={field.span ? { "--fb-span": field.span } : undefined}
        >
            {field.type !== "checkbox" && label &&
                (NO_FOR.includes(field.type) ? (
                    <span className="fb-label">{label}{required && <span className="fb-req"> *</span>}</span>
                ) : (
                    <label className="fb-label" htmlFor={id}>{label}{required && <span className="fb-req"> *</span>}</label>
                ))}
            <Control
                field={field} label={label} id={id} path={path} scope={scope}
                value={E.getIn(c.values, path)} onChange={onChange}
                disabled={c.readOnly || !!field.readOnly} invalid={!!error}
                required={required}
            />
            {error && <p className="fb-error" role="alert">{error}</p>}
            {!error && warning && <p className="fb-warning">{warning}</p>}
        </div>
    );
}

/* ───────── controls ───────── */
const toFile = (f) => ({ name: f.name, size: f.size, type: f.type, uploadedOn: E.todayInIndia(), file: f });

const uploadToFilestore = async (rawFile, field) => {
    if (field?.maxSizeMB && rawFile.size > field.maxSizeMB * 1024 * 1024) {
        throw new Error(`File "${rawFile.name}" exceeds the ${field.maxSizeMB} MB limit.`);
    }
    const tenantId =
        window?.Digit?.ULBService?.getCurrentTenantId?.() ||
        window?.Digit?.ULBService?.getStateId?.() ||
        "pg";
    const res = await window.Digit.UploadServices.Filestorage("WORKS", rawFile, tenantId);
    const fileStoreId = res?.data?.files?.[0]?.fileStoreId;
    if (!fileStoreId) {
        throw new Error(`Failed to upload "${rawFile.name}". Please try again.`);
    }
    return { ...toFile(rawFile), fileStoreId };
};

function FileControl({ field, base, value, onChange, disabled, types, tr }) {
    const [uploading, setUploading] = useState(false);
    const [uploadError, setUploadError] = useState(null);

    const handleFileChange = async (e) => {
        const f = e.target.files?.[0];
        e.target.value = "";
        if (!f) return;
        setUploadError(null);
        setUploading(true);
        try {
            const uploaded = await uploadToFilestore(f, field);
            onChange(uploaded);
        } catch (err) {
            setUploadError(
                err?.response?.data?.Errors?.[0]?.message ||
                    err?.message ||
                    "File upload failed. Please try again."
            );
        } finally {
            setUploading(false);
        }
    };

    if (value) {
        return (
            <div className="fb-file-chip">
                <div className="fb-file-chip__info">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                        <polyline points="14 2 14 8 20 8" />
                    </svg>
                    <span className="fb-file-chip__name">{value.name}</span>
                    {value.size ? <span className="fb-file-chip__size">({formatFileSize(value.size)})</span> : null}
                </div>
                {!disabled && (
                    <button type="button" className="fb-link-danger" onClick={() => onChange(undefined)}>
                        {tr("FB_REMOVE")}
                    </button>
                )}
            </div>
        );
    }

    return (
        <div>
            <div className="fb-upload-box">
                <input
                    {...base}
                    disabled={disabled || uploading}
                    className="fb-upload-input"
                    type="file"
                    accept={acceptOf(field)}
                    onChange={handleFileChange}
                />
                <p className="fb-hint">
                    {uploading ? "Uploading file..." : tr("FB_FILE_HINT", { types, size: field.maxSizeMB })}
                </p>
            </div>
            {uploadError && <p className="fb-error" role="alert">{uploadError}</p>}
        </div>
    );
}

function FileListControl({ field, base, value, onChange, disabled, tr }) {
    const list = value || [];
    const [uploading, setUploading] = useState(false);
    const [uploadError, setUploadError] = useState(null);

    const handleFilesChange = async (e) => {
        const files = Array.from(e.target.files || []);
        e.target.value = "";
        if (files.length === 0) return;

        if (field.maxFiles && list.length + files.length > field.maxFiles) {
            setUploadError(`You can upload a maximum of ${field.maxFiles} files.`);
            return;
        }

        setUploadError(null);
        setUploading(true);
        try {
            const uploadedFiles = await Promise.all(files.map((f) => uploadToFilestore(f, field)));
            onChange([...list, ...uploadedFiles]);
        } catch (err) {
            setUploadError(
                err?.response?.data?.Errors?.[0]?.message ||
                    err?.message ||
                    "File upload failed. Please try again."
            );
        } finally {
            setUploading(false);
        }
    };

    return (
        <div className="fb-filelist">
            <div className="fb-upload-box">
                <input
                    {...base}
                    disabled={disabled || uploading}
                    className="fb-upload-input"
                    type="file"
                    multiple
                    accept={acceptOf(field)}
                    onChange={handleFilesChange}
                />
                <p className="fb-hint">
                    {uploading
                        ? "Uploading file(s)..."
                        : tr("FB_FILES_COUNT", { count: list.length, max: field.maxFiles, size: field.maxSizeMB })}
                </p>
            </div>
            {uploadError && <p className="fb-error" role="alert">{uploadError}</p>}
            {list.length > 0 && (
                <div className="fb-table-wrap">
                    <table className="fb-table">
                        <thead>
                            <tr>
                                <th>{tr("FB_COL_NAME")}</th>
                                <th>{tr("FB_COL_TYPE")}</th>
                                <th>{tr("FB_COL_UPLOADED")}</th>
                                <th className="fb-table__actions-col" />
                            </tr>
                        </thead>
                        <tbody>
                            {list.map((f, i) => (
                                <tr key={f.fileStoreId || i}>
                                    <td>
                                        <span className="fb-table__filename">{f.name}</span>
                                        {f.size ? <span className="fb-file-chip__size"> ({formatFileSize(f.size)})</span> : null}
                                    </td>
                                    <td><span className="fb-type-badge">{(f.type || "PDF").split("/").pop().toUpperCase()}</span></td>
                                    <td>{f.uploadedOn}</td>
                                    <td className="fb-table__actions-col">
                                        {!disabled && (
                                            <button type="button" className="fb-link-danger" onClick={() => onChange(list.filter((_, k) => k !== i))}>
                                                {tr("FB_REMOVE")}
                                            </button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}
const acceptOf = (f) => (f.fileTypes || []).map((t) => `.${t}`).join(",");

function Choice({ field, id, value, onChange, disabled, scope }) {
    const { values, loadOptions, tr } = useCtx();
    const { options, loading, waiting } = useOptions(field, values, scope, loadOptions);
    const isStatic = Array.isArray(field.options);
    const prefix = field.options?.labelPrefix;

    // Static options: label is a key. Master options: translate <labelPrefix>_<code> if it exists, else use the master's name.
    const text = (o) => {
        if (isStatic) return tr(o.label);
        if (prefix) {
            const k = `${prefix}_${o.value}`;
            const r = tr(k);
            if (r !== k) return r;
        }
        return o.label;
    };

    if (field.type === "radio")
        return (
            <div className="fb-radios" role="radiogroup">
                {options.map((o) => {
                    const checked = value === o.value;
                    return (
                        <label key={o.value} className={`fb-radio-pill ${checked ? "is-checked" : ""} ${disabled ? "is-disabled" : ""}`}>
                            <input
                                type="radio"
                                name={id}
                                disabled={disabled}
                                checked={checked}
                                onChange={() => onChange(o.value)}
                            />
                            <span className="fb-radio-pill__dot" aria-hidden="true" />
                            <span className="fb-radio-pill__text">{text(o)}</span>
                        </label>
                    );
                })}
            </div>
        );
    return (
        <div className="fb-select-wrap">
            <select id={id} className="fb-input fb-select" value={value ?? ""} disabled={disabled || waiting} onChange={(e) => onChange(e.target.value)}>
                <option value="">{loading ? tr("FB_LOADING") : waiting ? tr("FB_SELECT_PARENT") : tr("FB_SELECT")}</option>
                {options.map((o) => (
                    <option key={o.value} value={o.value}>{text(o)}</option>
                ))}
            </select>
        </div>
    );
}

function Control(p) {
    const { tr } = useCtx();
    const { field, id, value, onChange, disabled, invalid } = p;
    const base = { id, disabled, className: "fb-input", "aria-invalid": invalid || undefined };
    const types = (field.fileTypes || []).join(", ").toUpperCase();

    switch (field.type) {
        case "text":
            return <input {...base} type="text" maxLength={field.maxLength} value={value ?? ""} onChange={(e) => onChange(e.target.value)} />;
        case "integer":
            return <input {...base} type="text" inputMode="numeric" value={value ?? ""} onChange={(e) => /^\d*$/.test(e.target.value) && onChange(e.target.value)} />;
        case "amount":
            return (
                <div className="fb-amount-wrap">
                    <span className="fb-amount-prefix" aria-hidden="true">₹</span>
                    <input {...base} className="fb-input fb-input--amount" type="text" inputMode="decimal" placeholder="0.00" value={value ?? ""} onChange={(e) => /^\d*(\.\d{0,2})?$/.test(e.target.value) && onChange(e.target.value)} />
                </div>
            );
        case "select":
        case "radio":
            return <Choice {...p} />;
        case "checkbox":
            return (
                <label className={`fb-checkbox-card ${value ? "is-checked" : ""} ${disabled ? "is-disabled" : ""}`} htmlFor={id}>
                    <input id={id} type="checkbox" disabled={disabled} checked={!!value} onChange={(e) => onChange(e.target.checked)} />
                    <span className="fb-checkbox-card__text">
                        {tr(field.label)}
                        {(field.mustBeTrue || p.required) && <span className="fb-req"> *</span>}
                    </span>
                </label>
            );
        case "date":
            return <input {...base} type="date" max={field.maxDate === "today" ? E.todayInIndia() : field.maxDate} value={value ?? ""} onChange={(e) => onChange(e.target.value)} />;
        case "dateRange":
            return (
                <div className="fb-date-range">
                    <div className="fb-date-range__item">
                        <span className="fb-date-range__sublabel">{tr("FB_FROM")}</span>
                        <input className="fb-input" type="date" aria-label={tr("FB_FROM")} disabled={disabled} value={value?.from ?? ""} onChange={(e) => onChange({ ...value, from: e.target.value })} />
                    </div>
                    <span className="fb-date-range__sep" aria-hidden="true">→</span>
                    <div className="fb-date-range__item">
                        <span className="fb-date-range__sublabel">{tr("FB_TO")}</span>
                        <input className="fb-input" type="date" aria-label={tr("FB_TO")} disabled={disabled} min={value?.from} value={value?.to ?? ""} onChange={(e) => onChange({ ...value, to: e.target.value })} />
                    </div>
                </div>
            );
        case "file":
            return <FileControl field={field} base={base} value={value} onChange={onChange} disabled={disabled} types={types} tr={tr} />;
        case "fileList":
            return <FileListControl field={field} base={base} value={value} onChange={onChange} disabled={disabled} tr={tr} />;
        case "computed":
            return <Computed field={field} />;
        case "summaryTable":
            return <SummaryTable field={field} />;
        default:
            return <p className="fb-error">{tr("FB_UNKNOWN_TYPE", { type: field.type })}</p>;
    }
}

function Computed({ field }) {
    const { computed } = useCtx();
    const p = computed[field.name] ?? 0;
    const isWarn = field.warnIfNotZero && p !== 0;
    return (
        <div className={`fb-computed-card ${isWarn ? "is-warn" : ""}`}>
            <div className={`fb-computed ${isWarn ? "is-warn" : ""}`}>
                {field.format === "currency" ? E.formatINR(p) : p}
            </div>
        </div>
    );
}

function SummaryTable({ field }) {
    const { clean, tr } = useCtx();
    const rows = E.collect(clean, field.rows).filter((r) => r && (r.componentName || r.componentAmount));
    const cell = (col, row) => {
        if (col.percentOf) {
            const base = E.toPaise(clean[col.percentOf]) || 0;
            return base ? `${(((E.toPaise(row[col.field]) || 0) / base) * 100).toFixed(2)}%` : "–";
        }
        return col.format === "currency" ? E.formatINR(E.toPaise(row[col.field]) || 0) : row[col.field] || "—";
    };
    return (
        <div className="fb-table-wrap">
            <table className="fb-table">
                <thead>
                    <tr>
                        {field.columns.map((c) => (
                            <th key={c.label} className={c.format === "currency" || c.percentOf ? "is-num" : ""}>
                                {tr(c.label)}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {rows.length > 0 ? (
                        rows.map((r, i) => (
                            <tr key={i}>
                                {field.columns.map((c) => (
                                    <td key={c.label} className={c.format === "currency" || c.percentOf ? "is-num" : ""}>
                                        {cell(c, r)}
                                    </td>
                                ))}
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={field.columns.length} className="fb-table__empty">
                                No components added yet. Add project components above to see the financial break-up.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    );
}

/* ───────── the builder ───────── */
export default function FormBuilder({
    config,
    initialValues,
    loadOptions,
    validate,
    onSubmit,
    onCancel,
    onDirtyChange,
    readOnly = false,
    t = (k) => k, // your translate function, e.g. from useTranslation()
    messages = {}, // this form's key → English map, used as a fallback
}) {
    const tr = useMemo(() => E.makeTr(t, { ...E.FB_MESSAGES, ...messages }), [t, messages]);

    const [values, setValues] = useState(() => ({ ...E.initialValues(config), ...initialValues }));
    const [errors, setErrors] = useState({});
    const [warnings, setWarnings] = useState({});
    const [openSections, setOpenSections] = useState(() => new Set([config.sections[0].id]));
    const [collapsed, setCollapsed] = useState(() => new Set()); // collapsed repeat items (default: open)
    const [dirty, setDirty] = useState(false);
    const [busy, setBusy] = useState(false);
    const [formError, setFormError] = useState(null);
    const [focusTick, setFocusTick] = useState(0);
    const busyRef = useRef(false);
    const rootRef = useRef(null);

    const clean = useMemo(() => E.cleanValues(config, values), [config, values]);
    const computed = useMemo(() => E.computeAll(config, clean), [config, clean]);
    const sectionOf = useMemo(() => {
        const m = {};
        config.sections.forEach((s) => s.fields.forEach((f) => { m[f.name] = s.id; }));
        return m;
    }, [config]);

    const totalErrorCount = Object.keys(errors).length;

    /* state changes */
    const dropKeys = (paths) => (m) => {
        const n = { ...m };
        paths.forEach((p) => delete n[p]);
        return n;
    };

    const setMany = useCallback((pairs) => {
        setValues((v) => pairs.reduce((acc, [p, val]) => E.setIn(acc, p, val), v));
        const paths = pairs.map((p) => p[0]);
        setErrors(dropKeys(paths));
        setWarnings(dropKeys(paths));
        setDirty(true);
    }, []);

    const addItem = useCallback((path, field) => {
        setValues((v) => E.setIn(v, path, [...(E.getIn(v, path) || []), E.newItem(field)]));
        setDirty(true);
    }, []);

    const removeItem = useCallback(
        (path, index, field, item) => {
            const hasNested = field.fields.some((f) => f.type === "repeat" && (item[f.name] || []).length > 0);
            if (hasNested && !window.confirm(tr("FB_CONFIRM_REMOVE", { item: tr(field.itemLabel || "FB_ITEM") }))) return;
            setValues((v) => E.setIn(v, path, (E.getIn(v, path) || []).filter((_, i) => i !== index)));
            // item numbers shift after a removal, so drop messages and panel state under this list
            const under = (p) => p.startsWith(`${path}.`);
            const prune = (m) => Object.fromEntries(Object.entries(m).filter(([p]) => !under(p)));
            setErrors(prune);
            setWarnings(prune);
            setCollapsed((s) => new Set([...s].filter((p) => !under(p))));
            setDirty(true);
        },
        [tr]
    );

    const toggleItem = useCallback(
        (p) => setCollapsed((s) => { const n = new Set(s); n.has(p) ? n.delete(p) : n.add(p); return n; }),
        []
    );
    const toggleSection = (id) =>
        setOpenSections((s) => { const n = new Set(s); n.has(id) ? n.delete(id) : n.add(id); return n; });

    const jumpToSection = (id) => {
        setOpenSections((s) => new Set([...s, id]));
        requestAnimationFrame(() => {
            const el = rootRef.current?.querySelector(`#fb-sec-${id}`);
            el?.scrollIntoView?.({ behavior: "smooth", block: "start" });
        });
    };

    const countErrors = useCallback(
        (prefix) => Object.keys(errors).filter((p) => p.startsWith(`${prefix}.`)).length,
        [errors]
    );

    /* warn before closing or refreshing the tab with unsaved changes */
    useEffect(() => {
        onDirtyChange?.(dirty);
        if (!dirty) return undefined;
        const h = (e) => { e.preventDefault(); e.returnValue = ""; };
        window.addEventListener("beforeunload", h);
        return () => window.removeEventListener("beforeunload", h);
    }, [dirty]); // eslint-disable-line react-hooks/exhaustive-deps

    /* after a failed submit: focus the first invalid field (panels are already open by then) */
    useEffect(() => {
        if (!focusTick) return;
        const el = rootRef.current?.querySelector('[data-error="true"]');
        if (!el) return;
        const target = el.querySelector("input,select,textarea,button") || el;
        target.scrollIntoView?.({ block: "center", behavior: "smooth" });
        target.focus?.({ preventScroll: true });
    }, [focusTick]);

    /* submit */
    const reveal = (all) => {
        const paths = Object.keys(all);
        setOpenSections((s) => new Set([...s, ...paths.map((p) => sectionOf[E.topName(p)]).filter(Boolean)]));
        setCollapsed((s) => {
            const n = new Set(s);
            paths.forEach((p) => E.ancestorItems(p).forEach((a) => n.delete(a)));
            return n;
        });
        const orphans = paths.filter((p) => !sectionOf[E.topName(p)]); // errors from validate() that match no field
        if (orphans.length) setFormError(orphans.map((p) => all[p]).join(" "));
        setFocusTick((n) => n + 1);
    };

    const handleSubmit = async (e) => {
        e?.preventDefault();
        if (busyRef.current) return; // ignore double clicks
        busyRef.current = true;
        setBusy(true);
        setFormError(null);
        try {
            const payload = E.cleanValues(config, values);
            const fieldErrors = E.validateFields(config, values, tr); // 1. rules declared in the JSON

            let result;
            try {
                // 2. rules written in code (gets `t` for its messages)
                result = (await validate?.(payload, { hasFieldErrors: Object.keys(fieldErrors).length > 0, t: tr })) || {};
            } catch (err) {
                setFormError(tr("FB_ERR_VALIDATE_FAILED"));
                return;
            }

            const all = { ...result.errors, ...fieldErrors }; // field-level message wins on the same path
            setErrors(all);
            setWarnings(result.warnings || {});
            if (Object.keys(all).length) {
                reveal(all);
                return;
            }

            try {
                await onSubmit(payload);
                setDirty(false);
            } catch (err) {
                const apiErrors = err?.response?.data?.Errors;
                const apiError =
                    Array.isArray(apiErrors) && apiErrors.length > 0
                        ? apiErrors
                              .map((e) => e?.message || e?.description || e?.code)
                              .filter(Boolean)
                              .join(" | ")
                        : err?.message;
                setFormError(apiError || tr("FB_ERR_SUBMIT_FAILED"));
            }
        } finally {
            busyRef.current = false;
            setBusy(false);
        }
    };

    const ctx = {
        values, clean, computed, errors, warnings, collapsed, readOnly, loadOptions, tr,
        setMany, addItem, removeItem, toggleItem, countErrors,
    };

    return (
        <Ctx.Provider value={ctx}>
            <form className="fb" ref={rootRef} onSubmit={handleSubmit} noValidate>
                {/* Hero Header Card */}
                <div className="fb-hero">
                    <div className="fb-head">
                        <div className="fb-head__titles">
                            <h1 className="fb-title">{tr(config.title)}</h1>
                            <p className="fb-subtitle">
                                Complete all sections below. Fields marked with <span className="fb-req">*</span> are mandatory.
                            </p>
                        </div>
                        <div className="fb-head__actions">
                            <button
                                type="button"
                                className="fb-btn fb-btn--ghost"
                                onClick={() => setOpenSections(new Set(config.sections.map((s) => s.id)))}
                            >
                                {tr("FB_EXPAND_ALL")}
                            </button>
                            <button
                                type="button"
                                className="fb-btn fb-btn--ghost"
                                onClick={() => setOpenSections(new Set())}
                            >
                                {tr("FB_COLLAPSE_ALL")}
                            </button>
                        </div>
                    </div>

                    {/* Interactive Section Stepper / Quick Jump */}
                    <nav className="fb-stepper" aria-label="Form sections">
                        {config.sections.map((s, i) => {
                            const secErrors = Object.keys(errors).filter((p) => sectionOf[E.topName(p)] === s.id).length;
                            const isOpen = openSections.has(s.id);
                            return (
                                <button
                                    key={s.id}
                                    type="button"
                                    className={`fb-step ${isOpen ? "is-active" : ""} ${secErrors > 0 ? "has-error" : ""}`}
                                    onClick={() => jumpToSection(s.id)}
                                >
                                    <span className="fb-step__num">{i + 1}</span>
                                    <span className="fb-step__label">{tr(s.title)}</span>
                                    {secErrors > 0 && <span className="fb-step__err">{secErrors}</span>}
                                </button>
                            );
                        })}
                    </nav>
                </div>

                {/* Sections */}
                <div className="fb-sections">
                    {config.sections.map((s, i) => {
                        const secErrors = Object.keys(errors).filter((p) => sectionOf[E.topName(p)] === s.id).length;
                        const nextSec = config.sections[i + 1];
                        return (
                            <Panel
                                key={s.id}
                                id={`fb-sec-${s.id}`}
                                order={i + 1}
                                title={tr(s.title)}
                                open={openSections.has(s.id)}
                                onToggle={() => toggleSection(s.id)}
                                errorCount={secErrors}
                                footer={
                                    nextSec ? (
                                        <button
                                            type="button"
                                            className="fb-btn fb-btn--ghost fb-btn--sm"
                                            onClick={() => jumpToSection(nextSec.id)}
                                        >
                                            <span>Next: {tr(nextSec.title)}</span>
                                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round" aria-hidden="true">
                                                <polyline points="9 18 15 12 9 6" />
                                            </svg>
                                        </button>
                                    ) : null
                                }
                            >
                                <FieldGrid fields={s.fields} scope="" columns={s.columns} />
                            </Panel>
                        );
                    })}
                </div>

                {/* Sticky Action Bar */}
                {!readOnly && (
                    <div className="fb-footer">
                        <div className="fb-footer__status">
                            {formError && <p className="fb-error" role="alert">{formError}</p>}
                            {!formError && totalErrorCount > 0 && (
                                <p className="fb-error" role="alert">
                                    Please resolve {totalErrorCount} {totalErrorCount === 1 ? "validation error" : "validation errors"} highlighted above.
                                </p>
                            )}
                        </div>
                        <div className="fb-footer__actions">
                            {onCancel && (
                                <button type="button" className="fb-btn fb-btn--ghost" onClick={onCancel} disabled={busy}>
                                    Cancel
                                </button>
                            )}
                            <button type="submit" className="fb-btn fb-btn--primary" disabled={busy}>
                                {busy ? tr("FB_SUBMITTING") : tr(config.submitLabel || "FB_SUBMIT")}
                            </button>
                        </div>
                    </div>
                )}
            </form>
        </Ctx.Provider>
    );
}
