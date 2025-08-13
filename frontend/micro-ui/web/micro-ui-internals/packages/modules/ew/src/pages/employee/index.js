import { PrivateRoute,BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Switch, useLocation } from "react-router-dom";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";

/**
 * Main employee interface for E-Waste management system.
 * Provides routing and navigation for various employee functions including
 * inbox management, application processing, and search capabilities.
 *
 * @param {Object} props Component properties
 * @param {string} props.path Base route path for the employee module
 * @returns {JSX.Element} Employee interface with navigation and content areas
 */
const EmployeeApp = ({ path }) => {
  const { t } = useTranslation();
  const location = useLocation();
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  /**
   * Default configuration for inbox filters and search parameters
   */
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["ewst"],
      applicationStatus: [],
      locality: [],
    },
  };

  /**
   * Renders navigation breadcrumbs based on current route
   * 
   * @param {Object} props Component properties
   * @param {Object} props.location Current route location
   * @returns {JSX.Element} Breadcrumb navigation component
   */
  const EWBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    
    const crumbs = [
      {
        path: "/digit-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/digit-ui/employee/ew/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("ew/inbox"),
      },
      {
        path: "/digit-ui/employee/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("/ew/my-applications") || location.pathname.includes("/ew/application-details"),
      },
    ];

    return (
      <BreadCrumb
        style={isMobile ? { display: "flex" } : {}}
        spanStyle={{ maxWidth: "min-content" }}
        crumbs={crumbs}
      />
    );
  };

  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWApplicationDetails");

  return (
    <Switch>
      <React.Fragment>
        <div className="ground-container">
          <div style={{ marginLeft: "12px" }}><EWBreadCrumbs location={location} /></div>

          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="ewst"
                filterComponent="EW_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />

          <PrivateRoute 
            path={`${path}/application-details/:id`} 
            component={() => <ApplicationDetails parentRoute={path} />} 
          />

          <PrivateRoute 
            path={`${path}/applicationsearch/application-details/:id`} 
            component={() => <ApplicationDetails parentRoute={path} />} 
          />

          <PrivateRoute 
            path={`${path}/search`} 
            component={(props) => <Search {...props} t={t} parentRoute={path} />} 
          />

          <PrivateRoute 
            path={`${path}/my-applications`} 
            component={(props) => <SearchApp {...props} parentRoute={path} />} 
          />
        </div>
      </React.Fragment>
    </Switch>
  );
};

export default EmployeeApp;