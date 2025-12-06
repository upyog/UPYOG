import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Redirect, Switch, useRouteMatch } from "react-router-dom";
import { APPLICATION_PATH } from "../../utils";
import SearchApp from "../employee/SearchApp";

// Main Routing Page used for routing accorss the Water Tanker Module
const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const WTCreate = Digit?.ComponentRegistryService?.getComponent("WTCreate");
  const WTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("WTApplicationDetails");
  const MTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("MTApplicationDetails");
  const TPApplicationDetails = Digit?.ComponentRegistryService?.getComponent("TPApplicationDetails");
  const WTMyApplications = Digit?.ComponentRegistryService?.getComponent("WTMyApplications");
  const Inbox = Digit.ComponentRegistryService.getComponent("WTEmpInbox");
  const WTCard = Digit.ComponentRegistryService.getComponent("WTCitizenCard");
  const MTCard = Digit.ComponentRegistryService.getComponent("MTCitizenCard");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");

  const getInboxInitialState = (service) => ({
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: [service],
      applicationStatus: [],
      locality: [],
    },
  });
  // Initial state for waterTanker inbox and mobileToilet inbox
  const inboxInitialStateWT = getInboxInitialState("watertanker");
  const inboxInitialStateMT = getInboxInitialState("mobileToilet");
  

  return (
    <span style={{ width: "100%" }}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              (
                <Inbox
                // Inbox component for waterTanker
                  useNewInboxAPI={true}
                  parentRoute={path}
                  moduleCode="WT"
                  businessService="watertanker"
                  filterComponent="WT_INBOX_FILTER"
                  initialStates={inboxInitialStateWT}
                  isInbox={true}
                />
              )
            )}
          />
          
          <PrivateRoute
            path={`${path}/mt/inbox`}
            component={() => (
              (
                <Inbox
                // Inbox component for mobileToilet
                  useNewInboxAPI={true}
                  parentRoute={path}
                  businessService="mobileToilet"
                  moduleCode="MT"
                  filterComponent="WT_INBOX_FILTER"
                  initialStates={inboxInitialStateMT}
                  isInbox={true}
                />
              )
            )}
          />

          <PrivateRoute path={`${path}/request-service`} component={WTCreate} />
          <PrivateRoute path={`${path}/status`} component={WTMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/booking/waterTanker/:acknowledgementIds/:tenantId`} component={WTApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/booking/mobileToilet/:acknowledgementIds/:tenantId`} component={MTApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/booking/treePruning/:acknowledgementIds/:tenantId`} component={TPApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/booking-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/bookingsearch/booking-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/wt-Vendor`} component={() => Digit.UserService.hasAccess(["WT_VENDOR"]) ?  <WTCard parentRoute={path} /> :<Redirect to={{
            pathname: `${APPLICATION_PATH}/citizen/login`,
            state: { from: `${path}/wt-Vendor`, role:"WT_VENDOR" }
          }} />} />
          <PrivateRoute path={`${path}/mt-Vendor`} component={() => Digit.UserService.hasAccess(["MT_VENDOR"]) ?  <MTCard parentRoute={path} /> :<Redirect to={{
            pathname: `${APPLICATION_PATH}/citizen/login`,
            state: { from: `${path}/mt-Vendor`, role:"MT_VENDOR" }
          }} />} />
          <PrivateRoute path={`${path}/my-bookings`} component={(props) => <SearchApp {...props} parentRoute={path} moduleCode={"WT"}/>} />
          <PrivateRoute path={`${path}/mt/my-bookings`} component={(props) => <SearchApp {...props} parentRoute={path} moduleCode={"MT"}/>} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;
