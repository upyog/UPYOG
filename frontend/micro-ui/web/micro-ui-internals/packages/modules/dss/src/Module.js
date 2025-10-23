import React, { Fragment } from "react";
import { useTranslation } from "react-i18next";
// import { useRouteMatch } from "react-router";
import { BackButton, Loader, PrivateRoute, BreadCrumb } from "@upyog/digit-ui-react-components";
import DashBoard from "./pages";
import Home from "./pages/Home";
import { Route, Switch, useRouteMatch, useLocation } from "react-router-dom";
import Overview from "./pages/Overview";
import {checkCurrentScreen, DSSCard,NDSSCard} from "./components/DSSCard";
import DrillDown from "./pages/DrillDown";
import FAQsSection from "./pages/FAQs/FAQs"
import About from "./pages/About";
const DssBreadCrumb = ({ location }) => {
  const { t } = useTranslation();
  const {fromModule=false,title}= Digit.Hooks.useQueryParams();
  const moduleName=Digit.Utils.dss.getCurrentModuleName();

  const crumbs = [
    {
      path: "/digit-ui/employee",
      content: t("ES_COMMON_HOME"),
      show: true,
    },
    {
      path: checkCurrentScreen() || window.location.href.includes("NURT_DASHBOARD") ? "/digit-ui/employee/dss/landing/NURT_DASHBOARD" : "/digit-ui/employee/dss/landing/home",
      content: t("ES_LANDING_PAGE"),
      show: true,
    },
    {
      path: fromModule?`/digit-ui/employee/dss/dashboard/${fromModule}`:`/digit-ui/employee/dss/dashboard/${Digit.Utils.dss.getCurrentModuleName()}`,
      content: t(`ES_COMMON_DSS_${Digit.Utils.locale.getTransformedLocale(fromModule?fromModule:moduleName)}`),
      show: location.pathname.includes("dashboard") ? true : false,
    },
    {
      path: "/digit-ui/employee/dss/drilldown",
      content:location.pathname.includes("drilldown")?t(title): t("ES_COMMON_DSS_DRILL"),
      show: location.pathname.includes("drilldown") ? true : false,
    },
    {
      path: "/digit-ui/employee/dss/national-faqs",
      content: t("ES_COMMON_DSS_FAQS"),
      show: location.pathname.includes("national-faqs") ? true : false,
    } ,
    {
      path: "/digit-ui/employee/dss/national-about",
      content: t("ES_COMMON_DSS_ABOUT"),
      show: location.pathname.includes("national-about") ? true : false,
    } 
  ];

  return <BreadCrumb crumbs={crumbs?.filter(ele=>ele.show)} />;
};

const Routes = ({ path, stateCode }) => {
  const location = useLocation();
  const isMobile = window.Digit.Utils.browser.isMobile();
  return (
    <div className="chart-wrapper" style={isMobile ? {marginTop:"unset"} : {}}>
      <DssBreadCrumb location={location} />
      <Switch>
        <PrivateRoute path={`${path}/landing/:moduleCode`} component={() => <Home stateCode={stateCode} />} />
        <PrivateRoute path={`${path}/dashboard/:moduleCode`} component={() => <DashBoard stateCode={stateCode} />} />
        <PrivateRoute path={`${path}/drilldown`} component={() => <DrillDown  stateCode={stateCode}  />} />
        <Route key={"national-faq"} path={`${path}/national-faqs`}>
          <FAQsSection/>
        </Route>
        <Route key={"national-about"} path={`${path}/national-about`}>
          <About/>
        </Route>
      </Switch>
    </div>
  );
};

const DSSModule = ({ stateCode, userType, tenants }) => {
  const moduleCode = "DSS";
  // const { path, url } = useRouteMatch();
  const { path, url } = useRouteMatch();
  const language = Digit.StoreData.getCurrentLanguage();
  const { isLoading, data: store } = Digit.Services.useStore({ stateCode, moduleCode, language });

  if (isLoading) {
    return <Loader />;
  }

  Digit.SessionStorage.set("DSS_TENANTS", tenants);

  if (userType !== "citizen") {
    return <Routes path={path} stateCode={stateCode} />;
  }
};

const componentsToRegister = {
  DSSModule,
  DSSCard,
  NDSSCard
};

export const initDSSComponents = () => {
  Object.entries(componentsToRegister).forEach(([key, value]) => {
    Digit.ComponentRegistryService.setComponent(key, value);
  });
};
