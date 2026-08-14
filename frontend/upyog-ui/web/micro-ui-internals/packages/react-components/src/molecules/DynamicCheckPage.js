/**
 * DynamicCheckPage.js
 *
 * Config-driven review / summary page shown before final wizard submit
 * (and optionally as a read-only application-details view).
 * Uses the same routeConfig.form schema as DynamicForm so summary rows,
 * labels, and file fields stay in sync with the create/edit steps.
 *
 * Responsibilities
 * ----------------
 * 1. Extract flat form values from wizard session data via
 *    extractWizardFormValues(value, config.key, payloadKey).
 * 2. Split routeConfig.form into summary sections + file fields with
 *    buildSummarySections (section headers, visible leaf fields).
 * 3. Render each section as CardSubHeader + StatusTable rows; cell text
 *    comes from resolveSummaryFieldValue (dropdown labels, dates via
 *    formatDate, empty → checkNA).
 * 4. Collect uploaded files with collectFormFileEntries, then Filefetch
 *    URLs for DocumentPreview (falls back to a plain file list if preview
 *    is unavailable or fetch fails).
 * 5. In edit mode (not viewOnly): declaration checkbox + SubmitBar gated
 *    on agree / isSubmitting; optional Edit icon on the first row navigates
 *    to editRoute with editNavigationState.
 * 6. In viewOnly mode: hide edit action, declaration, and submit — summary
 *    and documents only.
 *
 * Typical usage
 * -------------
 *   <DynamicCheckPage
 *     routeConfig={mergedRouteConfig}
 *     config={{ key: stepKey }}
 *     value={wizardFormData}
 *     editRoute="/employee/.../edit"
 *     onSubmit={handleFinalSubmit}
 *     DocumentPreview={DocumentPreview}
 *     t={t}
 *   />
 *
 *   // Application details (read-only)
 *   <DynamicCheckPage
 *     routeConfig={routeConfig}
 *     config={{ key: stepKey }}
 *     value={applicationData}
 *     viewOnly
 *     DocumentPreview={DocumentPreview}
 *     t={t}
 *   />
 *
 * Props
 * -----
 * @param {object}   routeConfig                 Merged MDMS route/step config
 *                                               (form, payloadKey, texts.header, etc.).
 * @param {object}   [config]                    Step identity; config.key selects wizard
 *                                               step data under value.
 * @param {object}   [value]                     Wizard / application session data.
 * @param {object}   [extraData]                 Extra context passed into value resolvers
 *                                               (module-specific overrides).
 * @param {string}   [editRoute]                 Path for the first-row Edit action.
 * @param {object}   [editNavigationState]       Location state passed with edit navigation.
 * @param {Function} [onSubmit]                  Final submit handler (enabled when agreed).
 * @param {string}   [summaryHeaderCode]         Card header i18n key; falls back to
 *                                               routeConfig.texts.header or CS_COMMON_SUMMARY.
 * @param {string}   [defaultSectionHeaderCode]  Fallback section sub-header i18n key.
 * @param {Function} [t]                         i18n translator; defaults to identity.
 * @param {Function} [formatDate]                Date formatter for summary cells
 *                                               (default formatCheckPageDate).
 * @param {Function} [checkNA]                   Empty-value fallback (default defaultCheckNA).
 * @param {Component} [DocumentPreview]          Optional preview component for Filefetch URLs.
 * @param {string}   [declarationCode]           Declaration checkbox label i18n key.
 * @param {string}   [submitLabelCode]           Submit button i18n key.
 * @param {string}   [documentPreviewCode]       Documents section sub-header i18n key.
 * @param {string}   [noDocumentsCode]           Empty documents message i18n key.
 * @param {boolean}  [isSubmitting=false]        Disables SubmitBar while mutation is in flight.
 * @param {boolean}  [viewOnly=false]            Read-only summary — no edit / declare / submit.
 *
 * @see DynamicForm
 * @see DynamicFormStep
 * @see buildSummarySections
 * @see extractWizardFormValues
 * @see resolveSummaryFieldValue
 */

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
  extractUrlFromFilefetchResponse,
  extractWizardFormValues,
  formatCheckPageDate,
  resolveFieldLabelKey,
  resolveSummaryFieldValue,
} from "../utilities/checkPageUtils";

/**
 * First-row Edit control for the summary StatusTable.
 * Navigates to `jumpTo` with optional location state so the wizard can
 * reopen the create/edit step with the same session context.
 *
 * @param {object} props
 * @param {string} props.jumpTo                 Route path for upyog custom navigate.
 * @param {object} [props.editNavigationState]  Location state passed with navigation.
 * @returns {JSX.Element}
 */
const ActionButton = ({ jumpTo, editNavigationState }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  return (
    <LinkButton
      label={<EditIcon />}
      onClick={() =>
        navigate(jumpTo, {
          state: editNavigationState || {},
        })
      }
    />
  );
};

/**
 * Config-driven review / summary page before final submit (or read-only
 * application details when viewOnly). Shares routeConfig.form with DynamicForm.
 *
 * @param {object} props — see file-level Props section above.
 * @returns {JSX.Element}
 */
