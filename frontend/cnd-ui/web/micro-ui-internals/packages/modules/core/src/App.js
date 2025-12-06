import React, { useEffect } from "react";
import { Redirect, Route, Switch, useHistory, useLocation } from "react-router-dom";
import EmployeeApp from "./pages/employee";
import CitizenApp from "./pages/citizen";

export const CndApp = ({ stateCode, modules, appTenants, logoUrl, initData }) => {
  const history = useHistory();
  const { pathname } = useLocation();
  const innerWidth = window.innerWidth;
  const cityDetails = Digit.ULBService.getCurrentUlb();
  const userDetails = Digit.UserService.getUser();
  const { data: storeData } = Digit.Hooks.useStore.getInitData();
  const { stateInfo } = storeData || {};
  let CITIZEN = userDetails?.info?.type === "CITIZEN" || !window.location.pathname.split("/").includes("employee") ? true : false;

  if (window.location.pathname.split("/").includes("employee")) CITIZEN = false;

  // useEffect(() => {
  //   if (!pathname?.includes("application-details")) {
  //     if (!pathname?.includes("inbox")) {
  //       Digit.SessionStorage.del("fsm/inbox/searchParams");
  //     }
  //     if (pathname?.includes("search")) {
  //       Digit.SessionStorage.del("fsm/search/searchParams");
  //     }
  //   }
  //   if (!pathname?.includes("dss")) {
  //     Digit.SessionStorage.del("DSS_FILTERS");
  //   }
  //   if (pathname?.toString() === "/cnd-ui/employee") {
  //     Digit.SessionStorage.del("SEARCH_APPLICATION_DETAIL");
  //     Digit.SessionStorage.del("WS_EDIT_APPLICATION_DETAILS");
  //   }
  //   if (pathname?.toString() === "/cnd-ui/citizen" || pathname?.toString() === "/cnd-ui/employee") {
  //     Digit.SessionStorage.del("WS_DISCONNECTION");
  //   }
  // }, [pathname]);

  history.listen(() => {
    window?.scrollTo({ top: 0, left: 0, behavior: "smooth" });
  });

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
    <Switch>
      <Route path="/cnd-ui/employee">
        <EmployeeApp {...commonProps} />
      </Route>
      <Route path="/cnd-ui/citizen">
        <CitizenApp {...commonProps} />
      </Route>
      <Route>
        <Redirect to="/upyog-ui/citizen" />
      </Route>
    </Switch>
  );
};
