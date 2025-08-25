/**
 * @file EmployeeApp.js
 * @description This component defines the routing and layout for the employee-facing application.
 * It handles navigation, private routing, and rendering of various inbox, application, and search components.
 * 
 * @component
 * - Uses `PrivateRoute` for secured routing.
 * - Renders `Inbox`, `SearchApp`, and other employee-specific components.
 * - Displays breadcrumbs for navigation context.
 * - Handles new application, application details, and search pages.
 * - Supports both desktop and mobile views.
 * 
 * @props
 * @param {string} path - The base route path for the employee module.
 * @param {string} url - The base URL for the employee module.
 * @param {string} userType - The type of user accessing the app.

 */


import { PrivateRoute,BreadCrumb } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import { Link, Switch, useLocation } from "react-router-dom";
import { PTRLinks } from "../../Module";
import Inbox from "./Inbox";
// import PaymentDetails from "./PaymentDetails";
import SearchApp from "./SearchApp";


const EmployeeApp = ({ path, url, userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["ptr"],
      applicationStatus: [],
      locality: [],

    },
  };

 

  const PETBreadCrumbs = ({ location }) => {
    const { t } = useTranslation();
    const search = useLocation().search;
    const fromScreen = new URLSearchParams(search).get("from") || null;
    const { from : fromScreen2 } = Digit.Hooks.useQueryParams();
    const crumbs = [
      {
        path: "/upyog-ui/employee",
        content: t("ES_COMMON_HOME"),
        show: true,
      },
      {
        path: "/upyog-ui/employee/ptr/petservice/inbox",
        content: t("ES_TITLE_INBOX"),
        show: location.pathname.includes("ptr/petservice/inbox") ? true : false,
      },
      {
        path: "/upyog-ui/employee/ptr/petservice/my-applications",
        content: t("ES_COMMON_APPLICATION_SEARCH"),
        show: location.pathname.includes("/ptr/petservice/my-applications") || location.pathname.includes("/ptr/applicationsearch/application-details/") ? true : false,
      },
    ];
  
    return <BreadCrumb style={isMobile?{display:"flex"}:{}}  spanStyle={{maxWidth:"min-content"}} crumbs={crumbs} />;
  }

  const NewApplication = Digit?.ComponentRegistryService?.getComponent("PTRNewApplication");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const isRes = window.location.href.includes("ptr/response");
  const isNewRegistration = window.location.href.includes("new-application") || window.location.href.includes("modify-application") || window.location.href.includes("ptr/application-details");
  return (
    <Switch>
      <React.Fragment>
        <div className="ground-container">
          
          {!isRes ? <div style={isNewRegistration ? {marginLeft: "12px" } : {marginLeft:"-4px"}}><PETBreadCrumbs location={location} /></div> : null}
          <PrivateRoute exact path={`${path}/`} component={() => <PTRLinks matchPath={path} userType={userType} />} />
          <PrivateRoute
            path={`${path}/petservice/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="ptr"
                filterComponent="PTR_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/petservice/new-application`} component={NewApplication} />
          <PrivateRoute path={`${path}/petservice/revised-application`} component={NewApplication} />
          <PrivateRoute path={`${path}/petservice/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/petservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
          <PrivateRoute
            path={`${path}/searchold`}
            component={() => (
              <Inbox
                parentRoute={path}
                businessService="ptr"
                middlewareSearch={searchMW}
                initialStates={inboxInitialState}
                isInbox={false}
                EmptyResultInboxComp={"PTEmptyResultInbox"}
              />
            )}
          />
          <PrivateRoute path={`${path}/petservice/my-applications`} component={(props) => <SearchApp {...props} parentRoute={path} />} />
        </div>
      </React.Fragment>
    </Switch>
  );
};

export default EmployeeApp;
