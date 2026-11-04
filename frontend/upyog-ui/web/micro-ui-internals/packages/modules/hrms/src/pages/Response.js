import React from "react";
import { Card, Banner, CardText, SubmitBar, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`EMPLOYEE_RESPONSE_${action ? action : "CREATE"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetMessage("ACTION", action, isSuccess, isEmployee, t);
};

const GetLabel = (action, isSuccess, isEmployee, t) => {
  if (isSuccess) {
    return t("HR_EMPLOYEE_ID_LABEL");
  }
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props.isSuccess ? props?.data?.Employees?.[0]?.code : ''}
      info={GetLabel(props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const Response = (props) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { state } = location || {};
  const [successData] = Digit.Hooks.useSessionStorage("EMPLOYEE_HRMS_MUTATION_SUCCESS_DATA", false);
  const [errorInfo] = Digit.Hooks.useSessionStorage("EMPLOYEE_HRMS_ERROR_DATA", false);

  const isSuccess = state?.success || !!successData;
  const isError = state?.error || !!errorInfo;
  
  const DisplayText = (action, isSuccess, isEmployee, t) => {
    if (!isSuccess) {
      return errorInfo || "ERROR";
    } else {
      Digit.SessionStorage.set("isupdate", Math.floor(100000 + Math.random() * 900000));
      return state?.key === "CREATE" ? "HRMS_CREATE_EMPLOYEE_INFO" : "";
    }
  };

  return (
    <Card>
      <BannerPicker
        t={t}
        data={successData}
        action={state?.action}
        isSuccess={isSuccess}
        isLoading={false}
        isEmployee={props.parentRoute.includes("employee")}
      />
      <CardText>{t(DisplayText(state?.action, isSuccess, props.parentRoute.includes("employee"), t), t)}</CardText>

      <ActionBar>
        <Link to={`${props.parentRoute.includes("employee") ? "/upyog-ui/employee" : "/upyog-ui/citizen"}`}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </Card>
  );
};

export default Response;
