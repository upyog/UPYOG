import React, { useEffect, useMemo, useState } from "react";
import {
  Card,
  CardHeader,
  CardSubHeader,
  StatusTable,
  Row,
  LinkButton,
  SubmitBar,
  CheckBox,
  EditIcon,
} from "@nudmcdgnpm/digit-ui-react-components";

/* =========================================================
   DynamicCheckPage  (lives in react-components)
   ---------------------------------------------------------
   Config-driven summary/check page. Renders sections + rows
   from the SAME routeConfig.form that DynamicForm renders
   inputs from, so form and summary can never drift.

   Field type → summary rendering:
   - sectionHeader  → new <CardSubHeader> section
   - date           → formatDate(value)
   - dropdown/radio → t(selected.i18nKey) (or code looked up in options)
   - file           → collected into one document-preview block
   - group          → children flattened into normal rows
   - everything else→ checkNA(value) (+ unit if declared)

   Per-field config extras it understands (all optional):
   - summaryLabel: "EST_DURATION_IN_YEARS"  → overrides t(fc.key) on this page only
   - hideInSummary: true                    → skip the field on the check page

   Value resolution order for each field `name`:
     formValues[name] → extraData[name]
   `extraData` surfaces display-only fields (excludeFromPayload)
   that live in router state instead of the saved record.

   Because this file lives in react-components, module-level
   utils are injected as props (with safe fallbacks):
   - formatDate(epoch)         (fallback: toLocaleDateString)
   - checkNA(value)            (fallback: value || "NA")
   - DocumentPreview           (e.g. ESTDocumnetPreview)
   ========================================================= */

const defaultCheckNA = (v) => (v === undefined || v === null || v === "" ? "NA" : v);
const defaultFormatDate = (v) => {
  if (!v) return "NA";
  const d = v instanceof Date ? v : new Date(Number(v) || v);
  return isNaN(d.getTime()) ? "NA" : d.toLocaleDateString("en-IN");
};

const sortByOrder = (fields = []) =>
  [...fields].sort((a, b) => (a.order ?? 0) - (b.order ?? 0));

const flattenForSummary = (formConfig = []) =>
  formConfig.reduce((acc, fc) => {
    if (fc.type === "group") return [...acc, ...(fc.children || [])];
    return [...acc, fc];
  }, []);

// const ActionButton = ({ jumpTo }) => {
//   const navigate = Digit.Hooks.useCustomNavigate();
//   return <LinkButton label={<EditIcon />} onClick={() => navigate(jumpTo)} />;
// };

const ActionButton = ({ jumpTo, editNavigationState }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  return (
    <LinkButton
      label={<EditIcon />}
      onClick={() =>
        navigate(jumpTo, {
          state: editNavigationState || window.history.state?.usr || {},
        })
      }
    />
  );
};


