import { Loader, PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { pdfDownloadLink } from "../utils";

// Component to display a list of documents related to NDC application. It fetches document data using a custom hook and renders links to view the documents.
function NDCDocument({ value = {}, Code, index }) {
  const { t } = useTranslation();
  const { isLoading, isError, error, data } = Digit.Hooks.ads.useADSDocumentSearch({ value }, { value }, Code, index);

  const documents = value?.documents
    ? value.documents.documents
        .filter((doc) => doc.documentType === Code)
        .map((doc) => ({ ...doc, documentType: doc.documentType.replace(/\./g, "_") }))
    : value.filter((doc) => doc.documentType === Code).map((doc) => ({ ...doc, documentType: doc.documentType.replace(/\./g, "_") }));

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="document-container">
      <React.Fragment>
        <div className="document-grid">
          {data?.pdfFiles && (
            <div>
              {documents?.map((document, index) => {
                let documentLink = pdfDownloadLink(data.pdfFiles, document?.fileStoreId);
                return (
                  <a
                    className="document-link"
                    target="_blank"
                    rel="noopener noreferrer"
                    href={documentLink}
                  
                    key={index}
                  >
                    <div className="document-card">
                      <div className="document-icon-wrapper">
                        <PDFSvg width={80} height={100} />
                      </div>
                       <p className="document-name" title={t(document?.documentType)}>
                    {(() => {
                      const text = t(document?.documentType);
                      return text?.length > 7
                        ? `${text.substring(0, 7)}...`
                        : text;
                    })()}
                  </p>
                      <div className="document-action-label">View</div>
                    </div>
                  </a>
                );
              })}
            </div>
          )}
        </div>
      </React.Fragment>
    </div>
  );
}

export default NDCDocument;
