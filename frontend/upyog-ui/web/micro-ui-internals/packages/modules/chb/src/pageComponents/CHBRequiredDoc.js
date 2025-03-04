import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { stringReplaceAll } from "../utils";

/*
  CHBRequiredDoc  displays the info page for  required documents for an advertisement to book
   and it also shows upload restrictions and document requirements.
*/
const CHBRequiredDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");
  
 
  const { isLoading, data: Documentsob = {}  } = Digit.Hooks.chb.useChbDocumentsMDMS(stateId, "CHB", "Documents");

  let docs = Documentsob?.CHB?.Documents;
  function onSave() {}
  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
      <CardHeader>{t("MODULE_CHB")}</CardHeader>
        <div>
         <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("CHB_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("CHB_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t('CHB_UPLOAD_RESTRICTIONS_TYPES')}</CardText>
          <CardText className={"primaryColor"}>{t('CHB_UPLOAD_RESTRICTIONS_SIZE')}</CardText>
          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? 
                docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t("CHB_" + stringReplaceAll(code, ".", "_"))}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData, dropdownIndex) => (
                        <CardText className={"primaryColor"}>
                          {`${dropdownIndex + 1}`}. {t("CHB_" + stringReplaceAll(dropdownData?.code, ".", "_"))}
                        </CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("CHB_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default CHBRequiredDoc;