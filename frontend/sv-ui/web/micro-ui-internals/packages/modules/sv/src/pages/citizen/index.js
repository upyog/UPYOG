import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Switch, useRouteMatch } from "react-router-dom";

/**
 * Citizen side main index page which is handling the Routing of whole citizen side
 */
const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const SVCreate = Digit?.ComponentRegistryService?.getComponent("Create");
  const MyApplication = Digit?.ComponentRegistryService?.getComponent("SVMyApplications");
  const SvApplicationDetails = Digit?.ComponentRegistryService?.getComponent("SvApplicationDetails");
  return (
    <span className={"sv-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          <BackButton>Back</BackButton>
          <PrivateRoute path={`${path}/apply`} component={SVCreate} />
          <PrivateRoute path={`${path}/renew-application`} component={SVCreate} />
          <PrivateRoute path={`${path}/edit`} component={SVCreate} />
          <PrivateRoute path={`${path}/my-applications`} component={MyApplication}></PrivateRoute>
          <PrivateRoute path={`${path}/application/:applicationNo/:tenantId`} component={SvApplicationDetails}></PrivateRoute>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;