import { PrivateRoute, BreadCrumb, AppContainer } from "@nudmcdgnpm/digit-ui-react-components";
import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Routes, Route, useLocation } from "react-router-dom";
import { ESTLinks } from "../../Module";
import SearchApp from "./SearchApp";
import ESTRegCreate from "./Create";
import ESTAssignAssetCreate from "./Create/AssignAssetIndex";

const EmployeeApp = ({ path }) => {
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

  const crumbs = [
    { path: employeeHome, content: t("ES_COMMON_HOME"), show: true },
    {
      path: `${path}/search-applications`,
      content: t("ES_COMMON_APPLICATION_SEARCH"),
      show: location.pathname.includes("est/search-applications"),
    },
    {
      path: `${path}/application-details`,
      content: t("EST_APPLICATION_DETAILS"),
      show: location.pathname.includes("est/application-details"),
    },
    {
      path: `${path}/create-asset`,
      content: t("EST_CREATE_ASSET"),
      show: location.pathname.includes("est/create-asset"),
    },
    {
      path: `${path}/assignassets`,
      content: t("EST_ASSIGN_ASSETS"),
      show: location.pathname.includes("est/assignassets"),
    },
  ];

  return (
    <AppContainer>
      <div className="ground-container" style={{ padding: isMobile ? "10px" : "20px" }}>
        <div style={{ marginLeft: isMobile ? "0" : "-4px", display: "flex", alignItems: "center" }}>
          <BreadCrumb
            style={
              isMobile
                ? { display: "flex", fontSize: "12px", padding: "5px" }
                : { margin: "0 0 4px", color: "#000000" }
            }
            spanStyle={{ maxWidth: "min-content" }}
            crumbs={crumbs}
          />
        </div>
        <Routes>
          <Route path="/*" element={<PrivateRoute><ESTLinks /></PrivateRoute>} />
          <Route path="search-applications/*" element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
          <Route path="create-asset/*" element={<PrivateRoute><ESTRegCreate parentRoute={path} /></PrivateRoute>} />
          <Route path="assignassets/*" element={<PrivateRoute><ESTAssignAssetCreate parentRoute={path} /></PrivateRoute>} />
          <Route path="application-details/:assetNo" element={<PrivateRoute><ESTApplicationDetails /></PrivateRoute>} />
        </Routes>
      </div>
    </AppContainer>
  );
};

export default EmployeeApp;