const DynamicCheckPage = ({
  routeConfig,
  config,
  value = {},
  extraData = {},
  editRoute,
  editNavigationState,
  onSubmit,
  summaryHeaderCode,
  defaultSectionHeaderCode = "CS_COMMON_DETAILS",
  t = (k) => k,
  formatDate = formatCheckPageDate,
  checkNA = defaultCheckNA,
  DocumentPreview,
  declarationCode = "CS_COMMON_DECLARATION_MESSAGE",
  submitLabelCode = "CS_COMMON_SUBMIT",
  documentPreviewCode = "CS_COMMON_DOCUMENT_PREVIEW",
  noDocumentsCode = "CS_COMMON_NO_DOCUMENTS_UPLOADED",
  isSubmitting = false,
  viewOnly = false,
}) => {
  /** Declaration checkbox; SubmitBar stays disabled until true. */
  const [agree, setAgree] = useState(false);
  /**
   * DocumentPreview-shaped list: [{ values: [{ url, title, fileStoreId, ... }] }].
   * Empty when there are no uploads or before Filefetch completes.
   */
  const [previewDocs, setPreviewDocs] = useState([]);
  /** True while Digit.UploadServices.Filefetch is in flight. */
  const [loadingDocs, setLoadingDocs] = useState(false);

  /** Wizard / API array key under which step form values are stored. */
  const payloadKey = routeConfig?.payloadKey || "Allotments";

  /**
   * Flat field map for the current step, extracted from wizard session `value`
   * at value[config.key][payloadKey][0] (with fallbacks inside extractWizardFormValues).
   *
   * @returns {object}
   */
  const formValues = useMemo(
    () => extractWizardFormValues(value, config?.key, payloadKey),
    [value, config, payloadKey]
  );

  /**
   * Splits routeConfig.form into:
   * - sections: [{ headerCode, fields }] for StatusTable rows
   * - fileFields: leaf configs with type === "file" for document preview
   *
   * @returns {{ sections: object[], fileFields: object[] }}
   */
  const { sections, fileFields } = useMemo(
    () => buildSummarySections(routeConfig?.form || []),
    [routeConfig]
  );

  /**
   * Uploaded file entries derived from fileFields + formValues
   * ({ id, label, reference, fileName } per filestore id).
   *
   * @returns {object[]}
   */
  const uploadedFiles = useMemo(
    () => collectFormFileEntries(fileFields, formValues, t),
    [fileFields, formValues, t]
  );

  /**
   * Resolves a summary cell's display text for one field config
   * (dropdown labels, dates via formatDate, empty → checkNA, extraData hooks).
   *
   * @param {object} fc - Leaf field config from a summary section.
   * @returns {string}
   */
  const resolveValue = (fc) =>
    resolveSummaryFieldValue(fc, { formValues, extraData, formatDate, checkNA, t });

  /**
   * Fetches download URLs for uploaded filestore ids and builds previewDocs.
   * Clears preview when there are no files. On Filefetch failure, still sets
   * a metadata-only preview list (url: null) so labels remain visible.
   * Uses a mounted flag to avoid setState after unmount.
   */
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
    const tenantId =
      Digit.ULBService.getCurrentTenantId() || Digit.ULBService.getStateId();

    Digit.UploadServices.Filefetch(filestoreIds, tenantId)
      .then((res) => {
        if (!mounted) return;
        const urlById = {};

        filestoreIds.forEach((id, index) => {
          const url = extractUrlFromFilefetchResponse(res, id, index);
          if (url) urlById[id] = url;
        });

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

  /**
   * Toggles the declaration checkbox that gates final submit.
   */
  const handleAgreeChange = () => setAgree(!agree);

  return (
    <Card>
      <CardHeader>
        {t(summaryHeaderCode || routeConfig?.texts?.header || "CS_COMMON_SUMMARY")}
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
                  !viewOnly && sIdx === 0 && fIdx === 0 && editRoute
                    ? <ActionButton jumpTo={editRoute} editNavigationState={editNavigationState} />
                    : undefined
                }
              />
            ))}
          </StatusTable>
        </React.Fragment>
      ))}

      {(fileFields.length > 0 || uploadedFiles.length > 0) && (
        <>
          <CardSubHeader>{t(documentPreviewCode)}</CardSubHeader>
          {loadingDocs ? (
            <div className="dynamic-check-page__loading">{t("CS_LOADING")}</div>
          ) : previewDocs.length > 0 && DocumentPreview ? (
            <div className="dynamic-check-page__preview">
              <DocumentPreview
                documents={previewDocs}
                pdfSize={48}
                labelWidth={220}
                useThumbnails
                thumbSize={80}
              />
            </div>
          ) : uploadedFiles.length > 0 ? (
            <div className="dynamic-check-page__file-list">
              {uploadedFiles.map((file) => (
                <div key={file.id} className="dynamic-check-page__file-item">
                  <div className="dynamic-check-page__file-label">{file.label}</div>
                  {file.fileName && file.fileName !== file.id ? (
                    <div className="dynamic-check-page__file-ref">{file.fileName}</div>
                  ) : null}
                </div>
              ))}
            </div>
          ) : (
            <div className="dynamic-check-page__empty-docs">
              {t(noDocumentsCode)}
            </div>
          )}
        </>
      )}

      {!viewOnly && (
        <>
          <div className="dynamic-check-page__declaration">
            <CheckBox
              label={t(declarationCode)}
              onChange={handleAgreeChange}
              value={agree}
            />
          </div>

          <div className="dynamic-check-page__submit">
            <SubmitBar
              label={t(submitLabelCode)}
              onSubmit={onSubmit}
              disabled={!agree || isSubmitting}
            />
          </div>
        </>
      )}
    </Card>
  );
};

export default DynamicCheckPage;
