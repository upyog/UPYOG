import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, useLocation, Routes, Route } from "react-router-dom";

const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const inboxInitialState = {
    searchParams: {
      tenantId: tenantId,
    },
  };

  const HRMSResponse = Digit?.ComponentRegistryService?.getComponent("HRMSResponse");
  const HRMSDetails = Digit?.ComponentRegistryService?.getComponent("HRMSDetails");
  const Inbox = Digit?.ComponentRegistryService?.getComponent("HRInbox");
  const CreateEmployee = Digit?.ComponentRegistryService?.getComponent("HRCreateEmployee");
  const EditEmpolyee = Digit?.ComponentRegistryService?.getComponent("HREditEmpolyee");
  return (
    <React.Fragment>
      <div className="ground-container">
        <p className="breadcrumb" style={{ marginLeft: mobileView ? "1vw" : "15px" }}>
          <Link to="/upyog-ui/employee" style={{ cursor: "pointer", color: "#666" }}>
            {t("HR_COMMON_BUTTON_HOME")}
          </Link>{" "}
          / <span>{location.pathname === "/upyog-ui/employee/hrms/inbox" ? t("HR_COMMON_HEADER") : t("HR_COMMON_HEADER")}</span>
        </p>
        <Routes>
          <Route
            path={`/inbox`}
            element={
              <PrivateRoute>
                <Inbox parentRoute={path} businessService="hrms" filterComponent="HRMS_INBOX_FILTER" initialStates={inboxInitialState} isInbox={true} />
              </PrivateRoute>
            }
          />
          <Route path={`create`} element={<PrivateRoute><CreateEmployee /></PrivateRoute>} />
          <Route path={`response`} element={<PrivateRoute><HRMSResponse parentRoute={path} /></PrivateRoute>} />
          <Route path={`details/:tenantId/:id`} element={<PrivateRoute><HRMSDetails /></PrivateRoute>} />
          <Route path={`edit/:tenantId/:id`} element={<PrivateRoute><EditEmpolyee /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
