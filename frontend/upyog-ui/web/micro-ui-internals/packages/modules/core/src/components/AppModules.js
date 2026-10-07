import React, { useContext, useEffect } from "react";
import { Route, Routes, useLocation, Navigate } from "react-router-dom";

import { AppHome } from "./Home";
import Login from "../pages/citizen/Login";
import EmployeeLogin from "../pages/employee/Login/index";
import ChangePassword from "../pages/employee/ChangePassword/index";
import ForgotPassword from "../pages/employee/ForgotPassword/index";
import LanguageSelection from "../pages/employee/LanguageSelection";
// import UserProfile from "./userProfile";

const getTenants = (codes, tenants) => {
  return tenants.filter((tenant) => codes?.map?.((item) => item.code).includes(tenant.code));
};

import { Loader } from "@nudmcdgnpm/digit-ui-react-components";

const DSSIntegrationRedirect = () => {
  const location = useLocation();
  const rawPath = location.pathname;
  let subPath = "home";
  if (rawPath.includes("/integration/dss/")) {
    subPath = rawPath.split("/integration/dss/")[1] || "home";
  } else if (rawPath.includes("/dss/")) {
    subPath = rawPath.split("/dss/")[1] || "home";
  }
  const cleanSub = subPath.replace(/^\/+|\/+$/g, "");
  const target =
    cleanSub === "home" || cleanSub === "NURT_DASHBOARD"
      ? `/upyog-ui/employee/dss/landing/${cleanSub}`
      : `/upyog-ui/employee/dss/dashboard/${cleanSub}`;
  return <Navigate to={target} replace />;
};

const MonoUIRedirect = () => {
  const location = useLocation();
  useEffect(() => {
    const monoUrl = location.pathname.replace("/upyog-ui", "") + location.search;
    window.location.href = monoUrl;
  }, [location.pathname, location.search]);
  return <Loader />;
};

export const AppModules = ({ stateCode, userType, modules, appTenants, emp, setEmp }) => {
  const ComponentProvider = Digit.Contexts.ComponentProvider;
  const { path } = Digit.Hooks.useModuleBasePath();
  const location = useLocation();

  const user = Digit.UserService.getUser();

  if (!user || !user?.access_token || !user?.info) {
    return (
      <Navigate to="/upyog-ui/employee/user/login" state={{ from: location.pathname + location.search }} replace />
    );
  }

  const FinanceModule = Digit.ComponentRegistryService.getComponent("FinanceModule");
  const financeModuleData = modules?.find?.((m) => m.code?.toLowerCase() === "finance");
  const financeTenants = financeModuleData?.tenants;

  const appRoutes = modules.map(({ code, tenants }, index) => {
  const Module = Digit.ComponentRegistryService.getComponent(`${code}Module`);
  return Module ? (
    <Route
      key={index}
      path={`${code.toLowerCase()}/*`}
      element={<Module stateCode={stateCode} moduleCode={code} userType={userType} tenants={getTenants(tenants, appTenants)} />}
    />
  ) : (
    <Route
      key={index}
      path={`${code.toLowerCase()}`}
      element={<Navigate to="/upyog-ui/employee/user/error?type=notfound" state={{ from: location.pathname + location.search }} replace />}
    />
  );
});

return (
  <div className="ground-container">
    <Routes>
      {appRoutes}
      {FinanceModule && (
        <>
          <Route
            path="services/*"
            element={<FinanceModule stateCode={stateCode} moduleCode="Finance" userType={userType} tenants={getTenants(financeTenants, appTenants)} />}
          />
          <Route
            path="egf/*"
            element={<FinanceModule stateCode={stateCode} moduleCode="Finance" userType={userType} tenants={getTenants(financeTenants, appTenants)} />}
          />
        </>
      )}
      <Route path="integration/dss/home" element={<Navigate to="/upyog-ui/employee/dss/landing/home" replace />} />
      <Route path="integration/dss/NURT_DASHBOARD" element={<Navigate to="/upyog-ui/employee/dss/landing/NURT_DASHBOARD" replace />} />
      <Route path="integration/dss/*" element={<DSSIntegrationRedirect />} />
      <Route path="payment/integration/dss/*" element={<DSSIntegrationRedirect />} />
      <Route path="mdms/*" element={<MonoUIRedirect />} />
      <Route path="login" element={<Navigate to="/upyog-ui/employee/user/login" state={{ from: location.pathname + location.search }} replace />} />
      <Route path="forgot-password" element={<ForgotPassword  emp={emp} setEmp={setEmp}  />} />
      <Route path="change-password" element={<ChangePassword  emp={emp} setEmp={setEmp}  />} />
      <Route path="*" element={<AppHome userType={userType} modules={modules} />} />
    </Routes>
  </div>
);

};
