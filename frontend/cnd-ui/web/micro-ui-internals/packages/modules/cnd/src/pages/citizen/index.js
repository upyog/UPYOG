import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Redirect, Switch, useRouteMatch } from "react-router-dom";
import Inbox from "../employee/Inbox";
import { cndStyles } from "../../utils/cndStyles";

/**
 * Component to handle all the routings of Citizen Side.
 * 1. Render the VendorCitizenCard for the Vendor Login and Inbox and My Request links are available in the Card for the same.
 * 2. Handle Apply and My Request for the Normal Citizen
 */

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const CndCreate = Digit?.ComponentRegistryService?.getComponent("CndCreate");
  const MyRequests = Digit?.ComponentRegistryService?.getComponent("MyRequests");
  const CndApplicationDetails = Digit?.ComponentRegistryService?.getComponent("CndApplicationDetails");
  const CNDCard = Digit?.ComponentRegistryService?.getComponent("CNDVendorCard");
  const ApplicationDetails = Digit?.ComponentRegistryService?.getComponent("ApplicationDetails");
  const inboxInitialState = {
    searchParams: {
      uuid: { code: "ASSIGNED_TO_ALL", name: "ES_INBOX_ASSIGNED_TO_ALL" },
      services: ["cnd"],
      status: null,
    },
  };

  return (
    <span style={cndStyles.wasteQuantityCitizen}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/apply`} component={CndCreate} />
          <PrivateRoute path={`${path}/my-request`} component={MyRequests} />
          <PrivateRoute path={`${path}/my-requests/:applicationNumber/:tenantId`} component={CndApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/cnd-vendor`} component={() => Digit.UserService.hasAccess(["CND_VENDOR"]) ?  <CNDCard parentRoute={path} /> :<Redirect to={{
            pathname: `/cnd-ui/citizen/login`,
            state: { from: `${path}/cnd-vendor`, role:"CND_VENDOR" }
          }} />} />
          <PrivateRoute
            path={`${path}/inbox`}
            component={() => (
              <Inbox
                useNewInboxAPI={true}
                parentRoute={path}
                businessService="cnd"
                filterComponent="CND_INBOX_FILTERS"
                initialStates={inboxInitialState}
                isInbox={true}
              />
            )}
          />
          <PrivateRoute path={`${path}/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
          <PrivateRoute path={`${path}/applicationsearch/application-details/:id`} component={() => <ApplicationDetails parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;