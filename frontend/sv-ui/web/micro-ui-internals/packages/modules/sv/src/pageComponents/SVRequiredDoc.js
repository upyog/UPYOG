import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { stringReplaceAll } from "../utils";

// First page which will render and shows the Pre-requistes to fill the application Form
const SVRequiredDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");
  
 
  const { isLoading, data: Documentsob = {} } = Digit.Hooks.sv.useSVDoc(stateId, "StreetVending", "Documents");
  let docs = Documentsob?.StreetVending?.Documents;
  function onSave() {}
  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
      <CardHeader>{t("MODULE_SV")}</CardHeader>
        <div>
         <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("SV_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t('SV_DOCUMENT_ACCEPTED_PDF_JPG_PNG')}</CardText>
          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? 
                docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t(stringReplaceAll(code, ".", "_"))}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData, dropdownIndex) => (
                        <CardText className={"primaryColor"}>
                          {`${dropdownIndex + 1}`}. {t(stringReplaceAll(dropdownData?.code, ".", "_"))}
                        </CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default SVRequiredDoc;