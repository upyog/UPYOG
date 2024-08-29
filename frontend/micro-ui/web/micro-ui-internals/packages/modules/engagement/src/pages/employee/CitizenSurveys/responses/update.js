import { Banner, Card, Loader, ActionBar, SubmitBar } from "@upyog/digit-ui-react-components";
import { useQueryClient } from "react-query";
import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";

const getMessage = (mutation) => {
  if (mutation.isSuccess) return mutation.data?.ServiceDefinition?.[0]?.id;
  return "";
};

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={props.mutation.isSuccess ? t(`ENGAGEMENT_SURVEY_UPDATED`) : t("ENGAGEMENT_SURVEY_UPDATE_FAILURE")}
      applicationNumber={getMessage(props.mutation)}
      info={props.mutation.isSuccess ? t("SURVEY_FORM_ID") : ""}
      successful={props.mutation.isSuccess}
    />
  );
};

const Response = (props) => {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const mutation = Digit.Hooks.survey.useUpdateSurvey();
  const { state } = props.location;

  useEffect(() => {
    const onSuccess = () => {
      queryClient.clear();
      window.history.replaceState(null, 'UPDATE_SURVEY_STATE')
    };
    if(!!state){
      mutation.mutate(state, {
        onSuccess,
      });
    }
  }, []);

  if (mutation.isLoading || mutation.isIdle) {
    return <Loader />;
  }

  return (
    <div>

      <Card>
        <BannerPicker t={t} data={mutation.data} mutation={mutation} isSuccess={mutation.isSuccess} isLoading={mutation.isIdle || mutation.isLoading} />
      </Card>
      <ActionBar>
        <Link to={"/digit-ui/employee"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default Response;
