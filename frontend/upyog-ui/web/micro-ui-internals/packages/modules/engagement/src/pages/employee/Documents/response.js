import { Banner, Card, ActionBar, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import React from "react";
import { useTranslation } from "react-i18next";

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={props.isSuccess ? t(`ENGAGEMENT_DOC_CREATED`) : t("ENGAGEMENT_DOC_FAILURE")}
      applicationNumber={props.isSuccess ? props.data?.Documents?.[0]?.uuid : ""}
      info={props.isSuccess ? t("ENGAGEMENT_DOCUMENT_ID") : ""}
      successful={props.isSuccess}
    />
  );
};

const Response = (props) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { state } = location;
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
