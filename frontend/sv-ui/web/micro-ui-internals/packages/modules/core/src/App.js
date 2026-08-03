import React, { useEffect } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import EmployeeApp from "./pages/employee";
import CitizenApp from "./pages/citizen";

export const SVApp = ({ stateCode, modules, appTenants, logoUrl, initData }) => {
  const navigate = Digit.Hooks.useCustomNavigate();
  const { pathname } = useLocation();

  const innerWidth = window.innerWidth;
  const cityDetails = Digit.ULBService.getCurrentUlb() || {};
  const userDetails = Digit.UserService.getUser();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};
  let CITIZEN = userDetails?.info?.type === "CITIZEN" || !window.location.pathname.split("/").includes("employee") ? true : false;

  if (window.location.pathname.split("/").includes("employee")) CITIZEN = false;


 useEffect(() => {
    window?.scrollTo({ top: 0, left: 0, behavior: "smooth" });
  }, [pathname]);


  const handleUserDropdownSelection = (option) => {
    option.func();
  };

  const mobileView = innerWidth <= 640;
  let sourceUrl = `${window.location.origin}/citizen`;
  const commonProps = {
    stateInfo,
    userDetails,
    CITIZEN,
    cityDetails,
    mobileView,
    handleUserDropdownSelection,
    logoUrl,
    stateCode,
    modules,
    appTenants,
    sourceUrl,
    pathname,
    initData,
  };
  return (
    <Routes>
      <Route path="/sv-ui/employee/*" element={<EmployeeApp {...commonProps} />} />
      <Route path="/sv-ui/citizen/*" element={<CitizenApp {...commonProps} />} />
      <Route path="*" element={<Navigate to="/sv-ui/citizen" replace />} />
    </Routes>
  );
};
