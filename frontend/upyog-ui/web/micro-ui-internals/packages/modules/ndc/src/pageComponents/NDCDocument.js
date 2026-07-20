import { PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { pdfDownloadLink } from "../utils";

// Component to display a list of documents related to NDC application. It fetches document data using a custom hook and renders links to view the documents.
function NDCDocument({ value = {} }) {
  const { t } = useTranslation();
  const isMobile = window.Digit.Utils.browser.isMobile();

  const { isLoading, isError, error, data } = Digit.Hooks.ndc.useNDCDocumentSearch(
    {
      value,
    },
    { value }
  );
  let documents = [];
  if (value?.workflowDocs) documents = value?.workflowDocs;
 

  return (
    <div className="document-container">
      <React.Fragment>
        <div className="document-grid">
          {documents?.map((document, index) => {
            let documentLink = pdfDownloadLink(data?.pdfFiles, document?.uuid);
            return (
              <a target="_blank" rel="noopener noreferrer" href={documentLink} className="document-link" key={index}>
                <div className="document-card">
                  <div className="document-icon-wrapper">
                    <PDFSvg width={isMobile ? 50 : 80} height={isMobile ? 60 : 100} />
                  </div>
                  <p className="document-name" title={t(document?.documentType)}>
                    {(() => {
                      const text = t(document?.documentType);
                      const maxLength = isMobile ? 8 : 12;
                      return text?.length > maxLength ? `${text.substring(0, maxLength)}...` : text;
                    })()}
                  </p>
                  <div className="document-action-label">
                    View
                  </div>
                </div>
              </a>
            );
          })}
        </div>
      </React.Fragment>
    </div>
  );
}

export default NDCDocument;
