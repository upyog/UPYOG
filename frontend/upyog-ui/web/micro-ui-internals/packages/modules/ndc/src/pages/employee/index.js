import { BreadCrumb, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
import SearchApplication from "./SearchApplication";
import { Routes, Route, useLocation } from "react-router-dom";
import Response from "./Response";

const NDCBreadCrumbs = ({ location }) => {
  const { t } = useTranslation();
  const crumbs = [
    {
      path: "/upyog-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true,
    },
    {
      path: "/upyog-ui/employee/ndc/inbox",
      content: t("ES_COMMON_INBOX"),
      show: location.pathname.includes("ndc/inbox") ? true : false,
    },
    {
      path: "/upyog-ui/employee/ndc/create",
      content: "NDC Application Create Page",
      show: location.pathname.includes("ndc/create") ? true : false,
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
  const ApplicationOverview = Digit?.ComponentRegistryService?.getComponent("NDCApplicationOverview");
  const Inbox = Digit?.ComponentRegistryService?.getComponent("NDCInbox");
  const NewNDCStepForm = Digit.ComponentRegistryService.getComponent("NewNDCStepFormEmployee");

  const isResponse = window.location.href.includes("/response");

  return (
    <Fragment>
      <div className="ground-container">
        {!isResponse ? <NDCBreadCrumbs location={location} /> : null}
        <Routes>
          <Route path="inbox/application-overview/:id" element={<PrivateRoute><ApplicationOverview /></PrivateRoute>} />
          {/* <Route path="search/application-overview/:id" element={<PrivateRoute><ApplicationOverview /></PrivateRoute>} /> */}
          <Route path="inbox/*" element={<Inbox parentRoute={path} />} />
          <Route path="create/*" element={<PrivateRoute><NewNDCStepForm parentRoute={path} /></PrivateRoute>} />
          <Route path="response/:id" element={<PrivateRoute><Response /></PrivateRoute>} />
        </Routes>
      </div>
    </Fragment>
  );
};

export default EmployeeApp;