import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { cardBodyStyle, stringReplaceAll } from "../utils";
//import { map } from "lodash-es";

const ServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");

  const docType = config?.isMutation ? ["MutationDocuments"] : "Documents";

  
 
  const { isLoading, data: Documentsob = {} } = Digit.Hooks.asset.useAssetDocumentsMDMS(stateId, "ASSET", docType);
  console.log("Documentsob",Documentsob);
  let docs = Documentsob?.ASSET?.Documents;
  function onSave() {}

  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
        <div>
          {/* <CardText className={"primaryColor"}>{t("ServiceDocDOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("ServiceDocDOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("ServiceDocDOC_REQ_SCREEN_SUB_TEXT")}</CardText> */}
          <CardSubHeader>{t("AST_REQ_SCREEN_LABEL")}</CardSubHeader>
          
          <CardText style={{color: 'red'}}>{t('AST_DOCUMENT_ACCEPTED_PDF_JPG_PNG')}</CardText>

          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? config?.isMutation
                ? docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t(code)}
                      </CardSubHeader>
                      <CardText className={"primaryColor"}>{dropdownData.map((dropdownData) => t(dropdownData?.code)).join(", ")}</CardText>
                    </div>
                  ))
                : docs.map(({ code, dropdownData }, index) => ( 
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

export default ServiceDoc;