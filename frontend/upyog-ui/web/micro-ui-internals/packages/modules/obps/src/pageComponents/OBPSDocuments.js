import { CardText, Loader, PDFSvg } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { pdfDownloadLink, stringReplaceAll } from "../utils";

function OBPSDocument({ value = {}, Code, index, isNOC = false, svgStyles = { width: "100px", height: "100px", viewBox: "0 0 25 25", minWidth: "100px" }, isStakeHolder = false }) {
  const { t } = useTranslation();
  const { isLoading, isError, error, data } = Digit.Hooks.obps.useOBPSDocumentSearch(
    {
      value,
    },
    { value }, Code, index, isNOC
  );
  let documents = [];

  if (isNOC) {
    value?.nocDocuments ? value?.nocDocuments?.nocDocuments.length > 0 && value?.nocDocuments?.nocDocuments.filter((ob) => ob?.documentType?.includes(Code)).map((ob) => {
      documents.push(ob);
    }) : value?.length > 0 && value?.filter((ob) => ob?.documentType?.includes(Code)).map((ob) => {
      documents.push(ob);
    });
  }
  else{
  value?.documents ? value?.documents?.documents.filter(doc => doc?.documentType === Code /* || doc?.documentType?.includes(Code.split(".")[1]) */).map((ob)=>{
    documents.push(ob);
  }) : value.filter(doc => doc?.documentType === Code /* || doc?.documentType.includes(Code.split(".")[1]) */).map((ob)=>{
    documents.push(ob);
  })
}
  
  if (isLoading) {
    return <Loader />;
  }

  return (
    <div className="obps-obpsdocuments-top-spacing">
      <React.Fragment>
        <div className="obps-document-details-flex-container">
          {documents.length > 0 ?
            <div className="obps-obpsdocuments-flex-row">
              {documents?.map((document, index) => {
                let documentLink = pdfDownloadLink(data.pdfFiles, document?.fileStoreId);
                return (
                  <a target="_blank" href={documentLink} className="obps-obpsdocuments-spacing" key={index}>
                    <div className="obps-document-details-flex-row-2">
                      <PDFSvg />
                    </div>
                    {isStakeHolder ? <p className="obps-obpsdocuments-centered">{t(`BPAREG_HEADER_${stringReplaceAll(Code?.toUpperCase(), ".", "_")}`)}</p> : null}  {/* document?.fileName ? `${document?.fileName}` : `${t(`ES_COMMON_DOC_DOCUMENT`)} - ${index + 1}` */}
                    {!isStakeHolder ? <p className="obps-obpsdocuments-centered">{`${t(Code)}`}</p> : null}
                  </a>
                );
              })}
            </div> : <CardText>{t("BPA_NO_DOCUMENTS_UPLOADED_LABEL")}</CardText>}
        </div>
      </React.Fragment>
    </div>
  );
}

export default OBPSDocument;