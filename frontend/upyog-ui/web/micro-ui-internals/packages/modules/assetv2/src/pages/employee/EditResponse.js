import React from "react";
import { Card, Banner, CardText, SubmitBar, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_ASSET_RESPONSE_${action ? action : "EDIT"}_${type}${isSuccess ? "" : "_ERROR"}`);
};

const GetActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetMessage("ACTION", action, isSuccess, isEmployee, t);
};

const GetLabel = (action, isSuccess, isEmployee, t) => {
  return GetMessage("LABEL", action, isSuccess, isEmployee, t);
};

const DisplayText = (action, isSuccess, isEmployee, t) => {
  return GetMessage("DISPLAY", action, isSuccess, isEmployee, t);
};

const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.applicationNo}
      info={GetLabel(props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const EditResponse = (props) => {
  const { t } = useTranslation();
  const location = useLocation();
  const { state } = location;

  // Safe check for parentRoute
  const isEmployee = props?.parentRoute?.includes?.("employee") || false;
  
  // Extract data from navigation state
  const isSuccess = state?.isSuccess ?? true; // Default to true if not specified
  const assetsData = state?.Assets || {};
  const action = state?.action || "EDIT";

  return (
    <div>
      <Card>
        <BannerPicker
          t={t}
          data={assetsData}
          action={action}
          isSuccess={isSuccess}
          isEmployee={isEmployee}
        />
        <CardText>
          {DisplayText(action, isSuccess, isEmployee, t)}
        </CardText>
      </Card>
      
      <ActionBar>
        <Link to={"/upyog-ui/employee"}>
          <SubmitBar label={t("CORE_COMMON_GO_TO_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default EditResponse;
