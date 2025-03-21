import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Switch, useRouteMatch } from "react-router-dom";


const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const CndCreate = Digit?.ComponentRegistryService?.getComponent("CndCreate");
  const MyRequests = Digit?.ComponentRegistryService?.getComponent("MyRequests");
  const CndApplicationDetails = Digit?.ComponentRegistryService?.getComponent("CndApplicationDetails");
  return (
    <span style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/apply`} component={CndCreate} />
          <PrivateRoute path={`${path}/my-request`} component={MyRequests} />
          <PrivateRoute path={`${path}/my-requests/:applicationNumber/:tenantId`} component={CndApplicationDetails}></PrivateRoute>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;