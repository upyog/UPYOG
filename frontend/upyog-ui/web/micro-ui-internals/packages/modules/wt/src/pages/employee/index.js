import {AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { useTranslation } from "react-i18next";
import {Switch, useLocation } from "react-router-dom";
import { WTLinks } from "../../Module";
import Inbox from "./Inbox";
import SearchApp from "./SearchApp";

 /**
 * `EmployeeApp` is a React component that handles routing and navigation for the Water Tanker (WT) service app.
 * It includes:
 * - A dynamic sidebar with a back button for navigation.
 * - Routes for inbox, service requests, application details, booking search, and my bookings.
 * - Conditional rendering based on the URL for new registrations and mobile views.
 * 
 * This component integrates with `PrivateRoute` to secure each route and uses the `Digit` component registry for rendering specific components like `WTCreate`, `Inbox`, and `ApplicationDetails`.
 * 
 * @param {string} path - The base path for the routes.
 * @param {string} userType - The type of user (not currently used in the component).
 * @returns {JSX.Element} The navigation structure and route handling for the WT service app.
 */

const EmployeeApp = ({ path,userType }) => {
  const { t } = useTranslation();
  const location = useLocation();
  const mobileView = innerWidth <= 640;
  sessionStorage.removeItem("revalidateddone");
  const isMobile = window.Digit.Utils.browser.isMobile();

  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["watertanker"],
      applicationStatus: [],
      locality: [],

    },
  };
// Initial state for mobileToilet inbox
  const inboxInitialStateMt = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["mobileToilet"],
      applicationStatus: [],
      locality: [],

    },
  };

  const inboxInitialStateTp = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["treePruning"],
      applicationStatus: [],
      locality: [],

    },
  };

  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");

  // const Response = Digit?.ComponentRegistryService?.getComponent("CHBResponse");
  const WTCreate = Digit?.ComponentRegistryService?.getComponent("WTCreate");
  const isNewRegistration = window.location.href.includes("info") || window.location.href.includes("wt/status");
  return (
    <Switch>
      <AppContainer>
      <React.Fragment>
        <div className="ground-container">
              <div style={isNewRegistration ? { marginLeft: "12px",display: "flex", alignItems: "center" } : { marginLeft: "-4px",display: "flex", alignItems: "center" }}>
                  <BackButton location={location} />
              </div>
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="watertanker"
                moduleCode="WT"
                filterComponent="WT_INBOX_FILTER"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute
            path={`${path}/mt/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                moduleCode="MT"
                businessService="mobileToilet"
                filterComponent="WT_INBOX_FILTER"
                initialStates={inboxInitialStateMt}
                isInbox={true}
              />
            )}
          />
           <PrivateRoute
            path={`${path}/tp/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                moduleCode="TP"
                businessService="treePruning"
                filterComponent="WT_INBOX_FILTER"
                initialStates={inboxInitialStateTp}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/request-service`} component={WTCreate} />
          <PrivateRoute path={`${path}/mt/request-service`} component={WTCreate} />
          <PrivateRoute path={`${path}/tp/request-service`} component={WTCreate} />
          <PrivateRoute path={`${path}/booking-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/bookingsearch/booking-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/my-bookings`} component={(props) => <SearchApp {...props} parentRoute={path} moduleCode={"WT"}/>} />
          <PrivateRoute path={`${path}/mt/my-bookings`} component={(props) => <SearchApp {...props} parentRoute={path} moduleCode={"MT"}/>} />
          <PrivateRoute path={`${path}/tp/my-bookings`} component={(props) => <SearchApp {...props} parentRoute={path} moduleCode={"TP"}/>} />
        </div>
        </React.Fragment>
      </AppContainer>
    </Switch>
  );
};

export default EmployeeApp;
