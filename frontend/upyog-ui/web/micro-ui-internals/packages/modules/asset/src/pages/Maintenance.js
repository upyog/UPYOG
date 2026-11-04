import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation,  } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useQueryClient } from "@tanstack/react-query";



const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(`${isEmployee ? "E" : "C"}S_MAINTENANCE_RESPONSE_${action ? action : "MAINTENANCE"}_${type}${isSuccess ? "" : "_ERROR"}`);
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
      message={GetActionMessage(props?.data?.Asset?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      // applicationNumber={props?.data?.Assets?.[0]?.applicationNo}
      // info={GetLabel(props.data?.Assets?.[0]?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const Maintenance = (props) => {
  const { t } = useTranslation();
        const location = useLocation();
        const { state } = location;
        // Safe check for parentRoute
        const isEmployee = Digit.UserService.getUser()?.info?.type || true;
        
        // Extract data from navigation state
        const isSuccess = state?.isSuccess ?? true; // Default to true if not specified
        const assetsData = state?.Assets || {};
        const action = state?.action || "MAINTENANCE";


  

  return (
    <div>
      <Card>
        <BannerPicker
          t={t}
          data={assetsData}
          action={action}
          isSuccess={isSuccess}
          isEmployee={true}
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
export default Maintenance;
