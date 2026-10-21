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
import EmployeeSideBar from "../../components/TopBarSideBar/SideBar/EmployeeSideBar";
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
  const [isMobileSidebarOpen, setIsMobileSidebarOpen] = useState(false);
  const [emp, setEmp] = useState({})

  const showLanguageChange = location?.pathname?.includes("language-selection");
  const isUserProfile = userScreensExempted.some((url) => location?.pathname?.includes(url));

  useEffect(() => {
    setIsMobileSidebarOpen(false);
  }, [location.pathname]);
  const { isLoading: islinkDataLoading, data: linkData, isFetched: isLinkDataFetched } =
    Digit.Hooks.useCustomMDMS(
      Digit.ULBService.getStateId(),
      "ACCESSCONTROL-ACTIONS-TEST",
      [
        {
          name: "actions-test",
        },
      ],
      {
        select: (data) => {
          const formattedData = data?.["ACCESSCONTROL-ACTIONS-TEST"]?.["actions-test"]
            ?.filter((el) => el.enabled === true)
            .reduce((a, b) => {
              const mod = b.parentModule || b.displayName || b.path?.split(".")?.[0] || "OTHER";
              a[mod] = a[mod]?.length > 0 ? [...a[mod], b] : [b];
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
    console.log("isMobile", window.Digit.Utils.browser.isMobile(), window.innerWidth);
    Digit.UserService.setType("employee");
    const { commonConfig = {} } = layout || {};
    const vars = {
      "--app-btn-primary-gradient": commonConfig?.gradient || "linear-gradient(90deg, #6A4A91 0%, #3E285F 100%)",
      "--app-layout-primary-background": commonConfig?.commonBackground || "#F3F2FA",
      "--app-layout-primary-color": commonConfig?.commonColor || "#7F7797",
    };

    Object.entries(vars).forEach(([k, v]) => {
      if (v) document.documentElement.style.setProperty(k, v);
    });

    return () => {
      Object.keys(vars).forEach((k) => document.documentElement.style.removeProperty(k));
    };
  }, [layout?.commonConfig]);
  sourceUrl = "https://s3.ap-south-1.amazonaws.com/egov-qa-assets";
  const pdfUrl = "https://pg-egov-assets.s3.ap-south-1.amazonaws.com/Upyog+Code+and+Copyright+License_v1.pdf";
  const toggleSidebarWidth = () => {
    if (window.innerWidth < 780 || window.Digit.Utils.browser.isMobile()) {
      setIsMobileSidebarOpen((prev) => !prev);
    } else {
      setIsSidebarCollapsed((prev) => !prev);
    }
  };
  console.log("emp", emp);

  return (
    <div className="employee">
      <Routes>
        <Route
          path="user/login"
          element={
            <LoginLayout layout={layout}>
              <EmployeeLogin layout={layout} emp={emp} setEmp={setEmp} />
            </LoginLayout>
          }
        />
        <Route
          path="user/forgot-password"
          element={
            <LoginLayout layout={layout}>
              <ForgotPassword layout={layout} emp={emp} setEmp={setEmp} />
            </LoginLayout>
          }
        />
        <Route
          path="user/change-password"
          element={
            <LoginLayout layout={layout}>
              <ChangePassword layout={layout} emp={emp} setEmp={setEmp} />
            </LoginLayout>
          }
        />
        <Route
          path="user/language-selection"
          element={<Navigate to="/upyog-ui/employee/user/login" replace />}
        />
        <Route
          path="*"
          element={
            <>
              <div className="dashboard-body">
                <div
                  className={`SideBarStatic ${isSidebarCollapsed ? "SideBarStatic--collapsed" : ""} ${isMobileSidebarOpen ? "SideBarStatic--mobile-open" : ""
                    }`}
                >
                  <EmployeeSideBar
                    linkData={linkData}
                    toggleSidebarWidth={toggleSidebarWidth}
                    isSidebarCollapsed={window.innerWidth < 780 ? false : isSidebarCollapsed}
                    closeMobileSidebar={() => setIsMobileSidebarOpen(false)}
                  />
                </div>
                {isMobileSidebarOpen && (
                  <div
                    className="sidebar-mobile-backdrop"
                    onClick={() => setIsMobileSidebarOpen(false)}
                    aria-hidden="true"
                  />
                )}
                <div className={`main ${isSidebarCollapsed ? "main--collapsed" : ""} ${DSO ? "m-auto" : ""}`}>
                  <TopBarSideBar t={t}
                    stateInfo={stateInfo}
                    userDetails={userDetails || Digit.UserService.getUser()}
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
                    <ErrorBoundary initData={initData}>
                      <Routes>
                        <Route path="dashboard" element={
                          <PrivateRoute>
                            <EmployeeDashboard />
                          </PrivateRoute>
                        } />
                        <Route
                          path="user/profile"
                          element={
                            <PrivateRoute>
                              <UserProfile stateCode={stateCode} userType={"employee"} cityDetails={cityDetails} />
                            </PrivateRoute>
                          }
                        />
                        <Route
                          path="user/error"
                          element={
                            <ErrorComponent
                              initData={initData}
                              goToHome={() => {
                                navigate("/upyog-ui/employee");
                              }}
                            />
                          }
                        />
                        <Route path="*" element={<AppModules stateCode={stateCode} userType="employee" modules={modules} appTenants={appTenants} />} />

                      </Routes>
                    </ErrorBoundary>
                  </div>
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