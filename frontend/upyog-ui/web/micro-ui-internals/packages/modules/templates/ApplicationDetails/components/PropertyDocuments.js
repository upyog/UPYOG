import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";

// const PDFSvg = ({ width = 34, height = 34, style, viewBox = "0 0 34 34" }) => (
//   <svg style={style} xmlns="http://www.w3.org/2000/svg" width={width} height={height} viewBox={viewBox} fill="gray">
//     <path d="M20 2H8c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-8.5 7.5c0 .83-.67 1.5-1.5 1.5H9v2H7.5V7H10c.83 0 1.5.67 1.5 1.5v1zm5 2c0 .83-.67 1.5-1.5 1.5h-2.5V7H15c.83 0 1.5.67 1.5 1.5v3zm4-3H19v1h1.5V11H19v2h-1.5V7h3v1.5zM9 9.5h1v-1H9v1zM4 6H2v14c0 1.1.9 2 2 2h14v-2H4V6zm10 5.5h1v-3h-1v3z" />
//   </svg>
// );

function PropertyDocuments({ documents, svgStyles = {}, isSendBackFlow=false }) {
  const { t } = useTranslation();
  const [filesArray, setFilesArray] = useState(() => [] );
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const [pdfFiles, setPdfFiles] = useState({});

  useEffect(() => {
    let acc = [];
    documents?.forEach((element, index, array) => {
      acc = [...acc, ...(element.values?element.values:[])];
    });
    setFilesArray(acc?.map((value) => value?.fileStoreId));
  }, [documents]);

  useEffect(() => {
     if (filesArray?.length && documents?.[0]?.BS === "BillAmend") {
      Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getCurrentTenantId()).then((res) => {
        setPdfFiles(res?.data);
      });
    }
    else if(filesArray?.length)
   { 
     Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()).then((res) => {
      setPdfFiles(res?.data);
     });
    }
    
  }, [filesArray]);

  const isStakeholderApplication = window.location.href.includes("stakeholder");

  return (
    <div className="tmpl-documents-preview-top-spacing">
      {!isStakeholderApplication &&
        documents?.map((document, index) => (
          <React.Fragment key={index}>
            {document?.title ? (
              <div className="app-details-section-header-wrap app-details-doc-header-wrap">
                <CardSubHeader className="app-details-doc-subheader">
                  {t(document?.title)}
                </CardSubHeader>
              </div>
            ) : null}
            <div className="app-details-doc-grid">
              {document?.values && document?.values.length > 0 ? (
                document?.values?.map((value, vIdx) => (
                  <a
                    target="_blank"
                    rel="noopener noreferrer"
                    href={pdfFiles[value.fileStoreId]?.split(",")[0]}
                    className="app-details-doc-card"
                    key={vIdx}
                  >
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
          </React.Fragment>
        ))}
      {isStakeholderApplication &&
        documents?.map((document, index) => (
          <React.Fragment key={index}>
            {document?.title ? <CardSubHeader className="tmpl-documents-preview-header">{t(document?.title)}</CardSubHeader> : null}
            <div className="app-details-doc-grid">
              {document?.values && document?.values.length > 0 ? (
                document?.values?.map((value, vIdx) => (
                  <a
                    target="_blank"
                    rel="noopener noreferrer"
                    href={pdfFiles[value.fileStoreId]?.split(",")[0]}
                    className="app-details-doc-card"
                    key={vIdx}
                  >
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

export default PropertyDocuments;
