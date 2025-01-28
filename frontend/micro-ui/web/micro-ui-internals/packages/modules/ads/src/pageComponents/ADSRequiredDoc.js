import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@upyog/digit-ui-react-components";
import React, { useEffect } from "react";
import { stringReplaceAll } from "../utils";
import { TimerValues } from "../components/TimerValues";

/*
  ADSRequiredDoc  displays the info page for  required documents for an advertisement to book
   and it also shows upload restrictions and document requirements.
*/

const ADSRequiredDoc = ({ t, config, onSelect, userType, formData,value=formData.adslist }) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const stateId = Digit.ULBService.getStateId();
  sessionStorage.removeItem("docReqScreenByBack");
  
  const { isLoading, data:Documentsob = {} } = Digit.Hooks.ads.useADSDocumentsMDMS(stateId, "Advertisement", "Documents");
  let docs = Documentsob?.Advertisement?.Documents;
  function onSave() {}
  function goNext() {
    onSelect();
  }

  return (
    <React.Fragment>
      <Card>
      <div style={{display:"flex", justifyContent: "space-between", width: "100%" }}>
      <CardHeader>{t("MODULE_ADS")}</CardHeader>
      <CardSubHeader>
        <TimerValues 
        timerValues={value?.existingDataSet?.timervalue?.timervalue} 
        SlotSearchData={value?.cartDetails} draftId={value?.existingDataSet?.draftId}
      />
      </CardSubHeader>
      </div>
        <div>
         <CardText className={"primaryColor"}>{t("ADS_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("ADS_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("ADS_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
          <CardSubHeader>{t("ADS_REQ_SCREEN_LABEL")}</CardSubHeader>
          <CardText className={"primaryColor"}>{t("ADS_DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t('ADS_UPLOAD_RESTRICTIONS_TYPES')}</CardText>
          <CardText className={"primaryColor"}>{t('ADS_UPLOAD_RESTRICTIONS_SIZE')}</CardText>
          <div>
            {isLoading && <Loader />}
            {Array.isArray(docs)
              ? 
                docs.map(({ code, dropdownData }, index) => ( 
                    <div key={index}>
                      <CardSubHeader>
                        {index + 1}. {t("ADS_" + stringReplaceAll(code, ".", "_"))}
                      </CardSubHeader>
                      {dropdownData.map((dropdownData, dropdownIndex) => (
                        <CardText className={"primaryColor"}>
                          {`${dropdownIndex + 1}`}. {t("ADS_" + stringReplaceAll(dropdownData?.code, ".", "_"))}
                        </CardText>
                      ))}
                    </div>
                  ))
              : null}
          </div>
        </div>
        <span>
          <SubmitBar label={t("ADS_COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default ADSRequiredDoc;