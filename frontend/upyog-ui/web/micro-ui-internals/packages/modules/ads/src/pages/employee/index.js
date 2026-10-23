import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import {  useLocation,Routes,Route } from "react-router-dom";
import SearchApp from "./SearchApp";


/* EmployeeApp component serves as the main application container for employee-related routes.
 * It utilizes the AppContainer, PrivateRoute, and other components for structured navigation.
 * The component handles rendering based on user types and different application states,
 * including displaying a back button.
 */


// to do, ApplicationDetail page pending
const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();
  const ADSCreate = Digit?.ComponentRegistryService?.getComponent("ADSCreate");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const isRes = window.location.href.includes("ads/response");
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");
  const isNewRegistration =
    window.location.href.includes("searchad") ||
    window.location.href.includes("modify-application") ||
    window.location.href.includes("ads/application-details");

  return (
    <AppContainer>
      <React.Fragment>
        <div className="ground-container">
          {!isRes ? (
            <div
              style={
                isNewRegistration
                  ? { marginLeft: "12px", display: "flex", alignItems: "center" }
                  : { marginLeft: "-4px", display: "flex", alignItems: "center" }
              }
            >
              <BackButton location={location} />
            </div>
          ) : null}
          <Routes>
            {/* <Route path={`${path}/bookad`} element={<PrivateRoute><ADSCreate /></PrivateRoute>} />
            <Route path={`${path}/my-applications`} element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
            <Route path={`${path}/applicationsearch/application-details/:id`} element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} /> */}

            <Route path="bookad/*" element={<PrivateRoute><ADSCreate /></PrivateRoute>} />
            <Route path="my-applications/*" element={<PrivateRoute><SearchApp parentRoute={path} /></PrivateRoute>} />
            <Route path="applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
            <Route path="AdvApplicationReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ads" reportName="AdvApplicationReport" /></PrivateRoute>} />



          </Routes>
        </div>
      </React.Fragment>
    </AppContainer>
  );
};

export default EmployeeApp;
