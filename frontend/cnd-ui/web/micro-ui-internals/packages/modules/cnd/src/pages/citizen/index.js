import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Switch, useRouteMatch } from "react-router-dom";


const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const CndCreate = Digit?.ComponentRegistryService?.getComponent("CndCreate");
  return (
    <span style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/apply`} component={CndCreate} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;