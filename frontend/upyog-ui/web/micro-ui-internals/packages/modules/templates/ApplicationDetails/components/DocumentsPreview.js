import React from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";

function DocumentsPreview({ documents, svgStyles = {}, isSendBackFlow = false, isHrLine = false, titleStyles }) {
  const { t } = useTranslation();
  const isStakeholderApplication = window.location.href.includes("stakeholder");

  return (
    <div className="tmpl-documents-preview-top-spacing">
      {!isStakeholderApplication &&
        documents?.map((document, index) => (
          <React.Fragment key={index}>
            {document?.title ? (
              <div className="app-details-section-header-wrap app-details-doc-header-wrap">
                <CardSubHeader className="app-details-doc-subheader" style={titleStyles ? titleStyles : {}}>
                  {t(document?.title)}
                </CardSubHeader>
              </div>
            ) : null}
            <div className="app-details-doc-grid">
              {document?.values && document?.values.length > 0 ? (
                document?.values?.map((value, vIdx) => (
                  <a target="_blank" rel="noopener noreferrer" href={value?.url} className="app-details-doc-card" key={vIdx}>
                    <div className="app-details-doc-icon-wrap">
                      <PDFSvg />
                    </div>
                    <div className="app-details-doc-info">
                      <p className="app-details-doc-title">{t(value?.title)}</p>
                      {isSendBackFlow && value?.documentType ? (
                        <span className="app-details-doc-type-badge">
                          {value?.documentType?.includes("NOC") ? t(value?.documentType.split(".")[1]) : t(value?.documentType)}
                        </span>
                      ) : null}
                    </div>
                    <span className="app-details-doc-action">{t("CS_COMMON_VIEW") || "View"} &rarr;</span>
                  </a>
                ))
              ) : (
                !window.location.href.includes("citizen") && (
                  <div className="app-details-no-doc-card">
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                      <line x1="9" y1="15" x2="15" y2="15"></line>
                    </svg>
                    <span>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL") || "No documents uploaded"}</span>
                  </div>
                )
              )}
            </div>
            {isHrLine && documents?.length !== index + 1 ? <hr className="tmpl-documents-preview-top-spacing-2" /> : null}
          </React.Fragment>
        ))}
      {isStakeholderApplication &&
        documents?.map((document, index) => (
          <React.Fragment key={index}>
            {document?.title ? <CardSubHeader className="tmpl-documents-preview-header">{t(document?.title)}</CardSubHeader> : null}
            <div className="app-details-doc-grid">
              {document?.values && document?.values.length > 0 ? (
                document?.values?.map((value, vIdx) => (
                  <a target="_blank" rel="noopener noreferrer" href={value?.url} className="app-details-doc-card" key={vIdx}>
                    <div className="app-details-doc-icon-wrap">
                      <PDFSvg />
                    </div>
                    <div className="app-details-doc-info">
                      <p className="app-details-doc-title">{t(value?.title)}</p>
                      {value?.docInfo ? <span className="app-details-doc-type-badge">{t(value?.docInfo)}</span> : null}
                    </div>
                    <span className="app-details-doc-action">{t("CS_COMMON_VIEW") || "View"} &rarr;</span>
                  </a>
                ))
              ) : (
                !window.location.href.includes("citizen") && (
                  <div className="app-details-no-doc-card">
                    <p>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</p>
                  </div>
                )
              )}
            </div>
          </React.Fragment>
        ))}
    </div>
  );
}

export default DocumentsPreview;
