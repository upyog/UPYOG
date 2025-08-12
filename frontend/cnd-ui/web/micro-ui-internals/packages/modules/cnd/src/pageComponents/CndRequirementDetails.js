import { Card, CardHeader, CardSubHeader, CardText, Loader, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { CND_VARIABLES } from "../utils";

// First page whic will render and shows the Pre-requistes to fill the application Form

const CndRequirementDetails = ({ t, onSelect }) => {
  sessionStorage.removeItem("docReqScreenByBack");
  
 

  return (
    <React.Fragment>
      <Card>
      <CardHeader>{t(CND_VARIABLES.MODULE)}</CardHeader>
        <div>
         <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
            <CardSubHeader>{t("REQ_SCREEN_LABEL")}</CardSubHeader>
            <CardText className={"primaryColor"}>{t("DOC_REQ_SCREEN_LABEL_TEXT")}</CardText>
            <CardText className={"primaryColor"}>{t('DOCUMENT_ACCEPTED_PDF_JPG_PNG')}</CardText>
            <CardText className={"primaryColor"}>
            {1}. {t("CND_UPLOAD_SITE_PHOTO")}
           </CardText>
           <CardText className={"primaryColor"}>
           {2}. {t("CND_UPLOAD_SITE_STACK_PHOTO")}
           </CardText>
        </div>
        <span>
          <SubmitBar label={t(CND_VARIABLES.NEXT)} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default CndRequirementDetails;