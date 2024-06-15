import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

const hideBackButtonConfig = [
  { screenPath: "bookHall/new-application/acknowledgement" },
  { screenPath: "bookHall/edit-application/acknowledgement" },

];

const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();
  const inboxInitialState = {
    searchParams: {},
  };

  const CHBCreate = Digit?.ComponentRegistryService?.getComponent("CHBCreate");
  const CHBApplicationDetails = Digit?.ComponentRegistryService?.getComponent("CHBApplicationDetails");
  const CHBMyApplications = Digit?.ComponentRegistryService?.getComponent("CHBMyApplications");
 
  return (
    <span className={"chb-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/bookHall`} component={CHBCreate} />
          <PrivateRoute path={`${path}/myBookings`} component={CHBMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/application/:acknowledgementIds/:tenantId`} component={CHBApplicationDetails}></PrivateRoute>
           {/* <PrivateRoute path={`${path}/petservice/my-payments`} component={PTMyPayments}></PrivateRoute> */}
          <PrivateRoute path={`${path}/bookHall/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} /> 
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;