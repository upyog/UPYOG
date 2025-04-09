// Importing necessary components and hooks from external libraries and local files
import { PrivateRoute, BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next"; // Hook for translations
import { Switch, useLocation } from "react-router-dom"; // React Router components for navigation
import Inbox from "./Inbox"; // Component for the inbox page
import SearchApp from "./SearchApp"; // Component for searching applications

// Main component for the Employee module
const EmployeeApp = ({ path }) => {
  const { t } = useTranslation(); // Translation hook
  const location = useLocation(); // Hook to get the current location
  sessionStorage.removeItem("revalidateddone"); // Removing a session storage item
  const isMobile = window.Digit.Utils.browser.isMobile(); // Check if the browser is on a mobile device

  // Initial state for the inbox search parameters
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" }, // Default UUID filter
      services: ["ewst"], // Service type for E-Waste
      applicationStatus: [], // Empty application status filter
      locality: [], // Empty locality filter
    },
  };

  // Component to render breadcrumbs for navigation
  const EWBreadCrumbs = ({ location }) => {
    const { t } = useTranslation(); // Translation hook
    const crumbs = [
      {
        path: "/digit-ui/employee",
        content: t("ES_COMMON_HOME"), // Home breadcrumb
        show: true,
      },
      {
        path: "/digit-ui/employee/ew/inbox",
        content: t("ES_TITLE_INBOX"), // Inbox breadcrumb
        show: location.pathname.includes("ew/inbox") ? true : false,
      },
      {
        path: "/digit-ui/employee/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"), // Application search breadcrumb
        show: location.pathname.includes("/ew/my-applications") || location.pathname.includes("/ew/application-details") ? true : false,
      },
    ];

    return (
      <BreadCrumb
        style={isMobile ? { display: "flex" } : {}} // Adjusting style for mobile view
        spanStyle={{ maxWidth: "min-content" }}
        crumbs={crumbs}
      />
    );
  };

  // Fetching the ApplicationDetails component dynamically from the Digit Component Registry Service
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWApplicationDetails");

  return (
    <Switch>
      <React.Fragment>
        <div className="ground-container">
          {/* Rendering breadcrumbs */}
          {<div style={{ marginLeft: "12px" }}><EWBreadCrumbs location={location} /></div>}

          {/* Route for the inbox page */}
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true} // Using the new inbox API
                parentRoute={path} // Parent route for navigation
                businessService="ewst" // Business service type
                filterComponent="EW_INBOX_FILTER" // Filter component for the inbox
                initialStates={inboxInitialState} // Initial state for the inbox
                isInbox={true} // Flag to indicate this is an inbox
              />
            )}
          />

          {/* Route for application details */}
          <PrivateRoute path={`${path}/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />

          {/* Route for application details from application search */}
          <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />

          {/* Route for the search page */}
          <PrivateRoute path={`${path}/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />

          {/* Route for the "My Applications" page */}
          <PrivateRoute path={`${path}/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </div>
      </React.Fragment>
    </Switch>
  );
};

export default EmployeeApp; // Exporting the main component