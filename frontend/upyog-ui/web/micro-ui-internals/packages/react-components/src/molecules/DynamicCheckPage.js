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
import {
  buildSummarySections,
  collectFormFileEntries,
  defaultCheckNA,
  extractWizardFormValues,
  formatCheckPageDate,
  resolveFieldLabelKey,
  resolveSummaryFieldValue,
} from "../utilities/checkPageUtils";

/* Config-driven summary page — shares routeConfig.form with DynamicForm.
   Utilities: utilities/checkPageUtils.js, useDynamicRouteConfig, useDynamicCheckSubmit */

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
  formatDate = formatCheckPageDate,
  checkNA = defaultCheckNA,
  DocumentPreview,         // component: ({documents, ...}) => JSX (e.g. ESTDocumentPreview)
  declarationCode = "EST_FINAL_DECLARATION_MESSAGE",
  submitLabelCode = "EST_COMMON_SUBMIT",
  isSubmitting = false,
}) => {
  const [agree, setAgree] = useState(false);
  const [previewDocs, setPreviewDocs] = useState([]);
  const [loadingDocs, setLoadingDocs] = useState(false);

  const payloadKey = routeConfig?.payloadKey || "Allotments";

  const formValues = useMemo(
    () => extractWizardFormValues(value, config?.key, payloadKey),
    [value, config, payloadKey]
  );

  const { sections, fileFields } = useMemo(
    () => buildSummarySections(routeConfig?.form || []),
    [routeConfig]
  );

  const uploadedFiles = useMemo(
    () => collectFormFileEntries(fileFields, formValues, t),
    [fileFields, formValues, t]
  );

  const resolveValue = (fc) =>
    resolveSummaryFieldValue(fc, { formValues, extraData, formatDate, checkNA, t });

  // ── Document preview: fetch URLs for every uploaded file field ──
  useEffect(() => {
    let mounted = true;

    if (uploadedFiles.length === 0) {
      if (mounted) setPreviewDocs([]);
      return () => { mounted = false; };
    }

    const filestoreIds = uploadedFiles.map((f) => f.id);
    const idToLabelMap = Object.fromEntries(
      uploadedFiles.map((f) => [f.id, f.label])
    );

    setLoadingDocs(true);
    Digit.UploadServices.Filefetch(filestoreIds, Digit.ULBService.getStateId())
      .then((res) => {
        if (!mounted) return;
        const arr = res?.data?.fileStoreIds;
        const urlById = {};

        if (Array.isArray(arr)) {
          arr.forEach((fsObj, index) => {
            const fsid = fsObj?.fileStoreId || fsObj?.id;
            const url = fsObj?.url?.split(",")[0];
            if (!url) return;
            if (fsid) urlById[fsid] = url;
            if (filestoreIds[index]) urlById[filestoreIds[index]] = url;
          });
        }

        const values = uploadedFiles.map((file) => ({
          url: urlById[file.id] || null,
          title: idToLabelMap[file.id] || file.label,
          documentType: file.id,
          fileStoreId: file.id,
          reference: file.reference,
          fileName: file.fileName,
        }));

        if (mounted) setPreviewDocs([{ values }]);
      })
      .catch((err) => {
        console.error("Error fetching files for preview:", err);
        if (mounted) {
          setPreviewDocs([
            {
              values: uploadedFiles.map((file) => ({
                url: null,
                title: file.label,
                documentType: file.id,
                fileStoreId: file.id,
                reference: file.reference,
                fileName: file.fileName,
              })),
            },
          ]);
        }
      })
      .finally(() => mounted && setLoadingDocs(false));

    return () => { mounted = false; };
  }, [uploadedFiles]);

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
                label={t(resolveFieldLabelKey(fc, formValues))}
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
      {(fileFields.length > 0 || uploadedFiles.length > 0) && (
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
                useThumbnails
                thumbSize={80}
              />
            </div>
          ) : uploadedFiles.length > 0 ? (
            <div style={{ padding: "8px 16px 12px" }}>
              {uploadedFiles.map((file) => (
                <div key={file.id} style={{ marginBottom: 12 }}>
                  <div style={{ fontWeight: 700, fontSize: "14px", color: "#111" }}>
                    {file.label}
                  </div>
                  <div style={{ fontSize: "13px", color: "#666", marginTop: 4 }}>
                    {file.fileName || file.reference}
                  </div>
                </div>
              ))}
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
