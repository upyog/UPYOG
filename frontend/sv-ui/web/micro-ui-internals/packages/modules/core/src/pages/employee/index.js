import React, { useEffect, Fragment } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { AppModules } from "../../components/AppModules";
import ErrorBoundary from "../../components/ErrorBoundaries";
import TopBarSideBar from "../../components/TopBarSideBar";
import ChangePassword from "./ChangePassword";
import ForgotPassword from "./ForgotPassword";
import LanguageSelection from "./LanguageSelection";
import EmployeeLogin from "./Login";
import UserProfile from "../citizen/Home/UserProfile";
import ErrorComponent from "../../components/ErrorComponent";
import { PrivateRoute } from "@nudmcdgnpm/upyog-ui-react-components-lts";

const userScreensExempted = ["user/profile", "user/error"];

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
  initData,
}) => {
  
  const navigate = Digit.Hooks.useCustomNavigate();
  
  const { t } = useTranslation();
  
  const location = useLocation();
  const showLanguageChange = location?.pathname?.includes("language-selection");
  const isUserProfile = userScreensExempted.some((url) => location?.pathname?.includes(url));
  
  useEffect(() => {
    Digit.UserService.setType("employee");
  }, []);
  
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";

  return (
    <div className="employee">
      <Routes>
         <Route path="user/*" element={
          <Fragment>
            {/* Fragment needed because we have multiple elements at root level */}
            {isUserProfile && (
              <TopBarSideBar
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
              />
            )}
            <div
              className={isUserProfile ? "grounded-container" : "loginContainer"}
              style={
                isUserProfile
                  ? { padding: 0, paddingTop: "80px", marginLeft: mobileView ? "" : "64px" }
                  : { "--banner-url": `url(${stateInfo?.bannerUrl})`, padding: "0px" }
              }
            >
              <div className="loginnn">
                <div className="login-logo-wrapper">
                  <div className="logoNiua"></div>
                </div>
                <picture>
                  <source 
                    id="backgroung-login" 
                    media="(min-width: 950px)" 
                    srcSet="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+(1920x1080).jpg" 
                    style={{"position":"absolute","height":"100%","width":"100%"}} 
                  />
                  <source 
                    media="(min-width: 250px)" 
                    srcSet="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%28500x900%29.jpg" 
                  />
                  <img 
                    src="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+(1920x1080).jpg" 
                    alt="imagealttext" 
                    style={{
                      "position":"absolute",
                      "height":"100%",
                      "width":"100%",
                      "zIndex":"1",
                      "display":window.location.href.includes("user/profile")?"none":""
                    }}
                  />
                </picture>
                
                <Routes>
                  <Route path="login" element={<EmployeeLogin />} />
                  <Route path="forgot-password" element={<ForgotPassword />} />
                  <Route path="change-password" element={<ChangePassword />} />
                  
                  <Route 
                    path="profile" 
                    element={
                      <PrivateRoute>
                        <UserProfile stateCode={stateCode} userType={"employee"} cityDetails={cityDetails} />
                      </PrivateRoute>
                    } 
                  />
                  
                  <Route 
                    path="error" 
                    element={
                      <ErrorComponent
                        initData={initData}
                        goToHome={() => {
                          navigate("/sv-ui/employee");
                        }}
                      />
                    } 
                  />
                  
                  {/* OLD: <Route path={`${path}/user/language-selection`}><LanguageSelection /></Route> */}
                  <Route path="language-selection" element={<LanguageSelection />} />
                  <Route path="*" element={<Navigate to="language-selection" replace />} />
                  
                </Routes>
                {/* OLD: </Switch> */}
              </div>
            </div>
          </Fragment>
        } />
       
        <Route path="*" element={
          <Fragment>
           
            <TopBarSideBar
              t={t}
              stateInfo={stateInfo}
              userDetails={userDetails}
              CITIZEN={CITIZEN}
              cityDetails={cityDetails}
              mobileView={mobileView}
              handleUserDropdownSelection={handleUserDropdownSelection}
              logoUrl={logoUrl}
              modules={modules}
            />
            <div className={`main ${DSO ? "m-auto" : ""}`}>
              <div className="employee-app-wrapper">
                <ErrorBoundary initData={initData}>
                  <AppModules stateCode={stateCode} userType="employee" modules={modules} appTenants={appTenants} />
                </ErrorBoundary>
              </div>
              
              {/* Footer Section - No changes needed */}
              <div style={{ width: '100%', position: 'fixed', bottom: 0, backgroundColor:"white", textAlign:"center" }}>
                <div style={{ display: 'flex', justifyContent: 'center', color:"black" }}>
                  <a 
                    style={{ 
                      cursor: "pointer", 
                      fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", 
                      fontWeight: "400"
                    }} 
                    href="#" 
                    target='_blank'
                  >
                    UPYOG License
                  </a>

                  <span className="upyog-copyright-footer" style={{ margin: "0 10px", fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px" }}>|</span>
                  <span 
                    className="upyog-copyright-footer" 
                    style={{ 
                      cursor: "pointer", 
                      fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", 
                      fontWeight: "400"
                    }} 
                    onClick={() => { window.open('https://niua.in/', '_blank').focus(); }}
                  >
                    Copyright © 2022 National Institute of Urban Affairs
                  </span>
                </div>
                <div className="upyog-copyright-footer-web">
                  <span 
                    className="" 
                    style={{ 
                      cursor: "pointer", 
                      fontSize: window.Digit.Utils.browser.isMobile()?"12px":"14px", 
                      fontWeight: "400"
                    }} 
                    onClick={() => { window.open('https://niua.in/', '_blank').focus(); }}
                  >
                    Copyright © 2022 National Institute of Urban Affairs
                  </span>
                </div>
              </div>
            </div>
          </Fragment>
        } />
       
      </Routes>
    </div>
  );
};
export default EmployeeApp;
