import { ActionBar, Banner, Card, CardText, SubmitBar } from "@nudmcdgnpm/digit-ui-react-components";
import { format } from "date-fns";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation } from "react-router-dom";

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={t(props.message)}
      applicationNumber={props?.data?.events?.[0]?.name}
      info={t(`ENGAGEMENT_EVENT_NAME`)}
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
                ? 'ENGAGEMENT_EVENT_UPDATED'
                : 'ENGAGEMENT_EVENT_UPDATED_FAILED'
              : isSuccess
              ? 'ENGAGEMENT_EVENT_DELETED'
              : 'ENGAGEMENT_EVENT_DELETED_FAILED'
          }
          data={data}
          isSuccess={isSuccess}
        />
        <CardText>
          {searchParams?.update
            ? isSuccess
              ? t('ENGAGEMENT_EVENT_UPDATED')
              : t('ENGAGEMENT_EVENT_UPDATED_FAILED')
            : isSuccess
            ? t('ENGAGEMENT_EVENT_DELETED')
            : t('ENGAGEMENT_EVENT_DELETED_FAILED')}
        </CardText>
        <ActionBar>
          <Link to={"/upyog-ui/employee"}>
            <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
          </Link>
        </ActionBar>
      </Card>
    );
  }

  const event = data?.events?.[0] || {};
  return (
    <Card>
      <BannerPicker
        t={t}
        message={isSuccess ? `ENGAGEMENT_EVENT_CREATED_MESSAGE` : `ENGAGEMENT_EVENT_FAILED_MESSAGES`}
        data={data}
        isSuccess={isSuccess}
      />
      <CardText>
        {isSuccess ? t(`ENGAGEMENT_EVENT_CREATED_MESSAGES`, {
          eventName: event?.name,
          fromDate: Digit.DateUtils.ConvertTimestampToDate(event?.eventDetails?.fromDate),
          toDate: Digit.DateUtils.ConvertTimestampToDate(event?.eventDetails?.toDate),
          fromTime: format(new Date(event?.eventDetails?.fromDate), 'HH:mm'),
          toTime: format(new Date(event?.eventDetails?.toDate), 'HH:mm'),
        }) : null}
      </CardText>
      <ActionBar>
        <Link to={"/upyog-ui/employee"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </Card>
  );
}

export default Response;