import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };

  const EWCreate = Digit?.ComponentRegistryService?.getComponent("EWCreatewaste");
  const EWASTEMyApplications = Digit?.ComponentRegistryService?.getComponent("EWASTEMyApplications");
  const EWASTEApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWASTEApplicationDetails");
 
  return (
    <span className={"pet-citizen"} style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/raiseRequest`} component={EWCreate} />
          <PrivateRoute path={`${path}/application/:acknowledgementIds/:tenantId`} component={EWASTEApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/myApplication`} component={EWASTEMyApplications}></PrivateRoute>
          {/* <PrivateRoute path={`${path}/petservice/my-payments`} component={PTMyPayments}></PrivateRoute> */}
          {/* <PrivateRoute path={`${path}/petservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} /> */}
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;