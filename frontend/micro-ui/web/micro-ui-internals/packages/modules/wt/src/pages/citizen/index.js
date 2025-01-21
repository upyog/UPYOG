import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Switch, useRouteMatch } from "react-router-dom";


const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const WTCreate = Digit?.ComponentRegistryService?.getComponent("WTCreate");
  return (
    <span style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/request-service`} component={WTCreate} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;