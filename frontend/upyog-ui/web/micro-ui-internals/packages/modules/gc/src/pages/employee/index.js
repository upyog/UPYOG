import { AppContainer, BreadCrumb, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";

const GCBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const crumbs = [
    {
      path: `/upyog-ui/employee`,
      content: t("ES_COMMON_HOME"),
      show: true,
    },
    {
      path: `/upyog-ui/employee/gc/inbox`,
      content: t("ES_COMMON_INBOX"),
      show: location.pathname.includes("inbox"),
    },
    {
      path: `/upyog-ui/employee/gc/application-details`,
      content: t("ES_GC_APPLICATION_DETAILS"),
      show: location.pathname.includes("application-details"),
    }
  ];

  return <BreadCrumb crumbs={crumbs} spansFirst={true} />;
};

const EmployeeApp = ({ path, url, userType }) => {
  const location = useLocation();
  const { t } = useTranslation();

  // Dynamically load components from the registry
  const Inbox = Digit.ComponentRegistryService.getComponent("GCInbox");  
  const ApplicationDetails = Digit.ComponentRegistryService.getComponent("GCApplicationDetails_Employee");
  const Search = Digit.ComponentRegistryService.getComponent("GCSearchApp");
  const Create = Digit.ComponentRegistryService.getComponent("GCCreateEmp");

  return (
      <AppContainer>
        <div className="ground-container">
          <div className="breadcrumb-title" style={{ marginLeft: "15px" }}>
            <GCBreadCrumb location={location} t={t} />
          </div>
          <Routes>
            {/* When the user enters the /gc URL, they are automatically redirected to the inbox */}
            <Route path="inbox" element={<PrivateRoute><Inbox key={location.pathname} parentUrl={url} /></PrivateRoute>} />
            <Route path="application-details/:applicationNo/*" element={<PrivateRoute><ApplicationDetails key={location.pathname} parentUrl={url} /></PrivateRoute>} />
            <Route path="search" element={<PrivateRoute><Search /></PrivateRoute>} />
            <Route path="new-application/*" element={<PrivateRoute><Create parentUrl={url} /></PrivateRoute>} />
            <Route path="bills" element={<div style={{ padding: "16px" }}><h1>{t("GC_BILLS")}</h1><p>{t("UNDER_CONSTRUCTION")}</p></div>} />
            {/* Default route to redirect to inbox */}
            <Route path="*" element={<Navigate to="inbox" replace />} />
          </Routes>
        </div>
      </AppContainer>
  );
};

export default EmployeeApp;