import { Banner, Card, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

const getMessage = (isSuccess, data) => {
  if (isSuccess) return data?.ServiceDefinition?.[0]?.id || data?.ServiceDefinition?.[0]?.code || "";
  return "";
};

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={props.isSuccess ? t(`ENGAGEMENT_SURVEY_UPDATED`) : t("ENGAGEMENT_SURVEY_UPDATE_FAILURE")}
      applicationNumber={getMessage(props.isSuccess, props.data)}
      info={props.isSuccess ? t("SURVEY_FORM_ID") : ""}
      successful={props.isSuccess}
    />
  );
};

const Response = (props) => {
  const { t } = useTranslation();
  const { state } = useLocation();

  const isSuccess = state?.isSuccess;
  const data = state?.data;

  return (
    <div>
      <Card>
        <BannerPicker t={t} data={data} isSuccess={isSuccess} />
      </Card>
      <ActionBar>
        <Link to={"/upyog-ui/employee"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default Response;
