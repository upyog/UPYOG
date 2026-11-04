import React, { useEffect } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import EmployeeApp from "./pages/employee";

export const DigitApp = ({ stateCode, modules, appTenants, logoUrl, initData }) => {
  const { pathname } = useLocation();
  const innerWidth = window.innerWidth;
  const cityDetails = Digit.ULBService.getCurrentUlb();
  const userDetails = Digit.UserService.getUser();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};

  useEffect(() => {
    if (!pathname?.includes("application-details")) {
      if (!pathname?.includes("inbox")) {
        Digit.SessionStorage.del("fsm/inbox/searchParams");
      }
      if (pathname?.includes("search")) {
        Digit.SessionStorage.del("fsm/search/searchParams");
      }
    }
    if (!pathname?.includes("dss")) {
      Digit.SessionStorage.del("DSS_FILTERS");
    }
    if (pathname?.toString() === `/${window?.contextPath}/employee`) {
      Digit.SessionStorage.del("SEARCH_APPLICATION_DETAIL");
      Digit.SessionStorage.del("WS_EDIT_APPLICATION_DETAILS");
      Digit.SessionStorage.del("WS_DISCONNECTION");
    }
  }, [pathname]);

  useEffect(() => {
    window?.scrollTo({ top: 0, left: 0, behavior: "smooth" });
  }, [pathname]);

  const handleUserDropdownSelection = (option) => {
    option.func();
  };

  const mobileView = innerWidth <= 640;
  const commonProps = {
    stateInfo,
    userDetails,
    cityDetails,
    mobileView,
    handleUserDropdownSelection,
    logoUrl,
    stateCode,
    modules,
    appTenants,
    pathname,
    initData,
  };
  return (
    <Routes>
      <Route path={`/${window?.contextPath}/employee/*`} element={<EmployeeApp {...commonProps} />} />
      <Route path="*" element={<Navigate to={`/${window?.contextPath}/employee`} replace />} />
    </Routes>
  );
};
