/* 
  App Component:
  - This is the main entry point for the pet service application.
  - It uses React Router for navigation and `digit-ui-react-components` for UI rendering.
  
  Key Features:
  - Conditional rendering of the back button based on specific route paths.
  - Dynamic component loading using `Digit.ComponentRegistryService`.
  - Private routes to ensure that only authenticated users can access the pages.
  - Supports multilingual translation using `react-i18next`.
  
  Routes:
  - `/petservice/new-application`: For creating a new pet application.
  - `/petservice/revised-application`: For renewing an existing pet application.
  - `/petservice/application/:acknowledgementIds/:tenantId`: For viewing application details.
  - `/petservice/my-applications`: For viewing the user's submitted applications.
  - `/petservice/search`: For searching applications with filters.
*/


import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
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
 
  return (
    <span className={"pet-citizen"}style={{width:"100%"}}>
      <Switch>
        <AppContainer>
          {!shouldHideBackButton(hideBackButtonConfig) ? <BackButton>Back</BackButton> : ""}
          <PrivateRoute path={`${path}/petservice/new-application`} component={PTRCreate} />
          {/* path added for renew application */}
          <PrivateRoute path={`${path}/petservice/revised-application`} component={PTRCreate} />
          <PrivateRoute path={`${path}/petservice/application/:acknowledgementIds/:tenantId`} component={PTRApplicationDetails}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/my-applications`} component={PTRMyApplications}></PrivateRoute>
          <PrivateRoute path={`${path}/petservice/search`} component={(props) => <Search {...props} t={t} parentRoute={path} />} />
        </AppContainer>
      </Switch>
    </span>
  );
};

export default App;