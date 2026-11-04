import React, { useContext, useEffect } from "react";
import { Route, Routes, Navigate, useLocation, useParams } from "react-router-dom";

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

export const AppModules = ({ stateCode, userType, modules, appTenants }) => {
  const ComponentProvider = Digit.Contexts.ComponentProvider;
  
  const location = useLocation();

  const user = Digit.UserService.getUser();

  if (!user || !user?.access_token || !user?.info) {
    return <Navigate to="/sv-ui/employee/user/login" state={{ from: location.pathname + location.search }} replace />;
  }

  const appRoutes = modules.map(({ code, tenants }, index) => {
    const Module = Digit.ComponentRegistryService.getComponent(`${code}Module`);
    return Module ? (
      <Route 
        key={index} 
        path={`${code.toLowerCase()}/*`}
        element={
          <Module 
            stateCode={stateCode} 
            moduleCode={code} 
            userType={userType} 
            tenants={getTenants(tenants, appTenants)} 
          />
        }
      />
    ) : (
      <Route 
        key={index} 
        path={`${code.toLowerCase()}/*`}
        element={
          <Navigate 
            to="/sv-ui/employee/user/error?type=notfound" 
            state={{ from: location.pathname + location.search }} 
            replace 
          />
        }
      />
    );
  });

  return (
    <div className="ground-container">
      <Routes>
        {appRoutes}
        <Route path="login" element={
            <Navigate 
              to="/sv-ui/employee/user/login" 
              state={{ from: location.pathname + location.search }} 
              replace 
            />
          } />
        <Route path="forgot-password" element={<ForgotPassword />} />
        <Route path="change-password" element={<ChangePassword />} />
        <Route path="*" element={<AppHome userType={userType} modules={modules} />} />
      </Routes>
    </div>
  );
};
