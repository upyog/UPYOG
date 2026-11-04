import { Banner, Card, Loader, CardText, ActionBar, SubmitBar, Menu } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
const getMessage = (isSuccess, data) => {
  if (isSuccess) return data?.ServiceDefinition?.[0]?.id || data?.ServiceDefinition?.[0]?.code || "";
  return "";
};

const BannerPicker = (props) => {
  const { t } = useTranslation();
  return (
    <Banner
      message={props.isSuccess ? t(`SURVEY_FORM_CREATED`) : t("SURVEY_FORM_FAILURE")}
      applicationNumber={getMessage(props.isSuccess, props.data)}
      info={props.isSuccess ? t("SURVEY_FORM_ID") : ""}
      successful={props.isSuccess}
    />
  );
};

const Acknowledgement = (props) => {
  const { t } = useTranslation();
  const { state } = useLocation();
  const navigate = Digit.Hooks.useCustomNavigate();
  const [isActionClicked, setIsActionClicked] = useState(false);

  const isSuccess = state?.isSuccess;
  const data = state?.data;

  const survey = data?.Surveys?.[0] || data?.ServiceDefinition?.[0] || data?.ServiceDefinition;

  const handleActionClick = () => {
    setIsActionClicked((prevState => {
      return !prevState;
    }));
  };

  const actionClickHandler = (option) => {
    if (option === t("GO_BACK_TO_HOME")) navigate("/upyog-ui/employee");
    else if (option === t("CREATE_ANOTHER_SURVEY")) navigate("/upyog-ui/employee/engagement/surveys/create");
  };

  return (
    <Card>
      <BannerPicker
        t={t}
        isSuccess={isSuccess}
        data={data}
      />
      <CardText>
        {isSuccess
          ? t(`SURVEY_FORM_CREATION_MESSAGE`, {
            surveyName: survey?.title || survey?.additionalDetails?.title || survey?.code,
            fromDate: Digit.DateUtils.ConvertTimestampToDate(survey?.startDate || survey?.additionalDetails?.startDate),
            toDate: Digit.DateUtils.ConvertTimestampToDate(survey?.endDate || survey?.additionalDetails?.endDate),
          })
          : null}
      </CardText>

      <ActionBar>
        <button onClick={handleActionClick}>
          <SubmitBar label="Action" />
          {isActionClicked && <Menu options={[t("GO_BACK_TO_HOME"), t("CREATE_ANOTHER_SURVEY")]} onSelect={actionClickHandler}></Menu>}
        </button>
      </ActionBar>
    </Card>
  );
};

export default Acknowledgement;