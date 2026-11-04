import React from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";

import ChangePassword from "../pages/employee/ChangePassword/index";
import ForgotPassword from "../pages/employee/ForgotPassword/index";
import { AppHome } from "./Home";
// import UserProfile from "./userProfile";

const getTenants = (codes, tenants) => {
  return tenants.filter((tenant) => codes?.map?.((item) => item.code).includes(tenant.code));
};

export const AppModules = ({ stateCode, userType, modules, appTenants }) => {
  const ComponentProvider = Digit.Contexts.ComponentProvider;
  const location = useLocation();

  const user = Digit.UserService.getUser();

  if (!user || !user?.access_token || !user?.info) {
    return <Navigate to={`/${window?.contextPath}/employee/user/login`} state={{ from: location.pathname + location.search }} replace />;
  }

  return (
    <div className="ground-container">
      <Routes>
        {modules.map(({ code, tenants }, index) => {
          const Module = Digit.ComponentRegistryService.getComponent(`${code}Module`);
          return Module ? (
            <Route key={index} path={`${code.toLowerCase()}/*`} element={
              <Module stateCode={stateCode} moduleCode={code} userType={userType} tenants={getTenants(tenants, appTenants)} />
            } />
          ) : (
            <Route key={index} path={`${code.toLowerCase()}/*`} element={
              <Navigate to={`/${window?.contextPath}/employee/user/error?type=notfound&module=${code}`} replace />
            } />
          );
        })}
        <Route path="login" element={
          <Navigate to={`/${window?.contextPath}/employee/user/login`} state={{ from: location.pathname + location.search }} replace />
        } />
        <Route path="forgot-password" element={<ForgotPassword />} />
        <Route path="change-password" element={<ChangePassword />} />
        <Route path="*" element={<AppHome modules={modules} />} />
        {/* <Route path="user-profile" element={<UserProfile />} /> */}
      </Routes>
    </div>
  );
};
