import { BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useLocation, Routes, Route, Navigate } from "react-router-dom";

const CitizenApp = ({ path }) => {
  const location = useLocation();
  const { t } = useTranslation();
  let isCommonPTPropertyScreen = window.location.href.includes("/ws/create-application/property-details");
  let isAcknowledgement = window.location.href.includes("/acknowledgement") || window.location.href.includes("/disconnect-acknowledge");
  const NewApplicationCitizen = Digit.ComponentRegistryService.getComponent("FSMNewApplicationCitizen");
  const MyApplications = Digit.ComponentRegistryService.getComponent("FSMMyApplications");
  const EmployeeApplicationDetails = Digit.ComponentRegistryService.getComponent("FSMEmployeeApplicationDetails");
  const ApplicationDetails = Digit.ComponentRegistryService.getComponent("FSMCitizenApplicationDetails");
  const SelectRating = Digit.ComponentRegistryService.getComponent("FSMSelectRating");
  const RateView = Digit.ComponentRegistryService.getComponent("FSMRateView");
  const Response = Digit.ComponentRegistryService.getComponent("FSMResponse");
  const DsoDashboard = Digit.ComponentRegistryService.getComponent("FSMDsoDashboard");
  const Inbox = Digit.ComponentRegistryService.getComponent("FSMEmpInbox");

  return (
    <React.Fragment>
      <div className="fsm-citizen-wrapper">
        {location.pathname.includes("/response") || location.pathname.split("/").includes("check") ? null : location.pathname.includes("/street") ? (
          <BackButton getBackPageNumber={() => -4}>{t("CS_COMMON_BACK")}</BackButton>
        ) : (
          <BackButton>{t("CS_COMMON_BACK")}</BackButton>
        )}
        <Routes>
          <Route
            path="inbox"
            element={
              Digit.UserService.hasAccess(["FSM_DSO"]) ? (
                <PrivateRoute>
                  <Inbox parentRoute={path} isInbox={true} />
                </PrivateRoute>
              ) : (
                <Navigate to="/upyog-ui/citizen" replace />
              )
            }
          />
          <Route
            path="search"
            element={
              Digit.UserService.hasAccess(["FSM_DSO"]) ? (
                <PrivateRoute>
                  <Inbox parentRoute={path} isSearch={true} />
                </PrivateRoute>
              ) : (
                <Navigate to="/upyog-ui/citizen" replace />
              )
            }
          />
          <Route path="new-application/*" element={<PrivateRoute><NewApplicationCitizen parentRoute={path} /></PrivateRoute>} />
          <Route path="my-applications/*" element={<PrivateRoute><MyApplications /></PrivateRoute>} />
          <Route path="dso-application-details/:id" element={<PrivateRoute><EmployeeApplicationDetails parentRoute={path} userType="DSO" /></PrivateRoute>}/>
          <Route path="application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path="rate/:id" element={<PrivateRoute><SelectRating parentRoute={path} /></PrivateRoute>} />
          <Route path="rate-view/:id" element={<PrivateRoute><RateView parentRoute={path} /></PrivateRoute>} />
          <Route path="response" element={<PrivateRoute><Response parentRoute={path}  /></PrivateRoute>} />
          <Route path="dso-dashboard" element={<PrivateRoute><DsoDashboard parentRoute={path} /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default CitizenApp;
