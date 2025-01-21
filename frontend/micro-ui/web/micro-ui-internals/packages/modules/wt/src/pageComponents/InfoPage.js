import { Card, CardHeader, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";

//first component which will show the details for the application form

const InfoPage = ({ t, onSelect}) => {
  sessionStorage.removeItem("docReqScreenByBack");

  return (
    <React.Fragment>
      <Card>
      <CardHeader>{t("MODULE_WT")}</CardHeader>
        <div>
         <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_SUB_HEADER")}</CardText>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_TEXT")}</CardText>
          <CardText className={"primaryColor"}>{t("SV_DOC_REQ_SCREEN_SUB_TEXT")}</CardText>
        </div>
        <span>
          <SubmitBar label={t("COMMON_NEXT")} onSubmit={onSelect} />
        </span>
      </Card>
    </React.Fragment>
  );
};

export default InfoPage;