const DynamicCheckPage = ({
  routeConfig,             // same route entry DynamicForm receives (must contain .form)
  config,                  // route config with .key (e.g. "AssignAssetsData")
  value = {},              // persisted wizard data (params)
  extraData = {},          // fallback values for display-only / excludeFromPayload fields
  editRoute,               // where the edit pencil navigates
  editNavigationState,     // optional router state when edit is clicked (e.g. { editData })
  onSubmit,
  summaryHeaderCode,       // optional i18n code for the page header
  defaultSectionHeaderCode = "EST_ASSET_DETAILS", // header for fields before the first sectionHeader
  t = (k) => k,
  formatDate = defaultFormatDate,
  checkNA = defaultCheckNA,
  DocumentPreview,         // component: ({documents, ...}) => JSX (e.g. ESTDocumnetPreview)
  declarationCode = "EST_FINAL_DECLARATION_MESSAGE",
  submitLabelCode = "EST_COMMON_SUBMIT",
  isSubmitting = false,
}) => {
  const [agree, setAgree] = useState(false);
  const [previewDocs, setPreviewDocs] = useState([]);
  const [loadingDocs, setLoadingDocs] = useState(false);

  const payloadKey = routeConfig?.payloadKey || "Allotments";

  // Persisted data may be an array (DynamicForm saves [formVal]) or a plain object.
  const formValues = useMemo(() => {
    const saved = value?.[config?.key]?.[payloadKey];
    if (Array.isArray(saved)) return saved[0] || {};
    return saved || {};
  }, [value, config, payloadKey]);

  const sortedFields = useMemo(
    () => flattenForSummary(sortByOrder(routeConfig?.form || [])),
    [routeConfig]
  );

  // ── Split flat field list into sections; pull out file fields ──
  const { sections, fileFields } = useMemo(() => {
    const secs = [];
    const files = [];
    let current = { headerCode: null, fields: [] };

    sortedFields.forEach((fc) => {
      if (fc.hideInSummary) return;
      if (fc.type === "sectionHeader") {
        if (current.fields.length) secs.push(current);
        current = { headerCode: fc.label?.code || fc.key, fields: [] };
        return;
      }
      if (fc.field?.type === "file") {
        files.push(fc);
        return;
      }
      if (fc.field) current.fields.push(fc);
    });
    if (current.fields.length) secs.push(current);

    return { sections: secs, fileFields: files };
  }, [sortedFields]);

  // ── Resolve one field's display text ──
  const resolveValue = (fc) => {
    const { field, options = [] } = fc;
    const name = field.name;
    const formVal = formValues[name];
    const raw =
      formVal !== undefined && formVal !== null && formVal !== ""
        ? formVal
        : extraData[name];

    if (raw === undefined || raw === null || raw === "") return t("NA");

    switch (field.type) {
      case "date":
        return formatDate(raw);

      case "dropdown":
      case "radio": {
        if (typeof raw === "object") return t(raw.i18nKey || raw.code || "NA");
        const opt = options.find((o) => o.code === raw);
        return opt ? t(opt.i18nKey || opt.code) : checkNA(raw);
      }

      default: {
        const text = checkNA(raw);
        return field.unit ? `${text} ${field.unit}` : text;
      }
    }
  };

  // ── Document preview: fetch URLs for every uploaded file field ──
  useEffect(() => {
    let mounted = true;

    const filestoreIds = [];
    const idToLabelMap = {};

    fileFields.forEach((fc) => {
      const obj = formValues?.[fc.field.name];
      if (obj?.filestoreId) {
        filestoreIds.push(obj.filestoreId);
        idToLabelMap[obj.filestoreId] = t(fc.summaryLabel || fc.key);
      }
    });

    if (filestoreIds.length === 0) {
      if (mounted) setPreviewDocs([]);
      return () => { mounted = false; };
    }

    setLoadingDocs(true);
    Digit.UploadServices.Filefetch(filestoreIds, Digit.ULBService.getStateId())
      .then((res) => {
        if (!mounted) return;
        const arr = res?.data?.fileStoreIds;
        const values = [];
        if (Array.isArray(arr)) {
          arr.forEach((fsObj) => {
            const fsid = fsObj?.fileStoreId || fsObj?.id;
            const url = fsObj?.url?.split(",")[0];
            if (url) {
              values.push({ url, title: idToLabelMap[fsid] || t("DOCUMENT"), documentType: fsid });
            }
          });
        }
        const ordered = filestoreIds
          .map((id) => values.find((v) => v.documentType === id))
          .filter(Boolean);
        if (mounted) setPreviewDocs(ordered.length ? [{ values: ordered }] : []);
      })
      .catch((err) => {
        console.error("Error fetching files for preview:", err);
        if (mounted) setPreviewDocs([]);
      })
      .finally(() => mounted && setLoadingDocs(false));

    return () => { mounted = false; };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [formValues, fileFields, t]);

  return (
    <Card>
      <CardHeader>
        {t(summaryHeaderCode || routeConfig?.texts?.header || "EST_ASSIGN_ASSETS_SUMMARY")}
      </CardHeader>

      {sections.map((section, sIdx) => (
        <React.Fragment key={section.headerCode || `section-${sIdx}`}>
          <CardSubHeader>
            {t(section.headerCode || defaultSectionHeaderCode)}
          </CardSubHeader>
          <StatusTable>
            {section.fields.map((fc, fIdx) => (
              <Row
                key={fc.key}
                label={t(fc.summaryLabel || fc.key)}
                text={resolveValue(fc)}
                actionButton={
                  sIdx === 0 && fIdx === 0 && editRoute
                    ? <ActionButton jumpTo={editRoute} editNavigationState={editNavigationState} />
                    : undefined
                }
              />
            ))}
          </StatusTable>
        </React.Fragment>
      ))}

      {/* ----------------- DOCUMENT PREVIEW ----------------- */}
      {fileFields.length > 0 && (
        <>
          <CardSubHeader>{t("EST_DOCUMENT_PREVIEW")}</CardSubHeader>
          {loadingDocs ? (
            <div style={{ padding: "12px 16px" }}>{t("CS_LOADING")}</div>
          ) : previewDocs.length > 0 && DocumentPreview ? (
            <div style={{ paddingTop: 8 }}>
              <DocumentPreview
                documents={previewDocs}
                titleStyles={{ fontSize: "14px" }}
                pdfSize={48}
                labelWidth={220}
              />
            </div>
          ) : (
            <div style={{ padding: "8px 16px", color: "#666" }}>
              {t("EST_NO_DOCUMENTS_UPLOADED_LABEL")}
            </div>
          )}
        </>
      )}

      {/* ----------------- DECLARATION + SUBMIT ----------------- */}
      <div style={{ marginTop: 16 }}>
        <CheckBox
          label={t(declarationCode)}
          onChange={() => setAgree(!agree)}
          value={agree}
        />
      </div>

      <div style={{ marginTop: 12 }}>
        <SubmitBar
          label={t(submitLabelCode)}
          onSubmit={onSubmit}
          disabled={!agree || isSubmitting}
        />
      </div>
    </Card>
  );
};

export default DynamicCheckPage;
