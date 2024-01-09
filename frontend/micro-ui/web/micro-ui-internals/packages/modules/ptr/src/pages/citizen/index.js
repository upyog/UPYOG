import { AppContainer, BackButton, PrivateRoute } from "@egovernments/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
// import Search from "../employee/Search";
import { useTranslation } from "react-i18next";
import { PTMyPayments } from "./MyPayments";

const hideBackButtonConfig = [
  { screenPath: "property/new-application/acknowledgement" },
  { screenPath: "property/edit-application/acknowledgement" },

];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };

  const PTRCreate = Digit?.ComponentRegistryService?.getComponent("PTRCreatePet");
  const EditProperty = Digit?.ComponentRegistryService?.getComponent("PTEditProperty");
  const SearchResultsComponent = Digit?.ComponentRegistryService?.getComponent("PTSearchResultsComponent");
  const PTApplicationDetails = Digit?.ComponentRegistryService?.getComponent("PTApplicationDetails");
  const PTMyApplications = Digit?.ComponentRegistryService?.getComponent("PTMyApplications");
 
  return (
    <span className={"pt-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/petservice/new-application`} component={PTRCreate} />
          <PrivateRoute path={`${path}/petservice/edit-application`} component={EditProperty} />
          <Route path={`${path}/property/search-results`} component={SearchResultsComponent} />
          <PrivateRoute path={`${path}/petservice/application/:acknowledgementIds/:tenantId`} component={PTApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/my-application`} component={PTMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/my-payments`} component={PTMyPayments}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;