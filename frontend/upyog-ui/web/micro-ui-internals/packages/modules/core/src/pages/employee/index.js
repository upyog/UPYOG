import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation, } from "react-router-dom";
import { AppModules } from "../../components/AppModules";
import ErrorBoundary from "../../components/ErrorBoundaries";
import TopBarSideBar from "../../components/TopBarSideBar";
import ChangePassword from "./ChangePassword";
import ForgotPassword from "./ForgotPassword";
import LanguageSelection from "./LanguageSelection";
import EmployeeLogin from "./Login";
import StaticCitizenSideBar from "../../components/TopBarSideBar/SideBar/StaticCitizenSideBar";
import LoginLayout from "../../components/LoginLayout";
import UserProfile from "../citizen/Home/UserProfile";
import ErrorComponent from "../../components/ErrorComponent";
import { PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import EmployeeDashboard from "../../components/EmployeeDashboard";
import appContent from "../../config/appContent.json";

const userScreensExempted = ["user/profile", "user/error"];
const layout = appContent.employee2.layout

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
  const { path } = Digit.Hooks.useModuleBasePath();
  const location = useLocation();
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState(false);
  const [emp, setEmp] = useState({})

  const showLanguageChange = location?.pathname?.includes("language-selection");
  const isUserProfile = userScreensExempted.some((url) => location?.pathname?.includes(url));
  const { isLoading: islinkDataLoading, data: linkData, isFetched: isLinkDataFetched } =
    Digit.Hooks.useCustomMDMS(
      Digit.ULBService.getStateId(),
      "ACCESSCONTROL-ACTIONS-TEST",
      [
        {
          name: "actions-test",
          filter: "[?(@.url == 'digit-ui-card')]",
        },
      ],
      {
        select: (data) => {
          const formattedData = data?.["ACCESSCONTROL-ACTIONS-TEST"]?.["actions-test"]
            ?.filter((el) => el.enabled === true)
            .reduce((a, b) => {
              a[b.parentModule] = a[b.parentModule]?.length > 0 ? [b, ...a[b.parentModule]] : [b];
              return a;
            }, {});
          return formattedData;
        },
      }
    );
  const isEmployeeAuthRoute =
    location?.pathname?.includes("/user/login") ||
    location?.pathname?.includes("/employee/user/login") ||
    location?.pathname?.includes("/user/forgot-password") ||
    location?.pathname?.includes("/employee/user/forgot-password") ||
    location?.pathname?.includes("/user/change-password") ||
    location?.pathname?.includes("/employee/user/change-password") ||
    location?.pathname?.includes("/user/language-selection") ||
    location?.pathname?.includes("/employee/user/language-selection");
  useEffect(() => {
    console.log("isMobile", window.Digit.Utils.browser.isMobile(), window.innerWidth)
    Digit.UserService.setType("employee");
  }, []);
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf"
  const toggleSidebarWidth = () => setIsSidebarCollapsed((prev) => !prev);
  console.log("emp",emp);
  
  return (
    <div className="employee">
      <Routes>
        <Route
          path="user/*"
          element={
            isEmployeeAuthRoute ? (
              <LoginLayout layout={layout}>
                <Routes>
                  <Route path="login" element={<EmployeeLogin layout={layout} emp={emp} setEmp={setEmp} />} />
                  <Route path="forgot-password" element={<ForgotPassword layout={layout}  emp={emp} setEmp={setEmp} />} />
                  <Route path="change-password" element={<ChangePassword layout={layout}  emp={emp} setEmp={setEmp} />} />
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
                          navigate("/upyog-ui/employee");
                        }}
                      />
                    }
                  />
                  <Route path="language-selection" element={<LanguageSelection />} />
                  <Route path="*" element={<Navigate to={`/user/language-selection`} replace />} />
                </Routes>
              </LoginLayout>
            ) : (
              <>
                {isUserProfile && (
                  <TopBarSideBar
                    t={t}
                    stateInfo={stateInfo}
                    userDetails={userDetails || {
                      access_token: "adfa",
                      info:{name: "Shubham Singh", uuid: "asdf"}
                    }}
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
                        srcset="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+(1920x1080).jpg"
                        style={{ position: "absolute", height: "100%", width: "100%" }}
                      />
                      <source
                        media="(min-width: 250px)"
                        srcset="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+%28500x900%29.jpg"
                      />
                      <img
                        src="https://nugp-assets.s3.ap-south-1.amazonaws.com/nugp+asset/Banner+UPYOG+(1920x1080).jpg"
                        alt="imagealttext"
                        style={{
                          position: "absolute",
                          height: "100%",
                          width: "100%",
                          zIndex: "1",
                          display: window.location.href.includes("user/profile") ? "none" : "",
                        }}
                      />
                    </picture>
                    <Routes>
                      <Route path="login" element={<EmployeeLogin  emp={emp} setEmp={setEmp} />} />
                      <Route path="forgot-password" element={<ForgotPassword  emp={emp} setEmp={setEmp} />} />
                      <Route path="change-password" element={<ChangePassword  emp={emp} setEmp={setEmp} />} />
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
                              navigate("/upyog-ui/employee");
                            }}
                          />
                        }
                      />
                      <Route path="language-selection" element={<LanguageSelection />} />
                      <Route path="*" element={<Navigate to={`/user/language-selection`} replace />} />
                    </Routes>
                  </div>
                </div>
              </>
            )
          }
        />
        <Route
          path="*"
          element={
            <>
              <div className="dashboard-body">
                {true && <div className={`SideBarStatic ${isSidebarCollapsed ? "SideBarStatic--collapsed" : ""}`}>
                <StaticCitizenSideBar linkData={linkData} toggleSidebarWidth={toggleSidebarWidth} isSidebarCollapsed={isSidebarCollapsed}/>
              </div>}
              <div className={`main ${DSO ? "m-auto" : ""}`}>
              <TopBarSideBar t={t}
                stateInfo={stateInfo}
                userDetails={ {access_token: "asdf", info: {
    tanentID: "pg",
    uuid: "asdf",
    name: "Shubham Singh",
    "mobileNumber": 1231231231,
    emailId: "ss@gmail.com"
  }}}
                CITIZEN={CITIZEN}
                cityDetails={cityDetails}
                mobileView={mobileView}
                handleUserDropdownSelection={handleUserDropdownSelection}
                logoUrl={logoUrl}
                showSidebar={true}
                linkData={linkData}
                toggleSidebarWidth={toggleSidebarWidth}
                isSidebarCollapsed={isSidebarCollapsed}
              />
                <div className="employee-app-wrapper">
                  {/* <ErrorBoundary initData={initData}> */}
                  <Routes>
                    <Route path="dashboard" element={
                      <EmployeeDashboard />
                    } />
                    <Route path="*" element={<AppModules stateCode={stateCode} userType="employee" modules={modules} appTenants={appTenants} emp={emp} setEmp={setEmp} />} />

                  </Routes>
                  {/* </ErrorBoundary> */}
                </div>
                {false && <div style={{ width: "100%", position: "fixed", bottom: 0, backgroundColor: "white", textAlign: "center" }}>
                  <div style={{ display: "flex", justifyContent: "center", color: "black" }}>
                    <a style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile() ? "12px" : "14px", fontWeight: "400" }} href="#" target="_blank">
                      UPYOG License
                    </a>
                    <span className="upyog-copyright-footer" style={{ margin: "0 10px", fontSize: window.Digit.Utils.browser.isMobile() ? "12px" : "14px" }}>
                      |
                    </span>
                    <span
                      className="upyog-copyright-footer"
                      style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile() ? "12px" : "14px", fontWeight: "400" }}
                      onClick={() => {
                        window.open("https://niua.in/", "_blank").focus();
                      }}
                    >
                      Copyright © 2022 National Institute of Urban Affairs
                    </span>
                  </div>
                  <div className="upyog-copyright-footer-web">
                    <span
                      className=""
                      style={{ cursor: "pointer", fontSize: window.Digit.Utils.browser.isMobile() ? "12px" : "14px", fontWeight: "400" }}
                      onClick={() => {
                        window.open("https://niua.in/", "_blank").focus();
                      }}
                    >
                      Copyright © 2022 National Institute of Urban Affairs
                    </span>
                  </div>
                </div>}
              </div>
              </div>
            </>
          }
        />
      </Routes>
    </div>
  );
};

export default EmployeeApp;