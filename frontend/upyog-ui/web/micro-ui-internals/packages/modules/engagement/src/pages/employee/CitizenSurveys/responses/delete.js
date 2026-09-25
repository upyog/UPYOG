import { Banner, Card, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={props.isSuccess ? t(`ENGAGEMENT_SURVEY_DELETED`) : t("ENGAGEMENT_SURVEY_DELETE_FAILURE")}
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
