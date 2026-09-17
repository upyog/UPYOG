/**
 * Renders the Employee workflow action response screen.
 * Displays application approval/rejection banner, NOC reference number, and a return to home action.
 */
import { Banner, Card, CardText, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";

import { stringReplaceAll } from "../../utils";

const Response = (props) => {
  const location = useLocation();
  const { state } = location;
  const { t } = useTranslation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const nocData = state?.data?.Noc?.[0];

  const onSubmit = () => {
    navigate(`/upyog-ui/employee`);
  }

  return (
    <div>
      <Card>
        <Banner
          message={t(`NOC_${stringReplaceAll(nocData?.nocType, ".", "_")}_${stringReplaceAll(nocData?.applicationStatus, ".", "_")}_HEADER`)}
          applicationNumber={nocData?.nocNo}
          info={nocData?.applicationStatus == "REJECTED" ? "" : t(`NOC_${stringReplaceAll(nocData?.nocType, ".", "_")}_APPROVAL_NUMBER`)}
          successful={nocData?.applicationStatus == "REJECTED" ? false : true}
          style={{ padding: "10px" }}
          headerStyles={{fontSize: "32px", wordBreak: "break-word"}}
        />
        { nocData?.applicationStatus !== "REJECTED" ? <CardText>{t(`NOC_${stringReplaceAll(nocData?.nocType, ".", "_")}_${stringReplaceAll(nocData?.applicationStatus, ".", "_")}_SUB_HEADER`)}</CardText> : null}
        <ActionBar style={{ display: "flex", justifyContent: "flex-end", alignItems: "baseline" }}>
          <SubmitBar
            label={t("CORE_COMMON_GO_TO_HOME")}
            onSubmit={onSubmit}
          />
        </ActionBar>
      </Card>
    </div>
  );
};
export default Response;