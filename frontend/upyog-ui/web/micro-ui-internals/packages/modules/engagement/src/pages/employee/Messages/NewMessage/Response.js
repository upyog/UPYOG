import { ActionBar, Banner, Card, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={t(props.message)}
      applicationNumber={props?.data?.events?.[0]?.name}
      successful={props.isSuccess}
    />
  )
}

const Response = () => {
  const { t } = useTranslation();
  const searchParams = Digit.Hooks.useQueryParams();
  const { state } = useLocation();

  const isSuccess = state?.isSuccess;
  const data = state?.data;

  if (searchParams?.delete || searchParams?.update) {
    return (
      <Card>
        <BannerPicker
          t={t}
          message={
            searchParams?.update
              ? isSuccess
                ? 'ENGAGEMENT_PUBLIC_BRDCST_UPDATED'
                : 'ENG_PUBLIC_BRDCST_UPDATION_FAILED'
              : isSuccess
              ? 'ENGAGEMENT_PUBLIC_BRDCST_DELETED'
              : 'ENGAGEMENT_PUBLIC_BRDCST_DELETION_FAILED'
          }
          data={data}
          isSuccess={isSuccess}
        />
        <CardText>
          {searchParams?.update ? t(`ENGAGEMENT_PUBLIC_BRDCST_MESSAGES`) : t(`ENGAGEMENT_PUBLIC_BRDCST_MESSAGES`)}
        </CardText>
        <ActionBar>
          <Link to={"/upyog-ui/employee"}>
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </ActionBar>
      </Card>
    );
  }

  return (
    <Card>
      <BannerPicker
        t={t}
        message={isSuccess ? `ENGAGEMENT_BROADCAST_MESSAGE_CREATED` : `ENGAGEMENT_BROADCAST_MESSAGE_FAILED`}
        data={data}
        isSuccess={isSuccess}
      />
      <ActionBar>
        <Link to={"/upyog-ui/employee"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </Card>
  );
}

export default Response;