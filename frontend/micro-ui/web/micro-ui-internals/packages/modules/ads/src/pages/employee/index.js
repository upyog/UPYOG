import { AppContainer, BackButton, PrivateRoute, BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import SearchApp from "./SearchApp";

/* EmployeeApp component serves as the main application container for employee-related routes.
 * It utilizes the AppContainer, PrivateRoute, and other components for structured navigation.
 * The component handles rendering based on user types and different application states,
 * including displaying a back button.
 */

const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();
  const ADSCreate = Digit?.ComponentRegistryService?.getComponent("ADSCreate");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const isRes = window.location.href.includes("ads/response");
  const isNewRegistration =
    window.location.href.includes("searchad") ||
    window.location.href.includes("modify-application") ||
    window.location.href.includes("ads/application-details");

  return (
    <Switch>
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
                {/* <CHBBreadCrumbs location={location} /> */}
              </div>
            ) : null}
            
            <PrivateRoute path={`${path}/bookad`} component={ADSCreate} />
            <PrivateRoute path={`${path}/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
            <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />

          </div>
        </React.Fragment>
      </AppContainer>
    </Switch>
  );
};

export default EmployeeApp;
