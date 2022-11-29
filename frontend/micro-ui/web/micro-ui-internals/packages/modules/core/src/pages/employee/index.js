import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Redirect, Route, Switch, useLocation, useRouteMatch } from "react-router-dom";
import { AppModules } from "../../components/AppModules";
import ErrorBoundary from "../../components/ErrorBoundaries";
import TopBarSideBar from "../../components/TopBarSideBar";
import ChangePassword from "./ChangePassword";
import ForgotPassword from "./ForgotPassword";
import LanguageSelection from "./LanguageSelection";
import EmployeeLogin from "./Login";
import UserProfile from "../citizen/Home/UserProfile";

const EmployeeApp = ({
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
  const { t } = useTranslation();
  const { path } = useRouteMatch();
  const location = useLocation();
  const showLanguageChange = location?.pathname?.includes("language-selection");
  const isUserProfile = location?.pathname?.includes("user/profile");
  useEffect(() => {
    Digit.UserService.setType("employee");
  }, []);
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf"

  return (
    <div className="employee">
      <Switch>
        <Route path={`${path}/user`}>
          {isUserProfile&&<TopBarSideBar
            t={t}
            stateInfo={stateInfo}
            userDetails={userDetails}
            CITIZEN={CITIZEN}
            cityDetails={cityDetails}
            mobileView={mobileView}
            handleUserDropdownSelection={handleUserDropdownSelection}
            logoUrl={logoUrl}
            showSidebar={isUserProfile ? true : false}
            showLanguageChange={!showLanguageChange}
          />}
          <div
            className={isUserProfile ? "grounded-container" : "loginContainer"}
            style={
              isUserProfile
                ? { padding: 0, paddingTop: "80px", marginLeft: mobileView ? "" : "64px" }
                : { "--banner-url": `url(${stateInfo?.bannerUrl})` ,padding:"0px"}
            }
          >
            <Switch>
              <Route path={`${path}/user/login`}>
                <EmployeeLogin />
              </Route>
              <Route path={`${path}/user/forgot-password`}>
                <ForgotPassword />
              </Route>
              <Route path={`${path}/user/change-password`}>
                <ChangePassword />
              </Route>
              <Route path={`${path}/user/profile`}>
                <UserProfile stateCode={stateCode} userType={"employee"} cityDetails={cityDetails} />
              </Route>
              <Route path={`${path}/user/language-selection`}>
                <LanguageSelection />
              </Route>
              <Route>
                <Redirect to={`${path}/user/language-selection`} />
              </Route>
            </Switch>
          </div>
        </Route>
        <Route>
          <TopBarSideBar
            t={t}
            stateInfo={stateInfo}
            userDetails={userDetails}
            CITIZEN={CITIZEN}
            cityDetails={cityDetails}
            mobileView={mobileView}
            handleUserDropdownSelection={handleUserDropdownSelection}
            logoUrl={logoUrl}
          />
          <div className={`main ${DSO ? "m-auto" : ""}`}>
            <div className="employee-app-wrapper">
              <ErrorBoundary>
                <AppModules stateCode={stateCode} userType="employee" modules={modules} appTenants={appTenants} />
                {/* <AppModules /> */}
              </ErrorBoundary>
            </div>
            {/* <div style={{ width: '100%', bottom: 0 }}>
              <div style={{ display: 'flex', justifyContent: 'center', color:"#22394d" }}>
                <img style={{ cursor: "pointer", display: "inline-flex", height: '1.4em' }} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
                  window.open('https://www.digit.org/', '_blank').focus();
                }}></img>
                <span style={{ margin: "0 10px" }}>|</span>
                <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
                <span style={{ margin: "0 10px" }}>|</span>
                <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href={pdfUrl} target='_blank'>UPYOG License</a>
              </div>
            </div> */}
          </div>
        </Route>
        <Route>
          <Redirect to={`${path}/user/language-selection`} />
        </Route>
      </Switch>
    </div>
  );
};

export default EmployeeApp;
