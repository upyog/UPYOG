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
    console.log("isMobile", window.Digit.Utils.browser.isMobile(),window.innerWidth)
    Digit.UserService.setType("employee");
  }, []);
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";

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
            <div className="loginnn">
              {/* <picture>
              <source media="(min-width: 760px)" src="https://i.postimg.cc/wxnnKGtG/Banner-18-10-22-1.png" style={{"position":"absolute","height":"100%","width":"100%"}}/>
                <source media="(min-width: 400px)" srcset="https://i.postimg.cc/9Q7jT6Dd/Banner-Image-2.png" style={{"position":"absolute","height":"100%","width":"100%"}}/>
                </picture> */}
                 <div className="login-logo-wrapper">
              <div className="logoNiua">
                <div style={{display:"flex",flexDirection:"column",width:"100%",height:"100%"}}>
              <img src="https://i.postimg.cc/d1nSHQ1K/UPYOG-Logo-removebg-preview.png" style={{"position":"relative", zIndex:"999",width:"15%",height:"15%",marginLeft:"auto",marginRight:"auto",marginTop:"15px"}}/>
        
              </div>
                </div>
                <div className="loginConference">
                  <img id="login-conference-table" src="https://i.postimg.cc/dt1C7cXf/Banner-Image-1-removebg-preview.png" ></img>
                  <div className="login-banner-wrapper">
                    {window.innerWidth >950?<div className="psuedo-banner"><span>U</span>rban <span>P</span>latform for<br />
                      deliver<span>Y</span> of <span>O</span>nline<br />
                      <span>G</span>overnance</div>:<div className="psuedo-banner" style={{textAlign:"center",position:"absolute",width:"100%",bottom:window.innerWidth <700?"55%":window.innerWidth>800&&window.innerWidth<950?"70%":""}}><span>U</span>rban <span>P</span>latform for deliver<span>Y</span><br />of <span>O</span>nline <span>G</span>overnance</div>}
                  <div className="banner-slogan" style={{position:window.innerWidth <950?"absolute":"",bottom:window.innerWidth <950?"35%":"",textAlign:window.innerWidth <950?"center":"",width:window.innerWidth<950?"100%":""}}>A digital platform for urban<br/>
                            citizen services</div>
                  </div>
                </div>
                </div>
              <picture>
                <source id="backgroung-login" media="(min-width: 950px)" srcset="https://i.postimg.cc/PrVjVmDf/Banner-Image-Desktop.png" style={{"position":"absolute","height":"100%","width":"100%"}} />
                  <source media="(min-width: 300px)" srcset="https://i.postimg.cc/MpF64xnV/Banner-Background-for-Mobile-App.png" />
                    <img src="https://i.postimg.cc/PrVjVmDf/Banner-Image-Desktop.png" alt="imagealttext" style={{"position":"absolute","height":"100%","width":"100%"}}/>
                    </picture>
              {/* <img class="image" id="main-img" src="https://i.postimg.cc/PrVjVmDf/Banner-Image-Desktop.png" /> */}
              {/* <img class="image" id="main-img" src="https://i.postimg.cc/9Q7jT6Dd/Banner-Image-2.png" /> */}
                {/* <img id="backgroung-login" src="https://i.postimg.cc/PrVjVmDf/Banner-Image-Desktop.png" style={{"position":"absolute","height":"100%","width":"100%"}}></img> */}
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
              </ErrorBoundary>
            </div>
            {/* <div className="footerr" style={{ width: '100%', bottom: 0,backgroundColor:"white",color:"black !important"}}>
              <div style={{ display: 'flex', justifyContent: 'center', color:"color","backgroundColor":"#808080b3"  }}>
                <img style={{ cursor: "pointer", display: "inline-flex", height: '1.4em' }} alt={"Powered by DIGIT"} src={`${sourceUrl}/digit-footer.png`} onError={"this.src='./../digit-footer.png'"} onClick={() => {
                  window.open('https://www.digit.org/', '_blank').focus();
                }}></img>
                <span style={{ margin: "0 10px" }}>|</span>
                <span style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} onClick={() => { window.open('https://niua.in/', '_blank').focus();}} >Copyright © 2022 National Institute of Urban Affairs</span>
                <span style={{ margin: "0 10px" }}>|</span>
                <a style={{ cursor: "pointer", fontSize: "16px", fontWeight: "400"}} href="#" target='_blank'>UPYOG License</a>
              </div>
            </div> */}
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
        </Route>
        <Route>
          <Redirect to={`${path}/user/language-selection`} />
        </Route>
      </Switch>
    </div>
  );
};

export default EmployeeApp;
