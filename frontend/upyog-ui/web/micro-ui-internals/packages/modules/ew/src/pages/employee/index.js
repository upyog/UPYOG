import { PrivateRoute,BreadCrumb } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { useLocation, Routes, Route } from "react-router-dom";
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
        path: "/upyog-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/upyog-ui/employee/ew/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("ew/inbox"),
      },
      {
        path: "/upyog-ui/employee/my-applications",
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
  const EnhancedReport = Digit?.ComponentRegistryService?.getComponent("EnhancedReport");

  return (
    <React.Fragment>
      <div className="ground-container">
        <div style={{ marginLeft: "12px" }}>
          <EWBreadCrumbs location={location} />
        </div>
        <Routes>
          <Route
            path="inbox"
            element={
              <PrivateRoute>
                <Inbox
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="ewst"
                  filterComponent="EW_INBOX_FILTER"
                  initialStates={inboxInitialState}
                  isInbox={true}
                />
              </PrivateRoute>
            }
          />
          <Route path="application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path="my-applications/applicationsearch/application-details/:id" element={<PrivateRoute><ApplicationDetails parentRoute={path} /></PrivateRoute>} />
          <Route path="search" element={<PrivateRoute><SearchApp path={`/search`} /></PrivateRoute>} />
          <Route
            path="searchold"
            element={
              <PrivateRoute>
                <Inbox
                  parentRoute={path}
                  businessService="ewst"
                  initialStates={inboxInitialState}
                  isInbox={false}
                  EmptyResultInboxComp={"PTEmptyResultInbox"}
                />
              </PrivateRoute>
            }
          />
          <Route path="my-applications" element={<PrivateRoute><SearchApp path={`/my-applications`} /></PrivateRoute>} />
          <Route path="DemandCollectionBalancedRegister" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ws" reportName="DemandCollectionBalancedRegister" /></PrivateRoute>} />
          <Route path="eWasteRegisterReport/*" element={<PrivateRoute><EnhancedReport parentRoute={path} moduleName="rainmaker-ew" reportName="eWasteRegisterReport" /></PrivateRoute>} />
        </Routes>
      </div>
    </React.Fragment>
  );
};

export default EmployeeApp;
