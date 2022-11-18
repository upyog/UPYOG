import { BackButton } from "@egovernments/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import ErrorBoundary from "../../components/ErrorBoundaries";
import { AppHome } from "../../components/Home";
import TopBarSideBar from "../../components/TopBarSideBar";
import CitizenHome from "./Home";
import LanguageSelection from "./Home/LanguageSelection";
import LocationSelection from "./Home/LocationSelection";
import Login from "./Login";
import UserProfile from "./Home/UserProfile";
// import PDF from "../../assets/";

const getTenants = (codes, tenants) => {
  return tenants.filter((tenant) => codes.map((item) => item.code).includes(tenant.code));
};

const Home = ({
  stateInfo,
  userDetails,
  CITIZEN,
  cityDetails,
  mobileView,
  handleUserDropdownSelection,
  logoUrl,
  DSO,
  stateCode,
  modules,
  appTenants,
  sourceUrl,
  pathname,
}) => {
  const classname = Digit.Hooks.fsm.useRouteSubscription(pathname);
  const { t } = useTranslation();
  const { path } = useRouteMatch();
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";

  const appRoutes = modules.map(({ code, tenants }, index) => {
    const Module = Digit.ComponentRegistryService.getComponent(`${code}Module`);
    return (
      <Route key={index} path={`${path}/${code.toLowerCase()}`}>
        <Module stateCode={stateCode} moduleCode={code} userType="citizen" tenants={getTenants(tenants, appTenants)} />
      </Route>
    );
  });

  const ModuleLevelLinkHomePages = modules.map(({ code, bannerImage }, index) => {
    let Links = Digit.ComponentRegistryService.getComponent(`${code}Links`) || (() => <React.Fragment />);
    return (
      <Route key={index} path={`${path}/${code.toLowerCase()}-home`}>
        <div className="moduleLinkHomePage">
          <img src={bannerImage || stateInfo?.bannerUrl} alt="noimagefound" />
          <BackButton className="moduleLinkHomePageBackButton" />
          <h1>{t("MODULE_" + code.toUpperCase())}</h1>
        </div>
        <div className="moduleLinkHomePageModuleLinks">
          <Links key={index} matchPath={`/digit-ui/citizen/${code.toLowerCase()}`} userType={"citizen"} />
        </div>
      </Route>
    );
  });

  return (
    <div className={classname}>
      <TopBarSideBar
        t={t}
        stateInfo={stateInfo}
        userDetails={userDetails}
        CITIZEN={CITIZEN}
        cityDetails={cityDetails}
        mobileView={mobileView}
        handleUserDropdownSelection={handleUserDropdownSelection}
        logoUrl={logoUrl}
        showSidebar={true}
      />

      <div className={`main center-container mb-25`}>
        <Switch>
          <Route exact path={path}>
            <CitizenHome />
          </Route>

          <Route exact path={`${path}/select-language`}>
            <LanguageSelection />
          </Route>

          <Route exact path={`${path}/select-location`}>
            <LocationSelection />
          </Route>

          <Route path={`${path}/all-services`}>
            <AppHome userType="citizen" modules={modules} />
          </Route>

          <Route path={`${path}/login`}>
            <Login stateCode={stateCode} />
          </Route>

          <Route path={`${path}/register`}>
            <Login stateCode={stateCode} isUserRegistered={false} />
          </Route>

          <Route path={`${path}/user/profile`}>
            <UserProfile stateCode={stateCode} userType={"citizen"} cityDetails={cityDetails} />
          </Route>

          <ErrorBoundary>
            {appRoutes}
            {ModuleLevelLinkHomePages}
          </ErrorBoundary>
        </Switch>
      </div>
      <div style={{ width: '100%', position: 'fixed', bottom: 0,backgroundColor:"white",textAlign:"center" }}>
        <div style={{ display: 'flex', justifyContent: 'center', color:"black" }}>
          <span style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} onClick={() => { window.open('https://www.digit.org/', '_blank').focus();}} >Powered by DIGIT</span>
          <span style={{ margin: "0 10px" ,fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px"}}>|</span>
          <a style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a>

          <span  className="upyog-copyright-footer" style={{ margin: "0 10px",fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px" }} >|</span>
          <span  className="upyog-copyright-footer" style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
          
          {/* <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a> */}
        </div>
        <div className="upyog-copyright-footer-web">
          <span className="" style={{ cursor: "pointer", fontSize:  window.Digit.Utils.browser.isMobile()?"12px":"14px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
          </div>
      </div>
    </div>
  );
};

export default Home;
