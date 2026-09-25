import React, { useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Navigate, Route, Routes, useLocation, useResolvedPath } from "react-router-dom";
import { AppModules } from "../../components/AppModules";
import ErrorBoundary from "../../components/ErrorBoundaries";
import TopBarSideBar from "../../components/TopBarSideBar";
import ChangePassword from "./ChangePassword";
import ForgotPassword from "./ForgotPassword";
import LanguageSelection from "./LanguageSelection";
import EmployeeLogin from "./Login";
import UserProfile from "./UserProfile";
import ErrorComponent from "../../components/ErrorComponent";
import { PrivateRoute } from "@upyog/workbench-ui-react-components";

const userScreensExempted = ["user/profile", "user/error"];

const EmployeeApp = ({
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
}) => {
  const navigate = Digit.Hooks.useCustomNavigate();                    
  const { t } = useTranslation();
  const { pathname: path } = useResolvedPath(".");   // useRouteMatch → useResolvedPath

  const location = useLocation();
  const showLanguageChange = location?.pathname?.includes("language-selection");
  const isUserProfile = userScreensExempted.some((url) => location?.pathname?.includes(url));
  useEffect(() => {
    Digit.UserService.setType("employee");
  }, []);

  return (
    <div className="employee">
      <Routes>
        <Route
          path="user/*"                              // relative + /* for nested routes
          element={
            <>
              {isUserProfile && (
                <TopBarSideBar
                  t={t}
                  stateInfo={stateInfo}
                  userDetails={userDetails}
                  cityDetails={cityDetails}
                  mobileView={mobileView}
                  handleUserDropdownSelection={handleUserDropdownSelection}
                  logoUrl={logoUrl}
                  showSidebar={isUserProfile}
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
                <Routes>
                  {/* children → element prop, relative paths */}
                  <Route path="login" element={<EmployeeLogin />} />
                  <Route path="forgot-password" element={<ForgotPassword />} />
                  <Route path="change-password" element={<ChangePassword />} />
                  <Route
                    path="profile"
                    element={
                      <PrivateRoute
                        element={<UserProfile stateCode={stateCode} userType={"employee"} cityDetails={cityDetails} />}
                      />
                    }
                  />
                  <Route
                    path="error"
                    element={
                      <ErrorComponent
                        initData={initData}
                        goToHome={() => {
                          navigate(`/${window?.contextPath}/${Digit?.UserService?.getType?.()}`); // history.push → navigate
                        }}
                      />
                    }
                  />
                  <Route path="language-selection" element={<LanguageSelection />} />
                  {/* Redirect → Navigate */}
                  <Route path="*" element={<Navigate to={`${path}/user/language-selection`} replace />} />
                </Routes>
              </div>
            </>
          }
        />

        <Route
          path="*"
          element={
            <>
              <TopBarSideBar
                t={t}
                stateInfo={stateInfo}
                userDetails={userDetails}
                cityDetails={cityDetails}
                mobileView={mobileView}
                handleUserDropdownSelection={handleUserDropdownSelection}
                logoUrl={logoUrl}
                modules={modules}
              />
              <div className="main">
                <div className="employee-app-wrapper">
                  <ErrorBoundary initData={initData}>
                    <AppModules stateCode={stateCode} userType="employee" modules={modules} appTenants={appTenants} />
                  </ErrorBoundary>
                </div>
                <div className="employee-home-footer" />
              </div>
            </>
          }
        />
      </Routes>
    </div>
  );
};

export default EmployeeApp;
