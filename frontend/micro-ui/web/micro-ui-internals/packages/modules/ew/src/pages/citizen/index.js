import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();

  const EWCreate = Digit?.ComponentRegistryService?.getComponent("EWCreatewaste");
  const EWASTEMyApplications = Digit?.ComponentRegistryService?.getComponent("EWASTEMyApplications");
  const EWASTEApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWASTECitizenApplicationDetails");
 
  return (
    <span className={"citizen"} style={{width:"100%"}}> 
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/raiseRequest`} component={EWCreate} />
          <PrivateRoute path={`${path}/application/:requestId/:tenantId`} component={EWASTEApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/myApplication`} component={EWASTEMyApplications}></PrivateRoute>
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;