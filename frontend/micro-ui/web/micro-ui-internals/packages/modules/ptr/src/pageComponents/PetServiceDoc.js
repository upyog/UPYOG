import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@egovernments/digit-ui-react-components";
import React, { useEffect } from "react";
import { cardBodyStyle, stringReplaceAll } from "../utils";
//import { map } from "lodash-es";

const PetServiceDoc = ({ t, config, onSelect, userType, formData }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");

  const docType = config?.isMutation ? ["MutationDocuments"] : "Documents";
  console.log("dc", docType);
  console.log("stateid", stateId);
  //const { isLoading, data: Documentsob } = Digit.Hooks.pt.usePropertyMDMS(stateId, "PropertyTax", docType);
  const { isLoading, data: Documentsob = {} } = Digit.Hooks.ptr.usePetMDMS(stateId, "PetService", docType);
  console.log("datassss", Documentsob);
  //let docs = Documentsob?.PropertyTax?.[config?.isMutation ? docType[0] : docType];
  let docs = Documentsob?.PetService?.Documents;
  console.log("fewfewffwf", docs);
  //if (!config?.isMutation) docs = docs?.filter((doc) => doc["digit-citizen"]);
  function onSave() {}

  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
        <CardHeader>{!config.isMutation ? t("PTR_DOC_REQ_SCREEN_HEADER") : t("PT_REQIURED_DOC_TRANSFER_OWNERSHIP")}</CardHeader>
        <div>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("PTR_DOC_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("PTR_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
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
                      {/* <CardText>{t(`${code.split('.')[0]}.${code.split('.')[1]}.${code.split('.')[1]}_DESCRIPTION`)}</CardText> */}
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
          <SubmitBar label={t("PTR_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default PetServiceDoc;