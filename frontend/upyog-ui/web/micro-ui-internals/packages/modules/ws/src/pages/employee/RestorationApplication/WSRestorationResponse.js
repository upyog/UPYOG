import { Banner, Card, CardText, SubmitBar, ActionBar } from "@upyog/digit-ui-react-components";
import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useHistory } from "react-router-dom";
import * as func from "../../../utils";
import getWSRestorationAcknowledgementData from "../../../utils/getWSRestorationAcknowledgementData";
import "../../../css/ws-inline-auto.css";
const WSDisconnectionResponse = props => {
  const {
    t
  } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const history = useHistory();
  let filters = func.getQueryStringParams(location.search);
  const disconnectionData = Digit.SessionStorage.get("WS_DISCONNECTION");
  const handleDownloadPdf = () => {
    const disconnectionRes = disconnectionData?.DisconnectionResponse;
    const PDFdata = getWSRestorationAcknowledgementData(disconnectionRes, disconnectionData?.propertyDetails, disconnectionRes?.tenantId, t);
    PDFdata.then(res => Digit.Utils.pdf.generatev1(res));
  };
  const onSubmit = () => {
    history.push(`/upyog-ui/employee`);
  };
  return <div>
      <Card>
        <Banner message={t("WS_APPLICATION_SUBMITTED_SUCCESSFULLY_LABEL")} applicationNumber={filters?.applicationNumber} info={filters?.applicationNumber?.includes("WS") ? t("WS_WATER_APPLICATION_NUMBER_LABEL") : t("WS_SEWERAGE_APPLICATION_NUMBER_LABEL")} successful={true} headerStyles={{
        fontSize: "32px"
      }} infoOneStyles={{
        paddingTop: "20px"
      }} className="ws-auto-332" />
        <CardText className="ws-auto-333">{t("WS_MESSAGE_SUB_DESCRIPTION_LABEL")}</CardText>
        <div className="ws-auto-334">
         <div className="primary-label-btn d-grid ws-auto-335" onClick={handleDownloadPdf}>
            <svg width="20" height="23" viewBox="0 0 20 23" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19.3334 8H14V0H6.00002V8H0.666687L10 17.3333L19.3334 8ZM0.666687 20V22.6667H19.3334V20H0.666687Z" fill="#a82227" />
            </svg>
            {t("WS_PRINT_APPLICATION_LABEL")}
          </div>
        </div>

        <ActionBar className="ws-auto-336">
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} onSubmit={onSubmit} className="ws-auto-337" />
        </ActionBar>
      </Card>
    </div>;
};
export default WSDisconnectionResponse;
