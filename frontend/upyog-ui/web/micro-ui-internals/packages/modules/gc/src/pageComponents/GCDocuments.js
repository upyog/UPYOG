import { Loader } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { pdfDownloadLink } from "../utils";

/* 
  GCDocuments Component

  This component is used for uploading and displaying documents.
  It fetches documents based on the provided type for Garbage Collection.
*/

function GCDocuments({ value = {}, Code, index, showFileName= false }) {
  const { t } = useTranslation();
  const { isLoading, isError, error, data } = Digit.Hooks.gc.useGCDocumentSearch(
    { value, },
    { value },
    Code,
    index
  );
  
  const PDFSvg = () => (
    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
      <rect width="24" height="24" rx="4" fill="#D32F2F"/>
      <text x="0" y="16" fontFamily="Arial, sans-serif" fontSize="12" fontWeight="bold" fill="#FFFFFF">PDF</text>
    </svg>
  );

  let docsArray = [];
  if (Array.isArray(value?.documents?.documents)) {
    docsArray = value.documents.documents;
  } else if (Array.isArray(value?.documents)) {
    docsArray = value.documents;
  } else if (Array.isArray(value)) {
    docsArray = value;
  }

  const documents = docsArray.filter(doc => doc.documentType === Code).map(doc => ({ ...doc, documentType: doc.documentType.replace(/\./g, '_') }));
  
  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
      <React.Fragment>
        <div >
          {documents?.map((document, idx) => {
            let documentLink = pdfDownloadLink(data?.pdfFiles, document.fileStoreId);
            return (
              <a target="_" href={documentLink} style={{ minWidth: "160px", display: "flex", alignItems: "center" }} key={document?.fileStoreId || idx}>
              {/* Text first */}
              <p style={{ marginRight: "8px", margin: "5px", color:"blue", fontWeight: "bold" }}>
                {t("GC_" + (Code?.split('.').slice(0, 4).join('_')))}
              </p>
      
              {/* Icon second */}
              <PDFSvg />
            </a>
            );
          })}
        </div>
      </React.Fragment>
    </div>
  );
}

export default GCDocuments;