/**
 * Main sub-router and navigation controller for the NOC Employee module.
 * Renders breadcrumb navigation and handles routes for Inbox, Application Overview, Search, and Response pages.
 */
import { BreadCrumb, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import SearchApplication from "./SearchApplication";
import { Routes, Route, useLocation } from "react-router-dom";
import Response from "./Response";
import SearchApp from "./SearchApp";

const NOCBreadCrumbs = ({ location }) => {
  const { t } = useTranslation();
  const crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true,
    },
    {
      path: "/upyog-ui/employee/noc/inbox",
      content: t("ES_COMMON_INBOX"),
      show: location.pathname.includes("noc/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/noc/inbox/application-overview/:id",
      content: t("NOC_APP_OVER_VIEW_HEADER"),
      show: location.pathname.includes("noc/inbox/application-overview") ? true : false,
    },
    {
      path: "/upyog-ui/employee/noc/search",
      content: t("ES_COMMON_APPLICATION_SEARCH"),
      show: location.pathname.includes("/upyog-ui/employee/noc/search") ? true : false,
    },
    {
      path: "/upyog-ui/employee/noc/search/application-overview/:id",
      content: t("NOC_APP_OVER_VIEW_HEADER"),
      show: location.pathname.includes("/upyog-ui/employee/noc/search/application-overview") ? true : false,
    },
  ];
  return <BreadCrumb crumbs={crumbs} />;
};

const EmployeeApp = ({ path }) => {
  const location = useLocation();
  const { t } = useTranslation();
  const ApplicationOverview = Digit?.ComponentRegistryService?.getComponent("NOCApplicationOverview");
  const Inbox = Digit?.ComponentRegistryService?.getComponent("NOCInbox");
  const FireNocInbox = Digit?.ComponentRegistryService?.getComponent("FireNocInbox");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const isResponse = window.location.href.includes("/response");
  const isMobile = window.Digit.Utils.browser.isMobile();
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["FIRENOC"],
      applicationStatus: [],
      locality: [],

    },
  };

  return (
    <Fragment>
      {!isResponse ? <div style={window.location.href.includes("application-overview") || isMobile ? { marginLeft: "10px" } : {}}>
        <NOCBreadCrumbs location={location} />
      </div> : null} 
      <Routes>
        <Route
            path= "firenoc/inbox/*"
            element={
              <PrivateRoute>
                <FireNocInbox
                  useNewInboxAPI={true}
                  businessService="FIRENOC"
                  filterComponent="FIRENOC_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                  parentRoute={path}
                />
              </PrivateRoute>
            }
          />
        <Route path="inbox/application-overview/:id" element={<PrivateRoute><ApplicationOverview /></PrivateRoute>} />
        <Route path="search/application-overview/:id" element={<PrivateRoute><ApplicationOverview /></PrivateRoute>} />
        <Route path="inbox" element={<PrivateRoute><Inbox parentRoute={path} /></PrivateRoute>} />
        <Route path="search" element={<PrivateRoute><SearchApplication parentRoute={path} /></PrivateRoute>} />
        <Route path="response" element={<PrivateRoute><Response /></PrivateRoute>} />
        <Route path= "firenoc/application-overview/:id" element={<PrivateRoute><ApplicationDetails /></PrivateRoute>} />
        <Route path= "firenoc/my-applications/*" element={<PrivateRoute><SearchApp /></PrivateRoute>} />
      </Routes>
    </Fragment>
  );
};

export default EmployeeApp;
