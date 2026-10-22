import React, { Fragment, useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { PDFSvg, Row } from "@nudmcdgnpm/digit-ui-react-components";

const DocumentDetails = ({ documents }) => {
  const { t } = useTranslation();
  const [filesArray, setFilesArray] = useState(() => []);
  const [pdfFiles, setPdfFiles] = useState({});

  if (documents?.length == 0) {
    return <div className="obps-document-details-container-padding"><p>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</p></div>
  }
  useEffect(() => {
    let acc = [];
    documents?.forEach((element, index, array) => {
      acc = [...acc, element];
    });
    setFilesArray(acc?.map((value) => value?.filestoreIdArray.map((val) => val)));
  }, [documents]);

  useEffect(() => {
    if (filesArray?.length) {
      Digit.UploadServices.Filefetch(filesArray, Digit.ULBService.getStateId()).then((res) => {
        setPdfFiles(res?.data);
      });
    }
  }, [filesArray]);

  return (
    <Fragment>
      {documents?.map((document, docIndex) => (
        <Fragment>
          <Row className="border-none" labelStyle={{ paddingTop: "10px", width: "100%" }} label={t(document?.title?.split('_')?.slice(0, 2).join('_'))} />
          <div className="obps-document-details-flex-container">
            {document?.filestoreIdArray && document?.filestoreIdArray.map((filestoreId, index) =>
              <div className="obps-document-details-flex-row">
                <a target="_blank" href={pdfFiles[filestoreId]?.split(",")[0]} className="obps-document-details-spacing" key={index}>
                  <div className="obps-document-details-flex-row-2">
                    <PDFSvg />
                  </div>
                  <p className="obps-document-details-centered">{t(document?.title)}</p>
                </a>
              </div>
            )}
          </div>
          {documents?.length != docIndex + 1 ? <hr className="obps-document-details-top-spacing" /> : null}
        </Fragment>
      ))}
    </Fragment>
  );
}

export default DocumentDetails;