import { PrivateRoute, BreadCrumb, AppContainer } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Routes, Route, useLocation } from "react-router-dom";
import { ESTLinks } from "../../Module";
import SearchApp from "./SearchApp";
import ManageProperties from "../../components/ManageProperties";
import ESTPropertyAllotteeDetails from "../../PageComponents/ESTPropertyAllotteeDetails";
import ESTRegCreate from "./Create";
import ESTAssignAssetCreate from "./Create/AssignAssetIndex";
import AllProperties from "../../components/AllProperties";
import ESTInbox from "./Inbox";
import ESTManageProperties from "../../PageComponents/ESTManageProperties";
import ESTActions from "../../PageComponents/ESTActions";
import ManageRebate from "../../components/ManageRebate";
import ManageInterest from "../../components/ManageInterest";
import ManagePenalty from "../../components/ManagePenalty";

const EmployeeApp = ({ path, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const employeeHome = path?.replace(/\/est\/?$/, "") || "/upyog-ui/employee";
  const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);
  const ESTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ESTApplicationDetails");

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= 768);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  const inboxInitialState = {
    pageOffset: 0,
    pageSize: 10,
    sortParams: [{ id: "createdTime", desc: true }],
    searchParams: {},
  };

  const ESTBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const crumbs = [
      { path: employeeHome, content: t("ES_COMMON_HOME"), show: true },
      { path: `${path}/inbox`, content: t("INBOX"), show: location.pathname.includes("est/inbox") },
      { path: `${path}/search-applications`, content: t("ES_COMMON_APPLICATION_SEARCH"), show: location.pathname.includes("est/search-applications") },
      { path: `${path}/application-details`, content: t("EST_APPLICATION_DETAILS"), show: location.pathname.includes("est/application-details") },
      { path: `${path}/create-asset`, content: t("EST_CREATE_ASSET"), show: location.pathname.includes("est/create-asset") },
      { path: `${path}/manage-properties-table`, content: t("EST_MANAGE_PROPERTY"), show: location.pathname.includes("est/manage-properties-table") },
      { path: `${path}/actions`, content: t("EST_ACTION"), show: location.pathname.includes("est/actions") },
      { path: `${path}/manage-rebate`, content: t("EST_MANAGE_REBATE"), show: location.pathname.includes("est/manage-rebate") },
      { path: `${path}/manage-interest`, content: t("EST_MANAGE_INTEREST"), show: location.pathname.includes("est/manage-interest") },
      { path: `${path}/manage-penalty`, content: t("EST_MANAGE_PENALTY"), show: location.pathname.includes("est/manage-penalty") },
    ];
    return (
      <BreadCrumb
        style={isMobile ? { display: "flex", fontSize: "12px", padding: "5px" } : { margin: "0 0 4px", color: "#000000" }}
        spanStyle={{ maxWidth: "min-content" }}
        crumbs={crumbs}
      />
    );
  };

  return (
    <AppContainer>
      <React.Fragment>
        <div className="ground-container" style={{ padding: isMobile ? "10px" : "20px" }}>
         <div style={{ marginLeft: isMobile ? "0" : "-4px", display: "flex", alignItems: "center" }}>
            <ESTBreadCrumbs location={location} />
          </div>
          <Routes>
            <Route path="/*" element={<PrivateRoute><ESTLinks /></PrivateRoute>} />
            <Route path="property-allottee-details/*" element={<PrivateRoute><ESTPropertyAllotteeDetails /></PrivateRoute>} /> 
            <Route path="assignassets/*" element={<PrivateRoute><ESTAssignAssetCreate parentRoute={path} /></PrivateRoute>} />
            <Route path="inbox/*" element={<PrivateRoute><ESTInbox parentRoute={path} businessService="EST" initialStates={inboxInitialState} isInbox={true} filterComponent="EST_INBOX_FILTER" useNewInboxAPI={true} /></PrivateRoute>} />
            <Route path="search-applications/*" element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
            <Route path="create-asset/*" element={<PrivateRoute><ESTRegCreate parentRoute={path} /></PrivateRoute>} />
            <Route path="manage-properties/*" element={<PrivateRoute><ESTManageProperties parentRoute={path} /></PrivateRoute>} />
            <Route path="all-properties/*" element={<PrivateRoute><AllProperties t={t} parentRoute={path} /></PrivateRoute>} />
            <Route path="manage-properties-table/*" element={<PrivateRoute><ManageProperties t={t} parentRoute={path} /></PrivateRoute>} />
            <Route path="application-details/:assetNo" element={<PrivateRoute><ESTApplicationDetails /></PrivateRoute>} />
            <Route path="actions/*" element={<PrivateRoute><ESTActions /></PrivateRoute>} />
            <Route path="manage-rebate/*" element={<PrivateRoute><ManageRebate t={t} /></PrivateRoute>} />
            <Route path="manage-interest/*" element={<PrivateRoute><ManageInterest t={t} /></PrivateRoute>} />
            <Route path="manage-penalty/*" element={<PrivateRoute><ManagePenalty t={t} /></PrivateRoute>} />
          </Routes>
        </div>
      </React.Fragment>
    </AppContainer>
  );
};

export default EmployeeApp;