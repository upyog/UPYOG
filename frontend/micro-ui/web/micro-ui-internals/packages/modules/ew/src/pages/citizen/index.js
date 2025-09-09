import { AppContainer, BackButton, PrivateRoute } from "@upyog/digit-ui-react-components";
import React from "react";
import { Route, Switch, useRouteMatch } from "react-router-dom";
import { shouldHideBackButton } from "../../utils";
import { useTranslation } from "react-i18next";

/**
 * Configuration for routes where back button should be hidden
 * Add route paths to hide the back button on specific screens
 */
const hideBackButtonConfig = [];

/**
 * Main application component for the E-Waste citizen module.
 * Handles routing between different citizen-facing interfaces including:
 * - New waste request creation
 * - Application tracking
 * - Application details viewing
 * 
 * Uses private routes to ensure authenticated access to all features.
 * Manages responsive layout and navigation elements.
 *
 * @returns {JSX.Element} Root component for citizen E-Waste module
 */
const App = () => {
  const { path, url, ...match } = useRouteMatch();
  const { t } = useTranslation();

  const EWCreate = Digit?.ComponentRegistryService?.getComponent("EWCreatewaste");
  const EWASTEMyApplications = Digit?.ComponentRegistryService?.getComponent("EWASTEMyApplications");
  const EWASTEApplicationDetails = Digit?.ComponentRegistryService?.getComponent("EWASTECitizenApplicationDetails");

  return (
    <span className={"citizen"} style={{ width: "100%" }}> 
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