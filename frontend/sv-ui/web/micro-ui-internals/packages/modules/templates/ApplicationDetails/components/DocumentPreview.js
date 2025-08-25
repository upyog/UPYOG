import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { CardSubHeader, PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";

/**
 * DocumentPreview Component
 * 
 * This component renders a preview of document files with their titles.
 * It accepts an array of document objects, fetches the associated files using the Digit.UploadServices API,
 * and displays them as clickable PDF icons with titles.
 * 
 * Features:
 * - Organizes documents by sections with optional section titles
 * - Extracts file IDs from nested document structure
 * - Fetches file URLs from the backend service
 * - Displays PDF icons with document titles in a responsive grid layout
 * - Opens documents in a new tab when clicked
 * 
 * @param {Object[]} documents - Array of document objects containing title and values
 * @returns {JSX.Element} DocumentPreview component
 */

function DocumentPreview({ documents }) {
  const { t } = useTranslation();
  const [filesArray, setFilesArray] = useState(() => [] );
  const [pdfFiles, setPdfFiles] = useState({});

  useEffect(() => {
    let acc = [];
    documents?.forEach((element, index, array) => {
      acc = [...acc, ...(element.values?element.values:[])];
    });
    setFilesArray(acc?.map((value) => value?.fileStoreId));
  }, [documents]);

  useEffect(() => {
   if(filesArray?.length)
   { 
     Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()).then((res) => {
      setPdfFiles(res?.data);
     });
    }
    
  }, [filesArray]);

  return (
    <div style={{ marginTop: "19px" }}>
      {documents?.map((document, index) => (
        <React.Fragment key={index}>
          {document?.title ? <CardSubHeader style={{ marginTop: "32px", marginBottom: "8px", color: "#505A5F", fontSize: "24px" }}>{t(document?.title)}</CardSubHeader>: null}
          <div style={{ display: "flex", flexWrap: "wrap", justifyContent: "flex-start", gap:"5%" }}>
            {document?.values && document?.values.length>0 ? document?.values?.map((value, index) => (
              <a target="_" href={pdfFiles[value.fileStoreId]?.split(",")[0]} style={{ minWidth: "80px", marginRight: "10px", maxWidth: "100px", height: "auto" }} key={index}>
                <div style={{ display: "flex", justifyContent: "center" }}>
                  <PDFSvg />
                </div>
                <p style={{ marginTop: "8px", fontWeight: "bold" }}>{t(value?.title)}</p>
              </a>
            )):""}
          </div>
        </React.Fragment>
      ))}
    </div>
  );
}

export default DocumentPreview;
