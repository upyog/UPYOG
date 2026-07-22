/**
 * DynamicCheckPage — config-driven review/summary page before submit.
 * Shares the same routeConfig.form schema as DynamicForm so summary rows stay in sync.
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
  /** Read-only summary (application details) — hide edit / declaration / submit */
  viewOnly = false,
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
                  <div className="dynamic-check-page__file-ref">
                    {file.fileName || file.reference}
                  </div>
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
              onChange={() => setAgree(!agree)}
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
