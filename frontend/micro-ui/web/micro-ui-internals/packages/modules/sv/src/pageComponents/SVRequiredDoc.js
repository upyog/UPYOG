import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useEffect } from "react";
import { stringReplaceAll } from "../utils";


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
        <div>
         
          <CardSubHeader>{t("SV_REQ_SCREEN_LABEL")}</CardSubHeader>
        
          <CardText style={{color: 'red'}}>{t('SV_DOCUMENT_ACCEPTED_PDF_JPG_PNG')}</CardText>
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