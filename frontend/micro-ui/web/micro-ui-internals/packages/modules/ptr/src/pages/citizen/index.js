import { AppContainer, BackButton, PrivateRoute } from "@nudmcdgnpm/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [
  { screenPath: "petservice/new-application/acknowledgement" },
  { screenPath: "petservice/edit-application/acknowledgement" },

];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };

  const PTRCreate = Digit?.ComponentRegistryService?.getComponent("PTRCreatePet");
  const PTRApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PTRApplicationDetails");
  const PTRMyApplications = Digit?.ComponentRegistryService?.getComponent("PTRMyApplications");
  const PTREditDetails = Digit?.ComponentRegistryService?.getComponent("PTREditDetails");
 
  return (
    <span className={"pet-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/petservice/new-application`} component={PTRCreate} />
          {/* path added for renew application */}
          <PrivateRoute path={`${path}/petservice/renew-application`} component={PTRCreate} />
          <PrivateRoute path={`${path}/petservice/application/:acknowledgementIds/:tenantId`} component={PTRApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/my-applications`} component={PTRMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;