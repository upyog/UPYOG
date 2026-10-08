import React, { useEffect, useState } from "react";
import { Card, Banner, CardText, SubmitBar, Loader, LinkButton, Toast, ActionBar } from "@nudmcdgnpm/digit-ui-react-components";
import { Link, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

/**
 * FacilitySubmissionResponse.js
 * 
 * This component handles the response view after submitting a CND (Construction and Demolition) application.
 * It triggers the API call to submit the application data when the page loads, displays a banner based on 
 * success/failure, and shows a toast message for any error. It also manages session-based mutation state 
 * and renders a loader during the API request. Once the submission is complete, the user is given the 
 * option to return to the home screen.
 * 
 * Key Features:
 * - API call to submit CND application using mutation hook
 * - Banner display based on submission outcome
 * - Toast for error messages
 * - Navigation back to employee home page
 */

const GetMessage = (type, action, isSuccess, isEmployee, t) => {
  return t(isSuccess ? `CND_APPLICATION_COMPLETE_SUCCESSFULL`:`CND_APPLICATION_COMPLETION_FAILED`);
};

const GetActionMessage = (action, isSuccess, isEmployee, t) => {
  return GetMessage("ACTION", action, isSuccess, isEmployee, t);
};

const DisplayText = (action, isSuccess, isEmployee, t) => {
  return GetMessage("DISPLAY", action, isSuccess, isEmployee, t);
};



const BannerPicker = (props) => {
  return (
    <Banner
      message={GetActionMessage(props?.data?.cndApplicationDetails?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      applicationNumber={props?.data?.applicationNumber}
      info={(props.data?.cndApplicationDetails?.applicationStatus || props.action, props.isSuccess, props.isEmployee, props.t)}
      successful={props.isSuccess}
    />
  );
};

const FacilitySubmissionResponse = (props) => {
 const { t } = useTranslation();
      const location = useLocation();
      const { state } = location;
      // Safe check for parentRoute
      const isEmployee = Digit.UserService.getUser()?.info?.type || true;
      // Extract data from navigation state
      const isSuccess = state?.isSuccess ?? true; // Default to true if not specified
      const cndData = state?.cndApplication || {};
      const action = state?.action || "CND_APPLICATION_COMPLETE_SUCCESSFULL";


  return (
    <div>
          <Card>
            <BannerPicker
              t={t}
              data={cndData}
              action={action}
              isSuccess={isSuccess}
              isEmployee={true}
            />
            <CardText>
              {DisplayText(action, isSuccess, isEmployee, t)}
            </CardText>
          </Card>
      <ActionBar>
        <Link to={`/cnd-ui/employee`}>
          <SubmitBar label={t("CND_HOME")} />
        </Link>
      </ActionBar>
    </div>
  );
};

export default FacilitySubmissionResponse;