import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Switch, useRouteMatch } from "react-router-dom";

// Main Routing Page used for routing accorss the Water Tanker Module
const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const WTCreate = Digit?.ComponentRegistryService?.getComponent("WTCreate");
  const WTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("WTApplicationDetails");
  const WTMyApplications = Digit?.ComponentRegistryService?.getComponent("WTMyApplications");
 
  return (
    <span style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/request-service`} component={WTCreate} />
          <PrivateRoute path={`${path}/status`} component={WTMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/booking/:acknowledgementIds/:tenantId`} component={WTApplicationDetails}></PrivateRoute>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;